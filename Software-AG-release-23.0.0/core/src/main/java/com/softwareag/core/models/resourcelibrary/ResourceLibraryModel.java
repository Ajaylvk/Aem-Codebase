package com.softwareag.core.models.resourcelibrary;

import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;

@ConsumerType
public interface ResourceLibraryModel extends Component{
	
	default String[] getContenttags() {
        return null;
    }
	
	default String[] getCategorytags() {
        return null;
    }
	
	default String[] getIndustrytags() {
        return null;
    }
	
	default String getResourcePath() {
		return null;
	}
	
	default String getPromotedTag() {
		return null;
	}
	
	default String getPopularTag() {
		return null;
	}
}
