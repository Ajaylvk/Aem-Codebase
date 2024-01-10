package com.softwareag.core.models.article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleNavigation {
	
	
	protected static final String ARTICLE_CONTAINER = "article-container";
	protected static final String HEADING = "heading";
	protected static final String NAVIGATION="navigation";
	protected static final String ARTICLETEXT="articletext";
	
	    @ValueMapValue
	    private String heading;
		
		@ValueMapValue
	    private String enableNumberlist;
		
		@ValueMapValue
		 private String enableFaqSchema;
	 
	    @Self
	    Resource resource;
	    
	    @Self
		private Resource currentResource;
	    
	    private final List<Resource> headingResources = new ArrayList<>();
	    private final List<String> headingList = new ArrayList<>();
		private final List<String> articleText = new ArrayList<>();
	    private final Map<String, String> articleMap = new HashMap<>();
	   // private String faqSchema;
	    private String faqArray;
	   
	    public String getFaqArray() {
			return faqArray;
		}

		private final  LinkedHashMap<String, String> navItems = new 
	    		LinkedHashMap<String,String>();
	    
	    public Map<String, String> getArticleMap() {
			return articleMap;
		}
		public LinkedHashMap<String, String> getNavItems() {
			return navItems;
		}
		 public String getEnableNumberlist() {
			return enableNumberlist;
		}

		 public String getEnableFaqSchema() {
				return enableFaqSchema;
			}
		
	    @PostConstruct
		private void init()
		{
	    	initNavigation();
	    	getTextComponent();	
	    	getResults();
	    	setMap();
	    	
		}
	    
		private void initNavigation() 
	    {
	    	final Resource tabResource = currentResource.getChild(NAVIGATION);
	    	if (tabResource != null && tabResource.hasChildren()) {
	    		
	    		tabResource.getChildren().forEach(headingResources::add);	        
	    }
	    }
	    
		public List<Resource> getHeadingResources() {
	        return Collections.unmodifiableList(headingResources);
	    
	    }
	    
	    private void getResults()
	    {
	    	if(headingResources!=null)
	    	{
	    		for(Resource res:headingResources)
	    		{ 
	    			ValueMap valueMap=res.getValueMap();
	    			heading=valueMap.get(HEADING,String.class);
	    			headingList.add(heading);
	    					
	    		}	
	    	}
	    } 
	    
	    private void getTextComponent()
	    {
	    	 Resource childResource = resource.getParent().getParent();
	    	 
	    	 
	    	 JsonObject faqJson = new JsonObject();
	    	 JsonArray faqJsonArray = new JsonArray();
	    	 if(childResource!=null && childResource.hasChildren())
	    	 {
	    		 Iterator<Resource> it = childResource.listChildren();
	    		 while(it.hasNext()) {
	    			 Resource r = it.next();
	    			 
	    			if(r.getName().equalsIgnoreCase(ARTICLE_CONTAINER)) 
	    			{
	    		 Iterator<Resource> container=r.listChildren();
	    		
	    		 while (container.hasNext())
	    		 {
	    			 Resource child = container.next();
	    			 JsonObject temp  = new JsonObject();
	 	    		JsonObject answerObj  = new JsonObject();
	    			 
	    				 String parentNodeName = child.getName();
	    				 ValueMap value = child.getValueMap();
	    	             if (parentNodeName.startsWith(ARTICLETEXT)) {
	    	            	 String header = "";
	    	            	 String summaryString = "";
	    	            	 if((value.containsKey("header"))&&(value.containsKey("summary"))) {
								 articleText.add(parentNodeName);
								 header = value.get("header", String.class);
								 summaryString = value.get("summary", String.class).replaceAll("\\<.*?\\>", "");
                              if(!(header.isBlank()||summaryString.isBlank())) {
								  //", Value = " + entry.getValue());
								  temp.addProperty("@type", "Question");
								  temp.addProperty("name", header);

								  answerObj.addProperty("@type", "Answer");
								  answerObj.addProperty("text", summaryString);
								  temp.add("acceptedAnswer", answerObj);
								  faqJsonArray.add(temp);
								  articleMap.put(header, summaryString);
							  }
							 }
	    	             }
    	             
	    		 	}
	    		 	
	    			}
	    		 
	    	 }
	    	 }	 
	    	 faqJson.addProperty("@context", "https://schema.org");
	    	 faqJson.addProperty("@type", "FAQPage");
	    	 faqJson.add("mainEntity", faqJsonArray);
	    	 faqArray = faqJsonArray.toString();
	    	// faqSchema = faqJson.toString();
	    }
	    
		/*
		 * public String getSchemaJson() { return faqSchema; }
		 */
		private void setMap()
	    {
	    	Iterator<String> text=articleText.iterator();
	    	
	    	Iterator<String> headings=headingList.iterator();
	    	
	    	while(text.hasNext() && headings.hasNext())
	    	navItems.put(text.next(),headings.next());
	    }
	        	    
}
