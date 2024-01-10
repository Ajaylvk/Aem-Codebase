package com.softwareag.core.models.inpagenavigation;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InPageNavigationModel {

    private static final String PN_ITEMS = "navigationItems";

    @ScriptVariable
    protected Resource resource;

    private final List<InPageNavigationItem> navigationItems = new ArrayList<>();

    @PostConstruct
    protected void init() {
        final Resource navigationResources = resource.getChild(PN_ITEMS);
        if (navigationResources != null && navigationResources.hasChildren()) {
            navigationResources.getChildren().forEach(item -> navigationItems.add(item.adaptTo(InPageNavigationItem.class)));
        }
    }

    public boolean hasContent() {
        return !navigationItems.isEmpty();
    }

    public List<InPageNavigationItem> getNavigationItems() {
        return Collections.unmodifiableList(navigationItems);
    }
}
