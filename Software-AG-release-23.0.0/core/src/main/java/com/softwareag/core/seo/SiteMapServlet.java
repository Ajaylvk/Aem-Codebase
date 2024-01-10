package com.softwareag.core.seo;

import com.adobe.acs.commons.util.ParameterUtil;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The sitemap servlet is a copy of the ACS Commons Sitemap Generator servlet with the following changes:
 * - update to OSGi DS Annotations
 * - change from checking a single page property to decide of a page should be included or not
 * - minor refactoring to address code smells etc
 * - improved tests
 * to a more extensive property check, in the method isHidden().
 *
 * More details are given in the ACS Commons docu
 * https://adobe-consulting-services.github.io/acs-aem-commons/features/sitemap/index.html
 * The original file is in Git
 * https://github.com/Adobe-Consulting-Services/acs-aem-commons/blob/master/bundle/src/main/java/com/adobe/acs/commons/wcm/impl/SiteMapServlet.java
 */

@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + "= Page and Asset Site Map Servlet",
        "sling.servlet.selectors=sitemap",
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=xml",
        "sling.servlet.resourceTypes=" + SiteMapServlet.ALLOWED_RESOURCETYPE,
        "sling.servlet.resourceTypes=" + SiteMapServlet.ALLOWED_RESOURCETYPE_2

    })
@Designate(ocd = SiteMapServlet.Configuration.class, factory = true)

public final class SiteMapServlet extends SlingSafeMethodsServlet {

    public static final String ALLOWED_RESOURCETYPE = "softwareag/components/structure/homepage";
    public static final String ALLOWED_RESOURCETYPE_2 = "softwareag/components/structure/basic-page";
    private static final Logger LOG = LoggerFactory.getLogger(SiteMapServlet.class);

    @ObjectClassDefinition(name = "Software AG Sitemap", description = "Configuration for the Sitemap")
    @interface Configuration {
        @AttributeDefinition(name = "Externalizer Domain", description = "Must correspond to a configuration of the Externalizer component.", type = AttributeType.STRING)
        String externalizerDomain() default "publish";

        @AttributeDefinition(name = "Include Last Modified", description = "If true, the last modified value will be included in the sitemap.", type = AttributeType.BOOLEAN)
        boolean includeLastmod() default false;

        @AttributeDefinition(name = "Change Frequency Properties", description = "The set of JCR property names which will contain the change frequency value.", type = AttributeType.STRING)
        String[] changefreqProperties();

        @AttributeDefinition(name = "Priority Properties", description = "The set of JCR property names which will contain the priority value.", type = AttributeType.STRING)
        String[] priorityProperties();

        @AttributeDefinition(name = "DAM Folder Property", description = "The JCR property name which will contain DAM folders to include in the sitemap.", type = AttributeType.STRING)
        String damassetsProperty();

        @AttributeDefinition(name = "DAM Asset MIME Types", description = "MIME types allowed for DAM assets.", type = AttributeType.STRING)
        String[] damassetsTypes();

        @AttributeDefinition(name = "URL Rewrites", description = "Colon separated URL rewrites to adjust the <loc> to match your dispatcher's apache rewrites.", type = AttributeType.STRING)
        String[] urlRewrites();

        @AttributeDefinition(name = "Include Inherit Value", description = "If true searches for the frequency and priority attribute in the current page if null looks in the parent.", type = AttributeType.BOOLEAN)
        boolean includeInherit() default false;

        @AttributeDefinition(name = "Extensionless URLs", description = "If true, page links included in sitemap are generated without .html extension and the path is included with a trailing slash, e.g. /content/geometrixx/en/.", type = AttributeType.BOOLEAN)
        boolean extensionlessUrls() default false;

        @AttributeDefinition(name = "Remove Trailing Slash from Extensionless URLs", description = "Only relevant if Extensionless URLs is selected.  If true, the trailing slash is removed from extensionless page links, e.g. /content/geometrixx/en.", type = AttributeType.BOOLEAN)
        boolean removeSlash() default false;

        @AttributeDefinition(name = "Character Encoding", description = "If not set, the container's default is used (ISO-8859-1 for Jetty)", type = AttributeType.STRING)
        String characterEncoding();
    }

    @SuppressWarnings("squid:S1948")
    @Reference
    private Externalizer externalizer;

    private static final String CHANGEFREQ = "changefreq";

    private static final String PRIORITY = "priority";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");

    static final String NS = "http://www.sitemaps.org/schemas/sitemap/0.9";

    private static final String PN_REDIRECT = "cq:redirectTarget";

    private static final String PN_NOINDEX = "noIndex";

    private String externalizerDomain;

    private boolean includeInheritValue;

    private boolean includeLastModified;

    private String[] changefreqProperties;

    private String[] priorityProperties;

    private String damAssetProperty;

    private List<String> damAssetTypes;

    private String characterEncoding;

    private boolean extensionlessUrls;

    private Map<String, String> urlRewrites;

    private boolean removeTrailingSlash;

    @Activate
    void activate(Configuration config) {
        externalizerDomain = config.externalizerDomain();
        includeInheritValue = config.includeInherit();
        includeLastModified = config.includeLastmod();
        changefreqProperties = config.changefreqProperties();
        priorityProperties = config.priorityProperties();
        damAssetProperty = config.damassetsProperty();
        damAssetTypes = Arrays.asList(config.damassetsTypes());
        characterEncoding = config.characterEncoding();
        extensionlessUrls = config.extensionlessUrls();
        urlRewrites = ParameterUtil.toMap(config.urlRewrites(), ":", true, "");
        removeTrailingSlash = config.removeSlash();
        characterEncoding = config.characterEncoding();
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
        response.setContentType(request.getResponseContentType());
        if (StringUtils.isNotEmpty(this.characterEncoding)) {
            response.setCharacterEncoding(characterEncoding);
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return;
        }
        Page page = pageManager.getContainingPage(request.getResource());
        if (page == null) {
            return;
        }
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        XMLStreamWriter stream = null;
        try {
            stream = outputFactory.createXMLStreamWriter(response.getWriter());
            stream.writeStartDocument("1.0");

            stream.writeStartElement("", "urlset", NS);
            stream.writeNamespace("", NS);

            // first do the current page
            write(page, stream, request);

            for (Iterator<Page> children = page.listChildren(new PageFilter(false, true), true); children.hasNext(); ) {
                write(children.next(), stream, request);
            }

            if (!damAssetTypes.isEmpty() && !damAssetProperty.isEmpty()) {
                for (Resource assetFolder : getAssetFolders(page, resourceResolver)) {
                    writeAssets(stream, assetFolder, request);
                }
            }

            stream.writeEndElement();

            stream.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (XMLStreamException e) {
                    LOG.warn("Can not close xml stream writer", e);
                }
            }
        }
    }

    private Collection<Resource> getAssetFolders(Page page, ResourceResolver resolver) {
        List<Resource> allAssetFolders = new ArrayList<>();
        ValueMap properties = page.getProperties();
        String[] configuredAssetFolderPaths = properties.get(damAssetProperty, String[].class);
        if (configuredAssetFolderPaths != null) {
            // Sort to aid in removal of duplicate paths.
            Arrays.sort(configuredAssetFolderPaths);
            String prevPath = "#";
            for (String configuredAssetFolderPath : configuredAssetFolderPaths) {
                // Ensure that this folder is not a child folder of another
                // configured folder, since it will already be included when
                // the parent folder is traversed.
                if (StringUtils.isNotBlank(configuredAssetFolderPath) && !configuredAssetFolderPath.equals(prevPath)
                    && !StringUtils.startsWith(configuredAssetFolderPath, prevPath + "/")) {
                    Resource assetFolder = resolver.getResource(configuredAssetFolderPath);
                    if (assetFolder != null) {
                        prevPath = configuredAssetFolderPath;
                        allAssetFolders.add(assetFolder);
                    }
                }
            }
        }
        return allAssetFolders;
    }

    private void write(Page page, XMLStreamWriter stream, SlingHttpServletRequest request) throws XMLStreamException {
        if (isHidden(page)) {
            return;
        }
        stream.writeStartElement(NS, "url");
        String loc;

        if (!StringUtils.isEmpty(page.getVanityUrl())) {
            loc = externalizer.absoluteLink(request, request.getScheme(), page.getVanityUrl());
        } else if (!extensionlessUrls) {
            loc = externalizer.absoluteLink(request, request.getScheme(), String.format("%s.html", page.getPath()));
        } else {
            String urlFormat = removeTrailingSlash ? "%s" : "%s/";
            loc = externalizer.absoluteLink(request, request.getScheme(), String.format(urlFormat, page.getPath()));
        }

        loc = applyUrlRewrites(loc);

        writeElement(stream, "loc", loc);

        if (includeLastModified && page.getContentResource() != null) {
            final Resource resource = Objects.requireNonNull(page.getContentResource());
            final ValueMap valueMap = resource.getValueMap();
            final ZonedDateTime resourceLastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, ZonedDateTime.class);

            if (resourceLastModified != null) {
                writeElement(stream, "lastmod", DATE_FORMAT.format(resourceLastModified));
            }
        }

        if (includeInheritValue) {
            HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                page.getContentResource());
            writeFirstPropertyValue(stream, CHANGEFREQ, changefreqProperties, hierarchyNodeInheritanceValueMap);
            writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);
        } else {
            ValueMap properties = page.getProperties();
            writeFirstPropertyValue(stream, CHANGEFREQ, changefreqProperties, properties);
            writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);
        }

        stream.writeEndElement();
    }

    private String applyUrlRewrites(String url) {
        try {
            String path = URI.create(url).getPath();
            for (Map.Entry<String, String> rewrite : urlRewrites.entrySet()) {
                if (path.startsWith(rewrite.getKey())) {
                    return url.replaceFirst(rewrite.getKey(), rewrite.getValue());
                }
            }
            return url;
        } catch (IllegalArgumentException e) {
            LOG.error("Could not create url from {}, {}", url, e.getMessage());
            return url;
        }
    }

    private boolean isHidden(final Page page) {
        ValueMap props = page.getProperties();
        boolean hasRedirect = StringUtils.isNotBlank(props.get(PN_REDIRECT, String.class));
        Boolean hasNoIndex = props.get(PN_NOINDEX, Boolean.class);
        return !page.isValid()
            || (hasNoIndex != null && hasNoIndex)
            || hasRedirect;
    }

    private void writeAsset(Asset asset, XMLStreamWriter stream, SlingHttpServletRequest request) throws XMLStreamException {
        stream.writeStartElement(NS, "url");

        String loc = externalizer.absoluteLink(request, request.getScheme(), asset.getPath());
        writeElement(stream, "loc", loc);

        if (includeLastModified) {
            long lastModified = asset.getLastModified();
            if (lastModified > 0) {
                writeElement(stream, "lastmod", DATE_FORMAT.format(lastModified));
            }
        }

        Resource assetResource = asset.adaptTo(Resource.class);
        if (assetResource != null) {
            Resource contentResource = assetResource.getChild(JcrConstants.JCR_CONTENT);
            if (contentResource != null) {
                if (includeInheritValue) {
                    HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
                        contentResource);
                    writeFirstPropertyValue(stream, CHANGEFREQ, changefreqProperties, hierarchyNodeInheritanceValueMap);
                    writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);
                } else {
                    ValueMap properties = contentResource.getValueMap();
                    writeFirstPropertyValue(stream, CHANGEFREQ, changefreqProperties, properties);
                    writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);
                }
            }
        }
        stream.writeEndElement();
    }

    private void writeAssets(final XMLStreamWriter stream, final Resource assetFolder, final SlingHttpServletRequest request)
        throws XMLStreamException {
        for (Iterator<Resource> children = assetFolder.listChildren(); children.hasNext(); ) {
            Resource assetFolderChild = children.next();
            if (assetFolderChild.isResourceType(DamConstants.NT_DAM_ASSET)) {
                Asset asset = assetFolderChild.adaptTo(Asset.class);
                if (asset != null && asset.getMimeType() != null && damAssetTypes.contains(asset.getMimeType())) {
                    writeAsset(asset, stream, request);
                }
            } else {
                writeAssets(stream, assetFolderChild, request);
            }
        }
    }

    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
                                         final String[] propertyNames, final ValueMap properties) throws XMLStreamException {
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            if (value != null) {
                writeElement(stream, elementName, value);
                break;
            }
        }
    }

    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
                                         final String[] propertyNames, final InheritanceValueMap properties) throws XMLStreamException {
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            if (value == null) {
                value = properties.getInherited(prop, String.class);
            }
            if (value != null) {
                writeElement(stream, elementName, value);
                break;
            }
        }
    }

    private void writeElement(final XMLStreamWriter stream, final String elementName, final String text)
        throws XMLStreamException {
        stream.writeStartElement(NS, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
    }

}
