package com.softwareag.core.services.resourcelibrary;

import java.util.*;

import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletResponse;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.services.language.LanguageService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.constants.RLConstants;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import com.softwareag.core.resourcelibrary.utils.ResourceLibraryServiceUtil;
import com.softwareag.core.resourcelibrary.utils.TagsBean;
import com.softwareag.core.util.LinkUtil;

@Component(service = ResourceLibraryService.class, immediate = true)
public class ResourceLibararyServiceImpl implements ResourceLibraryService {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceLibararyServiceImpl.class);

	@Reference
	QueryBuilder builder;

	@Reference
	private LanguageService languageService;

	private Locale pageLocale;

	@SuppressWarnings("unused")
	@Override
	public JsonObject getResponse(RequestObject requestObj, SlingHttpServletRequest request) {
		JsonObject rootObj = new JsonObject();
		ResourceResolver resolver = request.getResourceResolver();
		PageManager pgMgr= resolver.adaptTo(PageManager.class);
		Page currentPage=pgMgr.getPage(request.getResource().getPath().split("jcr:content")[0]);
        pageLocale= Locale.forLanguageTag(languageService.getLocaleString(currentPage));
		try {
			// TODO Auto-generated method stub
			Resource resourceObj = request.getResource();
			Iterator<Resource> resources = null;
			if (resolver != null) {
				Session session = resolver.adaptTo(Session.class);
				if (RLConstants.FILTER.equalsIgnoreCase(requestObj.getRequesttype())) {
					LOG.info("Filter based search service :::::");
					List<TagsBean> tagsBean = requestObj.getValues();
					Map<String, String> predicateMap = ResourceLibraryServiceUtil
							.getPredicateGroup(requestObj.getAssetpath(), tagsBean);
					LOG.debug("predicatemap:::::" + predicateMap);
					com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
					SearchResult result = query.getResult();
					resources = result.getResources();
					if (resources.hasNext()) {
						rootObj = getResponseJson(requestObj, resources, resolver, request);
					} else {
						LOG.info("FILTER : No query results found");
						rootObj.addProperty("message", "No Search Results Found");
					}
				} else if (RLConstants.RESOURCE_SEARCH.equalsIgnoreCase(requestObj.getRequesttype())) {
					LOG.info("full text search service:::::");

					Map<String, String> predicateMap = ResourceLibraryServiceUtil
							.getSearchPredicateGroup(requestObj.getAssetpath(), requestObj);
					LOG.debug("full text search predicatemap:::::" + predicateMap);
					com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
					SearchResult result = query.getResult();
					resources = result.getResources();
					if (resources.hasNext()) {
						rootObj = getResponseJson(requestObj, resources, resolver, request);
					} else {
						LOG.info("RESOURCE_SEARCH : No query results found");
						rootObj.addProperty("message", "No Search Results Found");
					}
				} else if (RLConstants.INITIAL_RENDER.equalsIgnoreCase(requestObj.getRequesttype())) {
					LOG.info("default Search service:::::::::");
					String query = "/jcr:root" + requestObj.getAssetpath()
							+ "//*[@sling:resourceType='softwareag/components/structure/resource-page'] order by @cq:lastModified descending";
					LOG.debug("default search query::::" + query);
					resources = resolver.findResources(query, Query.XPATH);
					if (resources.hasNext()) {
						rootObj = getResponseJson(requestObj, resources, resolver, request);
					} else {
						LOG.info("INITIAL_RENDER : No query results found");
						rootObj.addProperty("message", "No Search Results Found");
					}
				} else {
					LOG.debug("reqeust type is empty :::" + requestObj.getRequesttype());
				}
			}
			resolver.commit();
		} catch (Exception e) {
			LOG.debug("Error fetching search results:::" + e.getStackTrace());
			final String errorMsg = String.format("Error occured during peforming operation: %s", e.getMessage());
			// response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
			rootObj.addProperty("statuscode", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			rootObj.addProperty("message", RLConstants.ERROR_MSG);
			LOG.error("exception ", e);
		} finally {
			if (resolver != null && resolver.isLive()) {
				resolver.close();
				resolver = null;
			}
		}
		return rootObj;
	}

	private JsonObject getResponseJson(RequestObject requestObj, Iterator<Resource> resources,
			ResourceResolver resolver, SlingHttpServletRequest request) {
		JsonObject rootObj = new JsonObject();
		JsonArray resourceArray = new JsonArray();
		JsonObject tagsObj = new JsonObject();
		JsonArray popularArray = new JsonArray();
		JsonArray promotedArray = new JsonArray();

		JsonArray contentTags = new JsonArray();
		JsonArray topicTags = new JsonArray();
		JsonArray industryTags = new JsonArray();

		while (resources.hasNext()) {
			Resource resource = null;
			if (RLConstants.FILTER.equalsIgnoreCase(requestObj.getRequesttype())
					|| RLConstants.RESOURCE_SEARCH.equalsIgnoreCase(requestObj.getRequesttype())) {
				resource = resources.next().getChild(RLConstants.JCR_CONTENT);
			} else {
				resource = resources.next();
			}
			if (resource != null) {
				LOG.debug("Resource Name::::" + resource.getName());
				LOG.debug("Resource Path::::" + resource.getPath());
				ValueMap properties = resource.adaptTo(ValueMap.class);
				if (StringUtils.isNotEmpty(getPropertyValue(properties, RLConstants.TITLE))
						&& (StringUtils.isEmpty(getPropertyValue(properties, RLConstants.HIDE_IN_RESOURCE_LIBRARY))
								|| "false".equalsIgnoreCase(
										getPropertyValue(properties, RLConstants.HIDE_IN_RESOURCE_LIBRARY)))) {

					String contentTag[] = getMultiValueProperty(properties, RLConstants.CONTENT_TYPE_TAGS);
					String topicTag[] = getMultiValueProperty(properties, RLConstants.TOPIC_TAGS);
					String industryTag[] = getMultiValueProperty(properties, RLConstants.INDUSTRY_TAGS);

					contentTags = ResourceLibraryServiceUtil.updateContentTags(contentTag, resolver, contentTags,pageLocale);
					topicTags = ResourceLibraryServiceUtil.updateTopicTags(topicTag, resolver, topicTags,pageLocale);
					industryTags = ResourceLibraryServiceUtil.updateIndustryTags(industryTag, resolver, industryTags,pageLocale);
					final TagManager tagManager = resolver.adaptTo(TagManager.class);

					String popularTag = getPropertyValue(properties, RLConstants.POPULAR_TAG);
					String promotedTag = getPropertyValue(properties, RLConstants.PROMOTED_TAG);
					if (RLConstants.PROMOTED_TAG_ID.equalsIgnoreCase(promotedTag)) {
						promotedArray = getPromotedResource(promotedTag, resolver, promotedArray, resource, contentTag, request);
					} else if (RLConstants.POPULAR_TAG_ID.equalsIgnoreCase(popularTag)) {
						popularArray = getPopularResource(popularTag, resolver, popularArray, resource, contentTag, request);
					} else if (!RLConstants.POPULAR_TAG_ID.equalsIgnoreCase(popularTag)
							&& !RLConstants.PROMOTED_TAG_ID.equalsIgnoreCase(promotedTag)) {
						resourceArray = getResults(contentTag, topicTag, industryTag, resourceArray, resource,
								resolver, request);
					}
				} else {
					LOG.debug("Title value :::" + getPropertyValue(properties, RLConstants.TITLE));
					LOG.debug("Hide in resource library:::"
							+ getPropertyValue(properties, RLConstants.HIDE_IN_RESOURCE_LIBRARY));
				}
			}else {
				LOG.debug("Resource Object is NULL::");
			}
		}
		tagsObj.add(RLConstants.CONTENT_TYPE, contentTags);
		tagsObj.add(RLConstants.TOPIC, topicTags);
		tagsObj.add(RLConstants.INDUSTRY, industryTags);
		rootObj.addProperty(RLConstants.RESULTS_COUNT, resourceArray.size());
		rootObj.add(RLConstants.RESULTS, resourceArray);
		rootObj.add(RLConstants.POPULAR, popularArray);
		rootObj.addProperty(RLConstants.POPULAR_ARTICLE_COUNT, popularArray.size());
		rootObj.add(RLConstants.PROMOTED, promotedArray);
		rootObj.addProperty(RLConstants.PROMOTED_ARTICLE_COUNT, promotedArray.size());
		rootObj.add(RLConstants.TAGS, tagsObj);

		return rootObj;
	}

	private JsonArray getPromotedResource(String promotedTag, ResourceResolver resolver, JsonArray promotedArray,
			Resource resource, String[] contentTag, SlingHttpServletRequest request) {
		JsonObject promotedObj = new JsonObject();
		final TagManager tagManager = resolver.adaptTo(TagManager.class);
		Tag tag = Objects.requireNonNull(tagManager.resolve(promotedTag));
		ValueMap properties = resource.adaptTo(ValueMap.class);
		promotedObj.addProperty(RLConstants.TAG, tag.getTagID());
		promotedObj = getResourceProps(properties, promotedObj, contentTag, resolver, request);
		promotedObj = includeImageProp(resource, promotedObj);
		promotedArray.add(promotedObj);

		return promotedArray;
	}

	private JsonArray getPopularResource(String popularTag, ResourceResolver resolver, JsonArray popularArray,
			Resource resource, String[] contentTag, SlingHttpServletRequest request ) {
		JsonObject popularObj = new JsonObject();
		Tag tag = null;
		final TagManager tagManager = resolver.adaptTo(TagManager.class);
		ValueMap properties = resource.adaptTo(ValueMap.class);
		tag = Objects.requireNonNull(tagManager.resolve(popularTag));
		popularObj.addProperty(RLConstants.TAG, tag.getTagID());
		popularObj = getResourceProps(properties, popularObj, contentTag, resolver, request);
		popularObj = includeImageProp(resource, popularObj);
		popularArray.add(popularObj);
		return popularArray;
	}

	private JsonArray getResults(String[] contentTag, String[] topicTag, String[] industryTag, JsonArray resourceArray,
			Resource resource, ResourceResolver resolver, SlingHttpServletRequest request) {
		JsonObject tempObj = new JsonObject();
		ValueMap properties = resource.adaptTo(ValueMap.class);
		if (ArrayUtils.isNotEmpty(contentTag) || ArrayUtils.isNotEmpty(topicTag)
				|| ArrayUtils.isNotEmpty(industryTag)) {
			tempObj = getResourceProps(properties, tempObj, contentTag, resolver, request);
			tempObj = includeImageProp(resource, tempObj);
			tempObj.add(RLConstants.CONTENT_TAGS, tagsList(tempObj, contentTag));
			tempObj.add(RLConstants.TOPIC_TAGS, tagsList(tempObj, topicTag));
			tempObj.add(RLConstants.INDUSTRY_TAGS, tagsList(tempObj, industryTag));
			resourceArray.add(tempObj);
		}
		return resourceArray;
	}

	private JsonArray tagsList(JsonObject tempObj, String[] tagsList) {
		JsonArray tags = new JsonArray();
		if (ArrayUtils.isNotEmpty(tagsList)) {
			for (String str : tagsList) {
				tags.add(str);
			}
		}
		return tags;
	}

	private String getctaLabel(String[] contentTag) {
		// TODO Auto-generated method stub
		String ctaLabel = RLConstants.READ_ARTICLE;
		if(!ArrayUtils.isEmpty(contentTag)) {
			for (String str : contentTag) {
				if (str.contains(RLConstants.VIDEO) || str.contains(RLConstants.DEMO) || str.contains(RLConstants.WEBINAR)) {
					ctaLabel = RLConstants.WATCH_VIDEO;
				} else {
					ctaLabel = RLConstants.READ_ARTICLE;
				}
			}
		}
		return ctaLabel;
	}

	private JsonObject getResourceProps(ValueMap properties, JsonObject resourceObj, String[] contentTag,
			ResourceResolver resolver, SlingHttpServletRequest request) {
		JsonObject resourcePropObj = resourceObj;
		String url = LinkUtil.getLink(getPropertyValue(properties, RLConstants.CTA_LINK_DESTINATION));
		LOG.debug("CTA Link Destination path::" + url);
  
		Externalizer externalizer = resolver.adaptTo(Externalizer.class);
		if (externalizer != null) {
			String absoluteUrl = externalizer.absoluteLink(request, request.getScheme(), url);
			LOG.debug("absoluteUrl CTA Link Destination Url::" + absoluteUrl);											 
			/*
			 * String externalizedUrl = externalizer.publishLink(resolver, url);
			 * LOG.debug("Externalized CTA Link Destination Url::" + externalizedUrl);
			 */
			resourcePropObj.addProperty(RLConstants.CTA_LINK_DESTINATION, absoluteUrl);
		}
		
		resourcePropObj.addProperty(RLConstants.TITLE, getPropertyValue(properties, RLConstants.TITLE));
		resourcePropObj.addProperty(RLConstants.DESCRIPTION, getPropertyValue(properties, RLConstants.DESCRIPTION));
		resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.ALTTEXT));
		resourcePropObj.addProperty(RLConstants.CAPTION, getPropertyValue(properties, RLConstants.CAPTION));
		resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.ALTTEXT));
		resourcePropObj.addProperty(RLConstants.HIDE_IN_RESOURCE_LIBRARY,
				getPropertyValue(properties, RLConstants.HIDE_IN_RESOURCE_LIBRARY));
		// Lable name update
		String ctaLable = getPropertyValue(properties, RLConstants.CTA_LABEL);
		if(StringUtils.isNotEmpty(ctaLable)) {
			resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, ctaLable);
		}else {
			resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, getctaLabel(contentTag));
		}
		// resourcePropObj.addProperty(RLConstants.CTA_LINK_DESTINATION, url);
		resourcePropObj.addProperty(RLConstants.LINK_OPEN_IN, LinkUtil.linkOpenIn(url));
		resourcePropObj.addProperty(RLConstants.CQ_LAST_REPLICATED,
				getPropertyValue(properties, RLConstants.CQ_LAST_REPLICATED));
		resourcePropObj.addProperty(RLConstants.CQ_LAST_MODIFIED,
				getPropertyValue(properties, RLConstants.CQ_LAST_MODIFIED));
		return resourcePropObj;
	}

	private String getPropertyValue(ValueMap properties, String propkey) {
		// TODO Auto-generated method stub
		String propValue = null;

		if (properties.containsKey(propkey)) {
			propValue = properties.get(propkey, String.class);
		} else {
			propValue = "";
		}
		return propValue;
	}

	private String[] getMultiValueProperty(ValueMap properties, String tagProp) {
		// TODO Auto-generated method stub
		String[] tempArray = null;
		if (properties.containsKey(tagProp)) {
			tempArray = properties.get(tagProp, String[].class);
		} else {
			tempArray = null;
		}
		return tempArray;
	}

	private JsonObject includeImageProp(Resource resource, JsonObject popularObj) {
		JsonObject imageObj = popularObj;
		Iterator<Resource> resourcces = resource.listChildren();
		while (resourcces.hasNext()) {
			Resource childResource = resourcces.next();
			// LOG.info("resourcename:::"+childResource.getName()+"path:::"+childResource.getPath());
			if (RLConstants.IMAGE.equalsIgnoreCase(childResource.getName())) {
				ValueMap imageProps = childResource.adaptTo(ValueMap.class);
				imageObj.addProperty(RLConstants.IMAGE_SRC, imageProps.get(RLConstants.FILE_REFERENCE, String.class));
			}
		}
		return imageObj;
	}

}
