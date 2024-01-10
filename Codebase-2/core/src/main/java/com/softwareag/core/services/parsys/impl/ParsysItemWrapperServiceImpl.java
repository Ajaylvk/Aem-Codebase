package com.softwareag.core.services.parsys.impl;

import com.softwareag.core.services.parsys.ParsysItemWrapper;
import com.softwareag.core.services.parsys.ParsysItemWrapperService;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementing class of the ParsysItemWrapperService interface.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
@Component(service = ParsysItemWrapperService.class, immediate = true)
public class ParsysItemWrapperServiceImpl implements ParsysItemWrapperService {

    @Reference(
        service = ParsysItemWrapper.class,
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.DYNAMIC,
        bind = "bindParsysItemWrapper",
        unbind = "unbindParsysItemWrapper"
    )
    private final List<ParsysItemWrapper> parsysItemWrappers = new ArrayList<>();

    public void bindParsysItemWrapper(final ParsysItemWrapper service, final Map<String, Object> props) {
        if (!parsysItemWrappers.contains(service)) {
            parsysItemWrappers.add(service);
        }
    }

    public void unbindParsysItemWrapper(final ParsysItemWrapper service, final Map<String, Object> props) {
        parsysItemWrappers.remove(service);
    }

    @Override
    public ParsysItemWrapper findParsysItemWrapper(final Resource parsysResource, final String itemResourcePath, final String itemResourceType) {
        for (final ParsysItemWrapper parsysItemWrapper : parsysItemWrappers) {
            if (parsysItemWrapper.matches(parsysResource, itemResourcePath, itemResourceType)) {
                return parsysItemWrapper;
            }
        }
        return null;
    }

}
