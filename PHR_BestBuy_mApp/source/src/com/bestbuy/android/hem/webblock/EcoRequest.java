package com.bestbuy.android.hem.webblock;

import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestbuy.android.util.InputStreamExtensions;

public class EcoRequest {

	public static HttpClient httpclient;

	public static String getRequest(String url) throws Exception {
		String result = null;
		InputStream is = null;
		
		if (httpclient == null) {
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 15000); // 15 seconds
			HttpConnectionParams.setSoTimeout(params, 30000); // 15000=15 seconds
			httpclient = new MyDefaultHttpClient(params);
		}
		
		HttpGet get = new HttpGet(url);
		HttpResponse response = httpclient.execute(get);

		is = response.getEntity().getContent();
		result = InputStreamExtensions.InputStreamToString(is);
		return result;
	}
	
	public static String parse(String jSONResponse) throws JSONException {
		String termsAndConditions= null;
		JSONObject mainResponseJSONObj = new JSONObject(jSONResponse);
		termsAndConditions = mainResponseJSONObj.optString("EcoRebatesResponse", null);
		return termsAndConditions;
	}

}

