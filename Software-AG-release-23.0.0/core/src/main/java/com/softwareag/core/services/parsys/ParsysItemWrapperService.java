package com.softwareag.core.services.parsys;

import org.apache.sling.api.resource.Resource;

/**
 * Interface to find an applicable parsys item wrapper for a resource.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
public interface ParsysItemWrapperService {

    /**
     * Searches and returns an applicable item wrapper service among the available ParsysItemWrapper services for the given parsysResource.
     *
     * @param parsysResource
     *         Parsys resource item, which is going to include the given parsys item.
     * @return Matching ParsysItemWrapper for the given parsysResource. If none found, returns null.
     */
    ParsysItemWrapper findParsysItemWrapper(Resource parsysResource, String itemResourcePath, String itemResourceType);

}
