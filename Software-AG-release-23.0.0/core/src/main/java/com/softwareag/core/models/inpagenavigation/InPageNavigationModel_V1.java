package com.softwareag.core.models.inpagenavigation;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InPageNavigationModel_V1 {

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColor;

	@ValueMapValue
	private String alignment;
	
	
	@Setter(AccessLevel.NONE)
	@ChildResource(name = "pageNavigationLinks")
	Collection<NavItems> navItems;

	public String getAlignment(){ 
        return alignment;
    }
	
	

	
}
