package com.softwareag.core.models.navigation_v2;

import com.softwareag.core.util.LinkUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavItem {

    @Inject
    @Getter
    @Setter
    private String link;

    @Getter
    private String openIn;

    @PostConstruct
    public void init(){
        if(link!=null) {
            link = LinkUtil.getLink(link);
            openIn = LinkUtil.linkOpenIn(link);
        }
    }
}
