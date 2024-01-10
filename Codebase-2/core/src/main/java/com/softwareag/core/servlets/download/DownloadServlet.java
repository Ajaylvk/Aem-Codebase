package com.softwareag.core.servlets.download;

import com.day.cq.dam.api.Asset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

/**
 * This servlet is basically a copy of the AEM core components Download servlet.
 * Unused functions have been removed.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes=dam:Asset",
        "sling.servlet.resourceTypes=nt:file",
        "sling.servlet.selectors=" + DownloadServlet.SELECTOR
    }
)
public class DownloadServlet extends SlingAllMethodsServlet {

    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public static final String INLINE_SELECTOR = "inline";
    public static final String SELECTOR = "sagdownload";

    private static final long serialVersionUID = 1L;
    private static final String RFC_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
        Asset asset = request.getResource().adaptTo(Asset.class);
        if (asset != null) {
            if (!isChanged(asset.getLastModified(), request)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            Optional<InputStream> inputStream = Optional.empty();
            try {
                inputStream = getInputStream(asset);
                if (!inputStream.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                sendResponse(inputStream.get(), asset.getOriginal().getSize(), asset.getMimeType(), asset.getName(), asset.getLastModified(), response,
                    checkForInlineSelector(request));
            } finally {
                if (inputStream.isPresent()) {
                    inputStream.get().close();
                }
            }
        }
    }

    private boolean checkForInlineSelector(SlingHttpServletRequest request) {
        return Arrays.asList(request.getRequestPathInfo().getSelectors()).contains(INLINE_SELECTOR);
    }

    private Optional<InputStream> getInputStream(Asset asset) {
        return Optional.ofNullable(asset.getOriginal())
            .map(r -> r.adaptTo(InputStream.class));
    }

    private void sendResponse(InputStream stream, long size, String mimeType, String filename, long lastModifiedDate, SlingHttpServletResponse response,
                              boolean inline) throws IOException {
        response.setContentType(mimeType);

        // only set the size contentLength header if we have valid data
        if (size != -1) {
            response.setContentLength((int) size);
        }
        if (inline) {
            response.setHeader(CONTENT_DISPOSITION_HEADER, INLINE_SELECTOR);
        } else {
            response.setHeader(CONTENT_DISPOSITION_HEADER, "attachment; filename=\"" + filename + "\"");
        }
        if (lastModifiedDate > 0) {
            response.setHeader(HttpConstants.HEADER_LAST_MODIFIED, getLastModifiedDate(lastModifiedDate));
        }
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            IOUtils.copy(stream, outputStream);
        }
    }

    private String getLastModifiedDate(long lastModifiedDate) {
        Instant instant = Instant.ofEpochMilli(lastModifiedDate);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(RFC_DATE_FORMAT).withLocale(Locale.US).withZone(ZoneId.of("GMT"));

        return dateFormatter.format(instant);
    }

    private boolean isChanged(long lastModifiedDate, SlingHttpServletRequest request) {

        if (Collections.list(request.getHeaderNames()).contains(HttpConstants.HEADER_IF_MODIFIED_SINCE)) {
            String headerDate = request.getHeader(HttpConstants.HEADER_IF_MODIFIED_SINCE);

            if (StringUtils.isNotEmpty(headerDate)) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(RFC_DATE_FORMAT).withLocale(Locale.US).withZone(ZoneId.of("GMT"));
                ZonedDateTime zonedDateTime = ZonedDateTime.from(dateTimeFormatter.parse(headerDate));
                long headerTime = zonedDateTime.toInstant().toEpochMilli();

                return headerTime < lastModifiedDate;
            }
        }

        return true;
    }

}
