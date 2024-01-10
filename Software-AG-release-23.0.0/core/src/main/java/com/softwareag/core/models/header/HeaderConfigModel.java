package com.softwareag.core.models.header;

import com.softwareag.core.configuration.AbstractConfigurationComponent;
import com.softwareag.core.models.link.ImageLinkModel;
import com.softwareag.core.models.link.LinkModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderConfigModel extends AbstractConfigurationComponent {

    public static final String HEADER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/header";

    @Self
    private Resource currentResource;

    @ChildResource(name = "logo")
    private ImageLinkModel logo;

    @ValueMapValue
    private String title;

    @ChildResource
    private LinkModel link;

    @ChildResource(name = "firstIcon")
    private ImageLinkModel firstIcon;

    @ChildResource(name = "secondIcon")
    private ImageLinkModel secondIcon;

    @ChildResource(name = "secondIcon/link")
    private LinkModel secondIconLink;

    @ValueMapValue(name = "secondIcon/altText")
    private String secondIconAltText;

    @ValueMapValue(name = "search/altTextOpenSearch")
    private String openSearchAltText;

    @ValueMapValue(name = "search/altTextStartSearch")
    private String startSearchAltText;

    @ValueMapValue(name = "search/altTextCloseSearch")
    private String closeSearchAltText;

    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return HEADER_CONFIG_RESOURCE_TYPE;
    }

    public LinkModel getSecondIconLink() {
        return this.secondIconLink;
    }

    public ImageLinkModel getFirstIcon() {
        return firstIcon;
    }

    public ImageLinkModel getSecondIcon() {
        return secondIcon;
    }

    public ImageLinkModel getLogo() {
        return logo;
    }

    public String getSecondIconAltText() {
        return secondIconAltText;
    }

    public String getOpenSearchAltText() {
        return openSearchAltText;
    }

    public String getStartSearchAltText() {
        return startSearchAltText;
    }

    public String getCloseSearchAltText() {
        return closeSearchAltText;
    }

    public boolean isConfigured() {
        return StringUtils.isNotBlank(logo.getImage())
            && StringUtils.isNotBlank(logo.getAltText())
            && StringUtils.isNotBlank(logo.getHref());
    }

    public String getTitle() {
        return title;
    }

    public LinkModel getLink() {
        return link;
    }
   
}
