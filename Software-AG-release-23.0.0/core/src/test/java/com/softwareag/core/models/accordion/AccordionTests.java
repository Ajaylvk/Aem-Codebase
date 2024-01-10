
package com.softwareag.core.models.accordion;

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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class AccordionTests {

    private static final String DEST_PATH = "/content/softwareag/test/accordioncontainer";

    private final AemContext context = new AemContext();

    private AccordionContainer modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/accordion.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/accordioncontainer");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(AccordionContainer.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testHasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    void testGetTitle() {
        assertEquals("Accordion container", modelUnderTest.getTitle());
    }

    @Test
    void testGetDesign() {
        assertEquals("accordion__container--dark", modelUnderTest.getDesign());
    }

    @Test
    void testGetAnchorName() {
        assertEquals("container", modelUnderTest.getAnchorName());
    }

    @Test
    void testGetPanelResources() {
        List<Resource> list = modelUnderTest.getPanelResources();
        assertEquals(2, list.size());
    }

    @Test
    void testAccPanelProperties() {
        AccordionPanel accordionPanel = modelUnderTest.getPanelResources().get(0).adaptTo(AccordionPanel.class);
        String title = Objects.requireNonNull(accordionPanel).getTitlePanel();
        String subtitle = Objects.requireNonNull(accordionPanel).getSubtitle();

        assertEquals("Panel title 1", title);
        assertEquals("Panel subtitle 1", subtitle);
    }

    @Test
    void testAccItemProperties() {
        context.currentResource(DEST_PATH + "/jcr:content/parsys/accordioncontainer/panels/item0/parsys/accordionitem");
        AccordionItem accordionItem = Objects.requireNonNull(context.currentResource()).adaptTo(AccordionItem.class);

        assertEquals("<b><i>This accordion item text. When expanded it is visible, otherwise unvisible.</i></b>",
            Objects.requireNonNull(accordionItem).getText());
        assertEquals("anchorName", accordionItem.getAnchorName());
    }
}
