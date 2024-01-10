package com.softwareag.core.models.title;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TitleModel {

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue(name = JcrConstants.JCR_TITLE)
    private String title;

    @ValueMapValue
    private String position;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String fontStyle;

    @ValueMapValue
    private String backgroundStyle;

    @PostConstruct
    private void initModel() {
        if (StringUtils.isBlank(title)) {
            title = StringUtils.defaultIfEmpty(currentPage.getPageTitle(), currentPage.getTitle());
        }
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(copyText);
    }

    public String getTitle() {
        return title;
    }

    public String getPosition() {
        return position;
    }

    public String getCopyText() {
        return copyText;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }
}
