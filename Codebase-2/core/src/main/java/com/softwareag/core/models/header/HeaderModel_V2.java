package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.AbstractConfigurationComponent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = HeaderModel_V2.HEADER_CONFIG_RESOURCE_TYPE)
public class HeaderModel_V2 extends AbstractConfigurationComponent {
    public static final String HEADER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/header-v2";

    private static final Logger LOG = LoggerFactory.getLogger(HeaderModel_V2.class);

    @ValueMapValue
    @Getter
    @Setter
    private String logoenabler;
    @ScriptVariable
    private Resource resource;

    @ScriptVariable
    private Page currentPage;

    @Override
    public Resource getResource() {

        Resource HeaderResource;

        if (StringUtils.equals(HEADER_CONFIG_RESOURCE_TYPE, resource.getResourceType())) {
            HeaderResource = resource;
        } else {
            HeaderResource = Optional.ofNullable(currentPage).map(Page::getContentResource).orElse(null);
        }

        return HeaderResource;
    }

    @Override
    public String getConfigResourceType() {
        return HEADER_CONFIG_RESOURCE_TYPE;
    }

    public String getConfigurationPath() {
        LOG.info("configuration path"+Optional.ofNullable(getConfigurationResource()).map(Resource::getPath).orElse(StringUtils.EMPTY));
        return Optional.ofNullable(getConfigurationResource()).map(Resource::getPath).orElse(StringUtils.EMPTY);
    }
}
