package com.softwareag.core.models.search;

import com.softwareag.core.configuration.AbstractConfigurationComponent;
import com.softwareag.core.models.link.LinkModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchConfigModel extends AbstractConfigurationComponent {

    private static final String DEFAULT_SEARCH_UI_LANGUAGE = "en";
    private static final String DEFAULT_SEARCH_LANGUAGE = "en";

    public static final String SEARCH_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/search-config";

    @Self
    private Resource currentResource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    @Default(values = DEFAULT_SEARCH_UI_LANGUAGE)
    private String searchUiLanguage;

    @ValueMapValue
    @Default(values = DEFAULT_SEARCH_LANGUAGE)
    private String searchLanguage;

    @ChildResource
    private LinkModel link;

    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return SEARCH_CONFIG_RESOURCE_TYPE;
    }

    public String getSearchUiLanguage() {
        return searchUiLanguage;
    }

    public String getSearchLanguage() {
        return searchLanguage;
    }

    public LinkModel getLink() {
        return link.rewriteLink();
    }

    public boolean isConfigured() {
        return StringUtils.isNotBlank(Optional.ofNullable(this.link)
            .map(LinkModel::getHref)
            .orElse(StringUtils.EMPTY));
    }
}
