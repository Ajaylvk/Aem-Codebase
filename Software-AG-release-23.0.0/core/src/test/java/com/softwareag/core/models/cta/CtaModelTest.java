package com.softwareag.core.models.cta;

import com.softwareag.core.models.download.AssetModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CtaModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";

    private final AemContext ctx = new AemContext();

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private AssetModel assetModel;

    @InjectMocks
    private CtaModel modelWithLink;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        ctx.load().json("/components/cta.json", DEST_PATH);
        Resource assetResource = mock(Resource.class);
        when(resourceResolver.getResource("/content/dam/softwareag/test.png")).thenReturn(assetResource);
        when(assetResource.adaptTo(AssetModel.class)).thenReturn(assetModel);
        when(assetModel.getUrl()).thenReturn("/content/dam/softwareag/test.png?utm");
        this.modelWithLink = getModel(DEST_PATH + "/jcr:content/parsys/cta_with_link");
    }

    @Test
    void isValid_shouldReturnTrue_whenLinkIsValid() {
        assertThat(this.modelWithLink.isValid()).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenNoValidLinkIsGiven() throws IllegalAccessException {
        final CtaModel model1 = getModel(DEST_PATH + "/jcr:content/parsys/cta_without_link");
        final CtaModel model2 = getModel(DEST_PATH + "/jcr:content/parsys/cta_invalid_link");
        assertThat(model1.isValid()).isFalse();
        assertThat(model2.isValid()).isFalse();
    }

    @Test
    void getLink_shouldReturnGivenLink() {
        assertThat(this.modelWithLink.getLink()).isNotNull();
    }

    @Test
    void getDesign_shouldReturnGivenValue() {
        assertThat(this.modelWithLink.getDesign()).isEqualTo("link");
    }

    @Test
    void testGetText() {
        assertEquals("Learn more", modelWithLink.getLink().getText());
    }

    @Test
    void testGetTarget() {
        assertEquals("_self", modelWithLink.getLink().getTarget());
    }

    @Test
    void testGetHref() {
        assertEquals("/content/dam/softwareag/test.png?utm", modelWithLink.getHref());
    }

    @Test
    void testGetLastModified() {
        assertEquals(0L, modelWithLink.getLastModified());
    }

    @Test
    void testIsInline() {
        assertTrue(modelWithLink.isInline());
    }

    private CtaModel getModel(final String resourcePath) throws IllegalAccessException {
        ctx.currentResource(resourcePath);
        modelWithLink = Objects.requireNonNull(ctx.currentResource()).adaptTo(CtaModel.class);
        FieldUtils.writeField(Objects.requireNonNull(modelWithLink), "resourceResolver", resourceResolver, true);
        modelWithLink.init();

        return modelWithLink;
    }

}
