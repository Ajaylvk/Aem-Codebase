package com.softwareag.core.models.download;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AssetModelTest {

    private static final String ASSET_NAME = "test.png";
    private static final String ASSET_RESOURCE_PATH = "/content/dam/softwareag/" + ASSET_NAME;

    private final AemContext ctx = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private MimeTypeService mimeTypeService;

    @InjectMocks
    private AssetModel modelUnderTest;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        ctx.load().json("/components/asset.json", ASSET_RESOURCE_PATH);
        ctx.registerService(mimeTypeService);

        modelUnderTest = getModel(ASSET_RESOURCE_PATH);
    }

    @Test
    void getPath() {
        assertThat(modelUnderTest.getPath()).isEqualTo("/content/dam/softwareag/test.png");
    }

    @Test
    void getTitle() {
        assertThat(modelUnderTest.getTitle()).isEqualTo("Rocks Downhill");
    }

    @Test
    void getDescription() {
        assertThat(modelUnderTest.getDescription()).isEqualTo("Rider in action at Freestyle Mountain Bike Session");
    }

    @Test
    void getFilename() {
        assertThat(modelUnderTest.getFilename()).isEqualTo("test.png");
    }

    @Test
    void getFormat() {
        assertThat(modelUnderTest.getFormat()).isEqualTo("image/jpeg");
    }

    @Test
    void getSize() {
        assertThat(modelUnderTest.getSize()).isEqualTo("325 KB");
    }

    @Test
    void getExtension() {
        assertThat(modelUnderTest.getExtension()).isEqualTo("jpeg");
    }

    @Test
    void getLastModified() {
        assertThat(modelUnderTest.getLastModified()).isEqualTo(1473680806000L);
    }

    @Test
    void setUrl() {
        modelUnderTest.setUrl("abc");
        assertThat(modelUnderTest.getUrl()).isEqualTo("abc");
    }

    @Test
    void getUrl() {
        assertThat(modelUnderTest.getUrl()).isNull();
    }

    private AssetModel getModel(final String resourcePath) throws IllegalAccessException {
        ctx.currentResource(resourcePath);

        AssetModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(AssetModel.class);
        FieldUtils.writeField(modelUnderTest, "resourceResolver", resourceResolver, true);
        modelUnderTest.init();

        return modelUnderTest;
    }
}
