package com.softwareag.core.models.inpagenavigation;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class InPageNavigationModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/inpagenavigation";

    private final AemContext context = new AemContext();

    private InPageNavigationModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/inpagenavigation.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/inpagenavigation");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(InPageNavigationModel.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testHasContent() {
        assertTrue(modelUnderTest.hasContent());
    }

    @Test
    void testGetTitle() {
        List<InPageNavigationItem> list = modelUnderTest.getNavigationItems();
        assertEquals(3, list.size());
    }

    @Test
    void testGetInPageNavigationItemTitle() {
        InPageNavigationItem navigationItem = modelUnderTest.getNavigationItems().get(0);
        assertEquals("In-page item 1 - Text component", navigationItem.getTitle());
    }

    @Test
    void testGetInPageNavigationItemJumpDestination() {
        InPageNavigationItem navigationItem = modelUnderTest.getNavigationItems().get(0);
        assertEquals("#item1", navigationItem.getJumpDestination());
    }
}
