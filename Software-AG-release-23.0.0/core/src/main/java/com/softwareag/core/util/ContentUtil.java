package com.softwareag.core.util;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.softwareag.core.constants.Template;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.softwareag.core.util.FunctionalUtil.asStream;

/**
 * Helper class for providing information based on the content structure.
 */

public final class ContentUtil {

    private ContentUtil() {
        // Class is not meant to be instantiated.
    }

    /**
     * Finds the corresponding home page for the given {@link Page}.
     *
     * @param currentPage
     *     the current page
     * @return the home page
     */
    public static Optional<Page> findHomepage(final Page currentPage) {
        return findNearestParentByTemplate(currentPage, Template.HOME_PAGE);
    }

    /**
     * Finds the nearest parent {@link Page} by {@link Template}.
     *
     * @param currentPage
     *     a page to start search
     * @param template
     *     a template to search for
     * @return the next parent page with the given template
     */
    public static Optional<Page> findNearestParentByTemplate(final Page currentPage, final Template template) {
        if (currentPage == null || template == null) {
            return Optional.empty();
        }

        if (template.isTemplateOfPage(currentPage)) {
            return Optional.of(currentPage);
        }

        Page parent = currentPage.getParent();
        while (parent != null) {
            if (template.isTemplateOfPage(parent)) {
                return Optional.of(parent);
            }
            parent = parent.getParent();
        }

        return Optional.empty();
    }

    /**
     * Finds all direct child pages below the given {@link Page}.
     *
     * @param currentPage
     *     the {@link Page} which marks the starting point of the traversal
     * @param includeHidden
     *     if {@code true} hidden pages are included
     * @return the child pages
     */
    public static List<Page> getChildPages(final Page currentPage, final boolean includeHidden) {
        if (currentPage == null) {
            return Collections.emptyList();
        }
        return asStream(currentPage.listChildren(new PageFilter(false, includeHidden))).collect(Collectors.toList());
    }

    /**
     * Finds the first n direct child pages below the given {@link Page}.
     *
     * @param currentPage
     *     the {@link Page} which marks the starting point of the traversal
     * @param includeHidden
     *     if {@code true} hidden pages are included
     * @param n
     *     maximum number of pages to be returned
     * @return the child pages
     */
    public static List<Page> getFirstNChildPages(final Page currentPage, final boolean includeHidden, final int n) {
        if (currentPage == null || n < 0) {
            return Collections.emptyList();
        }
        return asStream(currentPage.listChildren(new PageFilter(false, includeHidden))).limit(n).collect(Collectors.toList());
    }
}
