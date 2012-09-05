package com.bestbuy.android.storeevent.library.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Store;

/**
 * Responsible to parse the Stores JSON response to the corresponding java object. 
 * @author lalitkumar_s
 */

public class StoreParser {
	
	public List<Store> parse(String responseString) throws Exception {
		JSONObject jsonResults;
		jsonResults = new JSONObject(responseString);
		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		List<Store> stores = new ArrayList<Store>();
		
		for (int i = 0; i < jsonStores.length(); i++) {
			JSONObject jsonStore = jsonStores.getJSONObject(i);
			Store store = new Store();
			store.loadStoreData(jsonStore);
			stores.add(store);
		} 
		
		return stores;
	}
}
