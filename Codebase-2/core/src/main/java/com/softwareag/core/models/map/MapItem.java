package com.softwareag.core.models.map;

import com.softwareag.core.models.link.LinkModel;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MapItem {

    @ValueMapValue
    private String latitude;

    @ValueMapValue
    private String longitude;

    @ValueMapValue
    private String text;

    @ChildResource
    private LinkModel link;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getText() {
        return text;
    }

    public LinkModel getLink() {
        return link;
    }
}
