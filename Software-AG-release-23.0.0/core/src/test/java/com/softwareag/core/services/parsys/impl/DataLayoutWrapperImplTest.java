package com.softwareag.core.services.parsys.impl;

import com.softwareag.core.models.reference.ReferenceModel;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.softwareag.core.models.contentitem.ContentItemModel.CONTENT_ITEM_RESOURCE_TYPE;
import static com.softwareag.core.models.parsys.ParsysItemModel.REFERENCE_COMPONENT_RESOURCE_PATH;
import static com.softwareag.core.models.reference.ReferenceModel.COMPONENT_TAGS;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class DataLayoutWrapperImplTest {

    public static final String PRODUCT_TEASER_RESOURCE_PATH = "productTeaserResourcePath";
    public static final String PRODUCT_TEASER_RESOURCE_TYPE = "productTeaserResourceType";
    public static final String CONTENT_ITEM_RESOURCE_PATH = "contentItemResourcePath";

    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Resource parsysResource;
    @Mock
    private Resource productTeaserResource;
    @Mock
    private Resource contentItemResource;
    @Mock
    private Resource referenceModelResource;
    @Mock
    private SlingHttpServletRequest request;
    @Mock
    private ReferenceModel referenceModel;
    @InjectMocks
    private DataLayoutWrapperImpl underTest;

    @Test
    void test_IsDisplayed() {
        when(parsysResource.getResourceResolver()).thenReturn(resourceResolver);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(CONTENT_ITEM_RESOURCE_PATH)).thenReturn(contentItemResource);
        when(resourceResolver.getResource(REFERENCE_COMPONENT_RESOURCE_PATH)).thenReturn(referenceModelResource);
        when(contentItemResource.isResourceType(DataLayoutWrapperImpl.DATA_LAYOUT_RESOURCE_TYPES[1])).thenReturn(true);

        when(request.getAttribute(REFERENCE_COMPONENT_RESOURCE_PATH)).thenReturn(REFERENCE_COMPONENT_RESOURCE_PATH);

        when(referenceModelResource.adaptTo(ReferenceModel.class)).thenReturn(referenceModel);
        final ValueMap valueMap = mock(ValueMap.class);
        when(contentItemResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(COMPONENT_TAGS, String[].class)).thenReturn(new String[]{"asd"});
        when(referenceModel.matchesTag(any())).thenReturn(true);

        assertTrue(underTest.isDisplayed(request, parsysResource, CONTENT_ITEM_RESOURCE_PATH, CONTENT_ITEM_RESOURCE_TYPE));
    }

    @Test
    void test_MatchesTrue() {
        when(parsysResource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(PRODUCT_TEASER_RESOURCE_PATH)).thenReturn(productTeaserResource);
        when(productTeaserResource.isResourceType(DataLayoutWrapperImpl.DATA_LAYOUT_RESOURCE_TYPES[0])).thenReturn(true);

        assertTrue(underTest.matches(parsysResource, PRODUCT_TEASER_RESOURCE_PATH, PRODUCT_TEASER_RESOURCE_TYPE));
    }

    @Test
    void test_MatchesFalse() {
        when(parsysResource.getResourceResolver()).thenReturn(resourceResolver);
        final Resource dummyResource = mock(Resource.class);
        when(resourceResolver.getResource("dummyResourcePath")).thenReturn(dummyResource);
        when(dummyResource.isResourceType(DataLayoutWrapperImpl.DATA_LAYOUT_RESOURCE_TYPES[0])).thenReturn(false);
        assertFalse(underTest.matches(parsysResource, "dummyResourcePath", "wrongType"));
    }

    @Test
    void get_WrapperElementAttributes_with_default_layout() {
        when(parsysResource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(PRODUCT_TEASER_RESOURCE_PATH)).thenReturn(productTeaserResource);

        Map<String, Object> valueMap = new HashMap<>();
        MockValueMap mockValueMap = new MockValueMap(productTeaserResource, valueMap);
        when(productTeaserResource.getValueMap()).thenReturn(mockValueMap);

        final Map<String, Object> wrapperElementAttributes = underTest.getWrapperElementAttributes(parsysResource, PRODUCT_TEASER_RESOURCE_PATH,
            PRODUCT_TEASER_RESOURCE_TYPE);

        assertThat(wrapperElementAttributes)
            .isNotEmpty()
            .containsKeys(DataLayoutWrapperImpl.LAYOUT_ATTRIBUTE_NAME)
            .containsValues(DataLayoutWrapperImpl.DEFAULT_LAYOUT);
    }

    @Test
    void get_WrapperElementAttributes() {
        when(parsysResource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource(PRODUCT_TEASER_RESOURCE_PATH)).thenReturn(productTeaserResource);

        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(DataLayoutWrapperImpl.LAYOUT_PN, "large");
        MockValueMap mockValueMap = new MockValueMap(productTeaserResource, valueMap);
        when(productTeaserResource.getValueMap()).thenReturn(mockValueMap);

        final Map<String, Object> wrapperElementAttributes = underTest.getWrapperElementAttributes(parsysResource, PRODUCT_TEASER_RESOURCE_PATH,
            PRODUCT_TEASER_RESOURCE_TYPE);

        assertThat(wrapperElementAttributes)
            .isNotEmpty()
            .containsKeys(DataLayoutWrapperImpl.LAYOUT_ATTRIBUTE_NAME)
            .containsValues("large");
    }

}
