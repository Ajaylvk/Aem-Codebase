package com.softwareag.core.models.link;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageLinkModel {

    @Inject
    private LinkModel link;

    @ValueMapValue
    private String image;

    @ValueMapValue
    private String altText;

    private String href;

    private String target;

    @PostConstruct
    private void init() {
        if (link != null) {
            this.href = link.getHref();
            this.target = link.getTarget();
        }
    }

    /**
     * Gets the configured image path
     *
     * @return {@link String} with the configured image path.
     */
    public String getImage() {

        return image;
    }

    /**
     * Gets the configured Alternative Text.
     *
     * @return {@link String} with the configured Alternative Text.
     */
    public String getAltText() {

        return altText;
    }

    /**
     * Gets the configured destination href.
     *
     * @return {@link String} with the configured destination href.
     */
    public String getHref() {

        return href;
    }

    /**
     * Gets the configured link target-attribute.
     *
     * @return target-attribute
     */
    public String getTarget() {

        return target;
    }

    public LinkModel getLink() {
        return link;
    }

	public void setLink(LinkModel link) {
		this.link = link;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setTarget(String target) {
		this.target = target;
	}
    
    
}
