package com.softwareag.core.models.popup;

import com.softwareag.core.models.label.LabelConfigModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class PopUpConfigModelTest {

    private static final String DEST_PATH = "/content/softwareag/test/config";

    private final AemContext context = new AemContext();

    private PopUpConfigModel modelUnderTest;
    private static final String DEFAULT_VALUE_LABEL_DAYS = "Days";
    private static final String DEFAULT_VALUE_LABEL_HOURS = "Hours";
    private static final String DEFAULT_VALUE_LABEL_MINUTES = "Minutes";
    private static final String DEFAULT_VALUE_LABEL_SECONDS = "Seconds";
    private static final String DEFAULT_VALUE_LABEL_LOAD_MORE = "Load more";
    private static final String DEFAULT_VALUE_LABEL_LEARN_MORE = "Learn more";
    private static final String DEFAULT_VALUE_LABEL_GO_TO_OVERVIEW = "Go to overview";
    private static final String DEFAULT_VALUE_LABEL_BACK = "Back";
    private static final String DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_ONE = "Choose type";
    private static final String DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_TWO = "Choose country";
    private static final String DEFAULT_VALUE_LABEL_COUNTRIES = "Countries";
    private static final String DEFAULT_VALUE_LABEL_PARTNER_TYPE = "Partner Type";
    private static final String DEFAULT_VALUE_LABEL_PRODUCT_CATEGORY = "Product Category";
    private static final String DEFAULT_VALUE_LABEL_INDUSTRIES = "Industries";

    @BeforeEach
    public void setUp() {
        context.load().json("/components/popUpConfig.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/parsys/popup");

        modelUnderTest = Objects.requireNonNull(context.currentResource()).adaptTo(PopUpConfigModel.class);
        assertThat(modelUnderTest).isNotNull();
    }

    @Test
    public void getter_shouldReturnGivenValues() {
        assertThat(modelUnderTest.getConfigResourceType()).isEqualTo(PopUpConfigModel.POPUP_CONFIG_RESOURCE_TYPE);
        assertThat(modelUnderTest.getEnableRegionalPopUp()).isEqualTo(true);
        assertThat(modelUnderTest.getRegionalPopUpMessage()).isEqualTo("Want to change the region?");
        assertThat(modelUnderTest.getRegionalLabelClose()).isEqualTo("Close");
        assertThat(modelUnderTest.getRegionalLabelRedirect()).isEqualTo("Proceed");
        assertThat(modelUnderTest.getEnableRedirectPopUp()).isEqualTo(true);
        assertThat(modelUnderTest.getExternalPopUpMessage()).isEqualTo("Want to continue to external link?");
        assertThat(modelUnderTest.getExternalLabelClose()).isEqualTo("Close");
        assertThat(modelUnderTest.getExternalLabelRedirect()).isEqualTo("Yes");
    }

    /* @Test
    public void isNotValid_returnsTrue_ifSeveralComponentsExists() {
        assertTrue(modelUnderTest.isNotValid());
    }

   @Test
    public void getter_shouldReturnDefaultValues() {
        context.currentResource(DEST_PATH + "/jcr:content/parsys/label_empty");

        LabelConfigModel emptyModel = Objects.requireNonNull(context.currentResource()).adaptTo(LabelConfigModel.class);
        assertThat(emptyModel).isNotNull();

        assertThat(emptyModel.getLabelDays()).isEqualTo(DEFAULT_VALUE_LABEL_DAYS);
        assertThat(emptyModel.getLabelHours()).isEqualTo(DEFAULT_VALUE_LABEL_HOURS);
        assertThat(emptyModel.getLabelMinutes()).isEqualTo(DEFAULT_VALUE_LABEL_MINUTES);
        assertThat(emptyModel.getLabelSeconds()).isEqualTo(DEFAULT_VALUE_LABEL_SECONDS);
        assertThat(emptyModel.getLabelLoadMore()).isEqualTo(DEFAULT_VALUE_LABEL_LOAD_MORE);
        assertThat(emptyModel.getLabelLearnMore()).isEqualTo(DEFAULT_VALUE_LABEL_LEARN_MORE);
        assertThat(emptyModel.getLabelGoToOverview()).isEqualTo(DEFAULT_VALUE_LABEL_GO_TO_OVERVIEW);
        assertThat(emptyModel.getLabelBack()).isEqualTo(DEFAULT_VALUE_LABEL_BACK);
        assertThat(emptyModel.getLabelEventFilterOptionOne()).isEqualTo(DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_ONE);
        assertThat(emptyModel.getLabelEventFilterOptionTwo()).isEqualTo(DEFAULT_VALUE_LABEL_EVENT_FILTER_OPTION_TWO);
        assertThat(emptyModel.getLabelCountries()).isEqualTo(DEFAULT_VALUE_LABEL_COUNTRIES);
        assertThat(emptyModel.getLabelPartnerType()).isEqualTo(DEFAULT_VALUE_LABEL_PARTNER_TYPE);
        assertThat(emptyModel.getLabelProductCategory()).isEqualTo(DEFAULT_VALUE_LABEL_PRODUCT_CATEGORY);
        assertThat(emptyModel.getLabelIndustries()).isEqualTo(DEFAULT_VALUE_LABEL_INDUSTRIES);
    }*/
}
