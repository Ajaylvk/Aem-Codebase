package com.softwareag.core.models.copyright;

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
public class CopyrightModelTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private static CopyrightModel copyrightModel;

    @BeforeEach
    public void setUp() throws RepositoryException {
        context.load().json("/components/copyright.json", "/content/softwareag/test");
        context.currentResource("/content/softwareag/test/jcr:content/parsys/copyright");

        copyrightModel = Objects.requireNonNull(context.currentResource()).adaptTo(CopyrightModel.class);
        assertThat(copyrightModel).isNotNull();
    }

    @Test
    public void textCopyrightInformation() {
        assertThat(copyrightModel.getCopyrightInformation()).isEqualTo("Software AG All Rights Reserved.");
    }

    @Test
    public void textCookieConsentInformation() {
        assertThat(copyrightModel.getCookieConsentInformation()).isEqualTo("Cookie Preferences");
    }

    @Test
    public void testLinkItems() {
        assertThat(copyrightModel.getLinkItems()).isNotNull();
    }
}
