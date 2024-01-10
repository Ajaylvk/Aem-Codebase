package com.softwareag.core.models;

import com.softwareag.core.configuration.AbstractConfigurationComponent;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GeneralConfigModel extends AbstractConfigurationComponent {
    public static final String GENERAL_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/general-config";

    @Self
    private Resource currentResource;

    @ValueMapValue
    private String dateFormat;

    @ValueMapValue
    private String timeFormat;

    @ValueMapValue
    private Boolean enableUtm;

    @ValueMapValue
    private String paramPrefix;

    public Boolean getEnableUtm() {
        return enableUtm;
    }

    public void setEnableUtm(Boolean enableUtm) {
        this.enableUtm = enableUtm;
    }

    public String getParamPrefix() {
        return paramPrefix;
    }

    public void setParamPrefix(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return GENERAL_CONFIG_RESOURCE_TYPE;
    }
}