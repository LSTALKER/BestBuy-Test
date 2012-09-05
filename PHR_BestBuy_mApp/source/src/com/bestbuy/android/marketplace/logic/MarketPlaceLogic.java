package com.bestbuy.android.marketplace.logic;

import java.net.SocketTimeoutException;

import com.bestbuy.android.marketplace.data.MarketPlaceDetailsData;
import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.BBYLog;

/**
 * The user interface is only depending upon this class to fetch the data from the server.
 * @author lalitkumar_s
 *
 */

public class MarketPlaceLogic {
	
	private static final String TAG = "MarketPlaceLogic.Java";
	
	public static MarketPlaceDetails getMarketPlaceDetails(String skuId, String productId){
		try {
			return MarketPlaceDetailsData.getMarketPlaceDetails(skuId, productId);
		} catch (SocketTimeoutException e) {
			BBYLog.printStackTrace(TAG, e);
			return null;
		} catch (APIRequestException e) {
			BBYLog.printStackTrace(TAG, e);
			return null;
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			return null;
		}
	}
}
