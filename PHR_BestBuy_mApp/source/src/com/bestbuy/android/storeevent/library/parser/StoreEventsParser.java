package com.bestbuy.android.storeevent.library.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;

/**
 * Responsible to parse the Store Events JSON response to the corresponding java object. 
 * @author lalitkumar_s
 */

public class StoreEventsParser {
	private static final String STOREID = "storeId";
	private static final String EVENTS = "events";
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String POST_DATE = "postDate";
	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String LOCATION = "location";
	public static String STORE_IMAGE = "store_image";
	private String storeId;
	
	public List<StoreEvents> parse(String responseString) throws JSONException {
		
		JSONArray results = null;
		List<StoreEvents> dests = null;
		
		JSONObject jsonObject = new JSONObject(responseString);
		
		if(jsonObject != null) {
			storeId = jsonObject.getString(STOREID);
			results = jsonObject.getJSONArray(EVENTS);
			
			int arrayLength = results.length();
			
			JSONObject source = null;
			dests = new ArrayList<StoreEvents>();

			for(int i=0; i<arrayLength; i++){
				source = results.getJSONObject(i);
				dests.add(createFrom(source));
			}
		}
		
		return dests;
	}
	
	private StoreEvents createFrom(JSONObject source) throws JSONException{
		StoreEvents result = new StoreEvents();
		
		result.setStoreId(storeId);
		result.setTitle(source.optString(TITLE));
		result.setDescription(source.optString(DESCRIPTION));
		result.setPostDate(source.optString(POST_DATE));
		result.setStartDate(source.optString(START_DATE));
		result.setEndDate(source.optString(END_DATE));
		result.setLocation(source.optString(LOCATION));
		
		return result;
	}
}
