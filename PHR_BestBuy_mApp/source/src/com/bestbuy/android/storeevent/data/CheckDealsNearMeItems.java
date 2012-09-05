package com.bestbuy.android.storeevent.data;

import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.storeevent.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;

public class CheckDealsNearMeItems {
	
	public String hasDealsNearMeItems(String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		String results = null;
		
		results = WebAccessCaller.makeGetRequest(AppConfig.getBestbuyStoresURL(), storeId + AppData.BBY_DEALS_NEARME_PATH, null, null);
		
		if(results == null)
			return null;
		
		return results;
	}
}
