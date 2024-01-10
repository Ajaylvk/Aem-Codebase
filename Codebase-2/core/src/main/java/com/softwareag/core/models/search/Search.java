package com.softwareag.core.models.search;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.services.search.SearchConfigService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Search {

    @OSGiService
    private SearchConfigService searchConfigService;

    private String searchInstanceUri;
    private String searchApi;
    private String suggestionApi;

    @ScriptVariable
    private Page currentPage;

    private SearchConfigModel config;

    @PostConstruct
    private void init() {
        if (searchConfigService != null) {
            searchInstanceUri = searchConfigService.getSearchInstanceUri();
            searchApi = searchConfigService.getSearchApi();
            suggestionApi = searchConfigService.getSuggestionApi();
        }

        final Resource configResource = currentPage == null ? null : ConfigurationFinder.getConfigurationComponent(currentPage,
            SearchConfigModel.SEARCH_CONFIG_RESOURCE_TYPE);

        config = Optional.ofNullable(configResource)
            .map(r -> r.adaptTo(SearchConfigModel.class))
            .orElse(null);
    }

    public String getSearchUri() {
        return searchInstanceUri + searchApi;
    }

    public String getSuggestionUri() {
        return searchInstanceUri + suggestionApi;
    }

    public SearchConfigModel getConfig() {
        return config;
    }
}
