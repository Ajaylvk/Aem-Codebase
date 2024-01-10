package com.softwareag.core.util;

import org.apache.commons.lang3.StringUtils;

public class LinkUtil {


    public static final String CONTENT = "/content";
    public static final String CONTENT_DAM = CONTENT + "/dam";
    public static final String DOT_HTML = ".html";
    public static final String HASH = "#";
    public static final String Q_MARK = "?";
    public static final String BLANK = "_blank";
    public static final String SELF = "_self";
    
    private static String target;

	 public static boolean isInternalLink(String path) {
	        return StringUtils.startsWith(path, CONTENT) || StringUtils.startsWith(path, HASH);
	    }

	    public static String getLink(String url){
	    	if(StringUtils.isNotEmpty(url)) {
		    	boolean internal = isInternalLink(url);
		    	if(internal) {
		    		boolean link = isLink(url);
		    		if(link) {
		    			url=addHtmlToInternalURL(url);
		    		}
		    		
		    	}
				else {
					if(url.startsWith("mailto:")){
						return url;
					}
		    		if(!url.startsWith("http://") && !url.startsWith("https://")){
		    		    url = "https://"+url;
		    		 }
		    	}
	    	}
	    	 return url;
	    }
	    
	    public static String linkOpenIn(String url) {
	    	String target = null;
	    	 if (StringUtils.isNotBlank(url)) {
	             // if the target property is not configured, external links will open in a new window and internal links in the same window
	             target = isInternalLink(url) ? SELF : BLANK;
	    	 }
			return target;
	    }
	    
	    private static boolean isLink(String path) {
			// TODO Auto-generated method stub
	    	boolean link = false;
	    	if(!StringUtils.startsWith(path, CONTENT_DAM)) {
	    		link = true;
	    	}
			return link;
		}

		private static String addHtmlToInternalURL(String path) {
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
	        return path;
	    }
}
