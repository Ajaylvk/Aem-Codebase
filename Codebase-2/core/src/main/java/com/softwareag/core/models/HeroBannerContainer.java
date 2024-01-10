package com.softwareag.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Model(adaptables = { SlingHttpServletRequest.class },defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroBannerContainer {

	@Inject
	private Resource resource;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String gradientColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String transparencyLevel;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String svgImage; 
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String heroBannerBackground;
	

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String heroBannerVariation;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String heroBannercards;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String heroCards;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundImage;
	
	@Setter @Getter
	private List<String> cards = new ArrayList<String>();
	
	@Getter @Setter
	private String uniqueId; 
    

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tabletImage;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tabletAltText;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String mobileImage;
	

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String mobileAltText;
	
	@PostConstruct
	public void init() {
		uniqueId = String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
		if(StringUtils.isNotEmpty(heroCards)) {
			cards = getCardsList(heroCards);
		}
		
	}

	private List<String> getCardsList(String heroCards) {
		// TODO Auto-generated method stub
		List<String> temp = new ArrayList<String>();
		int count = Integer.parseInt(heroCards);
		if(count == 2) {
			temp.add("card1");
			temp.add("card2");
		}else if(count ==3) {
			temp.add("card1");
			temp.add("card2");
			temp.add("card3");
		}
		return temp;
	}
	
}
