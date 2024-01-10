package com.softwareag.empower.core.servlets;

import com.adobe.granite.crypto.CryptoSupport;
import com.softwareag.empower.core.configurations.AuthConfig;
import com.softwareag.empower.core.utils.AEMUtil;
import com.softwareag.empower.core.utils.APIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Base64;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Designate(ocd = AuthConfig.class)
@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                SLING_SERVLET_PATHS + "=" + LoginCallbackServlet.SERVLET_PATH
        }
)
public class LoginCallbackServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LoginCallbackServlet.class);
    protected static final String SERVLET_PATH = "/bin/logincallback";

    private static final String GRANT_TYPE = "authorization_code";

    public static final String APPLICATION_JSON = "application/json";

    public static final String ACCEPT = "Accept";

    private String clientId;
    private String clientSecret;
    private String serviceUrl;
    private String redirectUri;

    @Activate
    protected void activate(final AuthConfig config) {
        serviceUrl = config.service_url();
        clientId = config.client_id();
        clientSecret = config.client_secret();
        redirectUri = config.redirect_uri();
        LOG.debug("Activate :: Service URL : {}, Client ID : {}, Redirect URI : {}", serviceUrl, clientId, redirectUri);
    }

    @Modified
    protected void modified(final AuthConfig config) {
        serviceUrl = config.service_url();
        clientId = config.client_id();
        clientSecret = config.client_secret();
        redirectUri = config.redirect_uri();
        LOG.debug("Modified :: Service URL : {}, Client ID : {}, Redirect URI : {}", serviceUrl, clientId, redirectUri);
    }

    @Reference
    private transient CryptoSupport cryptoSupport;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        String jsonString = "{}";

        String authCode = request.getRequestParameter("code") != null
                ? request.getRequestParameter("code").getString()
                : "";

        String redirectPath = request.getRequestParameter("state") != null
                ? request.getRequestParameter("state").getString()
                : "/";

        LOG.debug("Auth code :: {}", authCode);
        if (StringUtils.isNotBlank(authCode)) {

            Map<String, String> postParams = new HashMap<>();
            postParams.put("grant_type", GRANT_TYPE);
            postParams.put("code", authCode);
            postParams.put("redirect_uri", redirectUri);

            Map<String, String> headersMap = new HashMap<>();
            String authorization = "Basic" + " " + Base64.encode(clientId + ":" + clientSecret);
            headersMap.put("Authorization", authorization);
            headersMap.put(ACCEPT, APPLICATION_JSON);

            jsonString = APIUtil.executePostCall(serviceUrl, headersMap, postParams);

            //if (statusCode == 200) {
                try {
                    if (StringUtils.isNotBlank(jsonString)) {
                        JSONObject object = new JSONObject(jsonString);
                        String accessToken = object.getString("access_token");
                        String refreshToken = object.getString("refresh_token");
                        String idToken = object.getString("id_token");

                        response.addCookie(AEMUtil.setCookie("isLoggedIn", accessToken,
                                900, "/", false, true));
                        response.addCookie(AEMUtil.setCookie("refresh-token",
                                AEMUtil.getProtectedText(cryptoSupport, refreshToken),
                                86400, "/", true, true));
                    }
                } catch (JSONException e) {
                    LOG.error("Exception happened during parsing JSON response :: ", e);
                }
            //}
        } else {
            LOG.info("Auth code parameter not available!");
        }
        response.setStatus(302);
        response.sendRedirect(redirectPath);

    }

}
