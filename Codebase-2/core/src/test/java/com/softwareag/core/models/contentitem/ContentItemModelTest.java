package com.softwareag.core.models.contentitem;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContentItemModelTest {

    private static final String DEST_PATH = "/content/softwareag/test";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    private final AemContext context = new AemContext();

    private ContentItemModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/filtercontainer.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/filtercontainer/container/contentitem");
        context.addModelsForPackage("com.softwareag.core.models");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(ContentItemModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetTags() {
        assertEquals("banking", modelUnderTest.getFilter1());
        assertEquals("england", modelUnderTest.getFilter2());
        assertEquals("image", modelUnderTest.getFilter3());
    }

}
