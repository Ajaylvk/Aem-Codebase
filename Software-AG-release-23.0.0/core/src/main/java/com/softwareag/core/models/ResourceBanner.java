package com.softwareag.core.models;

import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.softwareag.core.util.LinkUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ResourceBanner {
	
	 @ValueMapValue
	private String title;
	 
	@ValueMapValue 
	private String copyText;
	
	@ValueMapValue
    private String captionText;
	
	@ValueMapValue
    private String captionTitle;
    
    @ValueMapValue
    private String description;
    

    @ValueMapValue
    private String linkDestination;
    
    @ValueMapValue
    private String linkLabel;
    
	private String openIn;
    
    
    @PostConstruct
    private void init() {
        
            this.linkDestination = LinkUtil.getLink(linkDestination);
            this.openIn=LinkUtil.linkOpenIn(linkDestination);
           
    }

	public String getOpenIn() {
		return openIn;
	}

	public String getTitle() {
		return title;
	}

	public String getCopyText() {
		return copyText;
	}
	
	
	public String getLinkDestination() {
		return linkDestination;
	}

	
	public String getDescription() {
		return description;
	}
	
	public String getCaptionText() {
		return captionText;
	}

	public String getCaptionTitle() {
		return captionTitle;
	}
	
	public String getLinkLabel() {
		return linkLabel;
	}
}
