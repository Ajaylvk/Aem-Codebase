package com.softwareag.core.models.event;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.util.AbstractNavigationLinkPage;
import com.softwareag.core.util.ContentUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class EventItemModel extends AbstractNavigationLinkPage {

    public static final String PN_RESOURCE_TYPE = "softwareag/components/content/eventitem";

    @OSGiService
    private LanguageService languageService;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private boolean hideEventInEventOverview;

    @ValueMapValue
    private boolean enableCounter;

    @ValueMapValue
    private String eventTitle;

    @ValueMapValue
    private String eventDescription;

    @ValueMapValue(name = "eventType")
    private String eventTypeTagId;

    @ValueMapValue(name = "eventCountry")
    private String eventCountryTagId;

    @ValueMapValue
    private String eventStartDate;

    @ValueMapValue
    private String eventEndDate;

    @ValueMapValue
    private String eventTimeZone;

    @ValueMapValue
    private String externalEventLink;

    private Locale pageLocale;
    private Page currentPage;
    private LabelConfigModel labelConfigModel;
    private GeneralConfigModel generalConfigModel;

    private String path;
    private String overviewPath;

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(resource);

        Optional.ofNullable(resourceResolver)
            .map(rr -> rr.adaptTo(PageManager.class))
            .ifPresent(pageManager -> this.currentPage = pageManager.getContainingPage(resource));

        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);
        generalConfigModel = ConfigurationFinder.findGeneralConfigComponent(this.currentPage).orElse(null);

        if (StringUtils.isNotBlank(externalEventLink)) {
            initRedirectNav(currentPage, externalEventLink);
        } else {
            initRedirectNav(currentPage);
        }
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(getEventTitle())
            && StringUtils.isNotBlank(getEventCountry())
            && StringUtils.isNotBlank(getEventType())
            && StringUtils.isNotBlank(getEventStartDate());
    }

    @Override
    protected String getPath() {
        if (StringUtils.isBlank(path)) {
            path = StringUtils.remove(resource.getPath(), "/jcr:content/eventitem");
        }
        return path;
    }

    public Locale getPageLocale() {
        return pageLocale;
    }

    public boolean isHideEventInEventOverview() {
        return hideEventInEventOverview;
    }

    public boolean isEnableCounter() {
        return enableCounter;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getExternalEventLink() {
        return externalEventLink;
    }

    public String getEventCountry() {
        return Optional.ofNullable(resourceResolver)
            .map(resolver -> resolver.adaptTo(TagManager.class))
            .map(manager -> manager.resolve(eventCountryTagId))
            .map(tag -> tag.getTitle(pageLocale))
            .orElse(StringUtils.EMPTY);
    }

    public String getEventType() {
        return Optional.ofNullable(resourceResolver)
            .map(resolver -> resolver.adaptTo(TagManager.class))
            .map(manager -> manager.resolve(eventTypeTagId))
            .map(tag -> tag.getTitle(pageLocale))
            .orElse(StringUtils.EMPTY);
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public String getOverviewPath() {
        if (StringUtils.isBlank(overviewPath)) {
            overviewPath = ContentUtil.findNearestParentByTemplate(this.currentPage, Template.EVENT_OVERVIEW_PAGE)
                .map(Page::getPath)
                .orElse(StringUtils.EMPTY);
        }

        return overviewPath;
    }

    public String getLabelGoToOverview() {
        return labelConfigModel != null ? labelConfigModel.getLabelGoToOverview() : "";
    }

    public String getLabelDays() {
        return labelConfigModel != null ? labelConfigModel.getLabelDays() : "";
    }

    public String getLabelHours() {
        return labelConfigModel != null ? labelConfigModel.getLabelHours() : "";
    }

    public String getLabelMinutes() {
        return labelConfigModel != null ? labelConfigModel.getLabelMinutes() : "";
    }

    public String getLabelSeconds() {
        return labelConfigModel != null ? labelConfigModel.getLabelSeconds() : "";
    }

    public String getGeneralConfigDateFormat() {
        return this.generalConfigModel != null ? this.generalConfigModel.getDateFormat() + " " + this.generalConfigModel.getTimeFormat() : "MM/dd/yyyy h:mm a";
    }

    public String getEventTimeZone() {
        return eventTimeZone;
    }

    public String getEventTypeTagId() {
        return eventTypeTagId;
    }

    public String getEventCountryTagId() {
        return eventCountryTagId;
    }

}
