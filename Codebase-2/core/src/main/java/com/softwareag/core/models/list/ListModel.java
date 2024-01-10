package com.softwareag.core.models.list;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.link.LinkItemModel;
import com.softwareag.core.models.link.LinkModel;
import com.softwareag.core.models.link.LinkModelBuilder;
import com.softwareag.core.util.PageUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ListModel {

    private static final Logger LOG = LoggerFactory.getLogger(ListModel.class);

    @ChildResource
    @Named("listItems")
    private List<LinkItemModel> staticLinkItemModels = new ArrayList<>();

    private List<LinkModel> links = new ArrayList<>();

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String listFrom;

    @ValueMapValue
    private String parentPage;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private PageManager pageManager;

    @PostConstruct
    void init() {
        final Source source = Source.fromString(this.listFrom);
        switch (source) {
            case STATIC:
                this.links = buildListFromStatic();
                break;
            case CHILDREN:
                this.links = buildListFromChildren();
                break;
            default:
                LOG.warn("the list can not be built. value '{}' of property '{}' is not supported.", this.listFrom, "listFrom");
                break;
        }
    }

    /**
     * Gets the configured title.
     *
     * @return {@link String} with the configured list title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Gets the configured list of links.
     *
     * @return {@link java.util.List} of {@link LinkModel} with the configured list of links.
     */
    public java.util.List<LinkModel> getLinks() {
        return new ArrayList<>(this.links);
    }

    /**
     * Checks if the list has the required content.
     *
     * @return {@code true} if the list has the content; otherwise {@code false}.
     */
    public boolean hasContent() {
        return StringUtils.isNotBlank(this.title);
    }

    private List<LinkModel> buildListFromStatic() {
        return this.staticLinkItemModels.stream()
            .filter(Objects::nonNull)
            .filter(LinkItemModel::hasContent)
            .map(LinkItemModel::getLink)
            .collect(Collectors.toList());
    }

    private List<LinkModel> buildListFromChildren() {
        List<LinkModel> linkModels = new ArrayList<>();
        if (this.pageManager != null && this.currentPage != null && this.resourceResolver != null) {
            Page rootPage = getRootPage(this.parentPage);
            List<Page> childPages = Optional.ofNullable(rootPage)
                .map(this::collectChildren)
                .orElse(Collections.emptyList());
            childPages.forEach(page -> linkModels.add(new LinkModelBuilder()
                .with(builder -> {
                    builder.setText(PageUtil.getPageTitle(page));
                    builder.setHref(page.getPath());
                })
                .createLinkModel()
            ));
        }
        return linkModels;
    }

    private List<Page> collectChildren(final Page rootPage) {
        List<Page> childPages = new ArrayList<>();
        final Iterator<Page> childIterator = rootPage.listChildren();
        while (childIterator.hasNext()) {
            final Page nextPage = childIterator.next();
            if (!Template.FOLDER_PAGE.isTemplateOfPage(nextPage) &&
                !Template.CONFIG_PAGE.isTemplateOfPage(nextPage) &&
                !Template.ERROR_PAGE.isTemplateOfPage(nextPage)) {
                childPages.add(nextPage);
            }
        }
        return childPages;
    }

    private Page getRootPage(String rootPagePath) {
        return PageUtil.getPageFromPath(rootPagePath, resourceResolver)
            .orElse(currentPage);
    }

    protected enum Source {
        CHILDREN("children"),
        STATIC("static"),
        EMPTY(StringUtils.EMPTY);

        private String value;

        Source(String value) {
            this.value = value;
        }

        public static Source fromString(final String value) {
            for (Source s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return EMPTY;
        }

        public String getValue() {
            return value;
        }
    }
}
