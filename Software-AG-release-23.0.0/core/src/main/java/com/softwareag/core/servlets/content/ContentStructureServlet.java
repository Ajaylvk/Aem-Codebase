package com.softwareag.core.servlets.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.oltu.oauth2.common.OAuth.HttpMethod;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.tika.mime.MediaType;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.softwareag.core.constants.Template;



/* This endpoint provides two functionalities:
 *   - Get a list of all error pages (used by error-page-cacher.sh script on dispatcher)
 *   - Get a list of all tenants, identified by having a homepage template (used for tenant/market specific backups)
 */


@SuppressWarnings("serial")
@Component(
	    service = Servlet.class,
	    property = {
	        Constants.SERVICE_DESCRIPTION + "= Content Structure Servlet",
	        ServletResolverConstants.SLING_SERVLET_PATHS+"=/bin/contentstructure",
	        ServletResolverConstants.SLING_SERVLET_METHODS+"="+HttpMethod.GET
	    })
@Designate(ocd = ContentStructureServlet.Configuration.class, factory = true)

public class ContentStructureServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentStructureServlet.class);
    
    @Reference
    private QueryBuilder builder;

    @ObjectClassDefinition(name = "Software AG Content Structure", description = "Configuration for the Content Structure endpoint")
    @interface Configuration {
        
        @AttributeDefinition(name = "Search limit", description = "Limit results for internal search query; actually returned results may be less due to excludes (see below)", type = AttributeType.INTEGER)
        int searchLimit() default 100;
        
        
        @AttributeDefinition(name = "Exclude list", description = "A list of path fragments that should be excluded (e.g. language-master) from the results", type = AttributeType.STRING)
        String[] excludePathes();

    }
    
    private String[] excludePathes;
    private int searchLimit;

    private static final String PARAMETER_CMD = "cmd";
    
    private enum COMMANDS {
    	errorPages, tenants
    }
    
    @Activate
    void activate(Configuration config) {
    	excludePathes = config.excludePathes() ;
    	searchLimit = config.searchLimit() ;
    	LOG.info("ContentStructureServlet activated with {} exludePathes and an internal limit of {}", excludePathes == null ? 0 : excludePathes.length, searchLimit);
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
    	
        response.setContentType(MediaType.TEXT_PLAIN.getType());
        ResourceResolver resourceResolver = request.getResourceResolver();
        if(resourceResolver == null || !resourceResolver.isLive()) {
        	LOG.debug("Could not retriebe a resource resolver from request.");
        	response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        String cmdParam = request.getParameter(PARAMETER_CMD);
        if(StringUtils.isEmpty(cmdParam)) {
        	LOG.debug("Called without/with empty parameter {}.", PARAMETER_CMD);
        	response.sendError(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        
        List<String> results;
        
        if(COMMANDS.errorPages.toString().equalsIgnoreCase(cmdParam)) {
        	results = getPages(resourceResolver, COMMANDS.errorPages);        	
        } else if(COMMANDS.tenants.toString().equalsIgnoreCase(cmdParam)) {
        	results = getPages(resourceResolver, COMMANDS.tenants);        	
        } else {
        	LOG.debug("Called with invalid parameter {}.", cmdParam);
        	response.sendError(HttpStatus.SC_BAD_REQUEST);
            return;
        }
        LOG.debug("Returning {} results", results.size());
        response.getWriter().append(results.toString().replaceAll(" ", ""));        
    }

    
    private List<String> getPages(ResourceResolver resourceResolver, COMMANDS type){
    	
    	List<String> resultList = new ArrayList<>();
    	
    	 Map<String, String> predicateMap=new HashMap<>();
    	 predicateMap.put ("path", "/content");
    	 predicateMap.put ("type", NameConstants.NT_PAGE);
    	 predicateMap.put("p.limit", Integer.toString(searchLimit));
    	 predicateMap.put ("property", NameConstants.NN_CONTENT + "/" + NameConstants.NN_TEMPLATE);
    	 if(type == COMMANDS.errorPages) {
    		 predicateMap.put ("property.value", Template.ERROR_PAGE.getPath());
    	 }
    	 else if(type == COMMANDS.tenants) {
    		 predicateMap.put ("property.value", Template.HOME_PAGE.getPath());
    	 } else {
    		 return resultList;
    	 }
    	 LOG.debug("{}", predicateMap);    	 
    	 
    	Query query = builder.createQuery(PredicateGroup.create(predicateMap), resourceResolver.adaptTo(Session.class));
    	SearchResult result = query.getResult();
    	Iterator<Resource> resourceIterator = result.getResources();
    	while (resourceIterator.hasNext()) {
    		String pagePath = resourceIterator.next().getPath();
    		if(type == COMMANDS.tenants && pagePath.length() > "/content/".length()) {
    			//pagePath = pagePath.substring(0, StringUtils.ordinalIndexOf(pagePath, "/", 3));
    			pagePath = pagePath.substring("/content/".length());
    			pagePath = pagePath.substring(0, pagePath.indexOf("/"));
    		}
   		 	LOG.trace("Handling pagePath={}", pagePath);
    		if(!isInExcludeList(pagePath) && !resultList.contains(pagePath)) {
    			resultList.add(pagePath);
    		} else {
    			LOG.trace("Ignoring pagePath due to exclude list or bbecause it's already contained in list: {}", pagePath);
    		}
    	}
		return resultList;
    	
    }
    
    private boolean isInExcludeList(String path) {
    	if(excludePathes == null || excludePathes.length <1 || StringUtils.isEmpty(path)) {
    		return false;
    	}
    	for(int i=0; i<excludePathes.length; i++) {
    		if(path.contains(excludePathes[i])) {
    			return true;
    		}
    	}
    	return false;
    }

}
