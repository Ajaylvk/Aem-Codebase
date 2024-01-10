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
class AssetComponentModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/asset";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    @Mock
    private LanguageService languageService;
    private final AemContext context = new AemContext();
    private final AemContext imageContext = new AemContext();

    private AssetComponentModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/assetcomponent.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/assetcomponent");
        context.addModelsForPackage("com.softwareag.core.models");
        context.registerService(languageService);
        modelUnderTest = Objects.requireNonNull(context.request()).getResource().adaptTo(AssetComponentModel.class);
        assertNotNull(modelUnderTest);
    }

    public void setUpImage() {
        imageContext.load().json("/components/assetcomponent.json", DEST_PATH);
        imageContext.load().json("/tags/tagList.json", TAGS_PATH);
        imageContext.currentResource(DEST_PATH + "/jcr:content/media/jcr:content/metadata");
        imageContext.addModelsForPackage("com.softwareag.core.models");
        imageContext.registerService(languageService);
        modelUnderTest = Objects.requireNonNull(imageContext.request()).getResource().adaptTo(AssetComponentModel.class);
        assertNotNull(modelUnderTest);
    }

    @Test
    void testGetAssetType() {
        assertEquals("softwareag:asset-type/image", modelUnderTest.getAssetType());
    }

    @Test
    void testGetTagBusinessUnit() {
        assertEquals("softwareag:product-categories/integration", modelUnderTest.getTagBusinessUnit());
    }

    @Test
    void testGetTagProduct() {
        assertEquals("softwareag:product-brand-names/webmethods", modelUnderTest.getTagProduct());
    }

    @Test
    void testGetTagsCapabilities() {
        assertEquals(2, modelUnderTest.getTagsCapabilities().length);
    }

    @Test
    void testGetInnerValue() {
        setUpImage();
        assertEquals("Integration concept ( image )",
            modelUnderTest.getInnerValue(Objects.requireNonNull(imageContext.currentResource()).getValueMap(), "dc:title"));
    }

    @Test
    void testGetInnerTagsAndCollectAllTags() {
        setUpImage();
        modelUnderTest.getInnerTags(Objects.requireNonNull(imageContext.currentResource()).getValueMap());
        assertEquals("", modelUnderTest.getAssetTypeTag());
        assertEquals("", modelUnderTest.getBusinessUnitTag());
        assertEquals("", modelUnderTest.getProductTag());
        assertEquals(1, modelUnderTest.getCapabilitiesTag().length);
    }

    @Test
    void testCollectAllTags() {
        assertEquals(5, modelUnderTest.collectAllTags().length);
    }
}
