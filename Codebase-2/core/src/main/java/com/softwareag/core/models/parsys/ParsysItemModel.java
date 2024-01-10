package com.softwareag.core.models.parsys;

import com.softwareag.core.models.reference.ReferenceModel;
import com.softwareag.core.services.parsys.ParsysItemWrapper;
import com.softwareag.core.services.parsys.ParsysItemWrapperService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;

/**
 * Parsys item model to be used by the customized parsys component for adding additional wrapper element
 * around the parsys items, when needed.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ParsysItemModel {

    public static final String REFERENCE_COMPONENT_RESOURCE_PATH = "referenceComponentResourcePath";

    @OSGiService
    private ParsysItemWrapperService parsysItemWrapperService;

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @RequestAttribute
    private String itemResourcePath;

    @RequestAttribute
    private String itemResourceType;

    // Wrapper to add outer div element around the OOTB generated parsys item
    private ParsysItemWrapper parsysItemWrapper;

    @PostConstruct
    public void init() {
        if (resource != null) {
            parsysItemWrapper = parsysItemWrapperService.findParsysItemWrapper(resource, itemResourcePath, itemResourceType);
        }
    }

    /**
     * Checks, if unwrap should be applied or not by the existence of a parysItemWrapper.
     *
     * @return true, if there is no applicable item wrapper. Otherwise, false.
     */
    public boolean isUnwrap() {
        return parsysItemWrapper == null;
    }

    /**
     * Returns the element attributes for the additional wrapper from the detected item wrapper.
     *
     * @return Map of attributes for the additional wrapper from the detected item wrapper. If no wrapper detected, empty map will be returned.
     */
    public Map<String, Object> getTagAttributes() {
        if (isUnwrap()) {
            return Collections.emptyMap();
        } else {
            return parsysItemWrapper.getWrapperElementAttributes(resource, itemResourcePath, itemResourceType);
        }
    }

    public boolean isDisplayed() {
        if (ReferenceModel.REFERENCE_COMPONENT_RESOURCE_TYPE.equals(itemResourceType)) {
            request.setAttribute(REFERENCE_COMPONENT_RESOURCE_PATH, itemResourcePath);
        }

        return parsysItemWrapper == null || parsysItemWrapper.isDisplayed(request, resource, itemResourcePath, itemResourceType);
    }

}
