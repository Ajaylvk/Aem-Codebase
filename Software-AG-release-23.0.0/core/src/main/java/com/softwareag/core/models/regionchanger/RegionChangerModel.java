package com.softwareag.core.models.regionchanger;

import com.softwareag.core.models.link.LinkModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RegionChangerModel {

    protected static final String GENERAL_TAB = "general";
    protected static final String GENERAL_TAB_TITLE = "title";
    protected static final String GENERAL_TAB_INTRO_TEXT = "introductoryText";
    protected static final String INTERNATIONAL_TAB = "international";
    protected static final String INTERNATIONAL_TAB_LINK = "link";
    protected static final String AMERICAS_TAB = "americas";
    protected static final String ASIA_PACIFIC_TAB = "asiaPacific";
    protected static final String EUROPE_TAB = "europe";
    protected static final String MID_EAST_AND_AFRICA_TAB = "middleEastAndAfrica";
    protected static final String[] REGION_RESOURCE_NAMES = {AMERICAS_TAB, ASIA_PACIFIC_TAB, EUROPE_TAB, MID_EAST_AND_AFRICA_TAB};

    @Self
    private Resource currentResource;

    // properties of General tab
    private String title;
    private String introductoryText;

    // property of International tab
    private LinkModel link;

    private final List<RegionChangerTab> tabItems = new ArrayList<>();

    @PostConstruct
    private void init() {
        initGeneral();
        initInternational();
        initTabItems();
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || StringUtils.isNotBlank(introductoryText);
    }

    public String getTitle() {
        return title;
    }

    public String getIntroductoryText() {
        return introductoryText;
    }

    public LinkModel getLink() {
        return link;
    }

    public List<RegionChangerTab> getTabItems() {
        return Collections.unmodifiableList(tabItems);
    }

    private void initGeneral() {
        final Resource generalResource = currentResource.getChild(GENERAL_TAB);
        if (generalResource != null) {
            final ValueMap valueMap = generalResource.getValueMap();
            title = valueMap.get(GENERAL_TAB_TITLE, String.class);
            introductoryText = valueMap.get(GENERAL_TAB_INTRO_TEXT, String.class);
        }
    }

    private void initInternational() {
        final Resource internationalResource = currentResource.getChild(INTERNATIONAL_TAB);
        if (internationalResource != null) {
            final Resource linkResource = internationalResource.getChild(INTERNATIONAL_TAB_LINK);
            if (linkResource != null) {
                link = linkResource.adaptTo(LinkModel.class);
            }
        }
    }

    private void initTabItems() {
        Stream.of(REGION_RESOURCE_NAMES).forEach(tabName ->
            {
                final Resource tabResource = currentResource.getChild(tabName);
                if (tabResource != null) {
                    final RegionChangerTab regionChangerTab = tabResource.adaptTo(RegionChangerTab.class);
                    tabItems.add(regionChangerTab);
                }
            }
        );
    }
}
