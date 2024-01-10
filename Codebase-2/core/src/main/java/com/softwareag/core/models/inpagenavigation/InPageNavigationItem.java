package com.softwareag.core.models.inpagenavigation;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InPageNavigationItem {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String jumpDestination;

    public String getTitle() {
        return title;
    }

    public String getJumpDestination() {
        return jumpDestination;
    }

}
