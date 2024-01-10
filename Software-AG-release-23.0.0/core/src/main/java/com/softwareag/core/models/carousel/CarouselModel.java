package com.softwareag.core.models.carousel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselModel {

    @ValueMapValue
    protected String carouselVariation;

    @ValueMapValue
    protected String carouselMode;

    @ValueMapValue
    protected String timer;
    
    @ValueMapValue
    protected String carouselPosition;

    @Self
    private Resource resource;

    public String getCarouselVariation() {
        return carouselVariation;
    }

    public boolean getCarouselMode() {
        return carouselMode.equals("autoplay") ? true : false;
    }
    
    public String getTimer() {
        return timer;
    }
    
    public String getCarouselPosition() {
        return carouselPosition;
    }
    
}
