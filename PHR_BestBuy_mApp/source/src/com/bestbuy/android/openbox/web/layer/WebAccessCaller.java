package com.bestbuy.android.openbox.web.layer;

import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bestbuy.android.util.APIRequestException;

/**
 * WebLayer that prepares data needed to call API
 * @author Lalit Kumar Sahoo
 * 
 */
public class WebAccessCaller {
	
	private static final String TAG = "WebAccessCaller.Java";
	
	private static String requestUrl= null;

	/*
	 * Creating the uri and adding Request Headers and making GET request
	 */
	public static InputStream makeGetRequest(String url) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		InputStream responseStream=null;
		requestUrl=url;
		
		HttpGet httpRequest = null;
	    httpRequest = new HttpGet(requestUrl);
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
	    HttpEntity entity = response.getEntity();
	    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity); 
	    responseStream= bufHttpEntity.getContent();
	    
		return responseStream;
	}
}
