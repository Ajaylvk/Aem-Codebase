package com.softwareag.core.models.link;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderSearch {
	
	
	     @ValueMapValue
	    private String image;

	    @ValueMapValue
	    private String altText;
	    
	    @ValueMapValue
	    private String text;
	    
	    
	    /**
	     * Gets the configured image path
	     *
	     * @return {@link String} with the configured image path.
	     */
	    public String getImage() {

	        return image;
	    }

	    /**
	     * Gets the configured Alternative Text.
	     *
	     * @return {@link String} with the configured Alternative Text.
	     */
	    public String getAltText() {

	        return altText;
	    }

	    /**
	     * Gets the search text.
	     *
	     * @return {@link String} with the configured destination href.
	     */
	    public String getText() {

	        return text;
	    }
	    
	    public void setAltText(String altText) {
			this.altText = altText;
		}

	    
	    

}
