package com.softwareag.core.models;

import com.softwareag.core.services.language.LanguageService;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DateTimeModel {

    private static final Logger LOG = LoggerFactory.getLogger(DateTimeModel.class);

    // Date pattern "2020-01-08" (from 1900-01-01 to 2999-12-31)
    private static final Pattern ALLOWED_DATE_PATTERN = Pattern.compile("(19|2[0-9])[0-9]{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])");

    // DateTime pattern "2020-02-02T02:02:02" (Dates from 2000-01-01 to 2999-12-31 and time 0-23:00-59:00-59)
    private static final Pattern ALLOWED_DATETIME_PATTERN = Pattern.compile(
        "(2[0-9])[0-9]{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]");

    private static final int OLD_PRESS_ITEMS_LENGTH = 29;
    private static final int PLUS_SIGN_INDEX = 23;

    @OSGiService
    private LanguageService languageService;

    @Inject
    private Resource resource;

    @Inject
    private String input;
    @Inject
    private String format;
    @Inject
    private String timeZone;

    private Locale pageLocale;
    private String output;

    @PostConstruct
    public void init() {
        pageLocale = languageService.getLocale(resource, Locale.US);

        assignOutput();
    }

    public String getOutput() {
        return output;
    }

    /**
     * Assigns output as a {String} for the following three possible use cases:
     *
     * 1. Date     - input type is Date, format is Optional.
     * 2. DateTime - input type is DateTime, format is Optional.
     * 3. Time     - input type is DateTime, format should be of type time.
     **/
    private void assignOutput() {
        if (StringUtils.isBlank(input)) {
            LOG.warn("Provided input -> '{}' cannot be blank.", input);
            return;
        }

        /*
         * dates of the press items which are already created are stored with the following format: "2018-10-11T00:00:00.000+02:00"
         * in order to display these dates this approach was implemented
         */
        if (input.length() == OLD_PRESS_ITEMS_LENGTH && input.charAt(PLUS_SIGN_INDEX) == '+') {
            try {
                final ZonedDateTime zonedDateTime = ZonedDateTime.parse(input);
                final LocalDate localDate = zonedDateTime.toLocalDate();
                assignDateOutput(localDate);
            } catch (final RuntimeException e) {
                final String errorMsg = String.format("Parsing input '%s' as ZonedDateTime failed.", input);
                LOG.warn(errorMsg, e);
            }
        } else if (input.matches(String.valueOf(ALLOWED_DATE_PATTERN))) {
            try {
                final LocalDate localDate = LocalDate.parse(input);
                assignDateOutput(localDate);
            } catch (final RuntimeException e) {
                final String errorMsg = String.format("Parsing input '%s' as LocalDate failed.", input);
                LOG.warn(errorMsg, e);
            }
        } else if (input.matches(String.valueOf(ALLOWED_DATETIME_PATTERN))) {
            assignDateTimeOutput();
        } else {
            LOG.warn("Your input -> '{}' uses an invalid date/time pattern.", input);
        }
    }

    private void assignDateOutput(final LocalDate localDate) {
        if (StringUtils.isBlank(format)) {
            output = localDate.toString();
        } else {
            try {
                final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format).withLocale(pageLocale);
                output = dateFormatter.format(localDate);
            } catch (final UnsupportedTemporalTypeException | IllegalArgumentException e) {
                final String errorMsg = String.format("Formatting date '%s' with the format '%s' failed.", localDate, format);
                LOG.warn(errorMsg, e);
            }
        }
    }

    private void assignDateTimeOutput() {
        if (StringUtils.isBlank(format)) {
            output = input;
            return;
        }

        if (timeZone == null) {
            timeZone = TimeZone.getDefault().toZoneId().toString();
        }

        /*
         * We can remove this if we first change timezones format in com.softwareag.core.services.timezone.TimeZoneService
         * from "GMT+1:00" to "GMT+01:00" because it is not a valid ZoneId
         */
        if (timeZone.length() == 8) {
            timeZone = timeZone.substring(0, 4) + "0" + timeZone.substring(4);
        }

        try {
            final LocalDateTime localDateTime = LocalDateTime.parse(input);
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format).withLocale(pageLocale).withZone(ZoneId.of(timeZone));
            output = dateFormatter.format(localDateTime);
        } catch (final UnsupportedTemporalTypeException | IllegalArgumentException e) {
            final String errorMsg = String.format("Formatting date and time '%s' with the format '%s' failed.", input, format);
            LOG.warn(errorMsg, e);
        }
    }

}
