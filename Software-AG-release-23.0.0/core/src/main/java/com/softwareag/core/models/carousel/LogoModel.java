package com.softwareag.core.models.carousel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
 
import javax.inject.Inject;
 
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LogoModel {
 
    @Inject
    private String logo;
 
    public String getLogo() {
        return logo;
    }
    
    @Inject
    private String altText;
 
    public String getAltText() {
        return altText;
    }
 
}