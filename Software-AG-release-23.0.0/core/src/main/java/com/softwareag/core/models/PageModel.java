package com.softwareag.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource pageResource;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap pageProperties;

    @ValueMapValue
    private String productCategoryTag;
    @ValueMapValue
    private String productNameTag;
    @ValueMapValue
    private String[] productCapabilityTags;
    @ValueMapValue
    private String productUsageLevelTag;
    @ValueMapValue
    protected boolean campaignLandingPage;

    private String templateName;
    private String bundleVersion;

    private String productCategory;
    private String productName;
    private String[] productCapabilities;
    private String productUsageLevel;

    @PostConstruct
    protected void initModel() {
        templateName = extractTemplateName();

        productCategory = resolveTagTitle(productCategoryTag);
        productName = resolveTagTitle(productNameTag);
        productCapabilities = resolveTagTitles(productCapabilityTags);
        productUsageLevel = resolveTagTitle(productUsageLevelTag);
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductName() {
        return productName;
    }

    public List<String> getProductCapabilities() {
        return Collections.unmodifiableList(Arrays.asList(productCapabilities));
    }

    public String getProductUsageLevel() {
        return productUsageLevel;
    }

    public boolean isCampaignLandingPage() {
        return campaignLandingPage;
    }

    public String getBundleVersion() {
        if (StringUtils.isBlank(bundleVersion)) {
            Bundle currentBundle = FrameworkUtil.getBundle(PageModel.class);
            bundleVersion = Optional.ofNullable(currentBundle).map(Bundle::getVersion).map(Version::toString)
                .orElse(StringUtils.EMPTY);
        }
        return bundleVersion;
    }

    public String getLanguage() {
        return currentPage == null ? Locale.getDefault().toLanguageTag()
                                   : currentPage.getLanguage(false).toLanguageTag();
    }

    public String getTemplateName() {
        return templateName;
    }

    private String extractTemplateName() {
        String extractedTemplateName = null;
        final String templatePath = Optional.ofNullable(pageProperties)
            .map(valueMap -> valueMap.get(NameConstants.PN_TEMPLATE, String.class)).orElse(StringUtils.EMPTY);
        if (StringUtils.isNotBlank(templatePath)) {
            int i = templatePath.lastIndexOf('/');
            if (i >= 0) {
                extractedTemplateName = templatePath.substring(i + 1);
            }
        }
        return extractedTemplateName;
    }

    private Tag resolveTag(final String tagPath) {
        if (StringUtils.isNotBlank(tagPath)) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            if (tagManager != null) {
                return tagManager.resolve(tagPath);
            }
        }
        return null;
    }

    private String resolveTagTitle(final String tagPath) {
        final Tag tag = resolveTag(tagPath);
        if (tag != null) {
            return tag.getTitle();
        }
        return null;
    }

    private String[] resolveTagTitles(final String[] tagPaths) {
        if (ArrayUtils.isNotEmpty(tagPaths)) {
            final List<String> tagTitles = new ArrayList<>();
            for (final String tagPath : tagPaths) {
                final Tag tag = resolveTag(tagPath);
                if (tag != null) {
                    tagTitles.add(tag.getTitle());
                }
            }
            return tagTitles.toArray(new String[0]);
        }
        return new String[0];
    }

}
