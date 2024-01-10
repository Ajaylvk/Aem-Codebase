package com.softwareag.empower.core.servlets;

import com.adobe.acs.commons.email.EmailService;
import com.day.cq.mailer.MessageGatewayService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(
        service = { Servlet.class },
        property = {
                SLING_SERVLET_PATHS + "=/bin/sendemail"
        }
)
public class SendEmailServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SendEmailServlet.class);

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private EmailService emailService;

    private static final String EMAIL_TEMPLATE = "/etc/notification/email/empower/email.html";

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        JSONObject jsonResponse = new JSONObject();
        boolean sent = false;
        response.setContentType("text/html");

        try {
            String json = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            // Parse the JSON data
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(json).getAsJsonObject();

            String htmlTable = null;
            if (jsonObject.has("Product Details")) {
                String productDetails = jsonObject.get("Product Details").getAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(productDetails);
                    JsonNode itemsNode = jsonNode.get("Items");

                    // Generate HTML table
                    htmlTable = generateHtmlTable(itemsNode);
                } catch (Exception e) {
                    LOG.error("Error occurred while generating html table!", e);
                }
            }

            final HashMap<String, String> emailParams = new HashMap<>();
            if (htmlTable != null) {
                emailParams.put("productDetailsLabel", "Product Details:");
                emailParams.put("productDetails", htmlTable);
            } else {
                emailParams.put("productDetailsLabel", "");
                emailParams.put("productDetails", "");
            }

            StringBuilder htmlBuilder = new StringBuilder("<div>");
            for (String key : jsonObject.keySet()) {
                if(!key.equals("Product Details") && !key.equals("Recipient Email")
                        && !key.equals("Mail Subject") && !key.equals("CC Email")) {
                    //String dynamicKey = key;
                    JsonElement dynamicValue = jsonObject.get(key);

                    // Generate values dynamically
                    htmlBuilder.append("<p>").append(key).append(": ").append(dynamicValue).append("</p><br>");
                }
            }

            htmlBuilder.append("</div>");
            emailParams.put("dynamicValues", htmlBuilder.toString());

            // Array of email recipients
            String[] recipients = null;
            if (jsonObject.has("Recipient Email")) {
                JsonArray recipientEmailArray = jsonObject.getAsJsonArray("Recipient Email");

                recipients = new String[recipientEmailArray.size()];

                for (int i = 0; i < recipientEmailArray.size(); i++) {
                    recipients[i] = recipientEmailArray.get(i).getAsString();
                }
            }

            if (jsonObject.has("CC Email")) {
                emailParams.put("ccRecipient", jsonObject.get("CC Email").getAsString());
            }

            //Generating Mail Subject
            String mailSubject = jsonObject.get("Mail Subject").getAsString();
            emailParams.put("subject", mailSubject);

            emailService.sendEmail(EMAIL_TEMPLATE, emailParams, recipients);
            sent = true;
        } catch (Exception e) {
            LOG.error("Error occurred while sending email!", e);
        }
        response.setStatus(200);
        try {
            jsonResponse.put("result", sent ? "done" : "something went wrong");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            LOG.error("Error occurred forming response!", e);
        }
    }

    private static String generateHtmlTable(JsonNode itemsNode) {
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table border='1'>");
        htmlTable.append("<tr><th>Product</th><th>Product Version</th><th>Operating System</th></tr>");

        for (JsonNode item : itemsNode) {
            String product = item.get("Product").asText();
            String productVersion = item.get("Product Version").asText();
            String operatingSystem = item.get("Operating System").asText();

            htmlTable.append("<tr>");
            htmlTable.append("<td>").append(product).append("</td>");
            htmlTable.append("<td>").append(productVersion).append("</td>");
            htmlTable.append("<td>").append(operatingSystem).append("</td>");
            htmlTable.append("</tr>");
        }

        htmlTable.append("</table>");
        return htmlTable.toString();
    }

}