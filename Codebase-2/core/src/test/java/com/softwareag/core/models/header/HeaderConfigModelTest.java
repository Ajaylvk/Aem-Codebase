package com.softwareag.core.models.header;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class HeaderConfigModelTest {

    private final AemContext context = new AemContext();
    private static final String DEST_PATH = "/content/softwareag/test/config";

    private HeaderConfigModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/header/headerConfig.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/header");

        modelUnderTest = Objects.requireNonNull(context.currentResource()).adaptTo(HeaderConfigModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void getter_shouldReturnGivenValues() {
        assertThat(modelUnderTest.getLogo()).isNotNull();
        assertThat(modelUnderTest.getLogo().getHref()).isEqualTo("/content/softwareag.html");
        assertThat(modelUnderTest.getLogo().getImage()).isEqualTo("/content/dam/softwareag/icons/logo.png");
        assertThat(modelUnderTest.getLogo().getAltText()).isEqualTo("Logo");
        assertThat(modelUnderTest.getStartSearchAltText()).isEqualTo("Start search");
        assertThat(modelUnderTest.getOpenSearchAltText()).isEqualTo("Open search");
        assertThat(modelUnderTest.getCloseSearchAltText()).isEqualTo("Close search");
        assertThat(modelUnderTest.getSecondIconAltText()).isEqualTo("Contact alt text");
        assertThat(modelUnderTest.getSecondIconLink()).isNotNull();
        assertThat(modelUnderTest.getSecondIconLink().getHref()).isEqualTo("/content/softwareag.html");
        assertThat(modelUnderTest.getSecondIconLink().getText()).isEqualTo("Contact Title");
        assertThat(modelUnderTest.getFirstIcon()).isNotNull();
        assertThat(modelUnderTest.getFirstIcon().getLink()).isNotNull();
        assertThat(modelUnderTest.getFirstIcon().getLink().getText()).isEqualTo("Additional Title");
        assertThat(modelUnderTest.getFirstIcon().getLink().getHref()).isEqualTo("https://www.softwareag.com");
        assertThat(modelUnderTest.getFirstIcon().getHref()).isEqualTo("https://www.softwareag.com");
        assertThat(modelUnderTest.getFirstIcon().getAltText()).isEqualTo("Additional Icon");
        assertThat(modelUnderTest.getFirstIcon().getImage()).isEqualTo("/content/dam/softwareag/icons/test.svg");
        assertThat(modelUnderTest.getConfigResourceType()).isEqualTo(HeaderConfigModel.HEADER_CONFIG_RESOURCE_TYPE);
        assertThat(modelUnderTest.getResource()).isEqualTo(context.currentResource());
    }

    @Test
    void isConfigured_returnsTrue_IfLogoAndContactUsLinkIsConfigured() {
        assertTrue(modelUnderTest.isConfigured());
    }

    @Test
    void isNotValid_returnsTrue_ifSeveralComponentsExists() {
        assertThat(modelUnderTest.isNotValid()).isTrue();
    }
}
