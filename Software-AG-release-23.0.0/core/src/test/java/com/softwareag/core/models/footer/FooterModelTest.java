package com.softwareag.core.models.footer;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class FooterModelTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private FooterModel footerModel;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/footercomponent.json", "/content/softwareag");
        context.currentResource("/content/softwareag/test/jcr:content/parsys/footer");

        footerModel = Objects.requireNonNull(context.request()).adaptTo(FooterModel.class);
        assertThat(footerModel).isNotNull();
    }

    @Test
    public void testGetConfigurationPath() {
        assertThat(footerModel.getConfigurationPath()).isEqualTo("/content/softwareag/config-page/jcr:content/parsys/footer");
    }
}
