
package com.softwareag.core.services.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.softwareag.core.util.APIServiceUtil;


@Component(immediate = true, service = ClientAPIService.class)
public class ClientAPIServiceImpl implements ClientAPIService {

	public static final String APPLICATION_JSON = "application/json";
	public static final String UTF8 = "UTF-8";
	 private static final Logger LOG = LoggerFactory.getLogger(ClientAPIServiceImpl.class);
	 
		/*
		 * @Override public void latestNewsRequest(String url) { // TODO Auto-generated
		 * method stub
		 * 
		 * }
		 * 
		 * @Override public NewsAndEventsResponse newsAndEventsResponse() { // TODO
		 * Auto-generated method stub return null; }
		 */
	 @Override
	public String execute(String endpointurl) {
		String url = endpointurl; //"https://tech.forums.softwareag.com/c/news/7.json";
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		if (url != null) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept", APPLICATION_JSON);
            httpGet.addHeader("Accept-Encoding", "gzip, deflate, br");
            LOG.info("Request: " + httpGet.getURI());
            System.out.println("Request: " + httpGet.getURI());
 
            try {
                CloseableHttpResponse response = httpClient.execute(httpGet);
                int httpResponseCode = response.getStatusLine().getStatusCode();
 
                if (httpResponseCode != HttpStatus.SC_OK) {
                    LOG.error("Error when performing query. Http response code" + httpResponseCode);
                    JSONObject jsonObject = new JSONObject();
                    //Add your custom logic based on the error code
                    //jsonObject.addProperty("status", "Server Error");
                   // return jsonObject;
                }
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
				
                String jsonResponse = IOUtils.toString(is, UTF8);
                System.out.println("jsonResponse: " + jsonResponse);
               // result = new JSONObject(jsonResponse);
                result = jsonResponse;
            } catch (UnsupportedOperationException | IOException e) {
                LOG.error("Failed to create JSON", e);
            } finally {
                httpGet.releaseConnection();
            }
        }
		return result;
	}

	/*
	 * getRequestHeaders(); executeMethod(); getStatusCode(); getResponse();
	 */

	/*
	 * public static void main(String[] args) { // final
	 * AtomicReference<CloseableHttpClient> httpClient = new AtomicReference<>(); //
	 * final AtomicReference<HttpClientContext> clientContext = new
	 * AtomicReference<>(); //if you need to create a custom context
	 * 
	 * }
	 */
	private static void getUrl() {
		
		
	}

	@Override
	public String latestNewsResponse(String endpointurl) {
		
		String url = endpointurl;
		
		String responseString = APIServiceUtil.executeGetCall(url);
		//get json object
		//convert json to bean class
		//
		
		return responseString;
	}

	@Override
	public String eventsResponse(String endpointurl) {
		// TODO Auto-generated method stub
		
		String url = endpointurl;
		String responseString = APIServiceUtil.executeGetCall(url);	
		return responseString;
	}
	

}
