package com.bestbuy.android.rewardzone.data;


import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.HttpHostConnectException;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.rewardzone.library.util.RZOptInOfferParser;
import com.bestbuy.android.rewardzone.web.weblayer.RZOfferWebCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;

public class RZOfferData {
	private static String TAG = "RZOfferListData*************";

	/*
	 * Getting response string from web layer and converting the json to
	 * appropriate object list using conversion utility classes
	 */
	public static String getOptInOfferResponseStatus(String offerId) {
		String response = null;
		String optInStatus = null;
		try {
			response = getOptInOfferResponse(offerId);
			optInStatus = RZOptInOfferParser.parseOptInResponse(response);
		}catch (SocketTimeoutException e) {
			BBYLog.printStackTrace(TAG, e);
		}catch (HttpHostConnectException e) {
			BBYLog.d("DATA ERROR: ", "HttpError");
			optInStatus = "NO_NETWORK_CONNECTION";
		}catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			optInStatus = "ERROR";
		}
		return optInStatus;
	}


	/*
	 * Method will internally calls other methods to get response and handle the
	 * repeating request
	 */
	public static String getOptInOfferResponse(String offerId) throws SocketTimeoutException,HttpHostConnectException, Exception {
		String results = null;
		String url = AppData.BBY_RZ_OPTIN_OFFER_PATH;
		final int MAX_RETRIES = 3;
		int retryCount = 0;
		int statusCode = 0;
		url = url.replace("?", offerId);
		Map<String, String> params = new HashMap<String, String>();
		params.put("source", "MAP");		
		// Retry the call 3 times if 500 error occurs
		while (retryCount < MAX_RETRIES) {
			try {
				results = RZOfferWebCaller.makeGetRequest(
						AppConfig.getBbyRzOfferURL(),
						url, params);
			} catch (APIRequestException apiEx) {
				statusCode = apiEx.getStatusCode();
				BBYLog.printStackTrace(TAG, apiEx);
				throw apiEx;
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				throw ex;
			}
			if (statusCode == 500) {
				retryCount++;
			} else {
				break;
			}
		}

		return results;
	}
}
