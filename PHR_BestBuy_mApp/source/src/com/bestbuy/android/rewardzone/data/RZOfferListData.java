package com.bestbuy.android.rewardzone.data;


import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.rewardzone.library.util.RZOfferDetailsParser;
import com.bestbuy.android.rewardzone.web.weblayer.RZOfferWebCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;

/**
 * Getting response data through communication weblayer
 * 
 * @author sharmila_j
 * 
 */
public class RZOfferListData {

	private static String TAG = "RZOfferListData*************";
	public static RZOfferDetailsParser parser = new RZOfferDetailsParser();
	/*
	 * Getting response string from web layer and converting the json to
	 * appropriate object list using conversion utility classes
	 */
	public static List<RZOfferDetails> getOfferDetailsList(int requestPageNumber) throws SocketTimeoutException,HttpHostConnectException, JSONException, Exception{
		List<RZOfferDetails> rzOfferDetailsList = null;
		String response = null;
		response = getOfferDetails(requestPageNumber);
		rzOfferDetailsList = parser.parse(response);		
		return rzOfferDetailsList;
	}

	/*
	 * Method will internally calls other methods to get response and handle the
	 * repeating request
	 */
	public static String getOfferDetails(int requestPageNumber) throws SocketTimeoutException, HttpHostConnectException, Exception {
		String results = null;
		final int MAX_RETRIES = 3;
		int retryCount = 0;
		int statusCode = 0;

		Map<String, String> params = new HashMap<String, String>();

		params.put("page", requestPageNumber+"");
		params.put("size", "20");

		// Retry the call 3 times if 500 error occurs
		while (retryCount < MAX_RETRIES) {
			try {
				
				results = RZOfferWebCaller.makeGetRequest(
						AppConfig.getBbyRzOfferURL(),
						AppData.BBY_RZ_OFFERS_PATH, params);
			} catch (APIRequestException apiEx) {
				statusCode = apiEx.getStatusCode();
				BBYLog.printStackTrace(TAG, apiEx);
				throw apiEx;
			} catch (SocketTimeoutException ex) {
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
