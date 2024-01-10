package com.softwareag.core.services.parsys.impl;

import com.softwareag.core.models.reference.ReferenceModel;
import com.softwareag.core.services.parsys.ParsysItemWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.softwareag.core.models.contentitem.ContentItemModel.CONTENT_ITEM_RESOURCE_TYPE;
import static com.softwareag.core.models.parsys.ParsysItemModel.REFERENCE_COMPONENT_RESOURCE_PATH;
import static com.softwareag.core.models.reference.ReferenceModel.COMPONENT_TAGS;

/**
 * Implementing class of the ParsysItemWrapper interface for the productteaser/contentitem component.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
@Component(service = ParsysItemWrapper.class)
public class DataLayoutWrapperImpl implements ParsysItemWrapper {

    public static final String PRODUCT_TEASER_RESOURCE_TYPE = "softwareag/components/content/productteaser";
    public static final String DOWNLOAD_RESOURCE_TYPE = "softwareag/components/content/download";


    protected static final String[] DATA_LAYOUT_RESOURCE_TYPES = {
        PRODUCT_TEASER_RESOURCE_TYPE,
        CONTENT_ITEM_RESOURCE_TYPE,
        DOWNLOAD_RESOURCE_TYPE
    };
    
    protected static final String LAYOUT_PN = "layout";
    protected static final String LAYOUT_ATTRIBUTE_NAME = "data-layout";
    protected static final String DEFAULT_LAYOUT = "small";

    @Override
    public boolean matches(final Resource parsysResource, final String itemResourcePath, final String itemResourceType) {
        final Resource itemResource = getItemResource(parsysResource, itemResourcePath);
        if (itemResource == null) {
            return false;
        }

        for (final String dataLayoutResourceType : DATA_LAYOUT_RESOURCE_TYPES) {
            if (itemResource.isResourceType(dataLayoutResourceType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isDisplayed(final SlingHttpServletRequest request,
                               final Resource parsysResource,
                               final String itemResourcePath,
                               final String itemResourceType) {
        final Resource itemResource = getItemResource(parsysResource, itemResourcePath);
        if (itemResource == null) {
            return false;
        }

        if (request == null || !itemResource.isResourceType(CONTENT_ITEM_RESOURCE_TYPE)) {
            return true;
        }

        final String referenceComponentResourcePath = (String) request.getAttribute(REFERENCE_COMPONENT_RESOURCE_PATH);
        if (StringUtils.isBlank(referenceComponentResourcePath)) {
            return true;
        }
        final Resource referenceComponentResource = request.getResourceResolver().getResource(referenceComponentResourcePath);

        final ReferenceModel referenceModel = Objects.requireNonNull(referenceComponentResource).adaptTo(ReferenceModel.class);
        final ValueMap valueMap = itemResource.getValueMap();
        final String[] childTagPaths = valueMap.get(COMPONENT_TAGS, String[].class);

        return Objects.requireNonNull(referenceModel).matchesTag(childTagPaths);
    }

    @Override
    public Map<String, Object> getWrapperElementAttributes(final Resource parsysResource, final String itemResourcePath, final String itemResourceType) {
        final Resource itemResource = getItemResource(parsysResource, itemResourcePath);
        if (itemResource == null) {
            return Collections.emptyMap();
        }

        // get the layout property and provide it as an attribute, if it is present.
        final Map<String, Object> attrMap = new HashMap<>();
        final String layoutValue = itemResource.getValueMap().get(LAYOUT_PN, String.class);
        if (StringUtils.isNotBlank(layoutValue)) {
            attrMap.put(LAYOUT_ATTRIBUTE_NAME, layoutValue);
        } else {
            attrMap.put(LAYOUT_ATTRIBUTE_NAME, DEFAULT_LAYOUT);
        }

        return Collections.unmodifiableMap(attrMap);
    }

    private Resource getItemResource(final Resource parsysResource, final String itemResourcePath) {
        if (parsysResource == null || StringUtils.isBlank(itemResourcePath)) {
            return null;
        }
        final ResourceResolver resourceResolver = parsysResource.getResourceResolver();
        return resourceResolver.getResource(itemResourcePath);
    }

}
