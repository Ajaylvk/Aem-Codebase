package com.softwareag.core.models.tab;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabModelV1 {

    private static final String PN_PANELS = "tabPanels";

    @ScriptVariable
    protected Resource resource;

    @ValueMapValue
    private String backgroundColor;

    @ValueMapValue
    private String title;

    @ValueMapValue
	private String alignment;
    
 

    private final List<Resource> panelResources = new ArrayList<>();
  

    @PostConstruct
    protected void init() {
        final Resource panelsResource = resource.getChild(PN_PANELS);
        if (panelsResource != null && panelsResource.hasChildren()) {
            panelsResource.getChildren().forEach(panelResources::add);
        }
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || CollectionUtils.isNotEmpty(panelResources);
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTitle() {
         return title;
    }

    public String getAlignment(){ 
        return alignment;
    }

    public List<Resource> getPanelResources() {
        return Collections.unmodifiableList(panelResources);
    }
    
}
