package com.softwareag.core.transformers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.softwareag.core.constants.GenericConstants;
import com.softwareag.core.util.PageUtil;
import org.apache.cocoon.xml.sax.AbstractSAXPipe;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;

/**
 * This link transformer factory is used to rewrite master links to live copy links
 */
@Component(
    service = TransformerFactory.class,
    property = {
        "pipeline.type=masterlinkrewriter"
    }
)
public class MasterLinkTransformerFactory implements TransformerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterLinkTransformerFactory.class);

    @Override
    public Transformer createTransformer() {

        return new MasterLinkTransformer();
    }

    private class MasterLinkTransformer extends AbstractSAXPipe implements Transformer {

        private Page currentPage;

        private String blueprintPath;

        private String livecopyPath;

        private String languageRoot;

        private boolean isLiveCopy;

        private boolean isHandleBarsTemplate;

        private ResourceResolver resourceResolver;

        private SlingHttpServletRequest request;

        @Override
        public void init(ProcessingContext processingContext,
                         ProcessingComponentConfiguration processingComponentConfiguration) throws IOException {

            request = processingContext.getRequest();
            resourceResolver = processingContext.getRequest().getResourceResolver();
            currentPage = PageUtil.resolveCurrentPage(processingContext.getRequest());
            if (currentPage == null) {
                return;
            }
            setBlueprintAndLiveCopyPaths(resourceResolver);

        }

        /**
         * @param resourceResolver
         *     This method checks whether the current page is part of a live copy or not.
         *     If live copy is found then corresponding blueprint and live copy paths are set
         */
        private void setBlueprintAndLiveCopyPaths(ResourceResolver resourceResolver) {
            LiveRelationshipManager liveRelationshipManager = resourceResolver.adaptTo(LiveRelationshipManager.class);
            if (liveRelationshipManager == null) {
                return;
            }
            Resource pageResource = currentPage.adaptTo(Resource.class);
            try {
                LiveRelationship liveRelationship = liveRelationshipManager.getLiveRelationship(pageResource, false);
                if (liveRelationship != null && liveRelationship.getLiveCopy() != null) {
					isLiveCopy=false;
                    blueprintPath = liveRelationship.getLiveCopy().getBlueprintPath();
                    livecopyPath = liveRelationship.getLiveCopy().getPath();
                    if(hasFiveSlash(livecopyPath)) {
					isLiveCopy = true;
                    languageRoot = livecopyPath.substring(0, StringUtils.ordinalIndexOf(livecopyPath, GenericConstants.SLASH, 5));
                    }
                }
            } catch (WCMException | StringIndexOutOfBoundsException e) {
                LOGGER.error("An error ocurred while getting live copy", e);
                isLiveCopy = false;
            }

        }

        /**
         * @param uri
         *     the Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed
         * @param localName
         *     the local name (without prefix), or the empty string if Namespace processing is not being performed
         * @param qName
         *     the qualified name (with prefix), or the empty string if qualified names are not available
         * @param atts
         *     the attributes attached to the element. If there are no attributes, it shall be an empty Attributes object. The value of this object after startElement returns is undefined
         * @throws SAXException
         *     any SAX exception, possibly wrapping another exception
         *     This method checks for anchor tags and replaces master link with live copy link.
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
            final AttributesImpl attributes = new AttributesImpl(atts);
            checkForHandlebarsTemplate(localName, attributes);
            if (GenericConstants.ANCHOR_TAG.equalsIgnoreCase(localName) && currentPage != null && isLiveCopy) {
                String href = attributes.getValue(GenericConstants.HREF_ATTRIBUTE);
                if (href != null && href.startsWith("/content/softwareag") && href.contains("master")) {
                    if (href.startsWith(blueprintPath)) {
                        replaceMasterLink(attributes, href);
                    } else {
                        replaceMasterLink(attributes, getLocalizedPath(href));
                    }
                }
            }
            if (GenericConstants.LINK.equalsIgnoreCase(localName) && currentPage != null && isLiveCopy) {
                String href = attributes.getValue(GenericConstants.HREF_ATTRIBUTE);
                if (href != null && href.contains("master")) {
                    if (href.startsWith(blueprintPath)) {
                        replaceMasterLink(attributes, href);
                    } else {
                        replaceMasterLink(attributes, getLocalizedPath(href));
                    }
                }
            }

            contentHandler.startElement(uri, localName, qName, attributes);
        }

        /**
         * Get localized master page based on current site language root
         *
         * @param currentPath
         *     current page path
         * @return localized path
         */
        private String getLocalizedPath(String currentPath) {
            String relativePath = currentPath.substring(StringUtils.ordinalIndexOf(blueprintPath, GenericConstants.SLASH, 5));
            String localizedPath = languageRoot + relativePath;
            if (resourceResolver.getResource(FilenameUtils.removeExtension(localizedPath)) != null) {
                return localizedPath;
            }
            return currentPath;
        }

        /**
         * @param localName
         *     local Name
         * @param attributes
         *     attributes
         *     This method checks whether there is a handlebars template in the page.
         */
        private void checkForHandlebarsTemplate(String localName, AttributesImpl attributes) {
            if (GenericConstants.SCRIPT_TAG.equalsIgnoreCase(localName)) {
                String type = attributes.getValue(GenericConstants.SCRIPT_TYPE);
                isHandleBarsTemplate = GenericConstants.SCRIPT_TYPE_HANDLEBARS_TEMPLATE.equalsIgnoreCase(type);
            } else {
                isHandleBarsTemplate = false;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            isHandleBarsTemplate = false;
            contentHandler.endElement(uri, localName, qName);
        }

        private void replaceMasterLink(AttributesImpl attributes, String href) {
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); attributeIndex++) {
                if (GenericConstants.HREF_ATTRIBUTE.equalsIgnoreCase(attributes.getQName(attributeIndex))) {
                    href = href.replace(blueprintPath, livecopyPath);
                    attributes.setValue(attributeIndex, href);
                    break;
                }
            }
        }

        /**
         * @param ch
         *     the characters from the XML document
         * @param start
         *     the start position in the array
         * @param length
         *     the number of characters to read from the array
         * @throws SAXException
         *     any SAX exception, possibly wrapping another exception
         *     This method replaces any master path inside handlebars template code with live copy path
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isHandleBarsTemplate && isLiveCopy) {
                String handleBarsCode = String.valueOf(ch, start, length);
                handleBarsCode = handleBarsCode.replace(blueprintPath, livecopyPath);
                contentHandler.characters(handleBarsCode.toCharArray(), 0, handleBarsCode.length());
            } else {
                contentHandler.characters(ch, start, length);
            }
        }

        @Override
        public void dispose() {
            // No operation required.
        }

       public boolean hasFiveSlash(String path) {
		   int occurance=0;
           for(int i =0;i<path.length();i++){
               if(path.charAt(i)=='/'){
                   occurance++;
               }
               if(occurance>=5){
                   return true;
               }
           }
		   return false;
       }
    }

}
