package com.softwareag.core.pojo;

import com.softwareag.core.models.link.LinkModel;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import static com.softwareag.core.constants.GenericConstants.HREF;
import static com.softwareag.core.constants.GenericConstants.JCR_CONTENT;

public class AssetResult implements ComponentResult {

    private String title;
    private String copyText;
    private String fileReference;
    private String assetTypeDisplay;
    private String ctaLabel;
    private String ctaURL;
    private String assetType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCopyText() {
        return copyText;
    }

    public void setCopyText(String copyText) {
        this.copyText = copyText;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getAssetTypeDisplay() {
        return assetTypeDisplay;
    }

    public void setAssetTypeDisplay(String assetTypeDisplay) {
        this.assetTypeDisplay = assetTypeDisplay;
    }

    public String getCtaLabel() {
        return ctaLabel;
    }

    public void setCtaLabel(String ctaLabel) {
        this.ctaLabel = ctaLabel;
    }

    public String getCtaURL() {
        return ctaURL;
    }

    public void setCtaURL(String ctaURL) {
        this.ctaURL = ctaURL;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setAssetResultLinkProps(Resource r) {
        String assetUrl = getUri(r);
        //need to check if it is internal or NOT
        LinkModel link = new LinkModel();
        link.setRawHref(assetUrl);
        if (link.isInternalAsset(link.getRawHref())) {
            assetType = "internal";
            assetUrl = r.getResourceResolver().map(assetUrl);
            if (!assetUrl.endsWith(".html")) {
                assetUrl = assetUrl + ".html";
            }
        } else {
            assetType = "external";
        }
        ctaURL = assetUrl;
    }

    private String getUri(Resource r) {
        String assetUrl = r.getPath();
        //if there is media component URI set up then this is dummy asset
        if (r.getChild(JCR_CONTENT + "/assetcomponent/mediaorpdfparsys/media") != null) {
            ValueMap mediaCompProp = r.getChild(JCR_CONTENT + "/assetcomponent/mediaorpdfparsys/media").getValueMap();
            if (mediaCompProp.containsKey(HREF)) {
                assetUrl = mediaCompProp.get(HREF).toString();
            }
        }
        return assetUrl;
    }
}
