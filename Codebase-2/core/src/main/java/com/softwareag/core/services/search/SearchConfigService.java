package com.softwareag.core.services.search;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Service to allow central configuration of Search API Uri
 */
@Designate(ocd = SearchConfigService.Config.class)
@Component(service = SearchConfigService.class, immediate = true)
public class SearchConfigService {

    @ObjectClassDefinition(name = "Software AG Search API Uri configuration",
                           description = "Configuration of Search API entry point")
    public @interface Config {

        @AttributeDefinition(name = "Software AG Search API Uri configuration",
                             description = "Retrieves the configured Search API Uri",
                             type = AttributeType.STRING)
        String searchInstanceUri() default "";
        String searchApi() default "";
        String suggestionApi() default "";
    }

    private String searchInstanceUri;

    private String searchApi;

    private String suggestionApi;

    @Activate
    protected void activate(final Config config) {
        searchInstanceUri = config.searchInstanceUri();
        searchApi = config.searchApi();
        suggestionApi = config.suggestionApi();
    }

    /**
     * @return Search API Uri
     */
    public String getSearchInstanceUri() {
        return searchInstanceUri;
    }

    public String getSearchApi() { return searchApi; }

    public String getSuggestionApi() { return suggestionApi; }

}
