package com.softwareag.core.models.embed;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.softwareag.core.models.embed.SagUrlProcessor.NAME;
import static com.softwareag.core.models.embed.SagUrlProcessor.URL_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagUrlProcessorTest {

    private final String[] allowedUrlPatternsList = {"https://.*.ariva.de/.*"};

    @Mock
    private SagUrlProcessor.Config mockConfig;

    @Test
    void testGetProperties() {
        SagUrlProcessor sagUrlProcessor = new SagUrlProcessor();
        when(mockConfig.allowedUrlPatterns()).thenReturn(allowedUrlPatternsList);
        sagUrlProcessor.activate(mockConfig);
        SagUrlProcessorResultImpl sagUrlProcessorResult = (SagUrlProcessorResultImpl) sagUrlProcessor.process(
            "https://bfrank.ariva.de/de/deutsche_boerse/kurse.m?isin=DE000A2GS401");

        assertEquals(NAME, sagUrlProcessorResult.getProcessor());
        assertEquals("https://bfrank.ariva.de/de/deutsche_boerse/kurse.m?isin=DE000A2GS401", sagUrlProcessorResult.getOptions().get(URL_ID));
    }
}
