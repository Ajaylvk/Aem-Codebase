
  package com.softwareag.core.services.client;

import com.softwareag.core.models.newsandevents.NewsAndEventsResponse;

public interface ClientAPIService {
  
  
  //public void latestNewsRequest(String url);
  
  //public NewsAndEventsResponse newsAndEventsResponse();
  
  public String latestNewsResponse(String endpointurl);
  
  public String eventsResponse(String endpointurl);
  

  public String execute(String endpointurl);
  
  
  }
 