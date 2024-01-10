package com.softwareag.core.models.popup;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.ConfigurationFinder;
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
public class PopUpRedirectModel {

    @OSGiService
    private LanguageService languageService;

    @ScriptVariable
    private Page currentPage;
    private Locale pageLocale;

    private PopUpConfigModel config;

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(currentPage, Locale.US);

        final Resource configResource = currentPage == null ? null : ConfigurationFinder.getConfigurationComponent(currentPage,
            PopUpConfigModel.POPUP_CONFIG_RESOURCE_TYPE);
        config = Optional.ofNullable(configResource)
            .map(r -> r.adaptTo(PopUpConfigModel.class))
            .orElse(null);
    }

    public Locale getPageLocale() {
        return pageLocale;
    }

    public PopUpConfigModel getConfig() {
        return config;
    }
}
