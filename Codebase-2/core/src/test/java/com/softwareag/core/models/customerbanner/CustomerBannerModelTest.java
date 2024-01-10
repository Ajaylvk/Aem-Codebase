package com.softwareag.core.models.customerbanner;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class CustomerBannerModelTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private CustomerBannerModel customerBannerModel;

    @BeforeEach
    public void setUp() {

        context.load().json("/components/customerbanner.json", "/content/softwareag/test");
        context.currentResource("/content/softwareag/test/jcr:content/parsys/customerbanner");
        customerBannerModel = Objects.requireNonNull(context.currentResource()).adaptTo(CustomerBannerModel.class);

        assertThat(customerBannerModel).isNotNull();
    }

    @Test
    public void testGetTitle() {

        assertThat(customerBannerModel.getTitle()).isEqualTo("Software AG Customers");
    }

    @Test
    public void testGetBackgroundStyle() {

        assertThat(customerBannerModel.getBackgroundStyle()).isNotNull();
    }

    @Test
    public void testGetFontStyle() {

        assertThat(customerBannerModel.getFontStyle()).isNotNull();
    }

    @Test
    public void testGetItems() {

        assertThat(customerBannerModel.getItems()).isNotNull();
        assertThat(customerBannerModel.hasContent()).isTrue();
    }

    @Test
    public void testGetAnchorName() {

        assertEquals("anchor", customerBannerModel.getAnchorName());
    }
}
