package com.softwareag.core.models.gridcontainer;


import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;

import static com.softwareag.core.models.contentitem.ContentItemModel.CONTENT_ITEM_RESOURCE_TYPE;
import static com.softwareag.core.services.parsys.impl.DataLayoutWrapperImpl.PRODUCT_TEASER_RESOURCE_TYPE;

/**
 * Grid container parsys model for extending the parsys class when needed.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GridContainerParsysModel {

    // List of resource types, which require additional no padding class on parsys.
    protected static final String[] NO_PADDING_RESOURCE_TYPES = {
        PRODUCT_TEASER_RESOURCE_TYPE,
        CONTENT_ITEM_RESOURCE_TYPE
    };
    protected static final String PARSYS_NO_PADDING_CLASS = "parsys--no-padding";

    @SlingObject
    private Resource resource;

    @RequestAttribute
    private String parsysResourcePath;

    // flag to indicate, if parsys contains resources, which require no padding classes.
    private boolean containsNoPaddingResource = false;

    @PostConstruct
    public void init() {
        containsNoPaddingResource = containsNoPaddingResource();
    }

    private boolean containsNoPaddingResource() {
        final Resource parsysResource = StringUtils.isBlank(parsysResourcePath) ? null : resource.getChild(parsysResourcePath);
        if (parsysResource != null) {
            for (final Resource parsysChild : parsysResource.getChildren()) {
                for (final String noPaddingResourceType : NO_PADDING_RESOURCE_TYPES) {
                    if (parsysChild.isResourceType(noPaddingResourceType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns additional classes on parsys, if needed. Currently, only the no padding logic implemented.
     *
     * @return Additional class string, when needed. Otherwise, returns empty string.
     */
    public String getAdditionalClasses() {
        final StringBuilder sb = new StringBuilder();
        if (containsNoPaddingResource) {
            sb.append(" ").append(PARSYS_NO_PADDING_CLASS);
        }
        return sb.toString();
    }

}
