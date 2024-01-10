package com.softwareag.empower.core.filters;

import com.day.cq.dam.api.Asset;
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Assets filter that restricts access to secure DAM assets.
 */
@Component(service = Filter.class,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=" + "/content/dam/empower-sag/secure/.*",
                EngineConstants.SLING_FILTER_RESOURCETYPES + "=" + "dam:Asset"
        })
@ServiceDescription("Restrict access to secure assets")
@ServiceRanking(-700)
@Designate(ocd = AssetsFilter.Config.class)
public class AssetsFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final String OAUTH_LOGIN_COOKIE = "isLoggedIn";

    @ObjectClassDefinition(
            name = "Empower Assets Filter Configurations",
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

        String assetName = StringUtils.EMPTY;
        if (res != null) {
            logger.debug("Resource Path :: {}", res.getPath());
            Asset asset = res.adaptTo(Asset.class);
            assetName = asset != null ? asset.getName() : "this asset";
        }

        slingResponse.setHeader("Dispatcher", "no-cache");
        Cookie accessTokenCookie = slingRequest.getCookie(OAUTH_LOGIN_COOKIE);
        if (accessTokenCookie == null) {
            logger.debug("Access token cookie not available!");
            String jsonString = "Forbidden: You don't have permission to access " + assetName + " on this server.";
            setResponse(slingResponse, jsonString);
        } else {
            logger.debug("Access token cookie is available!");
            filterChain.doFilter(request, response);
        }
    }

    private void setResponse (SlingHttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        final PrintWriter responseWriter = response.getWriter();
        responseWriter.write(message);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}