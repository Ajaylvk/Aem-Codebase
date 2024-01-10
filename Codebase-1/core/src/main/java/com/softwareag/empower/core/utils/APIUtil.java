package com.softwareag.empower.core.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

public class APIUtil {

	public static final String APPLICATION_JSON = "application/json";
	public static final String ACCEPT_ENCODING_VALUE = "gzip, deflate, br";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String ACCEPT = "Accept";
	public static final String UTF8 = "UTF-8";
	public static final String STATUS = "status";
	private static final Logger LOG = LoggerFactory.getLogger(APIUtil.class);
	 
	public static String executeGetCall(String serviceUrl, Map<String, String> headers)  {

		serviceUrl =  serviceUrl.trim().replaceAll(" ", "%20");
		String responseString = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (serviceUrl != null) {
            HttpGet httpGet = new HttpGet(serviceUrl);
            for (String key : headers.keySet()) {
            		httpGet.setHeader( key, headers.get(key));
            }
            responseString = execute(httpClient, httpGet);
            
        }
		return responseString;
	}

	public static String executePostCall(String serviceUrl, Map<String, String> headers, Map<String, String> paramsMap)
			throws UnsupportedEncodingException {

		String responseString = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (serviceUrl != null) {
			HttpPost httppost = new HttpPost(serviceUrl);
			for (String key : headers.keySet()) {
				httppost.addHeader( key, headers.get(key));
			}

			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getPostEntity(paramsMap), "UTF-8");
			httppost.setEntity(postEntity);

			responseString = execute(httpClient, httppost);

		}

		return responseString;
	}

	private static String execute(CloseableHttpClient httpClient, HttpRequestBase httpMethod) {
		String responseString= null;
		try {
            CloseableHttpResponse response = httpClient.execute(httpMethod);
            int httpResponseCode = response.getStatusLine().getStatusCode();
			LOG.debug("Status Code :: {}", httpResponseCode);
            HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity);
            if (httpResponseCode != HttpStatus.SC_OK) {
                LOG.error("Error Description :: " + response.getStatusLine());
                JSONObject errorResponse = new JSONObject();
                errorResponse.put(STATUS, httpResponseCode);
                return errorResponse.toString();
            }
        } catch (UnsupportedOperationException | IOException | JSONException e) {
            LOG.error("Failed to create JSON", e);
        } finally {
			httpMethod.releaseConnection();
        }
		return responseString;
	}

	private static ArrayList<BasicNameValuePair> getPostEntity(Map<String, String> paramsMap) {
		ArrayList<BasicNameValuePair> formParams = new ArrayList<>();

		for (String key : paramsMap.keySet()) {
			formParams.add(new BasicNameValuePair(key, paramsMap.get(key)));
		}
		return formParams;
	}

}
