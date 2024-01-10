package com.softwareag.core.servlets.component;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_SELECTORS;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softwareag.core.models.resourcelibrary.ResourceLibraryModel;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import com.softwareag.core.services.resourcelibrary.ResourceLibraryService;

@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                SLING_SERVLET_RESOURCE_TYPES + "=" + ResourceLibServlet.RESOURCE_TYPE,
                SLING_SERVLET_EXTENSIONS + "=" + ResourceLibServlet.EXTENSION_JSON,
                SLING_SERVLET_SELECTORS + "=" + "assetdata"
        }
)
public class ResourceLibServlet extends SlingAllMethodsServlet {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceLibServlet.class);
	 protected static final String SELECTOR = "assetdata";
	    protected static final String EXTENSION_JSON = "json";
		//public static final String RESOURCE_TYPE = "softwareag/components/content/resourcelibrary";
	    public static final String RESOURCE_TYPE = "softwareag/components/structure/basic-page";
		
		@Reference
		ResourceLibraryService resourceLibraryService;
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String inputJson = request.getRequestParameter("inputJson").toString();
		LOG.debug("inputJSON::::"+inputJson.toString());
		RequestObject requestObj = new RequestObject();
		if(StringUtils.isNotEmpty(inputJson)) {
	       JsonParser parse  = new JsonParser();
	       JsonObject inputJsonObj  = (JsonObject)parse.parse(inputJson);
	       Gson gson = new Gson();
	       requestObj = gson.fromJson(inputJsonObj, RequestObject.class);
	       LOG.debug("assetpath:::::"+requestObj.getAssetpath());
		}else {
			LOG.debug("inputJSON is empty "+inputJson);
		}
	     
	        JsonObject rootObj = new JsonObject();
	        Resource resource = request.getResource();
	        LOG.debug("request resource :::"+resource.getName());
	       // LOG.info("resource child:::"+resource.getChild("resourcelibrary").getName());
	       // ResourceLibraryModel model = resource.adaptTo(ResourceLibraryModel.class);
	        
	       rootObj =  resourceLibraryService.getResponse(requestObj, request);
	      LOG.debug("final json response::::"+rootObj.toString());
	      response.setContentType("application/json");
          response.setHeader("Cache-Control", "nocache");
          response.setCharacterEncoding("utf-8");
          PrintWriter out = null;
          out = response.getWriter();
          out.println(rootObj.toString());
           
	}
	
}
