package com.softwareag.core.services.map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MapConfigServiceTest {

    private static final String API_KEY_TOKEN = "apiKeyTokenExample";

    private MapConfigService serviceUnderTest;

    @Mock
    private MapConfigService.Config mockConfig;

    @BeforeEach
    public void setUp() {
        serviceUnderTest = new MapConfigService();
    }

    @Test
    public void getApiKey_returnsConfigString() {
        when(mockConfig.apiKeyToken()).thenReturn(API_KEY_TOKEN);
        serviceUnderTest.activate(mockConfig);

        String actualResult = serviceUnderTest.getApiKeyToken();

        assertEquals(API_KEY_TOKEN, actualResult);
    }

}
