package com.softwareag.core.models;

import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CampaignModelTest {

    private static final String TAGS_SOFTWARE = "/content/cq:tags/softwareag";
    private static final String DEST_PATH = "/content/softwareag/test";
    private static final String TITLE_FIELD = "titlePropName";

    private final AemContext ctx = new AemContext();

    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setUp() {
        ctx.load().json("/tags/tagList.json", TAGS_SOFTWARE);
        ctx.load().json("/components/campaign.json", DEST_PATH);
        ctx.registerService(languageService);
    }

    @Test
    void test_getCampaign() {
        ctx.currentResource(DEST_PATH + "/jcr:content/parsys/model");
        ctx.request().setAttribute(TITLE_FIELD, "eventTitle");

        final CampaignModel componentModel = Objects.requireNonNull(ctx.request()).adaptTo(CampaignModel.class);
        assertEquals("Conference", Objects.requireNonNull(componentModel).getCampaign());
        assertEquals("Event Title 01", componentModel.getTitle());
    }

    private CampaignModel createModel(final String componentPath) {
        Resource componentResource = ctx.resourceResolver().getResource(componentPath);
        return Objects.requireNonNull(componentResource).adaptTo(CampaignModel.class);
    }

}
