package com.softwareag.core.services.timezone;

import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeZoneServiceTest {

    private static final String TIMEZONE_KEY = "GMT+1:00";
    private static final String TIMEZONE_VALUE = "(GMT+1:00) European Central Time";
    private static final String TIMEZONE_ENTRY = TIMEZONE_KEY + TimeZoneService.DELIMITER + TIMEZONE_VALUE;

    private static final String[] TIMEZONE_LIST = {
        TIMEZONE_ENTRY,
        "invalid entry",
        "invalid=entry=foo"
    };

    private TimeZoneService serviceUnderTest;

    @Mock
    private TimeZoneService.Config mockConfig;

    @BeforeEach
    public void setUp() {
        serviceUnderTest = new TimeZoneService();
    }

    @Test
    void getTimeZone_returnsEmptyMap_ifConfigIsNull() {
        serviceUnderTest.activate(null);

        Map<String, String> timeZoneMap = serviceUnderTest.getTimeZones();

        assertTrue(timeZoneMap.isEmpty());
    }

    @Test
    void getTimeZone_returnsEmptyMap_ifNoTimeZoneIsConfigured() {
        when(mockConfig.getTimeZones()).thenReturn(ArrayUtils.EMPTY_STRING_ARRAY);

        serviceUnderTest.activate(mockConfig);

        Map<String, String> timeZoneMap = serviceUnderTest.getTimeZones();

        assertTrue(timeZoneMap.isEmpty());
    }

    @Test
    void getTimeZone_returnsCorrectAmountOfItems() {
        when(mockConfig.getTimeZones()).thenReturn(TIMEZONE_LIST);

        serviceUnderTest.activate(mockConfig);

        Map<String, String> timeZoneMap = serviceUnderTest.getTimeZones();

        assertEquals(1, timeZoneMap.size());
        assertEquals(TIMEZONE_VALUE, timeZoneMap.get(TIMEZONE_KEY));
        assertTrue(timeZoneMap.containsKey(TIMEZONE_KEY));
    }
}
