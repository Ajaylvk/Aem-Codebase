package com.softwareag.core.models.popup;

import com.softwareag.core.configuration.AbstractConfigurationComponent;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PopUpConfigModel extends AbstractConfigurationComponent {

    public static final String POPUP_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/popup";

    @Self
    private Resource currentResource;

    @ValueMapValue
    private Boolean enableRegionalPopUp;

    @ValueMapValue
    private String regionalPopUpMessage;

    @ValueMapValue
    private String regionalLabelClose;

    @ValueMapValue
    private String regionalLabelRedirect;

    @ValueMapValue
    private Boolean enableRedirectPopUp;

    @ValueMapValue
    private String externalPopUpMessage;

    @ValueMapValue
    private String externalLabelClose;

    @ValueMapValue
    private String externalLabelRedirect;


    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return POPUP_CONFIG_RESOURCE_TYPE;
    }

    public Boolean getEnableRegionalPopUp() {
        return enableRegionalPopUp;
    }

    public String getRegionalPopUpMessage() {
        return regionalPopUpMessage;
    }

    public String getRegionalLabelClose() {
        return regionalLabelClose;
    }

    public String getRegionalLabelRedirect() {
        return regionalLabelRedirect;
    }

    public Boolean getEnableRedirectPopUp() {
        return enableRedirectPopUp;
    }

    public String getExternalPopUpMessage() {
        return externalPopUpMessage;
    }

    public String getExternalLabelClose() {
        return externalLabelClose;
    }

    public String getExternalLabelRedirect() {
        return externalLabelRedirect;
    }

}
