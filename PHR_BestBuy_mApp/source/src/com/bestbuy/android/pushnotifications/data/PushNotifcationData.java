package com.bestbuy.android.pushnotifications.data;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Category;
import com.bestbuy.android.pushnotifications.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;

public class PushNotifcationData {
	public static final String PN_DOD="Dod";
	public static final String PN_RZ="Rz";
	public static final String PN_WO="Wo";
	
	public static final String STATUS="status";
	public static final String STATUS_FALSE="false";
	public static final String STATUS_TRUE="true";
	public static final String STATUS_SUCCESS="Success";
	
	public static final String STATUS_PN_RZ="Reward Zone";
	public static final String STATUS_PN_WO="Weekly Offers";
	public static final String STATUS_PN_DOD="Deal of the Day";

	
	public static String sendOffersNotificationConfig(
			List<String> selectedCategoryIds) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("urbanAirShipId", AppData.getUAid());
		params.put("deviceType", "1");
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "postSelectedCategories";
		try {
			return WebAccessCaller.makeGetRequest(host, path, params,
					selectedCategoryIds, null);
		} catch (SocketTimeoutException e) {
			return null;
		} catch (APIRequestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static String sendRZNotificationConfig(String rz_id) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("rzId", rz_id);
		params.put("deviceId", AppData.getUAid());
		params.put("deviceType", "1");
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "postSelectedCategories";
		try {
			return WebAccessCaller.makeGetRequest(host, path, params, null);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String sendResponsePNServer(){
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "checkServerStatus";
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		try {
			return WebAccessCaller.makeGetRequest(host, path, params, null);
		} catch (SocketTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String sendNotificationStatus(String catId,String status) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		params.put("deviceType", "1");
		params.put("urbanAirShipId", AppData.getUAid());
		params.put("offerCatId", catId);
		params.put("status", status);
		params.put("timeZone", UpdatedTimeZone.getTimeZoneName());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "postNotificationStatus";
		try {
			return APIRequest.makeGetRequest(host, path, params,false);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static NotificationStatus getNotificationStatus()  {
		NotificationStatus nStatus = new NotificationStatus();
		String result;
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		params.put("deviceType", "1");
		params.put("urbanAirShipId", AppData.getUAid());
		String path="getNotificationStatus";
		try {
			result=APIRequest.makeGetRequest(AppConfig.getPushNotificationAPIHost(), path, params,false);
			JSONObject jsonResult = new JSONObject(result);
			
			if (jsonResult.has("catStatus")) {
				nStatus.setStatus(jsonResult.getString("catStatus"));
			}
			if (jsonResult.has("code")) {
				nStatus.setStatusCode(jsonResult.getInt("code"));
			}
			if(jsonResult.has("offerCategoriesList")){
				JSONArray jsonOffCat=new JSONArray(jsonResult.getString("offerCategoriesList"));
				List<String> offCats=new ArrayList<String>();
				for (int i = 0; i < jsonOffCat.length(); i++) {
					JSONObject jsonObject=jsonOffCat.getJSONObject(i);
					if(jsonObject.has("offerCatDesc")){
						offCats.add(jsonObject.getString("offerCatDesc"));
					}
				}
				nStatus.setCategoryList(offCats);
			}
		} catch (Exception e) {
			return null;
		}
		
		return nStatus;

	}
	public static List<Category> getOfferCategories(Context context,
			AppData appData) throws Exception {
		List<String> categoryIds = new ArrayList<String>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", AppConfig.getBbyOfferApiKey());
		params.put("channel_key",
				AppData.BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL);

		String result = APIRequest.makeGetRequest(AppConfig.getBbyOfferURL(),
				AppData.BBY_OFFERS_DEPARTMENT_PATH, params, false, false);
		ArrayList<Category> categoryList = new ArrayList<Category>();
		JSONObject jsonResult = new JSONObject(result);
		JSONObject jsonData = jsonResult.getJSONObject("data");
		JSONArray jsonDepts = jsonData.getJSONArray("departments");
		for (int i = 0; i < jsonDepts.length(); i++) {
			JSONObject jsonCategory = jsonDepts.getJSONObject(i);
			Category category = new Category(null,
					jsonCategory.getString("name"), null,
					jsonCategory.getString("key"), false, null, false);
			categoryList.add(category);
			categoryIds.add(category.getRemixId());
		}
		appData.addToCategoryList(AppData.BBY_OFFERS_CATEGORY_LIST,
				categoryList);
		return categoryList;
	}

	
	public static List<String> getPrefferedCategoryIds() {
		
		List<String> categoryIds=new ArrayList<String>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "getSelectedCategories/"+AppData.getUAid();
		String result;
		try {
			result = APIRequest.makeGetRequest(host, path, params,false);
			JSONObject jsonResult = new JSONObject(result);
			JSONArray userDeatilArray= jsonResult.getJSONArray("userDetail");
			 
			for (int i = 0; i < userDeatilArray.length(); i++) {
				JSONObject categoryObjSource=userDeatilArray.getJSONObject(i);
				JSONObject categoryObj=categoryObjSource.getJSONObject("categories");
				String catCode=categoryObj.getString("catCode");
				categoryIds.add(catCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	return categoryIds;
  }
	
	public static String sendUpdatedUserTimeZone() {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		params.put("urbanAirShipId", AppData.getUAid());
		params.put("timeZone", UpdatedTimeZone.getTimeZoneName());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "updateUserTimezone";
		String result = null;
		try {
			result = APIRequest.makeGetRequest(host, path, params,false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
