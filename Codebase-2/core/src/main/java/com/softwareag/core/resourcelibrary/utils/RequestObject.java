package com.softwareag.core.resourcelibrary.utils;

import java.util.List;

public class RequestObject {

	
	List<String> names;
	
	String requesttype;
	
	String searchterm;
	
	List<TagsBean> values;

	String assetpath;
	
	List<UrlParams> urlparams;
	
	public List<UrlParams> getUrlparams() {
		return urlparams;
	}

	public void setUrlparams(List<UrlParams> urlparams) {
		this.urlparams = urlparams;
	}

	public String getAssetpath() {
		return assetpath;
	}

	public void setAssetpath(String assetpath) {
		this.assetpath = assetpath;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public String getRequesttype() {
		return requesttype;
	}

	public void setRequesttype(String requesttype) {
		this.requesttype = requesttype;
	}

	public String getSearchterm() {
		return searchterm;
	}

	public void setSearchterm(String searchterm) {
		this.searchterm = searchterm;
	}

	public List<TagsBean> getValues() {
		return values;
	}

	public void setValues(List<TagsBean> values) {
		this.values = values;
	}

}
