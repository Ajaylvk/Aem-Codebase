package com.softwareag.core.util;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.constants.Template;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class ContentUtilTest {

    private static final String DEST_PATH = "/content/softwareag/language-master";

    private final AemContext ctx = new AemContext();

    @BeforeEach
    private void setUp() {
        ctx.load().json("/util/contentUtil.json", DEST_PATH);
    }

    @Test
    public void findHomepage_returnsOptionalPage_ifThereIsAHomePage() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH + "/homepage/first-level-page/second-level-page/third-level-page")).adaptTo(Page.class);
        assertNotNull(testPage);

        final Optional<Page> homePage = ContentUtil.findHomepage(testPage);

        assertThat(homePage).hasValueSatisfying(page -> assertThat(Template.getTemplatePathFromPage(page)).isEqualTo(Template.HOME_PAGE.getPath()));
    }

    @Test
    public void findHomepage_returnsSelfPage_ifGivenPageIsAHomePage() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH + "/homepage")).adaptTo(Page.class);
        assertNotNull(testPage);

        final Optional<Page> homePage = ContentUtil.findHomepage(testPage);

        assertThat(homePage).hasValueSatisfying(page -> assertThat(page).isEqualTo(testPage));
    }

    @Test
    public void findHomepage_returnsEmptyOptional_ifThereIsNoHomePage() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH + "/non-homepage")).adaptTo(Page.class);
        assertNotNull(testPage);

        final Optional<Page> homePage = ContentUtil.findHomepage(testPage);

        assertThat(homePage).isNotPresent();
    }

    @Test
    public void findHomepage_returnsEmptyOptional_ifGivenPageIsNull() {
        final Optional<Page> homePage = ContentUtil.findHomepage(null);

        assertThat(homePage).isNotPresent();
    }

    @Test
    public void getChildPages_returnsAllPages_whenIncludeHiddenIsTrue() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getChildPages(testPage, true);

        assertThat(childPages.size()).isEqualTo(2);
    }

    @Test
    public void getChildPages_returnsJustNonHiddenPages_whenIncludeHiddenIsFalse() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getChildPages(testPage, false);

        assertThat(childPages.size()).isEqualTo(1);
        assertFalse(childPages.get(0).isHideInNav());
    }

    @Test
    public void getChildPages_returnsEmptyList_whenGivenPageIsNull() {
        List<Page> childPages = ContentUtil.getChildPages(null, true);

        assertThat(childPages.size()).isEqualTo(0);
    }

    @Test
    public void getFirstNChildPages_returnsEmptyList_whenGivenPageIsNull() {
        List<Page> childPages = ContentUtil.getFirstNChildPages(null, true, 2);

        assertThat(childPages.size()).isEqualTo(0);
    }

    @Test
    public void getFirstNChildPages_returnsEmptyList_whenNIsZero() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getFirstNChildPages(testPage, true, 0);

        assertThat(childPages.size()).isEqualTo(0);
    }

    @Test
    public void getFirstNChildPages_returnsEmptyList_whenNIsLowerThanZero() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getFirstNChildPages(testPage, true, -1);

        assertThat(childPages.size()).isEqualTo(0);
    }

    @Test
    public void testGetFirstNChildPages_returnsOnePage_whenNIsOne() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getFirstNChildPages(testPage, true, 1);

        assertThat(childPages.size()).isEqualTo(1);
    }

    @Test
    public void testGetFirstNChildPages_returnsAllPages_whenNIsGreaterThanTheCountOfChildPages() {
        final Page testPage = Objects.requireNonNull(ctx.currentResource(DEST_PATH)).adaptTo(Page.class);
        assertNotNull(testPage);

        List<Page> childPages = ContentUtil.getFirstNChildPages(testPage, true, 100);

        assertThat(childPages.size()).isEqualTo(2);
    }
}
