package com.softwareag.core.models.article;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleQuote 
{
	
	    @ValueMapValue
	    private String image;

	    @ValueMapValue
	    private String quote;
	    
	    @ValueMapValue
	    private String source;
	    
	    /**
	     * Gets the configured image path
	     *
	     * @return {@link String} with the configured image path.
	     */
	    
	    public String getImage() {
			return image;
		}

		public String getQuote() {
			return quote;
		}

		public String getSource() {
			return source;
		}

		

}