package com.bestbuy.android.rewardzone.web.weblayer;

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
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;

import com.bestbuy.android.rewardzone.library.util.TokenHelper;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.InputStreamExtensions;

/**
 * WebLayer that prepares data needed to call API
 * 
 * @author sharmila_j
 * 
 */
public class RZOfferWebCaller {

	private static String TAG = "RZOfferListWebCaller**************";
	private static String requestUrl = null;
	// Creating the uri and adding Request Headers and making GET request

	public static String makeGetRequest(String host, String path,
			Map<String, String> parameters) throws SocketTimeoutException,HttpHostConnectException, Exception {
		if (path != null) {
			requestUrl = host + "/" + path;
		} else {
			requestUrl = host;
		}
		//parameters.put("token", oAuthToken);
		TokenHelper tokenHelper = TokenHelper.getTokenInstance();
		if(tokenHelper.getToken() != null){
			parameters.put("token", tokenHelper.getToken());
			BBYLog.d("Final test:", tokenHelper.getToken());
		}else{
			//System.out.println("TOKEN NULL");
			return null;
		}
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		if (parameters != null) {
			Iterator<Map.Entry<String, String>> iter = parameters.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				valuePairs.add(new BasicNameValuePair(pair.getKey(), pair
						.getValue()));
			}
		}
		requestUrl = requestUrl.replace(" ", "%20");

		requestUrl = createQueryString(requestUrl, valuePairs);

		List<NameValuePair> requestHeaders = getRequestHeaders();
		//BBYLog.i("Request URL:===========================> " , requestUrl);
		
		HttpResponse response = APIRequest.makeRequest(requestUrl,
				requestHeaders, null, APIRequest.GET);
		return getResponseString(response);
	}

	// Seperating response string from obtained HttpResponse object and caching
	// the object

	
	private static String getResponseString(HttpResponse response)
			throws Exception {
		String responseBody = null;
		int statusCode = response.getStatusLine().getStatusCode();
		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		HttpEntity responseEntity = response.getEntity();

		if (response != null && response.getStatusLine() != null) {
			BBYLog.v(TAG, "Status Message: "
					+ response.getStatusLine().getReasonPhrase());
		} else {
			BBYLog.v(TAG, "Status or response was null");
		}
		if (statusCode >= 400) {			
			APIRequestException apiEx = new APIRequestException(response);
			responseBody = apiEx.getResponseBody();
			BBYLog.d(TAG, "APIRequest:makeRequest() - Response: "
					+ responseBody);
			throw apiEx;
		}

		if (responseEntity != null) {
			InputStream instream = responseEntity.getContent();

			if (AppConfig.isGZIP() && contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			responseBody = InputStreamExtensions.InputStreamToString(instream); // EntityUtils.toString(response.getEntity());

		}
		//System.out.println("RESPONSE IN WEB CALLER :" + responseBody);
		return responseBody;
	}

	/*
	 * Creating query string with respect to the url and valuepairs
	 */
	private static String createQueryString(String url,
			List<NameValuePair> valuePairs) {
		if (valuePairs == null || valuePairs.isEmpty()) {
			return url;
		}

		StringBuffer query = new StringBuffer();
		Iterator<NameValuePair> getParamsIter = valuePairs.iterator();
		while (getParamsIter.hasNext()) {
			NameValuePair pair = (NameValuePair) getParamsIter.next();
			String name = pair.getName().replace(" ", "%20").replace("&", "+");
			String value = pair.getValue().replace(" ", "%20")
					.replace("&", "+");
			query.append(name + "=" + value);
			if (getParamsIter.hasNext()) {
				query.append("&");
			}
		}
		return url + "?" + query;
	}

	/*
	 * RewardZone credentials to add with header
	 */
	private static List<NameValuePair> getRequestHeaders() {
		List<NameValuePair> requestHeaders = new ArrayList<NameValuePair>();
		requestHeaders.add(new BasicNameValuePair("username", "myrz"));
		requestHeaders.add(new BasicNameValuePair("Password", "points4purchase"));
		return requestHeaders;
	}

}
