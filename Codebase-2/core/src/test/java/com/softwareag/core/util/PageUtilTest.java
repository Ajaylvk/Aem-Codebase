package com.softwareag.core.util;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class PageUtilTest {

    public final AemContext context = new AemContext();
    private static final String CONTENT_PATH_PAGE = "/content/path/page";
    private static final String RESOURCE_PATH = "/content/path/page/resource";

    @Test
    public void getPageFromResource_returnsEmptyOptional_ifNullIsGiven() {
        assertThat(PageUtil.getPageFromResource(null)).isEqualTo(Optional.empty());
    }

    @Test
    public void getPageFromResource_returnsPageOptional_ifPageResourceIsGiven() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        Resource currentResource = this.context.create().resource(RESOURCE_PATH);
        this.context.currentPage(currentPage);
        assertThat(PageUtil.getPageFromResource(currentResource)).isEqualTo(Optional.of(currentPage));
    }

    @Test
    public void getPageFromResource_returnsEmptyOptional_ifResourceWithNoPageIsGiven() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        Resource currentResource = this.context.create().resource("/content/path/nopage/resource");
        this.context.currentPage(currentPage);
        assertThat(PageUtil.getPageFromResource(currentResource)).isEqualTo(Optional.empty());
    }

    @Test
    public void getPageFromRequest_returnsEmptyOptional_ifNullIsGiven() {
        assertThat(PageUtil.getPageFromRequest(null)).isEqualTo(Optional.empty());
    }

    @Test
    public void getPageFromRequest_returnsPageOptional_ifPageRequestIsGiven() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        this.context.create().resource(RESOURCE_PATH);
        this.context.currentPage(currentPage);
        assertThat(PageUtil.getPageFromRequest(this.context.request())).isEqualTo(Optional.of(currentPage));
    }

    @Test
    public void getPageFromPath_returnsEmptyOptional_ifNullIsGiven() {
        assertThat(PageUtil.getPageFromPath(null, null)).isEqualTo(Optional.empty());
        assertThat(PageUtil.getPageFromPath(CONTENT_PATH_PAGE, null)).isEqualTo(Optional.empty());
        assertThat(PageUtil.getPageFromPath(null, this.context.resourceResolver())).isEqualTo(Optional.empty());
    }

    @Test
    public void getPageFromPath_returnsPageOptional_ifValidPathIsGiven() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        this.context.create().resource(RESOURCE_PATH);
        this.context.currentPage(currentPage);
        assertThat(PageUtil.getPageFromPath(CONTENT_PATH_PAGE, this.context.resourceResolver())).isEqualTo(Optional.of(currentPage));
        assertThat(PageUtil.getPageFromPath(RESOURCE_PATH, this.context.resourceResolver())).isEqualTo(Optional.of(currentPage));
    }

    @Test
    public void isPageValid_returnsFalse_ifNullIsGiven() {
        assertThat(PageUtil.isPageValid(null)).isFalse();
    }

    @Test
    public void isPageValid_returnsTrue_ifPageIsGiven() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        assertThat(PageUtil.isPageValid(currentPage)).isTrue();
    }

    @Test
    public void getChildPages_returnsEmptyList_ifNullIsGiven() {
        assertThat(PageUtil.getChildPages(null)).isEmpty();
    }

    @Test
    public void getChildPages_returnsEmptyList_ifNoChildPagesArePresent() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        assertThat(PageUtil.getChildPages(currentPage)).isEmpty();
    }

    @Test
    public void getChildPages_returnsListOfChildPages_ifChildPagesArePresent() {
        Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        Page child1 = this.context.create().page(CONTENT_PATH_PAGE + "/child1");
        Page child2 = this.context.create().page(CONTENT_PATH_PAGE + "/child2");
        List<Page> children = Arrays.asList(child1, child2);
        assertThat(PageUtil.getChildPages(currentPage)).isEqualTo(children);
    }

    @Test
    public void getPageTitle_returnsEmptyString_ifGivenPageIsNull() {
        assertThat(PageUtil.getPageTitle(null)).isEqualTo("");
    }

    @Test
    public void getPageTitle_returnsTitle_ifNoNavAndPageTitleIsSet() {
        final Page currentPage = this.context.create().page(CONTENT_PATH_PAGE);
        assertThat(PageUtil.getPageTitle(currentPage)).isEqualTo("page");
    }

    @Test
    public void getPageTitle_returnsNavTitle_ifItIsSet() {
        final String expectedTitle = "Nav Title";
        this.context.build().resource(CONTENT_PATH_PAGE, "jcr:primaryType", "cq:Page").resource(CONTENT_PATH_PAGE + "/jcr:content", "navTitle", expectedTitle, "pageTitle", "Page Title");
        final Page currentPage = this.context.pageManager().getPage(CONTENT_PATH_PAGE);
        assertThat(PageUtil.getPageTitle(currentPage)).isEqualTo(expectedTitle);
    }

    @Test
    public void getPageTitle_returnsPageTitle_ifNoNavTitleIsSet() {
        final String expectedTitle = "Page Title";
        this.context.build().resource(CONTENT_PATH_PAGE, "jcr:primaryType", "cq:Page").resource(CONTENT_PATH_PAGE + "/jcr:content", "pageTitle", expectedTitle);
        final Page currentPage = this.context.pageManager().getPage(CONTENT_PATH_PAGE);
        assertThat(PageUtil.getPageTitle(currentPage)).isEqualTo(expectedTitle);
    }
}
