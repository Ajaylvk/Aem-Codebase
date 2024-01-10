package com.softwareag.core.configuration;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.util.PageUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Optional;

/**
 * Abstract class for Configuration Components
 */

public abstract class AbstractConfigurationComponent {

    private static final String MESSAGE = "More than one instance of this Configuration Component exists on this page.";

    /**
     * Retrieves a given resource.
     */
    public abstract Resource getResource();

    /**
     * Retrieves a given resourceType.
     */
    public abstract String getConfigResourceType();

    /**
     * Gets the validation Message.
     *
     * @return {@link String} containing the validation message.
     */
    public String getValidationMessage() {
        return AbstractConfigurationComponent.MESSAGE;
    }

    /**
     * Gets the validation Message.
     *
     * @return {@link boolean} 'true' if the resource has duplicates, 'false' otherwise.
     */
    public boolean isNotValid() {
        return existsDuplicateComponent();
    }

    /**
     * Gets the configuration resource for a given resourceType.
     *
     * @return {@link Resource} with the configuration resource for a given resourceType.
     */
    protected Resource getConfigurationResource() {
        Optional<Page> containingPage = PageUtil.getPageFromResource(getResource());
        return containingPage
            .map(page -> ConfigurationFinder.getConfigurationComponent(page, getConfigResourceType()))
            .orElse(null);
    }

    private boolean existsDuplicateComponent() {
        final Resource resource = getResource();

        if (resource == null || resource.getParent() == null || StringUtils.isBlank(resource.getResourceType())) {
            return false;
        }

        Iterable<Resource> iterator = resource.getParent().getChildren();
        for (Resource siblingResource : iterator) {
            if (!StringUtils.equals(siblingResource.getPath(), resource.getPath()) && siblingResource.isResourceType(resource.getResourceType())) {
                return true;
            }
        }

        return false;
    }

}
