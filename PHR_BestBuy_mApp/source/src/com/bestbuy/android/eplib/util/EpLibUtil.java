package com.bestbuy.android.eplib.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import android.content.Context;
import android.location.Location;

import com.bestbuy.android.data.AppData;
import com.bestbuy.eventcapture.EPLib;
import com.bestbuy.eventcapture.EPLibConstants;
/**
 * 
 * @author bharathkumar_s
 *
 */
public class EpLibUtil {

	public static final String ACTION_UI_CLICK = "uiClick";
	public static final String ACTION_PAGE_LOAD = "pageView";
	
	public static final String ITEM_TYPE_PRODUCTS = "Products";
	public static final String ITEM_TYPE_REWARD_ZONE = "Reward Zone";
	public static final String ITEM_TYPE_STORES = "Stores";
	public static final String ITEM_TYPE_OPEN_BOX = "Open Box";
	public static final String ITEM_TYPE_WEEKLY_AD = "Weekly Ad";
	public static final String ITEM_TYPE_DEALS = "Deals";
	public static final String ITEM_TYPE_CODE_SCAN = "Code Scan";
	public static final String ITEM_TYPE_PHOTO_SEARCH = "Photo Search";
	public static final String ITEM_TYPE_WISH_LIST = "Wish List";
	public static final String ITEM_TYPE_GIFT_CARD = "Gift Card";
	public static final String ITEM_TYPE_HISTORY = "History";
	public static final String ITEM_TYPE_GAME_TRADE = "Game Trade-In";
	public static final String ITEM_TYPE_YOUR_ACCOUNT = "My Account";
	public static final String ITEM_TYPE_ORDER_STATUS = "Order Status";
	public static final String ITEM_TYPE_UPGRADE_CHECKER = "Phone Upgrade";
	public static final String ITEM_TYPE_APP_CENTER = "App Center";
	
	public static final String ITEM_TYPE_APP_LAUNCH = "AppLaunch";
	public static final String ITEM_TYPE_QR_CODE_SCAN = "CodeScan";
	public static final String ITEM_TYPE_COMPARE_CODES = "compareCodes";
	public static final String ITEM_TYPE_APP_RATING = "appRating";
	
	public static final String SRC = "MobileApps";
	public static final String USER_AGENT = "Android";
	public static final String EP_URL = "http://dev.context.bestbuy.com/_.gif";
	private static String latitude;
	private static String longitude;
	
	public static void trackEvent(Context context,String action,String itemType) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put(EPLibConstants.ACTION, URLEncoder.encode(action));
		params.put(EPLibConstants.ITEM_TYPE, URLEncoder.encode(itemType));
		params.put(EPLibConstants.CLIENT_TIME, URLEncoder.encode(getClientTime()));
		updateLatLong(context);
		params.put(EPLibConstants.LATITUDE, latitude);
		params.put(EPLibConstants.LONGITUDE, longitude);
		params.put(EPLibConstants.USER_AGENT, EpLibUtil.USER_AGENT);
		params.put(EPLibConstants.SRC, EpLibUtil.SRC);
		
		trackEventCapture(context, params);
	}
	
	

	public static void trackEventByCodeScan(Context context,String action,String itemType,String destUrl) {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		params.put(EPLibConstants.ACTION, URLEncoder.encode(action));
		params.put(EPLibConstants.ITEM_TYPE, URLEncoder.encode(itemType));
		params.put(EPLibConstants.DEST_URL, URLEncoder.encode(destUrl));
		params.put(EPLibConstants.CLIENT_TIME, URLEncoder.encode(getClientTime()));
		updateLatLong(context);
		params.put(EPLibConstants.LATITUDE, latitude);
		params.put(EPLibConstants.LONGITUDE, longitude);
		
		params.put(EPLibConstants.USER_AGENT, EpLibUtil.USER_AGENT);
		params.put(EPLibConstants.SRC, EpLibUtil.SRC);
		
		trackEventCapture(context, params);
	}
	

	public static void trackEventCapture(Context context,LinkedHashMap<String, String> params) {

		EPLib epLib = EPLib.getInstance(context, EpLibUtil.EP_URL);
		epLib.trackEvents(params);
	}
	
	private static void updateLatLong(Context context) {
		
		boolean isCurrentLocationAlloweded= AppData.isCurrentLocationAlloweded(context);
		if(isCurrentLocationAlloweded){
			Location location = AppData.getLocation();
			if(location!=null){
				latitude = String.valueOf(location.getLatitude());
				longitude = String.valueOf(location.getLongitude());
			}
		}else{
			latitude = "0";
			longitude = "0";
		}
	}
	
	private static String getClientTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z");
	    Calendar cal = Calendar.getInstance();
	    String date=dateFormat.format(cal.getTime());
	    return  date;
	}


}