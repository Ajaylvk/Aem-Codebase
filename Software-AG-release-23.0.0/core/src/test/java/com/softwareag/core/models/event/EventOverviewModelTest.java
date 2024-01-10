package com.softwareag.core.models.event;

import com.day.cq.wcm.scripting.WCMBindingsConstants;
import com.softwareag.core.services.language.LanguageService;
import com.softwareag.core.services.query.QueryConfigService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EventOverviewModelTest {

    @Mock
    private QueryConfigService queryConfigService;

    @Mock
    private LanguageService languageService;

    private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);
    private static final String TAGS_SOFTWARE = "/content/cq:tags/softwareag";
    private static final String DEST_PATH = "/content/softwareag";

    private EventOverviewModel modelUnderTest;

    @BeforeEach
    public void setUp() throws ParseException {
        context.load().json("/components/event/eventoverview.json", DEST_PATH);
        context.load().json("/tags/tagList.json", TAGS_SOFTWARE);
        context.currentResource(DEST_PATH + "/jcr:content/eventoverview");
        SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, context.currentPage());
        context.registerService(QueryConfigService.class, queryConfigService);
        context.registerService(languageService);
        context.addModelsForPackage("com.softwareag.core.models");
    }

    @Test
    public void basicTest() {
        modelUnderTest = context.request().adaptTo(EventOverviewModel.class);
        assertNotNull(modelUnderTest);
        List<EventItemModel> eventItemModelList = modelUnderTest.getEventItems();
        assertThat(eventItemModelList.size()).isEqualTo(3);
        assertTrue(StringUtils.equals("Event Item 04", eventItemModelList.get(0).getEventTitle()));
        assertTrue(StringUtils.equals("Event Item 02-01", eventItemModelList.get(1).getEventTitle()));
        assertTrue(StringUtils.equals("Event Item 03", eventItemModelList.get(2).getEventTitle()));
        assertThat(modelUnderTest.getTitle()).isEqualTo("Event Overview Title");
        assertThat(modelUnderTest.getAmountOfMoreLoadedEventItems()).isEqualTo(10);
        assertThat(modelUnderTest.getAmountOfListedEventItems()).isEqualTo(10);
        assertThat(modelUnderTest.getCopyText()).isEqualTo("Event Overview Copy Text");
        List<String> countryTags = modelUnderTest.getCountryTagList();
        assertThat(countryTags.size()).isEqualTo(3);
        // check that the event country tag list is sorted alphabetically
        assertThat(countryTags.get(0)).isEqualTo("England");
        assertThat(countryTags.get(1)).isEqualTo("France");
        assertThat(countryTags.get(2)).isEqualTo("Germany");
        List<String> typeTags = modelUnderTest.getEventTypeTagList();
        assertThat(typeTags.size()).isEqualTo(3);
        // check that the event type tag list is sorted alphabetically
        assertThat(typeTags.get(0)).isEqualTo("Concert");
        assertThat(typeTags.get(1)).isEqualTo("Conference");
        assertThat(typeTags.get(2)).isEqualTo("Sport");
    }

    /* expected results
    event-01 should not show, in the past
    event-02 should not show, hidden
    PRESENT: event-03 should be last (in 2091)
    PRESENT: event-04 should be first (in 2089)
    PRESENT: event-02-01 should be between (in 2090)
     */

    @Test
    public void testQueryLimit() {
        when(queryConfigService.getEventQueryLimit()).thenReturn(2);
        modelUnderTest = context.request().adaptTo(EventOverviewModel.class);
        assertNotNull(modelUnderTest);
        assertEquals(2, modelUnderTest.getEventItems().size());
    }
}
