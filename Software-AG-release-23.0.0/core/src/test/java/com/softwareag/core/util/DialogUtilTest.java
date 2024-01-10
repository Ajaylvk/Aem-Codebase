package com.softwareag.core.util;

import com.softwareag.core.models.accordion.AccordionContainer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class DialogUtilTest {

    private static final String DEST_PATH = "/content/softwareag/test/accordioncontainer";

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    private AccordionContainer modelUnderTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/accordion.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/accordioncontainer/panels/item0");

        modelUnderTest = Objects.requireNonNull(context.request()).adaptTo(AccordionContainer.class);

        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    void testGetContentResource_without_contentPath() {
        final Resource contentResource = DialogUtil.getContentResource(context.request());
        assertThat(contentResource).isNull();
    }

    @Test
    void testGetContentResource_with_contentPath() {
        context.request().setAttribute(DialogUtil.GRANITE_CONTENT_PATH, Objects.requireNonNull(context.currentResource()).getPath());

        final Resource contentResource = DialogUtil.getContentResource(context.request());
        assertThat(contentResource).isNotNull();
        assertThat(contentResource.getPath()).isEqualTo(Objects.requireNonNull(context.currentResource()).getPath());
    }

    @Test
    void testGetHiddenFields_from_non_existing_resource() {
        final List<String> hiddenFields = DialogUtil.getHiddenFields(context.request());
        assertThat(hiddenFields)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void testGetHiddenFields_from_existing_resource() {
        context.request().setAttribute(DialogUtil.GRANITE_CONTENT_PATH, DEST_PATH + "/jcr:content/parsys/accordioncontainer/panels/item1");

        final List<String> hiddenFields = DialogUtil.getHiddenFields(context.request());
        assertThat(hiddenFields)
            .isNotNull()
            .hasSize(18)
            .containsAll(
                Arrays.asList(
                    " type=\"hidden\"  name=\"jcr:primaryType@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"jcr:primaryType\"  value=\"nt:unstructured\" ",
                    " type=\"hidden\"  name=\"subtitle@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"subtitle\"  value=\"Panel subtitle 2\" ",
                    " type=\"hidden\"  name=\"titlePanel@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"titlePanel\"  value=\"Panel title 2\" ",
                    " type=\"hidden\"  name=\"parsys/jcr:primaryType@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"parsys/jcr:primaryType\"  value=\"nt:unstructured\" ",
                    " type=\"hidden\"  name=\"parsys/sling:resourceType@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"parsys/sling:resourceType\"  value=\"softwareag/components/content/parsys\" ",
                    " type=\"hidden\"  name=\"parsys/accordionitem/jcr:primaryType@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"parsys/accordionitem/jcr:primaryType\"  value=\"nt:unstructured\" ",
                    " type=\"hidden\"  name=\"parsys/accordionitem/sling:resourceType@TypeHint\"  value=\"String\" ",
                    " type=\"hidden\"  name=\"parsys/accordionitem/sling:resourceType\"  value=\"softwareag/components/content/accordionitem\" "
                )
            );
    }

}
