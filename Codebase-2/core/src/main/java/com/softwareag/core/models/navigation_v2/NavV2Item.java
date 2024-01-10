package com.softwareag.core.models.navigation_v2;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

public class NavV2Item {

    @Getter
    @Setter
    private String linklabel;

    @Getter
    @Setter
    private String link;

    @Getter
    @Setter
    private String template;

    @Getter
    @Setter
    private String linkType;

    @Getter
    @Setter
    private String linkOpenIn;

    public NavV2Item(String linklabel, String link, String template, String linkType,String linkOpenIn) {
        this.linklabel = linklabel;
        this.link = link;
        this.template = template;
        this.linkType = linkType;
        this.linkOpenIn = linkOpenIn;
    }

    @Override
    public String toString() {
        return "NavV2Item{" +
                "linklabel='" + linklabel + '\'' +
                ", link='" + link + '\'' +
                ", template='" + template + '\'' +
                ", linkType='" + linkType + '\'' +
                '}';
    }
}
