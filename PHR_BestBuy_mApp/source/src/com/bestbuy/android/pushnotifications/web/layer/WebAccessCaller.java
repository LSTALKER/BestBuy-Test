package com.bestbuy.android.pushnotifications.web.layer;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import com.bestbuy.android.pushnotifications.web.block.WebAccessBlock;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.InputStreamExtensions;

/**
 * WebLayer that prepares data needed to call API
 * @author lalitkumar_s
 */

public class WebAccessCaller {
	private static String TAG = "WebAccessCaller**************";
	private static String requestUrl= null;

	/*
	 * Creating the uri and adding Request Headers and making GET request
	 */
	public static InputStream makeGetRequest(String url) throws SocketTimeoutException, APIRequestException, Exception {
		InputStream responseStream=null;
		requestUrl=url;		
		try{
			HttpGet httpRequest = null;
		    httpRequest = new HttpGet(requestUrl);
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
		    HttpEntity entity = response.getEntity();
		    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
		    responseStream= bufHttpEntity.getContent();
		    if(responseStream==null){
		    }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return responseStream;
	}
	
	public static String makeGetRequest(String host, String path, Map<String, String> parameters, List<NameValuePair> requestHeaders) throws SocketTimeoutException, APIRequestException, Exception {
		
		if (path != null)
			requestUrl = host + "/" + path;
		else
			requestUrl = host;
		
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		if (parameters != null) {
			Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				valuePairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
			}
		}
		
		requestUrl = requestUrl.replace(" ", "%20");

		requestUrl = createQueryString(requestUrl, valuePairs);
		
		
		HttpResponse response = WebAccessBlock.makeRequest(requestUrl, requestHeaders, APIRequest.GET);
		
		return getResponseString(response);
	}
	
public static String makeGetRequest(String host, String path, Map<String, String> params, List<String> parameters, List<NameValuePair> requestHeaders) throws SocketTimeoutException, APIRequestException, Exception {
		
		if (path != null)
			requestUrl = host + "/" + path;
		else
			requestUrl = host;
		
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		if (parameters != null) {
			for(int i = 0; i<parameters.size(); i++ ){
				valuePairs.add(new BasicNameValuePair("catId", parameters.get(i)));
			}
		}
		
		if (params != null) {
			Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				valuePairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
			}
		}
		
		requestUrl = requestUrl.replace(" ", "%20");

		requestUrl = createQueryString(requestUrl, valuePairs);
		
		BBYLog.v(TAG+"--REQ URL SEND CATIDS:",requestUrl);
		HttpResponse response = WebAccessBlock.makeRequest(requestUrl, requestHeaders, APIRequest.GET);
		
		return getResponseString(response);
	}

	/*
	 * Seperating response string from obtained HttpResponse object and caching the object
	 */
	private static String getResponseString(HttpResponse response) throws APIRequestException, Exception  {
		String responseBody = null;
		int statusCode = response.getStatusLine().getStatusCode();
		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		HttpEntity responseEntity = response.getEntity();

		if (response != null && response.getStatusLine() != null) {
			BBYLog.v(TAG , "Status Message: " + response.getStatusLine().getReasonPhrase());
		} else {
			BBYLog.v(TAG, "Status or response was null");
		}
		if (statusCode >= 400 ) {
			
			APIRequestException apiEx = new APIRequestException(response);
			responseBody = apiEx.getResponseBody();
			BBYLog.d(TAG, "APIRequest:makeRequest() - Response: " + responseBody);
			throw apiEx;
		}

		if (responseEntity != null) {
			InputStream instream = responseEntity.getContent();

			if (AppConfig.isGZIP() && contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			responseBody = InputStreamExtensions.InputStreamToString(instream); // EntityUtils.toString(response.getEntity());
		}
		
		BBYLog.d(TAG, "APIRequest:makeRequest() - Response: " + responseBody);
		
		return responseBody;
	}


	/*
	 * Creating query string with respect to the url and valuepairs
	 */
	private static String createQueryString(String url, List<NameValuePair> valuePairs) {
		
		if (valuePairs == null || valuePairs.isEmpty())
			return url;

		StringBuffer query = new StringBuffer();
		if (valuePairs != null) {
			Iterator<NameValuePair> getParamsIter = valuePairs.iterator();
			while (getParamsIter.hasNext()) {
				NameValuePair pair = getParamsIter.next();
				String name = pair.getName().replace(" ", "%20").replace("&", "+");
				String value = pair.getValue().replace(" ", "%20").replace("&", "+");
				query.append(name + "=" + value);
				
				if (getParamsIter.hasNext())
					query.append("&");
			}
		}
		
		return url + "?" + query;
	}
	
}
