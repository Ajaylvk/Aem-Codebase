package com.softwareag.core.models.header;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.util.ContentUtil;


import java.util.ArrayList;
import java.util.List;


public class FirstLevelNavItem extends NavItem {

    private List<SecondLevelNavItem> secondLevelNavItems = new ArrayList<>();

    public FirstLevelNavItem(final Page page, final LabelConfigModel labelConfigModel) {
        super(page, labelConfigModel);

        ContentUtil.getChildPages(page, false)
            .forEach(childPage -> this.secondLevelNavItems.add(new SecondLevelNavItem(childPage, labelConfigModel)));
    }

    public List<SecondLevelNavItem> getSecondLevelNavItems() {
        return new ArrayList<>(secondLevelNavItems);
    }
}
