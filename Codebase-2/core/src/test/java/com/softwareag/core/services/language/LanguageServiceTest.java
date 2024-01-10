package com.softwareag.core.services.language;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
    private static final String TENANT_PATH = "/content/softwareag";
    private static final String TEST_DOMAIN = "https://www.softwareag.com";

    @Mock
    Externalizer externalizer;

    private MockSlingHttpServletRequest request;

    @Mock
    LanguageManager languageManager;

    @InjectMocks
    LanguageService serviceUnderTest;

    @BeforeEach
    public void setUp() {
        context.registerService(serviceUnderTest);
        context.load().json("/services/language.json", TENANT_PATH);

        request = new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext()) {
            @Override
            public String getResponseContentType() {
                return "text/xml";
            }
        };
    }

    @Test
    void getAllLanguageRootsTest() {
        context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage");
        List<Page> languageRoots = serviceUnderTest.getAllLanguageRoots(context.currentPage());
        String[] requiredPaths = {TENANT_PATH + "/de/de_de", TENANT_PATH + "/corporate/en_corporate", TENANT_PATH + "/ch/de_ch", TENANT_PATH + "/ch/fr_ch"};
        List<String> requiredPathsList = Arrays.asList(requiredPaths);
        for (Page page : languageRoots) {
            assertTrue(requiredPathsList.contains(page.getPath()));
        }
        assertEquals(4, languageRoots.size());
    }

    @Test
    void getAllLanguageRootsJunkTest() {
        context.currentPage(TENANT_PATH + "/junk/junklanguage");
        List<Page> languageRoots = serviceUnderTest.getAllLanguageRoots(context.currentPage());
        String[] requiredPaths = {TENANT_PATH + "/de/de_de", TENANT_PATH + "/corporate/en_corporate", TENANT_PATH + "/ch/de_ch", TENANT_PATH + "/ch/fr_ch"};
        List<String> requiredPathsList = Arrays.asList(requiredPaths);
        for (Page page : languageRoots) {
            assertTrue(requiredPathsList.contains(page.getPath()));
        }
        assertEquals(4, languageRoots.size());
    }


    @Test
    void getPageInternationalVersionList() {
        when(externalizer.absoluteLink(eq(request), eq(request.getScheme()), anyString())).then(i -> TEST_DOMAIN + i.getArgument(2));

        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage/2ndlevelpage");
        List<PageInternationalVersion> pageList = serviceUnderTest.getPageInternationalVersionList(context.pageManager(), currentPage, request);
        String[] requiredUrls = {TEST_DOMAIN + TENANT_PATH + "/de/de_de/firstlevelpage/2ndlevelpage.html",
                                 TEST_DOMAIN + TENANT_PATH + "/corporate/en_corporate/firstlevelpage/2ndlevelpage.html",
                                 TEST_DOMAIN + TENANT_PATH + "/ch/fr_ch/firstlevelpage/2ndlevelpage.html"};
        List<String> requiredUrlList = Arrays.asList(requiredUrls);
        for (PageInternationalVersion version : pageList) {
            assertTrue(requiredUrlList.contains(version.getLanguageHref()));
        }
        assertEquals(3, pageList.size());
    }

    @Test
    void getPageInternationalVersionNoneList() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage/2ndlevelpage2");
        List<PageInternationalVersion> pageList = serviceUnderTest.getPageInternationalVersionList(context.pageManager(), currentPage, request);
        assertEquals(0, pageList.size());
    }

    @Test
    void getCountriesTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage/2ndlevelpage");
        List<Page> countriesList = serviceUnderTest.getCountries(currentPage);
        String[] requiredCountries = {TENANT_PATH + "/de", TENANT_PATH + "/corporate", TENANT_PATH + "/ch"};
        List<String> requiredCountriesList = Arrays.asList(requiredCountries);
        for (Page country : countriesList) {
            assertTrue(requiredCountriesList.contains(country.getPath()));
        }
        assertEquals(3, countriesList.size());
    }

    @Test
    void getLanguagesForCountryTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch");
        List<Page> languagesList = serviceUnderTest.getLanguagesForCountry(currentPage);
        String[] requiredLanguages = {TENANT_PATH + "/ch/de_ch", TENANT_PATH + "/ch/fr_ch"};
        List<String> requiredLanguagesList = Arrays.asList(requiredLanguages);
        for (Page language : languagesList) {
            assertTrue(requiredLanguagesList.contains(language.getPath()));
        }
        assertEquals(2, languagesList.size());
    }

    @Test
    void getLanguagesForCountryTestJunk() {
        Page currentPage = context.currentPage(TENANT_PATH + "/junk");
        List<Page> languagesList = serviceUnderTest.getLanguagesForCountry(currentPage);
        assertEquals(0, languagesList.size());
    }

    @Test
    void getLocaleTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage/2ndlevelpage");
        when(languageManager.getLanguage(currentPage.getContentResource())).thenReturn(new Locale("de", "ch"));
        assertEquals(new Locale("de", "ch"), serviceUnderTest.getLocale(currentPage));
    }

    @Test
    void getLocaleForCorporateTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/corporate/en_corporate/firstlevelpage/2ndlevelpage");
        assertEquals(Locale.US, serviceUnderTest.getLocale(currentPage));
    }

    @Test
    void getLanguageTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch/firstlevelpage/2ndlevelpage");
        when(languageManager.getLanguage(currentPage.getContentResource())).thenReturn(new Locale("de", "ch"));
        assertEquals("de", serviceUnderTest.getLanguage(currentPage.getContentResource()));
    }

    @Test
    void getLanguageIncorrectCountryTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/corporate/en_corporate/firstlevelpage/2ndlevelpage");
        assertEquals("en", serviceUnderTest.getLanguage(currentPage.getContentResource()));
    }

    @Test
    void getLanguageJunkTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/junk/junklanguage/firstlevelpage/2ndlevelpage");
        when(languageManager.getLanguage(currentPage.getContentResource())).thenReturn(null);
        assertEquals("", serviceUnderTest.getLanguage(currentPage.getContentResource()));
    }

    @Test
    void getLocaleStringTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/ch/de_ch");
        assertEquals("de-CH", serviceUnderTest.getLocaleString(currentPage));
    }

    @Test
    void getLocaleStringCorporateTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/corporate/en_corporate");
        assertEquals("en-US", serviceUnderTest.getLocaleString(currentPage));
    }

    @Test
    void getLocaleStringJunkTest() {
        Page currentPage = context.currentPage(TENANT_PATH + "/junk/junklanguage");
        assertEquals("", serviceUnderTest.getLocaleString(currentPage));
    }

}
