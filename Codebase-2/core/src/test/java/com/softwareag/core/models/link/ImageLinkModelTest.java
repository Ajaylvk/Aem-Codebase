package com.softwareag.core.models.link;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class ImageLinkModelTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private static ImageLinkModel imageLinkModel;

    @BeforeEach
    public void setUp() throws RepositoryException {
        context.load().json("/components/imagelink.json", "/content");

        context.currentResource("/content/imageLink");
        imageLinkModel = Objects.requireNonNull(context.currentResource()).adaptTo(ImageLinkModel.class);

        assertThat(imageLinkModel).isNotNull();
    }

    @Test
    public void testImage() {

        assertThat(imageLinkModel.getImage()).isEqualTo("/content/dam/softwareag/test-image.jpg");
    }

    @Test
    public void testAltText() {

        assertThat(imageLinkModel.getAltText()).isEqualTo("Alternative Text 01");
    }

    @Test
    public void testHref() {

        assertThat(imageLinkModel.getHref()).isEqualTo("/content/softwareag/test-page.html");
    }

    @Test
    public void testLink() {

        assertThat(imageLinkModel.getLink()).isNotNull();
        assertThat(imageLinkModel.getLink().getHref()).isEqualTo("/content/softwareag/test-page.html");
    }
}
