package com.softwareag.core.constants;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public enum Template {

    CONFIG_PAGE("/conf/softwareag/settings/wcm/templates/config-page"),
    HOME_PAGE("/conf/softwareag/settings/wcm/templates/homepage"),
    PRESS_PAGE("/conf/softwareag/settings/wcm/templates/press"),
    PRESS_OVERVIEW_PAGE("/conf/softwareag/settings/wcm/templates/pressoverview"),
    EVENT_PAGE("/conf/softwareag/settings/wcm/templates/event"),
    EVENT_OVERVIEW_PAGE("/conf/softwareag/settings/wcm/templates/eventoverview"),
    FOLDER_PAGE("/conf/softwareag/settings/wcm/templates/folder"),
    ERROR_PAGE("/conf/softwareag/settings/wcm/templates/error"),
    GENERIC_PAGE("/conf/softwareag/settings/wcm/templates/generic");

    private final String path;

    Template(String path) {
        this.path = path;
    }

    /**
     * Return the template path of the given {@link Page}.
     *
     * @param page
     * 		to get the template path from
     * @return the template path or empty string if not possible to find it
     */
    public static String getTemplatePathFromPage(final Page page) {
        return Optional.ofNullable(page)
            .map(Page::getProperties)
            .map(properties -> properties.get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY))
            .orElse(StringUtils.EMPTY);
    }

    /**
     * Checks whether the given {@link Page} has this template.
     *
     * @param page
     * 		the page to check
     * @return {@code true} if the page has this template, {@code false} otherwise
     */
    public boolean isTemplateOfPage(final Page page) {
        return StringUtils.equals(this.path, Template.getTemplatePathFromPage(page));
    }

    public String getPath() {
        return path;
    }
}
