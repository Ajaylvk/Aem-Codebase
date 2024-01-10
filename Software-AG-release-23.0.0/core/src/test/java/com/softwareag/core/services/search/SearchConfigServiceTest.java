package com.softwareag.core.services.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchConfigServiceTest {

    private static final String SEARCH_INSTANCE_PATH = "searchInstanceUri";
    private static final String SEARCH_API = "searchApiExample";
    private static final String SUGGESTION_API = "suggestionApiExample";

    private SearchConfigService serviceUnderTest;

    @Mock
    private SearchConfigService.Config mockConfig;

    @BeforeEach
    public void setUp() {
        serviceUnderTest = new SearchConfigService();
    }

    @Test
    void getSearchInstancePath_returnsConfigString() {
        when(mockConfig.searchInstanceUri()).thenReturn(SEARCH_INSTANCE_PATH);
        serviceUnderTest.activate(mockConfig);

        String actualResult = serviceUnderTest.getSearchInstanceUri();

        assertEquals(SEARCH_INSTANCE_PATH, actualResult);
    }

    @Test
    void getSearchApiPath_returnsConfigString() {
        when(mockConfig.searchApi()).thenReturn(SEARCH_API);
        serviceUnderTest.activate(mockConfig);

        String actualResult = serviceUnderTest.getSearchApi();

        assertEquals(SEARCH_API, actualResult);
    }

    @Test
    void getSuggestionApiPath_returnsConfigString() {
        when(mockConfig.suggestionApi()).thenReturn(SUGGESTION_API);
        serviceUnderTest.activate(mockConfig);

        String actualResult = serviceUnderTest.getSuggestionApi();

        assertEquals(SUGGESTION_API, actualResult);
    }
}
