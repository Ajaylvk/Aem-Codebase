package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.util.AbstractNavigationLinkPage;

import org.apache.commons.lang3.StringUtils;


public class NavItem extends AbstractNavigationLinkPage {

    private String overviewTitle;

    private Page currentPage;

    public NavItem(final Page page, final LabelConfigModel labelConfigModel) {

        this.currentPage = page;
        super.initRedirectNav(currentPage);

        String labelOverviewTitle = labelConfigModel != null ? labelConfigModel.getLabelOverview() : "";

        // create the overview title only if the page provides content
        if (!Template.FOLDER_PAGE.isTemplateOfPage(page) &&
            !Template.CONFIG_PAGE.isTemplateOfPage(page) &&
            !Template.ERROR_PAGE.isTemplateOfPage(page)) {
            if (StringUtils.isBlank(labelOverviewTitle)) {
                this.overviewTitle = title;
            } else {
                this.overviewTitle = title + " " + labelOverviewTitle;
            }
        }
    }

    public String getOverviewTitle() {
        return overviewTitle;
    }

    @Override
    protected String getPath() {
        return currentPage.getPath();
    }
}
