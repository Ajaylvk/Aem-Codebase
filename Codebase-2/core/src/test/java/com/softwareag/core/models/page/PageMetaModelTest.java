package com.softwareag.core.models.page;

import com.day.cq.commons.Externalizer;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.services.language.PageInternationalVersion;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PageMetaModelTest {

    private static final String CONTEXT_PATH = "/content/softwareag";
    private static final String HOME_PATH = "/corporate/en_corporate";
    private static final String NOT_CONFIGURED_HOME_PATH = "/page_no_config";
    private static final String IMAGE_PRESET_PATH = "/conf/global/settings/dam/dm/presets/macros/facebook-opengraph";
    private static final String SOCIAL_MEDIA_IMAGE_PATH = "https://softwareag.scene7.com/is/image/softwareaglocaldevelop1/facebook-1?$facebook-opengraph$";

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Mock
    private Externalizer externalizer;

    @Mock
    private LanguageService languageService;

    private PageMetaModel underTest;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/page/pagemeta.json", CONTEXT_PATH);
        context.registerService(Externalizer.class, externalizer);
        context.addModelsForPackage("com.softwareag.core.models");
        context.registerService(languageService);
    }

    /* Social Media Opengraph tests */

    @Test
    void testGetWebsiteMetadataProvider() {
        context.load().json("/components/page/socialmediaconf.json", IMAGE_PRESET_PATH);
        context.currentResource(CONTEXT_PATH + HOME_PATH);
        runSocialMediaTest(context, SOCIAL_MEDIA_IMAGE_PATH);
    }

    @Test
        /* Uses content with no configuration component.  We expect the model to work as normal, because the config component only provides a condition to HTL
         * The model should continue to provide a config(empty POJO with default enabled/disabled) and WebsiteMetaDataProvider */
    void testNoConfig() {
        context.currentResource(CONTEXT_PATH + NOT_CONFIGURED_HOME_PATH);
        runSocialMediaTest(context, "");
    }

    @Test
    void test_getCurrentPageLanguageVersion() {
        context.currentResource(CONTEXT_PATH + HOME_PATH);
        underTest = context.request().adaptTo(PageMetaModel.class);

        PageMetaModel.WebsiteMetadataProvider provider = Objects.requireNonNull(underTest).getWebsiteMetadataProvider();
        final List<PageInternationalVersion> pageLanguageVersionList = provider.getCurrentPageLanguageVersions();
        assertNotNull(pageLanguageVersionList);
        assertEquals(1, pageLanguageVersionList.size());
        assertEquals("en-US", pageLanguageVersionList.get(0).getLocaleString());
    }

    /* config exists but no image preset configured.  Image path is still delivered and DM ignores the preset when serving the image */
    @Test
    void testConfigNoImagePreset() {
        context.currentResource(CONTEXT_PATH + HOME_PATH);
        runSocialMediaTest(context, SOCIAL_MEDIA_IMAGE_PATH);
    }

    private void runSocialMediaTest(AemContext context, String imagePath) {
        context.requestPathInfo().setExtension("html");
        when(externalizer.absoluteLink(any(SlingHttpServletRequest.class), anyString(), anyString())).thenReturn(
            "https://www.softwareag.com" + Objects.requireNonNull(context.currentResource()).getPath());

        underTest = context.request().adaptTo(PageMetaModel.class);
        PageMetaModel.WebsiteMetadataProvider provider = Objects.requireNonNull(underTest).getWebsiteMetadataProvider();
        assertEquals("Shift to Digital with Enterprise Integration & IoT by Software AG", provider.getTitle());
        assertEquals("https://www.softwareag.com" + Objects.requireNonNull(context.currentResource()).getPath() + ".html", provider.getURL());
        assertEquals("Software AG", provider.getSiteName());
        assertEquals("Digital connections matter more than ever", provider.getDescription());
        assertEquals(imagePath, provider.getImage());
    }

}
