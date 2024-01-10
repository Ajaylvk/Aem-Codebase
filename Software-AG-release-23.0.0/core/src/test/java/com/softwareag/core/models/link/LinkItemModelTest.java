package com.softwareag.core.models.link;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
public class LinkItemModelTest {

    public final AemContext ctx = new AemContext();

    @BeforeEach
    public void setUp() {
        ctx.load().json("/components/linkItem.json", "/content");
    }

    @Test
    public void getLink_shouldReturnNull_whenLinkNodeDoesntExist() {
        final LinkItemModel model = getModel("/content/empty-item");
        assertThat(model.getLink()).isNull();
        assertThat(model.hasContent()).isFalse();
    }

    @Test
    public void getLink_shouldReturnNull_whenNameOfLinkNodeIsWrong() {
        final LinkItemModel model = getModel("/content/invalid-node-name");
        assertThat(model.getLink()).isNull();
        assertThat(model.hasContent()).isFalse();
    }

    @Test
    public void getLink_shouldReturnLink_whenLinkNodeExists() {
        final LinkItemModel model = getModel("/content/item");
        assertThat(model.getLink()).isNotNull();
        assertThat(model.hasContent()).isTrue();
    }

    @Test
    public void hasContent_shouldReturnFalse_whenLinkHasNoContent() {
        final LinkItemModel model = getModel("/content/empty-link");
        assertThat(model.getLink()).isNotNull();
        assertThat(model.hasContent()).isFalse();
    }

    private LinkItemModel getModel(final String resourcePath) {
        ctx.currentResource(resourcePath);
        return Objects.requireNonNull(ctx.currentResource()).adaptTo(LinkItemModel.class);
    }
}
