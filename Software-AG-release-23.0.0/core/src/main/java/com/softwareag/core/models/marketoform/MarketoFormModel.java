package com.softwareag.core.models.marketoform;


import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MarketoFormModel extends MarketoFormConfigModel {

	private static final String MARKETO_JS_URI = "/js/forms2/js/forms2.min.js";

	@ValueMapValue
	private String marketoFormId;

	@ValueMapValue
	private boolean showInLightbox;

	public boolean getShowInLightbox() {
		return showInLightbox;
	}

	public String getMarketoJSUrl() {
		String marketoJsUrl = "";
		String marketoEndpoint = getMarketoEndpoint();
		if (!marketoEndpoint.isEmpty()) {
			marketoJsUrl = marketoEndpoint + MARKETO_JS_URI;
		}
		return marketoJsUrl;
	}

	/**
	 * Gets the configured Marketo form id
	 *
	 * @return {@link String} with the configured Marketo form id.
	 */
	public String getMarketoFormId() {
		return marketoFormId;
	}

}
