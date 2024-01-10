package com.softwareag.core.models.title;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class TitleModelTest {

    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.load().json("/components/title.json", "/content/softwareag/test");
        context.currentPage("/content/softwareag/test");
    }

    @Test
    void testGetTitleNoTitleProperty() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title-without-property");
        assertEquals("Test Page Title", titleComponentModel.getTitle());
    }

    @Test
    void testGetTitleComponentProperty() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title-with-property");
        assertEquals("A Title Property", titleComponentModel.getTitle());
    }

    @Test
    public void testHasContent() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title");
        assertTrue(titleComponentModel.hasContent());
    }

    @Test
    public void testGetPosition() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title");
        assertEquals("left", titleComponentModel.getPosition());
    }

    @Test
    public void testGetCopyText() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title");
        assertEquals("A title with copyText", titleComponentModel.getCopyText());
    }

    @Test
    public void testGetBackgroundStyle() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title");
        assertThat(titleComponentModel.getBackgroundStyle()).isNotNull();
    }

    @Test
    public void testGetFontStyle() {
        TitleModel titleComponentModel = createTitleModel("/content/softwareag/test/jcr:content/parsys/title");
        assertThat(titleComponentModel.getFontStyle()).isNotNull();
    }

    private TitleModel createTitleModel(String componentPath) {
        Resource titleResource = context.resourceResolver().getResource(componentPath);
        context.request().setResource(titleResource);
        return context.request().adaptTo(TitleModel.class);
    }
}
