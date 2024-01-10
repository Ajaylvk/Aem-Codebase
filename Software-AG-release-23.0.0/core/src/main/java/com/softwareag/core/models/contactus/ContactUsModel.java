package com.softwareag.core.models.contactus;

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
public class ContactUsModel {
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String layout;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;
	
	@Setter(AccessLevel.NONE)
	@ChildResource(name = "cards")
	Collection<Card> cards;
	
	

}
