package com.softwareag.core.services.parsys;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.Map;

/**
 * Interface to add additional wrapper around the parsys items for a component.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
public interface ParsysItemWrapper {

    /**
     * Checks if this wrapper can be applicable for the given parsys item resource.
     *
     * @param parsysResource
     *     Parsys resource, which is going to include the given resource item.
     * @param itemResourcePath
     *     Resource path of the parsys item, which is being included.
     * @param itemResourceType
     *     Resource type of the parsys item, which is being included.
     * @return true, if this wrapper handles the given parsys resource.
     */
    boolean matches(Resource parsysResource, String itemResourcePath, String itemResourceType);

    boolean isDisplayed(SlingHttpServletRequest request, Resource parsysResource, String itemResourcePath, String itemResourceType);

    /**
     * Prepares and returns map of wrapper element attributes around the given parsys resource item.
     *
     * @param parsysResource
     *     Parsys resource, which is going to include the given resource item.
     * @param itemResourcePath
     *     Resource path of the parsys item, which is being included.
     * @param itemResourceType
     *     Resource type of the parsys item, which is being included.
     * @return Map of wrapper element attributes around the given parsys item resource.
     */
    Map<String, Object> getWrapperElementAttributes(Resource parsysResource, String itemResourcePath, String itemResourceType);

}
