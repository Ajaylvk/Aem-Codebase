package com.softwareag.core.models.socialmediabar;

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
public class SocialMediaBarTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private static SocialMediaBarModel socialMediaBarModel;

    @BeforeEach
    public void setUp() throws RepositoryException {
        context.load().json("/components/socialmediabar.json", "/content/softwareag/test");

        context.currentResource("/content/softwareag/test/jcr:content/parsys/socialmediabar");
        socialMediaBarModel = Objects.requireNonNull(context.currentResource()).adaptTo(SocialMediaBarModel.class);
        assertThat(socialMediaBarModel).isNotNull();
    }

    @Test
    public void testSocialMediaItems() {
        assertThat(socialMediaBarModel.getSocialMediaItems()).isNotNull();
    }

    @Test
    public void testSocialMediaItemsItem() {
        assertThat(socialMediaBarModel.getSocialMediaItems()).isNotNull();
        assertThat(socialMediaBarModel.getSocialMediaItems().get(0)).isNotNull();
    }
}
