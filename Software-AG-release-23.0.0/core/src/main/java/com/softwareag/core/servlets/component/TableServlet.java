package com.softwareag.core.servlets.component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.softwareag.core.constants.GenericConstants;
import com.softwareag.core.models.table.Table;
import com.softwareag.core.services.table.TableAction;
import com.softwareag.core.services.table.TableActionResult;
import com.softwareag.core.services.table.TableService;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import static com.softwareag.core.services.timezone.TimeZoneService.DELIMITER;
import static com.softwareag.core.servlets.component.TableServlet.EXTENSION_JSON;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(
    service = Servlet.class,
    property = {
        SLING_SERVLET_METHODS + DELIMITER + HttpConstants.METHOD_POST,
        SLING_SERVLET_RESOURCE_TYPES + DELIMITER + Table.RESOURCE_TYPE,
        SLING_SERVLET_SELECTORS + DELIMITER + TableServlet.SELECTOR,
        SLING_SERVLET_EXTENSIONS + DELIMITER + EXTENSION_JSON
    }
)

public class TableServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(TableServlet.class);
    private static final Gson GSON = new Gson();

    protected static final String SELECTOR = "performAction";
    protected static final String EXTENSION_JSON = "json";

    @Reference
    private transient TableService tableService;

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        final long started = System.currentTimeMillis();
        final TableActionResult actionResult = new TableActionResult();
        try {
            final TableAction action = createTableAction(request);
            tableService.handleAction(actionResult, action);
        } catch (final RuntimeException e) {
            final String errorMsg = String.format("Error occured during peforming operation: %s", e.getMessage());
            LOG.error(errorMsg, e);

            actionResult.setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            actionResult.setFailureMessage(errorMsg);
        } finally {
            actionResult.setDurationInMillis(System.currentTimeMillis() - started);
            writeJsonObject(response, actionResult);
        }
    }

    private JsonObject getJsonObject(final SlingHttpServletRequest request) throws IOException {
        final StringWriter writer = new StringWriter();
        IOUtils.copy(request.getInputStream(), writer, request.getCharacterEncoding());
        final String jsonString = writer.toString();

        return new JsonParser().parse(jsonString).getAsJsonObject();
    }

    private TableAction createTableAction(final SlingHttpServletRequest request) throws IOException {
        final JsonObject jsonObject = getJsonObject(request);
        final TableAction action = GSON.fromJson(jsonObject, TableAction.class);
        action.setTableResource(request.getResource());
        action.resolveTableProperties();
        return action;
    }

    private void writeJsonObject(final SlingHttpServletResponse response, final TableActionResult actionResult) throws IOException {
        final String jsonString = getResultJson(actionResult);
        if (actionResult != null) {
            if (actionResult.getResponseCode() != HttpURLConnection.HTTP_OK) {
                response.setStatus(actionResult.getResponseCode());
            } else if (!actionResult.isSuccess()) {
                actionResult.setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                response.setStatus(actionResult.getResponseCode());
            }
        } else {
            response.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        }

        final PrintWriter responseWriter = response.getWriter();
        response.setContentType(GenericConstants.CONTENT_TYPE_APPLICATION_JSON);
        response.setCharacterEncoding("utf-8");
        response.setContentLength(jsonString.length());
        responseWriter.write(jsonString);
    }

    private String getResultJson(final TableActionResult actionResult) {
        if (actionResult != null) {
            return GSON.toJson(actionResult);
        } else {
            return "{\"success\": false}";
        }
    }

}
