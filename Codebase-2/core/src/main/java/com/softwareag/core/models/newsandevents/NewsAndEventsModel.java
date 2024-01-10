package com.softwareag.core.models.newsandevents;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.softwareag.core.services.newsandevents.NewsAndEventsService;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = NewsAndEventsModel.RESOURCE_TYPE)
public class NewsAndEventsModel {

	
	public static final String NEWS = "news";
	public static final String EVENTS = "events";
	public static final String RESOURCE_TYPE = "softwareag/components/content/newsandevents";
	private static final Logger LOG = LoggerFactory.getLogger(NewsAndEventsModel.class);

	@Inject
	private String endpointurl;

	@Inject
	private String cardType;

	@Inject
	private String title;

	@Inject
	private String errormessage;

	@Inject
	private NewsAndEventsService service;

	public String getErrormessage() {
		return errormessage;
	}
	
	public String getCardType() {
		return cardType;
	}

	private NewsAndEventsResponse newsAndEventsResponse;
	
	public NewsAndEventsResponse getNewsAndEventsResponse() {
		return newsAndEventsResponse;
	}

	public String getTitle() {
		return title;
	}

	@PostConstruct
	protected void init()  {
		JSONObject responseString = null;
		
		if(StringUtils.isNotEmpty(cardType)) {
			
			if(NEWS.equalsIgnoreCase(cardType)) {
				responseString = service.latestNewsResponse(endpointurl, cardType);
			}else if(EVENTS.equalsIgnoreCase(cardType)) {
				responseString = service.eventsResponse(endpointurl, cardType);
			}
			LOG.debug("final Response:::::::::" + responseString);
			newsAndEventsResponse = getResponseObj(responseString.toString());
			
		}
	}

	private NewsAndEventsResponse getResponseObj(String message) {
		Gson gson = new Gson();
		NewsAndEventsResponse responObj = gson.fromJson(message, NewsAndEventsResponse.class);
		return responObj;
	}

}
