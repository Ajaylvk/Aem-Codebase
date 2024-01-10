package com.softwareag.core.servlets.download;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class DownloadServletTest {

    private static final String TEST_BASE = "/servlets/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_CONTENT_DAM_JSON = TEST_BASE + "/test-content-dam.json";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;

    private final AemContext context = new AemContext();

    private DownloadServlet downloadServlet;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        context.load().binaryFile(TEST_BASE + "/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
        downloadServlet = new DownloadServlet();
    }

    @Test
    void testAttachmentAssetDownload() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "");
        context.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        context.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(context.request(), context.response());
        assertTrue(context.response().containsHeader(DownloadServlet.CONTENT_DISPOSITION_HEADER));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", context.response().getHeader(DownloadServlet.CONTENT_DISPOSITION_HEADER));
        assertEquals(8192, context.response().getBufferSize());
    }

    @Test
    void testInlineAssetDownload() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "");
        context.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR + "." + DownloadServlet.INLINE_SELECTOR);
        context.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(context.request(), context.response());
        assertTrue(context.response().containsHeader(DownloadServlet.CONTENT_DISPOSITION_HEADER));
        assertEquals("inline", context.response().getHeader(DownloadServlet.CONTENT_DISPOSITION_HEADER));
        assertEquals(8192, context.response().getBufferSize());
    }

    @Test
    void testNotModifiedResponse() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "Fri, 19 Oct 2018 19:24:07 GMT");
        downloadServlet.doGet(context.request(), context.response());
        assertEquals(304, context.response().getStatus());
    }
}
