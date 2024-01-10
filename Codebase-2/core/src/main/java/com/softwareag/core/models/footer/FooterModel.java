package com.softwareag.core.models.footer;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.AbstractConfigurationComponent;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import java.util.Optional;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel extends AbstractConfigurationComponent {

    private static final String FOOTER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/footer";

    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private Page currentPage;

    @Override
    public Resource getResource() {
        Resource footerResource;

        if (StringUtils.equals(FOOTER_CONFIG_RESOURCE_TYPE, resource.getResourceType())) {
            footerResource = resource;
        } else {
            footerResource = Optional.ofNullable(currentPage).map(Page::getContentResource).orElse(null);
        }

        return footerResource;
    }

    @Override
    public String getConfigResourceType() {
        return FOOTER_CONFIG_RESOURCE_TYPE;
    }

    public String getConfigurationPath() {
        return Optional.ofNullable(getConfigurationResource()).map(Resource::getPath).orElse(StringUtils.EMPTY);
    }
}
