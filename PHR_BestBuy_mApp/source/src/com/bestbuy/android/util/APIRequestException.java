package com.bestbuy.android.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.bestbuy.android.data.commerce.CError;


@SuppressWarnings("serial")
public class APIRequestException extends Exception {
	private static final String TAG = "APIRequestException.java";
	private HttpResponse response;
    private String responseBody;
	
	public APIRequestException(HttpResponse response) {
		this.response = response;
	}
	public String getResponseBody() {
		try {
			if (responseBody == null) {
				responseBody = EntityUtils.toString(response.getEntity()); 
			}
			return responseBody;
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			return ex.getMessage();
		}
	}
	
	public JSONObject responseJSON(){
		if (responseBody == null) {
			getResponseBody();
		}
		try {
			return new JSONObject(responseBody);
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			return null;
		}
		
	}
	
	public int getStatusCode() {
		return response.getStatusLine().getStatusCode();
	}

	public String getReasonPhrase() { 
		return response.getStatusLine().getReasonPhrase();
	}
	
	public List<CError> getErrors() {
		String errorResp = getResponseBody();
		List<CError> cErrors = CErrorCodesHelper.parseAndGetValue(errorResp);
		if(cErrors != null){
			for(CError cError : cErrors){
				cError.setRawResult(errorResp);
			}
		}
		return cErrors;
	}
	public HttpResponse getResponse() {
		return response;
	}
}
