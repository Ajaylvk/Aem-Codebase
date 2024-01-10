package com.softwareag.core.models.link;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class LinkModelTest {

    public final AemContext ctx = new AemContext();

    @BeforeEach
    public void setUp() {
        ctx.load().json("/components/link.json", "/content");
    }

    @Test
    void getEnumByValue_shouldReturnEnumValue_whenCorrectValueIsGiven() {
        assertThat(LinkModel.Target.getEnumByValue("_blank")).isEqualTo(LinkModel.Target.BLANK);
        assertThat(LinkModel.Target.getEnumByValue("blank")).isEqualTo(LinkModel.Target.BLANK);
    }

    @Test
    void getEnumByValue_shouldReturnSelf_whenInCorrectValueIsGiven() {
        assertThat(LinkModel.Target.getEnumByValue("")).isEqualTo(LinkModel.Target.SELF);
        assertThat(LinkModel.Target.getEnumByValue(null)).isEqualTo(LinkModel.Target.SELF);
        assertThat(LinkModel.Target.getEnumByValue("asd")).isEqualTo(LinkModel.Target.SELF);
    }

    @Test
    void getValue_shouldReturnTheValueAsString() {
        assertThat(LinkModel.Target.BLANK.getValue()).isEqualTo("_blank");
    }

    @Test
    void isInternal_shouldReturnTrue_whenPathStartsWithContentOrHash() {
        assertTrue(LinkModel.isInternal(LinkModel.CONTENT));
        assertTrue(LinkModel.isInternal(LinkModel.HASH));
    }

    @Test
    void isInternal_shouldReturnFalse_whenNullOrEmptyIsGiven() {
        assertFalse(LinkModel.isInternal(null));
        assertFalse(LinkModel.isInternal(""));
    }

    @Test
    void isInternal_shouldReturnFalse_whenPathStartsWithHttp() {
        assertFalse(LinkModel.isInternal("http://www.softwareag.com"));
        assertFalse(LinkModel.isInternal("https://www.softwareag.com"));
    }

    @Test
    void hasContent_shouldReturnTrue_whenLinkAndTextAreGiven() {
        final LinkModel model = getModel("/content/internal-link");
        assertThat(model.hasContent()).isTrue();
    }

    @Test
    void hasContent_shouldReturnFalse_whenLinkAndTextAreNotGiven() {
        final LinkModel model1 = getModel("/content/without-text");
        final LinkModel model2 = getModel("/content/without-href");
        final LinkModel model3 = getModel("/content/empty");
        assertFalse(model1.hasContent());
        assertFalse(model2.hasContent());
        assertFalse(model3.hasContent());
    }

    @Test
    void getText_shouldReturnTheGivenValue() {
        final LinkModel model = getModel("/content/internal-link");
        assertThat(model.getText()).isEqualTo("Text");
    }

    @Test
    void getHref_shouldEndsWithHtml_whenLinkStartsWithContent() {
        final LinkModel model = getModel("/content/internal-link");
        assertTrue(StringUtils.endsWith(model.getHref(), LinkModel.DOT_HTML));
    }

    @Test
    void getHref_shouldHaveDotHtmlBeforeHash_whenLinkContainsRelPathAndHash() {
        final LinkModel model = getModel("/content/internal-rel-path-hash-link");
        assertThat(model.getHref()).isEqualTo("/content/softwareag.html#anchor");
    }

    @Test
    void getHref_shouldReturnHash_whenLinkContainsOnlyHash() {
        final LinkModel model = getModel("/content/internal-hash-only-link");
        assertThat(model.getHref()).isEqualTo("#anchor");
    }

    @Test
    void getHref_shouldHaveDotHtmlBeforeQMark_whenLinkContainsUtmParamAndHash() {
        final LinkModel model = getModel("/content/internal-hash-utm-link");
        assertThat(model.getHref()).isEqualTo("/content/softwareag.html?utm_source=facebook.com&utm_medium=social&utm_campaign=fs2017cat#anchor");
    }

    @Test
    void getHref_shouldHaveDotHtmlBeforeQMark_whenLinkContainsUtmParam() {
        final LinkModel model = getModel("/content/internal-utm-link");
        assertThat(model.getHref()).isEqualTo("/content/softwareag.html?utm_source=facebook.com&utm_medium=social&utm_campaign=fs2017cat");
    }

    @Test
    void getHref_shouldReturnTheGivenValue_whenLinkIsExternal() {
        final LinkModel model = getModel("/content/external-link");
        assertThat(model.getHref()).isEqualTo("https://www.softwareag.com");
    }

    @Test
    void getHref_shouldReturnTheGivenValue_whenLinkIsEmpty() {
        final LinkModel model = getModel("/content/empty");
        assertThat(model.getHref()).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    void getHref_shouldReturnTheGivenValue_whenLinkIsUnderDam() {
        final LinkModel model = getModel("/content/dam-link");
        assertThat(model.getHref()).isEqualTo("/content/dam/softwareag/test.png");
    }

    @Test
    void getTarget_shouldReturnTheGivenValue() {
        final LinkModel model = getModel("/content/internal-link");
        assertThat(model.getTarget()).isEqualTo(LinkModel.Target.BLANK.getValue());
    }

    @Test
    void getTarget_shouldReturnSelf_whenTargetIsIncorrect() {
        final LinkModel model = getModel("/content/incorrect-target");
        assertThat(model.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
    }

    @Test
    void getTarget_shouldReturnSelf_whenTargetIsMissingAndPathIsIntern() {
        final LinkModel model = getModel("/content/internal-without-target");
        assertThat(model.getTarget()).isEqualTo(LinkModel.Target.SELF.getValue());
    }

    @Test
    void getTarget_shouldReturnBlank_whenTargetIsMissingAndPathIsExtern() {
        final LinkModel model = getModel("/content/external-without-target");
        assertThat(model.getTarget()).isEqualTo(LinkModel.Target.BLANK.getValue());
    }

    @Test
    void set_shouldSetTheGivenValue() {
        final String expectedText = "Title";
        final String expectedHref = "/content/softwareag.html?utm_source=facebook.com&utm_medium=social&utm_campaign=fs2017cat";
        final String expectedTarget = "_self";
        final String expectedUtmParameter = "utm_source=facebook.com&utm_medium=social&utm_campaign=fs2017cat";

        final LinkModel underTest = new LinkModel();
        underTest.setText(expectedText);
        underTest.setRawHref("/content/softwareag");
        underTest.setTarget(expectedTarget);
        underTest.setUtmParameter(expectedUtmParameter);
        underTest.init();

        assertThat(underTest.getText()).isEqualTo(expectedText);
        assertThat(underTest.getHref()).isEqualTo(expectedHref);
        assertThat(underTest.getTarget()).isEqualTo(expectedTarget);
        assertThat(underTest.getUtmParameter()).isEqualTo(expectedUtmParameter);
    }

    private LinkModel getModel(final String resourcePath) {
        ctx.currentResource(resourcePath);
        return Objects.requireNonNull(ctx.currentResource()).adaptTo(LinkModel.class);
    }
}
