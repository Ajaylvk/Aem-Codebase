package com.softwareag.core.models.gridcontainer;

import com.softwareag.core.models.link.LinkModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GridContainerModel {

    @Inject
    private LinkModel link;

    @Inject
    private Resource container;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String gridLayout;

    @ValueMapValue
    private String design;

    @ValueMapValue
    private String fontStyle;

    @ValueMapValue
    private String backgroundStyle;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String anchorName;

    public boolean hasContent() {
        if (StringUtils.isNotBlank(title)) {
            return true;
        }

        return StringUtils.isNotBlank(text)
            || (link != null && link.hasContent())
            || (container != null && container.hasChildren());
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getGridLayout() {
        return gridLayout;
    }

    public String getDesign() {
        return design;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public String getFileReference() {
        return fileReference;
    }

    public LinkModel getLink() {
        return link;
    }

    public String getAnchorName() {
        return anchorName;
    }
}

