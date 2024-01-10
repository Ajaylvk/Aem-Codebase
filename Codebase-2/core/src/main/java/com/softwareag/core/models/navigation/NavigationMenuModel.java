package com.softwareag.core.models.navigation;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.util.LinkUtil;


@Model(adaptables = {Resource.class,SlingHttpServletRequest.class}, resourceType = { NavigationMenuModel.RESOURCE_TYPE }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationMenuModel {
	 private static final Logger LOG = LoggerFactory.getLogger(NavigationMenuModel.class);
	 public static final String RESOURCE_TYPE = "softwareag/components/content/navigation_menu-v1";
	    public static final String DOT_HTML = ".html";
	    public static final String HASH = "#";
	    public static final String Q_MARK = "?";
	    public static final String CONTENT = "/content";
	    
	@Inject    
    Resource resource;     
   
	@Inject
	String type;
	
    public String getType() {
		return type;
	}

	private SiteNavigation siteNavigation;
    @Getter @Setter
	private List<AdditionalLinks> combinedLinks;

	public SiteNavigation getSiteNavigation() {
		return siteNavigation;
	}
	
@PostConstruct
  public void init() {

	  JsonObject rootJson = new JsonObject();

	  Iterator<Resource> itr = resource.listChildren();
	  ValueMap type = resource.adaptTo(ValueMap.class);
	  while (itr.hasNext()) {
		  Resource siteNav = itr.next();
		  if (siteNav.getName().equalsIgnoreCase("additionalLinks")) {

			  Iterator<Resource> extralinks = siteNav.listChildren();
			  JsonArray extraLinksArray = getExtraLinks(extralinks);
			  LOG.debug("final Extralinks array is:::" + extraLinksArray.toString());
			  rootJson.add("additionalLinks", extraLinksArray);
		  } else if (siteNav.getName().equalsIgnoreCase("brandcenterzerolevel")&&(type.get("type",String.class).equalsIgnoreCase("brandcenter"))) {
			  Iterator<Resource> levelzerolinks = siteNav.listChildren();
			  LOG.debug("Entered into brandceterzerolevel" + levelzerolinks);
			  combinedLinks = getBrandCenterLevelZero(levelzerolinks);
		  } else {
			  LOG.debug("Name of the iterated node" + siteNav.getName());
				  LOG.debug("Name of the type from site NAv" + type.get("type",String.class));
				  if (siteNav.getName().equalsIgnoreCase(type.get("type",String.class))) {
					  rootJson.addProperty("siteName", siteNav.getName());
					  Iterator<Resource> levelzeroResources = siteNav.listChildren();

					  JsonArray levelZeroArray = new JsonArray();
					  while (levelzeroResources.hasNext()) {
						  Resource levelzeroResource = levelzeroResources.next();
						  // LOG.debug("level Zero Sitenav links::::"+levelzeroResource.getName()+"::path::"+levelzeroResource.getPath());
						  //Level Zero Links
						  JsonObject levelZeroObj = new JsonObject();
						  JsonArray levelOneArray = new JsonArray();
						  //Level Zero Links
						  if (siteNav.getName().equalsIgnoreCase("devcenter")) {
							  ValueMap levelZeroValueMap = levelzeroResource.adaptTo(ValueMap.class);
							  levelZeroObj.addProperty("levelZeroTitle", getJsonPropertyValue(levelZeroValueMap.get("levelzerotitle", String.class)));
							  levelZeroObj.addProperty("levelZeroLinkDestination", LinkUtil.getLink(getJsonPropertyValue(levelZeroValueMap.get("levelzerolinkdestination", String.class))));
							  levelZeroObj.addProperty("levelZeroLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(levelZeroValueMap.get("levelzerolinkdestination", String.class))));
						  } else {
							  ValueMap levelZeroValueMap = levelzeroResource.adaptTo(ValueMap.class);
							  String url = null;
							  if (StringUtils.isNotEmpty(levelZeroValueMap.get("overviewlinkdestination", String.class))) {
								  url = levelZeroValueMap.get("overviewlinkdestination", String.class);
							  }
							  levelZeroObj.addProperty("levelZeroTitle", getJsonPropertyValue(levelZeroValueMap.get("levelzerotitle", String.class)));
							  levelZeroObj.addProperty("overviewLinkLabel", getJsonPropertyValue(levelZeroValueMap.get("overviewlinklabel", String.class)));
							  levelZeroObj.addProperty("overviewLinkDestination", LinkUtil.getLink(getJsonPropertyValue(levelZeroValueMap.get("overviewlinkdestination", String.class))));
							  levelZeroObj.addProperty("overviewLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(levelZeroValueMap.get("overviewlinkdestination", String.class))));
						  }
						  Iterator<Resource> levelOneResources = levelzeroResource.listChildren();

						  while (levelOneResources.hasNext()) {
							  Resource levelOneResource = levelOneResources.next();
							  Iterator<Resource> levelOneResourceChild = levelOneResource.listChildren();
							  while (levelOneResourceChild.hasNext()) {
								  JsonObject levelOneObj = new JsonObject();
								  Resource leveloneLinks = levelOneResourceChild.next();
								  Map<String, Object> levelOneMap = new HashMap<>();
								  ValueMap leveloneLinksValueMap = leveloneLinks.adaptTo(ValueMap.class);
								  levelOneObj.addProperty("levelOneTitle", getJsonPropertyValue(leveloneLinksValueMap.get("levelonetitle", String.class)));
								  levelOneObj.addProperty("levelOneDestination", LinkUtil.getLink(getJsonPropertyValue(leveloneLinksValueMap.get("levelonedestination", String.class))));
								  levelOneObj.addProperty("levelOneLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(leveloneLinksValueMap.get("levelonedestination", String.class))));
								  levelOneObj.addProperty("enablePrimaryPromo", getJsonPropertyValue(leveloneLinksValueMap.get("enableprimarypromo", String.class)));
								  levelOneObj.addProperty("enableSecondaryPromo", getJsonPropertyValue(leveloneLinksValueMap.get("enablesecondarypromo", String.class)));
								  levelOneObj.addProperty("primaryPromoHeading", getJsonPropertyValue(leveloneLinksValueMap.get("primarypromoheading", String.class)));
								  levelOneObj.addProperty("primaryPromoCaption", getJsonPropertyValue(leveloneLinksValueMap.get("primarypromocaption", String.class)));
								  levelOneObj.addProperty("primaryPromoDescription", getJsonPropertyValue(leveloneLinksValueMap.get("primarypromodescription", String.class)));
								  levelOneObj.addProperty("primaryPromoLinkLabel", getJsonPropertyValue(leveloneLinksValueMap.get("primarypromolinklabel", String.class)));
								  levelOneObj.addProperty("primaryPromoLinkDestination", LinkUtil.getLink(getJsonPropertyValue(leveloneLinksValueMap.get("primarypromolinkdestination", String.class))));
								  levelOneObj.addProperty("primaryPromoLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(leveloneLinksValueMap.get("primarypromolinkdestination", String.class))));
								  levelOneObj.addProperty("primaryPromoReference", getJsonPropertyValue(leveloneLinksValueMap.get("primaryPromoReference", String.class)));
								  levelOneObj.addProperty("altTextPrimary", getJsonPropertyValue(leveloneLinksValueMap.get("altTextPrimary", String.class)));
								  levelOneObj.addProperty("secondaryPromoReference", getJsonPropertyValue(leveloneLinksValueMap.get("secondaryPromoReference", String.class)));
								  levelOneObj.addProperty("altTextSecondary", getJsonPropertyValue(leveloneLinksValueMap.get("altTextSecondary", String.class)));
								  levelOneObj.addProperty("secondaryPromoHeading", getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromoheading", String.class)));
								  levelOneObj.addProperty("secondaryPromoCaption", getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromocaption", String.class)));
								  levelOneObj.addProperty("secondaryPromoDescription", getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromodescription", String.class)));
								  levelOneObj.addProperty("secondaryPromoLinkLabel", getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromolinklabel", String.class)));
								  levelOneObj.addProperty("secondaryPromoLinkDestination", LinkUtil.getLink(getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromolinkdestination", String.class))));
								  levelOneObj.addProperty("secondaryPromoLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(leveloneLinksValueMap.get("secondarypromolinkdestination", String.class))));
								  JsonArray levelTwoArray = new JsonArray();
								  Iterator<Resource> levelTwoResources = leveloneLinks.listChildren();
								  while (levelTwoResources.hasNext()) {
									  Resource levelTwoResource = levelTwoResources.next();
									  Iterator<Resource> levelTwoResourceLinks = levelTwoResource.listChildren();
									  while (levelTwoResourceLinks.hasNext()) {
										  Resource levelTwoLinks = levelTwoResourceLinks.next();
										  ValueMap levelTwoLinksValueMap = levelTwoLinks.adaptTo(ValueMap.class);
										  JsonObject levelTwoObj = new JsonObject();
										  levelTwoObj.addProperty("levelTwoTitle", getJsonPropertyValue(levelTwoLinksValueMap.get("leveltwotitle", String.class)));
										  levelTwoObj.addProperty("levelTwoDestination", LinkUtil.getLink(getJsonPropertyValue(levelTwoLinksValueMap.get("leveltwodestination", String.class))));
										  levelTwoObj.addProperty("levelTwoLinkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(levelTwoLinksValueMap.get("leveltwodestination", String.class))));
										  levelTwoArray.add(levelTwoObj);
									  }
								  }
								  //add level two to level one obj
								  levelOneObj.add("levelTwo", levelTwoArray);
								  levelOneObj.addProperty("levelTwoListSize", levelTwoArray.size());
								  levelOneArray.add(levelOneObj);
							  }

						  }
						  levelZeroObj.add("levelOne", levelOneArray);
						  levelZeroObj.addProperty("levelOneListSize", levelOneArray.size());
						  levelZeroArray.add(levelZeroObj);
					  }

					  rootJson.add("levelZero", levelZeroArray);
					  rootJson.addProperty("levelZeroListSize", levelZeroArray.size());

				  }
			  }
		  }
	  LOG.debug("rootjson Object::::::"+rootJson.toString());
	  Gson gson = new Gson();
	  siteNavigation  = gson.fromJson(rootJson, SiteNavigation.class);
	  
}

	private List<AdditionalLinks> getBrandCenterLevelZero(Iterator<Resource> levelzerolinks) {
		List<AdditionalLinks> combinedLinks = new ArrayList<>();
		while (levelzerolinks.hasNext()){
		  Resource levelZero=levelzerolinks.next();
		  ValueMap level0=levelZero.adaptTo(ValueMap.class);
		  AdditionalLinks links=new AdditionalLinks();
		  links.setLinkLabel(level0.get("brandcenterlevelzerotitle",String.class));
		  links.setLinkDestination(level0.get("brandcenterlevelzerolinkdestination",String.class));
		  links.setLinkOpenIn(LinkUtil.linkOpenIn(level0.get("brandcenterlevelzerolinkdestination", String.class)));
		  combinedLinks.add(links);
	  }
		return combinedLinks;
	}

	private JsonArray getExtraLinks(Iterator<Resource> extralinks) {
	
		 JsonArray extraLinksArray = new JsonArray();
		   while(extralinks.hasNext()) {
			   Resource links = extralinks.next();
			   ValueMap linkValuesMap = links.adaptTo(ValueMap.class);
			   JsonObject tempObj = new JsonObject();
			   tempObj.addProperty("linkLabel", getJsonPropertyValue(linkValuesMap.get("linkLabel", String.class)));
			   tempObj.addProperty("linkDestination", LinkUtil.getLink(getJsonPropertyValue(linkValuesMap.get("linkDestination", String.class)))); 
			   tempObj.addProperty("linkOpenIn", LinkUtil.linkOpenIn(getJsonPropertyValue(linkValuesMap.get("linkDestination", String.class))));
			   extraLinksArray.add(tempObj);
		   }
		   
	return extraLinksArray;
	}


	private String getJsonPropertyValue(String inputString) {
		
		return StringUtils.isNotEmpty(inputString)? inputString : null;
		
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
