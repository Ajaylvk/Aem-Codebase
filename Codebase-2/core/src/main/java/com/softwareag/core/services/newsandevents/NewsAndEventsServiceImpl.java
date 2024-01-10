
package com.softwareag.core.services.newsandevents;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwareag.core.util.APIServiceUtil;

@Component(immediate = true, service = NewsAndEventsService.class)
public class NewsAndEventsServiceImpl implements NewsAndEventsService {


	public static final String TOPIC_LIST ="topic_list";
	public static final String TOPICS ="topics";
	public static final String ID ="id";
	public static final String TITLE ="title";
	public static final String SLUG ="slug";
	public static final String LAST_POSTED_AT ="last_posted_at";
	public static final String URL ="url";
	public static final String TYPE ="type";
	public static final String CARD_BEAN ="cardBean";
	public static final String RESUTLS ="results";
	public static final String START_DATE ="start_date";
	public static final String NEWS = "news";
	public static final String EVENTS = "events";
	public static final String SIZE = "size";
	public static final String TECH_FORUM_DOMIAN_URL = "https://tech.forums.softwareag.com/t";
	
	private static final Logger LOG = LoggerFactory.getLogger(NewsAndEventsServiceImpl.class);

	@Override
	public JSONObject latestNewsResponse(String endpointurl, String cardType) {

		String responseString = APIServiceUtil.executeGetCall(endpointurl);
		JSONObject finalResponse = getFinalResponseJson(responseString, cardType);
		return finalResponse;
	}

	@Override
	public JSONObject eventsResponse(String endpointurl, String cardType) {
		// TODO Auto-generated method stub

		String responseString = APIServiceUtil.executeGetCall(endpointurl);
		JSONObject finalResponse = getFinalResponseJson(responseString, cardType);
		return finalResponse;
	}
	
	private static JSONObject getFinalResponseJson(String responseString, String cardType) {
		JSONObject finalObj = new JSONObject();
		JSONArray arrayList = new JSONArray();
		JSONObject responseObj;
		try {
			responseObj = new JSONObject(responseString);
			// condition check for success or error response
			if(responseObj.isNull("status")){
				JSONArray resultArray = new JSONArray();
				//LOG.info("Response JSON:::"+responseObj.toString());
				LOG.info("card Type::::"+cardType);
				if(NEWS.equalsIgnoreCase(cardType)) {
					JSONObject topicsObj = responseObj.getJSONObject(TOPIC_LIST);
					resultArray = topicsObj.getJSONArray(TOPICS);
				}else if (EVENTS.equalsIgnoreCase(cardType)) {
					 resultArray = responseObj.getJSONArray(RESUTLS);
				}
				// response json array length check
				if(resultArray.length() != 0) {
					int arrayLength = resultArray.length();
			
					if(arrayLength > 3 ) {
						arrayLength = 3;
					}
					LOG.info("arrayLength ::::"+arrayLength);
					for (int i = 0; i < arrayLength; i++) {
						JSONObject resultObj = new JSONObject();
						JSONObject temp = resultArray.getJSONObject(i);
						String id = temp.get(ID).toString();
						resultObj.put(ID, id);
						resultObj.put(TITLE, temp.get(TITLE));
						if(NEWS.equalsIgnoreCase(cardType)) {
							String slug = temp.get(SLUG).toString();
							String last_posted_at = temp.get(LAST_POSTED_AT).toString();
							resultObj.put(SLUG, slug);
							resultObj.put(LAST_POSTED_AT, getFormattedDate(last_posted_at, cardType));
							resultObj.put(URL, getUrl(TECH_FORUM_DOMIAN_URL, id, slug));
						}else if (EVENTS.equalsIgnoreCase(cardType)) {
							String start_date = temp.get(START_DATE).toString();
							resultObj.put(START_DATE, getFormattedDate(start_date, cardType));
							resultObj.put(URL, temp.get(URL));
						}
						arrayList.put(resultObj);
					}
					finalObj.put(TYPE, cardType);
					finalObj.put(CARD_BEAN, arrayList);
					finalObj.put(SIZE, arrayList.length());
				}else {
					responseObj.put(TYPE, cardType);
					finalObj.put(SIZE, arrayList.length());
					finalObj = responseObj;
				}
				
			}else {
				responseObj.put(TYPE, cardType);
				finalObj = responseObj;
			}
			LOG.info("final Response::"+finalObj.toString());
			System.out.println("final Response::"+finalObj.toString());
		} catch (JSONException e) {
			LOG.error("JSON Parse Exception ",e);
		}
		return finalObj;
	}

	public static String getUrl(String domain, String id, String slug) {
		// TODO Auto-generated method stub
		String url =domain+"/"+slug+"/"+id;
		return url;
	}

	public static String getFormattedDate(String date, String cardType) {
		String formattedDate = null;
		 DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		 DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		 Date newDate = null;
		try {
				DateFormat pstFormat = new SimpleDateFormat("MMM d, yyyy");
				if(NEWS.equalsIgnoreCase(cardType)) {
					 utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					 newDate = utcFormat.parse(date);
				}else if (EVENTS.equalsIgnoreCase(cardType)) {
					newDate = sdf.parse(date);
					pstFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
				}
				formattedDate = pstFormat.format(newDate).toString();
			} catch (ParseException e) {
				LOG.error("Date format parse Exception ",e);
			}
		return formattedDate;
	}
}
