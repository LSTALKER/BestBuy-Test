package com.bestbuy.android.storeevent.data;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.storeevent.library.parser.StoreParser;
import com.bestbuy.android.storeevent.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;

/**
 * This class is responsible to hit the web services and after getting the response parse 
 * to corresponding java object.
 * @author lalitkumar_s
 */

public class StoreLocatorListData {
	
	public static List<Store> findNearbyStores(String zipCode) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return findStoresByLocation(-1, -1, zipCode);
	}
	
	public static List<Store> findNearbyStores(double latitude, double longitude) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return findStoresByLocation(latitude, longitude, "");
	}
	
	private static List<Store> findStoresByLocation(double latitude, double longitude, String zipCode) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		String results = null;
		String path = "";
		
		if(zipCode != null && !zipCode.equals(""))
			path = "stores(area(" + zipCode + "," + AppData.STORE_SEARCH_RADIUS + "))";
		else
			path = "stores(area(" + latitude + "," + longitude + "," + AppData.STORE_SEARCH_RADIUS + "))";

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");
		
		results = WebAccessCaller.makeGetRequest(AppConfig.getRemixURL() + "/v1", path, params, null);
		
		if(results == null)
			return null;
		
		return parseToJson(results);
	}
	
	private static List<Store> parseToJson(String responseString) throws Exception {
		StoreParser parser = new StoreParser();
		return parser.parse(responseString);
	}
	
}
