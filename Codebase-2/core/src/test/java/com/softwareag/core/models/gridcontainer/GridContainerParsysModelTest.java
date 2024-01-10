package com.softwareag.core.models.gridcontainer;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(AemContextExtension.class)
class GridContainerParsysModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/alfabet";
    private static final String MAIN_PARSYS_PATH = DEST_PATH + "/jcr:content/parsys/";
    private static final String PRODUCT_TEASER_PARSYS_NN = "container";
    //private static final String PRODUCT_TEASER_NN = "productteaser";

    private final AemContext context = new AemContext();

    private GridContainerParsysModel underTest;

    @BeforeEach
    void setUp() {
        context.load().json("/components/productteaser.json", DEST_PATH);
        context.addModelsForPackage("com.softwareag.core.models");
    }


    @Test
    void testGetAdditionalClassesWithEmptyParsys() {
        context.currentResource(MAIN_PARSYS_PATH + "gridcontainer_empty");
        context.request().setAttribute("parsysResourcePath", "nonexistingparsys");

        underTest = Objects.requireNonNull(context.request()).adaptTo(GridContainerParsysModel.class);
        assertThat(underTest.getAdditionalClasses()).isEqualTo("");
    }

    @Test
    void testGetAdditionalClassesWithParsys() {
        context.currentResource(MAIN_PARSYS_PATH + "gridcontainer_copy_196222524");
        context.request().setAttribute("parsysResourcePath", PRODUCT_TEASER_PARSYS_NN);

        underTest = Objects.requireNonNull(context.request()).adaptTo(GridContainerParsysModel.class);
        assertThat(underTest.getAdditionalClasses()).isEqualTo(" " + GridContainerParsysModel.PARSYS_NO_PADDING_CLASS);
    }


}
