package com.softwareag.core.models.regionchanger;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class RegionChangerModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";

    private final AemContext context = new AemContext();

    private RegionChangerModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/regionchanger.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/regionchanger");

        modelUnderTest = context.request().getResource().adaptTo(RegionChangerModel.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void test_hasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    void test_getTitle() {
        assertEquals("Region Changer", modelUnderTest.getTitle());
    }

    @Test
    void test_getIntroductoryText() {
        assertEquals("Selecting a region changes the language and/or content of this page.", modelUnderTest.getIntroductoryText());
    }

    @Test
    void test_getLink() {
        assertEquals("/content/softwareag/corporate/en_corporate.html", modelUnderTest.getLink().getHref());
        assertEquals("International", modelUnderTest.getLink().getText());
    }

    @Test
    void test_getTabItems() {
        assertEquals(4, modelUnderTest.getTabItems().size());
    }

    @Test
    void test_getTabItems_properties() {
        assertEquals("Americas", modelUnderTest.getTabItems().get(0).getTitle());
        assertEquals(2, modelUnderTest.getTabItems().get(0).getLinkItems().size());
    }

}
