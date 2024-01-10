package com.softwareag.core.models.header;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.AbstractConfigurationComponent;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = HeaderModel_V1.HEADER_CONFIG_RESOURCE_TYPE)
public class HeaderModel_V1 extends AbstractConfigurationComponent {
	 public static final String HEADER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/header-v1";
	 public static final String HEADER_RESOURCE_TYPE = "softwareag/components/content/header-v1";
	
	@ValueMapValue
	    private String backgroundColor;
	 
	@ValueMapValue
	    private String type;
	
	@ValueMapValue
	    private String websites;
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
        return Optional.ofNullable(getConfigurationResource()).map(Resource::getPath).orElse(StringUtils.EMPTY);
    }
	
	public String getBackgroundColor()
	{
		return backgroundColor;
	}
	
	public String getType()
	{
		return type;
	}
	public String getWebsites()
	{
		return websites;
	}
}