package com.softwareag.core.models.languagechanger;

import com.softwareag.core.models.regionchanger.RegionChangerModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class LanguageChangerModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";

    private final AemContext context = new AemContext();

    private LanguageChangerModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/languagechanger.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/languagechanger");

        modelUnderTest = context.request().getResource().adaptTo(LanguageChangerModel.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void test_hasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    void test_getTitle() {
        assertEquals("Language Changer title", modelUnderTest.getTitle());
    }

    @Test
    void test_getIntroductoryText() {
        assertEquals("Language Changer introductory text", modelUnderTest.getIntroductoryText());
    }

    @Test
    void test_getItems()
    {
        assertEquals(2, modelUnderTest.getLinkItems().size());
    }
}
