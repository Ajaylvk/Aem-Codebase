package com.softwareag.core.models.languagechanger;

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



@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LanguageChangerModel {

    protected static final String GENERAL_TAB = "general";
    protected static final String GENERAL_TAB_TITLE = "title";
    protected static final String GENERAL_TAB_INTRO_TEXT = "introductoryText";
    protected static final String LANGUAGE_TAB = "language";

    @Self
    private Resource currentResource;

    // properties of General tab
    private String title;
    private String introductoryText;

    //properties of Language tab
    private final List<LinkModel> linkItems = new ArrayList<>();

    @PostConstruct
    private void init() {
        initGeneral();
        initLanguage();
    }

    public String getTitle() {
        return title;
    }

    public String getIntroductoryText() {
        return introductoryText;
    }

    private void initGeneral() {
        final Resource generalResource = currentResource.getChild(GENERAL_TAB);
        if (generalResource != null) {
            final ValueMap valueMap = generalResource.getValueMap();
            title = valueMap.get(GENERAL_TAB_TITLE, String.class);
            introductoryText = valueMap.get(GENERAL_TAB_INTRO_TEXT, String.class);
        }
    }

    private void initLanguage(){
        final Resource languageResource = currentResource.getChild(LANGUAGE_TAB);
        if (languageResource != null && languageResource.hasChildren()) {
            languageResource.getChildren().forEach(languageItem -> {
                final LinkModel linkModel = languageItem.adaptTo(LinkModel.class);
                if(linkModel != null)
                {
                    linkItems.add(linkModel);
                }
            });
        }
    }

    public List<LinkModel> getLinkItems() {
        return Collections.unmodifiableList(linkItems);
    }

    public boolean hasContent() {
        return StringUtils.isNotBlank(title)
            || StringUtils.isNotBlank(introductoryText);
    }
}
