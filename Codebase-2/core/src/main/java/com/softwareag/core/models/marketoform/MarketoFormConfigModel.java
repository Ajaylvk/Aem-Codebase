package com.softwareag.core.models.marketoform;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.AbstractConfigurationComponent;




@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MarketoFormConfigModel extends AbstractConfigurationComponent {

	private static final String MARKETO_ENDPOINT = "marketoEndpoint";

	private static final String MARKETO_MUNCHKIN_ACCOUNT_ID = "munchkinAccountId";

	private static final String MARKETO_SERVER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/marketo-form";

	@ScriptVariable
	private Resource resource;

	@ScriptVariable
	private Page currentPage;

	/**
	 * Gets the configured Marketo endpoint
	 *
	 * @return {@link String} with the configured Marketo endpoint.
	 */
	public String getMarketoEndpoint() {
		Resource marketoServerConfigResource = getConfigurationResource();
		String marketoEndpoint = "";
		if (marketoServerConfigResource != null) {
			ValueMap map = marketoServerConfigResource.getValueMap();
			marketoEndpoint = map.getOrDefault(MARKETO_ENDPOINT, "").toString();
		}

		return marketoEndpoint;
	}

	/**
	 * Gets the configured Marketo Munchkin Account Id
	 *
	 * @return {@link String} with the configured Marketo endpoint.
	 */
	public String getMarketoMunchkinAccountId() {
		Resource marketoServerConfigResource = getConfigurationResource();
		String marketoMunchkinAccountId = "";
		if (marketoServerConfigResource != null) {
			ValueMap map = marketoServerConfigResource.getValueMap();
			marketoMunchkinAccountId = map.getOrDefault(MARKETO_MUNCHKIN_ACCOUNT_ID, "").toString();
		}

		return marketoMunchkinAccountId;
	}

	@Override
	public Resource getResource() {
		Resource marketoServerConfigResource;

		if (StringUtils.equals(MARKETO_SERVER_CONFIG_RESOURCE_TYPE, resource.getResourceType())) {
			marketoServerConfigResource = resource;
		} else {
			marketoServerConfigResource = Optional.ofNullable(currentPage).map(Page::getContentResource).orElse(null);
		}
		return marketoServerConfigResource;
	}

	@Override
	public String getConfigResourceType() {
		return MARKETO_SERVER_CONFIG_RESOURCE_TYPE;
	}

	public String getConfigurationPath() {
		return Optional.ofNullable(getConfigurationResource()).map(Resource::getPath).orElse(StringUtils.EMPTY);
	}
}
