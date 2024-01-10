package com.softwareag.core.models.list;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.models.link.LinkModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ListModelTest {
    private static final String DEST_PATH = "/content/test";
    private static final String JCR_CONTENT_PARSYS = "/jcr:content/parsys";
    private static final String EMPTY_STATIC_LIST_RESOURCE_PATH = DEST_PATH + JCR_CONTENT_PARSYS + "/empty_static_list";
    private static final String STATIC_LIST_RESOURCE_PATH = DEST_PATH + JCR_CONTENT_PARSYS + "/static_list";
    private static final String INVALID_LIST_RESOURCE_PATH = DEST_PATH + JCR_CONTENT_PARSYS + "/invalid_list";
    private static final String CHILDREN_LIST_RESOURCE_PATH = DEST_PATH + JCR_CONTENT_PARSYS + "/children_list";
    private static final String CHILD_PAGES_PATH = DEST_PATH + "/children";
    private static final String CHILD_PAGE_WITHOUT_TITLE = CHILD_PAGES_PATH + "/page_without_title";
    private static final String CHILD_PAGE_WITH_TITLE = CHILD_PAGES_PATH + "/page_with_title";
    private static final String CHILD_PAGE_WITH_PAGE_TITLE = CHILD_PAGES_PATH + "/page_with_pageTitle";
    private static final String CHILD_PAGE_WITH_NAV_TITLE = CHILD_PAGES_PATH + "/page_with_navTitle";

    public final AemContext context = new AemContext();

    @BeforeEach
    public void setUp() {
        context.load().json("/components/list.json", DEST_PATH);
    }

    @Test
    void testGetValue() {
        assertThat(ListModel.Source.CHILDREN.getValue()).isEqualTo("children");
        assertThat(ListModel.Source.STATIC.getValue()).isEqualTo("static");
        assertThat(ListModel.Source.EMPTY.getValue()).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    void hasContent_shouldReturnTrue_whenTitleIsSet() {
        ListModel modelUnderTest = getListModel(CHILDREN_LIST_RESOURCE_PATH);
        assertThat(modelUnderTest.hasContent()).isTrue();
    }

    @Test
    void hasContent_shouldReturnFalse_whenTitleIsNull() {
        ListModel modelUnderTest = new ListModel();
        assertThat(modelUnderTest.hasContent()).isFalse();
    }

    @Test
    void getTitle_shouldReturnGivenValue() {
        ListModel modelUnderTest = getListModel(CHILDREN_LIST_RESOURCE_PATH);
        assertThat(modelUnderTest.getTitle()).isEqualTo("Children List");
    }

    @Test
    void getLinks_shouldReturnEmptyList_whenListFromPropertyIsInvalid() {
        ListModel modelUnderTest = getListModel(INVALID_LIST_RESOURCE_PATH);
        assertThat(modelUnderTest.getLinks().isEmpty()).isTrue();
    }

    @Test
    void getLinks_shouldReturnAnEmptyList_whenNoStaticLinksAreAvailable() {
        ListModel modelUnderTest = getListModel(EMPTY_STATIC_LIST_RESOURCE_PATH);
        assertThat(modelUnderTest.getLinks().isEmpty()).isTrue();
    }

    @Test
    void getLinks_shouldReturnValidLinks_whenListIsBuiltFromStatic() {
        ListModel modelUnderTest = getListModel(STATIC_LIST_RESOURCE_PATH);
        assertThat(modelUnderTest.getLinks().size()).isEqualTo(1);
    }

    @Test
    void getLinks_shouldReturnValidLinks_whenListIsBuiltFromChildren() throws IllegalAccessException {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        PageManager pageManagerMock = mock(PageManager.class);
        Page currentPage = mock(Page.class);
        Page rootPage = mock(Page.class);
        PageManager pageManager = context.pageManager();
        Page pageWithoutTitle = Objects.requireNonNull(pageManager.getPage(CHILD_PAGE_WITHOUT_TITLE));
        Page pageWithTitle = Objects.requireNonNull(pageManager.getPage(CHILD_PAGE_WITH_TITLE));
        Page pageWithPageTitle = Objects.requireNonNull(pageManager.getPage(CHILD_PAGE_WITH_PAGE_TITLE));
        Page pageWithNavTitle = Objects.requireNonNull(pageManager.getPage(CHILD_PAGE_WITH_NAV_TITLE));
        List<Page> childPages = new ArrayList<Page>() {
            {
                add(pageWithoutTitle);
                add(pageWithTitle);
                add(pageWithPageTitle);
                add(pageWithNavTitle);
            }
        };
        when(rootPage.listChildren()).thenReturn(childPages.iterator());
        when(currentPage.listChildren()).thenReturn(childPages.iterator());
        when(resourceResolver.getResource(anyString())).thenReturn(mock(Resource.class));
        when(pageManagerMock.getContainingPage(any(Resource.class))).thenReturn(rootPage);

        ListModel modelUnderTest = getListModel(CHILDREN_LIST_RESOURCE_PATH);
        FieldUtils.writeField(modelUnderTest, "resourceResolver", resourceResolver, true);
        FieldUtils.writeField(modelUnderTest, "pageManager", pageManagerMock, true);
        FieldUtils.writeField(modelUnderTest, "currentPage", currentPage, true);
        modelUnderTest.init();

        List<LinkModel> resultLinks = modelUnderTest.getLinks();

        assertThat(resultLinks.isEmpty()).isFalse();
        assertThat(resultLinks.size()).isEqualTo(childPages.size());
        assertThat(resultLinks.get(0).getText()).isEqualTo(pageWithoutTitle.getName());
        assertThat(resultLinks.get(1).getText()).isEqualTo(pageWithTitle.getTitle());
        assertThat(resultLinks.get(2).getText()).isEqualTo(pageWithPageTitle.getPageTitle());
        assertThat(resultLinks.get(3).getText()).isEqualTo(pageWithNavTitle.getNavigationTitle());
        for (final LinkModel link : resultLinks) {
            assertThat(StringUtils.endsWith(link.getHref(), ".html")).isTrue();
            assertThat(StringUtils.equals(link.getTarget(), LinkModel.Target.SELF.getValue())).isTrue();
        }
    }

    private ListModel getListModel(final String resourcePath) {
        context.currentResource(resourcePath);
        ListModel listModel = context.request().adaptTo(ListModel.class);
        assertThat(listModel).isNotNull();
        return listModel;
    }
}
