package com.softwareag.core.models.parsys;

import com.softwareag.core.services.parsys.ParsysItemWrapper;
import com.softwareag.core.services.parsys.ParsysItemWrapperService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.softwareag.core.models.parsys.ParsysItemModel.REFERENCE_COMPONENT_RESOURCE_PATH;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class ParsysItemModelTest {

    private static final String ALFABET_PATH = "/content/softwareag/test/alfabet";
    private static final String ALFABET_PARSYS_PATH = ALFABET_PATH + "/jcr:content/parsys/gridcontainer_copy_196222524/container";
    private static final String PRODUCT_TEASER_NN = "productteaser";

    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    private final AemContext context = new AemContext();

    @Mock
    private ParsysItemWrapperService parsysItemWrapperService;
    @Mock
    private ParsysItemWrapper parsysItemWrapper;
    @InjectMocks
    private ParsysItemModel underTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/reference.json", "/content");
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.registerService(ParsysItemWrapperService.class, parsysItemWrapperService);
        context.registerService(ParsysItemWrapper.class, parsysItemWrapper);
        context.addModelsForPackage("com.softwareag.core");

        context.currentResource(ALFABET_PARSYS_PATH);
        final Resource productTeaserResource = Objects.requireNonNull(context.currentResource()).getChild(PRODUCT_TEASER_NN);
        context.request().setAttribute("itemResourcePath", Objects.requireNonNull(productTeaserResource).getPath());
        context.request().setAttribute("itemResourceType", productTeaserResource.getResourceType());
    }

    @Test
    void test_isDisplayed() {
        context.currentResource("/content/softwareag/test/jcr:content/parsys/reference_1");

        final Resource referenceModelResource = context.currentResource();
        context.request().setAttribute("itemResourcePath", Objects.requireNonNull(referenceModelResource).getPath());
        context.request().setAttribute("itemResourceType", referenceModelResource.getResourceType());
        when(parsysItemWrapperService.findParsysItemWrapper(any(), anyString(), anyString())).thenReturn(parsysItemWrapper);
        when(parsysItemWrapper.isDisplayed(any(), any(), anyString(), anyString())).thenReturn(true);

        underTest = Objects.requireNonNull(context.request()).adaptTo(ParsysItemModel.class);
        assertNotNull(underTest);
        assertTrue(underTest.isDisplayed());
        assertEquals("/content/softwareag/test/jcr:content/parsys/reference_1", context.request().getAttribute(REFERENCE_COMPONENT_RESOURCE_PATH));
    }

    /**
     * Asserts, if unwrap returns true, when no ParsysItemWrapper found during a ParsysItemModel initialization.
     */
    @Test
    void testIsUnwrapYes() {
        when(parsysItemWrapperService.findParsysItemWrapper(any(Resource.class), any(String.class), any(String.class))).thenReturn(null);

        underTest = Objects.requireNonNull(context.request()).adaptTo(ParsysItemModel.class);
        assertThat(underTest).isNotNull();

        assertTrue(underTest.isUnwrap());
    }

    /**
     * Asserts, if unwrap returns false, when a ParsysItemWrapper found during a ParsysItemModel initialization.
     */
    @Test
    void testIsUnwrapNo() {
        when(parsysItemWrapperService.findParsysItemWrapper(any(Resource.class), any(String.class), any(String.class))).thenReturn(parsysItemWrapper);

        underTest = Objects.requireNonNull(context.request()).adaptTo(ParsysItemModel.class);
        assertThat(underTest).isNotNull();

        assertFalse(underTest.isUnwrap());
    }

    /**
     * Asserts, if the wrapper tag attributes returned properly from the ParsysItemModel.
     */
    @Test
    void testGetTagAttributes() {
        final Map<String, Object> elementAttributes = new HashMap<>();
        elementAttributes.put("data-layout", "large");
        when(parsysItemWrapper.getWrapperElementAttributes(any(Resource.class), any(String.class), any(String.class))).thenReturn(elementAttributes);
        when(parsysItemWrapperService.findParsysItemWrapper(any(Resource.class), any(String.class), any(String.class))).thenReturn(parsysItemWrapper);

        underTest = Objects.requireNonNull(context.request()).adaptTo(ParsysItemModel.class);
        assertThat(underTest).isNotNull();

        assertFalse(underTest.isUnwrap());
        assertThat(underTest.getTagAttributes()).hasSize(1);
        assertThat(underTest.getTagAttributes()).containsKeys("data-layout");
        assertThat(underTest.getTagAttributes()).containsValues("large");
    }

}
