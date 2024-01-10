package com.softwareag.empower.core.models;

import com.softwareag.empower.core.utils.AEMUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchModel {

    @RequestAttribute
    @Default(values = "")
    private String resultsPagePath;

    @ValueMapValue
    @Default(values = "")
    private String detailsPagePath;

    @ScriptVariable
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;


    public String getResultsPagePath() {
        return AEMUtil.getURL(resultsPagePath, resourceResolver, request);
    }

    public String getDetailsPagePath() {
        return AEMUtil.getURL(detailsPagePath, resourceResolver, request);
    }
}
