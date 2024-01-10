package com.softwareag.core.models.tab;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class TabModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/tabcomponent";

    private final AemContext context = new AemContext();

    private TabModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/tabcomponent.json", DEST_PATH);
        context.addModelsForPackage("com.softwareag.core.models");
        context.currentResource(DEST_PATH + "/jcr:content/parsys/tabcomponent");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(TabModel.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testHasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    void testGetDesign() {
        assertEquals("tab__container--dark", modelUnderTest.getDesign());
    }

  /*  @Test
    void testGetAnchorName() {
        assertNull(modelUnderTest.getAnchorName());
    } */

    @Test
    void testGetPanelResources() {
        List<Resource> list = modelUnderTest.getPanelResources();
        assertEquals(2, list.size());
    }

    @Test
    void testGetPanelResourcesTitle() {
        List<Resource> list = modelUnderTest.getPanelResources();
        TabPanel tabPanel = list.get(0).adaptTo(TabPanel.class);

        assertEquals("1st panel", Objects.requireNonNull(tabPanel).getTitle());
    }
}
