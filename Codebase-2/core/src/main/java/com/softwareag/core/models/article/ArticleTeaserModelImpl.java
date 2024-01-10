package com.softwareag.core.models.article;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Component;
import com.softwareag.core.util.LinkUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Data
@EqualsAndHashCode(callSuper = false)
@Model(adaptables = { SlingHttpServletRequest.class },
		adapters = { ArticleTeaserModel.class, ComponentExporter.class },
		resourceType = { ArticleTeaserModelImpl.RESOURCE_TYPE },
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
		extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ArticleTeaserModelImpl implements ArticleTeaserModel{

	public static final String RESOURCE_TYPE = "softwareag/components/content/article/articleteaser";

	@Self
	@Delegate(types = Component.class)
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private Component component;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String articleteaser;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferencea;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferenceb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;
	
	@Setter @Getter
	private String iconName;
	
	@Setter @Getter
	private String iconNameb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String iconBackgroundColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String iconBackgroundColorb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String caption;
	

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLinkLabel;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String captiona;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLink;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaButtonLabel;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaButtonLink;
	
	@Setter @Getter
	private String ctaDestinationLink;
	
	@Setter @Getter
	private String ctaDestButtonLink;
	
	@Setter @Getter
	private String linkOpenIn;
	
	@Setter @Getter
	private String buttonLinkTargetVal_a;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String enableButton;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String enableLink;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLabelb;
	
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLinkb;
	
	@Setter @Getter
	private String ctaDestinationLinkb;
	
	@Setter @Getter
	private String linkTargetVal_b;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLabeld;
	
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLinkd;
	
	@Setter @Getter
	private String ctaDestinationLinkd;
	
	@Setter @Getter
	private String linkTargetVal_d;

	
	@Setter(AccessLevel.NONE)
	@ValueMapValue

	private String backgroundColora;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorb;
	
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColord;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String featureTag;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagCopyd;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagColord;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String featureTaga;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagCopya;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagColora;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String featureTagb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagCopyb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String tagColorb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTexta;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTextb;
	
	// Teaser C Properties
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferencec;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String captionc;
				   
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLabelc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String ctaLinkc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTextc;

	@Setter @Getter
	private String linkTargetVal_c;
	
	@Setter @Getter
	private String ctaDestinationLinkc;
	
	  @PostConstruct
	private void init() {
		if(StringUtils.isNotEmpty(fileReferencea)) {
			iconName = getIconNameVal(fileReferencea);
			iconName = iconName+"-"+iconBackgroundColor;
		}
		if(StringUtils.isNotEmpty(fileReferenceb)) {
			iconNameb = getIconNameVal(fileReferenceb);
			iconNameb = iconNameb+"-"+iconBackgroundColorb;
		}
		
		ctaDestinationLink = LinkUtil.getLink(ctaLink);
		linkOpenIn = LinkUtil.linkOpenIn(ctaLink);
		ctaDestButtonLink = LinkUtil.getLink(ctaButtonLink);
		buttonLinkTargetVal_a = LinkUtil.linkOpenIn(ctaButtonLink);
		
			
		ctaDestinationLinkb = LinkUtil.getLink(ctaLinkb);
		linkTargetVal_b = LinkUtil.linkOpenIn(ctaLinkb);
		//Article Teaser Variation D
		ctaDestinationLinkd = LinkUtil.getLink(ctaLinkd);
		linkTargetVal_d =  LinkUtil.linkOpenIn(ctaLinkd);
		
		// Articel teaser variation C 
		ctaDestinationLinkc = LinkUtil.getLink(ctaLinkc);
		linkTargetVal_c =  LinkUtil.linkOpenIn(ctaLinkc);
	}

	private String getIconNameVal(String filePath) {
		
		String[] tokens = filePath.split("/");
		String name = tokens[tokens.length-1];
		name = name.substring(0, name.lastIndexOf('.'));
		return name;
	}

}
