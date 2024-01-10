package com.softwareag.core.models.asset;

import static com.softwareag.core.constants.GenericConstants.COPY_TEXT;
import static com.softwareag.core.constants.GenericConstants.JCR_CONTENT;
import static com.softwareag.core.constants.GenericConstants.JCR_PRIMARY_TYPE_CQ_PAGE;
import static com.softwareag.core.constants.GenericConstants.TITLE;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.resource.filter.ResourceFilterStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.softwareag.core.configuration.ConfigurationFinder;
import com.softwareag.core.constants.GenericConstants;
import com.softwareag.core.constants.TagConstants;
import com.softwareag.core.models.label.LabelConfigModel;
import com.softwareag.core.pojo.AssetResult;
import com.softwareag.core.services.language.LanguageService;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RelatedAsset {

	private static final Logger LOG = LoggerFactory.getLogger(RelatedAsset.class);

	public static final int TOTAL_RELATED_ITEMS = 3;

	@OSGiService
	private LanguageService languageService;

	@Self
	private Resource resource;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ValueMapValue
	private String title;

	@ValueMapValue
	private String path;

	private Page currentPage;
	private Locale pageLocale;
	private LabelConfigModel labelConfigModel;

	String[] currentPageTags = {};
	String currentPageId = "";
	String[] output = {};
	String[] currentFilterCriteria = {};
	List<String[]> allCombinations = new ArrayList<>();
	Map<String, AssetResult> relatedAssets = new IdentityHashMap<>();
	List<AssetResult> assets = new ArrayList<>();

	@PostConstruct
	private void init() {
		pageLocale = languageService.getLocale(resource);
		Optional.ofNullable(resourceResolver).map(rr -> rr.adaptTo(PageManager.class))
				.ifPresent(pageManager -> this.currentPage = pageManager.getContainingPage(resource));

		labelConfigModel = ConfigurationFinder.findLabelConfigComponent(this.currentPage).orElse(null);

		getCurrentPageTags();

		if (StringUtils.isNotBlank(path)) {
			// find all possible combinations between all search criterias
			allCombinations.add(currentPageTags);
			for (int i = currentPageTags.length - 1; i > 0; i--) {
				findCombinations(currentPageTags, output, 0, currentPageTags.length, i);
			}

			// do filtering for all possible combinations until we have 3 results into the
			// list
			ResourceFilterStream resourceStream = Objects.requireNonNull(resourceResolver.getResource(path))
					.adaptTo(ResourceFilterStream.class);
			if (resourceStream != null) {
				for (int i = 0; i < allCombinations.size(); i++) {
					currentFilterCriteria = allCombinations.get(i);
					doFilter(currentFilterCriteria, resourceStream,
							TOTAL_RELATED_ITEMS - (relatedAssets != null ? relatedAssets.size() : 0));
					if (relatedAssets.size() == TOTAL_RELATED_ITEMS) {
						break;
					}
				}
			}
		}
		initAssets();
	}

	private void initAssets() {
		if (!relatedAssets.isEmpty()) {
			assets.addAll(relatedAssets.values());
		}
	}

	// do filtering by criterias, selectors are list of tags which are needed to
	// match.
	private void doFilter(String[] selectors, ResourceFilterStream resourceStream, int limiter) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String selector : selectors) {
			if (!selector.startsWith(GenericConstants.AT)) {
				stringBuilder.append("[jcr:content/tags]");
				stringBuilder.append("contains'").append(selector.replace("@", "/")).append("'");
				stringBuilder.append("and");
			}
		}
		// exclude current page + already added assets / pages if they exists, unique
		// IDs
		if (!currentPageId.startsWith(GenericConstants.AT)) {
			stringBuilder.append("[jcr:content/id]");
			stringBuilder.append("contains not'").append(currentPageId.replace("@", "/")).append("'");
			stringBuilder.append("and");
		}
		if (relatedAssets != null) {
			for (String currentId : relatedAssets.keySet()) {
				if (!currentId.startsWith(GenericConstants.AT)) {
					stringBuilder.append("[jcr:content/id]");
					stringBuilder.append("contains not'").append(currentId.replace("@", "/")).append("'");
					stringBuilder.append("and");
				}
			}
		}
		String childSelectorString = stringBuilder.substring(0, stringBuilder.lastIndexOf("and"));
		Objects.requireNonNull(resourceStream).setBranchSelector(JCR_PRIMARY_TYPE_CQ_PAGE)
				.setChildSelector(childSelectorString).stream().limit(limiter).forEach(childResource -> {
					if (childResource.hasChildren() && childResource.getChild(JCR_CONTENT) != null) {
						ValueMap resProp = childResource.getChild(JCR_CONTENT).getValueMap();
						if (resProp.containsKey("id")) {
							String currentID = resProp.get("id").toString();
							try {
								resolveAssetResults(childResource, currentID);
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				});
	}

	// covert resource r to AssetResult ValueMap where the key is ID of the asset
	// and value as AssetResult object
	private void resolveAssetResults(Resource r, String currentID) throws UnsupportedEncodingException {
		AssetResult assetResult = new AssetResult();
		ValueMap resProp = Objects.requireNonNull(r.getChild("jcr:content")).getValueMap();
		ValueMap assetCompProp = Objects.requireNonNull(r.getChild(JCR_CONTENT + "/assetcomponent")).getValueMap();
		if (assetCompProp.containsKey(TITLE)) {
			String title = assetCompProp.get(TITLE).toString();
			title = title.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			title = title.replaceAll("\\+", "%2B");
			String encodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);
			assetResult.setTitle(encodedTitle);
		}
		if (assetCompProp.containsKey(GenericConstants.COPY_TEXT)) {
			String copyText = assetCompProp.get(COPY_TEXT).toString();
			copyText = copyText.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			copyText = copyText.replaceAll("\\+", "%2B");
			String encodedText = URLDecoder.decode(copyText, StandardCharsets.UTF_8);
			assetResult.setCopyText(encodedText);
		}
		if (assetCompProp.containsKey(GenericConstants.FILE_REFERENCE)) {
			assetResult.setFileReference(assetCompProp.get(GenericConstants.FILE_REFERENCE).toString());
		}

		String[] tags = (String[]) resProp.get(GenericConstants.TAGS);
		for (String tag : tags) {
			if (tag.startsWith(TagConstants.ASSET_TYPE_TAG)) {
				if (tag.startsWith(TagConstants.TAG_ASSET_VIDEO)) {
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
		assetResult.setAssetResultLinkProps(r);

		relatedAssets.put(currentID, assetResult);
	}

	private String getTagTitle(String tagStr) {
		final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if (tagManager != null) {
			Tag tag = tagManager.resolve(tagStr);
			if (tag != null) {
				return tag.getLocalizedTitle(pageLocale) != null ? tag.getLocalizedTitle(pageLocale) : tag.getTitle();
			}
		}
		return null;
	}

	// find all combinations within array
	private void findCombinations(String[] array, String[] out, int i, int n, int k) {
		if (k > n) { // invalid input
			return;
		}
		if (k == 0) {
			allCombinations.add(out);
			return;
		}
		// start from the next index till the last index
		for (int j = i; j < n; j++) {
			// add current element `A[j]` to the solution and recur for next index,`j+1`
			// with one less element `k-1`
			findCombinations(array, (String[]) ArrayUtils.add(out, array[j]), j + 1, n, k - 1);
			// handle duplicates
			while (j < n - 1 && array[j].equals(array[j + 1])) {
				j++;
			}
		}
	}

	// get current page tags and ID
	private void getCurrentPageTags() {
		if (resourceResolver.getResource(this.currentPage.getPath()).hasChildren()
				&& resourceResolver.getResource(this.currentPage.getPath()).getChild(JCR_CONTENT) != null) {
			Resource currPageJcrRes = resourceResolver.getResource(this.currentPage.getPath()).getChild(JCR_CONTENT);
			if (currPageJcrRes != null) {
				ValueMap pageMap = currPageJcrRes.adaptTo(ValueMap.class);
				if (pageMap != null) {
					if (pageMap.get(GenericConstants.TAGS) != null) {
						currentPageTags = (String[]) pageMap.get(GenericConstants.TAGS);
					}
					if (pageMap.get(GenericConstants.ID) != null) {
						currentPageId = pageMap.get(GenericConstants.ID).toString();
					}
				}
			}
		}
	}

	public boolean hasContent() {
		return StringUtils.isNotBlank(title) && StringUtils.isNotBlank(path);
	}

	// getters for needed fields
	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public List<AssetResult> getAssets() {
		return Collections.unmodifiableList(assets);
	}

	public boolean hasAssets() {
		return !assets.isEmpty();
	}

	public String[] getPageTags() {
		return currentPageTags;
	}

	public String getPageId() {
		return currentPageId;
	}

	public List<String[]> getAllCombinations() {
		return allCombinations;
	}
}
