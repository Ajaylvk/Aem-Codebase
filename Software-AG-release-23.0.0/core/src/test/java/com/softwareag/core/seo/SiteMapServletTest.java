package com.softwareag.core.seo;

import com.day.cq.commons.Externalizer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.xmlunit.matchers.EvaluateXPathMatcher.hasXPath;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class SiteMapServletTest {
    private static final Map<String, String> NS = Collections.singletonMap("ns", SiteMapServlet.NS);

    AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);
    SiteMapServlet servlet = new SiteMapServlet();

    @Mock
    private Externalizer externalizer;

    private MockSlingHttpServletRequest request;

    private MockSlingHttpServletResponse response;

    @BeforeEach
    public void setup() throws InterruptedException {
        context.load().json(getClass().getResourceAsStream("/seo/sitemapservlet.json"), "/content/geometrixx");
        context.registerService(Externalizer.class, externalizer);
        response = context.response();
        request = new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext()) {
            @Override
            public String getResponseContentType() {
                return "text/xml";
            }
        };
        request.setResource(context.resourceResolver().getResource("/content/geometrixx/en"));
        when(externalizer.absoluteLink(eq(request), eq(request.getScheme()), anyString())).then(i -> "http://test.com" + i.getArgument(2));
    }

    @Test
    void testDefaultPageSetup() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        context.registerInjectActivateService(servlet, parameters);
        servlet.doGet(request, response);
        String output = response.getOutputAsString();
        assertThat(output, hasXPath("(//ns:loc)[1]/text()", equalTo("http://test.com/content/geometrixx/en.html")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[2]/text()", equalTo("http://test.com/content/geometrixx/en/events.html")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[3]/text()", equalTo("http://test.com/content/geometrixx/en/about.html")).withNamespaceContext(NS));
        assertTrue(StringUtils.contains(output, "hiddenPage"));
        assertFalse(StringUtils.contains(output, "folder"));
        assertFalse(StringUtils.contains(output, "noIndexPage"));
        assertTrue(StringUtils.contains(output, "empty-redirect"));
    }

    @Test
    void testExtensionlessPages() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("extensionlessUrls", true);
        context.registerInjectActivateService(servlet, parameters);
        servlet.doGet(request, response);
        String output = response.getOutputAsString();
        assertThat(output, hasXPath("(//ns:loc)[1]/text()", equalTo("http://test.com/content/geometrixx/en/")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[2]/text()", equalTo("http://test.com/content/geometrixx/en/events/")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[3]/text()", equalTo("http://test.com/content/geometrixx/en/about/")).withNamespaceContext(NS));
        assertTrue(StringUtils.contains(output, "hiddenPage"));
        assertFalse(StringUtils.contains(output, "folder"));
        assertFalse(StringUtils.contains(output, "noIndexPage"));
        assertTrue(StringUtils.contains(output, "empty-redirect"));
    }

    @Test
    void testExtensionlessAndSlashlessPages() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("configurationValue", "/somepath");
        parameters.put("extensionlessUrls", true);
        parameters.put("removeSlash", true);
        context.registerInjectActivateService(servlet, parameters);
        servlet.doGet(request, response);
        String output = response.getOutputAsString();
        assertThat(output, hasXPath("(//ns:loc)[1]/text()", equalTo("http://test.com/content/geometrixx/en")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[2]/text()", equalTo("http://test.com/content/geometrixx/en/events")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[3]/text()", equalTo("http://test.com/content/geometrixx/en/about")).withNamespaceContext(NS));
        assertTrue(StringUtils.contains(output, "hiddenPage"));
        assertFalse(StringUtils.contains(output, "folder"));
        assertFalse(StringUtils.contains(output, "noIndexPage"));
        assertTrue(StringUtils.contains(output, "empty-redirect"));
    }

    @Test
    void testUrlRewrite() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("urlRewrites", "/content/geometrixx/:/");
        context.registerInjectActivateService(servlet, parameters);
        servlet.doGet(request, response);
        String output = response.getOutputAsString();
        assertThat(output, hasXPath("(//ns:loc)[1]/text()", equalTo("http://test.com/en.html")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[2]/text()", equalTo("http://test.com/en/events.html")).withNamespaceContext(NS));
        assertThat(output, hasXPath("(//ns:loc)[3]/text()", equalTo("http://test.com/en/about.html")).withNamespaceContext(NS));
        assertTrue(StringUtils.contains(output, "hiddenPage"));
        assertFalse(StringUtils.contains(output, "folder"));
        assertFalse(StringUtils.contains(output, "noIndexPage"));
        assertTrue(StringUtils.contains(output, "empty-redirect"));
    }

}
