package com.softwareag.core.services.language;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.constants.NavigationConstants;
import com.softwareag.core.models.page.PageMetaModel;
import com.softwareag.core.util.PageUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * LanguageService
 * Generally used for hreflang / canonical tags of every page.
 */

@Component(service = LanguageService.class, immediate = true)
public class LanguageService {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageService.class);
    private static final String DOT_HTML = ".html";

    @Reference
    private LanguageManager languageManager;

    @Reference
    private Externalizer externalizer;

    /**
     * Returns a list of all language root pages (except invalid locales such as corporate)
     *
     * @param currentPage
     *     any page
     * @return list of language root pages in the current tenant
     */
    public List<Page> getAllLanguageRoots(Page currentPage) {
        List<Page> languageRoots = new ArrayList<>();
        List<Page> countryList = getCountries(currentPage);
        for (Page countryRoot : countryList) {
            languageRoots.addAll(getLanguagesForCountry(countryRoot));
        }

        return languageRoots;
    }

    /**
     * Returns a list of the language translations of the current page
     *
     * @param pageManager
     *     CQ Page API PageManager
     * @param currentPage
     *     any page
     * @param request
     *     SlingHttpServletRequest is required for externalizer.absoluteLink
     * @return List of versions of the same page in the content tree for other countries/languages,
     * or an empty List if no equivalent pages exist.
     */
    public List<PageInternationalVersion> getPageInternationalVersionList(final PageManager pageManager,
                                                                          final Page currentPage,
                                                                          final SlingHttpServletRequest request) {
        if (pageManager == null || currentPage == null || currentPage.getContentResource() == null) {
            return Collections.emptyList();
        }

        final List<PageInternationalVersion> internationalVersionList = new ArrayList<>();
        final List<Page> languagePages = getAllLanguageRoots(currentPage);

        final String currentPagePath = currentPage.getPath();
        final String relativePath = PageUtil.getPathRelativeToLanguageRoot(currentPage);
        LOG.info("Get relative path"+relativePath);
        for (final Page languagePage : languagePages) {
            final String pagePath = languagePage.getPath() + relativePath;
            LOG.info("Page path just before forming"+pagePath);
            if (!StringUtils.equals(pagePath, currentPagePath) && pageManager.getPage(pagePath) != null) {
                final String absoluteLink = externalizer.absoluteLink(request, request.getScheme(), pagePath) + DOT_HTML;
                internationalVersionList.add(new PageInternationalVersion(absoluteLink, getLocaleString(languagePage)));
            }
        }
        return internationalVersionList;
    }

    /**
     * Returns a list of all countries in the tenant of the current page
     *
     * @param currentPage
     *     any page
     * @return List of all countries
     */
    public static List<Page> getCountries(Page currentPage) {
        List<Page> countryPageList = new ArrayList<>();
        Page tenantPage = currentPage.getAbsoluteParent(NavigationConstants.TENANT_LEVEL);
        if (tenantPage != null) {
            for (Page countryLevelPage : PageUtil.getChildPages(tenantPage)) {
                if (PageUtil.isValidCountryPage(countryLevelPage)) {
                    countryPageList.add(countryLevelPage);
                }
            }
        }
        for (Page coutry:countryPageList
        ) {
            LOG.info("The countries are:"+coutry.getPath());
        }
        return countryPageList;
    }

    /**
     * Returns a list of all languages for a given country page
     *
     * @param countryRoot
     *     a country root page
     * @return List of language pages for the for the country
     */
    public List<Page> getLanguagesForCountry(Page countryRoot) {
        List<Page> languagePageList = new ArrayList<>();
        if (countryRoot != null) {
            for (Page languageLevelPage : PageUtil.getChildPages(countryRoot)) {
                if (PageUtil.isValidLanguagePage(languageLevelPage)) {
                    languagePageList.add(languageLevelPage);
                }
            }
        }
        return languagePageList;
    }

    /**
     * Returns the locale for the given resource. If not found, given default locale will be returned.
     *
     * @param resource
     *     any Resource
     * @param defaultLocale
     *     Default locale value to be used, when the locale is not resolved.
     * @return locale resolved by its path or jcr:language property from the first parent resource. Provided default locale, if no locale found.
     */
    public Locale getLocale(final Resource resource, final Locale defaultLocale) {
        if (resource == null) {
            return null;
        }
       /* if (StringUtils.contains(resource.getPath(), NavigationConstants.COUNTRY_CORPORATE)) {
            return Locale.US;
        } */
        final Locale locale = languageManager.getLanguage(resource);
        if (locale != null) {
            return locale;
        }

        return defaultLocale;
    }

    /**
     * Returns the locale for the given resource. If not found, given default locale will be returned.
     *
     * @param page
     *     any Page
     * @param defaultLocale
     *     Default locale value to be used, when the locale is not resolved.
     * @return locale resolved by its path or jcr:language property from the first parent resource. Provided default locale, if no locale found.
     */
    public Locale getLocale(final Page page, final Locale defaultLocale) {
        if (page == null) {
            return null;
        }
        return getLocale(page.getContentResource(), defaultLocale);
    }

    /**
     * Returns the locale for the given resource.
     *
     * @param resource
     *     any Resource
     * @return locale resolved by its path or jcr:language property from the first parent resource. null, if no locale found.
     */
    public Locale getLocale(final Resource resource) {
        return getLocale(resource, null);
    }

    /**
     * Returns the locale for the given resource.
     *
     * @param page
     *     any Page
     * @return locale resolved by its path or jcr:language property from the first parent resource. null, if no locale found.
     */
    public Locale getLocale(final Page page) {
        return getLocale(page, null);
    }

    /**
     * Returns the language of a resource as in the CQ Page API LanguageManager, but wth null check.
     *
     * @param resource
     *     any Resource
     * @return empty String if no language found, or language code if found
     */
    public String getLanguage(final Resource resource) {
        final Locale locale = getLocale(resource);
        if (locale != null) {
            return locale.getLanguage();
        }
        return StringUtils.EMPTY;
    }

    /**
     * Returns the local string of a language level page and handles corporate
     *
     * @param languagePage
     *     a language level page
     * @return Normal java Locale.toLanguageTag() string, and "en-US" for corporate.
     */
    public String getLocaleString(Page languagePage) {
        String languageRootPath = languagePage.getPath();
        if (StringUtils.contains(languageRootPath, NavigationConstants.COUNTRY_CORPORATE)) {
            return NavigationConstants.CORPORATE_LANGUAGE;
        }
        String languageLevel = LanguageUtil.getLanguageRoot(languagePage.getPath());
        Locale locale = LanguageUtil.getLocale(StringUtils.substringAfterLast(languageLevel, "/"));
        return locale != null ? locale.toLanguageTag() : "";
    }

}
