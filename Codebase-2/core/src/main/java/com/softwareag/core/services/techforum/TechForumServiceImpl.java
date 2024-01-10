
package com.softwareag.core.services.techforum;

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

@Component(immediate = true, service = TechForumService.class)
public class TechForumServiceImpl implements TechForumService {


	public static final String TOPIC_LIST ="topic_list";
	public static final String TOPICS ="topics";
	public static final String ID ="id";
	public static final String TITLE ="title";
	public static final String SLUG ="slug";
	public static final String LAST_POSTED_AT ="last_posted_at";
	public static final String URL ="url";
	public static final String FORUM_BEAN ="forumBean";
	public static final String RESUTLS ="results";
	public static final String POSTS_COUNT="posts_count";
	public static final String TECH_FORUM_DOMIAN_URL = "https://tech.forums.softwareag.com/t/";
	private static final Logger LOG = LoggerFactory.getLogger(TechForumServiceImpl.class);
	private static final String TYPE = "type";

	
	@Override
	public JSONObject forumResponse(String endpointurl) {

		String responseString = APIServiceUtil.executeGetCall(endpointurl);
		JSONObject finalResponse = getFinalResponseJson(responseString);
		
		return finalResponse;
	}
	
	private static JSONObject getFinalResponseJson(String response) {
		JSONObject finalObj = new JSONObject();
		JSONArray arrayList = new JSONArray();
		JSONObject responseObj;
		try {
			responseObj = new JSONObject(response);
			// condition check for success or error response
						if(responseObj.isNull("status")){
			    JSONArray resultArray = new JSONArray();
				JSONObject topicsObj = responseObj.getJSONObject(TOPIC_LIST);
				resultArray = topicsObj.getJSONArray(TOPICS);
					
			for (int i = 0; i < 5; i++) {
				JSONObject resultObj = new JSONObject();
				JSONObject temp = resultArray.getJSONObject(i);
				String id = temp.get(ID).toString();
				String slug = temp.get(SLUG).toString();
				String last_posted_at = temp.get(LAST_POSTED_AT).toString();
				int posts_count = (int) temp.get(POSTS_COUNT);
				resultObj.put(ID, id);
				resultObj.put(TITLE, temp.get(TITLE));
					resultObj.put(SLUG, slug);
					resultObj.put(LAST_POSTED_AT, getFormattedDate(last_posted_at));
				    resultObj.put(URL, getUrl(TECH_FORUM_DOMIAN_URL, id, slug));
				    resultObj.put(POSTS_COUNT,setPostCount(posts_count));
				arrayList.put(resultObj);
	
			}
			
			finalObj.put(FORUM_BEAN, arrayList);
						}else {
							responseObj.put(TYPE, FORUM_BEAN);
							finalObj = responseObj;
						}
		} catch (JSONException e) {
			LOG.error("JSON Parse Exception ",e);
		}
		return finalObj;
	}

	public static String getUrl(String domain, String id, String slug) {
		String url =domain+"/"+slug+"/"+id;
		return url;
	}

	public static String getFormattedDate(String last_posted_at) {
		String formattedDate = null;
		 DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		   utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		   Date forumdate;
		try {
				forumdate = utcFormat.parse(last_posted_at);
				DateFormat pstFormat = new SimpleDateFormat("MMM d, yyyy");
				formattedDate = pstFormat.format(forumdate).toString();
			} catch (ParseException e) {
				LOG.error("Date format parse Exception ",e);
			}
		return formattedDate;
	}
	
	public static String setPostCount(int number)
	{
		String result;
		
		result=String.valueOf(number-1);
			
		return result;
	}
	
	
}
