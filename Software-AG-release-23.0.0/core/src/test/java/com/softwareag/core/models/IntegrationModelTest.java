package com.softwareag.core.models;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class IntegrationModelTest {

    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        context.load().json("/components/integration.json", "/content/softwareag/test");
    }


    @Test
    void testIDNoExistingId() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/noexisting-id");
        assertEquals("noexisting-id-958669137",componentModel.getId());
    }

    @Test
    void testIDExistsEmpty() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/emptyexisting-id");
        assertEquals("emptyexisting-id-178509371",componentModel.getId());
    }

    @Test
    void testIdExistingId() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/existingid");
        assertEquals("some-saved-id",componentModel.getId());
    }

    @Test
    void testEnabledWithMBox() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/noexisting-id");
        assertEquals(true,componentModel.getTargetThisComponent());
        assertEquals("test/tag",componentModel.getMbox());
    }

    @Test
    void testDisabledWithMBox() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/noexisting-id-disabled");
        assertEquals(false,componentModel.getTargetThisComponent());
        assertEquals("test/tag",componentModel.getMbox());
    }

    @Test
    void testUnconfigured() {
        IntegrationModel componentModel = createModel("/content/softwareag/test/jcr:content/parsys/unconfigured");
        assertEquals(false,componentModel.getTargetThisComponent());
    }

    @Test
    void testUniqueByPathAndName() {
        IntegrationModel componentModel1 = createModel("/content/softwareag/test/jcr:content/parsys/noexisting-id");
        IntegrationModel componentModel2 = createModel("/content/softwareag/test/jcr:content/parsys2/noexisting-id");
        IntegrationModel componentModel3 = createModel("/content/softwareag/test/jcr:content/parsys2/noexisting-id2");
        String id1 = componentModel1.getId();
        String id2 = componentModel2.getId();
        String id3 = componentModel3.getId();
        assertTrue(!id1.equals(id2) && !id3.equals(id1) && !id3.equals(id2));
    }

    private IntegrationModel createModel(String componentPath) {
        Resource componentResource = context.resourceResolver().getResource(componentPath);
        return componentResource.adaptTo(IntegrationModel.class);
    }
}
