package com.softwareag.core.models.logo;

import com.softwareag.core.util.LinkUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LogoWallItem {

    @ValueMapValue
    @Getter
    @Setter
    private String linkdestination;

    @Getter
    private String openIn;

    @PostConstruct
    private void init() {
        if(linkdestination!=null&&linkdestination!="") {
            linkdestination = LinkUtil.getLink(linkdestination);
            openIn = LinkUtil.linkOpenIn(linkdestination);
        }
    }

}
