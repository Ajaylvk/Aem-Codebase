package com.softwareag.empower.core.configurations;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Software AG Empower Auth configuration",
        description = "Configuration of Authentication app properties")
public @interface AuthConfig {

    @AttributeDefinition(name = "Authentication app Service URL",
            description = "Retrieves the configured Service URL",
            type = AttributeType.STRING)
    String service_url() default "";

    @AttributeDefinition(name = "Authentication app Client ID",
            description = "Retrieves the configured Client ID",
            type = AttributeType.STRING)
    String client_id() default "";

    @AttributeDefinition(name = "Authentication app Client Secret",
            description = "Retrieves the configured Client Secret",
            type = AttributeType.STRING)
    String client_secret() default "";

    @AttributeDefinition(name = "Authentication app Redirect URI",
            description = "Retrieves the configured Redirect URI",
            type = AttributeType.STRING)
    String redirect_uri() default "";
}
