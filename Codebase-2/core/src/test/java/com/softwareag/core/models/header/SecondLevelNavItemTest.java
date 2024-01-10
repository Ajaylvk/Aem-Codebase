package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.label.LabelConfigModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class SecondLevelNavItemTest {

    private static final String DEST_PATH = "/content/softwareag/test/homepage";

    private final AemContext context = new AemContext();

    private static LabelConfigModel labelConfigModel;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/header/header.json", DEST_PATH);
        labelConfigModel = Objects.requireNonNull(context.currentResource(DEST_PATH + "/config-page/jcr:content/parsys/label")).adaptTo(LabelConfigModel.class);
    }

    @Test
    public void getThirdLevelNavItems_returnsEmptyList_whenThereAreNoChildPages() {
        final SecondLevelNavItem modelUnderTest = createSecondLevelNavItemModel(DEST_PATH + "/first-level-item-1/second-level-item-2");

        assertTrue(modelUnderTest.getThirdLevelNavItems().isEmpty());
    }

    @Test
    public void getThirdLevelNavItems_returnsFilledList_whenChildPagesExist() {
        final SecondLevelNavItem modelUnderTest = createSecondLevelNavItemModel(DEST_PATH + "/first-level-item-1/second-level-item-1");

        assertThat(modelUnderTest.getThirdLevelNavItems().size()).isEqualTo(3);

        for (NavItem item : modelUnderTest.getThirdLevelNavItems()) {
            assertThat(item.getLink()).isNotNull();
            assertTrue(item.getLink().hasContent());
        }
    }

    @Test
    public void getOverviewTitle_returnsNull_whenPageHasFolderTemplate() {
        final Page page = Objects.requireNonNull(context.currentResource(DEST_PATH + "/first-level-item-1/second-level-item-3")).adaptTo(Page.class);
        assertThat(page).isNotNull();
        assertTrue(Template.FOLDER_PAGE.isTemplateOfPage(page));

        final SecondLevelNavItem modelUnderTest = new SecondLevelNavItem(page, labelConfigModel);

        assertNull(modelUnderTest.getOverviewTitle());
    }

    @Test
    public void getOverviewTitle_returnsValue_whenPageProvidesContent() {
        final SecondLevelNavItem modelUnderTest = createSecondLevelNavItemModel(DEST_PATH + "/first-level-item-1/second-level-item-1");

        assertThat(modelUnderTest.getOverviewTitle()).endsWith("Overview");
    }

    @Test
    public void testThirdLevelNavItemRedirectHasTargetBlank() {

        final SecondLevelNavItem modelUnderTest = createSecondLevelNavItemModel(DEST_PATH + "/first-level-item-1/second-level-item-5");

        for (NavItem item : modelUnderTest.getThirdLevelNavItems()) {
            if (item.getPath().contains("third-level-item-1")) {
                assertEquals("_blank", item.getLink().getTarget());
                assertEquals("/content/softwareag.html", item.getLink().getHref());
            }
            if (item.getPath().contains("third-level-item-2")) {
                assertEquals("_blank", item.getLink().getTarget());
                assertEquals("https://www.google.com", item.getLink().getHref());
            }
            if (item.getPath().contains("third-level-item-3")) {
                assertEquals("_self", item.getLink().getTarget());
                assertEquals("/content/softwareag/test/homepage/first-level-item-1/second-level-item-5/third-level-item-3.html", item.getLink().getHref());
            }
        }
    }

    private SecondLevelNavItem createSecondLevelNavItemModel(final String path) {
        final Page page = Objects.requireNonNull(context.currentResource(path)).adaptTo(Page.class);
        assertThat(page).isNotNull();

        return new SecondLevelNavItem(page, labelConfigModel);
    }
}
