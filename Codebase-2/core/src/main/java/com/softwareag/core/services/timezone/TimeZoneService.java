package com.softwareag.core.services.timezone;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service that provides the supported timezones.
 */
@Component(service = TimeZoneService.class, immediate = true)
@Designate(ocd = TimeZoneService.Config.class)
@ServiceDescription("Provides all maintainable event timezones.")

public class TimeZoneService {

    public static final String DELIMITER = "=";

    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneService.class);

    @ObjectClassDefinition(name = "Software AG Timezones", description = "List of all maintainable event timezones.")
    public @interface Config {

        @AttributeDefinition(name = "Timezones",
                             description = "A timezone entry consists of a key-value pair, where the key is the GMT zone and the value is a corresponding description. " +
                                 "As a delimiter '" + DELIMITER + "' is used. Example: 'GMT+1:00=European Central Time'.")
        String[] getTimeZones() default {};
    }

    private Config config;

    @Activate
    public void activate(final Config config) {
        this.config = config;
    }

    /**
     * Gets all configured timezones.
     *
     * @return a {@code Map} with the configured timezones or an empty {@code Map} if no timezone is configured.
     * The {@code key} is the GMT zone and the {@code value} is a corresponding description.
     */
    public Map<String, String> getTimeZones() {
        Map<String, String> timeZoneMap = new LinkedHashMap<>();

        if (this.config != null && !ArrayUtils.isEmpty(this.config.getTimeZones())) {
            for (final String entry : this.config.getTimeZones()) {
                String[] splittedEntry = StringUtils.split(entry, DELIMITER);
                if (splittedEntry != null && splittedEntry.length == 2) {
                    timeZoneMap.put(splittedEntry[0], splittedEntry[1]);
                } else {
                    LOG.warn("Cannot parse timezone entry '{}'", entry);
                }
            }
        }

        return timeZoneMap;
    }
}
