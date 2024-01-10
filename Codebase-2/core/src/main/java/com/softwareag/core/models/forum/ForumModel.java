package com.softwareag.core.models.forum;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gson.Gson;
import com.softwareag.core.services.techforum.TechForumService;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = ForumModel.RESOURCE_TYPE)
public class ForumModel {

	
	public static final String RESOURCE_TYPE = "softwareag/components/content/forum";
	private static final Logger LOG = LoggerFactory.getLogger(ForumModel.class);

	@Inject
	private String endpointurl;

	@Inject
	private String title;

	@Inject
	private String errormessage;

	@Inject
	private TechForumService service;

	public String getErrormessage() {
		return errormessage;
	}
	
	private ForumResponse forumResponse;
	
	public ForumResponse getForumResponse() {
		return forumResponse;
	}

	public String getTitle() {
		return title;
	}

	@PostConstruct
	protected void init()  {
		
		if(StringUtils.isNotEmpty(endpointurl)) {
			
		JSONObject responseString = service.forumResponse(endpointurl);
		
		LOG.debug("final Response:::::::::" + responseString);
			forumResponse = getResponseObj(responseString.toString());		
		}
	}

	private ForumResponse getResponseObj(String message) {
		Gson gson = new Gson();
		ForumResponse responObj = gson.fromJson(message, ForumResponse.class);
		return responObj;
	}

}
