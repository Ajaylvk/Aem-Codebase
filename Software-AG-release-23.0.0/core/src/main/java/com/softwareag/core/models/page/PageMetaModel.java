package com.softwareag.core.models.page;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.LanguageUtil;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.NavigationConstants;
import com.softwareag.core.eventhandlers.NewPageReplicationNotifier;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.services.language.PageInternationalVersion;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The SocialMediaModel provides data for the Social Media headers and allows a centrally configured toggle of all OpenGraph page headers
 * and HrefLang page information using the Software AG configuration page mechanism instead of the Core Components 2.8
 * page properties config, which works per page, or configured in template initial properties.
 *
 * The WebsiteDataProvider class is a modified copy of the Core Components 2.8 https://github.com/adobe/aem-core-wcm-components/blob/master/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/SocialMediaHelperImpl.java
 * A config model and a WebsiteDataProvider is always returned regardless of the setting in the config component and even if a config component does not exist in the JCR for the page.  This avoids passing null.
 * The enabled condition used in the HTL will be the default as defined in the class SocialMediaConfigModel.
 */

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageMetaModel {

    private static final Logger LOG = LoggerFactory.getLogger(PageMetaModel.class);
    protected static final String DM_PRESET_OPENGRAPH = "facebook-opengraph";

    @OSGiService
    private Externalizer externalizer;

    @OSGiService
    LanguageService languageService;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    private PageManager pageManager;

    @Self
    SlingHttpServletRequest request;

    private SocialMediaConfigModel config;

    private WebsiteMetadataProvider websiteMetadataProvider;

    @PostConstruct
    private void init() {

        final Resource configResource = ConfigurationFinder.getConfigurationComponent(currentPage, SocialMediaConfigModel.SOCIALMEDIA_CONFIG_RESOURCE_TYPE);
        config = Optional.ofNullable(configResource)
                .map(r -> r.adaptTo(SocialMediaConfigModel.class))
                .orElse(new SocialMediaConfigModel());

        websiteMetadataProvider = new WebsiteMetadataProvider();

    }

    public SocialMediaConfigModel getConfig() {
        return config;
    }

    /**
     * gets an object containing meta data for the page
     *
     * @return {SocialMediaModel.WebsiteMetadataProvider}.
     */
    public WebsiteMetadataProvider getWebsiteMetadataProvider() {
        return websiteMetadataProvider;
    }

    /* copy of The WebsiteDataProvider class is based on the Core Components 2.8 https://github.com/adobe/aem-core-wcm-components/blob/master/bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/SocialMediaHelperImpl.java */
    public class WebsiteMetadataProvider {

        /**
         * @return the title for the current page
         */
        public String getTitle() {
            String title = currentPage.getTitle();
            if (StringUtils.isBlank(title)) {
                title = currentPage.getName();
            }
            return title;
        }

        /**
         * @return the URL including domain, protocol and extension for the current page
         */
        public String getURL() {
            String pagePath = currentPage.getPath();
            String extension = request.getRequestPathInfo().getExtension();
            return externalizer.absoluteLink(request, request.getScheme(), pagePath) + "." + extension;
        }

        /**
         * returns a link to a published Scene7 image
         **/
        public String getImage() {
            String assetID = config.getAssetID();
            String imageServer = config.getImageserverurl();
            return StringUtils.isNotBlank(assetID) && StringUtils.isNotBlank(imageServer) ? (imageServer + assetID + "?$" + DM_PRESET_OPENGRAPH + "$") : "";
        }

        /**
         * returns a list of Language Versions including the current page tree, and handles corporate
         *
         * @return a List of PageInternationalVersions for the current page and other languages / countries.
         */
        public List<PageInternationalVersion> getCurrentPageLanguageVersions() {
            List<PageInternationalVersion> pageLanguageVersionList = new ArrayList<>();
            String pagePath = currentPage.getPath();
            Locale locale = null;
            String languageLevel = LanguageUtil.getLanguageRoot(pagePath);
            LOG.info("LanguageLevel:"+languageLevel);
            if (languageLevel != null) {
                locale = LanguageUtil.getLocale(StringUtils.substringAfterLast(languageLevel, "/"));
                LOG.info("locale 1st condition:"+locale);
            }
            if (locale == null && isCorporate(pagePath)) {
                locale = Locale.US;
                LOG.info("locale corporate:"+locale);
            }
            if (locale != null) {
                PageInternationalVersion currentPageLanguageVersion = new PageInternationalVersion(getURL(), locale.toLanguageTag());
                pageLanguageVersionList.add(currentPageLanguageVersion);
                LOG.info("locale 3rd condition:"+locale);
            }
            pageLanguageVersionList.addAll(languageService.getPageInternationalVersionList(pageManager, currentPage, request));
            LOG.info("List of all international page versions:"+pageLanguageVersionList.toString());
            return pageLanguageVersionList;
        }

        /**
         * check if the current path is in the corporate tree
         *
         * @param path
         *     current path of the page
         * @return boolean true is in corporate tree
         */
        private boolean isCorporate(String path) {
            return StringUtils.contains(path, NavigationConstants.CORPORATE_LOCALE);
        }

        /**
         * get the page description
         *
         * @return description
         */
        public String getDescription() {
            return currentPage.getDescription();
        }

        /**
         * get the site name based on the Title of the tenant node
         *
         * @return site name
         */
        public String getSiteName() {
            Page page = findRootPage();

            String pageTitle = page.getPageTitle();
            if (StringUtils.isNotBlank(pageTitle)) {
                return pageTitle;
            }

            Resource content = page.getContentResource();
            if (content == null) {
                return null;
            }
            String title = content.getValueMap().get(JcrConstants.JCR_TITLE, String.class);
            if (StringUtils.isBlank(title)) {
                return null;
            }
            return title;
        }

        /**
         * get the highest level parent page
         *
         * @return root page
         */
        private Page findRootPage() {
            Page page = currentPage;
            while (true) {
                Page parent = page.getParent();
                if (parent == null) {
                    return page;
                } else {
                    page = parent;
                }
            }
        }
    }
}
