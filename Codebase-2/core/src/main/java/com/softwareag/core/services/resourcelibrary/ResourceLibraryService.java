package com.softwareag.core.services.resourcelibrary;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;

import com.google.gson.JsonObject;
import com.softwareag.core.resourcelibrary.utils.RequestObject;

public interface ResourceLibraryService {



	JsonObject getResponse(RequestObject requestObj, SlingHttpServletRequest request);

}
