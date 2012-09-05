package com.bestbuy.android.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.omniture.AppMeasurement;


public class EventsLogging extends AsyncTask<Void, Void, Void> {

	private String TAG = this.getClass().getName();
	private String host;
	private String path;
	private Map<String, String> parameters;
	private Exception exceptionMsg;
	//private static final Void Void = null;
	
	public static final String ADD_FROM_LIST_TO_CART = "api/V1/events/log/add_from_list_to_cart";
	public static final String ADD_ITEM_TO_CART_PATH = "api/V1/events/log/add_to_cart";
	public static final String APP_LAUNCH = "api/V1/events/log/app_launch";
	public static final String CHECKOUT_BEGIN_PATH = "api/V1/events/log/checkout_begin";
	public static final String CHECKOUT_COMPLETE_PATH = "api/V1/events/log/checkout_complete";
	public static final String CHECKOUT_LOGIN_PATH = "api/V1/events/log/checkout_login";
	public static final String CHECKOUT_SUBMIT_PATH = "api/V1/events/log/checkout_submit";
	public static final String COMPARISON_REFINEMENT = "api/V1/events/log/comparison_refinement";
	public static final String INTERNAL_CAMPAIGN_CLICK = "api/V1/events/log/internal_campaign_click";
	public static final String PRODUCT_DETAIL_VIEW_PATH = "api/V1/events/log/product_detail";
	public static final String QR_CODE_SCAN = "api/V1/events/log/qr_code_scan";
	public static final String QR_CODES_COMPARED = "api/V1/events/log/qr_codes_compared";
	public static final String RATE_APP_CLICK_REMIND_ME = "api/V1/events/log/rate_app_click_remind_me";
	public static final String RATE_APP_CLICK_SHOW_MORE = "api/V1/events/log/rate_app_click_show_more";
	public static final String RATE_APP_CLICK_NO_THANKS = "api/V1/events/log/rate_app_click_no_thanks";
	public static final String RATE_APP_CLICK_POP_UP = "api/V1/events/log/rate_this_app_popup";
	public static final String RZ_LOGIN_SUCCESS = "api/V1/events/log/rz_login_success";
	
	public static final String CUSTOM_CLICK_ACTION = "api/V1/events/log/custom_click";
	
	public static final String CUSTOM_CLICK_RZ_CARD_IMAGE_EVENT = "RZ Card Image";
	public static final String CUSTOM_CLICK_RZ_CERTIFICATE_EVENT = "RZ Certificate";
	public static final String CUSTOM_CLICK_RZ_LINK_EVENT = "RZ Link";
	public static final String CUSTOM_CLICK_RZ_OFFERS_EVENT = "RZ Offers";
	public static final String CUSTOM_CLICK_RZ_POINTS_EVENT = "RZ Points";
	public static final String CUSTOM_CLICK_RZ_PURCHASES_EVENT = "RZ Purchases";
	public static final String CUSTOM_CLICK_RZ_RECEIPTS_EVENT = "RZ Receipts";
	
	public static final String CUSTOM_CLICK_PRODUCT_BROWSE_EVENT = "Product Browse";
	public static final String CUSTOM_CLICK_USE_IMAGE_VIEWER_PHOTO_EVENT = "Use Image Viewer Photo";
	public static final String CUSTOM_CLICK_WEEKLY_AD_EVENT = "Weekly Ad";
	public static final String CUSTOM_CLICK_YOUR_HISTORY_EVENT = "Your History";
	
	/* Appolicious BlueShirt Omniture*/
	
	public static final String CUSTOM_CLICK_YOUR_APP_CENTER = "App Center";
	public static final String CUSTOM_CLICK_YOUR_BS_LOCAL_STORE_NAME = "Local Store Name";
	public static final String CUSTOM_CLICK_YOUR_BS_USER_RECOMMENDS = "Recommends";
	public static final String CUSTOM_CLICK_YOUR_BS_APP_NAME = "App Viewed";
	public static final String CUSTOM_CLICK_YOUR_BS_APP_DONWLODED = "App Name Downloaded";
	
	public static void fireAndForget(String path, Map<String, String> parameters) {
		/*EventsLogging el = new EventsLogging(AppConfig.getSmalHost(), path, parameters);
		el.execute(Void, Void, Void);*/
		
		prepareOmnitureCall(path, parameters);
	}
	
	private static void prepareOmnitureCall(String path, Map<String, String> parameters) {
		AppMeasurement appMeasure = new AppMeasurement();
		String value = "";
		String sku = "";
		String rz_id = "";
		String rz_tier = "";
		
		if (AppConfig.isOmnitureDevSuite())
			appMeasure.account = "bbyappcoreandroiddev,bbyappcoreglobaldev"; // For development
		else
			appMeasure.account = "bbyappcoreandroidprod,bbyappcoreglobalprod"; // For Production Build

		appMeasure.trackingServer = "bestbuy.122.2o7.net";
		
		appMeasure.visitorID = AppConfig.getEncryptedDeviceId();
		appMeasure.prop44 = AppConfig.getEncryptedDeviceId();
		appMeasure.eVar32 = "android";
		appMeasure.prop32 = "android";
		appMeasure.eVar31 = AppConfig.getPlatform();
		appMeasure.prop31 = AppConfig.getPlatform();
		if (AppData.getLocation() != null) 
			appMeasure.prop9 =  String.valueOf(AppData.getLocation().getLatitude())+ "," +String.valueOf(AppData.getLocation().getLongitude());	
		
		if (path.equals(APP_LAUNCH)) {
			appMeasure.pageName = "App launch";
			appMeasure.events = "event36";

		} else if (path.equals(CUSTOM_CLICK_ACTION)) {
			appMeasure.pageName = "Custom click";
			
			if (parameters != null && !parameters.isEmpty()) {
				value = parameters.get("value");
				//System.out.println("custom click");
				if (value.equals(CUSTOM_CLICK_RZ_CARD_IMAGE_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_CERTIFICATE_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_LINK_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_OFFERS_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_POINTS_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_PURCHASES_EVENT)
						|| value.equals(CUSTOM_CLICK_RZ_RECEIPTS_EVENT)) {
					rz_id = parameters.get("rz_id");
					rz_tier = parameters.get("rz_tier");
					appMeasure.prop21 = rz_id;
					appMeasure.eVar21 = rz_id;
					appMeasure.prop22 = rz_tier;
					appMeasure.eVar22 = rz_tier;
				}
			}

			appMeasure.prop38 = value;

		} else if (path.equals(ADD_FROM_LIST_TO_CART)) {
			appMeasure.pageName = "Add from list to cart";
			
			if (parameters != null && !parameters.isEmpty()) {
				sku = parameters.get("sku");
			}
			appMeasure.products = sku;
			appMeasure.events = "event65";

		} else if (path.equals(ADD_ITEM_TO_CART_PATH)) {
			
			appMeasure.pageName = "Add to cart";
			if (parameters != null && !parameters.isEmpty()) {
				sku = parameters.get("sku");
			}
			
			appMeasure.products = sku;
			appMeasure.events = "scAdd,scOpen";

		} else if (path.equals(CHECKOUT_BEGIN_PATH)) {
			appMeasure.pageName = "Checkout begin";
			appMeasure.events = "scCheckout";

		} else if (path.equals(CHECKOUT_COMPLETE_PATH)) {
			appMeasure.pageName = "Checkout complete";
			appMeasure.events = "purchase";

		} else if (path.equals(CHECKOUT_LOGIN_PATH)) {
			appMeasure.pageName = "Checkout login";
			appMeasure.events = "event9";

		} else if (path.equals(CHECKOUT_SUBMIT_PATH)) {
			appMeasure.pageName = "Checkout submit";
			appMeasure.events = "event20";

		} else if (path.equals(INTERNAL_CAMPAIGN_CLICK)) {
			appMeasure.pageName = "Internal campaign click";
			
			if (parameters != null && !parameters.isEmpty()) {
				value = parameters.get("value");
			}
			
			appMeasure.eVar47 = value;

		} else if (path.equals(PRODUCT_DETAIL_VIEW_PATH)) {
			appMeasure.pageName = "Product detail";
			
			if (parameters != null && !parameters.isEmpty()) {
				sku = parameters.get("sku");
			}
			
			appMeasure.products = sku;
			appMeasure.events = "prodView,event3";

		} else if (path.equals(QR_CODE_SCAN)) {
			String qr_code = "";
			String qr_code_url = "";
			String qr_code_screen = "";
			appMeasure.pageName = "Qr code scan";
			if (parameters != null && !parameters.isEmpty()) {
				qr_code = parameters.get("qr_code");
				qr_code_url = parameters.get("qr_code_url");
				qr_code_screen = parameters.get("qr_code_screen");
			}
			appMeasure.eVar52 = qr_code_url;
			appMeasure.eVar53 = qr_code;
			appMeasure.eVar55 = qr_code_screen;
			appMeasure.events = "event64";

		} else if (path.equals(QR_CODES_COMPARED)) {
			appMeasure.pageName = "Qr codes compared";
			
			if (parameters != null && !parameters.isEmpty()) {
				value = parameters.get("value");
			}
			
			appMeasure.eVar54 = value;

		} else if (path.equals(RATE_APP_CLICK_REMIND_ME)) {
			appMeasure.pageName = "Rate app click remind me";
			appMeasure.events = "event61";

		} else if (path.equals(RATE_APP_CLICK_SHOW_MORE)) {
			appMeasure.pageName = "Rate app click show more";
			appMeasure.events = "event62";

		} else if (path.equals(RATE_APP_CLICK_NO_THANKS)) {
			appMeasure.pageName = "Rate app click no thanks";
			appMeasure.events = "event63";

		} else if (path.equals(RATE_APP_CLICK_POP_UP)) {
			appMeasure.pageName = "Rate this app popup";
			appMeasure.eVar40 = "rate this app";

		} else if (path.equals(RZ_LOGIN_SUCCESS)) {
			appMeasure.pageName = "Rz login success";
			if (parameters != null && !parameters.isEmpty()) {
				rz_id = parameters.get("rz_id");
				rz_tier = parameters.get("rz_tier");
			}
			appMeasure.prop21 = rz_id;
			appMeasure.eVar21 = rz_id;
			appMeasure.prop22 = rz_tier;
			appMeasure.eVar22 = rz_tier;
			appMeasure.events = "event9";
		}

		appMeasure.track();
	}
	
	public static void trackRZButtonClick(Activity activity) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("value", EventsLogging.CUSTOM_CLICK_RZ_LINK_EVENT);
		
			AppData appData = ((BestBuyApplication) activity.getApplication()).getAppData();
			RZAccount rzAccount = appData.getRzAccount();
			if (rzAccount != null) {
				params.put("rz_id", rzAccount.getId());
				if (rzAccount.getTier() != null) {
					params.put("rz_tier", rzAccount.getStatusDisplay());
				} else {
					params.put("rz_tier", "");
				}
			}
			EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
		} catch (Exception ex) {
			BBYLog.e("EventsLogging", "EventsLogging.trackRZButtonClick() : " + ex.getMessage());
		}
	}
	
	public EventsLogging(String host, String path, Map<String, String> parameters) {
		this.host = host;
		this.path = path;
		this.parameters = parameters;
	}
	

	@Override
	protected Void doInBackground(Void... params) {
		try {
			if (parameters == null) {
				parameters = new HashMap<String, String>();
			}
			
			parameters.put("api_key", AppConfig.getSmalApiKey());
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("BB301-DEVICE", AppConfig.getDevice());
			headers.put("BB301-UUID", AppConfig.getEncryptedDeviceId());
			headers.put("BB301-PLATFORM", AppConfig.getPlatform());
			headers.put("BB301-APP-NAME", AppConfig.getAppName());
			headers.put("BB301-CARRIER", AppConfig.getCarrier());
			if (AppData.getLocation() != null) {
				headers.put("BB301-LAT", String.valueOf(AppData.getLocation().getLatitude()));
				headers.put("BB301-LONG", String.valueOf(AppData.getLocation().getLongitude()));
			}
			APIRequest.makeJSONPostRequest(host, path, parameters, headers);			
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.d(TAG, "Exception in EventsLogging.doInBackground() : " + ex.getMessage());
			this.exceptionMsg = ex;
		}
		return null;
	}
	
	public Exception getExceptionMsg() {
		return exceptionMsg;
	}


	@Override
	protected void onPostExecute(java.lang.Void result) {
	}


	@Override
	protected void onPreExecute() {
	}
}
