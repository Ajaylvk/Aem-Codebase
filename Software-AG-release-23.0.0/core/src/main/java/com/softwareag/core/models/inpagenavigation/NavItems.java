package com.softwareag.core.models.inpagenavigation;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavItems {

	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private Boolean hideInNavigation;
	
}
