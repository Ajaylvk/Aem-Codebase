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
class FirstLevelNavItemTest {

    private static final String DEST_PATH = "/content/softwareag/test/homepage";

    private final AemContext context = new AemContext();

    private static LabelConfigModel labelConfigModel;

    @BeforeEach
    public void setUp() {
        context.load().json("/components/header/header.json", DEST_PATH);
        labelConfigModel = Objects.requireNonNull(context.currentResource(DEST_PATH + "/config-page/jcr:content/parsys/label")).adaptTo(LabelConfigModel.class);
    }

    @Test
    public void getOverviewTitle_returnsNull_whenPageHasFolderTemplate() {
        final Page page = Objects.requireNonNull(context.currentResource(DEST_PATH + "/first-level-item-2")).adaptTo(Page.class);
        assertThat(page).isNotNull();
        assertTrue(Template.FOLDER_PAGE.isTemplateOfPage(page));

        final FirstLevelNavItem modelUnderTest = new FirstLevelNavItem(page, labelConfigModel);

        assertNull(modelUnderTest.getOverviewTitle());
    }

    @Test
    public void getOverviewTitle_returnsValue_whenPageProvidesContent() {
        final FirstLevelNavItem modelUnderTest = createFirstLevelNavItemModel(DEST_PATH + "/first-level-item-1");

        assertThat(modelUnderTest.getOverviewTitle()).endsWith("Overview");
    }

    @Test
    public void getSecondLevelNavItems_returnsFilledList_whenChildPagesExist() {
        final FirstLevelNavItem modelUnderTest = createFirstLevelNavItemModel(DEST_PATH + "/first-level-item-1");

        assertThat(modelUnderTest.getSecondLevelNavItems().size()).isEqualTo(6);

        for (SecondLevelNavItem item : modelUnderTest.getSecondLevelNavItems()) {
            assertThat(item.getLink()).isNotNull();
            assertTrue(item.getLink().hasContent());
        }
    }

    @Test
    public void getSecondLevelNavItems_returnsEmptyList_whenThereAreNoChildPages() {
        final FirstLevelNavItem modelUnderTest = createFirstLevelNavItemModel(DEST_PATH + "/first-level-item-3");

        assertTrue(modelUnderTest.getSecondLevelNavItems().isEmpty());
    }

    @Test
    public void testSecondLevelNavItemRedirectHasTargetBlank() {
        final FirstLevelNavItem modelUnderTest = createFirstLevelNavItemModel(DEST_PATH + "/first-level-item-1");

        for (SecondLevelNavItem item : modelUnderTest.getSecondLevelNavItems()) {
            if (item.getPath().contains("second-level-item-4")) {
                assertEquals("_blank", item.getLink().getTarget());
                assertEquals("https://www.google.com", item.getLink().getHref());
            }
            if (item.getPath().contains("second-level-item-5")) {
                assertEquals("_blank", item.getLink().getTarget());
                assertEquals("/content/softwareag.html", item.getLink().getHref());
            }
            if (item.getPath().contains("second-level-item-6")) {
                assertEquals("_self", item.getLink().getTarget());
                assertEquals("/content/softwareag/test/homepage/first-level-item-1/second-level-item-6.html", item.getLink().getHref());
            }
        }
    }

    private FirstLevelNavItem createFirstLevelNavItemModel(final String path) {
        final Page page = Objects.requireNonNull(context.currentResource(path)).adaptTo(Page.class);
        assertThat(page).isNotNull();

        return new FirstLevelNavItem(page, labelConfigModel);
    }
}
