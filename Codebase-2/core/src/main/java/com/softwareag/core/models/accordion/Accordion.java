package com.softwareag.core.models.accordion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
 
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Accordion {
 
	
	@ValueMapValue
    private String accordionTitle;
	
	 public String getAccordionTitle() {
	        return accordionTitle;
	    }
	 
	 @ValueMapValue
	    private String backgroundColor;
	    
	 public String getBackgroundColor() {
	        return backgroundColor;
	    }
	 
	   
	    @ValueMapValue
	    private String textAlignment;
	    
	    public String getTextAlignment() {
	        return textAlignment;
	    }
	
	    @ValueMapValue
	    private String enableFaqSchema;
	    
	    @Inject
	    @Named("accordion/.")
	    private List<AccordionModel> accordionList; 
 
	    public List<AccordionModel> getAccordionList() {
	        return accordionList;
	    }
 
    
    
	    private String faqArray;
	 
	    public String getEnableFaqSchema() {
			return enableFaqSchema;
		}
	    
		@Inject    
	    Resource resource; 
		
		List<Resource> accordionItems = new ArrayList<>();
	
		@PostConstruct
	    public void init() {
			
			if(StringUtils.isNotEmpty(enableFaqSchema) && enableFaqSchema.equalsIgnoreCase("true")) {
				getchildResource();
				getaccordionItemsList();
			}
		}
	
		private void getaccordionItemsList() {
			JsonArray faqJsonArray = new JsonArray();
			if(accordionItems!=null)
	    	{
	    		for(Resource res:accordionItems)
	    		{ 
	    			ValueMap valueMap=res.getValueMap();
	    			JsonObject faqObj = new JsonObject();
		    		JsonObject answerObj = new JsonObject();
		    		faqObj.addProperty("@type", "Question");
		        	faqObj.addProperty("name", valueMap.get("itemTitle", String.class));
		             
		             answerObj.addProperty("@type", "Answer");
		             answerObj.addProperty("text", valueMap.get("description", String.class));
		             faqObj.add("acceptedAnswer", answerObj);
		             faqJsonArray.add(faqObj);
	    					
	    		}	
	    	}
			faqArray = faqJsonArray.toString();
		}

		private void getchildResource() {
			
			final Resource tabResource = resource.getChild("accordion");
	    	if (tabResource != null && tabResource.hasChildren()) {
	    		
	    		tabResource.getChildren().forEach(accordionItems::add);	        
	    }
			
		}

		public String getFaqArray() {
			return faqArray;
		}
    
		
}


