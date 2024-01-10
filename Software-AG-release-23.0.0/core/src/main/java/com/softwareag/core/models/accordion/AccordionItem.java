package com.softwareag.core.models.accordion;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionItem {

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String anchorName;

    public String getText() {
        return text;
    }

    public String getAnchorName() {
        return anchorName;
    }
}

