package com.softwareag.core.models.navigation_v2;

import com.softwareag.core.util.LinkUtil;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Model(adaptables = Resource.class,resourceType = { NavigationV2Model.RESOURCE_TYPE },defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationV2Model {

    public static final String RESOURCE_TYPE = "softwareag/components/content/navigation_v2";
    private static final Logger LOG = LoggerFactory.getLogger(NavigationV2Model.class);
    @Inject
    @Named("navmenu")
    Resource resource;

    @Getter
    List<NavV2Item> navlist=new ArrayList<>();

    @PostConstruct
    public void init(){
        Iterator<Resource> itr = resource.listChildren();
        while (itr.hasNext()){
            Resource item = itr.next();
            ValueMap value=item.adaptTo(ValueMap.class);
            String title="",link="",templateType="",linkType="",linkOpenIn="";
            if(value.get("menuitemtitle")!=null){
                 title=value.get("menuitemtitle").toString();
            }
            if(value.get("menuitemlinkdestination")!=null){
                 link= LinkUtil.getLink(value.get("menuitemlinkdestination").toString());
                 linkOpenIn=LinkUtil.linkOpenIn(link);
            }
            if(value.get("templateType")!=null){
               templateType=value.get("templateType").toString();
            }
            if(value.get("linkType")!=null){
                 linkType=value.get("linkType").toString();
            }
            navlist.add(new NavV2Item(title,link,templateType,linkType,linkOpenIn));
        }
        LOG.info("Total list"+navlist.toString());
    }
}
