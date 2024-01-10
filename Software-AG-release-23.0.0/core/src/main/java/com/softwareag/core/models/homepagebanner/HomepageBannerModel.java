package com.softwareag.core.models.homepagebanner;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HomepageBannerModel {

    @ChildResource
    private TeaserBoxModel teaserBox;

    @ChildResource
    private SolutionBoxModel solutionBox1;

    @ChildResource
    private SolutionBoxModel solutionBox2;

    public TeaserBoxModel getTeaserBox() {
        return teaserBox;
    }

    public SolutionBoxModel getSolutionBox1() {
        return solutionBox1;
    }

    public SolutionBoxModel getSolutionBox2() {
        return solutionBox2;
    }

    public boolean isValid() {
        return Optional.ofNullable(teaserBox)
            .map(TeaserBoxModel::isValid)
            .orElse(false)
            && Optional.ofNullable(solutionBox1)
            .map(SolutionBoxModel::isValid)
            .orElse(false)
            && Optional.ofNullable(solutionBox2)
            .map(SolutionBoxModel::isValid)
            .orElse(false);
    }
}
