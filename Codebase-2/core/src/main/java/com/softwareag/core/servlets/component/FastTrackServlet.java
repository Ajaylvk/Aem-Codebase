package com.softwareag.core.servlets.component;

import com.google.gson.*;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import com.softwareag.core.services.filterservices.FilterService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_SELECTORS;

@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= FastTrack Service Servlet",
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                SLING_SERVLET_RESOURCE_TYPES + "=" + FastTrackServlet.RESOURCE_TYPE,
                SLING_SERVLET_EXTENSIONS + "=" + FastTrackServlet.EXTENSION_JSON,
                SLING_SERVLET_SELECTORS + "=" + "fasttrackservicedata"
        }
)
public class FastTrackServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(FastTrackServlet.class);
    protected static final String EXTENSION_JSON = "json";
    public static final String SELECTOR = "fasttrackservicedata";

    public static final String RESOURCE_TYPE = "softwareag/components/structure/basic-page";

    @Reference
    FilterService filterService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String inputJson = request.getRequestParameter("inputJson").toString();
        List<String> dropDownOrder=new ArrayList<>();
        LOG.debug("inputJSON::::" + inputJson.toString());
        RequestObject requestObj = new RequestObject();
        if (StringUtils.isNotEmpty(inputJson)) {
            JsonParser parse = new JsonParser();
            JsonObject inputJsonObj = (JsonObject) parse.parse(inputJson);
            Gson gson = new Gson();
            requestObj = gson.fromJson(inputJsonObj, RequestObject.class);
            JsonArray dropdownorderJsonArray = inputJsonObj.getAsJsonArray("dropdown_order");
            for (JsonElement element : dropdownorderJsonArray) {
                dropDownOrder.add(element.getAsString());
            }
            LOG.debug("assetpath:::::" + requestObj.getAssetpath());
        } else {
            LOG.debug("inputJSON is empty " + inputJson);
        }
        JsonObject rootObj = new JsonObject();
        Resource resource = request.getResource();
        LOG.debug("request resource :::" + resource.getName());
        rootObj = filterService.getResponse(requestObj, request, FastTrackServlet.SELECTOR,dropDownOrder);
        LOG.debug("final json response::::" + rootObj.toString());
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "nocache");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        out = response.getWriter();
        out.println(rootObj.toString());
    }
}
