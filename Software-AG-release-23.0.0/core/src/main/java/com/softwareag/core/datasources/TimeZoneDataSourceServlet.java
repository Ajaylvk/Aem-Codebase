package com.softwareag.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.crx.JcrConstants;
import com.softwareag.core.services.timezone.TimeZoneService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet to provide a Granite DataSource containing a list of timezones configured via OSGi.
 */

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "= DataSource Servlet for Timezone Dropdowns",
        "sling.servlet.resourceTypes=" + TimeZoneDataSourceServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    })
public class TimeZoneDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE = "softwareag/datasources/timezones";
    private static final Logger LOG = LoggerFactory.getLogger(TimeZoneDataSourceServlet.class);

    @SuppressWarnings("squid:S1948")
    @Reference
    private TimeZoneService timeZoneService;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) {

        // set fallback
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());

        if (timeZoneService == null) {
            LOG.error("timezone service is null");
            return;
        }

        Map<String, String> timezones = timeZoneService.getTimeZones();

        ResourceResolver resourceResolver = request.getResourceResolver();

        // create ArrayList to hold data
        List<Resource> resourceList = new ArrayList<>();

        for(Map.Entry<String,String> timezoneEntry: timezones.entrySet()) {
            ValueMap vm = new ValueMapDecorator(new HashMap<>());
            vm.put("value", timezoneEntry.getKey()); // is the GMT zone
            vm.put("text", timezoneEntry.getValue()); // is a corresponding description
            resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));
        }

        DataSource ds = new SimpleDataSource(resourceList.iterator());
        request.setAttribute(DataSource.class.getName(), ds);
    }
}
