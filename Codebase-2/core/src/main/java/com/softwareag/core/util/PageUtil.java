package com.softwareag.core.util;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.commons.WCMUtils;
import com.softwareag.core.constants.NavigationConstants;
import com.softwareag.core.constants.Template;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * Generic utils for AEM @Page objects.
 */
public final class PageUtil {

    private PageUtil() {
        // Class is not meant to be instantiated.
    }

    /**
     * Returns the (containing) Page of the current request.
     *
     * @param request
     *     the http request to get the page from.
     * @return the page of the current request.
     */
    public static Optional<Page> getPageFromRequest(SlingHttpServletRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        return getPageFromResource(request.getResource());
    }

    /**
     * Returns the (containing) Page of the current resource.
     *
     * @param resource
     *     the resource to get the containing page from.
     * @return the containing page of the current resource.
     */
    public static Optional<Page> getPageFromResource(Resource resource) {
        if (resource == null) {
            return Optional.empty();
        }
        return getPage(resource.getResourceResolver(), resource);
    }

    /**
     * Returns the (containing) Page of the current path.
     *
     * @param path
     *     the path to get the page from.
     * @param resourceResolver
     *     the current {@link ResourceResolver} instance.
     * @return the page of the current path.
     */
    public static Optional<Page> getPageFromPath(String path, ResourceResolver resourceResolver) {
        if (resourceResolver == null || StringUtils.isEmpty(path)) {
            return Optional.empty();
        }
        return getPage(resourceResolver, resourceResolver.getResource(path));
    }

    private static Optional<Page> getPage(ResourceResolver resourceResolver, Resource resource) {
        if (resourceResolver != null && resource != null) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                return Optional.ofNullable(pageManager.getContainingPage(resource));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns a flag indicating whether the referenced page is an existing page.
     *
     * @param page
     *     the page to check
     * @return the flag
     */
    public static boolean isPageValid(Page page) {
        return page != null && page.isValid();
    }

    /**
     * Returns children pages of the page.
     *
     * @param page
     *     the page to get children from
     * @return children of the page
     */
    public static List<Page> getChildPages(Page page) {
        if (page == null) {
            return Collections.emptyList();
        }
        return FunctionalUtil.asStream(page.listChildren(new PageFilter(true, true))).collect(Collectors.toList());
    }

    /**
     * Tests if a page is a valid country root
     *
     * @param page
     *     the page to get children from
     * @return boolean flag
     */
    public static boolean isValidCountryPage(Page page) {
        return page.getDepth() == NavigationConstants.COUNTRY_ROOT_LEVEL && (page.getName().matches(
            String.valueOf(NavigationConstants.COUNTRY_ROOT_REGEX_PATTERN)) || page.getName()
            .equals(NavigationConstants.COUNTRY_CORPORATE));
    }

    /**
     * Tests if a page is a valid language root
     *
     * @param page
     *     the page to get children from
     * @return boolean flag
     */
    public static boolean isValidLanguagePage(Page page) {
        ValueMap props = page.getProperties();
        String cqTemplate = props.get(NN_TEMPLATE, String.class);
        return (StringUtils.equals(cqTemplate, Template.HOME_PAGE.getPath())||StringUtils.equals(cqTemplate, Template.GENERIC_PAGE.getPath()))
            && (page.getName().matches(String.valueOf(NavigationConstants.LANGUAGE_ROOT_REGEX_PATTERN)) || page.getName().matches(
            String.valueOf(NavigationConstants.LANGUAGE_CORPORATE_REGEX_PATTERN)))
            && page.getDepth() == NavigationConstants.LANGUAGE_ROOT_LEVEL
            && page.isValid();
    }

    /**
     * Returning the relative path to the language root
     *
     * @param page
     *     any page
     * @return relative path
     */
    public static String getPathRelativeToLanguageRoot(Page page) {
        return RegExUtils.removeFirst(page.getPath(), NavigationConstants.LANGUAGE_ROOT_PATH_REGEX);
    }

    /**
     * Returns the title of a {@link Page}.
     * Priority of the page properties for the title: Navigation Title - Page Title - Title - Last path segment of the page path.
     *
     * @param page
     *     the page to get the title from
     * @return title of the {@link Page}. If the given {@link Page} is {@code Null}, an empty String is returned.
     */
    public static String getPageTitle(final Page page) {
        String pageTitle = StringUtils.EMPTY;
        if (page != null) {
            pageTitle = page.getNavigationTitle();
            if (StringUtils.isBlank(pageTitle)) {
                pageTitle = page.getPageTitle();
            }
            if (StringUtils.isBlank(pageTitle)) {
                pageTitle = page.getTitle();
            }
            if (StringUtils.isBlank(pageTitle)) {
                pageTitle = page.getName();
            }
        }
        return pageTitle;
    }

    public static Page resolveCurrentPage(SlingHttpServletRequest request) {
        ResourceResolver resolver = request.getResourceResolver();
        /* Remove extension if present */
        String suffix = FilenameUtils.removeExtension(request.getRequestPathInfo().getSuffix());
        /* Suffix null check not required as getResource method is null safe */
        Resource suffixResource = resolver.getResource(suffix);
        if (suffixResource != null && !ResourceUtil.isNonExistingResource(suffixResource)) {
            PageManager pageManager = resolver.adaptTo(PageManager.class);
            if (pageManager != null && pageManager.getContainingPage(suffixResource) != null) {
                return pageManager.getContainingPage(suffixResource);
            } else {
                return getCurrentPage(request);
            }
        }
        return getCurrentPage(request);
    }

    public static Page getCurrentPage(SlingHttpServletRequest request) {
        ComponentContext componentContext = WCMUtils.getComponentContext(request);
        if (componentContext != null && componentContext.getPage() != null) {
            return componentContext.getPage();
        }
        PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
        if (pageManager != null) {
            return pageManager.getContainingPage(request.getResource());
        }
        return null;
    }
}
