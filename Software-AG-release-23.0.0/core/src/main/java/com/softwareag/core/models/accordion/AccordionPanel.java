package com.softwareag.core.models.accordion;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionPanel {

    @ValueMapValue
    private String titlePanel;

    @ValueMapValue
    private String subtitle;

    public String getTitlePanel() {
        return titlePanel;
    }

    public String getSubtitle() {
        return subtitle;
    }

}

