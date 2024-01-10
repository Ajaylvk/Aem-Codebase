package com.softwareag.core.configuration;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.softwareag.core.constants.Template;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.util.FunctionalUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;


public class ConfigurationFinder {

    private static final String SOFTWAREAG_RESOURCE_TYPE_PREFIX = "softwareag/components/structure";

    private ConfigurationFinder() {
    }

    public static Resource getConfigurationComponent(final Page containingPage, final String resourceType) {
        Resource configOnPage = checkConfigOnPage(containingPage, resourceType);
        if (configOnPage != null) {
            return configOnPage;
        }
        Iterator<Page> configPages = containingPage.listChildren(getConfigTemplatePageFilter(), false);
        if (configPages.hasNext()) {
            Page configPage = configPages.next();
            Resource configOnChildPage = checkConfigOnPage(configPage, resourceType);
            if (configOnChildPage != null) {
                return configOnChildPage;
            }
        }
        if (containingPage.getParent() != null) {
            return getConfigurationComponent(containingPage.getParent(), resourceType);
        } else {
            return null;
        }
    }

    /**
     * Finds the label configuration model that provides access to the configured labels of the configuration page.
     *
     * @param page
     *     as base where to search for the configuration page.
     * @return {@code Optional} of a label configuration model.
     */
    public static Optional<LabelConfigModel> findLabelConfigComponent(final Page page) {
        Resource labelConfigResource = null;
        if (page != null) {
            labelConfigResource = getConfigurationComponent(page, LabelConfigModel.LABEL_CONFIG_RESOURCE_TYPE);
        }
        return Optional.ofNullable(labelConfigResource)
            .map(r -> r.adaptTo(LabelConfigModel.class));
    }

    /**
     * Finds the General configuration model that provides access to the configured dateFormat of the configuration page.
     *
     * @param page
     *     as base where to search for the configuration page.
     * @return {@code Optional} of a General configuration model.
     */
    public static Optional<GeneralConfigModel> findGeneralConfigComponent(final Page page) {
        Resource generalConfigResource = null;
        if (page != null) {
            generalConfigResource = getConfigurationComponent(page, GeneralConfigModel.GENERAL_CONFIG_RESOURCE_TYPE);
        }
        return Optional.ofNullable(generalConfigResource)
            .map(r -> r.adaptTo(GeneralConfigModel.class));
    }

    private static Resource checkConfigOnPage(final Page configPage, final String resourceType) {
        Iterator<Resource> configComponents;
        if (resourceType.equals(GeneralConfigModel.GENERAL_CONFIG_RESOURCE_TYPE)) {
            configComponents = Optional.ofNullable(configPage)
                .map(Page::getContentResource)
                .map(Resource::listChildren)
                .orElse(Collections.emptyIterator());
        } else {
            configComponents = Optional.ofNullable(configPage)
                .map(Page::getContentResource)
                .map(contentResource -> contentResource.getChild("parsys"))
                .map(Resource::listChildren)
                .orElse(Collections.emptyIterator());
        }

        return FunctionalUtil.asStream(configComponents)
            .filter(configComponent -> StringUtils.equals(configComponent.getResourceType(), resourceType))
            .findFirst()
            .orElse(null);
    }

    private static PageFilter getConfigTemplatePageFilter() {
        return new PageFilter() {
            @Override
            public boolean includes(Page p) {
                return isValidConfigPage(p);
            }
        };
    }

    private static boolean isValidConfigPage(Page p) {
        if (!p.hasContent()) {
            return false;
        }
        Resource contentResource = p.getContentResource();
        if (contentResource == null) {
            return false;
        }
        String resourceType = contentResource.getResourceType();
        ValueMap props = p.getProperties();
        String cqTemplate = props.get(NN_TEMPLATE, String.class);
        return StringUtils.equals(cqTemplate, Template.CONFIG_PAGE.getPath())
            && StringUtils.startsWith(resourceType, SOFTWAREAG_RESOURCE_TYPE_PREFIX)
            && p.isValid() && (p.getDeleted() == null);
    }

}
