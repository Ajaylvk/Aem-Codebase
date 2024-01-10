package com.softwareag.core.models.freetrail;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Component;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.softwareag.core.models.fasttrackservices.FastTrackServicesModelImpl;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

@Data
@Model(adaptables = { SlingHttpServletRequest.class },
        adapters = { FreeTrailModel.class, ComponentExporter.class },
        resourceType = { FreeTrailModelImpl.RESOURCE_TYPE },
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class FreeTrailModelImpl implements FreeTrailModel{

    public static final String RESOURCE_TYPE = "softwareag/components/content/freetrialfilter";


    @Self
    @Delegate(types = Component.class)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Component component;

    @Inject
    ResourceResolver resolver;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String[] filter1tags;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String[] filter2tags;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String[] filter3tags;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String filter1label;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String filter2label;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String filter3label;

    @Setter(AccessLevel.NONE)
    @ValueMapValue
    private String sourcePath;

    @Setter @Getter
    private Map<String, String> filter1TagMap = new HashMap<>();

    @Setter @Getter
    private Map<String, String> filter2TagMap = new HashMap<>();

    @Setter @Getter
    private Map<String, String> filter3TagMap = new HashMap<>();
    @Setter @Getter
    private String filter1ID;
    @Setter @Getter
    private String filter2ID;
    @Setter @Getter
    private String filter3ID;

    @PostConstruct
    private void init() {
        final TagManager tagManager = resolver.adaptTo(TagManager.class);

        if(ArrayUtils.isNotEmpty(filter1tags)) {
            for(String str: filter1tags) {
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                filter1TagMap.put(tag.getTitle(), tag.getTagID());
            }
            filter1TagMap = sortByKey(filter1TagMap);
        }
        if(ArrayUtils.isNotEmpty(filter2tags)) {
            for(String str: filter2tags) {
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                filter2TagMap.put(tag.getTitle(), tag.getTagID());
            }
            filter2TagMap = sortByKey(filter2TagMap);
        }
        if(ArrayUtils.isNotEmpty(filter3tags)) {
            for(String str: filter3tags) {
                Tag tag = Objects.requireNonNull(tagManager.resolve(str));
                filter3TagMap.put(tag.getTitle(), tag.getTagID());
            }
            filter3TagMap = sortByKey(filter3TagMap);
        }
        filter1ID=getFilterID(filter1label);
        filter2ID=getFilterID(filter2label);
        filter3ID=getFilterID(filter3label);
    }

    public  Map<String, String> sortByKey(Map<String, String> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, String> > list = new LinkedList<Map.Entry<String, String> >(hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(list, (i1, i2) -> i1.getKey().compareTo(i2.getKey()));

        // put data from sorted list to hashmap
        HashMap<String, String> temp
                = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    public String getFilterID(String filter){
        if(filter!=null&&filter!=""){
            return filter.toLowerCase().replace(" ","-");
        }
        return "";
    }

}
