package com.softwareag.core.models;


import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** the IntegrationModel provides shared functionality to support the addition of Integration-related
 * markup requirements
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class IntegrationModel {

    @ValueMapValue
    protected String id;

    @ValueMapValue
    protected String mbox;

    @ValueMapValue
    protected boolean targetThisComponent;

    @Self
    private Resource resource;

    public String getId() {
        if (StringUtils.isBlank(id)) {
            id = getIdPrefix() + "-" + Math.abs(resource.getPath().hashCode() - 1);
        }
        return id;
    }

    public String getMbox() {
        return mbox;
    }

    public boolean getTargetThisComponent() {
        return targetThisComponent
            && isMboxConfigured();
    }

    private String getIdPrefix() {
        return resource.getName();
    }

    private boolean isMboxConfigured() {
        return StringUtils.isNotBlank(mbox);
    }
}
