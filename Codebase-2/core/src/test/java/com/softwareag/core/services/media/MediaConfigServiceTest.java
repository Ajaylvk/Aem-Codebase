package com.softwareag.core.services.media;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MediaConfigServiceTest {

    private MediaConfigService serviceUnderTest;

    @Test
    void test_getDmBreakpoints_with_defaults() {
        final MediaConfigService.Config config = mock(MediaConfigService.Config.class);
        when(config.dm_breakpoints()).thenReturn("");

        this.serviceUnderTest = new MediaConfigService();
        this.serviceUnderTest.activate(config);

        assertEquals("", serviceUnderTest.getDmBreakpoints());
    }

    @Test
    void test_getDmBreakpoints_with_customisation() {
        final MediaConfigService.Config config = mock(MediaConfigService.Config.class);
        when(config.dm_breakpoints()).thenReturn("460,1280");

        this.serviceUnderTest = new MediaConfigService();
        this.serviceUnderTest.activate(config);
        assertEquals("460,1280", serviceUnderTest.getDmBreakpoints());
    }

}
