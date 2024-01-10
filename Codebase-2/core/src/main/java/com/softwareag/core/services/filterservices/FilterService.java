package com.softwareag.core.services.filterservices;

import com.google.gson.JsonObject;
import com.softwareag.core.resourcelibrary.utils.RequestObject;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.List;

public interface FilterService {
    JsonObject getResponse(RequestObject requestObj, SlingHttpServletRequest request, String selector, List<String> dropdownorder);
}
