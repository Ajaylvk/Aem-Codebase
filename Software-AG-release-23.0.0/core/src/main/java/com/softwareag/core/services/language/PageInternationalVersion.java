package com.softwareag.core.services.language;

import com.softwareag.core.constants.NavigationConstants;

public class PageInternationalVersion {

    private final String languageHref;

    private final String localeString;

    public PageInternationalVersion(final String languageHref, final String localeString) {
        this.languageHref = languageHref;
        this.localeString = localeString;
    }

    public String getLanguageHref() {
        return languageHref;
    }

    public String getLocaleString() { return localeString; }

    public Boolean isXDefault() {
        return NavigationConstants.CORPORATE_LANGUAGE.equalsIgnoreCase(localeString) || "en".equalsIgnoreCase(localeString);
    }
}
