package com.softwareag.core.models.blog;

import com.softwareag.core.models.link.LinkModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BlogFooterModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String blogAltText;

    @Inject
    private LinkModel link;

    public boolean hasContent() {
        return StringUtils.isNotBlank(title) || StringUtils.isNotBlank(copyText) || StringUtils.isNotBlank(image) ||
            (this.link != null && StringUtils.isNotBlank(this.link.getHref()));
    }

    public String getTitle() {

        return title;
    }

    public String getCopyText() {

        return copyText;
    }

    public String getImage() {

        return image;
    }

    public String getBlogAltText() {

        return blogAltText;
    }

    public LinkModel getLink() {

        return link;
    }
}
