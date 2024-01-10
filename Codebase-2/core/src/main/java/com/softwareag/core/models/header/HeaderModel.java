package com.softwareag.core.models.header;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.NavigationConstants;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.util.ContentUtil;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,resourceType=HeaderModel.HEADER_RESOURCE_TYPE)
public class HeaderModel {

	public static final String HEADER_RESOURCE_TYPE = "softwareag/components/content/header";
	 public static final String HEADER_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/header";
	public static final int MAX_FIRST_LEVEL_NAV_ITEMS = 6;

	@OSGiService
	private LanguageService languageService;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	private Page currentPage;
	private Locale pageLocale;

	private HeaderConfigModel config;

	private final List<FirstLevelNavItem> firstLevelNavItems = new ArrayList<>();

	private LabelConfigModel labelConfigModel;

	@PostConstruct
	private void init() {
		pageLocale = languageService.getLocale(currentPage, Locale.US);
		String path = StringUtils.EMPTY;
		String locale = StringUtils.EMPTY;
		String finalPath = StringUtils.EMPTY;
		final Resource configResource = currentPage == null ? null
				: ConfigurationFinder.getConfigurationComponent(currentPage,
						HeaderConfigModel.HEADER_CONFIG_RESOURCE_TYPE);
		if(null != configResource) {
			config = Optional.ofNullable(configResource).map(r -> r.adaptTo(HeaderConfigModel.class)).orElse(null);
		}
		if(null != config) {
			if (config.getFirstIcon().getHref() != null && config.getTitle() != null && config.getTitle().contains("Investor")) {
				String href = config.getFirstIcon().getHref();
				if (currentPage.getPath().startsWith("/content")) {
					path = substringNthOccurrence(currentPage.getPath(), '/', 3);
				}
				if (pageLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
					finalPath = "/content/investorrelation/de/de_de"
							+ (path.contains("/") ? "/" + substringNthOccurrence(path, '/', 1) : StringUtils.EMPTY);
				} else if (pageLocale.getLanguage().equals(Locale.GERMAN.getLanguage())) {
					finalPath = "/content/investorrelation/en/en_en"
							+ (path.contains("/") ? "/" + substringNthOccurrence(path, '/', 1) : StringUtils.EMPTY);
				}
				Resource res = resourceResolver.resolve(finalPath);
				if (res != null && !ResourceUtil.isNonExistingResource(res)) {
					config.getFirstIcon().getLink().setHref(res.getPath());
				} else {
					config.getFirstIcon().setHref(href);
				}
			}
			labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);

			Optional<Page> homePageOptional = ContentUtil.findHomepage(currentPage);
			homePageOptional.ifPresent(
					homepage -> ContentUtil.getFirstNChildPages(homepage, false, MAX_FIRST_LEVEL_NAV_ITEMS).forEach(
							childPage -> this.firstLevelNavItems.add(new FirstLevelNavItem(childPage, labelConfigModel))));
		}

	
	}

	public static String substringNthOccurrence(String string, char c, int n) {
		if (n <= 0) {
			return "";
		}

		int index = 0;
		while (n-- > 0 && index != -1) {
			index = string.indexOf(c, index + 1);
			System.out.println("-- index --" + index);
		}
		// return index > -1 ? string.substring(0, index + 1) : "";
		return index > -1 ? string.substring(index + 1, string.length()) : "";
	}

	public Locale getPageLocale() {
		return pageLocale;
	}

	public String getSearchCountry() {
		if (currentPage == null || StringUtils.contains(currentPage.getPath(), NavigationConstants.COUNTRY_CORPORATE)) {
			return NavigationConstants.COUNTRY_CORPORATE;
		}
		return pageLocale.getCountry().toLowerCase();
	}

	public HeaderConfigModel getConfig() {
		return config;
	}

	public List<FirstLevelNavItem> getFirstLevelNavItems() {
		return new ArrayList<>(firstLevelNavItems);
	}

	public String getLabelBack() {
		return labelConfigModel != null ? labelConfigModel.getLabelBack() : "";
	}
}
