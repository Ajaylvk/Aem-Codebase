package com.softwareag.core.models.navigation_v2;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Section {

    @Inject
    @Named("nooflinks")
    private String noLinks;
    @Inject
    @Getter
    @Setter
    @Named("sectiontitle")
    private String sectionTitle;

    @Getter
    private List<Integer> elements=new ArrayList<>();

    @PostConstruct
    public void init(){
        if(noLinks!=null)
            generateList(noLinks);
    }

    private void generateList(String noLinks) {
        for (int i=0;i<Integer.parseInt(noLinks);i++)
            elements.add(i);
    }
}
