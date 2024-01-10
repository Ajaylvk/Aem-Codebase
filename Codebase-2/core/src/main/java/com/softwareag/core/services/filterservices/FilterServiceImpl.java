package com.softwareag.core.services.filterservices;

import com.day.cq.commons.Externalizer;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softwareag.core.constants.RLConstants;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import com.softwareag.core.servlets.component.FastTrackServlet;
import com.softwareag.core.servlets.component.FreeTrailServlet;
import com.softwareag.core.util.LinkUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component(service = FilterService.class, immediate = true)
public class FilterServiceImpl implements FilterService {
    private static final Logger LOG = LoggerFactory.getLogger(FilterServiceImpl.class);
    @Reference
    QueryBuilder builder;

    @Override
    public JsonObject getResponse(RequestObject requestObj, SlingHttpServletRequest request, String selector, List<String> dropDownOrder) {
        JsonObject rootObj = new JsonObject();
        ResourceResolver resolver = request.getResourceResolver();
        try {
            // TODO Auto-generated method stub

            Iterator<Resource> resources;
            if (resolver != null) {
                Session session = resolver.adaptTo(Session.class);
                if (RLConstants.FILTER.equalsIgnoreCase(requestObj.getRequesttype()) || RLConstants.INITIAL_RENDER.equalsIgnoreCase(requestObj.getRequesttype())) {
                    LOG.debug("Filter based search service :::::");
                    Map<String, String> predicateMap = new HashMap<>();
                    if (selector.equalsIgnoreCase(FastTrackServlet.SELECTOR)) {
                        predicateMap = FilterUtil.getPredicateGroupFastTrack(requestObj);
                        LOG.debug("predicatemap:::::" + predicateMap);
                        com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
                        SearchResult result = query.getResult();
                        resources = result.getResources();
                        if (resources.hasNext()) {
                            rootObj = getResponseJson(requestObj, resources, resolver, request, dropDownOrder, selector);
                        } else {
                            LOG.debug("FILTER : No query results found");
                            rootObj.addProperty("message", "No Search Results Found");
                        }
                    } else if (selector.equalsIgnoreCase(FreeTrailServlet.SELECTOR)) {
                        LinkedHashMap<String, String> ArrayMap;
                        Resource res = resolver.getResource("/content/cq:tags/softwareag/free-trail-categories");
                        ArrayMap = formFreeTrailArrayMap(res, "compare");
                        JsonObject FreeTrailArrayKeyvalue = new JsonObject();
                        JsonArray filter1Tags = new JsonArray();
                        JsonArray filter2Tags = new JsonArray();
                        JsonArray filter3Tags = new JsonArray();
                        JsonObject tagsObj = new JsonObject();
                        int count = 0;
                        Gson gson = new Gson();
                        String json = gson.toJson(formFreeTrailArrayMap(res, "obj"));
                        JsonParser parse = new JsonParser();
                        FreeTrailArrayKeyvalue = (JsonObject) parse.parse(json);
                        LOG.debug("Free Trail Array Keyvalue" + FreeTrailArrayKeyvalue.toString());
                        rootObj.add(RLConstants.FREE_ARRAY_KEY_VALUE, FreeTrailArrayKeyvalue);
                        for (Map.Entry<String, String> entry : ArrayMap.entrySet()) {
                            predicateMap = FilterUtil.getPredicateGroupFreeTrail(requestObj, entry.getKey());
                            LOG.debug("predicatemap:::::" + predicateMap);
                            com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
                            SearchResult result = query.getResult();
                            resources = result.getResources();
                            if (resources.hasNext()) {
                                JsonArray tempArray = new JsonArray();
                                tempArray = getResponseJsonFreeTrail(requestObj, resources, resolver, request, dropDownOrder, selector, filter1Tags, filter2Tags, filter3Tags);
                                count += tempArray.size();
                                rootObj.add(entry.getValue(), tempArray);
                            } else {
                                JsonArray tempArray = new JsonArray();
                                rootObj.add(entry.getValue(), tempArray);
                            }
                        }
                        rootObj.addProperty(RLConstants.RESULTS_COUNT, count);
                        tagsObj.add(dropDownOrder.get(0), filter1Tags);
                        tagsObj.add(dropDownOrder.get(1), filter2Tags);
                        tagsObj.add(dropDownOrder.get(2), filter3Tags);
                        rootObj.add(RLConstants.TAGS, tagsObj);
                    }
                } else if (RLConstants.RESOURCE_SEARCH.equalsIgnoreCase(requestObj.getRequesttype())) {
                    if (selector.equalsIgnoreCase(FastTrackServlet.SELECTOR)) {
                        LOG.debug("full text search service:::::");
                        Map<String, String> predicateMap = FilterUtil.getSearchPredicateGroup(requestObj);
                        LOG.debug("full text search predicatemap:::::" + predicateMap);
                        com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
                        SearchResult result = query.getResult();
                        resources = result.getResources();
                        if (resources.hasNext()) {
                            rootObj = getResponseJson(requestObj, resources, resolver, request, dropDownOrder, selector);
                        } else {
                            LOG.debug("RESOURCE_SEARCH : No query results found");
                            rootObj.addProperty("message", "No Search Results Found");
                        }
                    } else if (selector.equalsIgnoreCase(FreeTrailServlet.SELECTOR)) {
                        LinkedHashMap<String, String> ArrayMap;
                        Resource res = resolver.getResource("/content/cq:tags/softwareag/free-trail-categories");
                        ArrayMap = formFreeTrailArrayMap(res, "compare");
                        JsonObject FreeTrailArrayKeyvalue = new JsonObject();
                        JsonArray filter1Tags = new JsonArray();
                        JsonArray filter2Tags = new JsonArray();
                        JsonArray filter3Tags = new JsonArray();
                        JsonObject tagsObj = new JsonObject();
                        int count = 0;
                        Gson gson = new Gson();
                        String json = gson.toJson(formFreeTrailArrayMap(res, "obj"));
                        JsonParser parse = new JsonParser();
                        FreeTrailArrayKeyvalue = (JsonObject) parse.parse(json);
                        LOG.debug("Free Trail Array Keyvalue" + FreeTrailArrayKeyvalue.toString());
                        rootObj.add(RLConstants.FREE_ARRAY_KEY_VALUE, FreeTrailArrayKeyvalue);
                        for (Map.Entry<String, String> entry : ArrayMap.entrySet()) {
                            Map<String, String> predicateMap = FilterUtil.getSearchPredicateGroupFreeTrail(requestObj, entry.getKey());
                            LOG.debug("predicatemap:::::" + predicateMap);
                            com.day.cq.search.Query query = builder.createQuery(PredicateGroup.create(predicateMap), session);
                            SearchResult result = query.getResult();
                            resources = result.getResources();
                            if (resources.hasNext()) {
                                JsonArray tempArray = new JsonArray();
                                tempArray = getResponseJsonFreeTrail(requestObj, resources, resolver, request, dropDownOrder, selector, filter1Tags, filter2Tags, filter3Tags);
                                count += tempArray.size();
                                rootObj.add(entry.getValue(), tempArray);
                            } else {
                                JsonArray tempArray = new JsonArray();
                                rootObj.add(entry.getValue(), tempArray);
                            }
                        }
                        rootObj.addProperty(RLConstants.RESULTS_COUNT, count);
                        tagsObj.add(dropDownOrder.get(0), filter1Tags);
                        tagsObj.add(dropDownOrder.get(1), filter2Tags);
                        tagsObj.add(dropDownOrder.get(2), filter3Tags);
                        rootObj.add(RLConstants.TAGS, tagsObj);
                }
                } else {
                    LOG.debug("reqeust type is empty :::" + requestObj.getRequesttype());
                }
            }
            resolver.commit();
        } catch (Exception e) {
            LOG.debug("Error fetching search results:::" + e.getStackTrace());
            rootObj.addProperty("statuscode", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            rootObj.addProperty("message", RLConstants.ERROR_MSG);
            LOG.error("exception ", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
        return rootObj;
    }

    private JsonArray getResponseJsonFreeTrail(RequestObject requestObj, Iterator<Resource> resources, ResourceResolver resolver, SlingHttpServletRequest request, List<String> dropDownOrder, String selector,JsonArray filter1Tags,JsonArray filter2Tags,JsonArray filter3Tags) {
        JsonObject tagsObj = new JsonObject();

        JsonArray tempArray = new JsonArray();
        while (resources.hasNext()) {
            Resource resource = null;
            if (RLConstants.FILTER.equalsIgnoreCase(requestObj.getRequesttype())
                    || RLConstants.RESOURCE_SEARCH.equalsIgnoreCase(requestObj.getRequesttype()) || RLConstants.INITIAL_RENDER.equalsIgnoreCase(requestObj.getRequesttype())) {
                resource = resources.next().getChild(RLConstants.JCR_CONTENT);
            } else {
                resource = resources.next();
            }
            if (resource != null) {
                LOG.debug("Resource Name::::" + resource.getName());
                LOG.debug("Resource Path::::" + resource.getPath());
                ValueMap properties = resource.adaptTo(ValueMap.class);
                if (StringUtils.isNotEmpty(getPropertyValue(properties, RLConstants.FT_CAPTION))
                        && StringUtils.isEmpty(getPropertyValue(properties, RLConstants.HIDE_IN_FREE_TRAIL))
                        || "false".equalsIgnoreCase(
                        getPropertyValue(properties, RLConstants.HIDE_IN_FREE_TRAIL))) {

                    String filter1Tag[] = getMultiValueProperty(properties, RLConstants.FREE_TRAIL_TOPIC_PROP.split("/")[1]);
                    String filter2Tag[] = getMultiValueProperty(properties, RLConstants.FREE_TRAIL_PRODUCT_PROP.split("/")[1]);
                    String filter3Tag[] = getMultiValueProperty(properties, RLConstants.FREE_TRAIL_DEPLOYMENT_PROP.split("/")[1]);

                    filter1Tags = FilterUtil.updateFilter1Tags(filter1Tag, resolver, filter1Tags);
                    filter2Tags = FilterUtil.updateFilter2Tags(filter2Tag, resolver, filter2Tags);
                    filter3Tags = FilterUtil.updateFilter3Tags(filter3Tag, resolver, filter3Tags);

                    tempArray = getResults(filter1Tag, filter2Tag, filter3Tag, tempArray, resource,
                            resolver, request, dropDownOrder, selector);
                } else {
                    LOG.debug("Hide in resource library:::"
                            + getPropertyValue(properties, RLConstants.HIDE_IN_FREE_TRAIL));
                }
            } else {
                LOG.debug("Resource Object is NULL::");
            }
        }
        return tempArray;
    }

    private JsonObject getResponseJson(RequestObject requestObj, Iterator<Resource> resources,
                                       ResourceResolver resolver, SlingHttpServletRequest request, List<String> dropDownOrder, String selector) {
        JsonObject rootObj = new JsonObject();
        JsonObject tagsObj = new JsonObject();
        JsonArray filter1Tags = new JsonArray();
        JsonArray filter2Tags = new JsonArray();
        JsonArray filter3Tags = new JsonArray();

        JsonArray fastTrackArray = new JsonArray();

        while (resources.hasNext()) {
            Resource resource = null;
            if (RLConstants.FILTER.equalsIgnoreCase(requestObj.getRequesttype())
                    || RLConstants.RESOURCE_SEARCH.equalsIgnoreCase(requestObj.getRequesttype()) || RLConstants.INITIAL_RENDER.equalsIgnoreCase(requestObj.getRequesttype())) {
                resource = resources.next().getChild(RLConstants.JCR_CONTENT);
            } else {
                resource = resources.next();
            }
            if (resource != null) {
                LOG.debug("Resource Name::::" + resource.getName());
                LOG.debug("Resource Path::::" + resource.getPath());
                ValueMap properties = resource.adaptTo(ValueMap.class);
                if (StringUtils.isNotEmpty(getPropertyValue(properties, RLConstants.FS_TITLE))
                        && (StringUtils.isEmpty(getPropertyValue(properties, RLConstants.HIDE_IN_FASTTRACK_SERVICES))
                        || "false".equalsIgnoreCase(
                        getPropertyValue(properties, RLConstants.HIDE_IN_FASTTRACK_SERVICES)))) {

                    String filter1Tag[] = getMultiValueProperty(properties, RLConstants.SERVICE_TYPE_TAGS_PROPS.split("/")[1]);
                    String filter2Tag[] = getMultiValueProperty(properties, RLConstants.PRODUCT_TAGS_PROPS.split("/")[1]);
                    String filter3Tag[] = getMultiValueProperty(properties, RLConstants.CREDIT_RANGE_TAGS_PROPS.split("/")[1]);

                    filter1Tags = FilterUtil.updateFilter1Tags(filter1Tag, resolver, filter1Tags);
                    filter2Tags = FilterUtil.updateFilter2Tags(filter2Tag, resolver, filter2Tags);
                    filter3Tags = FilterUtil.updateFilter3Tags(filter3Tag, resolver, filter3Tags);

                    fastTrackArray = getResults(filter1Tag, filter2Tag, filter3Tag, fastTrackArray, resource,
                            resolver, request, dropDownOrder, selector);
                } else {
                    LOG.debug("Title value :::" + getPropertyValue(properties, RLConstants.FS_TITLE));
                    LOG.debug("Hide in resource library:::"
                            + getPropertyValue(properties, RLConstants.HIDE_IN_FASTTRACK_SERVICES));
                }
            } else {
                LOG.debug("Resource Object is NULL::");
            }
        }
        tagsObj.add(dropDownOrder.get(0), filter1Tags);
        tagsObj.add(dropDownOrder.get(1), filter2Tags);
       // tagsObj.add(dropDownOrder.get(2), filter3Tags);
        rootObj.addProperty(RLConstants.RESULTS_COUNT, fastTrackArray.size());
        rootObj.add(RLConstants.RESULTS, fastTrackArray);
        rootObj.add(RLConstants.TAGS, tagsObj);
        return rootObj;
    }


    private LinkedHashMap<String,String> formFreeTrailArrayMap(Resource res, String compare){
        LinkedHashMap<String,String> ArrayMap=new LinkedHashMap<>();
        Node resnode=res.adaptTo(Node.class);
        try {
            NodeIterator children = resnode.getNodes();
            while (children.hasNext()) {
                Node child = children.nextNode();
                if(compare.equals("compare"))
                    ArrayMap.put(child.getPath().split("cq:tags/")[1].replaceFirst("/",":"),(child.getPath().split("free-trail-categories")[1].replaceAll("[^a-zA-Z0-9]","").replace(" ","")));
                if(compare.equals("obj"))
                    ArrayMap.put((child.getPath().split("free-trail-categories")[1].replaceAll("[^a-zA-Z0-9]","").replace(" ","")),child.getProperty("jcr:title").getString());
            }
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        return ArrayMap;
    }

    private String getPropertyValue(ValueMap properties, String propKey) {
        // TODO Auto-generated method stub
        String propValue;

        if (properties.containsKey(propKey)) {
            propValue = properties.get(propKey, String.class);
        } else {
            propValue = "";
        }
        return propValue;
    }
    private String[] getMultiValueProperty(ValueMap properties, String tagProp) {
        // TODO Auto-generated method stub
        String[] tempArray;
        if (properties.containsKey(tagProp)) {
            tempArray = properties.get(tagProp, String[].class);
        } else {
            tempArray = null;
        }
        return tempArray;
    }

    private JsonArray getResults(String[] filter1Tag, String[] filter2Tag, String[] filter3Tag, JsonArray freeTrailArray,
                                 Resource resource, ResourceResolver resolver, SlingHttpServletRequest request, List<String> dropDownOrder, String selector) {
        JsonObject tempObj = new JsonObject();
        ValueMap properties = resource.adaptTo(ValueMap.class);
        if (ArrayUtils.isNotEmpty(filter1Tag) || ArrayUtils.isNotEmpty(filter2Tag)
                || ArrayUtils.isNotEmpty(filter3Tag)) {
            tempObj = getResourceProps(properties, tempObj, resolver, request,selector);
            tempObj.add(dropDownOrder.get(0), tagsList(tempObj, filter1Tag));
            tempObj.add(dropDownOrder.get(1), tagsList(tempObj, filter2Tag));
            tempObj.add(dropDownOrder.get(2), tagsList(tempObj, filter3Tag));
            freeTrailArray.add(tempObj);
        }
        return freeTrailArray;
    }
    private JsonObject getResourceProps(ValueMap properties, JsonObject resourceObj,
                                        ResourceResolver resolver, SlingHttpServletRequest request, String selector) {
        JsonObject resourcePropObj = resourceObj;
        if(selector.equalsIgnoreCase(FreeTrailServlet.SELECTOR)) {
            String url = LinkUtil.getLink(getPropertyValue(properties, RLConstants.FT_CTA_LINK_DESTINATION));
            LOG.debug("CTA Link Destination path::" + url);

            Externalizer externalizer = resolver.adaptTo(Externalizer.class);
            if (externalizer != null) {
                String absoluteUrl = externalizer.absoluteLink(request, request.getScheme(), url);
                LOG.debug("absoluteUrl CTA Link Destination Url::" + absoluteUrl);
                resourcePropObj.addProperty(RLConstants.CTA_LINK_DESTINATION, absoluteUrl);
            }
            resourcePropObj.addProperty(RLConstants.DESCRIPTION, getPropertyValue(properties, RLConstants.FT_DESCRIPTION));
            resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.FT_ALTTEXT));
            resourcePropObj.addProperty(RLConstants.CAPTION, getPropertyValue(properties, RLConstants.FT_CAPTION));
            resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.FT_ALTTEXT));
            resourcePropObj.addProperty(RLConstants.HIDE_IN_FREE_TRAIL, getPropertyValue(properties, RLConstants.HIDE_IN_FREE_TRAIL));
            resourcePropObj.addProperty(RLConstants.IMAGE_SRC, getPropertyValue(properties, RLConstants.FREE_TRAIL_LOGO));
            resourcePropObj.addProperty(RLConstants.FT_BUTTON_TEXT, getPropertyValue(properties, RLConstants.FT_BUTTON_TEXT));
            resourcePropObj.addProperty(RLConstants.FT_BUTTON_ICON, getPropertyValue(properties, RLConstants.FT_BUTTON_ICON));
            resourcePropObj.addProperty(RLConstants.FT_BUTTON_COLOR, getPropertyValue(properties, RLConstants.FT_BUTTON_COLOR));
            resourcePropObj.addProperty(RLConstants.FT_BUTTON_LINK, getPropertyValue(properties, RLConstants.FT_BUTTON_LINK));
            resourcePropObj.addProperty(RLConstants.FT_BUTTON_OPEN_IN, LinkUtil.linkOpenIn(LinkUtil.getLink(getPropertyValue(properties, RLConstants.FT_BUTTON_LINK))));
            String ctaLable = getPropertyValue(properties, RLConstants.FT_CTA_LABEL);
            if (StringUtils.isNotEmpty(ctaLable)) {
                resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, ctaLable);
            } else {
                resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, RLConstants.LEARN_MORE);
            }
            resourcePropObj.addProperty(RLConstants.LINK_OPEN_IN, LinkUtil.linkOpenIn(url));
            resourcePropObj.addProperty(RLConstants.CQ_LAST_REPLICATED,
                    getPropertyValue(properties, RLConstants.CQ_LAST_REPLICATED));
            resourcePropObj.addProperty(RLConstants.CQ_LAST_MODIFIED,
                    getPropertyValue(properties, RLConstants.CQ_LAST_MODIFIED));
        } else if (selector.equalsIgnoreCase(FastTrackServlet.SELECTOR)) {
            String url = LinkUtil.getLink(getPropertyValue(properties, RLConstants.FS_CTA_LINK_DESTINATION));
            LOG.debug("CTA Link Destination path::" + url);

            Externalizer externalizer = resolver.adaptTo(Externalizer.class);
            if (externalizer != null) {
                String absoluteUrl = externalizer.absoluteLink(request, request.getScheme(), url);
                LOG.debug("absoluteUrl CTA Link Destination Url::" + absoluteUrl);
                resourcePropObj.addProperty(RLConstants.CTA_LINK_DESTINATION, absoluteUrl);
            }

            resourcePropObj.addProperty(RLConstants.TITLE, getPropertyValue(properties, RLConstants.FS_TITLE));
            resourcePropObj.addProperty(RLConstants.DESCRIPTION, getPropertyValue(properties, RLConstants.FS_DESCRIPTION));
            resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.FS_ALTTEXT));
            resourcePropObj.addProperty(RLConstants.CAPTION, getPropertyValue(properties, RLConstants.FS_CAPTION));
            resourcePropObj.addProperty(RLConstants.ALTTEXT, getPropertyValue(properties, RLConstants.FS_ALTTEXT));
            resourcePropObj.addProperty(RLConstants.HIDE_IN_FASTTRACK_SERVICES, getPropertyValue(properties, RLConstants.HIDE_IN_FASTTRACK_SERVICES));
            if (!(getPropertyValue(properties, RLConstants.CREDIT_RANGE_TAGS_PROPS.split("/")[1]).isEmpty())){
                resourcePropObj.addProperty(RLConstants.CREDITS, getPropertyValue(properties, RLConstants.CREDIT_RANGE_TAGS_PROPS.split("/")[1]).split("/")[1]);
            }
            resourcePropObj.addProperty(RLConstants.IMAGE_SRC, getPropertyValue(properties, RLConstants.FS_ICON));
            resourcePropObj.addProperty(RLConstants.FS_ICON_COLOR, getPropertyValue(properties, RLConstants.FS_ICON_COLOR));
            String ctaLable = getPropertyValue(properties, RLConstants.FS_CTA_LABEL);
            if(StringUtils.isNotEmpty(ctaLable)) {
                resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, ctaLable);
            }else {
                resourcePropObj.addProperty(RLConstants.CTA_LINK_LABEL, RLConstants.LEARN_MORE);
            }
            resourcePropObj.addProperty(RLConstants.LINK_OPEN_IN, LinkUtil.linkOpenIn(url));
            resourcePropObj.addProperty(RLConstants.CQ_LAST_REPLICATED,
                    getPropertyValue(properties, RLConstants.CQ_LAST_REPLICATED));
            resourcePropObj.addProperty(RLConstants.CQ_LAST_MODIFIED,
                    getPropertyValue(properties, RLConstants.CQ_LAST_MODIFIED));
        }
        return resourcePropObj;

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

}

