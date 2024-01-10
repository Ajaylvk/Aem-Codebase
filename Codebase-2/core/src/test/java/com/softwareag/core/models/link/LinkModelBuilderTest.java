package com.softwareag.core.models.link;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class LinkModelBuilderTest {

    private LinkModelBuilder underTest;

    @BeforeEach
    public void setUp() {
        underTest = new LinkModelBuilder();
    }

    @Test
    public void createLinkModel_shouldReturnEmptyModel_whenNoFieldIsSet() {
        final LinkModel model = underTest.createLinkModel();
        assertThat(model.hasContent()).isFalse();
    }

    @Test
    public void createLinkModel_shouldReturnFilledModel_whenFieldsAreSet() {
        final String expectedText = "Test Text";
        final String expectedHref = "http://www.softwareag.com";
        final String expectedTarget = "_blank";

        final LinkModel model = underTest
            .with(builder -> {
                builder.setText(expectedText);
                builder.setHref(expectedHref);
                builder.setTarget(expectedTarget);
            })
            .createLinkModel();

        assertThat(model.hasContent()).isTrue();
        assertThat(model.getText()).isEqualTo(expectedText);
        assertThat(model.getHref()).isEqualTo(expectedHref);
        assertThat(model.getTarget()).isEqualTo(expectedTarget);
    }

    @Test
    public void createLinkModel_targetValueIsSelf_whenNoValueIsGiven() {
        final LinkModel model = underTest.createLinkModel();
        assertThat(model.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
    }

    @Test
    public void createLinkModel_targetValueIsSelf_whenNoOrInvalidValueIsGiven() {
        final LinkModel model1 = underTest.createLinkModel();
        final LinkModel model2 = underTest
            .with(builder -> builder.setTarget(null))
            .createLinkModel();
        final LinkModel model3 = underTest
            .with(builder -> builder.setTarget("abc"))
            .createLinkModel();

        assertThat(model1.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
        assertThat(model2.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
        assertThat(model3.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
    }

    @Test
    public void createLinkModel_hrefEndsWithHtml_whenContentLink() {
        final LinkModel model = underTest
            .with(builder -> builder.setHref("/content/softwareag"))
            .createLinkModel();
        assertThat(StringUtils.endsWith(model.getHref(), ".html")).isTrue();
    }
}
