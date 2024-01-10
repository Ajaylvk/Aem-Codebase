package com.softwareag.core.models.link;

import com.softwareag.core.services.domains.RegisteredDomainsService;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkModel {

    public static final String CONTENT = "/content";
    public static final String CONTENT_DAM = CONTENT + "/dam";
    public static final String DOT_HTML = ".html";
    public static final String HASH = "#";
    public static final String Q_MARK = "?";

    @OSGiService
    private RegisteredDomainsService registeredDomainsService;

    @ValueMapValue
    private String target;

    @ValueMapValue
    private String text;

    @ValueMapValue(name = "href")
    private String rawHref;

    @ValueMapValue
    private String utmParameter;

    @SlingObject
    private ResourceResolver resourceResolver;

    private boolean damContent;
    private String href;

    private String assetType;

    private List<String> registeredDomains = new ArrayList<>();

    /**
     * Checks whether the given path is an internal link.
     *
     * @param path
     *     the path
     * @return {@code true} if the path is internal, {@code false} otherwise
     */
    public static boolean isInternal(final String path) {
        return StringUtils.startsWith(path, CONTENT) || StringUtils.startsWith(path, HASH);
    }

    public boolean isInternalAsset(final String path) {
        return isInternal(path) || isInternalDomain(path);
    }

    private boolean isInternalDomain(final String path) {
        for (String currentDomain : registeredDomains) {
            if (path.contains(currentDomain)) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    void init() {
        this.href = rawHref;
        this.href = StringUtils.isBlank(this.href) ? StringUtils.EMPTY : this.href;
        this.utmParameter = StringUtils.isBlank(this.utmParameter) ? StringUtils.EMPTY : this.utmParameter;
        this.href = addUtmParameterToURL(this.href, this.utmParameter);
        this.damContent = StringUtils.startsWith(this.href, CONTENT_DAM);

        this.href = Optional.of(this.href)
            .filter(LinkModel::isInternal)
            .filter(s -> !StringUtils.startsWith(s, CONTENT_DAM)) // do not add html extension for paths under /content/dam
            .map(this::addHtmlToInternalURL)
            .orElse(this.href);

        if (StringUtils.isBlank(this.target) && StringUtils.isNotBlank(this.href)) {
            // if the target property is not configured, external links will open in a new window and internal links in the same window
            this.target = isInternal(this.href) ? Target.SELF.getValue() : Target.BLANK.getValue();
        } else {
            this.target = Target.getEnumByValue(this.target).value;
        }

        if (registeredDomainsService != null) {
            registeredDomains = registeredDomainsService.getRegisteredDomains();
        }

        setAssetType();
    }

    public boolean isDamContent() {
        return damContent;
    }

    /**
     * Checks if the link has content attached.
     *
     * @return {@code true} if the link has content; otherwise {@code false}.
     */
    public boolean hasContent() {
        return StringUtils.isNotBlank(this.href) && StringUtils.isNotBlank(this.text);
    }

    private String addHtmlToInternalURL(String path) {
        if (!StringUtils.containsIgnoreCase(path, DOT_HTML)) {
            // If Link Destination is set to "#jumpTo".
            if (StringUtils.startsWith(path, HASH)) {
                return path;
            }
            // If Link Destination is set to "/content/softwareag/currentPage#jumpTo".
            int hashIndex = path.indexOf(HASH);
            if (StringUtils.contains(path, HASH) && !StringUtils.contains(path, Q_MARK)) {
                path = path.substring(0, hashIndex) + DOT_HTML + path.substring(hashIndex);
                return path;
            }
            // If UTM param is used "utm_source=facebook...." and no matter what Link destination pattern is used.
            int qMarkIndex = path.indexOf(Q_MARK);
            if (StringUtils.contains(path, Q_MARK)) {
                path = path.substring(0, qMarkIndex) + DOT_HTML + path.substring(qMarkIndex);
                return path;
            }
            path += DOT_HTML;
        }
        return path;
    }

    private String addUtmParameterToURL(String href, String utmParameter) {
        if (!utmParameter.isEmpty()) {
            int hashIndex = href.indexOf(HASH);

            if (utmParameter.charAt(0) != '?') {
                utmParameter = '?' + utmParameter;
            }

            if (href.contains(HASH)) {
                href = href.substring(0, hashIndex) + utmParameter + href.substring(hashIndex);
            } else {
                href += utmParameter;
            }
            return href;
        }
        return href;
    }

    public String getRawHref() {
        return rawHref;
    }

    public void setRawHref(final String rawHref) {
        this.rawHref = rawHref;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getHref() {
        return href;
    }
    
    public void setHref(String href) {
		this.href = href;
	}
    
    public String getUtmParameter() {
        return utmParameter;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setUtmParameter(final String utmParameter) {
        this.utmParameter = utmParameter;
    }

    /**
     * This enum represents the possible values a target-attribute may adopt.
     */
    public enum Target {
        DEFAULT("default"),
        BLANK("_blank"),
        SELF("_self"),
        PARENT("_parent"),
        TOP("_top"),
        FRAMENAME("framename");

        private final String value;
        private static final Map<String, Target> TARGET_VALUE_MAP = new HashMap<>();

        static {
            final Target[] enums = values();
            for (final Target anEnum : enums) {
                TARGET_VALUE_MAP.put(anEnum.value, anEnum);
            }
        }

        Target(final String value) {
            this.value = value;
        }

        /**
         * Checks whether the given value can adopt the value of a valid target-attribute
         * and returns the appropriate {@link Target} constant.
         *
         * @param value
         *     the target-attribute
         * @return the appropriate {@link Target} constant, otherwise {@link Target#SELF} is returned.
         */
        public static Target getEnumByValue(final String value) {
            Target ret = SELF;
            if (StringUtils.isNotEmpty(value)) {
                String lowerCaseValue = value.toLowerCase(Locale.ENGLISH);
                Target targetByValue = TARGET_VALUE_MAP.get(lowerCaseValue);
                if (targetByValue == null) {
                    targetByValue = TARGET_VALUE_MAP.get("_" + lowerCaseValue);
                }
                if (targetByValue != null) {
                    ret = targetByValue;
                }
            }
            return ret;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * Call this method when you need shortened url like passing data as json or in JS
     * We have 3 options for rewriting :
     * <ol>
     *     <li>resourceResolver.map(resourcePath);</li>
     *     <li>resourceResolver.map(request, resourcePath);</li>
     *     <li>externalizer.relativeLink(request, resourcePath);</li>
     * </ol>
     *
     * @return this
     */
    public LinkModel rewriteLink() {
        if (!LinkModel.isInternal(this.href) || !StringUtils.contains(href, LinkModel.DOT_HTML)) {
            return this;
        }
        final String resourcePath = StringUtils.substring(href, 0, href.indexOf(LinkModel.DOT_HTML));
        final String pathEnding = StringUtils.substring(href, href.indexOf(LinkModel.DOT_HTML));
        final String mappedResourcePath = resourceResolver.map(resourcePath);
        this.href = mappedResourcePath + pathEnding;

        return this;
    }

    private void setAssetType() {
        if (isDamContent()) {
            assetType = "dam";
        } else if (isInternalAsset(this.href)) {
            assetType = "internal";
        } else {
            assetType = "external";
        }
    }    
    
    
}
