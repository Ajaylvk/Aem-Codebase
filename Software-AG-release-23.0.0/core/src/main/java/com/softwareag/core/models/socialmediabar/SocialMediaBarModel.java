package com.softwareag.core.models.socialmediabar;

import com.softwareag.core.models.link.ImageLinkModel;

import org.apache.commons.collections4.ListUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.List;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialMediaBarModel {

    @Inject
    private List<ImageLinkModel> socialMediaItems;

    /**
     * Gets the configured list of items with the social media information.
     *
     * @return {@link List} of {@link ImageLinkModel} with the configured list of items with the social media information.
     */
    public List<ImageLinkModel> getSocialMediaItems() {

        return ListUtils.emptyIfNull(socialMediaItems);
    }
}
