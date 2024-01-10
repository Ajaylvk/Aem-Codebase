package com.softwareag.core.models.banner;

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
@Model(adaptables= {SlingHttpServletRequest.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BannerModel {

	@Inject
	private Resource resource;

	
	//Generic properties
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerType;
	
	//Text Area config
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String caption;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;

	@Setter(AccessLevel.NONE)
	private boolean isDesktopImage;

	@Setter(AccessLevel.NONE)
	private boolean isTabletImage;

	@Setter(AccessLevel.NONE)
	private boolean isMobileImage;

	//Background Image config


	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReference;

	@ValueMapValue
	private String backgroundImage;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String desktopSvgImage;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tabletImage;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String mobileImage;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String imgAltText;
		
	//Banner A properties
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerASize;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorA;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String textImageRatio;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerATextAlignment;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fontColorA;
	
	
		//KPI section of Banner A
		@Setter(AccessLevel.NONE)
		@ValueMapValue
		private String addKPISectionA;
		
		@Setter(AccessLevel.NONE)
		@ValueMapValue
		private String kpiCards;
		
		@Setter @Getter
		private List<String> cards = new ArrayList<String>();
		
	//Banner B Properties
		
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerBSize;
		
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorB;
		
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerBTextAlignment;
		
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String addKPISectionB;
		
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerBCards;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fontColorB;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fgImageMedium;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fgImageLarge;
	
	@Setter @Getter
	private List<String> bCards = new ArrayList<String>();
	
	//Banner C Properties
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerCSize;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorC;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String gradientPreset;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String checkboxGradient;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String checkboxTransperancy;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String transperancyLevel;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerCTextAlignment;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String addKPISectionC;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerCCards;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fontColorC;
	
	
	@Setter @Getter
	private List<String> cCards = new ArrayList<String>();
	
	//Banner D Properties
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorD;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String addDivider;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String addBannerBar;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerDGradientColors;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String bannerDTextAlignment;
	
	@PostConstruct
	public void init() {

		if(StringUtils.isNotEmpty(kpiCards)) {
			cards = getCardsList(kpiCards,bannerType);
		}
		
		if(StringUtils.isNotEmpty(bannerBCards)) {
			bCards = getCardsList(bannerBCards, bannerType);
		}
		
		if(StringUtils.isNotEmpty(bannerCCards)) {
			cCards = getCardsList(bannerCCards, bannerType);
		}
		isDesktopImage=checkIsImage(fileReference);
		isTabletImage=checkIsImage(tabletImage);
		isMobileImage=checkIsImage(mobileImage);
	}

	private  boolean checkIsImage(String fileReference) {
		if(fileReference!=null&&!fileReference.isEmpty()) {
			if (fileReference.endsWith(".png") || fileReference.endsWith(".jpeg") || fileReference.endsWith(".jpg")||fileReference.endsWith(".svg"))
				return true;
		}
		return false;
	}

	private List<String> getCardsList(String kpiCards, String bannerType) {
		// TODO Auto-generated method stub
		List<String> temp = new ArrayList<String>();
		int count = Integer.parseInt(kpiCards);
		if(count == 2) {
			temp.add(bannerType+"_card1");
			temp.add(bannerType+"_card2");
		}else if(count ==3) {
			temp.add(bannerType+"_card1");
			temp.add(bannerType+"_card2");
			temp.add(bannerType+"_card3");
		}
		return temp;
	}
}
