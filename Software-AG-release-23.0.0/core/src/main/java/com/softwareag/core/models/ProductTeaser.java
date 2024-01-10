package com.softwareag.core.models;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softwareag.core.models.download.AssetModel;

@ConsumerType
public interface ProductTeaser extends Component{


	// button, caption, description, fileReferencea, b, c,d , link productteaser, title
	
	String getProductteaser();
	
	String getTitle();
	
	String getDescription();
	
	String getButton();
	
	String getButtonText();
	
	String getCaptiona();
	
	String getCaptionc();
	
	String getCaptiond();
	
	String getBackgroundColora();
	
	String getBackgroundColorb();
	
	String getBackgroundColorc();
	
	String getBackgroundColord();
	
	String getLinka();
	
	String getLinkTexta();
	
	String getLinkb();
	
	String getLinkTextb();
	
	String getLinkc();
	
	String getLinkTextc();
	
	String getLinkd();
	
	String getLinkTextd();

	String getProductsCounte();
	String getSymbole();
	String getBackgroundColore();
	String getLinkTexte();
	String getLinke();

	
	/*
	 * String getTargeta();
	 * 
	 * String getTargetb();
	 * 
	 * String getTargetc();
	 * 
	 * String getTargetd();
	 */
	
	Boolean getEnableLink();
	
	Boolean getEnableButton();
	
	String getFileReferencea();
	
	String getFileReferenceb();
	
	String getFileReferencec();
	
	String getFileReference();
	
	String getAltTexta();
	
	String getAltTextb();
	
	String getAltTextc();
	
	String getIconBackgroundColor();
	
	String getIconBackgroundColorb();
	
	String getIconName();
	
	String getIconNameb();
	
	String getFeatureTag();
}
