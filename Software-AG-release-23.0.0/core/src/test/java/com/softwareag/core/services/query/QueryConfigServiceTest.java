package com.softwareag.core.services.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QueryConfigServiceTest {

    private QueryConfigService serviceUnderTest;

    @Test
    void test_getters_with_defaults() {
        final QueryConfigService.Config config = mock(QueryConfigService.Config.class);

        this.serviceUnderTest = new QueryConfigService();
        this.serviceUnderTest.activate(config);

        assertEquals(0, serviceUnderTest.getEventQueryLimit());
        assertEquals(0, serviceUnderTest.getPressQueryLimit());
    }

    @Test
    void test_getters_with_customisation() {
        final QueryConfigService.Config config = mock(QueryConfigService.Config.class);
        when(config.event_query_limit()).thenReturn(99);
        when(config.press_query_limit()).thenReturn(101);

        this.serviceUnderTest = new QueryConfigService();
        this.serviceUnderTest.activate(config);

        assertEquals(99, serviceUnderTest.getEventQueryLimit());
        assertEquals(101, serviceUnderTest.getPressQueryLimit());
    }

}
