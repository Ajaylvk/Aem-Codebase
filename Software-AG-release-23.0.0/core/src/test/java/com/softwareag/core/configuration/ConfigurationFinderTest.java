package com.softwareag.core.configuration;

import com.day.cq.wcm.api.Page;
import com.softwareag.core.models.label.LabelConfigModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ConfigurationFinderTest {

    private static final String DEST_PATH = "/content/softwareag/en_us";

    private final AemContext ctx = new AemContext();

    @BeforeEach
    private void setUp() {
        ctx.load().json("/util/configurationFinder.json", DEST_PATH);
    }

    @Test
    public void findLabelConfigComponent_returnsEmptyOptional_whenGivenPageIsNull() {
        final Optional<LabelConfigModel> labelConfigModel = ConfigurationFinder.findLabelConfigComponent(null);
        assertThat(labelConfigModel).isNotPresent();
    }

    @Test
    public void findLabelConfigComponent_returnsEmptyOptional_whenTheComponentIsNotConfigured() {
        final Page page = Objects.requireNonNull(ctx.currentResource(DEST_PATH + "/with-empty-config")).adaptTo(Page.class);
        final Optional<LabelConfigModel> labelConfigModel = ConfigurationFinder.findLabelConfigComponent(page);
        assertThat(labelConfigModel).isNotPresent();
    }

    @Test
    public void findLabelConfigComponent_returnsLabelConfigModel_whenTheComponentIsConfigured() {
        final Page page = Objects.requireNonNull(ctx.currentResource(DEST_PATH + "/with-config")).adaptTo(Page.class);
        final Optional<LabelConfigModel> labelConfigModel = ConfigurationFinder.findLabelConfigComponent(page);
        assertThat(labelConfigModel).isPresent();
        assertThat(labelConfigModel.get().getLabelLoadMore()).isEqualTo("Load more");
    }
}
