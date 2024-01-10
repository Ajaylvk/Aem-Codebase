package com.softwareag.core.models.press;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PressOverviewTest {

    private static final String DEST_PATH = "/content/softwareag/presspage";

    private final AemContext context = new AemContext(ResourceResolverType.JCR_OAK);

    @Mock
    private QueryConfigService queryConfigService;

    @Mock
    private LanguageService languageService;

    private PressOverview modelUnderTest;

    @BeforeEach
    public void setUp() throws ParseException {
        context.load().json("/components/press/pressoverview.json", DEST_PATH);
        context.currentResource(DEST_PATH + "/jcr:content/pressoverview");
        context.addModelsForPackage("com.softwareag.core.models");
        context.registerService(languageService);

        SlingBindings slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindingsConstants.NAME_CURRENT_PAGE, context.currentPage());
    }

    /*
    expected, in order (release date newest to oldest):
    - PRESENT: press-folder-01/press-page-01
    - press-folder-02/press-page-04  <-- not shown as hidden
    - PRESENT: press-folder-02/press-page-03  <-- only one press component returned (json contains pressitem and pressitem2)
    - PRESENT: press-folder-01/press-folder-01-01/press-page-01-01
    - corrupt content pages ignored
    */

    @Test
    public void testBasic() {
        modelUnderTest = context.request().adaptTo(PressOverview.class);
        assertNotNull(modelUnderTest);
        assertEquals(3, modelUnderTest.getSearchResults().size());
        assertEquals(6, modelUnderTest.getAmountOfListedPressItems());
        assertEquals(20, modelUnderTest.getAmountOfMoreLoadedPressItems());
        List<PressItem> resultList = modelUnderTest.getSearchResults();
        assertTrue(StringUtils.equals("Press Item 01", resultList.get(0).getPressTitle()));
        assertTrue(StringUtils.equals("Press Item 03", resultList.get(1).getPressTitle()));
        assertTrue(StringUtils.equals("Press Item 01-01", resultList.get(2).getPressTitle()));
    }

    @Test
    public void testLimit() {
        when(queryConfigService.getPressQueryLimit()).thenReturn(2);
        context.registerService(QueryConfigService.class, queryConfigService);
        modelUnderTest = context.request().adaptTo(PressOverview.class);
        assertNotNull(modelUnderTest);
        assertEquals(2, modelUnderTest.getSearchResults().size());
    }

}
