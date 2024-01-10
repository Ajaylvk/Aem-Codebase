package com.softwareag.core.models.search;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SearchConfigTest {

    private final AemContext context = new AemContext();
    private static final String DEST_PATH = "/content/softwareag/test/config";

    private SearchConfigModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/search/search.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/search-config");

        modelUnderTest = Objects.requireNonNull(context.currentResource()).adaptTo(SearchConfigModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetSearchUiLanguage() {
        assertEquals("de", modelUnderTest.getSearchUiLanguage());
    }

    @Test
    void testGetSearchLanguage() {
        assertEquals("en", modelUnderTest.getSearchLanguage());
    }
}
