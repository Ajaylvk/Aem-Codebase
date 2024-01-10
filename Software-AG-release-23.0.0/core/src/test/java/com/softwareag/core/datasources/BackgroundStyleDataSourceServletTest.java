package com.softwareag.core.datasources;

import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class BackgroundStyleDataSourceServletTest {

    public final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private ContentPolicyManager contentPolicyManager;

    private SlingHttpServletRequest request;

    private SlingHttpServletResponse response;

    private BackgroundStyleDataSourceServlet dataSourceServlet;

    private static final String CONTENT_POLICY_PATH = "/conf/softwareag/settings/wcm/policies/app/components/content/text/component/policy";
    private static final String COMPONENT_PATH = "/content/softwareag/page/jcr:content/parsys/text";

    @BeforeEach
    public void setUp() throws Exception {

        context.load().json("/components/text/text-policy.json", CONTENT_POLICY_PATH);
        Resource policyResource = context.resourceResolver().getResource(CONTENT_POLICY_PATH);
        ContentPolicy policy = policyResource.adaptTo(ContentPolicy.class);

        context.load().json("/components/text/text-component.json",COMPONENT_PATH);
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) context.request().getRequestPathInfo();
        requestPathInfo.setSuffix(COMPONENT_PATH);

        contentPolicyManager = mock(ContentPolicyManager.class);
        registerContentPolicyManager();
        when(contentPolicyManager.getPolicy(any(Resource.class))).thenReturn(policy);

        request = context.request();
        response = context.response();

        dataSourceServlet = new BackgroundStyleDataSourceServlet();
    }

    @Test
    public void testDataSource() throws Exception {
        dataSourceServlet.doGet(request,response);
        DataSource dataSource = (DataSource) request.getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        int runCount = 0;
        Iterator iterator = dataSource.iterator();
        while(iterator.hasNext()) {
            Resource resource = (Resource) iterator.next();
            assertTrue(StringUtils.startsWith(resource.getValueMap().get("value", String.class), "input__colorselect-background-color"));
            assertTrue(StringUtils.startsWith(resource.getValueMap().get("icon", String.class), "border"));
            runCount ++;
        }
        assertEquals(8,runCount);
    }

    @Test
    public void testDataSourceNoPolicy() throws Exception {
        when(contentPolicyManager.getPolicy(any(Resource.class))).thenReturn(null);
        dataSourceServlet.doGet(request,response);
        DataSource dataSource = (DataSource) request.getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        int runCount = 0;
        Iterator iterator = dataSource.iterator();
        while(iterator.hasNext()) {
          runCount ++;
        }
        assertEquals(0,runCount);
    }

    @Test
    public void testDataSourceNoSuffix() throws Exception {

        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(null);
        dataSourceServlet.doGet(request, response);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testDataSourceNoResource() throws Exception {

        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("/some/junk/path");
        dataSourceServlet.doGet(request, response);
        assertEquals(404,response.getStatus());
    }

    private void registerContentPolicyManager() {
        context.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
            new Function<ResourceResolver, ContentPolicyManager>() {
                @Override
                public ContentPolicyManager apply(ResourceResolver resolver) {
                    return contentPolicyManager;
                }
            });
    }
}
