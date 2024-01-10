package com.softwareag.core.models.asset;

import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class RelatedAssetTest {

    private static final String DEST_PATH = "/content/softwareag/test/asset";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    @Mock
    private LanguageService languageService;
    private final AemContext context = new AemContext();

    private RelatedAsset modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/relatedasset.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.currentResource(DEST_PATH + "/assetimage/jcr:content/relatedassetcomponent");
        context.registerService(languageService);
        context.addModelsForPackage("com.softwareag.core.models");
        modelUnderTest = Objects.requireNonNull(context.request()).getResource().adaptTo(RelatedAsset.class);
        assertNotNull(modelUnderTest);
    }

    @Test
    void testGetAllCombinations() {
        assertEquals(7, modelUnderTest.getAllCombinations().size());
    }

    @Test
    void testGetPageTags() {
        assertEquals(3, modelUnderTest.getPageTags().length);
    }

    @Test
    void testGetPageId() {
        assertEquals("d79906cc-0d82-49f8-bd0b-bd29025c7433", modelUnderTest.getPageId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Related Asset", modelUnderTest.getTitle());
    }

    @Test
    void testGetPath() {
        assertEquals("/content/softwareag/test/asset", modelUnderTest.getPath());
    }

    @Test
    void testGetAssets() {
        assertEquals(0, modelUnderTest.getAssets().size());
    }

    @Test
    void testHasAssets() {
        assertEquals(false, modelUnderTest.hasAssets());
    }
}


