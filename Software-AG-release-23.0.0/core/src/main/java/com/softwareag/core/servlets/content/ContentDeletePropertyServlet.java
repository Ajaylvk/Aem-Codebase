package com.softwareag.core.servlets.content;

import com.google.gson.Gson;
import com.softwareag.core.constants.GenericConstants;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.resource.filter.ResourceFilterStream;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.softwareag.core.constants.GenericConstants.JCR_PRIMARY_TYPE_CQ_PAGE;
import static com.softwareag.core.services.timezone.TimeZoneService.DELIMITER;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * When content editors delete a certain page it is actually NOT deleted but
 * just adds 'deleted' and 'deletedBy' properties to its jcr:content.
 * If chief editor does not approve deletion of these pages it remains hidden in the UI for all the editors
 * but NOT deleted from repository.
 *
 * This servlet removes the 'deleted' and 'deletedBy' properties
 * and restore the pages so it can be visible again.
 *
 * An example of executing the servlet:
 * http://localhost:4502/bin/deleteProperty.softwareag@language-master.delete.json
 *
 * Path always starts with '/content/'
 */
@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_METHODS + DELIMITER + HttpConstants.METHOD_GET,
        SLING_SERVLET_PATHS + DELIMITER + ContentDeletePropertyServlet.SERVLET_PATH,
        SLING_SERVLET_EXTENSIONS + DELIMITER + ContentDeletePropertyServlet.EXTENSION_JSON
    }
)

public class ContentDeletePropertyServlet extends SlingAllMethodsServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ContentDeletePropertyServlet.class);
    protected static final String SERVLET_PATH = "/bin/deleteProperty";
    protected static final String EXTENSION_JSON = "json";
    int count;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        String[] selectors = request.getRequestPathInfo().getSelectors();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> countMap = new HashMap<>();

        if ("delete".equals(selectors[1])) {

            String path = "/content/" + selectors[0].replace("@", "/");
            try {
                ResourceFilterStream resourceStream = Objects.requireNonNull(request.getResourceResolver().getResource(path))
                    .adaptTo(ResourceFilterStream.class);

                Objects.requireNonNull(resourceStream).setBranchSelector(JCR_PRIMARY_TYPE_CQ_PAGE)
                    .setChildSelector("[jcr:content/sling:resourceType] != 'apps/softwareag/components/structure/folder'")
                    .stream()
                    .forEach(r -> {
                        Map<String, Object> finalMap = new HashMap<>();
                        try {
                            Node metaNode = Objects.requireNonNull(r.getChild("jcr:content")).adaptTo(Node.class);
                            if (Objects.requireNonNull(metaNode).getProperty("deleted") != null && metaNode.getProperty("deletedBy") != null) {
                                metaNode.getProperty("deleted").remove();
                                metaNode.getProperty("deletedBy").remove();
                                metaNode.getSession().save();
                                finalMap.put("PageURL", r.getPath());
                                list.add(finalMap);
                                count++;
                            }

                        } catch (RepositoryException e) {
                            LOG.error("Error while processing tags for current Page.", e);
                        }

                    });
                countMap.put("Total Successful Count", count);

                list.add(countMap);
            } catch (final RuntimeException e) {
                final String errorMsg = String.format("Error occured during peforming operation '%s'", e.getMessage());
                LOG.error(errorMsg, e);
                Map<String, Object> errorMap = new HashMap<>();
                errorMap.put("Error", "Error while retreiving the Correct Path");
                list.add(errorMap);
            } finally {
                Gson gson = new Gson();
                String jsonString = gson.toJson(list);
                LOG.info("Gson:: {}", jsonString);

                final PrintWriter responseWriter = response.getWriter();
                response.setContentType(GenericConstants.CONTENT_TYPE_APPLICATION_JSON);
                response.setCharacterEncoding("utf-8");
                response.setContentLength(jsonString.length());
                responseWriter.write(jsonString);
            }
        }

    }

}
