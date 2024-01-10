package com.softwareag.core.models.popup;

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
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PopUpRedirectModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/homepage";

    private final AemContext context = new AemContext();

    @Mock
    private LanguageService languageService;

    private PopUpRedirectModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/popUpConfig.json", DEST_PATH);
        context.currentResource(DEST_PATH);
        context.registerService(languageService);

        when(languageService.getLocale(any(Page.class), any())).thenReturn(Locale.GERMANY);

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(PopUpRedirectModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetConfig() {
        assertThat(modelUnderTest.getConfig()).isNotNull();
    }

    @Test
    void testPageLocale() {
        assertThat(modelUnderTest.getPageLocale()).isEqualTo(Locale.GERMANY);
    }

}
