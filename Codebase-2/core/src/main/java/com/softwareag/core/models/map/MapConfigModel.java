package com.softwareag.core.models.map;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.AbstractConfigurationComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Objects;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MapConfigModel extends AbstractConfigurationComponent {

    private static final String LATITUDE_PN = "latitude";
    private static final String LONGITUDE_PN = "longitude";
    private static final String ZOOM_LEVEL_PN = "zoomLevel";
    private static final String DEFAULT_LATITUDE = "13.0612527";
    private static final String DEFAULT_LONGITUDE = "24.2707508";
    private static final String DEFAULT_ZOOM_LEVEL = "2";

    private static final String MAP_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/map-config";

    @ScriptVariable
    protected Resource resource;

    @ScriptVariable
    protected Page currentPage;

    private Resource mapConfigResource;

    @PostConstruct
    protected void init() {
        mapConfigResource = getConfigurationResource();
    }

    /**
     * Gets the configured Latitude
     *
     * @return {@link String} with the configured default map latitude value.
     */
    public String getLatitude() {
        return getConfigValue(LATITUDE_PN, DEFAULT_LATITUDE);
    }

    /**
     * Gets the configured Longitude
     *
     * @return {@link String} with the configured default map longitude value.
     */
    public String getLongitude() {
        return getConfigValue(LONGITUDE_PN, DEFAULT_LONGITUDE);
    }

    /**
     * Gets the configured zoom level
     *
     * @return {@link String} with the configured default map latitude value.
     */
    public String getZoomLevel() {
        return getConfigValue(ZOOM_LEVEL_PN, DEFAULT_ZOOM_LEVEL);
    }

    @Override
    public Resource getResource() {
        if (resource.isResourceType(MAP_CONFIG_RESOURCE_TYPE)) {
            return resource;
        } else {
            if (currentPage != null) {
                return Objects.requireNonNull(currentPage).getContentResource();
            } else {
                return null;
            }
        }
    }

    @Override
    public String getConfigResourceType() {
        return MAP_CONFIG_RESOURCE_TYPE;
    }

    public String getConfigurationPath() {
        if (mapConfigResource != null) {
            return resource.getPath();
        } else {
            return StringUtils.EMPTY;
        }
    }

    private String getConfigValue(final String propertyName, final String defaultValue) {
        if (mapConfigResource != null) {
            ValueMap map = mapConfigResource.getValueMap();
            return map.getOrDefault(propertyName, defaultValue).toString();
        } else {
            return defaultValue;
        }
    }
}
