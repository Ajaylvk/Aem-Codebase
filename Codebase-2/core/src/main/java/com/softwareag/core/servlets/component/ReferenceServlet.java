package com.softwareag.core.servlets.component;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.Gson;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.GenericConstants;
import com.softwareag.core.constants.ReferenceConstants;
import com.softwareag.core.constants.TagConstants;
import com.softwareag.core.models.filtercontainer.FilterContainerModel;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.pojo.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.resource.filter.ResourceFilterStream;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static com.softwareag.core.constants.GenericConstants.JCR_CONTENT;
import static com.softwareag.core.constants.GenericConstants.JCR_PRIMARY_TYPE_CQ_PAGE;
import static com.softwareag.core.services.timezone.TimeZoneService.DELIMITER;
import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(
        service = Servlet.class,
        property = {
                SLING_SERVLET_METHODS + DELIMITER + HttpConstants.METHOD_GET,
                SLING_SERVLET_PATHS + DELIMITER + ReferenceServlet.SERVLET_PATH,
                SLING_SERVLET_EXTENSIONS + DELIMITER + ReferenceServlet.EXTENSION_JSON
        }
)
public class ReferenceServlet extends SlingAllMethodsServlet implements Comparator<Resource> {
    private static final Logger LOG = LoggerFactory.getLogger(ReferenceServlet.class);
    protected static final String SERVLET_PATH = "/bin/reference";
    protected static final String EXTENSION_JSON = "json";
    public static final String CONTAINER_REFERENCE = "container/reference";

    private String path = "";
    private String[] referenceTags = {};
    private String filterOptionOne;
    private String filterOptionTwo;
    private String filterOptionThree;
    private List<String> allTags;

    private transient Map<String, List<Filter>> filterTagsMap;
    private transient ResourceResolver resourceResolver;
    private transient LabelConfigModel labelConfigModel;
    private transient Locale pageLocale;
    private transient List<Resource> assetList;
    private transient PartnerOrderList partnerOrderList;
    private transient List<ComponentResult> resultList;

    public static final String PATH_PARTNER_BANNER = "jcr:content/partnerbanner";
    public static final String PATH_PARTNER_TABLE = "jcr:content/partnertable";
    public static final String PATH_TYPE_ASSET = "asset";
    public static final String PROP_LAST_REPLICATED = "cq:lastModified";
    public static final String PROPERTY_TAGS = "tags";
    public static final String QUERY_JCR_CONTENT_TAGS = "[jcr:content/tags]";
    public static final String QUERY_CONTAINS = "contains'";
    public static final String QUERY_AND = "and";

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        String jsonString = StringUtils.EMPTY;
        Gson gson = new Gson();

        resourceResolver = request.getResourceResolver();
        pageLocale = request.getLocale();
        assetList = new ArrayList<>();
        resultList = new ArrayList<>();
        partnerOrderList = new PartnerOrderList();
        filterTagsMap = new HashMap<>();
        allTags = new ArrayList<>();

        filterOptionOne = StringUtils.EMPTY;
        filterOptionTwo = StringUtils.EMPTY;
        filterOptionThree = StringUtils.EMPTY;

        String[] selectors = request.getRequestPathInfo().getSelectors();
        String currentPagePath = selectors[0].replace(GenericConstants.AT, GenericConstants.SLASH);
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getPage(currentPagePath);
        labelConfigModel = ConfigurationFinder.findLabelConfigComponent(currentPage).orElse(null);

        Iterator<Resource> parItems = Objects.requireNonNull(Objects.requireNonNull(resourceResolver
                                .getResource(currentPagePath))
                        .getChild(GenericConstants.JCR_CONTENT + GenericConstants.SLASH + GenericConstants.PARSYS))
                .listChildren();

        iterateAllParItems(parItems);
        try {
            if (selectors[1].equals(GenericConstants.ALL)) {
                queryForALLSelector();
            } else {
                queryForSelectors(selectors);
            }

            OverviewResult overview = new OverviewResult();
            overview.setResultList(resultList);
            overview.setFilterList(filterTagsMap);
            jsonString = gson.toJson(overview);
            LOG.info("Gson:: {}", jsonString);
        } catch (final RuntimeException e) {
            final String errorMsg = String.format("Error occured during peforming operation: %s.", e.getMessage());
            LOG.error(errorMsg, e);
            OverviewResult overview = new OverviewResult();
            overview.setError("Error while retreiving the References");
            jsonString = gson.toJson(overview);
        } finally {
            response.setContentType(GenericConstants.CONTENT_TYPE_APPLICATION_JSON);
            response.setCharacterEncoding("utf-8");
            final PrintWriter responseWriter = response.getWriter();
            responseWriter.write(jsonString);
        }
    }

    /**
     * Query for the initial load when selector is passed as "all"
     */
    private void queryForALLSelector() {
        StringBuilder stringBuilder = new StringBuilder();
        String childSelectorString = null;

        if (referenceTags != null) {
            for (String selector : referenceTags) {
                if (!selector.startsWith(GenericConstants.AT)) {
                    stringBuilder.append(QUERY_JCR_CONTENT_TAGS);
                    stringBuilder.append(QUERY_CONTAINS).append(selector).append("'");
                    stringBuilder.append(QUERY_AND);
                }
            }
            childSelectorString = stringBuilder.substring(0, stringBuilder.lastIndexOf(QUERY_AND));
        }

        if (path.contains(PATH_TYPE_ASSET)) {
            queryAssets(childSelectorString);
        } else {
            queryPartners(childSelectorString);
        }

    }

    /**
     * Query for the filters, when filter options is passed as selectors
     *
     * @param selectors filter selectors
     */
    private void queryForSelectors(String[] selectors) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String selector : selectors) {
            if (!selector.startsWith(GenericConstants.AT)) {
                stringBuilder.append(QUERY_JCR_CONTENT_TAGS);
                stringBuilder.append(QUERY_CONTAINS).append(selector.replace("@", "/")).append("'");
                stringBuilder.append(QUERY_AND);
            }
        }
        String childSelectorString = stringBuilder.substring(0, stringBuilder.lastIndexOf(QUERY_AND));

        if (path.contains(PATH_TYPE_ASSET)) {
            queryAssets(childSelectorString);
        } else {
            queryPartners(childSelectorString);
        }
    }

    /**
     * Query for the Partner Pages
     *
     * @param childSelectorString partner page child selector
     */
    private void queryPartners(String childSelectorString) {
        if (StringUtils.isEmpty(childSelectorString)) {
            childSelectorString = "[jcr:content/cq:template] == '/conf/softwareag/settings/wcm/templates/partnerpage'";
        }

        ResourceFilterStream resourceStream = Objects.requireNonNull(resourceResolver.getResource(path)).adaptTo(ResourceFilterStream.class);
        Objects.requireNonNull(resourceStream).setBranchSelector(JCR_PRIMARY_TYPE_CQ_PAGE)
                .setChildSelector(childSelectorString)
                .stream()
                .forEach(resource -> {
                    ValueMap resProp = Objects.requireNonNull(resource.getChild(JCR_CONTENT)).getValueMap();
                    if (resProp.containsKey(PROPERTY_TAGS)) {
                        String[] tags = (String[]) resProp.get(PROPERTY_TAGS);
                        populatePartnerOrderLists(tags, resource);
                        allTags.addAll(Arrays.asList(tags));
                    }
                });

        resolveFilterTags(allTags);
        resolvePartnerResults();
    }

    /**
     * Query for the Asset Pages
     *
     * @param childSelectorString asset page child selector
     */
    private void queryAssets(String childSelectorString) {
        if (StringUtils.isEmpty(childSelectorString)) {
            childSelectorString = "[jcr:content/cq:template] == '/conf/softwareag/settings/wcm/templates/assetpage'";
        }

        ResourceFilterStream resourceStream = Objects.requireNonNull(resourceResolver.getResource(path)).adaptTo(ResourceFilterStream.class);
        Objects.requireNonNull(resourceStream).setBranchSelector(JCR_PRIMARY_TYPE_CQ_PAGE)
                .setChildSelector(childSelectorString)
                .stream()
                .sorted((r1, r2) -> compare(r1, r2))
                .forEach(resource -> {
                    ValueMap resProp = Objects.requireNonNull(resource.getChild(JCR_CONTENT)).getValueMap();
                    if (resProp.containsKey(PROPERTY_TAGS)) {
                        String[] tags = (String[]) resProp.get(PROPERTY_TAGS);
                        if (tags.length > 0) {
                            assetList.add(resource);
                            allTags.addAll(Arrays.asList(tags));
                        }
                    }
                });

        resolveFilterTags(allTags);
        resolveAssetResults();
    }

    /**
     * Override compare method to sort asset pages using last replicated property
     *
     * @param r1 resource 1
     * @param r2 resource 2
     * @return sorting numeric
     */
    @Override
    public int compare(Resource r1, Resource r2) {
        if (!r2.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED) &&
                r1.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED)) {
            return 1;
        } else if (r2.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED) &&
                !r1.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED)) {
            return -1;
        } else if (r2.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED) &&
                r1.getChild(JCR_CONTENT).getValueMap().containsKey(PROP_LAST_REPLICATED)) {
            return r2.getChild(JCR_CONTENT).getValueMap().get(PROP_LAST_REPLICATED, Calendar.class).
                    compareTo(r1.getChild(JCR_CONTENT).getValueMap().get(PROP_LAST_REPLICATED, Calendar.class));
        }
        return 0;
    }

    /**
     * Resolve the filter tags and populate filter maps
     *
     * @param allTags List of all tags from the query result pages
     */
    private void resolveFilterTags(List<String> allTags) {
        if (!allTags.isEmpty()) {
            Set<String> tagSet1 = new TreeSet<>();
            Set<String> tagSet2 = new TreeSet<>();
            Set<String> tagSet3 = new TreeSet<>();

            for (String tag : allTags) {
                if (!filterOptionOne.isEmpty() && tag.contains(filterOptionOne)) {
                    tagSet1.add(tag);
                }
                if (!filterOptionTwo.isEmpty() && tag.contains(filterOptionTwo)) {
                    tagSet2.add(tag);
                }
                if (!filterOptionThree.isEmpty() && tag.contains(filterOptionThree)) {
                    tagSet3.add(tag);
                }
            }

            tagSet1.remove(TagConstants.TAG_SELECTED_PARTNER);
            tagSet2.remove(TagConstants.TAG_SELECTED_PARTNER);
            tagSet3.remove(TagConstants.TAG_SELECTED_PARTNER);

            filterTagsMap.put("filterOptionOneList", populateFilterMap(tagSet1));
            filterTagsMap.put("filterOptionTwoList", populateFilterMap(tagSet2));
            filterTagsMap.put("filterOptionThreeList", populateFilterMap(tagSet3));
        }
    }

    /**
     * Populate the partner list with the pages sorted in the required order : i.e.
     * Elite Global, Premier Global, Selected Global, Elite, Premier, Selected - this order needs to be followed as per the business requirement
     *
     * @param cqTags tags array
     * @param r      Resource
     */
    private void populatePartnerOrderLists(String[] cqTags, Resource r) {
        ValueMap bannerProp = Objects.requireNonNull(r.getChild(PATH_PARTNER_BANNER)).getValueMap();
        if (bannerProp.containsKey(ReferenceConstants.PROP_GLOBAL_PARTNER) && bannerProp.get(ReferenceConstants.PROP_GLOBAL_PARTNER, Boolean.class) == true) {
            if (ArrayUtils.contains(cqTags, TagConstants.TAG_ELITE_PARTNER)) {
                partnerOrderList.setEliteGlobalList(r);
            } else if (ArrayUtils.contains(cqTags, TagConstants.TAG_PREMIER_PARTNER)) {
                partnerOrderList.setPremierGlobalList(r);
            } else if (ArrayUtils.contains(cqTags, TagConstants.TAG_SELECTED_PARTNER)) {
                partnerOrderList.setSelectedGlobalList(r);
            }
        } else {
            if (ArrayUtils.contains(cqTags, TagConstants.TAG_ELITE_PARTNER)) {
                partnerOrderList.setEliteList(r);
            } else if (ArrayUtils.contains(cqTags, TagConstants.TAG_PREMIER_PARTNER)) {
                partnerOrderList.setPremierList(r);
            } else if (ArrayUtils.contains(cqTags, TagConstants.TAG_SELECTED_PARTNER)) {
                partnerOrderList.setSelectedList(r);
            }
        }
    }

    /**
     * Iterate all the parsys items of the current page - to fetch filter options from filter container component,
     * reference tags for pre-filtering from the reference component,
     * path of the asset/partner parent page
     *
     * @param parItems par items of current page
     */
    private void iterateAllParItems(Iterator<Resource> parItems) {
        while (parItems.hasNext()) {
            Resource filterContainerRes = parItems.next();
            if (filterContainerRes.getName().contains(GenericConstants.FILTER_CONTAINER)) {
                setFilterOptions(filterContainerRes);
                if (filterContainerRes.getChild(CONTAINER_REFERENCE) != null) {
                    path = Objects.requireNonNull(filterContainerRes.getChild(CONTAINER_REFERENCE)).getValueMap().get(GenericConstants.PATH, String.class);
                    referenceTags = Objects.requireNonNull(filterContainerRes.getChild(CONTAINER_REFERENCE)).getValueMap().get(PROPERTY_TAGS, String[].class);
                } else {
                    Resource referenceRes = parItems.next();
                    if (referenceRes.getName().contains(GenericConstants.REFERENCE) && referenceRes.getValueMap()
                            .get(GenericConstants.PATH, String.class)
                            .contains(GenericConstants.PARTNER)) {
                        path = referenceRes.getValueMap().get(GenericConstants.PATH, String.class);
                    }
                }
            }
        }
    }

    /**
     * set the three filter options from the filter container component properties
     *
     * @param filterContainerRes filter container component resource
     */
    private void setFilterOptions(Resource filterContainerRes) {
        ValueMap map = filterContainerRes.getValueMap();
        if (map.get(FilterContainerModel.FILTER_OPTION_ONE) != null) {
            filterOptionOne = map.get(FilterContainerModel.FILTER_OPTION_ONE, String.class);
        }
        if (map.get(FilterContainerModel.FILTER_OPTION_TWO) != null) {
            filterOptionTwo = map.get(FilterContainerModel.FILTER_OPTION_TWO, String.class);
        }
        if (map.get(FilterContainerModel.FILTER_OPTION_THREE) != null) {
            filterOptionThree = map.get(FilterContainerModel.FILTER_OPTION_THREE, String.class);
        }
    }

    /**
     * Populate the filter Map as title and values of the filters to be shown in the dropdown of the filter container.
     *
     * @param tagSet set of the tags
     * @return List of the Filter objects
     */
    private List<Filter> populateFilterMap(Set<String> tagSet) {
        List<Filter> optionList = new ArrayList<>();
        final TagManager tagManager = Objects.requireNonNull(resourceResolver).adaptTo(TagManager.class);
        for (String tagStr : tagSet) {
            Tag tag = Objects.requireNonNull(tagManager).resolve(tagStr);
            if (tag != null) {
                Filter filter = new Filter();
                filter.setTitle(tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle());
                filter.setValue(tag.getName());

                if (tagStr.equals(TagConstants.TAG_PARTNER_COUNTRIES_GLOBAL)) {
                    optionList.add(0, filter);
                } else {
                    optionList.add(filter);
                }
            }
        }
        return optionList;
    }

    private void resolvePartnerResults() {
        sortList(partnerOrderList.getEliteGlobalList());
        sortList(partnerOrderList.getPremierGlobalList());
        sortList(partnerOrderList.getSelectedGlobalList());
        sortList(partnerOrderList.getEliteList());
        sortList(partnerOrderList.getPremierList());
        sortList(partnerOrderList.getSelectedList());

        for (Resource r : partnerOrderList.getEliteGlobalList()) {
            resultList.add(addPartnerProperties(r));
        }
        for (Resource r : partnerOrderList.getPremierGlobalList()) {
            resultList.add(addPartnerProperties(r));
        }
        for (Resource r : partnerOrderList.getSelectedGlobalList()) {
            resultList.add(addPartnerProperties(r));
        }
        for (Resource r : partnerOrderList.getEliteList()) {
            resultList.add(addPartnerProperties(r));
        }
        for (Resource r : partnerOrderList.getPremierList()) {
            resultList.add(addPartnerProperties(r));
        }
        for (Resource r : partnerOrderList.getSelectedList()) {
            resultList.add(addPartnerProperties(r));
        }
    }

    /**
     * resolve asset page results list and populate the json list with the required details only
     */
    private void resolveAssetResults() {
        for (Resource r : assetList) {
            AssetResult assetResult = new AssetResult();
            ValueMap resProp = Objects.requireNonNull(r.getChild("jcr:content")).getValueMap();
            ValueMap assetCompProp = Objects.requireNonNull(r.getChild("jcr:content/assetcomponent")).getValueMap();
            if (assetCompProp.containsKey(GenericConstants.TITLE)) {
                assetResult.setTitle(assetCompProp.get(GenericConstants.TITLE, String.class));
            }
            if (assetCompProp.containsKey(GenericConstants.COPY_TEXT)) {
                assetResult.setCopyText(assetCompProp.get(GenericConstants.COPY_TEXT, String.class).replaceAll(ReferenceConstants.HTML_REGEX, ""));
            }
            if (assetCompProp.containsKey(GenericConstants.FILE_REFERENCE)) {
                assetResult.setFileReference(assetCompProp.get(GenericConstants.FILE_REFERENCE, String.class));
            }

            setAssetTagRelatedProps(assetResult, resProp);
            assetResult.setAssetResultLinkProps(r);

            resultList.add(assetResult);
        }
    }

    /**
     * Helping method to populate asset json with the required tag details
     *
     * @param assetResult result object
     * @param resProp     value map
     */
    private void setAssetTagRelatedProps(AssetResult assetResult, ValueMap resProp) {
        String[] tags = (String[]) resProp.get(PROPERTY_TAGS);
        for (String tag : tags) {
            if (tag.startsWith(TagConstants.ASSET_TYPE_TAG)) {
                if (tag.contains(TagConstants.TAG_ASSET_VIDEO)) {
                    assetResult.setAssetTypeDisplay(getTagTitle(tag));
                    if (labelConfigModel != null) {
                        assetResult.setCtaLabel(labelConfigModel.getLabelWatchVideo());
                    } else {
                        assetResult.setCtaLabel("Watch video");
                    }
                } else {
                    assetResult.setAssetTypeDisplay(getTagTitle(tag));
                    if (labelConfigModel != null) {
                        assetResult.setCtaLabel(labelConfigModel.getLabelReadArticle());
                    } else {
                        assetResult.setCtaLabel("Read more");
                    }
                }
                break;
            }
        }
    }

    /**
     * Helping method to fetch the localized title of the tag
     *
     * @param tagStr tag
     * @return localized title of the tag
     */
    public String getTagTitle(String tagStr) {
        final TagManager tagManager = Objects.requireNonNull(resourceResolver).adaptTo(TagManager.class);
        Tag tag = Objects.requireNonNull(tagManager).resolve(tagStr);
        if (tag != null) {
            return tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle();
        }
        return null;
    }

    /**
     * sort the partner list in alphabetical order of partner name
     *
     * @param list partner list
     */
    private void sortList(List<Resource> list) {
        list.sort(Comparator.comparing(
                one -> Objects.requireNonNull(one.getChild(PATH_PARTNER_BANNER)).getValueMap().get(ReferenceConstants.PROP_PARTNER_NAME, String.class).toLowerCase()));
    }

    /**
     * Populate the partner results json with the required details
     *
     * @param r Partner resource
     * @return Partner Result Object
     */
    private PartnerResult addPartnerProperties(Resource r) {
        PartnerResult partnerResult = new PartnerResult();
        ValueMap bannerProp = Objects.requireNonNull(r.getChild(PATH_PARTNER_BANNER)).getValueMap();
        ValueMap tableProp = Objects.requireNonNull(r.getChild(PATH_PARTNER_TABLE)).getValueMap();

        if (bannerProp.containsKey(ReferenceConstants.PROP_PARTNER_NAME)) {
            partnerResult.setTitle(bannerProp.get(ReferenceConstants.PROP_PARTNER_NAME, String.class));
        }
        if (bannerProp.containsKey(ReferenceConstants.PROP_PARTNER_LEVEL_TAG)) {
            partnerResult.setPartnerLevelTag(bannerProp.get(ReferenceConstants.PROP_PARTNER_LEVEL_TAG, String.class));
        }
        if (bannerProp.containsKey(ReferenceConstants.PROP_PARTNER_LEVEL_TAG_NAME)) {
            partnerResult.setPartnerLevelTagName(bannerProp.get(ReferenceConstants.PROP_PARTNER_LEVEL_TAG_NAME, String.class));
        }
        if (bannerProp.containsKey(ReferenceConstants.PROP_ICON)) {
            partnerResult.setFileReference(bannerProp.get(ReferenceConstants.PROP_ICON, String.class));
        }
        if (bannerProp.containsKey(ReferenceConstants.PROP_ICON_ALT_TEXT)) {
            partnerResult.setAltText(bannerProp.get(ReferenceConstants.PROP_ICON_ALT_TEXT, String.class));
        }
        if (bannerProp.containsKey(ReferenceConstants.PROP_GLOBAL_PARTNER)) {
            partnerResult.setGlobalPartner(bannerProp.get(ReferenceConstants.PROP_GLOBAL_PARTNER, Boolean.class));
        }

        partnerResult.setIndustryTags(getIndustryTags(tableProp));
        partnerResult.setPartnerPageURL(r);

        return partnerResult;
    }

    /**
     * Helping method to get the industry tags as per business logic.
     *
     * @param tableProp value map
     * @return industry tag string
     */
    private String getIndustryTags(ValueMap tableProp) {
        String[] tags = tableProp.get(TagConstants.TAG_NAME_INDUSTRY_TAGS, String[].class);
        if (tags != null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (labelConfigModel != null) {
                stringBuilder.append(labelConfigModel.getLabelIndustry()).append(": ");
            } else {
                stringBuilder.append("Industry: ");
            }

            if (tags.length > 3) {
                return labelConfigModel != null ? labelConfigModel.getLabelIndustry() + ": " + labelConfigModel.getLabelCrossIndustry()
                        : "Industry: Cross Industry";
            } else {
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                for (String value : tags) {
                    stringBuilder.append(Objects.requireNonNull(tagManager).resolve(value).getTitle());
                    stringBuilder.append(", ");
                }
                return stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
            }
        }
        return "-";
    }

}
