package com.bestbuy.android.icr.util;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestbuy.android.util.BBYLog;

/**
 * In this class i am putting sku as a key and hideprice as a value into hash
 * map. This class is used in Home,SearchresultList classes.
 * 
 * @author bharathkumar_s
 * 
 */

public class IcrUtil {
	private static final String TAG = "IcrUtil";
	public static HashMap<String, Boolean> icrHidePrice = new HashMap<String, Boolean>();
	private static String sku;
	private static Boolean hidePrice;

	/**
	 * this method called from SearchResultClass
	 * 
	 * @return
	 */
	public static HashMap<String, Boolean> getIcrHidePrice() {
		return icrHidePrice;
	}

	/**
	 * this method called from Home class.
	 * 
	 * @param mContext
	 * 
	 * @param array
	 */
	public static void putJSonArray(JSONArray array) {

		IcrUtil.icrHidePrice.clear();
		for (int i = 0; i < array.length(); i++) {

			try {
				JSONObject jsonObject = array.getJSONObject(i);
				if (jsonObject.has("sku")) {
					sku = jsonObject.getString("sku");
				}
				if (jsonObject.has("hidePrice")) {
					hidePrice = jsonObject.getBoolean("hidePrice");
				}
				if (!icrHidePrice.containsKey(sku)) // make sure that the list
													// has unique SKU's
					icrHidePrice.put(sku, hidePrice);
			} catch (JSONException e) {
				BBYLog.printStackTrace(TAG, e);
			}
		}
	}

	/**
	 * by using this method i am clearing hash map content at time destroying
	 * Home class
	 * 
	 * this method called from Home class on Destroy method.
	 */
	public static void clearIcrHashMap() {

		if (icrHidePrice != null && icrHidePrice.size() > 0) {
			icrHidePrice.clear();
		}
	}
}