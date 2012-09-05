package com.bestbuy.android.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Environment;

import com.bestbuy.android.activity.ProductCompare;
import com.bestbuy.android.data.actionapi.AApiClient;
import com.bestbuy.android.data.commerce.CCommerce;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheDBAdapter;
import com.bestbuy.android.util.EncryptUtil;

/**
 * Contains necessary data to be shared between activities
 * 
 * @author Recursive Awesome
 * 
 */
public class AppData {

	private static final String TAG = "AppData";

	private static Context context;
	public static boolean enableConnectivity = true;

	private List<Offer> offers;
	private Product selectedProduct;
	private Warranty selectedWarranty;
	private SearchRequest searchRequest;
	private Map<String, List<Category>> categoryList;
	private static Location location;
	private BBYLocationManager bbyLocationManager;
	private List<Store> stores;
	private int searchSortPosition;
	private DealCategory dealCategory;
	private ArrayList<Product> compareProducts;
	private ArrayList<Product> scannedProducts;
	private ArrayList<Product> savedProducts;
	private ArrayList<Product> recentProducts;
	private Vector<PhotoSearch> photoSearches;
	private RZAccount rzAccount;
	private CCommerce commerce;
	private AApiClient actionApiClient;
	private static CacheDBAdapter dbAdapter;
	private NotificationManager notificationManager;
	private Product scannedProduct;
	private String zip;
	private boolean isCompareMode;
	private Store connectedStore;

	private static Map<String, String> globalConfig;
	private static boolean isSplashScreen;
	// private static boolean isHomeScreenBackPressed = false;

	private String deviceModel;
	private String searchResultsort;

	private OAuthAccessor oAuthAccessor;

	private static boolean isPNtrayopen = false;
	private boolean isReceivedUAid = false;
	private static String optIn="on";
	private static boolean optinStatus;
	private static boolean apiKeyStatus;
	private static boolean regwithPNStatus;
	private static String uaAppId;
	public static final String OPTIN_STATE = "optinstate";

	private List<StoreEvents> storeEvents;
	private List<String> selectedCategoryIds;

	public static final String _301_ITEMS = "items";

	private static final String OAUTH_ACCESSOR_REQUEST_TOKEN = "OAUTH_ACCESSOR_REQUEST_TOKEN";
	private static final String OAUTH_ACCESSOR_ACCESS_TOKEN = "OAUTH_ACCESSOR_ACCESS_TOKEN";
	private static final String OAUTH_ACCESSOR_TOKEN_SECRET = "OAUTH_ACCESSOR_TOKEN_SECRET";
	public static final String API_REMIX_PRODUCTS = "products";
	public static final String API_OFFER_PRODUCTS = "offers";
	public static final String SEARCH_INTENT_KEY = "searchIntentKey";
	public static final String CATEGORY_PATH_NAME_INTENT_KEY = "departmentIntentKey";
	public static final String PRODUCT_SELECTION_INTENT_KEY = "productSelectionIntentKey";
	public static final String JSON_NULL = "null";
	public static final String BBY_SERV_API_USERNAME = "recursiveawesome";
	public static final String BBY_SERV_API_PASSWORD = "tm99er$rm";
	public static final String BBY_OFFERS_PATH = "api/v1/offer";
	public static final String BBY_RZ_OFFERS_PATH = "api/offers/singleusercoupon";
	public static final String BBY_RZ_OPTIN_OFFER_PATH = "api/offer/?/acknowledge";
	public static final String BBY_OFFERS_DEPARTMENT_PATH = "api/v1/department";
	public static final String BBY_OFFERS_DATA = "data";
	public static final String BBY_FACETED_PATH = "api/V1/products/faceted_search";
	public static final String BBY_OFFERS_CATEGORY_LIST = "offerCategories";
	public static final String BBY_SERV_CHANNELS = "bby-android";
	public static final String BBY_SERV_RZ_CHANNELS = "rz-mobile";
	public static final String BBY_OFFERS_MOBILE_FEATURED_OFFERS_CHANNEL = "mobile-featured-offers";
	public static final String BBY_OFFERS_MOBILE_WEEKLY_AD_CHANNEL = "bby-mobile-weekly-ad";
	public static final String BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL = "bby-mobile-special-offers";
	public static final String BBY_ICR_PRICING_HOST = "http://images.bestbuy.com";
	public static final String BBY_ICR_PRICING_PATH = "BestBuy_US/js/icrskulist.json";
	// /http://reviews.bestbuy.com
	// public static final String REMIX_HOST = "api.remix.bestbuy.com/v1";
	// public static final String REMIX_URL = "http://" + REMIX_HOST;
	// public static final String REVIEWS_HOST = "reviews.bestbuy.com/3545";
	// public static final String REVIEWS_URL = "http://" + REVIEWS_HOST;
	public static final String CART_SERIAL_FILE_PATH = "bestbuy-android-cart";
	public static final String MOTRACKER_APP_ID = "wk8os2ex";
	public static final String ROOT_REMIX_CATEGORY = "cat00000";
	public static final String _301_ROOT_CATEGORY = "ROOT";
	public static final String CATEGORIES = "categories";
	public static final String SUB_CATEGORIES = "subCategories";
	public static final String ROOT_REMIX_CATEGORIES_PATH = "categories(id="
			+ ROOT_REMIX_CATEGORY + ")";
	public static final String _301_CATEGORIES_PATH = "api/V1/categories";
	public static final String _301_APP_LAUNCH_PATH = "api/V1/events/log/app_launch";
	public static final String _301_CONFIG_GLOBAL_HASH = "api/V1/config/global";
	public static final String _301_ALERTS_PATH = "api/V1/alerts";
	public static final String _301_RECOMMENDATIONS_PATH = "api/V1/products/recommendations";
	public static final String _301_TERMS_AND_CONDITIONS_PATH = "api/V1/plans/terms_and_conditions";
	public static final String CATEGORY_ID = "categoryId";
	public static final String CATEGORY_NAME = "categoryName";
	public static final String REMIX_CATEGORY_ID = "remixCategoryId";
	public static final String STORES = "stores";

	public static final String STORE_SEARCH_RADIUS = "50";
	public static final String INDEX = "index";
	public static final String DEALS_HOST = "buyphone.bbyserv.com";
	public static final String DEALS_URL = "http://" + DEALS_HOST;
	public static final String DEALS_PATH = "circular/current";
	public static final String DATA_LOADED = "dataLoaded";
	public static final String SELECTED_PRODUCT = "selectedProduct";
	public static final String SEARCH_REQUEST = "searchRequest";
	public static final String NOTIFICATIONS = "notifications";
	public static final String QRCODE_ALERT = "qrcodeAlert";
	public static final String RATING_REMINDER = "ratingReminder";
	public static final String LAUNCH_COUNT = "launchCount";
	public static final String DISMISSED_ALERT_MESSAGE_IDS = "dismissedMessageIds";
	public static final String SCANNED_SKUS = "scannedSkus";
	public static final String COMPARE_SKUS = "scannedSkus";
	public static final String RATED_SKUS = "ratedSkus";
	public static final String RECENT_SKUS = "recentSkus";
	public static final String LIKED_SKUS = "likedSkus";
	public static final String GAME_LIBRARY_SKUS = "gameLibrarySkus";
	public static final String PREVIOUS_NOTIFICATIONS = "previousNotifications";
	public static final String PHOTO_SEARCHES = "photoSearches";
	public static final String PREFERRED_CATEGORIES = "preferredCategories";
	public static final String PAGE_0_GRID_IDS = "page0GridIds";
	public static final String PAGE_1_GRID_IDS = "page1GridIds";
	public static final String SHARED_PREFS = "sharedPrefs";
	public static final String APP_VERSION = "appVersion";
	public static final String STORES_HOST = "http://stores.bestbuy.com";
	public static final String LAUNCH_MEDIA_QR_AVAILABLE = "launchMediaQRAvailable";
	public static final boolean EMAIL_ON_FORCE_CLOSE = false;
	public static final String BITLY_USER = "bbyandroid";
	public static final String BITLY_KEY = "R_91551506be657b910da7c0a7153dacbc";
	public static final String BITLY_URL = "http://api.bit.ly";
	public static final String BITLY_PATH = "v3/shorten/";
	public static final String REWARD_ZONE_DATA_PATH = "/v1/parties/me.xml";
	public static final String REWARD_ZONE_PURCHASES_PATH = "/v1/parties/me/transactions.xml";
	public static final String REWARD_ZONE_REQUEST_PATH = "/oauth/request_token";
	public static final String REWARD_ZONE_AUTHORIZE_PATH = "/authorizations";
	public static final String REWARD_ZONE_ACCESS_PATH = "/oauth/access_token";
	public static final String IQENGINES_KEY = "eaf97231d4a8441fa83a8bb14a33c2dd";
	public static final String IQENGINES_SECRET = "4ee56dafd5924b47b0a6fb222ecf5673";
	public static final int MAX_PHOTO_SEARCHES = 10;
	public static final String COMMERCE_API_KEY = null;
	public static final String COMMERCE_USERNAME = "commerceUsername";
	public static final String BBY_LIST_ACCOUNT_PATH = "api/v1/account";
	public static final String BBY_LIST_WISHLIST_PATH = "api/v1/wishlist";
	public static final String BBY_LIST_LISTITEM_PATH = "api/v1/listitem";
	public static final String BBY_ACTION_API_PATH = "v1/targets/";
	public static final String BBY_ACTION_API_GET_PATH = "/actions";
	public static final String BBY_ACTION_API_RP_PATH = "/actions/offers/products/";
	public static final String BBY_ACTION_API_LP_PATH = "/actions/lifecycles/phases/";
	public static final String BBY_ACTION_API_UA_JSON_PATH = "/userActions.json";
	public static final String BBY_BROWSE_CATEGORY_FILE = "/data/data/com.bestbuy.android/browse_history.obj";
	public static String proxyUsername = "a188312";
	public static String proxyPassword = "Awesome3";
	public static String proxyDomain = "na";
	public static String proxyServer = "usproxy.na.bestbuy.com";
	public static final int PROXY_PORT = 8080;

	public static final String BBY_EVENT_SHARE_PATH = "/events/";
	public static final String BBY_STORE_EVENTS_PATH = "/d/events/output/";
	public static final String BBY_DEALS_NEARME_PATH = "/deals-near-me/";

	public static final Double CONNECTED_STORE_DISTANCE = 0.0; // TODO change to
																// .5

	
	// Needed for displaying cart count
	public static final String CART_QTY = "cartQuantity";

	// Needed for displaying the corresponding screen of dragable gallery
	public static final String HOME_SELECTED_PAGE = "homePageView";

	// Needed for displaying the corresponding screen of preferred categories
	// after done
	public static final String PREFERED_CATEGORIES_ONRETURN = "preferredcatagoriesonreturn";

	// Needed for displaying the corresponding screen of preferred categories
	// after done
	public static final String LAST_USED_LOCATION = "LAST_LOCATION_USED";

	// Upgrade Checker phone number cache
	public static final String ATT_UPGRADE_CHECKER_PHONE_NUMBER = "AttUpgradeCheckerPhoneNumber";
	public static final String SPRINT_UPGRADE_CHECKER_PHONE_NUMBER = "SprintUpgradeCheckerPhoneNumber";
	public static final String VERIZON_UPGRADE_CHECKER_PHONE_NUMBER = "VerizonUpgradeCheckerPhoneNumber";
	public static final String TMOBILE_UPGRADE_CHECKER_PHONE_NUMBER = "TmobileUpgradeCheckerPhoneNumber";

	public static final String MAINTANANCE_SHUTDOWN = "maintenance_shutdown";
	public static final String MAINTANANCE_SPLASH = "maintenance_splash";
	public static final String MAINTANANCE_SPLASH_RETINA = "maintenance_splash_retina";

	public static final String PRODUCT_SEARCH_QUERY = "PRODUCT_SEARCH_QUERY";
	public static final String IS_CODE_SCAN = "Is_Code_Scan";

	// push notification
	public static final String PUSHNOTIFICATION_REWARDZONE_STATUS = "pushnotificationRewardZoneStatus";
	public static final String PUSHNOTIFICATION_WEEKLYOFFERS_STATUS = "pushnotificationWeeklyoffersStatus";
	public static final String PUSH_NOTIFICATION_PREFERRED_CATEGORIES = "pnpreferredCategories";
	public static final String PUSH_NOTIFICATION_OPTIN_ON = "on";
	private boolean isChangeInPreferredCat = false;

	
	
	public AppData(Context context) {
		AppData.context = context;
		setDeviceInfo();
		compareProducts = new ArrayList<Product>();
		rzAccount = new RZAccount();
		commerce = new CCommerce();
		actionApiClient = new AApiClient();
		globalConfig = new HashMap<String, String>();
	}

	private void setDeviceInfo() {
		if (context != null) {
			deviceModel = android.os.Build.MODEL;
		}
	}

	public static String getVersionName(Class<?> cls) {
		try {
			ComponentName comp = new ComponentName(AppData.getContext(), cls);
			PackageInfo pinfo = AppData.getContext().getPackageManager()
					.getPackageInfo(comp.getPackageName(), 0);
			return pinfo.versionName;
		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
			BBYLog.printStackTrace(TAG, e);
			return "Version 1.0";
		}
	}

	public static Context getContext() {
		return context;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	public List<Offer> getOffers() {
		return offers;
	}

	public Product getOffer(int id) {
		return offers.get(id);
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public Warranty getSelectedWarranty() {
		return selectedWarranty;
	}

	public void setSelectedWarranty(Warranty selectedWarranty) {
		this.selectedWarranty = selectedWarranty;
	}

	public SearchRequest getSearchRequest() {
		return searchRequest;
	}

	public void setSearchRequest(SearchRequest searchRequest) {
		this.searchRequest = searchRequest;
	}

	public String getSearchResultSort() {
		if (searchResultsort == null) {
			searchResultsort = "";
		}
		return searchResultsort;
	}

	public void setSearchResultSort(String searchResultSort) {
		this.searchResultsort = searchResultSort;
	}

	public int getSearchSortPosition() {
		return searchSortPosition;
	}

	public void setSearchSortPosition(int searchSortPosition) {
		this.searchSortPosition = searchSortPosition;
	}

	public List<Category> getFromCategoryList(String id) {
		if (this.categoryList == null || this.categoryList.values() == null) {
			try {
				CategoryUtilities.loadCategory(context, null, this);
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				BBYLog.e(
						TAG,
						"Exception AppData.getCategoryList() : "
								+ ex.getMessage());
				// set empty list if not, cause null pointer up the chain.
				this.categoryList = new HashMap<String, List<Category>>();
				this.categoryList.put(id, new ArrayList<Category>());
			}
		}
		return this.categoryList.get(id);
	}

	public boolean isCategoryListEmpty() {
		return this.categoryList != null && this.categoryList.isEmpty();
	}

	public void addToCategoryList(String id, List<Category> categories) {
		if (this.categoryList == null) {
			this.categoryList = new HashMap<String, List<Category>>();
		}
		this.categoryList.put(id, categories);
	}

	public static void setLocation(Location l) {
		location = l;
	}

	public static Location getLocation() {
		return location;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getZip() {
		return zip;
	}

	public void setScannedProduct(Product scannedProduct) {
		this.scannedProduct = scannedProduct;
	}

	public Product getScannedProduct() {
		return scannedProduct;
	}

	public BBYLocationManager getBBYLocationManager() {
		if (bbyLocationManager == null) {
			bbyLocationManager = new BBYLocationManager();
		}
		return bbyLocationManager;
	}

	public void setStores(List<Store> stores) {
		this.stores = stores;
	}

	public List<Store> getStores() {
		if (stores == null) {
			stores = new ArrayList<Store>();
		}
		return stores;
	}

	public Store getConnectedStore() {
		return connectedStore;
	}

	public void setConnectedStore(Store connectedStore) {
		this.connectedStore = connectedStore;
	}

	public void setStoreEvents(List<StoreEvents> storeEvents) {
		this.storeEvents = storeEvents;
	}

	public List<StoreEvents> getStoreEvents() {
		return storeEvents;
	}

	public void setSelectedDealCategory(DealCategory dealCategory) {
		this.dealCategory = dealCategory;
	}

	public DealCategory getDealCategory() {
		return dealCategory;
	}

	public void addCompareProduct(Product product) {
		if (!compareProducts.contains(product)
				&& compareProducts.size() < ProductCompare.MAX_PRODUCTS) {
			compareProducts.add(product);
		}
	}

	public ArrayList<Product> getCompareProducts() {
		return compareProducts;
	}

	public void clearCompareProducts() {
		compareProducts.clear();
	}

	public RZAccount getRzAccount() {
		return rzAccount;
	}

	public void setRzAccount(RZAccount rzAccount) {
		this.rzAccount = rzAccount;
	}

	public CCommerce getCommerce() {
		if (commerce == null) {
			commerce = new CCommerce();
		}
		return commerce;
	}

	public AApiClient getActionApiClient() {
		if (actionApiClient == null) {
			actionApiClient = new AApiClient();
		}
		return actionApiClient;
	}

	public void setCommerce(CCommerce commerce) {
		this.commerce = commerce;
	}

	public static String getProxyUsername() {
		return proxyUsername;
	}

	public static void setProxyUsername(String proxyUsername) {
		AppData.proxyUsername = proxyUsername;
	}

	public static String getProxyPassword() {
		return proxyPassword;
	}

	public static void setProxyPassword(String proxyPassword) {
		AppData.proxyPassword = proxyPassword;
	}

	public static String getProxyDomain() {
		return proxyDomain;
	}

	public static void setProxyDomain(String proxyDomain) {
		AppData.proxyDomain = proxyDomain;
	}

	public static String getProxyServer() {
		return proxyServer;
	}

	public static void setProxyServer(String proxyServer) {
		AppData.proxyServer = proxyServer;
	}

	public static List<String> getNoSaleSkus() {
		String skus = globalConfig.get("NoSaleSkus");
		if (skus == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(skus.split(" "));
	}

	public OAuthAccessor getOAuthAccessor() {
		if (oAuthAccessor != null) {
			return oAuthAccessor;
		} else {
			SharedPreferences settings = context.getSharedPreferences(
					AppData.SHARED_PREFS, 0);
			OAuthAccessor tmpAuthAccessor = createOAuthAccessor();
			String requestToken = settings.getString(
					OAUTH_ACCESSOR_REQUEST_TOKEN, tmpAuthAccessor.requestToken);
			BBYLog.i(TAG, "Request Token: " + requestToken);
			if (requestToken == null) {
				return null;
			} else {
				try {
					tmpAuthAccessor.requestToken = EncryptUtil.decrypt(
							AppData.getEncryptionKey(), requestToken);
					BBYLog.i(TAG, "tmpAuthAccessor.requestToken: "
							+ tmpAuthAccessor.requestToken);

					tmpAuthAccessor.accessToken = EncryptUtil.decrypt(AppData
							.getEncryptionKey(), settings.getString(
							OAUTH_ACCESSOR_ACCESS_TOKEN, "0"));
					BBYLog.i(TAG, "tmpAuthAccessor.accessToken: "
							+ tmpAuthAccessor.accessToken);

					tmpAuthAccessor.tokenSecret = EncryptUtil.decrypt(AppData
							.getEncryptionKey(), settings.getString(
							OAUTH_ACCESSOR_TOKEN_SECRET, "0"));
					BBYLog.i(TAG, "tmpAuthAccessor.tokenSecret: "
							+ tmpAuthAccessor.tokenSecret);
					oAuthAccessor = tmpAuthAccessor;
				} catch (Exception ex) {
					BBYLog.printStackTrace(TAG, ex);
					BBYLog.e(TAG, "Exception in AppData.getOAuthAccessor() : "
							+ ex.getMessage());
				}
			}
		}
		return oAuthAccessor;
	}

	public static OAuthAccessor createOAuthAccessor() {

		String consumerKey = AppData.getGlobalConfig()
				.get("oauth_consumer_key");
		String consumerSecret = AppData.getGlobalConfig().get(
				"oauth_consumer_secret");

		if (consumerKey == null && consumerSecret == null) {
			// Retry the call.
			try {
				AppData.fetchGlobalConfig();
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}
		}

		String callbackUrl = AppConfig.getRemixURL();
		String reqUrl = AppConfig.getRewardZoneRequestUrl();
		String authzUrl = AppConfig.getRewardZoneAuthorizeUrl();
		String accessUrl = AppConfig.getRewardZoneAccessUrl();

		OAuthServiceProvider provider = new OAuthServiceProvider(reqUrl,
				authzUrl, accessUrl);
		OAuthConsumer consumer = new OAuthConsumer(callbackUrl, consumerKey,
				consumerSecret, provider);

		return new OAuthAccessor(consumer);
	}

	public void setOAuthAccessor(OAuthAccessor oAuthAccessor) {
		SharedPreferences settings = context.getSharedPreferences(
				AppData.SHARED_PREFS, 0);
		String encryptKey = AppData.getEncryptionKey();
		try {
			String encryptedRequestToken = EncryptUtil.encrypt(encryptKey,
					oAuthAccessor.requestToken);
			String encryptedAccessToken = EncryptUtil.encrypt(encryptKey,
					oAuthAccessor.accessToken);
			String encryptedTokenSecret = EncryptUtil.encrypt(encryptKey,
					oAuthAccessor.tokenSecret);

			settings.edit()
					.putString(OAUTH_ACCESSOR_REQUEST_TOKEN,
							encryptedRequestToken).commit();
			settings.edit()
					.putString(OAUTH_ACCESSOR_ACCESS_TOKEN,
							encryptedAccessToken).commit();
			settings.edit()
					.putString(OAUTH_ACCESSOR_TOKEN_SECRET,
							encryptedTokenSecret).commit();
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(
					TAG,
					"Exception in AppData.setOAuthAccessor() : "
							+ ex.getMessage());
		}
		this.oAuthAccessor = oAuthAccessor;
	}

	public void clearOAuthAccessor() {
		oAuthAccessor = null;
		SharedPreferences settings = context.getSharedPreferences(
				AppData.SHARED_PREFS, 0);
		settings.edit().remove(OAUTH_ACCESSOR_REQUEST_TOKEN).commit();
		settings.edit().remove(OAUTH_ACCESSOR_ACCESS_TOKEN).commit();
		settings.edit().remove(OAUTH_ACCESSOR_TOKEN_SECRET).commit();
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public ArrayList<Product> getSavedProducts() {
		if (savedProducts == null) {
			savedProducts = new ArrayList<Product>();
		}
		return savedProducts;
	}

	public ArrayList<Product> getScannedProducts() {
		if (scannedProducts == null) {
			scannedProducts = new ArrayList<Product>();
		}
		return scannedProducts;
	}

	public ArrayList<Product> getRecentProducts() {
		if (recentProducts == null) {
			recentProducts = new ArrayList<Product>();
		}
		return recentProducts;
	}

	private void setupPhotoSearches() {
		SharedPreferences settings = context.getSharedPreferences(
				AppData.SHARED_PREFS, 0);
		String savedSearches = settings.getString(AppData.PHOTO_SEARCHES, null);
		photoSearches = new Vector<PhotoSearch>(); // multiple threads might be
													// addng/removing/viewing
													// this list. Using a
													// vector.
		if (savedSearches != null) {
			try {
				JSONArray photosArray = new JSONArray(savedSearches);
				for (int i = 0; i < photosArray.length(); i++) {
					JSONObject photoData = photosArray.getJSONObject(i);
					PhotoSearch photoSearch = new PhotoSearch(
							photoData.getString("id"),
							photoData.getString("qid"),
							photoData.getString("file_path"),
							photoData.getString("description"));
					photoSearches.add(photoSearch);
				}
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				BBYLog.e(
						TAG,
						"Exception loading savedSearches from Preferences - AppData.setupPhotoSearches(): "
								+ ex.getMessage());
			}
		}
	}

	public List<PhotoSearch> getPhotoSearches() {
		if (photoSearches == null) {
			setupPhotoSearches();
		}
		return photoSearches;
	}

	public static Map<String, String> getGlobalConfig() {
		return globalConfig;
	}

	private static void setGlobalConfig(Map<String, String> gc) {
		globalConfig = gc;
	}

	public static String getEncryptionKey() {
		return AppData.getGlobalConfig().get("oauth_consumer_key");
	}

	public static void fetchGlobalConfigSlice() throws Exception {
		Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("api_key", AppConfig.getSmalApiKey());
		APIRequest.makeGetRequest(AppConfig.getSmalHost(),
				_301_APP_LAUNCH_PATH, urlParams, false);
	}

	public static void fetchGlobalConfig() throws Exception {

		// make the call over ssl for global config parameters
		Map<String, String> secureUrlParams = new HashMap<String, String>();
		secureUrlParams.put("api_key", AppConfig.get301SecureAPIKey());
		// TODO: RYAN SET TIME
		String configResult = APIRequest.makeGetRequest(
				AppConfig.getSmalSecureHost(), _301_CONFIG_GLOBAL_HASH,
				secureUrlParams, false, false);

		JSONObject configArray = new JSONObject(configResult);
		@SuppressWarnings("unchecked")
		Iterator<String> iter = configArray.keys();
		Map<String, String> globalConfigFromJSON = new HashMap<String, String>();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String value = configArray.getString(key);
			globalConfigFromJSON.put(key, value);
		}
		setGlobalConfig(globalConfigFromJSON);
	}

	public boolean deletePhotoSearchItem(String qid) {
		if (photoSearches == null) {
			setupPhotoSearches();
		}
		Iterator<PhotoSearch> iter = photoSearches.iterator();
		while (iter.hasNext()) {
			PhotoSearch ps = iter.next();
			if (ps.getQid().equals(qid)) {
				return photoSearches.removeElement(ps);
			}
		}
		return false;
	}

	public void updatePhotoSearchItem(String qid, String description) {
		if (photoSearches == null) {
			setupPhotoSearches();
		}
		Iterator<PhotoSearch> iter = photoSearches.iterator();
		while (iter.hasNext()) {
			PhotoSearch ps = iter.next();
			if (ps.getQid().equals(qid)) {
				ps.setDescription(description);
				ps.setAnalyzing(false);
			}
		}
	}

	public void addPhotoSearch(PhotoSearch photoSearch, boolean addFirst) {
		if (photoSearches == null) {
			setupPhotoSearches();
		}

		if (addFirst) {
			photoSearches.add(0, photoSearch);
		} else {
			photoSearches.add(photoSearch);
		}
	}

	public static File getHiddenDataDirectory() throws Exception {
		return getAppDataDirectory(AppConfig.getHiddenDataDirectory());
	}

	public static SharedPreferences getSharedPreferences() {
		return context.getSharedPreferences(AppData.SHARED_PREFS, 0);
	}

	public static File getAppDataDirectory(String directory) throws Exception {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File appDataDirectory = new File(getAppDataDirectoryPath(directory));

			if (!appDataDirectory.exists()) {
				@SuppressWarnings("unused")
				boolean madeDir = appDataDirectory.mkdirs();
			}

			return appDataDirectory;
		} else {
			throw new Exception("Can't mount media.");
		}
	}

	public static String getAppDataDirectoryPath(String directory)
			throws Exception {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdCard = Environment.getExternalStorageDirectory();
			String path;

			if (directory != null) {
				path = sdCard.getAbsolutePath() + File.separator + "bestbuy"
						+ File.separator + directory; // /mnt/BestBuy
			} else {
				path = sdCard.getAbsolutePath() + File.separator + "bestbuy";
			}

			return path;
		} else {
			throw new Exception("Can't mount media.");
		}
	}

	public static CacheDBAdapter getDBAdapter() {
		if (dbAdapter == null) {
			dbAdapter = new CacheDBAdapter(getContext());
		}
		return dbAdapter;
	}

	public NotificationManager getNotificationManager() {
		if (notificationManager == null) {
			notificationManager = new NotificationManager(this);
		}
		return notificationManager;
	}

	public boolean isCompareMode() {
		return isCompareMode;
	}

	public static String CURRENT_LOCATION_ALLOWEDED = "com.bestbuy.android.data.CURRENT_LOCATION_ALLOWEDED";
	public static final int KEY_EXIST = 0;
	public static final int ALLOWED = 1;
	public static final int NOT_ALLOWED = 2;

	public static boolean isCurrentLocationAlloweded(Context context) {

		/*
		 * Getting force close issues from Market release, since the below line
		 * throws class cast exception and it expects the Boolean value as this
		 * variable holds the boolean value in earlier version and resides in
		 * users device already.
		 */
		// int value = getSharedPreferences().getInt(CURRENT_LOCATION_ALLOWEDED,
		// KEY_EXIST);

		// if(value == ALLOWED)
		// return true;
		Object currentLocationAllowed = getSharedPreferences().getAll().get(
				CURRENT_LOCATION_ALLOWEDED);
		if (currentLocationAllowed instanceof Boolean) {
			return (Boolean) currentLocationAllowed;
		} else if (currentLocationAllowed instanceof Integer) {
			if ((Integer) currentLocationAllowed == ALLOWED)
				return true;
		}

		return false;
	}

	public static void setCurrentLocationAllow(Context context, boolean allow) {
		int value = NOT_ALLOWED;
		if (allow)
			value = ALLOWED;

		getSharedPreferences().edit().putInt(CURRENT_LOCATION_ALLOWEDED, value)
				.commit();
	}

	public static boolean isLocationKeyExist(Context context) {
		if (!getSharedPreferences().contains(CURRENT_LOCATION_ALLOWEDED))
			return false;
		/*
		 * Getting force close issues from Market release, since the below line
		 * throws class cast exception and it expects the Boolean value as this
		 * variable holds the boolean value in earlier version and resides in
		 * users device already.
		 */
		/*
		 * int value = getSharedPreferences().getInt(CURRENT_LOCATION_ALLOWEDED,
		 * KEY_EXIST);
		 * 
		 * if(value == KEY_EXIST){ return false; }
		 */
		Object currentLocationAllowed = getSharedPreferences().getAll().get(
				CURRENT_LOCATION_ALLOWEDED);
		if (currentLocationAllowed instanceof Boolean) {
			return (Boolean) currentLocationAllowed;
		} else if (currentLocationAllowed instanceof Integer) {
			if ((Integer) currentLocationAllowed == KEY_EXIST)
				return false;
		}

		return true;
	}

	public void setCompareMode(boolean isCompareMode) {
		this.isCompareMode = isCompareMode;
	}

	public static boolean isSplashScreen() {
		return isSplashScreen;
	}

	public static void setSplashScreen(boolean isSplashScreen) {
		AppData.isSplashScreen = isSplashScreen;
	}

	/*
	 * public static boolean isHomeScreenBackPressed() { return
	 * isHomeScreenBackPressed; }
	 * 
	 * public static void setHomeScreenBackPressed(boolean
	 * isHomeScreenBackPressed) { AppData.isHomeScreenBackPressed =
	 * isHomeScreenBackPressed; }
	 */

	public static String TRACKING_ALLOWEDED = "TRACKING_ALLOWEDED";

	public static boolean isTrackerActive(Context context) {
		boolean isTrackerAllowed = getSharedPreferences().getBoolean(
				TRACKING_ALLOWEDED, true);

		if (isTrackerAllowed)
			return true;

		return false;
	}

	public static void setTrackerStatus(Context context, boolean allow) {
		getSharedPreferences().edit().putBoolean(TRACKING_ALLOWEDED, allow)
				.commit();
	}

	/*
	 * public void updateCartItemCount(CCommerce mCommerce){ int qty = 0;
	 * ArrayList<CCartItem> cartList=
	 * mCommerce.getOrder().getCart().getCartItems(); for(CCartItem cartItem :
	 * cartList){ qty = qty + Integer.parseInt(cartItem.getQuantity()); }
	 * AppData.getSharedPreferences().edit().putInt(AppData.CART_QTY,
	 * qty).commit(); }
	 */

	public static HashMap<String, String> getOMSOutageData() {
		HashMap<String, String> oMSOutage = new HashMap<String, String>();

		oMSOutage.put(MAINTANANCE_SHUTDOWN,
				globalConfig.get(MAINTANANCE_SHUTDOWN));
		oMSOutage.put(MAINTANANCE_SPLASH_RETINA,
				globalConfig.get(MAINTANANCE_SPLASH_RETINA));
		oMSOutage.put(MAINTANANCE_SPLASH, globalConfig.get(MAINTANANCE_SPLASH));

		return oMSOutage;
	}

	/*
	 * public static boolean isApplicationSentToBackground(Context context) {
	 * ActivityManager am = (ActivityManager) context
	 * .getSystemService(Context.ACTIVITY_SERVICE); List<RunningTaskInfo> tasks
	 * = am.getRunningTasks(1); if (!tasks.isEmpty()) { ComponentName
	 * topActivity = tasks.get(0).topActivity; if
	 * (!topActivity.getPackageName().equals(context.getPackageName())) { return
	 * true; } } return false; }
	 */
	public static void setPNtrayopen(boolean open) {
		isPNtrayopen = open;
	}

	public static boolean getPNtrayopen() {
		return isPNtrayopen;
	}

	public void setReceivedUAid(boolean isReceived) {
		isReceivedUAid = isReceived;
	}

	public boolean getReceivedUAid() {
		return isReceivedUAid;

	}

	public static void setOptin(String optin) {
		optIn = optin;
	}

	public static String getOptin() {
		return optIn;
	}

	public static void setOptinStatus(boolean status) {
		optinStatus = status;
	}

	public static boolean getOptinStatus() {
		return optinStatus;
	}

	public static void setapiKeyStatus(boolean status) {
		apiKeyStatus = status;
	}

	public static boolean getapiKeyStatus() {
		return apiKeyStatus;
	}

	public static void setRegwithPNStatus(boolean status) {
		regwithPNStatus = status;
	}

	public static boolean getRegwithPNStatus() {
		return regwithPNStatus;
	}

	public static void setUAid(String pushId) {
		uaAppId = pushId;
	}

	public static String getUAid() {
		return uaAppId;
	}

	public void setChangesInPnPreferences(boolean ischange) {
		isChangeInPreferredCat = ischange;
	}

	public boolean isChangesInPnPreferences() {
		return isChangeInPreferredCat;
	}

	public void setWoStatus(boolean woStatus) {
		getSharedPreferences()
				.edit()
				.putBoolean(AppData.PUSHNOTIFICATION_WEEKLYOFFERS_STATUS,
						woStatus).commit();
	}

	public boolean getWoStatus() {
		return getSharedPreferences().getBoolean(
				AppData.PUSHNOTIFICATION_WEEKLYOFFERS_STATUS, false);
	}

	public void setRZStatus(boolean rzStatus) {
		getSharedPreferences()
				.edit()
				.putBoolean(AppData.PUSHNOTIFICATION_REWARDZONE_STATUS,
						rzStatus).commit();
	}

	public boolean getRzStatus() {
		return getSharedPreferences().getBoolean(
				AppData.PUSHNOTIFICATION_REWARDZONE_STATUS, false);
	}
	
	public void setPreferedCategories(List<String> selectedCategoryIds) {
		this.selectedCategoryIds = selectedCategoryIds;
	}

	public List<String> getPreferedCategories() {
		return this.selectedCategoryIds;
	}
}
