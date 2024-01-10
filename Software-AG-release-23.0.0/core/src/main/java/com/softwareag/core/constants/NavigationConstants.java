package com.softwareag.core.constants;

import java.util.regex.Pattern;

public final class NavigationConstants {
    public static final int TENANT_LEVEL = 1;
    public static final int COUNTRY_ROOT_LEVEL = 3;
    public static final int LANGUAGE_ROOT_LEVEL = COUNTRY_ROOT_LEVEL + 1;
    public static final String COUNTRY_ROOT_REGEX = "^[a-z]{2}$";
    public static final String LANGUAGE_ROOT_REGEX = "^[a-z]{2}_[a-z]{2}$";
    public static final String LANGUAGE_ROOT_PATH_REGEX = "^(/[^/.]+){" + LANGUAGE_ROOT_LEVEL + "}";
    public static final String COUNTRY_CORPORATE = "corporate";
    public static final String LANGUAGE_CORPORATE_REGEX = "^[a-z]{2}_" + COUNTRY_CORPORATE + "$";
    public static final String CORPORATE_LOCALE = "/en_corporate";
    public static final String CORPORATE_LANGUAGE = "en-US";
    public static final Pattern COUNTRY_ROOT_REGEX_PATTERN = Pattern.compile(COUNTRY_ROOT_REGEX);
    public static final Pattern LANGUAGE_ROOT_REGEX_PATTERN = Pattern.compile(LANGUAGE_ROOT_REGEX);
    public static final Pattern LANGUAGE_CORPORATE_REGEX_PATTERN = Pattern.compile(LANGUAGE_CORPORATE_REGEX);

    private NavigationConstants() {
        // Class is not meant to be instantiated.
    }
}
