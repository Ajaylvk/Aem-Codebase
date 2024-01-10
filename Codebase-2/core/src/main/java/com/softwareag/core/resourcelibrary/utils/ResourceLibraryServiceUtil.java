package com.softwareag.core.resourcelibrary.utils;

import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.constants.RLConstants;

public class ResourceLibraryServiceUtil {

	public static final String CQ_LAST_MODIFIED = "@jcr:content/cq:lastModified";
	
	private static final Logger LOG = LoggerFactory.getLogger(ResourceLibraryServiceUtil.class);
	
	public static Map<String, String> getPredicateGroup(String path, List<TagsBean> tagsBean) {
		
		 Map<String, String> map = new HashMap<String, String>();
		  map.put(RLConstants.PATH, path);
		  map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
		  map.put(RLConstants.ONE_PROPERTY, RLConstants.TOPIC_TAGS_PROP);
		  map.put(RLConstants.TWO_PROPERTY, RLConstants.CONTENT_TYPE_TAGS_PROP);
		  map.put(RLConstants.THREE_PROPERTY, RLConstants.INDUSTRY_TAGS_PROP);
		  //dynamic tags 
		  int i = 1;
		  if(tagsBean.size() > 0) {
		  for(TagsBean bean: tagsBean) {
			  LOG.debug("Tag value::"+bean.getValue());
			  if(bean.getValue().contains(RLConstants.TOPIC)) {
				  map.put(RLConstants.ONE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
			  }else if(bean.getValue().contains(RLConstants.CONTENT)) {
				  map.put(RLConstants.TWO_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
			  }else if(bean.getValue().contains(RLConstants.INDUSTRY_V1)) {
				  map.put(RLConstants.THREE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
			  }
			  i++;
		  } 
		  }else {
			  LOG.debug("No tags available:::");
			  map.put("property.value","");
		  }
		  map.put(RLConstants.ORDER_BY,CQ_LAST_MODIFIED);
		  map.put(RLConstants.ORDER_BY_SORT,RLConstants.DESC);
		  map.put(RLConstants.P_DOT_LIMIT, RLConstants.MINUS_ONE);
		  
		  return map;
	}

	public static JsonArray updateContentTags(String[] contentTag, ResourceResolver resolver, JsonArray contentTags, Locale pageLocale) {
		final TagManager tagManager = resolver.adaptTo(TagManager.class);
		if(ArrayUtils.isNotEmpty(contentTag)) {
			for (String str : contentTag) {
				LOG.debug("Content type Tag ::::"+str);
				JsonObject tagObj = new JsonObject();
				Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				tagObj.addProperty(tag.getTitle(pageLocale), tag.getTagID());
				LOG.debug("Content type Tag Name:::"+tag.getName());
				 if (!contentTags.contains(tagObj)) {
					contentTags.add(tagObj);
				}
			}
		}
		return contentTags;
	}


	public static JsonArray updateIndustryTags(String[] industryTag, ResourceResolver resolver,
											   JsonArray industryTags, Locale pageLocale) {
		 //JsonArray tempArray = industryTags1; 
		  final TagManager tagManager = resolver.adaptTo(TagManager.class); 
		  if(ArrayUtils.isNotEmpty(industryTag)) {
			  for(String str: industryTag) { 
				  LOG.debug("Industry Tag ::::"+str);
				  JsonObject tagObj = new JsonObject(); 
				  Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				  tagObj.addProperty(tag.getTitle(pageLocale), tag.getTagID());
				  LOG.debug("Industry Tag Name:::"+tag.getName());
				 if(!industryTags.contains(tagObj)) {
					  industryTags.add(tagObj);
				  }
				}
		  }
		  return industryTags;
	}

	public static JsonArray updateTopicTags(String[] topicTag, ResourceResolver resolver, JsonArray topicTags, Locale pageLocale) {
		final TagManager tagManager = resolver.adaptTo(TagManager.class);
		 if(ArrayUtils.isNotEmpty(topicTag)) {
			for (String str : topicTag) {
				LOG.debug("Topic Tag ::::"+str);
				JsonObject tagObj = new JsonObject();
				Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				tagObj.addProperty(tag.getTitle(pageLocale), tag.getTagID());
				 LOG.debug("Topic Tag Name:::"+tag.getName());
				if ( !topicTags.contains(tagObj)) {
					topicTags.add(tagObj);
				}
			}
		 }
		return topicTags;
	}

	public static Map<String, String> getSearchPredicateGroup(String assetpath, RequestObject requestObj) {
		// TODO Auto-generated method stub
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
		map.put(RLConstants.FULLTEXT, requestObj.getSearchterm());
		map.put(RLConstants.GROUP_PATH,assetpath);
		map.put(RLConstants.GROUP2_PROP, RLConstants.SEARCH_PROP);
		map.put(RLConstants.GROUP2_PROP_VALUE, requestObj.getSearchterm());
		map.put(RLConstants.GROUP2_PROP_OPERATION, RLConstants.LIKE);
		map.put(RLConstants.GROUP_P_OR, RLConstants.TRUE);
		map.put(RLConstants.ORDER_BY,CQ_LAST_MODIFIED);
		map.put(RLConstants.ORDER_BY_SORT,RLConstants.DESC);
		map.put(RLConstants.P_DOT_LIMIT, RLConstants.MINUS_ONE);
		 
		return map;
	}
	
	

}
