package com.softwareag.core.models.blogfooter;

import com.softwareag.core.models.blog.BlogFooterModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.ParseException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class BlogFooterModelTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private BlogFooterModel blogFooterModel;

    @BeforeEach
    public void setUp() throws ParseException {

        context.load().json("/components/blogfooter.json", "/content/softwareag");
        context.currentResource("/content/softwareag/jcr:content/parsys/blogfooter");

        blogFooterModel = Objects.requireNonNull(context.currentResource()).adaptTo(BlogFooterModel.class);

        assertThat(blogFooterModel).isNotNull();
    }

    @Test
    public void testHasContent() {

        assertThat(blogFooterModel.hasContent()).isEqualTo(true);
    }

    @Test
    public void testGetTitle() {

        assertThat(blogFooterModel.getTitle()).isEqualTo("Sync your Mainframe Data to the Cloud for Insights");
    }

    @Test
    public void testGetCopyText() {

        assertThat(blogFooterModel.getCopyText()).isEqualTo("The cloud offers innovations for the future.");
    }

    @Test
    public void testGetImage() {

        assertThat(blogFooterModel.getImage()).isEqualTo("/content/dam/softwareag-test/images/text-image/softwareag_hq_1000x667.png");
    }

    @Test
    public void testGetBlogAltText() {

        assertThat(blogFooterModel.getBlogAltText()).isEqualTo("Sync your Mainframe Data to the Cloud for Insights");
    }

    @Test
    public void testGetLink() {

        assertThat(blogFooterModel.getLink()).isNotNull();
        assertThat(blogFooterModel.getLink().getHref()).isEqualTo("https://softwareag.zoom.us/webinar/register/8615784921278/WN_2xmZH2tHRz-RJqcjxHEe0g");
        assertThat(blogFooterModel.getLink().getTarget()).isEqualTo("_self");
        assertThat(blogFooterModel.getLink().getText()).isNull();
    }
}
