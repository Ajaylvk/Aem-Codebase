package com.softwareag.core.pojo;

import org.apache.sling.api.resource.Resource;

public class PartnerResult implements ComponentResult {

    private String title;
    private String partnerLevelTag;
    private String partnerLevelTagName;
    private String fileReference;
    private String altText;
    private Boolean globalPartner;
    private String industryTags;
    private String partnerPageURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPartnerLevelTag() {
        return partnerLevelTag;
    }

    public void setPartnerLevelTag(String partnerLevelTag) {
        this.partnerLevelTag = partnerLevelTag;
    }

    public String getPartnerLevelTagName() {
        return partnerLevelTagName;
    }

    public void setPartnerLevelTagName(String partnerLevelTagName) {
        this.partnerLevelTagName = partnerLevelTagName;
    }

    public String getFileReference() {
        return fileReference;
    }

    public void setFileReference(String fileReference) {
        this.fileReference = fileReference;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public Boolean getGlobalPartner() {
        return globalPartner;
    }

    public void setGlobalPartner(Boolean globalPartner) {
        this.globalPartner = globalPartner;
    }

    public String getIndustryTags() {
        return industryTags;
    }

    public void setIndustryTags(String industryTags) {
        this.industryTags = industryTags;
    }

    public String getPartnerPageURL() {
        return partnerPageURL;
    }

    public void setPartnerPageURL(String partnerPageURL) {
        this.partnerPageURL = partnerPageURL;
    }

    public void setPartnerPageURL(Resource r) {
        String partnerUri = r.getResourceResolver().map(r.getPath());
        if (!partnerUri.endsWith(".html")) {
            partnerUri = partnerUri + ".html";
        }
        this.partnerPageURL = partnerUri;
    }
}
