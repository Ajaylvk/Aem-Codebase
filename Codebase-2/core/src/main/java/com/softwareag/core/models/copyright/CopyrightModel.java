package com.softwareag.core.models.copyright;

import com.softwareag.core.models.link.LinkItemModel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CopyrightModel {

    @ChildResource
    private String copyrightInformation;

    @ChildResource
    private String cookieConsentInformation;

    @ChildResource
    private List<LinkItemModel> linkItems;

    /**
     * Gets the configured copyright information.
     *
     * @return {@link String} with the configured copyright information.
     */
    public String getCopyrightInformation() {
        return copyrightInformation;
    }

    /**
     * Gets the configured cookie consent information.
     *
     * @return {@link String} with the configured cookie consent information.
     */
    public String getCookieConsentInformation() {
        return cookieConsentInformation;
    }

    /**
     * Gets the configured list of links.
     *
     * @return {@link List} of {@link LinkItemModel} with the configured list of links.
     */
    public List<LinkItemModel> getLinkItems() {

        return Optional.ofNullable(linkItems)
            .map(List::stream)
            .orElseGet(Stream::empty)
            .collect(Collectors.toList());
    }
}
