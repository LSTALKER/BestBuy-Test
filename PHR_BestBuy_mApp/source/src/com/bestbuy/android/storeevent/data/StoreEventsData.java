package com.bestbuy.android.storeevent.data;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.storeevent.library.parser.StoreEventsParser;
import com.bestbuy.android.storeevent.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;

/**
 * This class is responsible to hit the web services and after getting the response parse 
 * to corresponding java object.
 * @author lalitkumar_s
 */

public class StoreEventsData {
	
	public static List<StoreEvents> getStoreEvents(String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		String results = null;
		
		results = WebAccessCaller.makeGetRequest(AppConfig.getBestbuyStoresURL(), storeId + AppData.BBY_STORE_EVENTS_PATH, null, null);
		
		if(results == null)
			return null;
		
		return parseToJson(results);
	}
	
	private static List<StoreEvents> parseToJson(String responseString) throws JSONException {
		return new StoreEventsParser().parse(responseString);
	}
}
