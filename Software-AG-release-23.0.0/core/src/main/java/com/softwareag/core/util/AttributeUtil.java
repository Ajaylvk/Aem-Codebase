package com.softwareag.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides data-attrib related utility methods.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
public class AttributeUtil {

    private AttributeUtil() {
        // Class is not meant to be instantiated.
    }

    /**
     * Replaces empty lines with space. However, no empty space will be added, if the previous character
     * is already an empty character.
     */
    public static String removeEmptyLines(final String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (sb.toString().endsWith(" ") && ch == ' ') {
                continue;
            }
            if (ch == '\r' || ch == '\n' || ch == '\t') {
                if (sb.length() > 0 && !Character.isWhitespace(sb.charAt(sb.length() - 1))) {
                    sb.append(' ');
                }
            } else {
                sb.append(ch);
            }
        }

        return sb.toString().trim();
    }

}
