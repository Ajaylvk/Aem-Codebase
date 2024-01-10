package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HeaderModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/homepage";

    private final AemContext context = new AemContext();

    @Mock
    private LanguageService languageService;

    private HeaderModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/header/header.json", DEST_PATH);
        context.currentResource(DEST_PATH);
        context.registerService(languageService);

        when(languageService.getLocale(any(Page.class), any())).thenReturn(Locale.GERMANY);

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(HeaderModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetFirstLevelNavItems() {
        assertThat(modelUnderTest.getFirstLevelNavItems().size()).isEqualTo(HeaderModel.MAX_FIRST_LEVEL_NAV_ITEMS);

        for (FirstLevelNavItem firstLevelNavItem : modelUnderTest.getFirstLevelNavItems()) {
            assertThat(firstLevelNavItem.getLink()).isNotNull();
            assertTrue(firstLevelNavItem.getLink().hasContent());
            assertThat(firstLevelNavItem.getLink().getText()).startsWith("First Level Item");
        }
    }

    @Test
    void testFirstLevelNavItemRedirectHasTargetBlank() {
        for (FirstLevelNavItem firstLevelNavItem : modelUnderTest.getFirstLevelNavItems()) {
            if (firstLevelNavItem.getPath().contains("first-level-item-2")) {
                assertEquals("_blank", firstLevelNavItem.getLink().getTarget());
                assertEquals("/content/softwareag.html", firstLevelNavItem.getLink().getHref());
            }
            if (firstLevelNavItem.getPath().contains("first-level-item-3")) {
                assertEquals("_blank", firstLevelNavItem.getLink().getTarget());
                assertEquals("https://www.google.com", firstLevelNavItem.getLink().getHref());
            }
            if (firstLevelNavItem.getPath().contains("first-level-item-5")) {
                assertEquals("_self", firstLevelNavItem.getLink().getTarget());
                assertEquals("/content/softwareag/test/homepage/first-level-item-5.html", firstLevelNavItem.getLink().getHref());
            }
        }
    }

    @Test
    void testGetConfig() {
        assertThat(modelUnderTest.getConfig()).isNotNull();
    }

    @Test
    void test_getSearchCountry() {
        final String searchCountry = modelUnderTest.getSearchCountry();
        assertThat(searchCountry).isEqualTo("de");
    }

}
