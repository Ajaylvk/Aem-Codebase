package com.softwareag.core.servlets.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.Servlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;


@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Region Changer service",
                SLING_SERVLET_METHODS +"="+HttpMethod.POST,
                SLING_SERVLET_EXTENSIONS + "=" + "html",
                SLING_SERVLET_RESOURCE_TYPES + "=" + RegionChangerServlet.RESOURCE_TYPE,
                SLING_SERVLET_SELECTORS + "=" + "regionchanger"

        })
public class RegionChangerServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(RegionChangerServlet.class);

    public static final String RESOURCE_TYPE = "softwareag/components/structure/basic-page";
    private String pageLocale;
    private String currentPage;
    private String currentDomain;
    private String authoredUrl;
    private List<String> regionList=new ArrayList<>();

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {
        String path=StringUtils.EMPTY;
        String finalPath=StringUtils.EMPTY;
        try {
            setRequestAttributes(request);
            boolean identifier=isSag(currentPage,currentDomain);
            if (currentPage.startsWith("/content")) {
                path = substringNthOccurrence(currentPage, '/', 3).replace(".html", StringUtils.EMPTY);
            }
            else {
                path=currentPage.replace(".html", StringUtils.EMPTY);
            }
            LOG.debug("Path is " + path);
            for (String locale : regionList) {
                if (pageLocale.equals(locale)) {
                    if (identifier) {
                        finalPath =
                                checkIfResourceExists("/content/softwareag/" + locale.substring(3) + "/" + locale
                                        + (path.contains("/") ? "/" + substringNthOccurrence(path, '/', 1) : StringUtils.EMPTY), authoredUrl, request);
                    } else {
                        finalPath =
                                checkIfResourceExists("/content/investorrelation/" + locale.substring(3) + "/" + locale
                                        + (path.contains("/") ? "/" + substringNthOccurrence(path, '/', 1) : StringUtils.EMPTY), authoredUrl, request);
                    }
                    LOG.debug("Final Path is if" + finalPath);
                    break;
                }
            }
            regionList.clear();
            response.setContentType("text/plain");
            response.getWriter().println(finalPath);
            response.getWriter().close();
        }catch(Exception e){
            LOG.debug("Exception is:"+e);
        }
    }

    private boolean isSag(String currentPage, String currentDomain) {
        return currentPage.startsWith("/content/softwareag/") || currentDomain.equals("https://www.softwareag.com") || currentDomain.equals("https://software-ag-stg-65.adobecqms.net");
    }

    private void setRequestAttributes(SlingHttpServletRequest request) {
        String jsonStr = Objects.requireNonNull(request.getRequestParameter("reqValue")).toString();
        LOG.debug("Json String:"+jsonStr);
        Gson gson = new Gson();
        JsonObject jsonObj=gson.fromJson(jsonStr, JsonObject.class);
        pageLocale = jsonObj.get("country").getAsString();
        currentPage = jsonObj.get("current_page").getAsString();
        currentDomain = jsonObj.get("current_domain").getAsString();
        authoredUrl = jsonObj.get("current_authored_url").getAsString();
        JsonArray regionlistJsonArray = jsonObj.getAsJsonArray("regionlist");
        for (JsonElement element : regionlistJsonArray) {
            regionList.add(element.getAsString());
        }
    }

    private String checkIfResourceExists(String s, String authoredUrl, SlingHttpServletRequest request) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        LOG.debug("String passed into this method"+s);
        final Resource resource = resourceResolver.getResource(s);
        if (resource!=null&& !ResourceUtil.isNonExistingResource(resource)){
            if(resource.getChild(com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT)!=null) {
                return currentDomain + resource.getPath() + ".html";
            }
        }
        return authoredUrl;
    }

    public static String substringNthOccurrence(String string, char c, int n) {
        if (n <= 0) {
            return "";
        }
        int index = 0;
        while (n-- > 0 && index != -1) {
            index = string.indexOf(c, index + 1);
        }
        return index > -1 ? string.substring(index + 1, string.length()) : "";
    }

}
