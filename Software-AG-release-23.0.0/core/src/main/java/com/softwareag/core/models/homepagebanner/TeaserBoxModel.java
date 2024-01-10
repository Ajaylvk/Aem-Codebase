package com.softwareag.core.models.homepagebanner;

import com.softwareag.core.models.CampaignModel;
import com.softwareag.core.models.cta.CtaModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeaserBoxModel {

    @Self
    private Resource resource;

    @ValueMapValue
    private String title;

    @ChildResource
    private CtaModel cta;

    @ValueMapValue
    private String fontStyle;

    @ValueMapValue
    private String backgroundStyle;

    private CampaignModel campaignModel;
    private String campaign;
    private String campaignTitle;

    @PostConstruct
    private void init() {
        campaignModel = resource.adaptTo(CampaignModel.class);
        campaign = campaignModel.getCampaign();
        campaignTitle = campaignModel.getTitle();
    }

    public String getTitle() {
        return title;
    }

    public CtaModel getCta() {
        return cta;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public String getCampaign() {
        return campaign;
    }

    public String getCampaignTitle() {
        return campaignTitle;
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(this.title) &&
            Optional.ofNullable(cta)
                .map(CtaModel::isValid)
                .orElse(false);
    }

}
