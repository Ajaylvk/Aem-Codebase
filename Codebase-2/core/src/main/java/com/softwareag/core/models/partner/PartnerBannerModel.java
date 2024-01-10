package com.softwareag.core.models.partner;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.ReferenceConstants;
import com.softwareag.core.constants.TagConstants;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.language.LanguageService;
import org.apache.sling.api.resource.*;
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
public class PartnerBannerModel {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerBannerModel.class);

    public static final String PN_RESOURCE_TYPE = "softwareag/components/content/partnerbanner";

    @OSGiService
    private LanguageService languageService;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String partnerLevelTag;

    @ValueMapValue
    private String partnerLevelTagName;

    @ValueMapValue
    private String globalPartner;

    private LabelConfigModel labelConfigModel;
    private Page currentPage;
    private List<String> industriesTag;
    private Locale pageLocale;

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(resource);
        industriesTag = new ArrayList<>();
        Optional.ofNullable(resourceResolver)
                .map(rr -> rr.adaptTo(PageManager.class))
                .ifPresent(pageManager -> this.currentPage = pageManager.getContainingPage(resource));

        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);

        if (partnerLevelTag != null) {
            try {
                Resource currPageJcrRes = Objects.requireNonNull(resourceResolver).getResource(this.currentPage.getPath()).getChild(JCR_CONTENT);
                Set<String> allTagsSet = new HashSet<>();
                allTagsSet.add(partnerLevelTag);

                if ("true".equals(globalPartner)) {
                    allTagsSet.add(TagConstants.TAG_PARTNER_TYPE_GLOBAL);
                }
                ModifiableValueMap map = Objects.requireNonNull(currPageJcrRes).adaptTo(ModifiableValueMap.class);
                setIndustriesTag(currPageJcrRes);

                if (map != null) {
                    map.put("tags", allTagsSet.toArray());
                }

                updatePartnerLevelName();
                resource.getResourceResolver().commit();
            } catch (PersistenceException e) {
                LOG.error("Error while processing tags for current Page!", e);
            }
        }

    }

    private void updatePartnerLevelName() {
        if (partnerLevelTag.equalsIgnoreCase(TagConstants.TAG_SELECTED_PARTNER)) {
            ModifiableValueMap bannerResMap = resource.adaptTo(ModifiableValueMap.class);
            if (bannerResMap != null) {
                bannerResMap.remove(ReferenceConstants.PROP_PARTNER_LEVEL_TAG_NAME);
            }
        } else {
            final TagManager tagManager = Objects.requireNonNull(resourceResolver).adaptTo(TagManager.class);
            final Tag tag = Objects.requireNonNull(tagManager).resolve(partnerLevelTag);
            partnerLevelTagName = tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle();
            ModifiableValueMap bannerResMap = resource.adaptTo(ModifiableValueMap.class);
            if (bannerResMap != null) {
                bannerResMap.put(ReferenceConstants.PROP_PARTNER_LEVEL_TAG_NAME, partnerLevelTagName);
            }
        }
    }

    public String getPartnerLevelTag() {
        return partnerLevelTag;
    }

    private void setIndustriesTag(Resource currPageJcrRes) {
        ValueMap map = Objects.requireNonNull(currPageJcrRes.getChild("partnertable")).getValueMap();
        if (map.containsKey(TagConstants.TAG_NAME_INDUSTRY_TAGS)) {
            industriesTag = Arrays.asList(map.get(TagConstants.TAG_NAME_INDUSTRY_TAGS, String[].class));
        }
    }

    /**
     * Helping method to get the industry tags as per business logic.
     *
     * @return industry display string
     */
    public String getIndustries() {
        if (!industriesTag.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            if (labelConfigModel != null) {
                stringBuilder.append(labelConfigModel.getLabelIndustry()).append(": ");
            } else {
                stringBuilder.append("Industry: ");
            }

            if (industriesTag.size() <= 3) {
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                for (String value : industriesTag) {
                    stringBuilder.append(Objects.requireNonNull(tagManager).resolve(value).getTitle());
                    stringBuilder.append(", ");
                }
                return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
            }
            else {
                stringBuilder.append("Cross Industry");
                return stringBuilder.toString();
            }
        }
        return null;
    }

    public String getGlobalTagTitle() {
        final TagManager tagManager = Objects.requireNonNull(resourceResolver).adaptTo(TagManager.class);
        final Tag tag = Objects.requireNonNull(tagManager).resolve(TagConstants.TAG_PARTNER_TYPE_GLOBAL);
        return tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle();
    }
}
