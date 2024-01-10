package com.softwareag.core.models.logo;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LogoWallModel {

    @ValueMapValue
    private String columns;

    @ValueMapValue
    private String rows;

    @Getter
    public List<Integer> placeHolderList;

    @PostConstruct
    private void init() {
        if(columns!=null&&rows!=null) {
            int total = Integer.parseInt(columns) * Integer.parseInt(rows);
            setList(total);
        }
    }

    private void setList(int total) {
        placeHolderList=new ArrayList<>();
        for (int i=0;i<total;i++)
            placeHolderList.add(i);
    }

}
