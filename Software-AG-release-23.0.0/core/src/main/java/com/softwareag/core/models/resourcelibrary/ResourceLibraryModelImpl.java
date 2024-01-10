package com.softwareag.core.models.resourcelibrary;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.softwareag.core.services.language.LanguageService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Component;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@Data
@Model(adaptables = { Resource.class,SlingHttpServletRequest.class },
		adapters = { ResourceLibraryModel.class, ComponentExporter.class },
		resourceType = { ResourceLibraryModelImpl.RESOURCE_TYPE },
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
		extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ResourceLibraryModelImpl implements ResourceLibraryModel{

	public static final String RESOURCE_TYPE = "softwareag/components/content/resourcefilter-v1"; 
	private static final Logger LOG = LoggerFactory.getLogger(ResourceLibraryModelImpl.class);
	
	@Self
	@Delegate(types = Component.class)
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private Component component;

	@OSGiService
	private LanguageService languageService;

	@Inject
	private Resource resource;

	private Locale pageLocale;

	@Inject
	ResourceResolver resolver;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String[] topictags;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String[] contenttypetags;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String[] industrytags;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String resourcePath;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String promotedTag;
	
	@Setter(AccessLevel.NONE)
	@ValueMapValue
	private String popularTag;
	
	@Setter @Getter
	private Map<String, String> topicTagMap = new HashMap<>();
	
	@Setter @Getter
	private Map<String, String> contentTypeTagMap = new HashMap<>();
	
	@Setter @Getter
	private Map<String, String> industryTagMap = new HashMap<>();
	
	@PostConstruct
	private void init() {
		pageLocale = languageService.getLocale(resource);
		LOG.info("Locale:"+pageLocale);
		 final TagManager tagManager = resolver.adaptTo(TagManager.class);
		if(ArrayUtils.isNotEmpty(topictags)) {
			for(String str: topictags) {
				Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				topicTagMap.put(tag.getTitle(pageLocale), tag.getTagID());
			}
			topicTagMap = sortByKey(topicTagMap);
		}
		if(ArrayUtils.isNotEmpty(contenttypetags)) {
			for(String str: contenttypetags) {
				Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				contentTypeTagMap.put(tag.getTitle(pageLocale), tag.getTagID());
			}
			contentTypeTagMap = sortByKey(contentTypeTagMap);
		}
		if(ArrayUtils.isNotEmpty(industrytags)) {
			for(String str: industrytags) {
				Tag tag = Objects.requireNonNull(tagManager.resolve(str));
				industryTagMap.put(tag.getTitle(pageLocale), tag.getTagID());
			}
			industryTagMap = sortByKey(industryTagMap);
		}
	}
	public  Map<String, String>
    sortByKey(Map<String, String> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, String> > list
            = new LinkedList<Map.Entry<String, String> >(
                hm.entrySet());
 
        // Sort the list using lambda expression
        Collections.sort(
            list,
            (i1, i2) -> i1.getKey().compareTo(i2.getKey()));
 
        // put data from sorted list to hashmap
        HashMap<String, String> temp
            = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
}
