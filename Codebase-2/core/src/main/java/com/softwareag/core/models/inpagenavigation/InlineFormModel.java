package com.softwareag.core.models.inpagenavigation;

import java.util.Collection;

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
public class InlineFormModel {

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String greyLine;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String formDescription;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String buttonLabel;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String buttonDestination;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkLabel;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkDestination;
	
	
}
