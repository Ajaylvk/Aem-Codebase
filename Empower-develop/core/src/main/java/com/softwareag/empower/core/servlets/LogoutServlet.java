package com.softwareag.empower.core.servlets;

import com.softwareag.empower.core.utils.AEMUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                SLING_SERVLET_PATHS + "=" + LogoutServlet.SERVLET_PATH
        }
)
public class LogoutServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LogoutServlet.class);
    protected static final String SERVLET_PATH = "/bin/logout";

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {

        LOG.debug("Expiring isLoggedIn and refresh-token cookies");
        response.addCookie(AEMUtil.expireCookie("isLoggedIn", "/"));
        response.addCookie(AEMUtil.expireCookie("refresh-token", "/"));

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        final PrintWriter responseWriter = response.getWriter();
        responseWriter.write("{Success}");

    }

}
