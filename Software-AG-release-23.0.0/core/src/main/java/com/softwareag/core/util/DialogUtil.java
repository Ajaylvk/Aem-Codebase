package com.softwareag.core.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dialog utility class to provide dialog specific utility methods, eg. getContentResource.
 *
 * @author : Yunus Bayraktar (bayrakta@adobe.com)
 */
public final class DialogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DialogUtil.class);
    public static final String GRANITE_CONTENT_PATH = "granite.ui.form.contentpath";
    private static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
    private static Pattern pattern = Pattern.compile(HTML_PATTERN);

    private DialogUtil() {
    }

    /**
     * Current content resource
     *
     * @param request
     *     Request
     * @return Current content resource or null
     */
    public static Resource getContentResource(final HttpServletRequest request) {
        final String contentPath = getContentPath(request);
        if (contentPath != null) {
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            return slingRequest.getResourceResolver().getResource(contentPath);
        }
        return null;
    }

    /**
     * Current content path
     *
     * @param request
     *     Request
     * @return Current content path or null
     */
    private static String getContentPath(final HttpServletRequest request) {
        return (String) request.getAttribute(GRANITE_CONTENT_PATH);
    }

    /**
     * Generates hidden input attributes for the request resource recursively as a tree.
     *
     * @param request
     *     Current resource request for the hiddenResourceType dialog field.
     * @return List of hidden field attributes as string for the content resource and its children. If no resource found, then empty list.
     */
    public static List<String> getHiddenFields(final HttpServletRequest request) {
        final List<String> hiddenFields = new ArrayList<>();

        final Resource contentResource = getContentResource(request);
        if (contentResource == null) {
            return hiddenFields;
        }

        buildHiddenAttributes(hiddenFields, "", contentResource);

        return hiddenFields;
    }

    /**
     * Extends the given hiddenFields for the given resource and its children recursively.
     *
     * @param hiddenFields
     *     List of hidden field attributes.
     * @param namePrefix
     *     Name prefix for the resource fields, which contains the parent path.
     * @param resource
     *     Resource to load its value map as hidden fields and apply recursion on its children.
     */
    private static void buildHiddenAttributes(final List<String> hiddenFields, final String namePrefix, final Resource resource) {
        if (hiddenFields == null || resource == null) {
            return;
        }

        try {
            extendHiddenFields(hiddenFields, namePrefix, resource);
        } catch (final RepositoryException e) {
            final String errorMsg = String.format("Error occured during hidden field generation for the the resource '%s'.", resource.getPath());
            LOGGER.error(errorMsg, e);
        }

        if (resource.hasChildren()) {
            resource.getChildren().forEach(item ->
                {
                    final String childNamePrefix = (StringUtils.isBlank(namePrefix) ? "" : (namePrefix + "/")) + item.getName();
                    buildHiddenAttributes(hiddenFields, childNamePrefix, item);
                }
            );
        }
    }

    /**
     * Generates hidden input fields for the given resource value map properties and adds them into the provided field list.
     *
     * @param hiddenFields
     *     List of hidden fields to be extended.
     * @param namePrefix
     *     Name prefix to be prepended to the property keys.
     * @param resource
     *     Resource to load its property list.
     * @throws RepositoryException
     *     thrown when the provided resource can not be adapted to a Node object.
     */
    private static void extendHiddenFields(final List<String> hiddenFields, final String namePrefix, final Resource resource) throws RepositoryException {
        if (hiddenFields == null || resource == null || MapUtils.isEmpty(resource.getValueMap())) {
            return;
        }

        final ValueMap valueMap = resource.getValueMap();
        for (final Map.Entry<String, Object> entry : valueMap.entrySet()) {
            final String propKey = entry.getKey();
            // skip the first resource type, as it has to be specified in the hidden field definition
            if (StringUtils.isBlank(namePrefix) && JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY.equals(propKey)) {
                continue;
            }
            final String propName = buildPropName(namePrefix, propKey);

            // Step 1 : generate @TypeHint for the current property
            final Node resourceNode = resource.adaptTo(Node.class);
            final Property property = Objects.requireNonNull(resourceNode).getProperty(propKey);
            final String propertyType = PropertyType.nameFromValue(property.getType());
            final boolean multiple = property.isMultiple();
            final String propTypeName = propName + "@TypeHint";
            final String propTypeValue = propertyType + (multiple ? "[]" : "");
            addPropValue(hiddenFields, propTypeName, propTypeValue);

            // Step 2 : generate single or multiple input fields for the current property value(s)
            if (multiple) {
                final String[] propValues = valueMap.get(propKey, String[].class);
                if (ArrayUtils.isNotEmpty(propValues)) {
                    Arrays.asList(Objects.requireNonNull(propValues)).forEach(propValue -> addPropValue(hiddenFields, propName, propValue));
                }
            } else {
                String propValue = valueMap.get(propKey, String.class);
                if(checkIfHtml(propValue)) {
                    propValue = StringEscapeUtils.escapeHtml(propValue);
                }
                addPropValue(hiddenFields, propName, propValue);
            }
        }
    }

    private static boolean checkIfHtml(String propValue) {
            Matcher matcher = pattern.matcher(propValue);
            return matcher.find();
    }

    /**
     * Builds property name by appending the property key to the given name prefix, if not blank.
     *
     * @param namePrefix
     *     Name prefix to be extended as a path.
     * @param valueMapPropName
     *     Name of the map property.
     * @return Path to the property.
     */
    private static String buildPropName(final String namePrefix, final String valueMapPropName) {
        final StringBuilder propName = new StringBuilder();
        if (StringUtils.isNotBlank(namePrefix)) {
            propName.append(namePrefix).append("/");
        }
        propName.append(valueMapPropName);
        return propName.toString();
    }

    /**
     * Concatenates the type, name and value attributes for the input and adds the result to the given hiddenFields list.
     *
     * @param hiddenFields
     *     List of hidden fields to be extended.
     * @param propName
     *     Name of the input field.
     * @param propValue
     *     Value of the input field.
     */
    private static void addPropValue(final List<String> hiddenFields, final String propName, final Object propValue) {
        final StringBuilder sb = new StringBuilder();
        sb.append(" type=\"hidden\" ");
        sb.append(" name=\"").append(propName).append("\" ");
        sb.append(" value=\"").append(propValue).append("\" ");

        hiddenFields.add(sb.toString());
    }

}
