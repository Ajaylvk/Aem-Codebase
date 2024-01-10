package com.softwareag.core.models.links;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.softwareag.core.models.download.AssetModel;
import com.softwareag.core.models.download.DownloadModel;
import com.softwareag.core.models.download.DownloadModel.DownloadType;
import com.softwareag.core.models.link.LinkModel;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinksModel {
	
	@SlingObject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	private ValueMap properties;

	@ValueMapValue
	private String fileReference;

	@ValueMapValue
	private boolean inline;
   
	/* Variable for button-link-row component */
	@ValueMapValue(name = "nooflinks")
	private String linknumber;
    
	/*Variable for button-link-row component */
	@ValueMapValue(name = "buttonlinks")
	private String buttonlinknumber;

	@ChildResource
	private LinkModel link;

	private AssetModel assetModel;
	private long lastModified = -1L;

	/* List for button-link-row component */
	private List<Integer> nooflinks;

	/* List for button-link-row component */
	private List<Integer> noofbuttonlinks;

	@PostConstruct
	protected void init() {
		initAssetModel();
	}

	public String getHref() {
		if (link == null) {
			return null;
		}

		if (assetModel != null) {
			return assetModel.getUrl();
		}

		return link.getHref();
	}

	public long getLastModified() {
		if (lastModified > 0) {
			return lastModified;
		}

		lastModified = DownloadModel.getAssetLastModified(properties, assetModel);
		return lastModified;
	}

	public AssetModel getAssetModel() {
		return assetModel;
	}

	public boolean isInline() {
		return inline;
	}

	/**
	 * Checks if the CTA widget is valid.
	 *
	 * @return {@code true} if the link has the required content; otherwise
	 *         {@code false}.
	 */
	public boolean isValid() {
		return Optional.ofNullable(this.link).map(LinkModel::hasContent).orElse(false);
	}

	/**
	 * Function that returns the link configurations of a given CTA widget.
	 *
	 * @return {@link LinkModel} with the link configurations.
	 */
	public LinkModel getLink() {
		return link;
	}

	private void initAssetModel() {
		if (link == null) {
			return;
		}

		final String rawHref = link.getRawHref();
		final String utmParams = link.getUtmParameter();

		if (link.isDamContent()) {
			this.assetModel = buildAssetModel(rawHref);
		} else if (StringUtils.isNotBlank(link.getText()) && StringUtils.isBlank(rawHref)
				&& StringUtils.isNotBlank(fileReference)) {
			final AssetModel model = buildAssetModel(fileReference);
			if (model != null && model.isVideo()) {
				this.assetModel = model;
			}
		}
		if (assetModel != null && StringUtils.isNotBlank(utmParams)) {
			if (utmParams.contains("?")) {
				assetModel.setUrl(assetModel.getUrl() + "?" + utmParams);
			} else {
				assetModel.setUrl(assetModel.getUrl() + utmParams);
			}
		}
	}

	private AssetModel buildAssetModel(final String damPath) {
		if (StringUtils.isBlank(damPath)) {
			return null;
		}

		final Resource assetResource = resourceResolver.getResource(damPath);
		if (assetResource == null) {
			return null;
		}

		final AssetModel model = assetResource.adaptTo(AssetModel.class);
		final DownloadType downloadType = inline ? DownloadType.INLINE : DownloadType.DOWNLOAD;
		DownloadModel.assignDownloadUrl(model, DownloadModel.getAssetLastModified(properties, model), downloadType);
		return model;
	}

	/**
	 * Function that returns list with desired length for button-link-row component.
	 *
	 */
	public List<Integer> getNooflinks() {
		nooflinks = new ArrayList<>();
		if (linknumber != null) {
			for (int i = 0; i < Integer.parseInt(linknumber); i++)
				nooflinks.add(i);
		}
		return nooflinks;
	}

	/**
	 * Function that returns list with desired length for button-link-row component.
	 *
	 */
	public List<Integer> getNoofbuttonlinks() {
		noofbuttonlinks = new ArrayList<>();
		if (buttonlinknumber != null) {
			for (int i = 0; i < Integer.parseInt(buttonlinknumber); i++)
				noofbuttonlinks.add(i);
		}
		return noofbuttonlinks;
	}
}
