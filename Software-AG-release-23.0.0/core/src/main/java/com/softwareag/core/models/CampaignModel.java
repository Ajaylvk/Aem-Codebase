package com.softwareag.core.models;

import com.day.cq.tagging.TagManager;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.util.AttributeUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;
import java.util.Optional;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CampaignModel {

    private static final String DEFAULT_TITLE_PN = "title";

    @OSGiService
    private LanguageService languageService;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private SlingHttpServletRequest request;

    @Self
    @ScriptVariable
    private Resource resource;

    @ValueMapValue(name = "campaign")
    private String campaignId;

    @Inject
    private String titlePropName;

    private Locale pageLocale;
    private String campaign;
    private String title;

    @PostConstruct
    private void init() {
        if (resource == null && request != null) {
            resource = request.getResource();
        }
        pageLocale = languageService.getLocale(resource);
        campaign = AttributeUtil.removeEmptyLines(resolveTagTitle(campaignId));
        title = AttributeUtil.removeEmptyLines(resolveTitle());
    }

    private String resolveTitle() {
        if (resource == null) {
            return null;
        }
        final String propName = StringUtils.isBlank(titlePropName) ? DEFAULT_TITLE_PN : titlePropName;
        return resource.getValueMap().get(propName, String.class);
    }

    public String getCampaign() {
        return campaign;
    }

    public String getTitle() {
        return title;
    }

    private String resolveTagTitle(final String tagId) {
        return Optional.ofNullable(resourceResolver)
            .map(resolver -> resolver.adaptTo(TagManager.class))
            .map(manager -> manager.resolve(tagId))
            .map(tag -> tag.getTitle(pageLocale))
            .orElse(StringUtils.EMPTY);
    }

}
