package com.bestbuy.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.PickUPStoresAvail;
import com.bestbuy.android.data.Store;
import com.google.android.maps.GeoPoint;

/**
 * Contains methods for finding store locations
 * 
 * @author Recursive Awesome
 * 
 */
public class StoreLocator {

	@SuppressWarnings("unused")
	private static String TAG = "StoreLocator";

	public static List<Store> findNearbyStores(Location location) throws Exception {
		return findStoresByLocation(location.getLatitude(), location.getLongitude());
	}

	public static List<Store> findNearbyStoresWithSku(Location location, String sku) throws Exception {
		return findStoresByLocationWithSku(location.getLatitude(), location.getLongitude(), sku);
	}

	private static List<Store> findStoresByLocation(double latitude, double longitude) throws Exception {
		List<Store> stores = new ArrayList<Store>();

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");

		String query = "stores(area(" + latitude + "," + longitude + "," + AppData.STORE_SEARCH_RADIUS + "))";

		String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query, params, false);
		JSONObject jsonResults = new JSONObject(results);
		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		for (int i = 0; i < jsonStores.length(); i++) {
			JSONObject jsonStore = jsonStores.getJSONObject(i);
			Store store = new Store();
			store.loadStoreData(jsonStore);
			stores.add(store);
		}
		return stores;
	}

	private static List<Store> findStoresByLocationWithSku(double latitude, double longitude, String sku) throws Exception {
		List<Store> stores = new ArrayList<Store>();

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");
		String query = "stores(area(" + latitude + "," + longitude + "," + AppData.STORE_SEARCH_RADIUS + "))+products(sku=" + sku + ")";
		String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query, params, false);
		JSONObject jsonResults = new JSONObject(results);
		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		for (int i = 0; i < jsonStores.length(); i++) {
			JSONObject jsonStore = jsonStores.getJSONObject(i);
			Store store = new Store();
			store.loadStoreData(jsonStore);
			stores.add(store);
		}
		return stores;
	}

	public static boolean validateZipCode(String zipCode){
		String result = "";
		Map<String, String> params = new HashMap<String, String>();
		params.put("zip",zipCode);
//				xmlResponse = APIRequest.makeGetRequest("https://bestbuy.ecorebates.com/api/search/bestbuy/productRebateSummaries?zip="+zipCode, null, null, null);
				try {
					result = APIRequest.makeGetRequest(AppConfig.getEcoRebatesUrl(), null, params, false);
				} catch (Exception e) {
					BBYLog.printStackTrace(TAG, e);
				}
				return result.contains("error");
	}
	
	public static List<PickUPStoresAvail> findStoresByLocationWithSkus(double latitude, double longitude, ArrayList<String> skuList) throws Exception {
		StringBuilder query = new StringBuilder("stores(area(" + latitude + "," + longitude + "," + "50" + "))+products(sku in(");
		
		int index = skuList.size();
		for (int i = 0; i < skuList.size(); i++) {
			query.append(skuList.get(i));
			if (i != index - 1) {
				query.append(",");
			}
		}
		
		query.append("))");
		
		return serachStoreWithLocation(query);
	}
	
	public static List<PickUPStoresAvail> findStoresByLocationWithSkus(String zip, ArrayList<String> skuList) throws Exception {
		StringBuilder query = new StringBuilder("stores(area(" + zip + ", 50" + "))+products(sku in(");
		int index = skuList.size();
		for (int i = 0; i < skuList.size(); i++) {
			query.append(skuList.get(i));
			if (i != --index) {
				query.append(",");
			}
		}
		
		query.append("))");
		
		return serachStoreWithLocation(query);
	}

	private static List<PickUPStoresAvail> serachStoreWithLocation(StringBuilder query) throws Exception {
		List<PickUPStoresAvail> stores = new ArrayList<PickUPStoresAvail>();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");
		params.put("show", "name,products.sku");

		String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query.toString(), params, false);
		JSONObject jsonResults = new JSONObject(results);

		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		for (int i = 0; i < jsonStores.length(); i++) {
			JSONObject jsonStore = jsonStores.getJSONObject(i);
			String Name = jsonStore.getString("name");
			JSONArray jsonProducts = jsonStore.getJSONArray("products");
			ArrayList<String> skuLists = new ArrayList<String>();
			for (int j = 0; j < jsonProducts.length(); j++) {
				JSONObject jsonProduct = jsonProducts.getJSONObject(j);
				String tempSku = jsonProduct.getString("sku");
				skuLists.add(tempSku);
			}
			PickUPStoresAvail store = new PickUPStoresAvail();
			store.setName(Name);
			store.setSKUList(skuLists);
			stores.add(store);
		}

		return stores;
	}
	
	public static Store findStoreById(String id) throws Exception {
		Store store = new Store();

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");

		String query = "stores(storeId=" + id + ")";

		String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query, params, false);
		JSONObject jsonResults = new JSONObject(results);
		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		JSONObject jsonStore = jsonStores.getJSONObject(0);
		store.loadStoreData(jsonStore);
		return store;
	}
	
	public static Store findStoreByIdWithZip(String id, String zip) throws Exception {
		Store store = new Store();

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");

		String query = "stores(area(" + zip + ",5000)&storeId=" + id + ")";

		String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query, params, false);
		JSONObject jsonResults = new JSONObject(results);
		JSONArray jsonStores = jsonResults.getJSONArray(AppData.STORES);
		JSONObject jsonStore = jsonStores.getJSONObject(0);
		store.loadStoreData(jsonStore);
		return store;
	}

	public static GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	public static void setLocationUpdates(LocationManager locationManager, LocationListener onLocationChange) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10000.0f, onLocationChange);
	}

}
