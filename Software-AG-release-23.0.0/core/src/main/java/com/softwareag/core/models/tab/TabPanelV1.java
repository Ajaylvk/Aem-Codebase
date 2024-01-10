package com.softwareag.core.models.tab;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabPanelV1 {

    @ValueMapValue
    private String label;
    
    @ValueMapValue
    private String anchorName;
    

    public String getLabel() {
        return label;
    }
    
    public String getAnchorName()
    {
		return anchorName;
    }

}
