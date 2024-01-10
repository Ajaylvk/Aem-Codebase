package com.softwareag.core.models.contactus;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.softwareag.core.util.LinkUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Card {

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String cardTitle;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String cardDescription;
	
	@Setter @Getter
	 private String linkDestinationUrl;
	 
	@Setter @Getter
	private String linkOpenIn;	
	
	@PostConstruct
	private void init() {
		linkDestinationUrl = LinkUtil.getLink(ctaLink);
		linkOpenIn = LinkUtil.linkOpenIn(ctaLink);
	}
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String enableLinka;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String enableLinkb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLabela;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLabelb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLink;
	
}
