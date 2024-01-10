package com.softwareag.core.services.query;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Service to allow central configuration of query parameters
 */
@Designate(ocd = QueryConfigService.Config.class)
@Component(service = QueryConfigService.class, immediate = true)
public class QueryConfigService {

    @ObjectClassDefinition(name = "Software AG Query Configurations",
                           description = "Configuration of query parameters")
    public @interface Config {

        @AttributeDefinition(name = "Press items: Query Limit",
                             description = "the limit for the number of Press Item pages that will be returned in the PressOverview query")
        int press_query_limit() default 1000;

        @AttributeDefinition(name = "Event items: Query Limit",
                             description = "the limit for the number of EventItem pages that will be returned in the EventOverview query")
        int event_query_limit() default 500;

    }

    private int eventQueryLimit;

    private int pressQueryLimit;

    @Activate
    protected void activate(final Config config) {
        eventQueryLimit = config.event_query_limit();
        pressQueryLimit = config.press_query_limit();
    }

    /**
     * provides the limit for the the Event Overview Items query
     *
     * @return query limit
     */
    public int getEventQueryLimit() {
        return eventQueryLimit;
    }

    /**
     * provides the limit for the the Press Overview Items query
     *
     * @return query limit
     */
    public int getPressQueryLimit() {
        return pressQueryLimit;
    }

}
