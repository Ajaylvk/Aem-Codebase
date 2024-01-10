package com.softwareag.core.models.press;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.util.AbstractNavigationLinkPage;
import com.softwareag.core.util.AttributeUtil;
import com.softwareag.core.util.ContentUtil;

import org.apache.commons.lang.StringUtils;
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


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PressItem extends AbstractNavigationLinkPage {

    @OSGiService
    private LanguageService languageService;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private boolean hidePressInPressOverview;

    @ValueMapValue
    private String pressTitle;

    @ValueMapValue
    private String pressHighlights;

    @ValueMapValue
    private String pressDescription;

    @ValueMapValue
    private String pressSummary;

    @ValueMapValue
    private String pressReleaseDate;

    @ValueMapValue
    private String externalPressLink;

    private Locale pageLocale;
    private Page currentPage;

    private String path;
    private String overviewPath;

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(resource);

        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            currentPage = pageManager.getContainingPage(resource);
        }

        if (StringUtils.isNotBlank(externalPressLink)) {
            initRedirectNav(currentPage, externalPressLink);
        } else {
            initRedirectNav(currentPage);
        }
    }

    public boolean isHidePressInPressOverview() {
        return hidePressInPressOverview;
    }

    public String getPressTitle() {
        return pressTitle;
    }

    public String getDataAttribValue() {
        return AttributeUtil.removeEmptyLines(getPressTitle());
    }

    public String getPressHighlights() { return pressHighlights; }

    public String getPressSummary() { return pressSummary; }

    public String getPressDescription() {
        return pressDescription;
    }

    public String getExternalPressLink() {
        return externalPressLink;
    }

    public String getPressReleaseDate() {
        return pressReleaseDate;
    }

    public Locale getPageLocale() {
        return pageLocale;
    }

    @Override
    public String getPath() {
        if (StringUtils.isBlank(path)) {
            this.path = StringUtils.remove(resource.getPath(), "/jcr:content/pressitem");
        }

        return path;
    }

    public String getOverviewPath() {
        if (StringUtils.isBlank(overviewPath)) {
            overviewPath = ContentUtil.findNearestParentByTemplate(currentPage, Template.PRESS_OVERVIEW_PAGE)
                .map(Page::getPath)
                .orElse(StringUtils.EMPTY);
        }

        return overviewPath;
    }

    public String getLabelGoToOverview() {
        return ConfigurationFinder.findLabelConfigComponent(currentPage)
            .map(LabelConfigModel::getLabelGoToOverview)
            .orElse("");
    }

    public String getGeneralConfigDateFormat() {
        return ConfigurationFinder.findGeneralConfigComponent(currentPage)
            .map(GeneralConfigModel::getDateFormat)
            .orElse("MM/dd/yyyy");
    }

}
