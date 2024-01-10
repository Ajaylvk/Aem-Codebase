package com.softwareag.core.services.domains;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service to register allowed domains
 */
@Designate(ocd = RegisteredDomainsService.Config.class)
@Component(service = RegisteredDomainsService.class, immediate = true)
public class RegisteredDomainsService {

    private final List<String> registeredDomains = new ArrayList<>();

    @ObjectClassDefinition(name = "Software AG Registered Domains", description = "All softwareag valid domains within AEM")
    public @interface Config {

        @AttributeDefinition(name = "Registered domains",
                             description = "AEM domains",
                             type = AttributeType.STRING)
        String[] registeredDomains() default {};
    }

    /**
     * Activates the OSGi config service
     *
     * @param config
     *     requires config to activate the service
     */
    @Activate
    public void activate(Config config) {
        if (ArrayUtils.isNotEmpty(config.registeredDomains())) {
            registeredDomains.addAll(Arrays.asList(config.registeredDomains()));
        } else {
            registeredDomains.clear();
        }
    }

    public List<String> getRegisteredDomains() {
        if (CollectionUtils.isEmpty(registeredDomains)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(registeredDomains);
        }
    }
}
