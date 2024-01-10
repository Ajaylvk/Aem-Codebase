package com.softwareag.empower.core.utils;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

public class AEMUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AEMUtil.class);

    private static final String CONTENT_PATH = "/content";
    private static final String HTML_EXTENSION = ".html";

    public static String getURL (String pagePath, ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        String url = pagePath;
        if (StringUtils.isNotBlank(pagePath) && resourceResolver != null && request != null) {
            if (StringUtils.startsWith(pagePath, CONTENT_PATH)) {
                pagePath = StringUtils.appendIfMissing(pagePath, HTML_EXTENSION);
                url = resourceResolver.map(request, pagePath);
            }
        }
        return url;
    }

    public static Cookie setCookie (String cookieName, String cookieValue, int expiry, String path, boolean isHttpOnly, boolean isSecure) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(expiry);
        cookie.setPath(path);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(isSecure);
        return cookie;
    }

    public static Cookie expireCookie (String cookieName, String path) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath(path);
        return cookie;
    }

    public static String getProtectedText(CryptoSupport cryptoSupport, String plainText) {
        String protectedText = null;
        try {
            protectedText = cryptoSupport.protect(plainText);
        } catch (CryptoException cryptoException) {
            LOG.error("CryptoException occurred while encrypting plain text : ", cryptoException);
        }
        return protectedText;
    }

    public static String getUnprotectedText(CryptoSupport cryptoSupport, String protectedText) {
        String plainText = null;
        try {
            plainText = cryptoSupport.unprotect(protectedText);
        } catch (CryptoException cryptoException) {
            LOG.error("CryptoException occurred while decrypting protected text {} : {}", protectedText, cryptoException);
        }
        return plainText;
    }

}
