package com.softwareag.core.pojo;

import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class PartnerOrderList {
    private List<Resource> eliteGlobalList;
    private List<Resource> premierGlobalList;
    private List<Resource> selectedGlobalList;
    private List<Resource> eliteList;
    private List<Resource> premierList;
    private List<Resource> selectedList;

    public PartnerOrderList() {
        this.eliteGlobalList = new ArrayList<>();
        this.premierGlobalList = new ArrayList<>();
        this.selectedGlobalList = new ArrayList<>();
        this.eliteList = new ArrayList<>();
        this.premierList = new ArrayList<>();
        this.selectedList = new ArrayList<>();
    }

    public List<Resource> getEliteGlobalList() {
        return eliteGlobalList;
    }

    public void setEliteGlobalList(Resource r) {
        this.eliteGlobalList.add(r);
    }

    public List<Resource> getPremierGlobalList() {
        return premierGlobalList;
    }

    public void setPremierGlobalList(Resource r) {
        this.premierGlobalList.add(r);
    }

    public List<Resource> getSelectedGlobalList() {
        return selectedGlobalList;
    }

    public void setSelectedGlobalList(Resource r) {
        this.selectedGlobalList.add(r);
    }

    public List<Resource> getEliteList() {
        return eliteList;
    }

    public void setEliteList(Resource r) {
        this.eliteList.add(r);
    }

    public List<Resource> getPremierList() {
        return premierList;
    }

    public void setPremierList(Resource r) {
        this.premierList.add(r);
    }

    public List<Resource> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(Resource r) {
        this.selectedList.add(r);
    }
}
