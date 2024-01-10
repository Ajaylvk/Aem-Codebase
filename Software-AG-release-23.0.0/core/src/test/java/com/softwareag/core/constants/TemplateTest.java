package com.softwareag.core.constants;

import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
class TemplateTest {

    private static final String DEST_PATH = "/content/softwareag/test";

    private final AemContext ctx = new AemContext();

    private Page testPage;

    @BeforeEach
    private void setUp() {
        ctx.load().json("/constants/template.json", DEST_PATH);
        ctx.currentResource(DEST_PATH);

        this.testPage = Objects.requireNonNull(ctx.currentResource()).adaptTo(Page.class);
        assertNotNull(this.testPage);
    }

    @Test
    public void testGetTemplatePathFromPage() {
        assertThat(Template.getTemplatePathFromPage(this.testPage)).isEqualTo(Template.HOME_PAGE.getPath());
    }

    @Test
    public void testIsTemplateOfPage() {
        assertThat(Template.HOME_PAGE.isTemplateOfPage(this.testPage)).isTrue();
        assertThat(Template.HOME_PAGE.isTemplateOfPage(null)).isFalse();
        assertThat(Template.CONFIG_PAGE.isTemplateOfPage(this.testPage)).isFalse();
    }
}
