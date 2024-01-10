package com.softwareag.core.models.event;

import com.softwareag.core.models.DateTimeModel;
import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
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
class EventItemModelTest {

    @Mock
    private LanguageService languageService;

    private final AemContext context = new AemContext();

    private static final String TAGS_SOFTWARE = "/content/cq:tags/softwareag";
    private static final String DEST_PATH = "/content/softwareag";
    private static final String INPUT_FIELD = "input";
    private static final String FORMAT_FIELD = "format";
    private static final String VALID_DATETIME_FORMAT = "MMM dd, yyyy h:mm a";

    private EventItemModel eventItem;

    @BeforeEach
    public void setUp() throws ParseException {

        context.load().json("/components/event/eventitem.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_SOFTWARE);
        context.currentResource(DEST_PATH + "/event-folder-01/event-01/jcr:content/eventitem");
        context.registerService(languageService);
        context.addModelsForPackage("com.softwareag.core.models");

        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);

        assertThat(eventItem).isNotNull();
    }

    @Test
    void testHasContent() {
        assertThat(eventItem.hasContent()).isTrue();
    }

    @Test
    void testGetPath() {
        assertThat(eventItem.getPath()).isEqualTo(DEST_PATH + "/event-folder-01/event-01");
    }

    @Test
    void testIsHideEventInEventOverview() {
        assertThat(eventItem.isHideEventInEventOverview()).isTrue();
    }

    @Test
    void testIsEnableCounter() {
        assertThat(eventItem.isEnableCounter()).isTrue();
    }

    @Test
    void testEventTitle() {
        assertThat(eventItem.getEventTitle()).isEqualTo("Event Title 01");
    }

    @Test
    void testEventDescription() {
        assertThat(eventItem.getEventDescription()).isEqualTo("<b><i>Event Title 01</i></b>");
    }

    @Test
    void testEventCountryTagId() {
        assertThat(eventItem.getEventCountryTagId()).isEqualTo(EventOverviewModel.COUNTRY_TAG_PATH + "england");
    }

    @Test
    void testEventTypeTagId() {
        assertThat(eventItem.getEventTypeTagId()).isEqualTo(EventOverviewModel.EVENT_TYPE_TAG_PATH + "conference");
    }

    @Test
    void testEventCountry() {
        assertThat(eventItem.getEventCountry()).isEqualTo("England");
    }

    @Test
    void testEventType() {
        assertThat(eventItem.getEventType()).isEqualTo("Conference");
    }

    @Test
    void testEventStartDate() {
        context.request().setAttribute(INPUT_FIELD, eventItem.getEventStartDate());
        context.request().setAttribute(FORMAT_FIELD, VALID_DATETIME_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals("Jan 20, 2020 12:00 AM", dateTimeModel.getOutput());
    }

    @Test
    void testEventEndDate() {
        context.request().setAttribute(INPUT_FIELD, eventItem.getEventEndDate());
        context.request().setAttribute(FORMAT_FIELD, VALID_DATETIME_FORMAT);
        context.request().setAttribute("timezone", "GMT");

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals("Jan 21, 2020 12:00 AM", dateTimeModel.getOutput());
    }

    @Test
    void testGetOverviewPath() {
        assertThat(eventItem.getOverviewPath()).isEqualTo(DEST_PATH);
    }

    @Test
    void testGetEventTimeZone() {
        assertThat(eventItem.getEventTimeZone()).isEqualTo("GMT");
    }

    @Test
    void testLinkCreationRedirectExternal() {
        context.currentResource(DEST_PATH + "/event-folder-01/event-02/jcr:content/eventitem");
        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);
        assertEquals("https://www.google.com", Objects.requireNonNull(eventItem).getLink().getHref());
        assertEquals("_blank", eventItem.getLink().getTarget());
    }

    @Test
    void testLinkCreationRedirectInternal() {
        context.currentResource(DEST_PATH + "/event-folder-01/event-03/jcr:content/eventitem");
        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);
        assertEquals("/content/softwareag.html", Objects.requireNonNull(eventItem).getLink().getHref());
        assertEquals("_blank", eventItem.getLink().getTarget());
    }

    @Test
    void testLinkCreationInternal() {
        context.currentResource(DEST_PATH + "/event-folder-01/event-01/jcr:content/eventitem");
        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);
        assertEquals("/content/softwareag/event-folder-01/event-01.html", Objects.requireNonNull(eventItem).getLink().getHref());
        assertEquals("_self", eventItem.getLink().getTarget());
    }

    @Test
    void testLinkExternal() {
        context.currentResource(DEST_PATH + "/event-folder-01/event-04/jcr:content/eventitem");
        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);
        assertEquals("https://www.bbc.com", Objects.requireNonNull(eventItem).getLink().getHref());
        assertEquals("_blank", eventItem.getLink().getTarget());
    }

    @Test
    void testLinkExternalOverridesPageRedirect() {
        context.currentResource(DEST_PATH + "/event-folder-01/event-05/jcr:content/eventitem");
        eventItem = Objects.requireNonNull(context.currentResource()).adaptTo(EventItemModel.class);
        assertEquals("https://www.bbc.com", Objects.requireNonNull(eventItem).getLink().getHref());
        assertEquals("_blank", eventItem.getLink().getTarget());
    }
}
