package com.softwareag.core.models.page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class SocialMediaConfigModelTest {

    public final AemContext context = new AemContext();

    private static String CONTEXT_PATH = "/content/softwareag";
    private static String IMAGE_PRESET_PATH = "/conf/global/settings/dam/dm/presets/macros/facebook-opengraph";

    @BeforeEach
    public void setUp() {
        context.load().json("/components/page/pagemeta.json", CONTEXT_PATH);
        context.addModelsForPackage("com.softwareag.core.models");
        context.currentResource("/content/softwareag/corporate/en_corporate/configuration/jcr:content/parsys/socialmedia");
    }

    @Test
    void testGetImagePresetWarning() {
        SocialMediaConfigModel underTest = context.currentResource().adaptTo(SocialMediaConfigModel.class);
        assertTrue(StringUtils.isNotEmpty(underTest.getImagePresetWarning()));
    }

    @Test
    void testGetNoImagePresetWarning() {
        context.load().json("/components/page/socialmediaconf.json", IMAGE_PRESET_PATH);
        SocialMediaConfigModel underTest = context.currentResource().adaptTo(SocialMediaConfigModel.class);
        assertTrue(StringUtils.isEmpty(underTest.getImagePresetWarning()));
    }

    @Test
    void testFacebookEnabled() {
        SocialMediaConfigModel underTest = context.currentResource().adaptTo(SocialMediaConfigModel.class);
        assertTrue(underTest.isEnabledFacebook());
    }

    @Test
    void testFacebookDefaultState() {
        context.currentResource("/content/softwareag/corporate/en_corporate/configuration-unconfigured/jcr:content/parsys/socialmedia");
        SocialMediaConfigModel underTest = context.currentResource().adaptTo(SocialMediaConfigModel.class);
        assertFalse(underTest.isEnabledFacebook());
    }
}
