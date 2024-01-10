package com.softwareag.core.models.carousel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselLogo {

	@Inject
    @Named("logos/.")
	private List<LogoModel> logosList;

    
    public List<LogoModel> getLogosList() {
        return logosList;
    }
    
}



