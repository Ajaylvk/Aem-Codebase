package com.softwareag.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.softwareag.core.services.timezone.TimeZoneService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
class TimeZoneDataSourceServletTest {

    private static final Map<String, String> TIMEZONE_MAP = new LinkedHashMap<String, String>() {
        {
            put("GMT", "(GMT) Greenwich Mean Time");
            put("GMT+1:00", "(GMT+1:00) European Central Time");
            put("GMT+2:00", "(GMT+2:00) Eastern European Time");
            put("GMT-1:00", "(GMT-1:00) Central African Time");
        }
    };

    private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private SlingHttpServletRequest request;

    private SlingHttpServletResponse response;

    @BeforeEach
    public void setUp() throws IllegalAccessException {
        request = context.request();
        response = context.response();
    }

    @Test
    public void testDoGet() throws IllegalAccessException {
        final TimeZoneService timeZoneService = mock(TimeZoneService.class);
        when(timeZoneService.getTimeZones()).thenReturn(TIMEZONE_MAP);

        final TimeZoneDataSourceServlet servletUnderTest = new TimeZoneDataSourceServlet();
        FieldUtils.writeField(servletUnderTest, "timeZoneService", timeZoneService, true);

        servletUnderTest.doGet(request, response);
        final DataSource dataSource = (DataSource) request.getAttribute(DataSource.class.getName());

        assertNotNull(dataSource);

        int counter = 0;
        final Iterator iterator = dataSource.iterator();
        while (iterator.hasNext()) {
            final Resource resource = (Resource) iterator.next();
            assertTrue(StringUtils.startsWith(resource.getValueMap().get("text", String.class), "(GMT"));
            assertTrue(StringUtils.startsWith(resource.getValueMap().get("value", String.class), "GMT"));
            counter++;
        }

        assertEquals(TIMEZONE_MAP.size(), counter);
    }

    @Test
    public void testDoGetTimeZoneServiceIsNull() {
        final TimeZoneDataSourceServlet servletUnderTest = new TimeZoneDataSourceServlet();
        servletUnderTest.doGet(request, response);

        final DataSource dataSource = (DataSource) request.getAttribute(DataSource.class.getName());

        assertFalse(dataSource.iterator().hasNext());
    }
}
