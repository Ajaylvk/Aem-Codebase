package com.softwareag.core.models.reference;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static com.softwareag.core.models.reference.ReferenceModel.COMPONENT_TAGS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ReferenceModelTest {

    private static final String DEST_PATH = "/content";
    private static final String TAGS_PATH = "/content/cq:tags/softwareag";

    private final AemContext context = new AemContext();
    private ReferenceModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/reference.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_PATH);
        context.currentResource(DEST_PATH + "/softwareag/test/jcr:content/parsys/reference_1");
        context.addModelsForPackage("com.softwareag.core.models");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(ReferenceModel.class);
        assertNotNull(modelUnderTest);
    }

   

}
