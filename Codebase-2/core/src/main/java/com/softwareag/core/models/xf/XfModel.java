package com.softwareag.core.models.xf;

import com.day.cq.commons.Externalizer;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.Iterator;

@Model(adaptables = Resource.class)
public class XfModel {

    private static final String XF_ROOT = "root";
    private static final String HTML_EXTENSION = ".html";

    @OSGiService
    private Externalizer externalizer;

    @Self
    private Resource resource;

    private String xfRemoteOfferLink = "";

    @PostConstruct
    private void init() {
        initRemoteOfferLink();
    }

    public String getXfRemoteOfferLink() {
        return xfRemoteOfferLink;
    }

    private void initRemoteOfferLink() {
        final Resource parsysResource = resource.getChild(XF_ROOT);
        if (parsysResource == null || !parsysResource.hasChildren()) {
            return;
        }

        final Iterator<Resource> parsysChildren = parsysResource.listChildren();
        if (parsysChildren.hasNext()) {
            final String firstParsysChildPath = parsysChildren.next().getPath();
            xfRemoteOfferLink = externalizer.publishLink(resource.getResourceResolver(), firstParsysChildPath);
            xfRemoteOfferLink += HTML_EXTENSION;
        }
    }

}
