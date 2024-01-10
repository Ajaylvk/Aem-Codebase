package com.softwareag.core.models.label;

import com.softwareag.core.configuration.AbstractConfigurationComponent;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LabelConfigModel extends AbstractConfigurationComponent {

    public static final String LABEL_CONFIG_RESOURCE_TYPE = "softwareag/components/configs/label";
    private static final String DEFAULT_VALUE_LABEL_LOAD_MORE = "Load more";
    private static final String DEFAULT_VALUE_LABEL_LEARN_MORE = "Learn more";
    private static final String DEFAULT_VALUE_LABEL_GO_TO_OVERVIEW = "Go to overview";
    private static final String DEFAULT_VALUE_LABEL_BACK = "Back";
    private static final String DEFAULT_VALUE_LABEL_DAYS = "Days";
    private static final String DEFAULT_VALUE_LABEL_HOURS = "Hours";
    private static final String DEFAULT_VALUE_LABEL_MINUTES = "Minutes";
    private static final String DEFAULT_VALUE_LABEL_SECONDS = "Seconds";
    private static final String DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_ONE = "Choose type";
    private static final String DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_TWO = "Choose country";
    private static final String DEFAULT_VALUE_OVERVIEW = "Overview";

    /**
     * Partner Related Labels
     */
    private static final String DEFAULT_COUNTRIES = "Countries";
    private static final String DEFAULT_PARTNER_TYPE = "Partner Type";
    private static final String DEFAULT_PRODUCT_CATEGORY = "Product Category";
    private static final String DEFAULT_INDUSTRIES = "Industries";
    private static final String DEFAULT_CROSS_INDUSTRY = "Cross Industry";
    private static final String DEFAULT_INDUSTRY = "Industry";

    /**
     * Asset Related Labels
     */
    private static final String DEFAULT_WATCH_VIDEO = "Watch Video";
    private static final String DEFAULT_READ_ARTICLE = "Read Article";

    @Self
    private Resource currentResource;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_LOAD_MORE)
    private String labelLoadMore;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_LEARN_MORE)
    private String labelLearnMore;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_GO_TO_OVERVIEW)
    private String labelGoToOverview;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_BACK)
    private String labelBack;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_DAYS)
    private String labelDays;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_HOURS)
    private String labelHours;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_MINUTES)
    private String labelMinutes;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_SECONDS)
    private String labelSeconds;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_ONE)
    private String labelEventFilterOptionOne;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_TWO)
    private String labelEventFilterOptionTwo;

    @ValueMapValue
    @Default(values = DEFAULT_VALUE_OVERVIEW)
    private String labelOverview;

    @ValueMapValue
    @Default(values = DEFAULT_COUNTRIES)
    private String labelCountries;

    @ValueMapValue
    @Default(values = DEFAULT_PARTNER_TYPE)
    private String labelPartnerType;

    @ValueMapValue
    @Default(values = DEFAULT_PRODUCT_CATEGORY)
    private String labelProductCategory;

    @ValueMapValue
    @Default(values = DEFAULT_INDUSTRIES)
    private String labelIndustries;

    @ValueMapValue
    @Default(values = DEFAULT_CROSS_INDUSTRY)
    private String labelCrossIndustry;

    @ValueMapValue
    @Default(values = DEFAULT_INDUSTRY)
    private String labelIndustry;

    @ValueMapValue
    @Default(values = DEFAULT_WATCH_VIDEO)
    private String labelWatchVideo;

    @ValueMapValue
    @Default(values = DEFAULT_READ_ARTICLE)
    private String labelReadArticle;

    @Override
    public Resource getResource() {
        return this.currentResource;
    }

    @Override
    public String getConfigResourceType() {
        return LABEL_CONFIG_RESOURCE_TYPE;
    }

    public String getLabelLoadMore() {
        return labelLoadMore;
    }

    public String getLabelLearnMore() {
        return labelLearnMore;
    }

    public String getLabelGoToOverview() {
        return labelGoToOverview;
    }

    public String getLabelBack() {
        return labelBack;
    }

    public String getLabelDays() {
        return labelDays;
    }

    public String getLabelHours() {
        return labelHours;
    }

    public String getLabelMinutes() {
        return labelMinutes;
    }

    public String getLabelSeconds() {
        return labelSeconds;
    }

    public String getLabelEventFilterOptionOne() {
        return labelEventFilterOptionOne;
    }

    public String getLabelEventFilterOptionTwo() {
        return labelEventFilterOptionTwo;
    }

    public String getLabelOverview() { return labelOverview; }

    public String getLabelCountries() { return labelCountries; }

    public String getLabelPartnerType() { return labelPartnerType; }

    public String getLabelProductCategory() { return labelProductCategory; }

    public String getLabelIndustries() {return labelIndustries; }

    public String getLabelCrossIndustry() {return labelCrossIndustry; }

    public String getLabelIndustry() {return labelIndustry; }

    public String getLabelWatchVideo() {return labelWatchVideo; }

    public String getLabelReadArticle() {return labelReadArticle; }
}
