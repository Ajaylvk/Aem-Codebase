package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.util.ContentUtil;


import java.util.ArrayList;
import java.util.List;


public class SecondLevelNavItem extends NavItem {

    private List<NavItem> thirdLevelNavItems = new ArrayList<>();

    public SecondLevelNavItem(final Page page, final LabelConfigModel labelConfigModel) {
        super(page, labelConfigModel);

        ContentUtil.getChildPages(page, false)
            .forEach(childPage -> this.thirdLevelNavItems.add(new NavItem(childPage, labelConfigModel)));
    }

    public List<NavItem> getThirdLevelNavItems() {
        return new ArrayList<>(thirdLevelNavItems);
    }
}
