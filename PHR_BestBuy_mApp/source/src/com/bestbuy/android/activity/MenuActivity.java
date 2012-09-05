package com.bestbuy.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.MdotWebView;
import com.bestbuy.android.util.QRCodeWorker;
import com.nullwire.trace.ExceptionHandler;

/**
 * Extend this to handle the menus for activities
 * 
 * @author Recursive Awesome
 * 
 */
public class MenuActivity extends Activity {

	private static final String TAG = "MenuActivity.java";

	public static final int VIEW_CART = 0;
	public static final int FEEDBACK = 1;
	public static final int HOME = 2;
	public static final int STORE_LOCATOR = 3;
	public static final int TERMS = 4;
	public static final int BROWSE_CATEGORY = 5;
	public static final int QR_CODE_SEARCH = 6;
	public static final int SHARE = 7;
	public static final int REWARD_ZONE = 8;
	public static final int GIFT_LIST = 9;
	public static final int HISTORY = 10;
	public static final int PHOTO_SEARCH = 11;
	public static final int ORDER_STATUS = 12;
	public static final int GC_BALANCE = 13;
	public static final int YOUR_ACCOUNT = 14;
	public static final int TOGGLE_CONNECTIVITY = 15;
	public static final int NOTIFICATIONS = 16;
	public static final int ENVIRONMENT = 17;
	public static final int GAME_TRADE_IN = 18;
	public static final int REBATE_FINDER=19;
	public static final int REQUEST_DECODE = 99; // Arbitrary ID for request
	public static final int APPOLICIOUS = 100; // Arbitrary ID for Appolicious
	
	// passed to DecodeActivity

	protected final int LOADING_PRODUCTS_DIALOG = 1;
	protected final int ADDING_DIALOG = 2;
	protected final int LOADING_DIALOG = 3;

	protected Context _context;
	protected AppData appData;
	protected final int APPLIANCE_REQ_CODE = 1000;
	protected final int BRAND_REQ_CODE = 2000;
	
	//For mDOT
	protected static final String MDOT_URL = "mDotURL";
	//private Handler trackerHandler = new Handler();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		_context = this; 
		if (AppData.EMAIL_ON_FORCE_CLOSE) {
			ExceptionHandler.register(this,getResources().getString(R.string.exception_handler_url));
		}
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		if (savedInstanceState != null) {
			Diagnostics.StartMethodTracing(this);
			appData.setSelectedProduct((Product) savedInstanceState.getSerializable(AppData.SELECTED_PRODUCT));
			appData.setSearchRequest((SearchRequest) savedInstanceState.getSerializable(AppData.SEARCH_REQUEST));
			Diagnostics.StopMethodTracing(this, TAG, "Deserializing savedInstanceState took: ");
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		updateCartIcon(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(AppData.SELECTED_PRODUCT, appData.getSelectedProduct());
		outState.putSerializable(AppData.SEARCH_REQUEST, appData.getSearchRequest());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (AppData.getGlobalConfig() == null 
				|| AppData.getGlobalConfig().entrySet() == null 
				|| AppData.getGlobalConfig().entrySet().isEmpty()) {
			//Make call to get global config again in background task
			runGlobalConfigTask(this);
		}
		updateCartIcon(this);
		
		// Google Analytics for the Application Launch
		/*trackerHandler.post(new Runnable() {

			public void run() {
				if (AppData.isTrackerActive(MenuActivity.this)) {
					EventsLogging.fireAndForget(EventsLogging.APP_LAUNCH, null);
					// Setting the Google Event tracker status to inactive
					AppData.setTrackerStatus(MenuActivity.this, false);
				}
			}
		});*/
	}
	
	protected void onPause(){
		super.onPause();	
		
		/*if(!AppData.isHomeScreenBackPressed())
			AppData.setTrackerStatus(this, AppData.isApplicationSentToBackground(this));*/
	}
	
	/**
	 * Syncs the cart badge to number of items currently in cart
	 * @param activity
	 * @param appData
	 */
	public static void updateCartIcon(Context context) {
		Activity activity = (Activity) context;
		View cartBadge = activity.findViewById(R.id.header_cart_badge);
		if (cartBadge != null) {
			//int cartQuantity = CartPersister.getCartProducts(activity).getTotalQuantity();
			final int cartQty = AppData.getSharedPreferences().getInt(AppData.CART_QTY, 0);
			ImageView cartButton = (ImageView) activity.findViewById(R.id.header_cart);
			TextView cartBadgeText = (TextView) activity.findViewById(R.id.header_cart_badge_text);
			int cartBtnVisibility = cartButton.getVisibility();
			//Modifying the logic slightly since this is NOT applicable for webcommerce
			if (cartQty > 0 && cartBtnVisibility == View.VISIBLE) {
				cartBadge.setVisibility(View.VISIBLE);
				if(cartQty > 9)
					cartBadge.setBackgroundResource(R.drawable.btn_cart_badge_oval);
				else
					cartBadge.setBackgroundResource(R.drawable.btn_cart_badge);
			} else {
				cartBadge.setVisibility(View.INVISIBLE);
			}
			//TODO: Un comment for other apps and this is not required for webcommerce.
			//cartBadgeText.setText(String.valueOf(cartQuantity));
			cartBadgeText.setText(String.valueOf(cartQty));
		}
	}

	public static void runGlobalConfigTask(Activity activity) {
		new GlobalConfigTask(activity).execute();
	}
	
	private static class GlobalConfigTask extends BBYAsyncTask {
		public GlobalConfigTask(Activity activity) {
			super(activity);
			getDialog().setCancelable(false);
		}
		@Override
		public void doReconnect() {
			runGlobalConfigTask(activity);
		}
		@Override
		public void doFinish() {
			return;
		}
		@Override
		public void doTask() throws Exception {
			AppData.fetchGlobalConfig();
		}
	}

	@Override
	protected void onActivityResult(int iRequestCode, int iResultCode, Intent intData) {
		//Added for localrebates
		//System.out.println("Requestcode******" +iRequestCode);
		if(iRequestCode == APPLIANCE_REQ_CODE || iRequestCode == BRAND_REQ_CODE)
			super.onActivityResult(iRequestCode, iResultCode, intData);
		else
			new QRCodeWorker().onActivityResult(iRequestCode, iResultCode, intData, this);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		buildMenu(this, menu,appData);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return launchMenuItem(this, item);
	}

	
	public static void buildMenu(Activity activity, Menu menu, AppData appData) {
		menu.clear();
		
		// Adding 3 Menu items in all pages. The menu item visibility depends upon the current page.
		menu.add(0, HOME, 0, "Home").setIcon(R.drawable.home_menuicon_a).setIntent(new Intent(activity, Home.class));
		
		menu.add(0, VIEW_CART, 0, "View Cart " /*+ "(" + quantity + ")"*/).setIcon(R.drawable.cart_menuicon_a).setIntent(new Intent(activity, MDOTProductDetail.class));

		//System.out.println("SOURCE : " + activity.getPackageName() + " : " + activity.getClass().getName());
		
		//if (activity.getParent() == null || !activity.getParent().getClass().getName().equals("com.bestbuy.android.activity.RewardZone")) {
		// Added this condition instead of above as some classes are present is different package and all
		// reward zone related activities are started from RewardZone String
		if (!activity.getClass().getName().contains("RewardZone")) {
			menu.add(0, REWARD_ZONE, 0, "Reward Zone").setIcon(R.drawable.rewardzone_icon_a).setIntent(new Intent(activity, RewardZone.class));
		}
		//}
		
		/* Per Mat, He does not need the other options apart from Home, RZ, Cart */
		 /* menu.add(0, STORE_LOCATOR, 0, "Store Locator").setIcon(R.drawable.locator_menuicon_a).setIntent(new Intent(activity, StoreLocatorList.class));
		if (Boolean.parseBoolean(AppData.getGlobalConfig().get(AppData.LAUNCH_MEDIA_QR_AVAILABLE))) {
			menu.add(0, QR_CODE_SEARCH, 0, "Scan a Code").setIcon(R.drawable.qr_menuicon_a);
		}

		int quantity = 0;
		for (CartProductItem cartItem : CartPersister.getCartProducts(activity).getItems()) {
			quantity += cartItem.getQuantity();
		}*/
		/*
		if (activity.getClass().getName().equals("com.bestbuy.android.activity.ProductDetail")) {
			menu.add(0, SHARE, 0, "Share").setIcon(android.R.drawable.ic_menu_share);
		}
		

		menu.add(0, YOUR_ACCOUNT, 0, "Your Account").setIcon(R.drawable.youraccount_menuicon).setIntent(new Intent(activity, YourAccount.class));
		menu.add(0, NOTIFICATIONS, 0, "Notifications " + "(" + appData.getNotificationManager().getNewNotificationCount() + ")").setIcon(R.drawable.icon_notifications).setIntent(new Intent(activity, NotificationList.class));
		menu.add(0, GC_BALANCE, 0, "Gift Card Balance").setIntent(new Intent(activity, CommerceGiftCardBalance.class));
		menu.add(0, GIFT_LIST, 0, "Gift List").setIntent(new Intent(activity, WishListView.class));
		menu.add(0, HISTORY, 0, "History").setIntent(new Intent(activity, History.class));
		menu.add(0, FEEDBACK, 0, "Feedback").setIcon(R.drawable.feedback_menuicon_a).setIntent(new Intent(activity, Feedback.class));
		if (activity.getParent() == null || !activity.getParent().getClass().getName().equals("com.bestbuy.android.activity.hem")) {
			menu.add(0, REBATE_FINDER, 0, "Rebate Finder").setIcon(R.drawable.rebate_finder_icon).setIntent(new Intent(activity, LocalRebateFinder.class));
    	}
		menu.add(0, ORDER_STATUS, 0, "Order Status").setIntent(new Intent(activity, CommerceOrderStatusLookup.class));
		menu.add(0, TERMS, 0, "Terms").setIcon(R.drawable.terms_menuicon_a).setIntent(new Intent(activity, Terms.class)).getIntent().putExtra("url", "file:///android_asset/legalterms.html");
		menu.add(0, PHOTO_SEARCH, 0, "Photo Search").setIntent(new Intent(activity, PhotoSearchList.class));
		menu.add(0, GAME_TRADE_IN, 0, "Game Trade-In").setIntent(new Intent(activity, GamingSearch.class));
		menu.add(0, APPOLICIOUS, 0, "Appolicious").setIntent(new Intent(activity, AppoliciousCategoriesList.class));
		menu.add(0, UPGRADE_CHECKER, 0, "Phone Upgrade Eligibility").setIntent(new Intent(activity, UpgradeChecker.class));
		
		if (AppConfig.isTest()) {
			if (AppData.enableConnectivity) {
				menu.add(0, TOGGLE_CONNECTIVITY, 0, "Connectivity: On");
			} else {
				menu.add(0, TOGGLE_CONNECTIVITY, 0, "Connectivity: Off");
			}
			menu.add(0, ENVIRONMENT, 0, "Environment").setIntent(new Intent(activity, Environment.class));
		} */
		// Hide the menu icon if we are currently in that activity
		for (int i = 0; i < menu.size(); i++) {
			if (menu.getItem(i).getIntent() != null) {
				if (menu.getItem(i).getIntent().getComponent().getClassName().equals(activity.getClass().getName())) {
					
					//Added this extra condition as the MDot page is refreshed itself as per URL
					if(activity.getClass().getName().contains("MDOTProductDetail")) {
						if(hasCart(activity))
							menu.removeItem(menu.getItem(i).getItemId());
					} else
						menu.removeItem(menu.getItem(i).getItemId());
				}
			}
		}
	}
	
	private static boolean hasCart(Activity activity) {
		String currentUrl = MdotWebView.getCurrentDisplayedUrl();
		Resources resources = activity.getResources();
		if(currentUrl !=  null) {
			if(currentUrl.contains(resources.getString(R.string.CART_URL))
				|| currentUrl.contains(resources.getString(R.string.ADDTOCART_PAGE)))
				return true;
		}
		
		return false;
	}
	
	//Override certain menu items.  By default will launch the intent associated with that item.
	public static boolean launchMenuItem(Activity activity, MenuItem item) {
		Intent i = item.getIntent();
		
		switch (item.getItemId()) {
		case HOME:
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(i);
			return true;
		case VIEW_CART:
			i.putExtra(MDOT_URL, MdotWebView.getUrl(activity, MdotWebView.ADD_TO_CART_URL));
			activity.startActivity(i);
			activity.overridePendingTransition(R.anim.slide_up_slow, R.anim.no_animation);
			return true;
		case REWARD_ZONE:
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			EventsLogging.trackRZButtonClick(activity);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			
			activity.startActivity(i);
			return true;
		/*case BROWSE_CATEGORY:
			i.putExtra(AppData.CATEGORY_ID, AppData.ROOT_REMIX_CATEGORY);
			activity.startActivity(i);
			return true;
		case QR_CODE_SEARCH:
			new QRCodeWorker().openQRCode(activity);
			return true;
		case STORE_LOCATOR:
			EventsLogging.trackRZButtonClick(activity);
			activity.startActivity(i);
			return true;
		case SHARE:
			int LOADING_DIALOG = 1;
			AppData appData = ((BestBuyApplication) activity.getApplication()).getAppData();
			ShareTask st = new ShareTask(appData.getSelectedProduct(), activity, LOADING_DIALOG);
			st.execute();
			return true;
		case REBATE_FINDER:
			activity.startActivity(i);
			return true;
		case TOGGLE_CONNECTIVITY:
			if (AppData.enableConnectivity) {
				AppData.enableConnectivity = false;
			} else {
				AppData.enableConnectivity = true;
				
			}
			return true;*/
		}
		return false;
		
	}

	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		if (id == LOADING_PRODUCTS_DIALOG) {
			dialog.setMessage("Loading products...");
		} else if (id == ADDING_DIALOG) {
			dialog.setMessage("Adding product to Wish List");
		} else if (id == LOADING_DIALOG) {
			dialog.setMessage("Loading...");
		} else {
			dialog.setMessage("Saving product...");
		}
		return dialog;
	}
	
	/**
	 * Used for the cart button in the header
	 * @param v
	 */
	public void cartListener(View v) {
		Intent intent = new Intent(this, MDOTProductDetail.class);
		intent.putExtra(MDOT_URL, MdotWebView.getUrl(this, MdotWebView.ADD_TO_CART_URL));
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up_slow, R.anim.no_animation);
	}
	
	public void searchListener(View v) {
		
		onSearchRequested();
	}
	
	public void homeListener(View v) {
		AppData.setSplashScreen(true);
		Intent i = new Intent(this, Home.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
}
