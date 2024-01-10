package com.softwareag.core.models.cta;

import com.softwareag.core.models.download.AssetModel;
import com.softwareag.core.models.download.DownloadModel;
import com.softwareag.core.models.download.DownloadModel.DownloadType;
import com.softwareag.core.models.link.LinkModel;

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

import javax.annotation.PostConstruct;
import java.util.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CtaModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private ValueMap properties;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private boolean inline;

    @ValueMapValue
    private String design;

    @ChildResource
    private LinkModel link;

    private AssetModel assetModel;
    private long lastModified = -1L;

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
     * @return {@code true} if the link has the required content; otherwise {@code false}.
     */
    public boolean isValid() {
        return Optional.ofNullable(this.link)
            .map(LinkModel::hasContent)
            .orElse(false);
    }

    /**
     * Gets the configured design.
     *
     * @return {@link String} with the configured design.
     */
    public String getDesign() {
        return design;
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
        } else if (StringUtils.isNotBlank(link.getText()) && StringUtils.isBlank(rawHref) && StringUtils.isNotBlank(fileReference)) {
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

}
