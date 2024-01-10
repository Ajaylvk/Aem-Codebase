package com.softwareag.core.models.utm;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.models.GeneralConfigModel;
import com.softwareag.core.models.popup.PopUpConfigModel;
import com.softwareag.core.services.language.LanguageService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UTMParameterModel {

    @ScriptVariable
    private Page currentPage;
    private GeneralConfigModel config;

    @PostConstruct
    private void init() {
        final Resource configResource = currentPage == null ? null : ConfigurationFinder.getConfigurationComponent(currentPage,
            GeneralConfigModel.GENERAL_CONFIG_RESOURCE_TYPE

        );
        config = Optional.ofNullable(configResource)
            .map(r -> r.adaptTo(GeneralConfigModel.class))
            .orElse(null);
    }

    public GeneralConfigModel getConfig() {
        return config;
    }
}
