package com.softwareag.core.models.accordion;

import com.softwareag.core.util.AttributeUtil;

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


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionContainer {

    private static final String PN_PANELS = "panels";

    @ScriptVariable
    protected Resource resource;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String design;

    @ValueMapValue
    private String anchorName;

    private final List<Resource> panelResources = new ArrayList<>();

    @PostConstruct
    protected void init() {
        final Resource panelsResource = resource.getChild(PN_PANELS);
        if (panelsResource != null && panelsResource.hasChildren()) {
            panelsResource.getChildren().forEach(item -> panelResources.add(item));
        }
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || StringUtils.isNotBlank(anchorName)
            || CollectionUtils.isNotEmpty(panelResources);
    }

    public String getTitle() {
        return title;
    }

    public String getDataAttribValue() {
        return AttributeUtil.removeEmptyLines(getTitle());
    }

    public String getDesign() {
        return design;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public List<Resource> getPanelResources() {
        return Collections.unmodifiableList(panelResources);
    }

}

