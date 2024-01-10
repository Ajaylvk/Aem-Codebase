package com.softwareag.core.services.filterservices;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwareag.core.constants.RLConstants;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import com.softwareag.core.resourcelibrary.utils.TagsBean;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FilterUtil {
    public static final String CQ_LAST_MODIFIED = "@jcr:content/cq:lastModified";
    private static final Logger LOG = LoggerFactory.getLogger(FilterUtil.class);

    public static Map<String, String> getPredicateGroupFastTrack(RequestObject requestObject) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(RLConstants.PATH, requestObject.getAssetpath());
        map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
        map.put(RLConstants.ONE_PROPERTY, RLConstants.SERVICE_TYPE_TAGS_PROPS);
        map.put(RLConstants.TWO_PROPERTY, RLConstants.PRODUCT_TAGS_PROPS);
       // map.put(RLConstants.THREE_PROPERTY, RLConstants.CREDIT_RANGE_TAGS_PROPS);
        //dynamic tags
        List<TagsBean> tagsBean = requestObject.getValues();
        int i = 1;
        if(tagsBean.size() > 0) {
            for(TagsBean bean: tagsBean) {
                LOG.debug("Tag value::"+bean.getValue());
                if(bean.getValue().contains(RLConstants.SERVICE_TYPE)) {
                    map.put(RLConstants.ONE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }else if(bean.getValue().contains(RLConstants.TOPIC)) {
                    map.put(RLConstants.TWO_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }/*else if(bean.getValue().contains(RLConstants.CREDIT_RANGE)) {
                    map.put(RLConstants.THREE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }*/
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
    public static Map<String, String> getPredicateGroupFreeTrail(RequestObject requestObject, String key) {

        Map<String, String> map = new HashMap<String, String>();
        map.put(RLConstants.PATH, requestObject.getAssetpath());
        map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
        map.put(RLConstants.ONE_PROPERTY, RLConstants.FREE_TRAIL_TOPIC_PROP);
        map.put(RLConstants.TWO_PROPERTY, RLConstants.FREE_TRAIL_PRODUCT_PROP);
        map.put(RLConstants.THREE_PROPERTY, RLConstants.FREE_TRAIL_DEPLOYMENT_PROP);
        map.put(RLConstants.FOUR_PROPERTY, RLConstants.FREE_TRAIL_CATEGORY_PROP);
        //dynamic tags
        List<TagsBean> tagsBean = requestObject.getValues();
        int i = 1;
        if(tagsBean.size() > 0) {
            for(TagsBean bean: tagsBean) {
                LOG.debug("Tag value::"+bean.getValue());
                if(bean.getValue().contains(RLConstants.TOPIC)) {
                    map.put(RLConstants.ONE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }else if(bean.getValue().contains(RLConstants.PRODUCT)) {
                    map.put(RLConstants.TWO_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }else if(bean.getValue().contains(RLConstants.FREE_TRAIL_DEPLOYMENT)) {
                    map.put(RLConstants.THREE_PROPERTY_DOT+i+RLConstants.VALUE,bean.getValue());
                }
                i++;
            }
        }else {
            LOG.debug("No tags available:::");
            map.put("property.value","");
        }
        map.put(RLConstants.FOUR_PROPERTY+".1"+RLConstants.VALUE,key);
        map.put(RLConstants.ORDER_BY,CQ_LAST_MODIFIED);
        map.put(RLConstants.ORDER_BY_SORT,RLConstants.DESC);
        map.put(RLConstants.P_DOT_LIMIT, RLConstants.MINUS_ONE);

        return map;
    }

    public static JsonArray updateFilter1Tags(String[] Filter1Tag, ResourceResolver resolver, JsonArray filter1Tags) {
        final TagManager tagManager = resolver.adaptTo(TagManager.class);
        if(ArrayUtils.isNotEmpty(Filter1Tag)) {
            for (String str : Filter1Tag) {
                LOG.debug("Content type Tag ::::"+str);
                JsonObject tagObj = new JsonObject();
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                tagObj.addProperty(tag.getTitle(), tag.getTagID());
                LOG.debug("Content type Tag Name:::"+tag.getName());
                if (!filter1Tags.contains(tagObj)) {
                    filter1Tags.add(tagObj);
                }
            }
        }
        return filter1Tags;
    }


    public static JsonArray updateFilter2Tags(String[] Filter2Tag, ResourceResolver resolver,JsonArray filter2Tags) {
        final TagManager tagManager = resolver.adaptTo(TagManager.class);
        if(ArrayUtils.isNotEmpty(Filter2Tag)) {
            for(String str: Filter2Tag) {
                LOG.debug("Industry Tag ::::"+str);
                JsonObject tagObj = new JsonObject();
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                tagObj.addProperty(tag.getTitle(), tag.getTagID());
                LOG.debug("Industry Tag Name:::"+tag.getName());
                if(!filter2Tags.contains(tagObj)) {
                    filter2Tags.add(tagObj);
                }
            }
        }
        return filter2Tags;
    }

    public static JsonArray updateFilter3Tags(String[] Filter3Tag, ResourceResolver resolver, JsonArray filter3Tags) {
        final TagManager tagManager = resolver.adaptTo(TagManager.class);
        if(ArrayUtils.isNotEmpty(Filter3Tag)) {
            for (String str : Filter3Tag) {
                LOG.debug("Topic Tag ::::"+str);
                JsonObject tagObj = new JsonObject();
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                tagObj.addProperty(tag.getTitle(), tag.getTagID());
                LOG.debug("Topic Tag Name:::"+tag.getName());
                if ( !filter3Tags.contains(tagObj)) {
                    filter3Tags.add(tagObj);
                }
            }
        }
        return filter3Tags;
    }
    public static Map<String, String> getSearchPredicateGroup(RequestObject requestObj) {
        // TODO Auto-generated method stub

        Map<String, String> map = new HashMap<String, String>();
        map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
        map.put(RLConstants.FULLTEXT, requestObj.getSearchterm());
        map.put(RLConstants.GROUP_PATH,requestObj.getAssetpath());
        map.put(RLConstants.GROUP2_PROP, RLConstants.FS_SEARCH_PROP);
        map.put(RLConstants.GROUP2_PROP_VALUE, requestObj.getSearchterm());
        map.put(RLConstants.GROUP2_PROP_OPERATION, RLConstants.LIKE);
        map.put(RLConstants.GROUP_P_OR, RLConstants.TRUE);
        map.put(RLConstants.ORDER_BY,CQ_LAST_MODIFIED);
        map.put(RLConstants.ORDER_BY_SORT,RLConstants.DESC);
        map.put(RLConstants.P_DOT_LIMIT, RLConstants.MINUS_ONE);

        return map;
    }
    public static Map<String, String> getSearchPredicateGroupFreeTrail(RequestObject requestObj,String key) {
        // TODO Auto-generated method stub

        Map<String, String> map = new HashMap<String, String>();
        map.put(RLConstants.TYPE, RLConstants.CQ_PAGE);
        map.put(RLConstants.ONE_PROPERTY, RLConstants.FREE_TRAIL_CATEGORY_PROP);
        map.put(RLConstants.ONE_PROPERTY+".1"+RLConstants.VALUE,key);
        map.put(RLConstants.FULLTEXT, requestObj.getSearchterm());
        map.put(RLConstants.GROUP_PATH,requestObj.getAssetpath());
        map.put(RLConstants.GROUP2_PROP, RLConstants.FS_SEARCH_PROP);
        map.put(RLConstants.GROUP2_PROP_VALUE, requestObj.getSearchterm());
        map.put(RLConstants.GROUP2_PROP_OPERATION, RLConstants.LIKE);
        map.put(RLConstants.GROUP_P_OR, RLConstants.TRUE);
        map.put(RLConstants.ORDER_BY,CQ_LAST_MODIFIED);
        map.put(RLConstants.ORDER_BY_SORT,RLConstants.DESC);
        map.put(RLConstants.P_DOT_LIMIT, RLConstants.MINUS_ONE);

        return map;
    }
}
