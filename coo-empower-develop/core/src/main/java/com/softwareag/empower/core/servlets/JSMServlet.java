package com.softwareag.empower.core.servlets;

import com.softwareag.empower.core.configurations.AuthConfig;
import com.softwareag.empower.core.utils.APIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Designate(ocd = AuthConfig.class)
@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                SLING_SERVLET_PATHS + "=" + JSMServlet.SERVLET_PATH
        }
)
public class JSMServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(JSMServlet.class);
    protected static final String SERVLET_PATH = "/bin/jsm";

    public static final String APPLICATION_JSON = "application/json";

    public static final String ACCEPT = "Accept";

    public static final String CONTENT_TYPE = "Content-Type";

    private String serviceUrl;

    @Activate
    protected void activate(final AuthConfig config) {
        serviceUrl = config.service_url();
        LOG.debug("Activate :: Service URL : {}", serviceUrl);
    }

    @Modified
    protected void modified(final AuthConfig config) {
        serviceUrl = config.service_url();
        LOG.debug("Modified :: Service URL : {}", serviceUrl);
    }

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        String jsonString = "{}";

        String params = request.getQueryString();
        LOG.debug("Method :: {}", params);

        String accessToken = null;
        Cookie accessTokenCookie = request.getCookie("isLoggedIn");
        if (accessTokenCookie != null) {
            accessToken = accessTokenCookie.getValue();
        } else {
            LOG.info("Access token not available!");
            jsonString = "{Error: Access token not available!}";
            setResponse(response, jsonString);
            return;
        }

        if (StringUtils.isNotBlank(params)) {

            Map<String, String> headersMap = new HashMap<>();
            String authorization = "Bearer" + " " + accessToken;
            headersMap.put("Authorization", authorization);
            headersMap.put(CONTENT_TYPE, APPLICATION_JSON);
            //headersMap.put(ACCEPT, APPLICATION_JSON);

            jsonString = APIUtil.executeGetCall(serviceUrl.concat("?").concat(params), headersMap);


            //if (statusCode == 200) {
                try {
                    if (StringUtils.isNotBlank(jsonString)) {
                        LOG.debug("Response :: {}", jsonString);
                    }
                } catch (Exception e) {
                    LOG.error("Exception happened during parsing JSON response :: ", e);
                }
            //}
        } else {
            LOG.info("Method not available!");
            jsonString = "{Error: Method not available!}";
        }
        setResponse(response, jsonString);
    }

    private void setResponse (SlingHttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        final PrintWriter responseWriter = response.getWriter();
        responseWriter.write(message);
    }

}
