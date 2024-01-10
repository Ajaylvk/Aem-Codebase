package com.softwareag.core.models.download;

import com.softwareag.core.servlets.download.DownloadServlet;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class DownloadModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";
    private static final String ASSET_NAME = "test.png";
    private static final String ASSET_RESOURCE_PATH = "/content/dam/softwareag/" + ASSET_NAME;
    private static final String ASSET_FORMAT = "image/png";
    private static final long ASSET_SIZE = 1000L;
    private static final long ASSET_LAST_MODIFIED = 12345678L;

    private final AemContext ctx = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private AssetModel assetModel;

    @InjectMocks
    private DownloadModel modelUnderTest;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        ctx.load().json("/components/download.json", DEST_PATH);

        Resource assetResource = mock(Resource.class);
        when(resourceResolver.getResource(ASSET_RESOURCE_PATH)).thenReturn(assetResource);
        when(assetResource.adaptTo(AssetModel.class)).thenReturn(assetModel);

        modelUnderTest = getModel(DEST_PATH + "/jcr:content/parsys/download");
    }

    @Test
    public void testGetTitle() {
        assertThat(modelUnderTest.getTitle()).isEqualTo("Download Title");
    }

    @Test
    public void testGetDescription() {
        assertThat(modelUnderTest.getDescription()).isEqualTo("Download Description");
    }

    @Test
    public void testGetActionText() {
        assertThat(modelUnderTest.getActionText()).isEqualTo("Download");
    }

    @Test
    public void testGetFilename() {
        when(assetModel.getFilename()).thenReturn(ASSET_NAME);
        assertThat(modelUnderTest.getFilename()).isEqualTo("test.png");
    }

    @Test
    public void testGetFormat() {
        when(assetModel.getFormat()).thenReturn(ASSET_FORMAT);
        assertThat(modelUnderTest.getFormat()).isEqualTo("image/png");
    }

    @Test
    public void testGetSize() {
        when(assetModel.getSize()).thenReturn(FileUtils.byteCountToDisplaySize(ASSET_SIZE));
        assertThat(modelUnderTest.getSize()).isEqualTo(ASSET_SIZE + " bytes");
    }

    @Test
    public void testGetUrl() {
        final String url = ASSET_RESOURCE_PATH + "." + DownloadServlet.SELECTOR + "." +
            DownloadServlet.INLINE_SELECTOR + "." + ASSET_LAST_MODIFIED + ".png";
        when(assetModel.getUrl()).thenReturn(url);
        assertThat(modelUnderTest.getUrl()).isEqualTo(url);
    }

    @Test
    public void testGetAnchorName() {
        assertThat(modelUnderTest.getAnchorName()).isEqualTo("anchor");
    }

    @Test
    public void testHasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    public void testIsDisplayFileSize() {
        assertTrue(modelUnderTest.isDisplayFileSize());
    }

    @Test
    public void testIsDisplayFileFormat() {
        assertTrue(modelUnderTest.isDisplayFileFormat());
    }

    @Test
    public void testIsDisplayFilename() {
        assertTrue(modelUnderTest.isDisplayFilename());
    }

    @Test
    public void testIsInline() {
        assertTrue(modelUnderTest.isInline());
    }

    private DownloadModel getModel(final String resourcePath) throws IllegalAccessException {
        ctx.currentResource(resourcePath);

        DownloadModel modelUnderTest = Objects.requireNonNull(ctx.request()).adaptTo(DownloadModel.class);
        FieldUtils.writeField(modelUnderTest, "resourceResolver", resourceResolver, true);
        modelUnderTest.init();

        return modelUnderTest;
    }
}
