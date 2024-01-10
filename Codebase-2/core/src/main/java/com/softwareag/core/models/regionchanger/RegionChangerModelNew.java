package com.softwareag.core.models.regionchanger;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.softwareag.core.models.link.LinkModel;
import com.softwareag.core.services.language.LanguageService;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RegionChangerModelNew{
	    
	    public static final String DOT_HTML = ".html";
	    protected static final String GENERAL_TAB = "general";
	    protected static final String GENERAL_TAB_TITLE = "title";
	    protected static final String  GENERAL_TAB_INTRO_TEXT = "introductoryText";
	    protected static final String REGION_TAB = "regions";
	    protected static final String REGION_TAB_REGION = "region";
	    protected static final String REGION_TAB_LANGUAGE = "language";
	    protected static final String REGION_TAB_LINK_HREF = "link";
	    protected static final String REGION_TAB_HREF = "href";
	   
	@Self
	private Resource currentResource;
	 
	@Inject
    private LinkModel link;
	
    private String title;
    
    private String introductoryText;
    
    @ValueMapValue
    private String region;
    
    @ValueMapValue
    private String language;
        
    @ValueMapValue
    private String href;
    
    @SlingObject
    private ResourceResolver resourceResolver;
	
	@OSGiService
	private LanguageService languageService;
    
    private final List<Resource> regionResources = new ArrayList<>();
   
     private final  LinkedHashMap<String, List<String>> mapRegion = new 
    		LinkedHashMap<String,List<String>>();
    
	@PostConstruct
	private void init()
	{
		initGeneral();
		initRegion();
		getRegionMap();
	}
	
	 public boolean hasContent() {
	        return StringUtils.isNotBlank(title)
	            || StringUtils.isNotBlank(introductoryText);
	    }
	
    public String getTitle()
    {
    	return title;
    }
    public String getIntroductoryText()
    {
    	return introductoryText;
    }
    
    public String getRegion()
    {
    	return region;
    }
    
    public String getHref() {

        return href;
    }
    
    public String getLanguage()
    {
    	return language;

    }
    public LinkModel getLink() {
        return link;
    }
    
    public  LinkedHashMap<String,List<String>> getMapRegion()
    {
    	return mapRegion;
    }
    
    private void initGeneral() {
        final Resource generalResource = currentResource.getChild(GENERAL_TAB);
        if (generalResource != null) {
            final ValueMap valueMap = generalResource.getValueMap();
            title = valueMap.get(GENERAL_TAB_TITLE, String.class);
            introductoryText = valueMap.get(GENERAL_TAB_INTRO_TEXT, String.class);
        }
    }
    
    private void initRegion() 
    {
    	final Resource tabResource = currentResource.getChild(REGION_TAB);
    	if (tabResource != null && tabResource.hasChildren()) {
    		
    		tabResource.getChildren().forEach(regionResources::add);	        
    }
    }
    
    public List<Resource> getRegionResources() {
        return Collections.unmodifiableList(regionResources);
    
    } 
    private void getRegionMap()
    {
    	if(regionResources!=null)
    	{
    		for(Resource res:regionResources)
    		{ 
    			final Resource linkResource = res.getChild(REGION_TAB_LINK_HREF);
    			link = linkResource.adaptTo(LinkModel.class);
                href=link.getHref();
    			ValueMap valueMap=res.getValueMap();
    			region=valueMap.get(REGION_TAB_REGION,String.class);
    			language=valueMap.get(REGION_TAB_LANGUAGE,String.class);
    			 Resource langResource = resourceResolver.getResource(link.getRawHref());
    			 String languageVal = languageService.getLanguage(langResource);
    			String encodeLanguage = URLDecoder.decode(language, StandardCharsets.UTF_8);
    			List<String> temp=new ArrayList<String>();
    			temp.add(encodeLanguage);
    			temp.add(href);
    			temp.add(languageVal);
    			mapRegion.put(region,temp);
    		}	
    	}
    }    
}
    