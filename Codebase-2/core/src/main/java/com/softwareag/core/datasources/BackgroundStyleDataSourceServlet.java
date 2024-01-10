package com.softwareag.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.crx.JcrConstants;

import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "= Policy-Backed Background-Color DataSource Servlet for Style Dropdowns",
        "sling.servlet.resourceTypes=" + BackgroundStyleDataSourceServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    })

/**
 * Servlet to provide a Granite DataSource containing a list of Background Color styles drawn from the policy of a component.
 *
 * The Servlet expects a policy in the same structure as the OOTB style system, with the exception that
 * cq:StyleGroups has been renamed to styleGroups_disabled so that the Style System paintbrush icon in
 * component edit bars is not shown.
 *
 * To use the OOTB style system, this servlet would become obsolete and the node stylegroups_disabled changed to
 * cq:StyleGroups.
 *
 * A cq:styleGroupLabel with label BACKGROUND_COLOR_STYLEGROUP_LABEL must exist in the style system policy.
 */

public class BackgroundStyleDataSourceServlet extends SlingSafeMethodsServlet {

    static final String RESOURCE_TYPE = "softwareag/datasources/backgroundcolors";
    private static final String NN_STYLE_GROUPS = "styleGroups_disabled";
    private static final String PN_STYLE_GROUP_LABEL = "cq:styleGroupLabel";
    private static final String NN_STYLES = "cq:styles";
    private static final String BACKGROUND_COLOR_STYLEGROUP_LABEL = "Background Color";
    private static final String PN_STYLE_LABEL = "cq:styleLabel";
    private static final String PN_STYLE_ID = "cq:styleId";
    private static final String CORAL_SELECT_ITEM_ICON = "border";

    private static final Logger LOG = LoggerFactory.getLogger(BackgroundStyleDataSourceServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) {

        try {
            String componentPath = request.getRequestPathInfo().getSuffix();

            if (StringUtils.isBlank(componentPath)) {
                LOG.error("invalid request - no suffix");
                if (!response.isCommitted()) { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
                return;
            }

            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource componentResource = resourceResolver.getResource(componentPath);

            if (componentResource == null) {
                LOG.error("no resource found at path {}",componentPath);
                if (!response.isCommitted()) { response.sendError(HttpServletResponse.SC_NOT_FOUND); }
                return;
            }

            List<KeyValue> swatchList = new ArrayList<>();
            Resource policyResource = getContentPolicyResource(componentResource);
            if (policyResource != null) {
                swatchList = this.getBackgroundColorKvList(policyResource);
            }

            @SuppressWarnings("unchecked")
            DataSource ds =
                new SimpleDataSource(
                    new TransformIterator(
                        swatchList.iterator(),
                        input -> {
                            KeyValue keyValue = (KeyValue) input;
                            ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                            vm.put("value", keyValue.key);
                            vm.put("icon",  CORAL_SELECT_ITEM_ICON);
                            vm.put("text", keyValue.value);
                            return new ValueMapResource(
                                resourceResolver, new ResourceMetadata(),
                                JcrConstants.NT_UNSTRUCTURED, vm);
                        }));
            request.setAttribute(DataSource.class.getName(), ds);

        } catch (java.io.IOException e) {
            LOG.error("Error attempting to fetch colorpicker dropdown values  Unable to set response", e);
        }
    }

    private static Resource getContentPolicyResource(Resource resource) {
        ContentPolicyManager policyManager = resource.getResourceResolver().adaptTo(ContentPolicyManager.class);
        if (policyManager != null) {
            ContentPolicy policy = policyManager.getPolicy(resource);
            if (policy != null) {
                return policy.adaptTo(Resource.class);
            }
        }
        return null;
    }

    private static Resource getStyleGroupResourceByLabel(Resource contentPolicyResource) {
        if (contentPolicyResource != null) {
            Resource styleGroupsResource = contentPolicyResource.getChild(NN_STYLE_GROUPS);
            if (styleGroupsResource != null) {
                for (Resource styleGroupResource : styleGroupsResource.getChildren()) {
                    String styleGroupLabel = styleGroupResource.getValueMap().get(PN_STYLE_GROUP_LABEL, String.class);
                    if (StringUtils.equalsIgnoreCase(BackgroundStyleDataSourceServlet.BACKGROUND_COLOR_STYLEGROUP_LABEL, styleGroupLabel)) {
                        return styleGroupResource;
                    }
                }
            }
        }
        return null;
    }

    private List<KeyValue> getBackgroundColorKvList(Resource componentResource) {
        List<KeyValue> styles = new LinkedList<>();
        Resource styleGroupResource = getStyleGroupResourceByLabel(componentResource);

        if (styleGroupResource != null) {
            Resource stylesResource = styleGroupResource.getChild(NN_STYLES);
            if (stylesResource != null) {
                for (Resource styleResource : stylesResource.getChildren()) {
                    ValueMap properties = styleResource.getValueMap();
                    String key = properties.get(PN_STYLE_ID, String.class);
                    String value = properties.get(PN_STYLE_LABEL, String.class);
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        KeyValue kv = new KeyValue(key, value);
                        styles.add(kv);
                    }
                }
            }
        }
        return styles;
    }

    private static class KeyValue {

        private String key;
        private String value;

        private KeyValue(final String newKey, final String newValue) {
            this.key = newKey;
            this.value = newValue;
        }
    }
}
