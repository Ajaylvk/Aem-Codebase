package com.softwareag.core.models.accordion;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
 
import javax.inject.Inject;
 
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionModel {
 
	
    @Inject
    private String description;
    
    @Inject
    private String itemTitle;
    
    
 
    public String getDescription() {
        return description;
    }
    
    public String getItemTitle() {
        return itemTitle;
    }
    
  
 
}