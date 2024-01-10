package com.softwareag.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class PageTest {

    private static final String TEMPLATE = "hero";
    private static final String TITLE_OF_PAGE = "Thank you for contacting us";

    private static final String PRODUCT_CATEGORY_TAG_TITLE = "Asset-type";
    private static final String PRODUCT_NAME_TAG_TITLE = "Event Type";
    private static final String PRODUCT_CAPABILITY_TAG_TITLE = "Industry";
    private static final String PRODUCT_USAGE_TAG_TITLE = "Banking";

    private static final String DEST_PATH = "/content/softwareag/language-master/en_us/answer";
    private static final String DEST_JCR_CONTENT_PATH = DEST_PATH + "/jcr:content";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    private final AemContext context = new AemContext();

    @InjectMocks
    private PageModel underTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/page/pagemodel.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.addModelsForPackage("com.softwareag.core.models");

        context.currentResource(DEST_JCR_CONTENT_PATH);
        //final Resource productTeaserResource = context.currentResource().getChild(PRODUCT_TEASER_NN);
    }

    @Test
    void testPageProperties() {
        underTest = Objects.requireNonNull(context.request()).adaptTo(PageModel.class);
        assertThat(underTest).isNotNull();

        assertEquals(TEMPLATE, underTest.getTemplateName());
        assertEquals(PRODUCT_CATEGORY_TAG_TITLE, underTest.getProductCategory());
        assertEquals(PRODUCT_NAME_TAG_TITLE, underTest.getProductName());
        assertThat(underTest.getProductCapabilities()).isNotNull().isNotEmpty().containsExactly(PRODUCT_CAPABILITY_TAG_TITLE);
        assertEquals(PRODUCT_USAGE_TAG_TITLE, underTest.getProductUsageLevel());
    }

}
