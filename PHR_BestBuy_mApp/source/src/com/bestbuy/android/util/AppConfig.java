package com.bestbuy.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;

public class AppConfig {

	@SuppressWarnings("unused")
	private static final String TAG = "AppConfig";

	/*
	 * String reqUrl = "https://api.remix.bestbuy.com/oauth/request_token";
	 * String authzUrl = "https://authorize.bestbuy.com/authorizations"; String
	 * accessUrl = "https://api.remix.bestbuy.com/oauth/access_token";
	 * 
	 * DEV : http://dldolsapp02.na.bestbuy.com:22422 CIT :
	 * https://dlcolsapp01.na.bestbuy.com:14341 QA :
	 * https://commerce.qa.bestbuy.com:443 STAGE :
	 * https://commerce-ssl.stage.bestbuy.com:443 PROD :
	 * https://commerce-ssl.bestbuy.com:443
	 */
	
	private static String bbyListHost;
	private static String bbyListApiKey;
	private static String bbyOfferApiKey;
	private static String smalHost;
	private static String smalSecureHost;
	private static String smalApiKey;
	private static String smalSecureApiKey;
	private static String bbyScanHost;
	private static String bbyScanApiKey;
	private static String bbyOfferHost;
	private static String commerceHost;
	private static String commerceDomain;
	private static String commercePath;
	private static boolean useProxy;
	private static String bbyrzOfferHost;
	private static String mDotURL;
	private static String mDotHost;
	private static String mDotSignInHost;
	private static String ugradeCheckerApiKey;
	private static String upgradeCheckerSourceSystemValue;
	private static String upgradeCheckerDemoURL;
	private static String upgradeCheckerProdURL;
	
	private static String storeLocatorMapURL;
	private static String facebookPostDefaultURL;
	private static String bbyIQEnginesURL;
	private static String apiIQEnginesURL;
	private static String fbDialogBaseURL;
	private static String fbDialogGraphURL;
	private static String fbRestServerURL;
	private static String mDOTMainURL;
	
	static {
		
			getPreferenceValues();	
	}
	public static void getPreferenceValues(){
		SharedPreferences prefs = AppData.getSharedPreferences();
		bbyListHost = prefs.getString("bbyListHost", AppData.getContext().getString(R.string.bby_list_host));
		bbyListApiKey = prefs.getString("bbyListApiKey", AppData.getContext().getString(R.string.bby_list_api_key));
		
		
//		bbyOfferHost = prefs.getString("bbyOfferHost", AppData.getContext().getString(R.string.bby_offer_url));
//		bbyOfferApiKey = prefs.getString("bbyOfferApiKey", AppData.getContext().getString(R.string.bby_offer_api_key));
		
		
		bbyOfferHost = prefs.getString(EnvironmentConstants.MODULE_NAME3+EnvironmentConstants.TAG_HOST, AppData.getContext().getString(R.string.bby_offer_url));
		bbyOfferApiKey = prefs.getString(EnvironmentConstants.MODULE_NAME3+EnvironmentConstants.TAG_APIKEY, AppData.getContext().getString(R.string.bby_offer_api_key));
		
		bbyrzOfferHost = prefs.getString("bbyrzOfferHost", AppData.getContext().getString(R.string.bby_rz_offer));
		
	
//		smalHost = prefs.getString("smalHost", AppData.getContext().getString(R.string.SMAL_service_url));
//		smalSecureHost = prefs.getString("smalSecureHost", AppData.getContext().getString(R.string.SMAL_service_secure_url));
//		smalApiKey = prefs.getString("smalApiKey", AppData.getContext().getString(R.string.SMAL_api_key));
//		smalSecureApiKey = prefs.getString("smalSecureApiKey", AppData.getContext().getString(R.string.SMAL_secure_api_key));
		
		smalHost = prefs.getString(EnvironmentConstants.MODULE_NAME2+EnvironmentConstants.TAG_HOST, AppData.getContext().getString(R.string.SMAL_service_url));
		smalSecureHost = prefs.getString(EnvironmentConstants.MODULE_NAME2+EnvironmentConstants.TAG_SECUREHOST, AppData.getContext().getString(R.string.SMAL_service_secure_url));
		smalApiKey = prefs.getString(EnvironmentConstants.MODULE_NAME2+EnvironmentConstants.TAG_APIKEY, AppData.getContext().getString(R.string.SMAL_api_key));
		smalSecureApiKey = prefs.getString(EnvironmentConstants.MODULE_NAME2+EnvironmentConstants.TAG_SECURE_APIKEY, AppData.getContext().getString(R.string.SMAL_secure_api_key));
		
		
		bbyScanHost = prefs.getString("bbyScanHost", AppData.getContext().getString(R.string.bby_scan_url));
		bbyScanApiKey = prefs.getString("bbyScanApiKey", AppData.getContext().getString(R.string.bby_scan_api_key));
	
//		commerceDomain = prefs.getString("commerceDomain", AppData.getContext().getString(R.string.commerce_domain));
//		commerceHost = prefs.getString("commerceHost", AppData.getContext().getString(R.string.commerce_api_url));
//		commercePath = prefs.getString("commercePath", AppData.getContext().getString(R.string.commerce_path));
		
		commerceDomain = prefs.getString(EnvironmentConstants.MODULE_NAME1+EnvironmentConstants.TAG_DOMAIN, AppData.getContext().getString(R.string.commerce_domain));
		commerceHost = prefs.getString(EnvironmentConstants.MODULE_NAME1+EnvironmentConstants.TAG_HOST, AppData.getContext().getString(R.string.commerce_api_url));
		commercePath = prefs.getString(EnvironmentConstants.MODULE_NAME1+EnvironmentConstants.TAG_PATH, AppData.getContext().getString(R.string.commerce_path));
	
		//useProxy = prefs.getBoolean("useProxy", Boolean.parseBoolean(AppData.getContext().getString(R.string.use_proxy)));
		useProxy=Boolean.parseBoolean(prefs.getString(EnvironmentConstants.PROXY_SELECTION, AppData.getContext().getString(R.string.use_proxy)));
		
//		mDotHost = prefs.getString("mDotHost", AppData.getContext().getString(R.string.mdot_url_production));
//		mDotSignInHost = prefs.getString("mDotSignInHost", AppData.getContext().getString(R.string.mdot_signin_url_production));
		
		mDotHost = prefs.getString(EnvironmentConstants.MODULE_NAME4+EnvironmentConstants.TAG_HOST, AppData.getContext().getString(R.string.mdot_url_production));
		mDotURL = prefs.getString(EnvironmentConstants.MODULE_NAME4+EnvironmentConstants.TAG_HOST, AppData.getContext().getString(R.string.mdot_url_production));
		mDotSignInHost = prefs.getString(EnvironmentConstants.MODULE_NAME4+EnvironmentConstants.TAG_SECUREHOST, AppData.getContext().getString(R.string.mdot_signin_url_production));
	
//		AppData.setProxyDomain(prefs.getString("proxyDomain", AppData.getProxyDomain()));
//		AppData.setProxyPassword(prefs.getString("proxyPassword", AppData.getProxyPassword()));
//		AppData.setProxyServer(prefs.getString("proxyServer", AppData.getProxyServer()));
//		AppData.setProxyUsername(prefs.getString("proxyUsername", AppData.getProxyUsername()));
		
		AppData.setProxyDomain(prefs.getString(EnvironmentConstants.PROXY_DOMAIN, AppData.getProxyDomain()));
		AppData.setProxyPassword(prefs.getString(EnvironmentConstants.PROXY_PASSWORD, AppData.getProxyPassword()));
		AppData.setProxyServer(prefs.getString(EnvironmentConstants.PROXY_HOST, AppData.getProxyServer()));
		AppData.setProxyUsername(prefs.getString(EnvironmentConstants.PROXY_USERNAME, AppData.getProxyUsername()));
		
		ugradeCheckerApiKey = prefs.getString("ugradeCheckerApiKey", AppData.getContext().getString(R.string.upgrade_checker_api_key));
		upgradeCheckerSourceSystemValue = prefs.getString("upgradeCheckerSourceSystemValue", AppData.getContext().getString(R.string.upgrade_checker_source_system));
		upgradeCheckerDemoURL = prefs.getString("upgradeCheckerDemoURL", AppData.getContext().getString(R.string.upgrade_checker_demo_url));
		upgradeCheckerProdURL = prefs.getString("upgradeCheckerProdURL", AppData.getContext().getString(R.string.upgrade_checker_prod_url));
		
		storeLocatorMapURL=prefs.getString("storeLocatorMapURL", AppData.getContext().getString(R.string.storelocator_map_url));
		facebookPostDefaultURL=prefs.getString("facebookPostDefaultURL",AppData.getContext().getString(R.string.facebook_post_default_link));
		bbyIQEnginesURL= prefs.getString("bbyIQEnginesURL",AppData.getContext().getString(R.string.bby_iqengines_url));
		apiIQEnginesURL=prefs.getString("apiIQEnginesURL",AppData.getContext().getString(R.string.iqengines_api_url));
		fbDialogBaseURL=prefs.getString("fbDialogBaseURL",AppData.getContext().getString(R.string.fb_dialog_base_url));
		fbDialogGraphURL=prefs.getString("fbDialogGraphURL",AppData.getContext().getString(R.string.fb_graph_base_url));
		fbRestServerURL=prefs.getString("fbRestServerURL",AppData.getContext().getString(R.string.fb_restserver_url));
		mDOTMainURL=prefs.getString("mDOTMainURL", AppData.getContext().getString(R.string.mdot_url));	
	}

	

	/* Configuration Values */
	public static String getRemixApiKey()
	{
		return AppData.getContext().getString(R.string.remix_api_key);
	}
	
	public static boolean isTest() {
		if (AppData.getContext().getString(R.string.is_test).equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isSplash() {
		if (AppData.getContext().getString(R.string.is_splash).equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isOmnitureDevSuite() {
		if (AppData.getContext().getString(R.string.is_omniture_devsuite).equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isGZIP() {
		if (AppData.getContext().getString(R.string.enable_gzip).equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean enableLogging() {
		return isTest();
	}

	public static String getHiddenDataDirectory() {
		return AppData.getContext().getString(R.string.hidden_data_directory);
	}

	public static String getUUID() {
		String retVal = "";
		TelephonyManager mTelephonyMgr = (TelephonyManager) AppData.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		retVal = mTelephonyMgr.getDeviceId();
		return retVal;
	}
	
	public static String getEncryptedDeviceId() {
		String uuid = AppConfig.getUUID();
		String encryptedId = null;
		try {
			encryptedId = EncryptUtil.encrypt(AppData.getEncryptionKey(), uuid);
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return encryptedId;
	}
	
	public static String getHashedDeviceId() {
		String uuid = AppConfig.getUUID();
		String hashedDeviceId = null;
		try {
			hashedDeviceId = EncryptUtil.getHashedDeviceId(uuid);
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return hashedDeviceId;
	}

	public static String getDevice() {
		return android.os.Build.MODEL;
	}

	public static String getPlatform() {
		return "Android-" + android.os.Build.VERSION.RELEASE;
	}
	
	public static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getCarrier() {
		String apn = "";
		Cursor mCursor = AppData.getContext().getContentResolver().query(Uri.parse("content://telephony/carriers"),
				new String[] { "name" }, "current=1", null, null);
		if (mCursor != null) {
			try {
				if (mCursor.moveToFirst()) {
					apn = mCursor.getString(0);
				}
			} finally {
				mCursor.close();
			}
		}
		return apn;
	}

	public static String getAppName() {
		return "BestBuy-Version" + AppData.getVersionName(BestBuyApplication.class);
	}
	
	public static String getAppVersion() {
		return AppData.getVersionName(BestBuyApplication.class);
	}

	/* End Configuration Values */

	public static String getReviewsURL() {
		return AppData.getContext().getString(R.string.reviews_host);
	}
	
	/* Commerce */
	public static String getCommerceDomain() {
		return commerceDomain;
	}

	public static String getCommerceHost() {
		return commerceHost;
	}

	public static String getCommercePath() {
		return commercePath;
	}

	public static boolean isProxy() {
		return useProxy;
	}

	/* End Commerce */

	/* Start Environment */

	public static String getBbyListHost() {
		return bbyListHost;
	}

	public static String getBbyListAPIKey() {
		return bbyListApiKey;
	}

	public static String getBbyOfferURL() {
		return bbyOfferHost;
	}

	public static String getBbyOfferApiKey() {
		return bbyOfferApiKey;
	}

	public static String getSmalHost() {
		return smalHost;
	}

	public static String getSmalSecureHost() {
		return smalSecureHost;
	}

	public static String getSmalApiKey() {
		return smalApiKey;
	}

	public static String get301SecureAPIKey() {
		return smalSecureApiKey;
	}
	
	public static String getMdotURL() {
		return mDotURL;
	}
	
	public static String getMdotSignInURL() {
		return mDotSignInHost;
	}

	// TODO find all references .. check /
	/* RZ Values */
	public static String getSecureRemixURL() {
		return AppData.getContext().getString(R.string.secure_remix_host);
	}

	public static String getRemixURL() {
		return AppData.getContext().getString(R.string.remix_host);
	}
	
	public static String getRemixAuthorizeHost() {
		return AppData.getContext().getString(R.string.remix_authorize_host);
	}

	/* Calculated Values */
	public static String getCommerceStartURL() {
		return getCommerceHost() + getCommercePath();
	}

	public static String getRewardZoneAuthorizeUrl() {
		return getRemixAuthorizeHost() + AppData.REWARD_ZONE_AUTHORIZE_PATH;
	}

	public static String getRewardZoneRequestUrl() {
		return getSecureRemixURL() + AppData.REWARD_ZONE_REQUEST_PATH;
	}

	public static String getRewardZoneAccessUrl() {
		return getSecureRemixURL() + AppData.REWARD_ZONE_ACCESS_PATH;
	}

	public static String getBbyScanHost() {
		return bbyScanHost;
	}

	public static String getBbyScanAPIKey() {
		return bbyScanApiKey;
	}

	public static void setBbyListHost(String bbyListHost) {
		AppConfig.bbyListHost = bbyListHost;
	}

	public static void setBbyListApiKey(String bbyListApiKey) {
		AppConfig.bbyListApiKey = bbyListApiKey;
	}

	public static void setBbyOfferApiKey(String bbyOfferApiKey) {
		AppConfig.bbyOfferApiKey = bbyOfferApiKey;
	}

	public static void setSmalHost(String smalHost) {
		AppConfig.smalHost = smalHost;
	}

	public static void setSmalSecureHost(String smalSecureHost) {
		AppConfig.smalSecureHost = smalSecureHost;
	}

	public static void setSmalApiKey(String smalApiKey) {
		AppConfig.smalApiKey = smalApiKey;
	}

	public static void setSmalSecureApiKey(String smalSecureApiKey) {
		AppConfig.smalSecureApiKey = smalSecureApiKey;
	}

	public static void setBbyScanHost(String bbyScanHost) {
		AppConfig.bbyScanHost = bbyScanHost;
	}

	public static String getActionApiURL(){
		return AppData.getContext().getString(R.string.action_api_host);
	}
	
	public static String getActionApiKey(){
		return AppData.getContext().getString(R.string.action_api_key);
	}

	public static void setBbyScanApiKey(String bbyScanApiKey) {
		AppConfig.bbyScanApiKey = bbyScanApiKey;
	}

	public static void setBbyOfferHost(String bbyOfferHost) {
		AppConfig.bbyOfferHost = bbyOfferHost;
	}

	public static void setCommerceHost(String commerceHost) {
		AppConfig.commerceHost = commerceHost;
	}

	public static void setCommerceDomain(String commerceDomain) {
		AppConfig.commerceDomain = commerceDomain;
	}

	public static void setCommercePath(String commercePath) {
		AppConfig.commercePath = commercePath;
	}

	public static void setUseProxy(boolean useProxy) {
		AppConfig.useProxy = useProxy;
	}

	public static String getBbyRzOfferURL() {
		return bbyrzOfferHost;
	}
	
	public static String getBestbuyStoresURL() {
		return AppData.getContext().getString(R.string.bestbuy_stores_host);
	}
	
	public static String getBestbuyRebateURL() {
		return AppData.getContext().getString(R.string.bestbuy_rebate_host);
	}
	
	public static String getBestbuyOpenboxAPIKey() {
		return AppData.getContext().getString(R.string.openbox_api_key);
	}
	
	public static void setmDotURL(String mDotURL) {
		AppConfig.mDotURL = mDotURL;
	}
	
	public static void setmDotSignInHost(String mDotSignIn) {
		AppConfig.mDotSignInHost = mDotSignIn;
	}
	
	public static String getUgradeCheckerApiKey() {
		return ugradeCheckerApiKey;
	}

	public static String getUpgradeCheckerSourceSystemValue() {
		return upgradeCheckerSourceSystemValue;
	}
	
	public static String getupgradeCheckerDemoURL(){
		return upgradeCheckerDemoURL;
	}
	
	public static String getupgradeCheckerProdURL(){
		return upgradeCheckerProdURL;
	}
	
	public static String getAppCenterHost() {
		return AppData.getContext().getString(R.string.appcenter_host);
	}
	
	public static String getPushNotificationAPIHost(){
		return AppData.getContext().getString(R.string.push_notification_api_host);
	}
	
	public static String getPushNotificationDetailsHost(){
		return AppData.getContext().getString(R.string.push_notification_details_host);
	}
	
	public static String getPushNotificationAPIKey(){
		return AppData.getContext().getString(R.string.pn_api_key);
	}
	
	public static String getStoreLocatorMapURL() {
		return storeLocatorMapURL;
	}
	public static String getFacebookPostDefaultURL(){
		return facebookPostDefaultURL;
	}
	
	public static String getBBYIQEnginesURL(){
		return bbyIQEnginesURL;
	}
	public static String getApiIQEnginesURL(){
		return apiIQEnginesURL;
	}
	
	public static String getFBDialogBaseURL(){
		return fbDialogBaseURL;
	}
	public static String getFBDialogGraphURL(){
		return fbDialogGraphURL;
	}
	
	public static String getFBRestServerURL(){
		return fbRestServerURL;
	}
	public static String getMdotMainURL(){
		return mDOTMainURL;
	}
	
	public static String getEcoRebatesUrl(){
		return AppData.getContext().getString(R.string.eco_rebates_url);
	}
	public static void  printValues(){
		BBYLog.i("AppConfig","***********Proxy Values*************");
		BBYLog.i("AppConfig","Proxy Flag :"+useProxy);
		
		BBYLog.i("AppConfig","Proxy Domain :"+AppData.getProxyDomain());
		BBYLog.i("AppConfig","Proxy Host :"+AppData.getProxyServer());
		BBYLog.i("AppConfig","Proxy UserName :"+AppData.getProxyUsername());
		BBYLog.i("AppConfig","Proxy PassWord :"+AppData.getProxyPassword());
		BBYLog.i("AppConfig","************************************");
		
		BBYLog.i("AppConfig","bbyOfferHost :"+AppConfig.getBbyOfferURL());
		BBYLog.i("AppConfig","bbyOfferApiKey :"+AppConfig.getBbyOfferApiKey());
		
		BBYLog.i("AppConfig","smalHost :"+AppConfig.getSmalHost());
		BBYLog.i("AppConfig","smalSecureHost :"+AppConfig.getSmalSecureHost());
		BBYLog.i("AppConfig","smalApiKey :"+AppConfig.getSmalApiKey());
		BBYLog.i("AppConfig","smalSecureApiKey :"+AppConfig.get301SecureAPIKey());
		
		BBYLog.i("AppConfig","commerceDomain :"+AppConfig.getCommerceDomain());
		BBYLog.i("AppConfig","commerceHost :"+AppConfig.getCommerceHost());
		BBYLog.i("AppConfig","commercePath :"+AppConfig.getCommercePath());
		
		BBYLog.i("AppConfig","mDotHost :"+AppConfig.getMdotURL());
		BBYLog.i("AppConfig","mDotSignInHost :"+AppConfig.getMdotSignInURL());
	}

	public static String getmDotHost() {
		return mDotHost;
	}

	public static void setmDotHost(String mDotHost) {
		AppConfig.mDotHost = mDotHost;
	}
}
