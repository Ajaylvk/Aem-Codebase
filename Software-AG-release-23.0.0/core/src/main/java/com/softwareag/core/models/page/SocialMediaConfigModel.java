package com.softwareag.core.models.page;

import com.softwareag.core.configuration.AbstractConfigurationComponent;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialMediaConfigModel extends AbstractConfigurationComponent {

    public static final String SOCIALMEDIA_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/socialmedia";
    private static final boolean DEFAULT_ENABLED_FACEBOOK = false;
    private static final String PATH_TO_IMAGE_PRESET = "/conf/global/settings/dam/dm/presets/macros";
    private static final String NO_IMAGE_PRESET_ERROR = "Warning: an image preset " + PageMetaModel.DM_PRESET_OPENGRAPH + " is NOT present.  This is required to serve the Facebook og:image property in the correct size";

    @Self
    private Resource currentResource;

    @ValueMapValue
    @Named("facebookSharingEnabled")
    @Default(booleanValues = DEFAULT_ENABLED_FACEBOOK)
    private boolean enabledFacebook;

    @ValueMapValue
    private String imageserverurl;

    @ValueMapValue
    private String assetID;

    private Boolean hasOpenGraphImagePreset = false;

    @PostConstruct
    private void init() {
        ResourceResolver resolver = currentResource.getResourceResolver();
        Resource oGImagePresetResource = resolver.getResource(PATH_TO_IMAGE_PRESET + "/" + PageMetaModel.DM_PRESET_OPENGRAPH);
        if (oGImagePresetResource != null ) {
            hasOpenGraphImagePreset = true;
        }
    }

    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return SOCIALMEDIA_CONFIG_RESOURCE_TYPE;
    }

    public boolean isEnabledFacebook() { return enabledFacebook; }

    public String getImageserverurl() { return imageserverurl; }

    public String getAssetID() {  return assetID; }

    public String getImagePresetWarning() { return hasOpenGraphImagePreset ? null : NO_IMAGE_PRESET_ERROR; }
}
