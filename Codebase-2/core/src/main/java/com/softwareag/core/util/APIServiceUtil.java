package com.softwareag.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIServiceUtil {

	public static final String APPLICATION_JSON = "application/json";
	public static final String ACCEPT_ENCODING_VALUE = "gzip, deflate, br";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT = "Accept";
	public static final String UTF8 = "UTF-8";
	public static final String STATUS = "status";
	private static final Logger LOG = LoggerFactory.getLogger(APIServiceUtil.class);
	 
	public static String executeGetCall(String url)  {
		// TODO Auto-generated method stub
		url =  url.trim().replaceAll(" ", "%20");
		String responseString = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (url != null) {
            HttpGet httpGet = new HttpGet(url);
            Map<String, String> headers = getHeader();
            for (String key : headers.keySet()) {
            		httpGet.setHeader( key, headers.get(key));
            }
            responseString = execute(httpClient, httpGet);
            
        }
		return responseString;
	}

	private static String execute(CloseableHttpClient httpClient, HttpGet httpGet) {
		String responseString= null;
		try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int httpResponseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            responseString = IOUtils.toString(is, UTF8);
            if (httpResponseCode != HttpStatus.SC_OK) {
                LOG.error("Error Status code" + httpResponseCode);
               // LOG.error("error Response:::"+responseString);
                JSONObject errorResponse = new JSONObject();
                errorResponse.put(STATUS, httpResponseCode);
                return errorResponse.toString();
            }
        } catch (UnsupportedOperationException | IOException | JSONException e) {
            LOG.error("Failed to create JSON", e);
        } finally {
            httpGet.releaseConnection();
        }
		return responseString;
	}

	private static Map<String, String> getHeader() {
		// TODO Auto-generated method stub
		  Map<String, String> headers = new HashMap<String, String>();
		  headers.put(ACCEPT, APPLICATION_JSON);
		  headers.put(ACCEPT_ENCODING, ACCEPT_ENCODING_VALUE);
		return headers;
	}

}
