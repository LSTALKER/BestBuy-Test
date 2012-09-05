package com.bestbuy.android.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;

public class CategoryUtilities {

	public static List<Category> loadCategory(Context context, String categoryId, AppData appDataLocal) throws Exception {

		String url = AppData._301_CATEGORIES_PATH;
		if (categoryId != null && !categoryId.equals(AppData._301_ROOT_CATEGORY)) {
			url = AppData._301_CATEGORIES_PATH + "/" + categoryId + "/children";
		} else {
			categoryId = AppData._301_ROOT_CATEGORY;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", AppConfig.getSmalApiKey());
		params.put("fetch_all", "1");
		// TODO: RYAN SET TIME
		String results = APIRequest.makeGetRequest(AppConfig.getSmalHost(), url, params, false, true);

		JSONObject jsonResults = new JSONObject(results);
		JSONArray categories = jsonResults.getJSONArray("items");

		ArrayList<Category> categoryList = new ArrayList<Category>();

		for (int i = 0; i < categories.length(); i++) {
			JSONObject jsonCategoryObj = categories.getJSONObject(i);
			JSONObject jsonCategory = jsonCategoryObj.getJSONObject("category");

			String isLeaf = jsonCategory.getString("is_leaf");
			boolean leaf = false;
			if (isLeaf.equals("1")) {
				leaf = true;
			}
			String isFetchedViaSearch = jsonCategory.getString("is_product_list_fetched_via_search");
			boolean fetchedViaSearch = false;
			if (isFetchedViaSearch.equals("1")) {
				fetchedViaSearch = true;
			}

			Category subCat = new Category(jsonCategory.getString("id"), jsonCategory.getString("name"), jsonCategory.getString("image_url"), jsonCategory.getString("remix_id"), leaf, null, fetchedViaSearch);

			categoryList.add(subCat);
		}
		if(appDataLocal != null){
			appDataLocal.addToCategoryList(categoryId, categoryList);
		}
		return categoryList;
	}

	public static void savePreferredCategoryIds(List<String> preferredCategoryIds) {
		// Save the ids to the preferences
		StringBuilder idString = new StringBuilder();
		for (int i = 0; i < preferredCategoryIds.size(); i++) {
			if (i > 0) {
				idString.append(" ");
			}
			idString.append(preferredCategoryIds.get(i));
		}
		AppData.getSharedPreferences().edit().putString(AppData.PREFERRED_CATEGORIES, idString.toString()).commit();
	}

	public static List<String> getPreferredCategoryIds() {
		String preferredString = AppData.getSharedPreferences().getString(AppData.PREFERRED_CATEGORIES, null);
		BBYLog.d("CategoryUtilities.java", "preferredString: " + preferredString);
		List<String> preferredList = new ArrayList<String>();
		if (preferredString != null) {
			preferredList.addAll(Arrays.asList(preferredString.split(" ")));
			preferredList.remove("");
		}
		return preferredList;
	}

	public static List<Category> getOfferCategories(Context context, AppData appData) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", AppConfig.getBbyOfferApiKey());
		params.put("channel_key", AppData.BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL);

		String result = APIRequest.makeGetRequest(AppConfig.getBbyOfferURL(), AppData.BBY_OFFERS_DEPARTMENT_PATH, params, false, false);
		ArrayList<Category> categoryList = new ArrayList<Category>();
		JSONObject jsonResult = new JSONObject(result);
		JSONObject jsonData = jsonResult.getJSONObject("data");
		JSONArray jsonDepts = jsonData.getJSONArray("departments");
		for (int i = 0; i < jsonDepts.length(); i++) {
			JSONObject jsonCategory = jsonDepts.getJSONObject(i);
			Category category = new Category(null, jsonCategory.getString("name"), null, jsonCategory.getString("key"), false, null, false);
			categoryList.add(category);
		}
		appData.addToCategoryList(AppData.BBY_OFFERS_CATEGORY_LIST, categoryList);
		return categoryList;
	}
	
	public static void clearPreferredCategoryIds() {
		AppData.getSharedPreferences().edit().putString(AppData.PREFERRED_CATEGORIES, "").commit();
	}
}
