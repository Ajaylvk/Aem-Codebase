package com.softwareag.core.pojo;

import java.util.List;
import java.util.Map;

public class OverviewResult {

    private List<ComponentResult> resultList;
    private Map<String, List<Filter>> filterList;
    private String error;

    public List<ComponentResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<ComponentResult> resultList) {
        this.resultList = resultList;
    }

    public Map<String, List<Filter>> getFilterList() {
        return filterList;
    }

    public void setFilterList(Map<String, List<Filter>> filterList) {
        this.filterList = filterList;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
