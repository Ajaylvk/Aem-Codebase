package com.softwareag.core.servlets.testutils;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseCodeServletTest {

    private MockSlingHttpServletRequest request;
    private final MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
    private final ResponseCodeServlet systemUnderTest = new ResponseCodeServlet();

    /**
     * Checks if doGet method sets the status to 500.
     *
     * @throws ServletException
     *     API related exception
     * @throws IOException
     *     API related exception
     */
    @Test
    void test_doGet() throws ServletException, IOException {
        systemUnderTest.doGet(request, response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

}
