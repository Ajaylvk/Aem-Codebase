package com.softwareag.core.models.homepagebanner;

import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class HomepageBannerModelTest {

    private static final String TAGS_SOFTWARE = "/content/cq:tags/softwareag";

    private final AemContext ctx = new AemContext();

    @Mock
    private LanguageService languageService;

    private HomepageBannerModel modelUnderTest;

    @BeforeEach
    public void setUp() {
        ctx.load().json("/components/homepagebanner.json", "/content/softwareag");
        ctx.load().json("/tags/tagList.json", TAGS_SOFTWARE);
        ctx.currentResource("/content/softwareag/jcr:content/homepagebanner");
        ctx.registerService(languageService);

        this.modelUnderTest = Objects.requireNonNull(ctx.currentResource()).adaptTo(HomepageBannerModel.class);
        assertNotNull(this.modelUnderTest);
    }

    @Test
    public void testIsValid() {
        assertTrue(this.modelUnderTest.isValid());
    }

    @Test
    public void testGetTeaserBox() {
        final TeaserBoxModel teaserBox = this.modelUnderTest.getTeaserBox();

        assertNotNull(teaserBox);
        assertNotNull(teaserBox.getBackgroundStyle());
        assertNotNull(teaserBox.getCta());
        assertNotNull(teaserBox.getFontStyle());
        assertNotNull(teaserBox.getTitle());
        assertEquals("Conference", teaserBox.getCampaign());
    }

    @Test
    public void testGetSolutionBox() {
        final SolutionBoxModel solutionBox1 = this.modelUnderTest.getSolutionBox1();
        final SolutionBoxModel solutionBox2 = this.modelUnderTest.getSolutionBox2();

        assertNotNull(solutionBox1);
        testSolutionBoxModelGetter(solutionBox1);
        assertNotNull(solutionBox2);
        testSolutionBoxModelGetter(solutionBox2);
    }

    private void testSolutionBoxModelGetter(final SolutionBoxModel solutionBox) {
        assertNotNull(solutionBox.getAltText());
        assertNotNull(solutionBox.getBackgroundStyle());
        assertNotNull(solutionBox.getCta());
        assertNotNull(solutionBox.getFontStyle());
        assertNotNull(solutionBox.getImage());
        assertNotNull(solutionBox.getTitle());
        assertNotNull(solutionBox.getUpperTitle());
        assertEquals("Conference", solutionBox.getCampaign());
    }

}
