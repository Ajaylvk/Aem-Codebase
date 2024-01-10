package com.softwareag.core.models.tableitem;

import com.softwareag.core.models.cta.CtaModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TableItem {

    public static final String RESOURCE_TYPE = "softwareag/components/content/tableitem";
    public static final String CTA_NN = "cta";

    @Self
    @ScriptVariable
    protected Resource resource;

    @ScriptVariable
    private SlingHttpServletRequest request;

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String backgroundStyle;

    private final List<CtaModel> ctaItems = new ArrayList<>();

    @PostConstruct
    void init() {
        if (request != null && resource == null) {
            resource = request.getResource();
        }

        final Resource ctaResource = resource.getChild(CTA_NN);
        if (ctaResource != null && ctaResource.hasChildren()) {
            ctaResource.getChildren().forEach(ctaItemResource -> {
                    final CtaModel ctaModel = ctaItemResource.adaptTo(CtaModel.class);
                    ctaItems.add(ctaModel);
                }
            );
        }
    }

    public String getText() {
        return text;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public List<CtaModel> getCtaItems() {
        return Collections.unmodifiableList(ctaItems);
    }

    /**
     * Function that checks if there is any information to be shown in the component.
     *
     * @return {@link Boolean} {@code true} if there is any information to be shown, otherwise returns {@code false}.
     */
    public Boolean hasContent() {
        return StringUtils.isNotBlank(text) || CollectionUtils.isNotEmpty(ctaItems) || StringUtils.equalsIgnoreCase("input__colorselect-background-color7", backgroundStyle);
    }

}
