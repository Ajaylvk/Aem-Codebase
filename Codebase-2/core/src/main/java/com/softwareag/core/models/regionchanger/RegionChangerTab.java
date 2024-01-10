package com.softwareag.core.models.regionchanger;

import com.softwareag.core.models.link.LinkModel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RegionChangerTab {

    @Self
    private Resource currentResource;

    @ValueMapValue
    private String title;

    private final List<LinkModel> linkItems = new ArrayList<>();

    @PostConstruct
    private void init() {
        currentResource.getChildren().forEach(childResource -> {
                final LinkModel linkModel = childResource.adaptTo(LinkModel.class);
                linkItems.add(linkModel);
            }
        );
    }

    public String getTitle() {
        return title;
    }

    public List<LinkModel> getLinkItems() {
        return Collections.unmodifiableList(linkItems);
    }

}
