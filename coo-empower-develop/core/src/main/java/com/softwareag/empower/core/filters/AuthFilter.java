package com.softwareag.empower.core.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.softwareag.empower.core.utils.AEMUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Auth filter that restricts access to secure content.
 */
@Component(service = Filter.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=" + "/content/empower-sag/customer/en/.*",
                EngineConstants.SLING_FILTER_EXTENSIONS + "=" + "html"
        })
@ServiceDescription("Restrict access to secure content")
@ServiceRanking(-700)
@Designate(ocd = AuthFilter.Config.class)
public class AuthFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String PROPERTY_IS_SECURE = "isSecure";
    private static final String PROPERTY_LOGIN_PAGE = "loginPagePath";
    private static final String OAUTH_LOGIN_COOKIE = "isLoggedIn";

    @ObjectClassDefinition(
            name = "Empower Authentication Filter Configurations",
            description = "OSGi Service providing Empower Authentication Filter configuration parameters"
    )
    @interface Config {

        @AttributeDefinition(
                name = "Authentication cookie name",
                description = "Name of the authentication cookie based on which secure pages will get rendered",
                type = AttributeType.STRING
        )
        String cookie_name() default OAUTH_LOGIN_COOKIE;
    }

    private String cookieName;

    @Activate
    protected void activate(final Config config) {
        cookieName = config.cookie_name();
        logger.debug("Activate :: Cookie name : {}", cookieName);
    }

    @Modified
    protected void modified(final Config config) {
        cookieName = config.cookie_name();
        logger.debug("Modified :: Cookie name : {}", cookieName);
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain filterChain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse)response;

        ResourceResolver resolver = slingRequest.getResourceResolver();
        Resource res = resolver.getResource(slingRequest.getResource().getPath());

        String isSecure = null;
        String loginPage = null;
        if (res != null) {
            logger.debug("Resource Path :: {}", res.getPath());
            Resource contentRes = res.getChild(JcrConstants.JCR_CONTENT);
            InheritanceValueMap inheritedProp = new HierarchyNodeInheritanceValueMap(contentRes);
            isSecure = inheritedProp.getInherited(PROPERTY_IS_SECURE, String.class);
            logger.debug("Is secure? :: {}", isSecure);
            loginPage = inheritedProp.getInherited(PROPERTY_LOGIN_PAGE, String.class);
            logger.debug("Login Page path :: {}", loginPage);
        }

        if (StringUtils.isNotBlank(isSecure)) {
            Cookie accessTokenCookie = slingRequest.getCookie(OAUTH_LOGIN_COOKIE);
            if (accessTokenCookie == null) {
                String referrerUrlParam = "referrer=".concat(AEMUtil.getURL(res.getPath(), resolver, slingRequest));
                loginPage = StringUtils.isNotBlank(loginPage)
                        ? AEMUtil.getURL(loginPage, resolver, slingRequest).concat("?").concat(referrerUrlParam)
                        : "/?error=login-required&".concat(referrerUrlParam);
                logger.debug("Missing Login cookie. Redirecting to Login page :: {}", loginPage);
                slingResponse.setStatus(302);
                slingResponse.sendRedirect(loginPage);
            }
            slingResponse.setHeader("Dispatcher", "no-cache");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}