package com.softwareag.core.models.partner;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.language.LanguageService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.softwareag.core.constants.GenericConstants.JCR_CONTENT;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PartnerTableModel {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerTableModel.class);

    public static final String PN_RESOURCE_TYPE = "softwareag/components/content/partnertable";

    @OSGiService
    private LanguageService languageService;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String title;

    @ValueMapValue(name = "countryTags")
    private String[] countryTagsPath;

    @ValueMapValue(name = "partnerTypeTags")
    private String[] partnerTypeTagsPath;

    @ValueMapValue(name = "productCategoryTags")
    private String[] productCategoryTagsPath;

    @ValueMapValue(name = "industryTags")
    private String[] industriesTagsPath;

    @ValueMapValue
    private String anchorName;

    private Locale pageLocale;
    private Page currentPage;
    private LabelConfigModel labelConfigModel;

    private List<String> countryTags = new ArrayList<>();
    private List<String> partnerTypeTags = new ArrayList<>();
    private List<String> productCategoryTags = new ArrayList<>();
    private List<String> industryTags = new ArrayList<>();

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(resource);
        Optional.ofNullable(resourceResolver)
                .map(rr -> rr.adaptTo(PageManager.class))
                .ifPresent(pageManager -> this.currentPage = pageManager.getContainingPage(resource));

        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);
        populateTags();
        Resource currPageJcrRes = Objects.requireNonNull(resourceResolver.getResource(this.currentPage.getPath())).getChild(JCR_CONTENT);

        Set<String> allTagsSet = new HashSet<>();
        if (countryTagsPath != null) {
            allTagsSet.addAll(Arrays.asList(countryTagsPath));
        }
        if (partnerTypeTagsPath != null) {
            allTagsSet.addAll(Arrays.asList(partnerTypeTagsPath));
        }
        if (productCategoryTagsPath != null) {
            allTagsSet.addAll(Arrays.asList(productCategoryTagsPath));
        }
        if (industriesTagsPath != null) {
            allTagsSet.addAll(Arrays.asList(industriesTagsPath));
        }
        ModifiableValueMap map = Objects.requireNonNull(currPageJcrRes).adaptTo(ModifiableValueMap.class);
        try {
            if (map != null) {
                if (map.get("tags") != null) {
                    String[] existingTags = (String[]) map.get("tags");
                    allTagsSet.addAll(Arrays.asList(existingTags));
                }
                map.put("tags", allTagsSet.toArray());
                resource.getResourceResolver().commit();
            }

        } catch (PersistenceException e) {
            LOG.error("Error while processing tags for current Page", e);
        }
    }

    private void populateTags() {
        countryTags = getTags(countryTagsPath);
        partnerTypeTags = getTags(partnerTypeTagsPath);
        productCategoryTags = getTags(productCategoryTagsPath);
        industryTags = getTags(industriesTagsPath);
    }

    private List<String> getTags(String[] tagsPath) {
        List<String> tagList = new ArrayList<>();
        if (tagsPath != null) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            for (final String tagPath : Objects.requireNonNull(tagsPath)) {
                final Tag tag = Objects.requireNonNull(tagManager).resolve(tagPath);
                if(tag == null) {
                    tagList.add("-");
                    return tagList;
                }else{
                    tagList.add(tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle());
                }
            }
        }else{
            tagList.add("-");
        }
        return tagList;
    }

    public boolean hasContent() {
        if(countryTags.size() == 1 && countryTags.get(0).equals("-")){
            return false;
        }
        else {
            return true;
        }
    }

    public Locale getPageLocale() {
        return pageLocale;
    }

    public String getTitle() {
        return title;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public List<String> getCountryTags() {
        if (CollectionUtils.isEmpty(countryTags)) {
            return Collections.emptyList();
        } else {
            Collections.sort(countryTags);
            return Collections.unmodifiableList(countryTags);
        }
    }

    public List<String> getPartnerTypeTags() {
        if (CollectionUtils.isEmpty(partnerTypeTags)) {
            return Collections.emptyList();
        } else {
            Collections.sort(partnerTypeTags);
            return Collections.unmodifiableList(partnerTypeTags);
        }
    }

    public List<String> getProductCategoryTags() {
        if (CollectionUtils.isEmpty(productCategoryTags)) {
            return Collections.emptyList();
        } else {
            Collections.sort(productCategoryTags);
            return Collections.unmodifiableList(productCategoryTags);
        }
    }

    public List<String> getIndustryTags() {
        if (CollectionUtils.isEmpty(industryTags)) {
            return Collections.emptyList();
        } else {
            Collections.sort(industryTags);
            return Collections.unmodifiableList(industryTags);
        }
    }

    public String getLabelCountries() {
        return labelConfigModel != null ? labelConfigModel.getLabelCountries() : "";
    }

    public String getLabelPartnerType() {
        return labelConfigModel != null ? labelConfigModel.getLabelPartnerType() : "";
    }

    public String getLabelProductCategory() {
        return labelConfigModel != null ? labelConfigModel.getLabelProductCategory() : "";
    }

    public String getLabelIndustries() {
        return labelConfigModel != null ? labelConfigModel.getLabelIndustries() : "";
    }
}
