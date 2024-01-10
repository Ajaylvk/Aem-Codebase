package com.softwareag.core.models.download;

import com.day.cq.commons.jcr.JcrConstants;
import com.softwareag.core.servlets.download.DownloadServlet;
import com.softwareag.core.util.AttributeUtil;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;


@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DownloadModel {

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private ValueMap properties;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private boolean titleFromAsset;

    @ValueMapValue(name = JcrConstants.JCR_TITLE)
    private String title;

    @ValueMapValue
    private boolean descriptionFromAsset;

    @ValueMapValue(name = JcrConstants.JCR_DESCRIPTION)
    private String description;

    @ValueMapValue
    private boolean displayFileSize;

    @ValueMapValue
    private boolean displayFileFormat;

    @ValueMapValue
    private boolean displayFilename;

    @ValueMapValue
    private String actionText;

    @ValueMapValue
    private boolean inline;

    @ValueMapValue
    private String anchorName;

    private AssetModel assetModel;
    private long lastModified = -1L;

    @PostConstruct
    protected void init() {
        initAssetModel();
    }

    private void initAssetModel() {
        if (StringUtils.isNotBlank(fileReference)) {
            final Resource assetResource = resourceResolver.getResource(fileReference);
            if (assetResource != null) {
                this.assetModel = assetResource.adaptTo(AssetModel.class);

                final DownloadType downloadType = inline ? DownloadType.INLINE : DownloadType.DOWNLOAD;
                assignDownloadUrl(this.assetModel, getLastModified(), downloadType);
            }
        }
    }

    public boolean isDisplayFileSize() {
        return displayFileSize;
    }

    public boolean isDisplayFileFormat() {
        return displayFileFormat;
    }

    public boolean isDisplayFilename() {
        return displayFilename;
    }

    public String getTitle() {
        if (titleFromAsset) {
            return assetModel.getTitle();
        }
        return title;
    }

    public String getDataAttribValue() {
        return AttributeUtil.removeEmptyLines(getTitle());
    }

    public String getDescription() {
        if (descriptionFromAsset) {
            return assetModel.getDescription();
        }
        return description;
    }

    public String getActionText() {
        return actionText;
    }

    public String getFilename() {
        if (assetModel != null) {
            return assetModel.getFilename();
        }
        return null;
    }

    public String getFormat() {
        if (assetModel != null) {
            return assetModel.getFormat();
        }
        return null;
    }

    public String getSize() {
        if (assetModel != null) {
            return assetModel.getSize();
        }
        return null;
    }

    public String getUrl() {
        if (assetModel != null) {
            return assetModel.getUrl();
        }
        return null;
    }

    public boolean isInline() {
        return inline;
    }

    public long getLastModified() {
        if (lastModified < 0) {
            lastModified = getAssetLastModified(properties, assetModel);
        }

        return lastModified;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public boolean hasContent() {
        return assetModel != null;
    }

    public static long getAssetLastModified(final ValueMap properties, final AssetModel assetModel) {
        long lastModified = 0L;
        if (MapUtils.isNotEmpty(properties)) {
            final ZonedDateTime resourceLastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, ZonedDateTime.class);
            if (resourceLastModified != null) {
                lastModified = resourceLastModified.toInstant().toEpochMilli();
            }
        }

        if (assetModel != null && lastModified < assetModel.getLastModified()) {
            lastModified = assetModel.getLastModified();
        }

        return lastModified;
    }

    public static String assignDownloadUrl(final AssetModel assetModel, final long lastModified, final DownloadType downloadType) {
        if (assetModel == null) {
            return null;
        }

        if (StringUtils.isNotBlank(assetModel.getUrl())) {
            return assetModel.getUrl();
        }

        final StringBuilder sb = new StringBuilder();
        sb.append(assetModel.getPath());

        if (downloadType == DownloadType.INLINE) {
            sb.append('.').append(DownloadServlet.SELECTOR).append(".");
            sb.append(DownloadServlet.INLINE_SELECTOR).append(".");

            if (lastModified > 0) {
                sb.append(lastModified).append(".");
            }

            sb.append(assetModel.getExtension());
        }

        assetModel.setUrl(sb.toString());
        return assetModel.getUrl();
    }

    public enum DownloadType {
        DOWNLOAD, INLINE
    }

}
