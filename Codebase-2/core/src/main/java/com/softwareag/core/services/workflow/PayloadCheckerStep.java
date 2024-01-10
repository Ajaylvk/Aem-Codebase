package com.softwareag.core.services.workflow;

import com.adobe.granite.asset.api.AssetException;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.crx.JcrConstants;
import com.softwareag.core.services.virusscan.VirusScanConfigurationMissingException;
import com.softwareag.core.services.virusscan.VirusScanFailedException;
import com.softwareag.core.services.virusscan.VirusScanService;
import com.softwareag.core.util.InboxUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Component(
    service = WorkflowProcess.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "= Software AG - Payload Check Process Step",
        "sling.servlet.methods=GET",
        "process.label=Software AG Payload Check"
    })

public class PayloadCheckerStep implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(PayloadCheckerStep.class);

    public static final String TYPE_JCR_PATH = "JCR_PATH";
    public static final String KEY_USER_ID = "userId";
    private static final Map<String, Object> AUTH_INFO;

    private static final String INBOX_TITLE_VIRUS_DETECTED = "Virus Detected";
    private static final String INBOX_MESSAGE_VIRUS_DETECTED = "A virus was detected. The uploaded asset has been removed.";

    private static final String INBOX_TITLE_SCAN_FAILED = "Virus Scan Failed";
    private static final String INBOX_MESSAGE_SCAN_FAILED = "The virus scan of the uploaded asset has failed. The asset could not be uploaded.";

    static {
        AUTH_INFO = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "virus-scan");
    }

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private VirusScanService virusScanService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        if (this.virusScanService != null) {
            final WorkflowData workflowData = workItem.getWorkflowData();
            if (StringUtils.equals(workflowData.getPayloadType(), TYPE_JCR_PATH)) {
                final Session session = workflowSession.adaptTo(Session.class);
                if (session != null) {
                    final String payload = workItem.getWorkflowData().getPayload().toString() + "/" + JcrConstants.JCR_CONTENT;
                    // check only assets that are inside the softwareag DAM folder
                    if (StringUtils.startsWith(payload, "/content/dam/softwareag/")) {
                        try {
                            final Node node = session.getNode(payload);
                            checkPayload(node, workItem, workflowSession);
                        } catch (RepositoryException e) {
                            LOG.error("Could not get node of payload '{}'.", payload, e);
                        }
                    }
                }
            }
        }
    }

    private void checkPayload(final Node payloadNode, final WorkItem workItem, final WorkflowSession workflowSession) {
        if (payloadNode != null && !assetAlreadyUploaded(payloadNode)) {
            try {
                LOG.info("Check payload '{}'", payloadNode);
                InputStream is = payloadNode.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();
                final boolean virusDetected = virusScanService.fileContainsVirus(is);
                is.close();
                if (virusDetected) {
                    handleTerminateWorkflow(payloadNode, workItem, workflowSession, INBOX_TITLE_VIRUS_DETECTED, INBOX_MESSAGE_VIRUS_DETECTED);
                }
            } catch (VirusScanConfigurationMissingException e) {
                LOG.warn("Skip workflow step.", e);
            } catch (VirusScanFailedException | RepositoryException | IOException e) {
                LOG.error("Could not check payload '{}'.", payloadNode, e);
                handleTerminateWorkflow(payloadNode, workItem, workflowSession, INBOX_TITLE_SCAN_FAILED, INBOX_MESSAGE_SCAN_FAILED);
            }
        }
    }

    /**
     * Checks whether an asset to be scanned has already been uploaded and scanned accordingly.
     * For this, the timestamp property {@link DamConstants#PN_EXTRACTED} is read from the metadata.
     * After an asset is successfully uploaded, the metadata is automatically extracted and the timestamp is set.
     * This property can then be used to determine whether the asset has already been uploaded.
     *
     * @param payloadNode
     *     to be checked.
     * @return {@code true} if asset has already been uploaded. Otherwise {@code false}.
     */
    private boolean assetAlreadyUploaded(final Node payloadNode) {
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)) {
            final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            if (assetManager != null) {
                final String payloadNodePath = getPayloadNodePath(payloadNode);
                final String assetBinaryPath = getAssetBinaryPath(payloadNodePath);
                final Asset asset = assetManager.getAssetForBinary(assetBinaryPath);
                if (asset != null) {
                    return StringUtils.isNotBlank(asset.getMetadataValue(DamConstants.PN_EXTRACTED));
                }
            }
        } catch (LoginException | AssetException e) {
            LOG.error("Cannot check if the payload has already been uploaded.", e);
        }

        return false;
    }

    /**
     * Removes the asset from the repository, sends an an appropriate inbox message to the user and finally terminates the workflow.
     */
    private void handleTerminateWorkflow(final Node payloadNode,
                                         final WorkItem workItem,
                                         final WorkflowSession workflowSession,
                                         final String inboxTitle,
                                         final String inboxDescription) {
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(AUTH_INFO)) {
            final String payloadNodePath = getPayloadNodePath(payloadNode);

            // remove asset from repository
            final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
            if (assetManager != null) {
                final String assetBinaryPath = getAssetBinaryPath(payloadNodePath);
                assetManager.removeAssetForBinary(assetBinaryPath);
            }

            // inform user about the termination of the workflow
            InboxUtil.addMessageToInbox(resourceResolver, workItem.getWorkflowData().getMetaDataMap().get(KEY_USER_ID, String.class),
                inboxDescription, inboxTitle, payloadNodePath);

        } catch (LoginException | AssetException | TaskManagerException e) {
            LOG.error("Error during handling workflow termination.", e);
        } finally {
            final Workflow workflow = workItem.getWorkflow();
            if (workflow != null && workflow.isActive()) {
                try {
                    LOG.info("Terminate workflow with id '{}'.", workflow.getId());
                    workflowSession.terminateWorkflow(workflow);
                } catch (WorkflowException e) {
                    LOG.error("Could not terminate workflow with id '{}'.", workflow.getId(), e);
                }
            }
        }
    }

    private String getPayloadNodePath(final Node payloadNode) {
        String path = StringUtils.EMPTY;
        try {
            path = payloadNode.getPath();
        } catch (RepositoryException e) {
            LOG.error("Cannot get path from payload node {}.", payloadNode, e);
        }
        return path;
    }

    private String getAssetBinaryPath(final String payloadNodePath) {
        String assetBinaryPath = StringUtils.EMPTY;

        if (StringUtils.isNotBlank(payloadNodePath)) {
            final String assetNodePath = StringUtils.replaceOnce(payloadNodePath, "/jcr:content/renditions/original/jcr:content", StringUtils.EMPTY);
            assetBinaryPath = DamUtil.assetToBinaryPath(assetNodePath);
        }

        return assetBinaryPath;
    }
}
