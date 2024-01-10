package com.softwareag.core.models.table;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
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

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TableV2 {

    private static final Logger LOG = LoggerFactory.getLogger(TableV2.class);

    @Getter
    private int intRowCount;

    @Inject
    @Getter
    private String tableBackGroundColor;

    @Inject
    @Named("colspecifications")
    Resource columnSpecifications;

    @Inject
    @Named("rowspecifications")
    Resource rowSpecifications;

    @Getter
    private List<String> colWidths=new ArrayList<>();

    @Getter
    private List<String> colColours=new ArrayList<>();

    @Getter
    private List<String> rowColours=new ArrayList<>();
    @Getter
    List<Integer> totalItems = new ArrayList<>();

    @PostConstruct
    void init() {
        Iterator<Resource> itr = columnSpecifications.listChildren();
        while (itr.hasNext()) {
            Resource item = itr.next();
            ValueMap value = item.adaptTo(ValueMap.class);
            if (value.get("colwidth") != null) {
                if (!value.get("colwidth").equals("0%")) {
                    colWidths.add(value.get("colwidth").toString());
                }
            }
            if(value.get("colbgcolor")!=null){
                if(!value.get("colbgcolor").equals("nocolor"))
                    colColours.add(value.get("colbgcolor").toString());
                else
                    colColours.add("NC");
            }
        }
        Iterator<Resource> row = rowSpecifications.listChildren();
        while (row.hasNext()) {
            Resource item = row.next();
            ValueMap value = item.adaptTo(ValueMap.class);
            if (value.get("rowbgcolor") != null) {
                if(!value.get("rowbgcolor").equals("nocolor"))
                    rowColours.add(value.get("rowbgcolor").toString());
                else
                    rowColours.add("NC");
            }
            }
        intRowCount=rowColours.size();
        formPlaceholderList(colWidths,intRowCount);
    }

    private void formPlaceholderList(List colCount, int intRowCount) {
        for(int i = 0; i<colCount.size()*intRowCount; i++)
           totalItems.add(i);
    }
}
