package com.softwareag.core.models.impl;

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
import com.softwareag.core.models.ProductTeaser;
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

		adapters = { ProductTeaser.class, ComponentExporter.class },

		resourceType = { ProductTeaserImpl.RESOURCE_TYPE },

		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,

		extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ProductTeaserImpl implements ProductTeaser {

	public static final String RESOURCE_TYPE = "softwareag/components/content/productteaser_v1";

	@Self
	@Delegate(types = Component.class)
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private Component component;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String productteaser;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String title;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String captiona;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String captionc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String captiond;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColora;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorb;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColorc;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColord;


	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String description;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String button;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String buttonText;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linka;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkTexta;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkTextb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkTextc;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkd;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkTextd;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linke;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String linkTexte;
	
	/*
	 * @Setter(AccessLevel.NONE)
	 * 
	 * @ValueMapValue private String targeta;
	 * 
	 * @Setter(AccessLevel.NONE)
	 * 
	 * @ValueMapValue private String targetb;
	 * 
	 * @Setter(AccessLevel.NONE)
	 * 
	 * @ValueMapValue private String targetc;
	 * 
	 * @Setter(AccessLevel.NONE)
	 * 
	 * @ValueMapValue private String targetd;
	 */
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private Boolean enableLink;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private Boolean enableButton;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferencea;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferenceb;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReferencec;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String fileReference;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTexta;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTextb;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String altTextc;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String iconBackgroundColor;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String iconBackgroundColorb;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String backgroundColore;
	
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
	private String productsCounte;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String symbole;

	@Setter @Getter
	private String iconName;
	
	@Setter @Getter
	private String iconNameb;
	
	@Setter @Getter
	private String buttonDestLink;
	
	@Setter @Getter
	private String linkaDestUrl;
	
	@Setter @Getter
	private String linkbDestUrl;
	
	@Setter @Getter
	private String linkcDestUrl;
	
	@Setter @Getter
	private String linkdDestUrl;

	@Setter @Getter
	private String linkeDestUrl;
	
	@Setter @Getter
	private String buttonDestTagetVal;
	
	@Setter @Getter
	private String linkaTargetVal;
	
	@Setter @Getter
	private String linkbTargetVal;
	
	@Setter @Getter
	private String linkcTargetVal;
	
	@Setter @Getter
	private String linkdTargetVal;

	@Setter @Getter
	private String linkeTargetVal;

	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String html;
	
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
		
		buttonDestLink = LinkUtil.getLink(button);
		linkaDestUrl = LinkUtil.getLink(linka);
		linkbDestUrl = LinkUtil.getLink(linkb);
		linkcDestUrl = LinkUtil.getLink(linkc);
		linkdDestUrl = LinkUtil.getLink(linkd);
		linkeDestUrl = LinkUtil.getLink(linke);
		
		buttonDestTagetVal = LinkUtil.linkOpenIn(button);
		linkaTargetVal = LinkUtil.linkOpenIn(linka);
		linkbTargetVal = LinkUtil.linkOpenIn(linkb);
		linkcTargetVal = LinkUtil.linkOpenIn(linkc);
		linkdTargetVal = LinkUtil.linkOpenIn(linkd);
		linkeTargetVal = LinkUtil.linkOpenIn(linke);
	}

	private String getIconNameVal(String filePath) {
		
		String[] tokens = filePath.split("/");
		String name = tokens[tokens.length-1];
		name = name.substring(0, name.lastIndexOf('.'));
		return name;
	}
	
	
}
