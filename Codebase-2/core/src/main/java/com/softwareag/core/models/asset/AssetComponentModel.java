package com.softwareag.core.models.asset;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.services.language.LanguageService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.*;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.day.cq.tagging.TagConstants.PN_TAGS;
import static com.softwareag.core.constants.GenericConstants.*;
import static com.softwareag.core.constants.TagConstants.*;

/* The class is used for AssetComponent */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetComponentModel {

    private static final Logger LOG = LoggerFactory.getLogger(AssetComponentModel.class);

    public static final String ASSET_RESOURCE_TYPE = "softwareag/components/content/asset";
    public static final String MEDIA_UNDER_MEDIAORPDFPARSYS = "mediaorpdfparsys/media";
    public static final String PDF_UNDER_MEDIAORPDFPARSYS = "mediaorpdfparsys/pdfviewer";

    @OSGiService
    private LanguageService languageService;

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String copyText;

    @ValueMapValue
    private String assetType;

    @ValueMapValue
    private String tagBusinessUnit;

    @ValueMapValue
    private String tagProduct;

    @ValueMapValue
    private String[] tagsCapabilities;

    private Page currentPage;
    private Locale pageLocale;

    protected String assetTypeTag = "";
    protected String businessUnitTag = "";
    protected String productTag = "";
    protected String[] capabilitiesTag = {};

    @PostConstruct
    private void init() {
        pageLocale = languageService.getLocale(resource);
        Optional.ofNullable(resourceResolver)
                .map(rr -> rr.adaptTo(PageManager.class))
                .ifPresent(pageManager -> this.currentPage = pageManager.getContainingPage(resource));

        prefillValuesFromNestedComponent();
        publishTagsToPage();
    }

    //returns value for a property name from a given ValueMap
    protected String getInnerValue(ValueMap metaValueMap, String propertyName) {
        String value = "";
        if (metaValueMap.get(propertyName) != null) {
            value = metaValueMap.get(propertyName).toString();
        }
        return value;
    }

    //collect all needed tags from inner child and keep it in separate fields
    protected void getInnerTags(ValueMap metaValueMap) {
        List<String> allInnerTags = new ArrayList<>();
        if (metaValueMap.get(PN_TAGS) != null) {
            allInnerTags = Arrays.asList((String[]) metaValueMap.get(PN_TAGS));
        }

        if (!allInnerTags.isEmpty()) {
            for (String currentTag : allInnerTags) {
                if (currentTag.contains(BUSINESS_UNIT_TAG)) {
                    businessUnitTag = currentTag;
                } else if (currentTag.contains(PRODUCT_CATEGORIES_TAG)) {
                    productTag = currentTag;
                } else if (currentTag.contains(ASSET_CAPABILITIES_TAG)) {
                    capabilitiesTag = (String[]) ArrayUtils.add(capabilitiesTag, currentTag);
                }
            }
        }
    }

    //Get some fields from child component - media OR pdf, metadata values and promote them on asset component level.
    //Prefill is done in case when on parent ( asset component ) level information is missing.
    private void prefillValuesFromNestedComponent() {
        ValueMap metaValueMap = null;
        ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
        if (map != null) {
            String fileReference;
            Resource fileReferenceRes = null;

            if (resource.getChild(MEDIA_UNDER_MEDIAORPDFPARSYS) != null) {
                if (Objects.requireNonNull(resource.getChild(MEDIA_UNDER_MEDIAORPDFPARSYS)).getValueMap().containsKey(FILE_REFERENCE)) {
                    fileReference = Objects.requireNonNull(resource.getChild(MEDIA_UNDER_MEDIAORPDFPARSYS)).getValueMap().get(FILE_REFERENCE).toString();
                    fileReferenceRes = resourceResolver.getResource(fileReference);
                    assetTypeTag = TAG_ASSET_VIDEO;
                }
            } else if (resource.getChild(PDF_UNDER_MEDIAORPDFPARSYS) != null) {
                if (Objects.requireNonNull(resource.getChild(PDF_UNDER_MEDIAORPDFPARSYS)).getValueMap().containsKey(DOCUMENT_PATH)) {
                    fileReference = Objects.requireNonNull(resource.getChild(PDF_UNDER_MEDIAORPDFPARSYS)).getValueMap().get(DOCUMENT_PATH).toString();
                    fileReferenceRes = resourceResolver.getResource(fileReference);
                    assetTypeTag = TAG_ASSET_ARTICLE;
                }
            }
            if (fileReferenceRes != null && fileReferenceRes.getChild(JCR_CONTENT_METADATA) != null) {
                metaValueMap = Objects.requireNonNull(fileReferenceRes.getChild(JCR_CONTENT_METADATA)).getValueMap();
            }
            try {
                if (metaValueMap != null) {
                    getInnerTags(metaValueMap);
                    if (!getInnerValue(metaValueMap, DC_TITLE).trim().isEmpty() && !map.containsKey("title")) {
                        map.put("title", getInnerValue(metaValueMap, DC_TITLE));
                    }
                    if (!getInnerValue(metaValueMap, DC_DESCRIPTION).trim().isEmpty() && !map.containsKey("copyText")) {
                        map.put("copyText", getInnerValue(metaValueMap, DC_DESCRIPTION));
                    }
                    if (!assetTypeTag.trim().isEmpty() && !map.containsKey("assetType")) {
                        map.put("assetType", assetTypeTag);
                    }
                    if (!businessUnitTag.trim().isEmpty() && !map.containsKey("tagBusinessUnit")) {
                        map.put("tagBusinessUnit", businessUnitTag);
                    }
                    if (!productTag.trim().isEmpty() && !map.containsKey("tagProduct")) {
                        map.put("tagProduct", productTag);
                    }
                    if (capabilitiesTag != null && capabilitiesTag.length > 0 && !map.containsKey("tagsCapabilities")) {
                        map.put("tagsCapabilities", capabilitiesTag);
                    }
                }
                resource.getResourceResolver().commit();
            } catch (PersistenceException e) {
                LOG.error("Error occured while processing tags for the current Page.", e);
            }
        }
    }

    //collect tags separated in different fields into the same String[] object.
    //Preparation step to push all of these from component to page level
    protected String[] collectAllTags() {
        String[] allTags = {};
        if (StringUtils.isNotBlank(assetType)) {
            allTags = (String[]) ArrayUtils.add(allTags, assetType);
        }
        if (StringUtils.isNotBlank(tagBusinessUnit)) {
            allTags = (String[]) ArrayUtils.add(allTags, tagBusinessUnit);
        }
        if (StringUtils.isNotBlank(tagProduct)) {
            allTags = (String[]) ArrayUtils.add(allTags, tagProduct);
        }
        if (ArrayUtils.isNotEmpty(tagsCapabilities)) {
            allTags = (String[]) ArrayUtils.addAll(allTags, tagsCapabilities);
        }

        return allTags;
    }

    //Write specific tags on page level under "tag". Needed for filtering assets / pages
    //Added unique ID on the page level in order to avoid duplicated assets when
    private void publishTagsToPage() {
        String[] allTags = collectAllTags();

        Resource currPageJcrRes = Objects.requireNonNull(resourceResolver.getResource(this.currentPage.getPath())).getChild(JCR_CONTENT);
        ModifiableValueMap pageMap = Objects.requireNonNull(currPageJcrRes).adaptTo(ModifiableValueMap.class);

        //write Tags on page level
        try {
            if (pageMap != null) {
                pageMap.put("tags", allTags);
                //put unique ID on the page level
                if (!pageMap.containsKey("id")) {
                    pageMap.put("id", UUID.randomUUID().toString());
                }
                currPageJcrRes.getResourceResolver().commit();
            }
        } catch (PersistenceException e) {
            LOG.error("Error while processing the tags for the current Page.", e);
        }
    }

    public boolean hasContent() {
        return !assetType.isEmpty() && !title.isEmpty() && !copyText.isEmpty();
    }

    /* Getters for all needed properties / fields */
    public String getTitle() {
        return title;
    }

    public String getCopyText() {
        return copyText;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getTagBusinessUnit() {
        return tagBusinessUnit;
    }

    public String getTagProduct() {
        return tagProduct;
    }

    public String[] getTagsCapabilities() {
        return tagsCapabilities;
    }

    public String getAssetTypeTag() {
        return assetTypeTag;
    }

    public String getBusinessUnitTag() {
        return businessUnitTag;
    }

    public String getProductTag() {
        return productTag;
    }

    public String[] getCapabilitiesTag() {
        return capabilitiesTag;
    }
}
