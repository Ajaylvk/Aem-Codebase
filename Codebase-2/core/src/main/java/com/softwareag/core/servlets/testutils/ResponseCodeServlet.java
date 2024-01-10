package com.softwareag.core.servlets.testutils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

/** Servlet to deliver a test error 500. Only works on the error page resource type, eg:
 * http://localhost:4502/editor.html/content/softwareag/test/noncomponent/error-pages/errors/500.404.html **/

@Component(
    service = {Servlet.class},
    property = {
        SLING_SERVLET_RESOURCE_TYPES + "=/apps/softwareag/components/structure/errorpage",
        SLING_SERVLET_METHODS + "=GET",
        SLING_SERVLET_EXTENSIONS + "=html",
        SLING_SERVLET_SELECTORS + "=500",
    }
)
public class ResponseCodeServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
