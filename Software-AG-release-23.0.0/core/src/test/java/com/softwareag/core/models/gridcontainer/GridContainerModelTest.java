package com.softwareag.core.models.gridcontainer;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class GridContainerModelTest {

    private static final String PARSYS_PATH = "/content/jcr:content/parsys/";

    public final AemContext ctx = new AemContext();

    @BeforeEach
    public void setUp() {
        ctx.load().json("/components/gridcontainer.json", PARSYS_PATH);
    }

    @Test
    void testAllProperties() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertNotNull(Objects.requireNonNull(modelUnderTest).getBackgroundStyle());
        assertNotNull(modelUnderTest.getDesign());
        assertNotNull(modelUnderTest.getFontStyle());
        assertNotNull(modelUnderTest.getLink());
        assertNotNull(modelUnderTest.getAnchorName());
        assertNotNull(modelUnderTest.getText());
        assertNotNull(modelUnderTest.getTitle());
        assertNotNull(modelUnderTest.getGridLayout());
    }

    @Test
    void testHasNoContent() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-empty");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertFalse(Objects.requireNonNull(modelUnderTest).hasContent());
    }

    @Test
    void testHasContentTitle() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-hastitle");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertTrue(Objects.requireNonNull(modelUnderTest).hasContent());
    }

    @Test
    void testHasContentText() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-hastext");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertTrue(Objects.requireNonNull(modelUnderTest).hasContent());
    }

    @Test
    void testHasContentLink() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-haslink");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertTrue(Objects.requireNonNull(modelUnderTest).hasContent());
    }

    @Test
    void testHasContentComponent() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-hassubcomponent");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertTrue(Objects.requireNonNull(modelUnderTest).hasContent());
    }

    @Test
    void testHasContentTargetName() {
        ctx.currentResource(PARSYS_PATH + "gridcontainer-hasanchorname");
        GridContainerModel modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(GridContainerModel.class);
        assertEquals("AnchorName", Objects.requireNonNull(modelUnderTest).getAnchorName());
    }
}
