
  package com.softwareag.core.services.newsandevents;

import org.json.JSONObject;

public interface NewsAndEventsService {
  
  
  public JSONObject latestNewsResponse(String endpointurl, String cardType) ;
  
  public JSONObject eventsResponse(String endpointurl, String cardType) ;
  
  
  }
 