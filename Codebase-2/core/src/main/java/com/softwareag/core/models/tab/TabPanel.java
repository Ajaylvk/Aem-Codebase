package com.softwareag.core.models.tab;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabPanel {

    @ValueMapValue
    private String title;
    
    @ValueMapValue
    private String anchorName;
    

    public String getTitle() {
        return title;
    }
    
    public String getAnchorName()
    {
		return anchorName;
    }

}
