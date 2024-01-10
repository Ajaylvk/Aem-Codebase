package com.softwareag.core.services.workflow;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.DamConstants;
import com.day.crx.JcrConstants;
import com.softwareag.core.services.virusscan.VirusScanService;
import com.softwareag.core.util.InboxUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayloadCheckerStepTest {

    private static final String FIELD_NAME_RESOURCE_RESOLVER_FACTORY = "resourceResolverFactory";
    private static final String FIELD_NAME_VIRUS_SCAN_SERVICE = "virusScanService";
    private static final String ADMIN_USER_ID = "admin";
    private static final String TEST_PAYLOAD_INSIDE_SOFTWARE_AG = "/content/dam/softwareag/test.png/jcr:content/renditions/original";
    private static final String TEST_PAYLOAD_OUTSIDE_SOFTWARE_AG = "/content/dam/test/test.png/jcr:content/renditions/original";
    private static final String TEST_WORKFLOW_ID = "Workflow ID";

    private PayloadCheckerStep underTest;

    @BeforeEach
    private void setUp() {
        underTest = new PayloadCheckerStep();
    }

    @Test
    public void execute_doesNothing_ifVirusScanServiceIsNull() {
        final WorkItem workItem = mock(WorkItem.class);
        final PayloadCheckerStep checkerStep = new PayloadCheckerStep();

        checkerStep.execute(workItem, null, null);

        verify(workItem, never()).getWorkflowData();
    }

    @Test
    public void execute_removesAssetSendsInboxMessageTerminatesWorkflow_ifVirusIsDetected() throws IllegalAccessException, RepositoryException, WorkflowException, LoginException, TaskManagerException {
        VirusScanService virusScanService = mock(VirusScanService.class);

        Object payloadObject = mock(Object.class);
        when(payloadObject.toString()).thenReturn(TEST_PAYLOAD_INSIDE_SOFTWARE_AG);

        MetaDataMap metaDataMap = mock(MetaDataMap.class);
        when(metaDataMap.get(PayloadCheckerStep.KEY_USER_ID, String.class)).thenReturn(ADMIN_USER_ID);

        WorkflowData workflowData = mock(WorkflowData.class);
        when(workflowData.getPayloadType()).thenReturn(PayloadCheckerStep.TYPE_JCR_PATH);
        when(workflowData.getPayload()).thenReturn(payloadObject);
        when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);

        Workflow workflow = mock(Workflow.class);
        when(workflow.isActive()).thenReturn(true);
        when(workflow.getId()).thenReturn(TEST_WORKFLOW_ID);

        WorkItem workItem = mock(WorkItem.class);
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workItem.getWorkflow()).thenReturn(workflow);

        InputStream inputStream = mock(InputStream.class);
        when(virusScanService.fileContainsVirus(inputStream)).thenReturn(true);

        Binary assetBinary = mock(Binary.class);
        when(assetBinary.getStream()).thenReturn(inputStream);

        Property dataProperty = mock(Property.class);
        when(dataProperty.getBinary()).thenReturn(assetBinary);

        Node payloadNode = mock(Node.class);
        when(payloadNode.getProperty(JcrConstants.JCR_DATA)).thenReturn(dataProperty);
        when(payloadNode.getPath()).thenReturn(TEST_PAYLOAD_INSIDE_SOFTWARE_AG + "/" + JcrConstants.JCR_CONTENT);

        Session session = mock(Session.class);
        when(session.getNode(anyString())).thenReturn(payloadNode);

        WorkflowSession workflowSession = mock(WorkflowSession.class);
        when(workflowSession.adaptTo(Session.class)).thenReturn(session);

        AssetManager assetManager = mock(AssetManager.class);

        Task task = mock(Task.class);
        TaskManagerFactory taskManagerFactory = mock(TaskManagerFactory.class);
        when(taskManagerFactory.newTask(InboxUtil.NOTIFICATION_TASK_TYPE)).thenReturn(task);

        TaskManager taskManager = mock(TaskManager.class);
        when(taskManager.getTaskManagerFactory()).thenReturn(taskManagerFactory);

        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.adaptTo(AssetManager.class)).thenReturn(assetManager);
        when(resourceResolver.adaptTo(TaskManager.class)).thenReturn(taskManager);

        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);

        FieldUtils.writeField(underTest, FIELD_NAME_RESOURCE_RESOLVER_FACTORY, resourceResolverFactory, true);
        FieldUtils.writeField(underTest, FIELD_NAME_VIRUS_SCAN_SERVICE, virusScanService, true);

        underTest.execute(workItem, workflowSession, null);

        verify(assetManager, times(1)).removeAssetForBinary(anyString());
        verify(taskManager, times(1)).createTask(task);
        verify(workflowSession, times(1)).terminateWorkflow(workflow);
    }

    @Test
    public void execute_skipVirusCheck_ifAssetIsNotInsideSoftwareAgDamFolder() throws RepositoryException, IllegalAccessException {
        VirusScanService virusScanService = mock(VirusScanService.class);

        Object payloadObject = mock(Object.class);
        when(payloadObject.toString()).thenReturn(TEST_PAYLOAD_OUTSIDE_SOFTWARE_AG);

        WorkflowData workflowData = mock(WorkflowData.class);
        when(workflowData.getPayloadType()).thenReturn(PayloadCheckerStep.TYPE_JCR_PATH);
        when(workflowData.getPayload()).thenReturn(payloadObject);

        WorkItem workItem = mock(WorkItem.class);
        when(workItem.getWorkflowData()).thenReturn(workflowData);

        Node payloadNode = mock(Node.class);

        Session session = mock(Session.class);

        WorkflowSession workflowSession = mock(WorkflowSession.class);
        when(workflowSession.adaptTo(Session.class)).thenReturn(session);

        FieldUtils.writeField(underTest, FIELD_NAME_VIRUS_SCAN_SERVICE, virusScanService, true);

        underTest.execute(workItem, workflowSession, null);

        verify(virusScanService, times(0)).fileContainsVirus(any());
    }

    @Test
    public void execute_skipVirusCheck_ifAssetHasBeenAlreadyUploaded() throws RepositoryException, IllegalAccessException, LoginException {
        VirusScanService virusScanService = mock(VirusScanService.class);

        Object payloadObject = mock(Object.class);
        when(payloadObject.toString()).thenReturn(TEST_PAYLOAD_INSIDE_SOFTWARE_AG);

        WorkflowData workflowData = mock(WorkflowData.class);
        when(workflowData.getPayloadType()).thenReturn(PayloadCheckerStep.TYPE_JCR_PATH);
        when(workflowData.getPayload()).thenReturn(payloadObject);

        WorkItem workItem = mock(WorkItem.class);
        when(workItem.getWorkflowData()).thenReturn(workflowData);

        Node payloadNode = mock(Node.class);
        when(payloadNode.getPath()).thenReturn(TEST_PAYLOAD_INSIDE_SOFTWARE_AG + "/" + JcrConstants.JCR_CONTENT);

        Session session = mock(Session.class);
        when(session.getNode(anyString())).thenReturn(payloadNode);

        WorkflowSession workflowSession = mock(WorkflowSession.class);
        when(workflowSession.adaptTo(Session.class)).thenReturn(session);

        Asset asset = mock(Asset.class);
        when(asset.getMetadataValue(DamConstants.PN_EXTRACTED)).thenReturn("2020-03-31T15:39:53.920+02:00");

        AssetManager assetManager = mock(AssetManager.class);
        when(assetManager.getAssetForBinary(anyString())).thenReturn(asset);

        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.adaptTo(AssetManager.class)).thenReturn(assetManager);

        ResourceResolverFactory resourceResolverFactory = mock(ResourceResolverFactory.class);
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);

        FieldUtils.writeField(underTest, FIELD_NAME_RESOURCE_RESOLVER_FACTORY, resourceResolverFactory, true);
        FieldUtils.writeField(underTest, FIELD_NAME_VIRUS_SCAN_SERVICE, virusScanService, true);

        underTest.execute(workItem, workflowSession, null);

        verify(virusScanService, times(0)).fileContainsVirus(any());
    }
}
