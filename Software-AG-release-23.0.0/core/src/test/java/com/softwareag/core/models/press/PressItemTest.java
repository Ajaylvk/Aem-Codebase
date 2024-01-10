package com.softwareag.core.models.press;

import com.softwareag.core.models.DateTimeModel;
import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PressItemTest {

    private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    @Mock
    private LanguageService languageService;

    private PressItem pressItem;

    @BeforeEach
    public void setUp() throws ParseException {
        context.load().json("/components/press/pressitem.json", "/content/softwareag");
        context.currentResource("/content/softwareag/press-folder-01/press-page-01/jcr:content/pressitem");
        context.registerService(languageService);
        context.addModelsForPackage("com.softwareag.core.models");

        pressItem = Objects.requireNonNull(context.currentResource()).adaptTo(PressItem.class);

        assertThat(pressItem).isNotNull();
    }

    @Test
    void testIsHidePressInPressOverview() {
        assertThat(pressItem.isHidePressInPressOverview()).isFalse();
    }

    @Test
    void testGetPressTitle() {
        assertThat(pressItem.getPressTitle()).isEqualTo("Press Item 01");
    }

    @Test
    void testGetPressDescription() {
        assertThat(pressItem.getPressDescription()).isEqualTo("Press Item 01");
    }

    @Test
    void testGetPressSummary() {
        assertThat(pressItem.getPressSummary()).isEqualTo("Press Summary");
    }

    @Test
    void testGetPressReleaseDate() throws ParseException {
        context.request().setAttribute("input", pressItem.getPressReleaseDate());
        context.request().setAttribute("format", "MMM dd, yyyy");

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals("Jan 08, 2020", dateTimeModel.getOutput());
    }

    @Test
    void testGetPath() {
        assertThat(pressItem.getPath()).isEqualTo("/content/softwareag/press-folder-01/press-page-01");
    }

    @Test
    void testGetOverviewPath() {
        assertThat(pressItem.getOverviewPath()).isEqualTo("/content/softwareag");
    }

    @Test
    void testGetPressHighlights() {
        assertThat(pressItem.getPressHighlights()).isEqualTo("Press Highlights");
    }

    @Test
    void testLinkCreationRedirectExternal() {
        context.currentResource("/content/softwareag/press-folder-01/press-page-02");
        pressItem = Objects.requireNonNull(context.currentResource()).adaptTo(PressItem.class);
        assertEquals("https://www.google.com", Objects.requireNonNull(pressItem).getLink().getHref());
        assertEquals("_blank", pressItem.getLink().getTarget());
    }

    @Test
    void testLinkCreationRedirectInternal() {
        context.currentResource("/content/softwareag/press-folder-01/press-page-03");
        pressItem = Objects.requireNonNull(context.currentResource()).adaptTo(PressItem.class);
        assertEquals("/content/softwareag.html", Objects.requireNonNull(pressItem).getLink().getHref());
        assertEquals("_blank", pressItem.getLink().getTarget());
    }

    @Test
    void testLinkCreationInternal() {
        context.currentResource("/content/softwareag/press-folder-01/press-page-01");
        pressItem = Objects.requireNonNull(context.currentResource()).adaptTo(PressItem.class);
        assertEquals("/content/softwareag/press-folder-01/press-page-01.html", Objects.requireNonNull(pressItem).getLink().getHref());
        assertEquals("_self", pressItem.getLink().getTarget());
    }

    @Test
    void testExternalPressLink() {
        context.currentResource("/content/softwareag/press-folder-01/press-page-04/jcr:content/pressitem");
        pressItem = Objects.requireNonNull(context.currentResource()).adaptTo(PressItem.class);
        assertEquals("https://www.google.com/", Objects.requireNonNull(pressItem).getExternalPressLink());
        assertEquals("https://www.google.com/", pressItem.getLink().getHref());
    }

}
