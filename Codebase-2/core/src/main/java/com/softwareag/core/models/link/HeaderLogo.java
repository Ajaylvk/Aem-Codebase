package com.softwareag.core.models.link;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class HeaderLogo {
	
	public static final String DOT_HTML = ".html";
    public static final String HASH = "#";
    public static final String Q_MARK = "?";
    public static final String CONTENT = "/content";
	   
	    @Inject
        private LinkModel link;
	
	    @ValueMapValue
	    private String image;

	    @ValueMapValue
	    private String altText;
	    
	    @ValueMapValue
	    private String title;
	    
	    private String href;//for logo link destination
	    
	    @ValueMapValue
	    private String hrefSite;//for site link destination
	    
	    @ValueMapValue
	    private String utmParameter;
	    
	    private String target;
	    
	    
	    @PostConstruct
	    private void init() {
	        if (link != null) {
	            this.href=link.getHref();
	            this.hrefSite=addHtmlToInternalURL(hrefSite);
	            this.utmParameter=link.getUtmParameter();
	            this.target=link.getTarget();
	        }
	    }
	    public String getImage() {

	        return image;
	    }
	    
	    public String getAltText() {

	        return altText;
	    }
	    
	    public String getTitle() {

	        return title;
	    }
	    
	    public String getUtmParameter() {

	        return utmParameter;
	    }
	    
	    public String getHref() {

	        return href;
	    }
	    
	    public String getHrefSite() {
	    	
	        return addHtmlToInternalURL(hrefSite);
	    }
	      
	    public LinkModel getLink() {
	        return link;
	    }
	    
	    public String getTarget() {

	        return target;
	    }
	    
	    public void setLink(LinkModel link) {
			this.link = link;
		}

		public void setAltText(String altText) {
			this.altText = altText;
		}

		public void setHref(String href) {
			this.href = href;
		}
		
		
		public void setTarget(String target) {
			this.target = target;
		}
		
		private String addHtmlToInternalURL(String path) {
			
			if(StringUtils.startsWith(path, CONTENT)) {
			    if (!StringUtils.containsIgnoreCase(path, DOT_HTML)) {
			        // If Link Destination is set to "#jumpTo".
			        if (StringUtils.startsWith(path, HASH)) {
			            return path;
			        }
			        // If Link Destination is set to "/content/softwareag/currentPage#jumpTo".
			        int hashIndex = path.indexOf(HASH);
			        if (StringUtils.contains(path, HASH) && !StringUtils.contains(path, Q_MARK)) {
			            path = path.substring(0, hashIndex) + DOT_HTML + path.substring(hashIndex);
			            return path;
			        }
			       
			        path += DOT_HTML;
			    }
			}
		    return path;
		}
		
}
