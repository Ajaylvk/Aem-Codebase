package com.softwareag.core.models.link;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkItemModel {

    @ChildResource
    private LinkModel link;

    /**
     * Function that returns the link configurations for a given link item.
     *
     * @return {@link LinkModel} with the link configurations for a given link item.
     */
    public LinkModel getLink() {
        return link;
    }

    /**
     * Checks if the link item has content attached.
     *
     * @return {@code true} if the link item has content; otherwise {@code false}.
     */
    public boolean hasContent() {
        return Optional.ofNullable(this.link)
            .map(LinkModel::hasContent)
            .orElse(false);
    }
}
