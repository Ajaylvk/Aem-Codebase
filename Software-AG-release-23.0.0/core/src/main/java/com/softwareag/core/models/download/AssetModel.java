package com.softwareag.core.models.download;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AssetModel {

    private static final Set<String> VIDEO_MIME_TYPES = new HashSet<>(
        Arrays.asList("video", "application/x-shockwave-flash", "application/vnd.rn-realmedia", "application/mxf"));

    @OSGiService
    private MimeTypeService mimeTypeService;

    @SlingObject
    private ResourceResolver resourceResolver;

    @Self
    private Resource resource;

    private Asset asset;
    private String filename;
    private String format;
    private String size;
    private String title;
    private String description;
    private String extension;
    private long lastModified;
    private boolean video;

    private String extensionCta;

    private String url;

    @PostConstruct
    protected void init() {
        initAssetDownload();
    }

    public String getPath() {
        return resource.getPath();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFilename() {
        return filename;
    }

    public String getFormat() {
        return format;
    }

    public String getSize() {
        return size;
    }

    public String getExtension() {
        return extension;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public boolean isVideo() {
        return video;
    }

    public String getExtensionCta() {
        return extensionCta;
    }

    private void initAssetDownload() {
        asset = resource.adaptTo(Asset.class);
        if (asset == null) {
            return;
        }

        final String mimeType = asset.getMimeType();
        filename = asset.getName();
        format = asset.getMetadataValue(DamConstants.DC_FORMAT);
        size = getAssetSize(asset);
        title = asset.getMetadataValue(DamConstants.DC_TITLE);
        description = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
        lastModified = asset.getLastModified();

        if (StringUtils.isNotBlank(format)) {
            extension = mimeTypeService.getExtension(format);
        } else {
            extension = FilenameUtils.getExtension(filename);
        }

        setExtensionCta();

        video = isMimeTypeVideo(mimeType);
    }

    private void setExtensionCta() {
        switch (extension) {
            case "pdf":
            case "xls":
            case "xlsx":
            case "doc":
            case "docx":
            case "ics":
            case "jpg":
            case "jpeg":
                extensionCta = extension;
                return;
            default:
                extensionCta = "svg";
        }
    }

    private static boolean isMimeTypeVideo(final String mimeType) {
        if (StringUtils.isNotBlank(mimeType)) {
            if (VIDEO_MIME_TYPES.contains(mimeType)) {
                return true;
            }

            for (final String videoMimeType : VIDEO_MIME_TYPES) {
                if (mimeType.startsWith(videoMimeType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getAssetSize(final Asset downloadAsset) {
        Object rawFileSizeObject = downloadAsset.getMetadata(DamConstants.DAM_SIZE);
        long rawFileSize;
        if (rawFileSizeObject != null) {
            rawFileSize = (Long) rawFileSizeObject;
        } else {
            rawFileSize = downloadAsset.getOriginal().getSize();
        }

        return FileUtils.byteCountToDisplaySize(rawFileSize);
    }

}
