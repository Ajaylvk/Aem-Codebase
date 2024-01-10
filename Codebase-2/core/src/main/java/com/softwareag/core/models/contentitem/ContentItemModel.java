package com.softwareag.core.models.contentitem;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.softwareag.core.models.cta.CtaModel;
import com.softwareag.core.models.filtercontainer.FilterContainerModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

import static com.softwareag.core.models.filtercontainer.FilterContainerModel.FILTER_CONTAINER_RESOURCE_TYPE;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ContentItemModel {

    public static final String CONTENT_ITEM_RESOURCE_TYPE = "softwareag/components/content/contentitem";

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private Resource resource;

    @ValueMapValue(name = "tags")
    private String[] tagPaths;

    private String filter1 = "";
    private String filter2 = "";
    private String filter3 = "";

    private CtaModel ctaModel;

    @PostConstruct
    protected void init() {
        prepareFilters();
        ctaModel = resource.adaptTo(CtaModel.class);
    }

    public CtaModel getCtaModel() {
        return ctaModel;
    }

    public String getFilter1() {
        return filter1;
    }

    public String getFilter2() {
        return filter2;
    }

    public String getFilter3() {
        return filter3;
    }

    private void prepareFilters() {
        if (ArrayUtils.isEmpty(tagPaths)) {
            return;
        }

        final Resource filterContainerResource = getParentResource(resource, FILTER_CONTAINER_RESOURCE_TYPE);
        if (filterContainerResource == null) {
            return;
        }

        final FilterContainerModel filterContainerModel = filterContainerResource.adaptTo(FilterContainerModel.class);
        if (filterContainerModel == null) {
            return;
        }

        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        for (final String tagPath : Objects.requireNonNull(tagPaths)) {
            final Tag tag = Objects.requireNonNull(tagManager).resolve(tagPath);
            if (filterContainerModel.matchesOptionTag1(tagPath)) {
                filter1 = extendFilter(filter1, tag);
            }
            if (filterContainerModel.matchesOptionTag2(tagPath)) {
                filter2 = extendFilter(filter2, tag);
            }
            if (filterContainerModel.matchesOptionTag3(tagPath)) {
                filter3 = extendFilter(filter3, tag);
            }
        }
    }

    private String extendFilter(final String filter, final Tag tag) {
        if (tag == null) {
            return filter;
        }

        String tmpString = filter;
        if (!tmpString.isEmpty()) {
            tmpString += ";";
        }
        return tmpString + tag.getName();
    }

    private static Resource getParentResource(final Resource resource, final String parentResourceType) {
        if (resource == null || StringUtils.isBlank(parentResourceType)) {
            return null;
        }
        Resource tmpResource = resource;
        while (tmpResource.getParent() != null) {
            if (tmpResource.getParent().isResourceType(parentResourceType)) {
                return tmpResource.getParent();
            }
            tmpResource = tmpResource.getParent();
        }
        return null;
    }

}
