package com.softwareag.core.models;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Spacer {

	@ValueMapValue
	protected String backgroundColor;

	@ValueMapValue
	protected String spacing;

	@Self
	private Resource resource;

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public String getSpacing() {
		return spacing;
	}

	
}
