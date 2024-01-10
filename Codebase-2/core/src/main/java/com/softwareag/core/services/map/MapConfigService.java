package com.softwareag.core.services.map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Service to allow central configuration of Map API Key token
 */
@Designate(ocd = MapConfigService.Config.class)
@Component(service = MapConfigService.class, immediate = true)
public class MapConfigService {

    @ObjectClassDefinition(name = "Software AG Map Configuration",
                           description = "Configuration of Map parameters")
    public @interface Config {

        @AttributeDefinition(name = "Map Configuration API Key Token",
                             description = "Retrieves the configured Map API key token",
                             type = AttributeType.STRING)
        String apiKeyToken() default "";
    }

    private String apiKeyToken;

    @Activate
    protected void activate(final Config config) {
        apiKeyToken = config.apiKeyToken();
    }

    /**
     * @return Map API Key point
     */
    public String getApiKeyToken() {
        return apiKeyToken;
    }

}
