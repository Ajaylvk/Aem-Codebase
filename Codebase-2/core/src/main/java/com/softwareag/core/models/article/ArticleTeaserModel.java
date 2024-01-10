package com.softwareag.core.models.article;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;

@ConsumerType
public interface ArticleTeaserModel extends Component{

	String getArticleteaser();
	
	String getTitle();
	
	String getDescription();
	
	String getFileReferencea();
	
	String getFileReferenceb();
	
	String getIconName();
	
	String getIconNameb();
	
	String getIconBackgroundColor();
	
	String getIconBackgroundColorb();
	
	String getCtaDestinationLink();
	
	String getLinkOpenIn();
	
}
