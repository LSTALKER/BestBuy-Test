package com.bestbuy.android.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.commerce.CommerceGiftCardBalance;
import com.bestbuy.android.appolicious.activity.AppoliciousCategoriesList;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.CategoryUtilities;
import com.bestbuy.android.data.Notification;
import com.bestbuy.android.data.Offer;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.device.register.RegisterDevice;
import com.bestbuy.android.environment.activity.EnvironmentActivity;
import com.bestbuy.android.eplib.util.EpLibUtil;
import com.bestbuy.android.icr.util.IcrAsyncTask;
import com.bestbuy.android.icr.util.IcrUtil;
import com.bestbuy.android.location.MyLocationManager;
import com.bestbuy.android.pushnotifications.activity.PnRewardZoneActivity;
import com.bestbuy.android.pushnotifications.activity.PushNotificationsActivity;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.pushnotifications.logic.PushNotificationsLogic;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZParser;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.CommerceBox;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.ui.draggable.DraggableGallery;
import com.bestbuy.android.ui.draggable.DraggableIcon;
import com.bestbuy.android.ui.draggable.DraggableIconFrame;
import com.bestbuy.android.upgradechecker.activity.UpgradeChecker;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheManager;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.InputStreamExtensions;
import com.bestbuy.android.util.MdotWebView;
import com.bestbuy.android.util.QRCodeWorker;
import com.bestbuy.eventcapture.EPLib;
import com.bestbuy.eventcapture.EPLibConstants;

/**
 * Main activity that is shown after splash page. Lets users search for items or
 * browse deals and offers.
 * 
 * @author Recursive Awesome
 * 
 */
public class Home extends MenuActivity {
	private final String TAG = this.getClass().getName();
	private static int RATE_DIALOG = 1;
	public static final String PRODUCTS = "Products";
	public static final String REWARD_ZONE = "Reward Zone";
	public static final String STORES = "Stores";
	public static final String OPEN_BOX = "Open Box";
	public static final String WEEKLY_AD = "Weekly Ad";
	public static final String DEALS = "Deals";
	public static final String CODE_SCAN = "Code Scan";
	public static final String PHOTO_SEARCH = "Photo Search";
	public static final String WISH_LIST = "Wish List";
	public static final String GIFT_CARD = "Gift Card";
	public static final String HISTORY = "History";
	public static final String GAME_TRADE = "Game Trade-In";
	public static final String YOUR_ACCOUNT = "My Account";
	public static final String ORDER_STATUS = "Order Status";
	public static final String UPGRADE_CHECKER = "Phone Upgrade";
	public static final String APP_CENTER = "App Center";
	//public static final String REBATE_FINDER = /* "Rebate Finder" */"EcoRebates";

	private final String REMOVE_SEARCH_BAR = "RemoveSearchBar";
	private static List<String> defaultPage0List = new ArrayList<String>();
	private static List<String> defaultPage1List = new ArrayList<String>();
	private static Map<String, IconInfo> icons = new HashMap<String, IconInfo>();

	private SharedPreferences settings;

	DraggableGallery draggableGallery;

	private LoadPromotionsTask loadPromotionsTask;
	private LoadNotificationsTask loadNotificationsTask;
	private IcrAsyncTask loadIcrPricingTask;
	private ListView lv;
	private LinearLayout pb;
	private NotificationAdapter notificationAdapter;
	private boolean isGPSAllowed = false;
	private static boolean firstAttempt = true;
	private SlidingDrawer homeNotifcationsSlider = null;
	private Handler trackerHandler = new Handler();

	// This field is used to track intent from BroadcastReceiver
	private boolean isFromReceiver = false;
	private boolean listDisplayed;
	private List<String> prefferedCatIds;
	private RZParser rzParser = null;
	private boolean isOnResumeCalled;
	private static int noOfPages = 2;
	static {
		defaultPage0List.add(PRODUCTS);
		defaultPage0List.add(REWARD_ZONE);
		defaultPage0List.add(STORES);
		defaultPage0List.add(OPEN_BOX);
		defaultPage0List.add(WEEKLY_AD);
		defaultPage0List.add(DEALS);
		defaultPage0List.add(CODE_SCAN);
		defaultPage0List.add(PHOTO_SEARCH);
		defaultPage0List.add(WISH_LIST);

		defaultPage1List.add(GIFT_CARD);
		defaultPage1List.add(HISTORY);
		defaultPage1List.add(GAME_TRADE);
		defaultPage1List.add(YOUR_ACCOUNT);
		defaultPage1List.add(ORDER_STATUS);
		defaultPage1List.add(UPGRADE_CHECKER);
		defaultPage1List.add(APP_CENTER);
		//defaultPage1List.add(REBATE_FINDER);
	}

	// TODO NOTE: If Best Buy decided not to implement the moving of icons page
	// to page. I would suggest replacing the Draggable IconGrid with a
	// gridLayout or something similar to recommendations.xml.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			String fromIrString = bundle.getString("fromIR");
			if(fromIrString.equals("pushnotification")){
				AppData.setSplashScreen(true);
				appData.setChangesInPnPreferences(true);
				isFromReceiver = true;
			}
		}
		
		if (AppConfig.isTest()) {
			if (AppData.getGlobalConfig() == null
					|| AppData.getGlobalConfig().entrySet() == null
					|| AppData.getGlobalConfig().entrySet().isEmpty()) {
				Intent i = new Intent(this, EnvironmentActivity.class);
				i.putExtra("ShowDialog", true);
				startActivity(i);
				this.finish();
				return;
			}
		}

		if (AppConfig.isSplash()) {
			if (!AppData.isSplashScreen()) {
				Intent i = new Intent(this, SplashActivity.class);
				startActivity(i);
				this.finish();
				return;
			} else if (AppData.getOMSOutageData()!=null && AppData.getOMSOutageData()
					.get(AppData.MAINTANANCE_SHUTDOWN)!=null && AppData.getOMSOutageData()
					.get(AppData.MAINTANANCE_SHUTDOWN).equals("true")) {
				Intent i = new Intent(this, SplashActivity.class);
				startActivity(i);
				this.finish();
				return;
			}
		}

		showRateReminder();
		setContentView(R.layout.home);
		if(!isFromReceiver){
			showRateReminder();
			performIcrPricingTask();
			//Check device registration with PN server.
			if(!AppData.getRegwithPNStatus()){
				enabledNotificationTray(true,false);
			}
		}
		buildIconInfo();
		performIcrPricingTask();

		if (loadPromotionsTask == null
				|| !loadPromotionsTask.getStatus().equals(
						AsyncTask.Status.RUNNING)) {
			loadPromotionsTask = new LoadPromotionsTask(this);
			loadPromotionsTask.execute();
		}

		findViewById(R.id.header_done).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (draggableGallery.isEditMode()) {
							refreshDraggableIcons();
						}
					}
				});

		draggableGallery = ((DraggableGallery) findViewById(R.id.draggable_gallery));

		int position = AppData.getSharedPreferences().getInt(
				AppData.HOME_SELECTED_PAGE, 0);
		if (position != 0) {
			draggableGallery.setSelection(position);
			((ImageView) findViewById(R.id.home_page_circle_0))
					.setBackgroundResource(R.drawable.page_circle_inactive);
			((ImageView) findViewById(R.id.home_page_circle_1))
					.setBackgroundResource(R.drawable.page_circle_active);
		}

		draggableGallery
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> adapter,
							View view, int position, long id) {
						draggableGallery.setCurrentIndex(position);
						draggableGallery.setFlinging(false);
						AppData.getSharedPreferences().edit()
								.putInt(AppData.HOME_SELECTED_PAGE, position)
								.commit();
						if (position == 1) {
							((ImageView) findViewById(R.id.home_page_circle_0))
									.setBackgroundResource(R.drawable.page_circle_inactive);
							((ImageView) findViewById(R.id.home_page_circle_1))
									.setBackgroundResource(R.drawable.page_circle_active);
						} else {
							((ImageView) findViewById(R.id.home_page_circle_0))
									.setBackgroundResource(R.drawable.page_circle_active);
							((ImageView) findViewById(R.id.home_page_circle_1))
									.setBackgroundResource(R.drawable.page_circle_inactive);

						}
					}

					public void onNothingSelected(AdapterView<?> adapter) {
						((ImageView) findViewById(R.id.home_page_circle_0))
								.setBackgroundResource(R.drawable.page_circle_active);
						((ImageView) findViewById(R.id.home_page_circle_1))
								.setBackgroundResource(R.drawable.page_circle_inactive);
					}
				});

		pb = (LinearLayout) findViewById(R.id.offers_tab_progress);
		homeNotifcationsSlider = (SlidingDrawer) findViewById(R.id.home_notifcations_slider);
		((LinearLayout) findViewById(R.id.home_notifications_layout)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// Nothing TODO 
				
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		/*
		 * List<String> savedIds = CategoryUtilities.getPreferredCategoryIds();
		 * if(savedIds.size() == 0 && appData.getOAuthAccessor() == null &&
		 * firstAttempt) { notificationAdapter = new
		 * NotificationAdapter(getDefaultNotification()); lv = (ListView)
		 * findViewById(android.R.id.list); registerForContextMenu(lv);
		 * 
		 * lv.setAdapter(notificationAdapter);
		 * lv.setOnItemClickListener(defaultNotificationClickedHandler); } else
		 * { firstAttempt = false; if (loadNotificationsTask == null ||
		 * !loadNotificationsTask.getStatus().equals(AsyncTask.Status.RUNNING)
		 * || appData.getOAuthAccessor() != null) { loadNotificationsTask = new
		 * LoadNotificationsTask(this); loadNotificationsTask.execute(); } }
		 * if(appData.getOAuthAccessor() == null){
		 * appData.getNotificationManager().removeRZAlerts(); }
		 * 
		 * if(appData.getOAuthAccessor() == null){
		 * appData.getNotificationManager().removeRZAlerts(); }
		 * 
		 * if(draggableGallery.isEditMode()){
		 * findViewById(R.id.header_search).setVisibility(View.GONE);
		 * findViewById(R.id.header_cart).setVisibility(View.GONE);
		 * findViewById(R.id.header_cart_badge).setVisibility(View.GONE);
		 * findViewById(R.id.header_done).setVisibility(View.VISIBLE); }
		 * MyLocationManager mLocationManager = new
		 * MyLocationManager(Home.this); boolean areLocationProvidersEnabled =
		 * mLocationManager.areLocationProvidersEnabled();
		 * if(areLocationProvidersEnabled && isGPSAllowed){ isGPSAllowed =
		 * false; AppData.setCurrentLocationAllow(Home.this, true); }
		 */
		// AppData.setHomeScreenBackPressed(false);

		/*
		 * if the intent call from IntentReceiver receiver
		 * class,homeNotifcationsSlider should be open
		 */
		if (isFromReceiver || AppData.getPNtrayopen()) {
			if (!homeNotifcationsSlider.isOpened())
				homeNotifcationsSlider.open();
			isFromReceiver = false;
			AppData.setPNtrayopen(false);
		}
		// registering device with PN server if device not registered.
		if (!AppData.getRegwithPNStatus()) {
			AppData.setOptin(AppData.PUSH_NOTIFICATION_OPTIN_ON);
			AppData.setOptinStatus(true);
			new RegisterDevice();
		}
		// checking for DefaultNotifications in in-app notification tray
		displayDefaultNotifications();

		// Check push notification for Market Updates
		List<String> categories = CategoryUtilities.getPreferredCategoryIds();
		System.out.println(" categories 1 : " + categories);
		if (categories != null) {
			System.out.println(" categories 2 : " + categories.size());
			if (categories.size() > 0) {
				new PushNotificationOffersTask(Home.this).execute();
			} else if (AppData.getRegwithPNStatus()) {
				System.out.println(" categories 3 : " + categories);
				getPrefferedCatIdsFromPNServer();
			}
		} else {
			System.out.println(" categories 4 : " + categories);
			getPrefferedCatIdsFromPNServer();
		}

		if (draggableGallery.isEditMode()) {
			findViewById(R.id.header_search).setVisibility(View.GONE);
			findViewById(R.id.header_cart).setVisibility(View.GONE);
			findViewById(R.id.header_cart_badge).setVisibility(View.GONE);
			findViewById(R.id.header_done).setVisibility(View.VISIBLE);
		}
		/*if (!homeNotifcationsSlider.isOpened()) {
			enabledNotificationTray(false, false);
		} else*/
		if (appData.getNotificationManager().getNewNotificationCount() > 0
				&& listDisplayed) {
			enabledNotificationTray(true, true);
		}
		/*else {
			enabledNotificationTray(false, false);
		}*/

		// Google Analytics for the Application Launch
		trackerHandler.post(new Runnable() {

			public void run() {
				if (AppData.isTrackerActive(Home.this)) {
					EventsLogging.fireAndForget(EventsLogging.APP_LAUNCH, null);
					// Setting the Google Event tracker status to inactive
					AppData.setTrackerStatus(Home.this, false);
				}
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			showView();
		}
	}

	private boolean isNetAvailable(final Intent intent) {

		if (!BaseConnectionManager.isNetAvailable(Home.this)
				|| BaseConnectionManager.isAirplaneMode(Home.this)) {
			NoConnectivityExtension.noConnectivity(Home.this,
					new OnReconnect() {
						public void onReconnect() {
							if (isNetAvailable(intent)) {
								if (intent == null) {
									if (prefferedCatIds!=null &&prefferedCatIds.size() > 0 && pb.getVisibility() == View.INVISIBLE) {
										new LoadNotificationsTask(Home.this).execute();
									}else{
										showList();
									}
								} else
									startActivity(intent);
							}
						}
					}, new OnCancel() {

						public void onCancel() {
							if (intent == null)
								((SlidingDrawer) findViewById(R.id.home_notifcations_slider))
										.animateClose();
						}
					});
			return false;
		} else {
			return true;
		}
	}

	// @Override
	public void onBackPressed() {
		if (draggableGallery.isEditMode()) {
			refreshDraggableIcons();
		} else {
			super.onBackPressed();
		}

	}

	private void refreshDraggableIcons() {
		draggableGallery.cancelEditMode();
		findViewById(R.id.header_search).setVisibility(View.VISIBLE);
		findViewById(R.id.header_cart).setVisibility(View.VISIBLE);
		updateCartIcon(this);
		findViewById(R.id.header_done).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.home_featured_ad_banner))
				.setEnabled(true);
		((Button) findViewById(R.id.dummy_btn)).setVisibility(View.GONE);
	}

	private void showView() {

		if (buildIconGallery()) {
			if (!AppData.isLocationKeyExist(Home.this)) {
				alertforCurrentLocationPermission();
			}
		}

		((SlidingDrawer) findViewById(R.id.home_notifcations_slider))
				.setOnDrawerCloseListener(new OnDrawerCloseListener() {
					public void onDrawerClosed() {
						appData.getNotificationManager().saveNotificationHash();
						if (notificationAdapter != null) {
							notificationAdapter.notifyDataSetChanged();
						}
						if (appData.getNotificationManager()
								.getNewNotificationCount() > 0) {
							((LinearLayout) findViewById(R.id.home_notifications_handle))
									.setBackgroundResource(R.drawable.bg_notifications_bar_active);
							((RelativeLayout) findViewById(R.id.home_notifications_badge))
									.setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.home_notifications_number))
									.setText(Integer.toString(appData
											.getNotificationManager()
											.getNewNotificationCount()));
						} else {
							((LinearLayout) findViewById(R.id.home_notifications_handle))
									.setBackgroundResource(R.drawable.bg_notifications_bar_inactive);
							((RelativeLayout) findViewById(R.id.home_notifications_badge))
									.setVisibility(View.GONE);
						}
					}
				});

		((SlidingDrawer) findViewById(R.id.home_notifcations_slider))
				.setOnDrawerOpenListener(new OnDrawerOpenListener() {

					public void onDrawerOpened() {
						if(appData.getNotificationManager().getNewNotificationCount()<=0)
						enabledNotificationTray(false, false);
						if (isNetAvailable(null)) {
							/*if (CategoryUtilities.getPreferredCategoryIds()	.size() > 0	&& pb.getVisibility() == View.INVISIBLE) {
								new LoadNotificationsTask(Home.this).execute();
							}*/
							if (!isOnResumeCalled&&prefferedCatIds!=null && prefferedCatIds.size() > 0	&& pb.getVisibility() == View.INVISIBLE) {
								new LoadNotificationsTask(Home.this).execute();
							}
							
						} else {
							pb.setVisibility(View.INVISIBLE);
						}
					}

				});

	}

	private void performIcrPricingTask() {

		if (loadIcrPricingTask == null
				|| !loadIcrPricingTask.getStatus().equals(
						AsyncTask.Status.RUNNING)) {
			loadIcrPricingTask = new IcrAsyncTask();
			loadIcrPricingTask.execute();

		}
	}

	// /////////////////////////////////////
	// Icons //
	// ////////////////////////////////////

	private boolean buildIconGallery() {
		Display display = ((WindowManager) Home.this
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int freeSpace = display.getHeight()
				- (findViewById(R.id.home_header).getHeight() + findViewById(
						R.id.home_featured_ad_layout).getHeight())
				- (findViewById(R.id.home_notifications_handle).getHeight() + findViewById(
						R.id.home_notifcations_shadow).getHeight());
		int offset = ((findViewById(R.id.home_header).getHeight() + findViewById(
				R.id.home_featured_ad_layout).getHeight()) + freeSpace / 2)
				- display.getHeight() / 2;
		List<String> iconNames;
		for (int i = 0; i < noOfPages; i++) {
			if (i == 0) {
				iconNames = defaultPage0List;
				if (!refinedGridIds(0).isEmpty()) {
					iconNames = refinedGridIds(0);
				}
			} else {
				iconNames = defaultPage1List;
				if (!refinedGridIds(2).isEmpty()) {
					iconNames = refinedGridIds(1);
				}
			}

			// TODO iconGrid.setDoneButton(((Button)
			// findViewById(R.id.home_btn_browse)));
			if (freeSpace - 10 < display.getWidth()) {
				draggableGallery.setMaxWidth(freeSpace - 10);
			}
			draggableGallery.setCenterYOffset(offset);

			for (final String iconName : iconNames) {
				if (iconName != CODE_SCAN
						|| Boolean.parseBoolean(AppData.getGlobalConfig().get(
								AppData.LAUNCH_MEDIA_QR_AVAILABLE))) {
					draggableGallery
							.addIcon(getImageForKey(iconName), iconName,
									getRemoveableForKey(iconName),
									getOnClickListenerForKey(iconName),
									longListener, i);
				}

			}
		}
		return true;
	}

	private void buildIconInfo() {

		icons.put(PRODUCTS, new IconInfo(R.drawable.icon_home_products, false,
				new OnClickListener() {
					public void onClick(View v) {
						Map<String, String> params = new HashMap<String, String>();
						params.put("value",
								EventsLogging.CUSTOM_CLICK_PRODUCT_BROWSE_EVENT);
						EventsLogging.fireAndForget(
								EventsLogging.CUSTOM_CLICK_ACTION, params);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_PRODUCTS);
						Intent i = new Intent(Home.this, BrowseCategory.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(REWARD_ZONE, new IconInfo(R.drawable.icon_home_reward_zone,
				false, new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this, RewardZone.class);
						BBYLog.d("Active task:", "Reward zone");
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_REWARD_ZONE);
						i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						EventsLogging.trackRZButtonClick(Home.this);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(STORES, new IconInfo(R.drawable.icon_home_stores, false,
				new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this, StoreLocatorList.class);
						i.putExtra(StoreUtils.IS_OPENBOX, false);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_STORES);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(OPEN_BOX, new IconInfo(R.drawable.icon_home_open_box, false,
				new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this, StoreLocatorList.class);
						i.putExtra(StoreUtils.IS_OPENBOX, true);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_OPEN_BOX);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(WEEKLY_AD, new IconInfo(R.drawable.icon_home_weekly_deals,
				false, new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this,
								DealRootCategoryList.class);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_WEEKLY_AD);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(DEALS, new IconInfo(R.drawable.icon_home_deals, false,
				new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this, OffersList.class);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_DEALS);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(CODE_SCAN, new IconInfo(R.drawable.icon_home_code_scan,
				false, new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_CODE_SCAN);
						new QRCodeWorker().openQRCode(Home.this, "CODE_SCAN");
					}
				}));
		icons.put(PHOTO_SEARCH, new IconInfo(R.drawable.icon_home_photo_search,
				false, new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this, PhotoSearchQuery.class);
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_PHOTO_SEARCH);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(WISH_LIST, new IconInfo(R.drawable.icon_home_gift_list,
				false, new OnClickListener() {
					public void onClick(View v) {
						/*
						 * Intent i = new Intent(Home.this, WishListView.class);
						 * startActivity(i);
						 */
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_WISH_LIST);
						Intent intent = new Intent(Home.this,
								MDOTProductDetail.class);
						intent.putExtra(MDOT_URL, MdotWebView.getUrl(Home.this,
								MdotWebView.WHISH_LIST));
						if (isNetAvailable(intent))
							startActivity(intent);
					}
				}));
		icons.put(GIFT_CARD, new IconInfo(R.drawable.icon_home_gift_cards,
				false, new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_GIFT_CARD);
						PackageManager pm = getPackageManager();

						if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
							new QRCodeWorker().openQRCode(Home.this,
									"GIFT_CARD");
						} else {
							Intent i = new Intent(Home.this,
									CommerceGiftCardBalance.class);
							i.putExtra("gift_id", "0");
							startActivity(i);
						}

					}
				}));
		icons.put(HISTORY, new IconInfo(R.drawable.icon_home_history, false,
				new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_HISTORY);
						Intent i = new Intent(Home.this, History.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(GAME_TRADE, new IconInfo(R.drawable.icon_home_game_tradein,
				false, new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_GAME_TRADE);
						Intent i = new Intent(Home.this, GamingSearch.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(YOUR_ACCOUNT, new IconInfo(R.drawable.icon_home_my_account,
				false, new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_YOUR_ACCOUNT);
						Intent i = new Intent(Home.this, YourAccount.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(ORDER_STATUS, new IconInfo(R.drawable.icon_home_order_status,
				false, new OnClickListener() {
					public void onClick(View v) {
						/*
						 * Intent i = new Intent(Home.this,
						 * CommerceOrderStatusLookup.class); startActivity(i);
						 */
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_ORDER_STATUS);
						Intent intent = new Intent(Home.this,
								MDOTProductDetail.class);
						intent.putExtra(MDOT_URL, MdotWebView.getUrl(Home.this,
								MdotWebView.VIEW_ORDER_URL));
						intent.putExtra(REMOVE_SEARCH_BAR, true);
						if (isNetAvailable(intent))
							startActivity(intent);
					}
				}));
		icons.put(UPGRADE_CHECKER, new IconInfo(
				R.drawable.icon_home_upgrade_checker, false,
				new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_UPGRADE_CHECKER);
						Intent i = new Intent(Home.this, UpgradeChecker.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));
		icons.put(APP_CENTER, new IconInfo(R.drawable.icon_home_app_center,
				false, new OnClickListener() {
					public void onClick(View v) {
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_UI_CLICK, EpLibUtil.ITEM_TYPE_APP_CENTER);
						Map<String, String> params = new HashMap<String, String>();
						params.put("value",
								EventsLogging.CUSTOM_CLICK_YOUR_APP_CENTER);
						EventsLogging.fireAndForget(
								EventsLogging.CUSTOM_CLICK_ACTION, params);

						Intent i = new Intent(Home.this,
								AppoliciousCategoriesList.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));

		/*icons.put(REBATE_FINDER, new IconInfo(R.drawable.icon_home_rebates,
				false, new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(Home.this,
								LocalRebateFinder.class);
						if (isNetAvailable(i))
							startActivity(i);
					}
				}));*/
	}

	public static void saveGridIds(int page, List<String> gridIds) {

		StringBuilder idStringBuilder = new StringBuilder();
		for (int i = 0; i < gridIds.size(); i++) {
			if (i > 0) {
				idStringBuilder.append("|");
			}
			idStringBuilder.append(gridIds.get(i));
		}
		String idString = idStringBuilder.toString();
		String key = AppData.PAGE_0_GRID_IDS;
		if (page == 1) {
			key = AppData.PAGE_1_GRID_IDS;
		}
		AppData.getSharedPreferences().edit().putString(key, idString).commit();
	}

	public static List<String> getGridIds(int page) {
		String key = AppData.PAGE_0_GRID_IDS;
		if (page == 1) {
			key = AppData.PAGE_1_GRID_IDS;
		}
		String gridString = AppData.getSharedPreferences().getString(key, null);
		List<String> gridList = new ArrayList<String>();
		if (gridString != null) {
			gridList.addAll(Arrays.asList(gridString.split("\\|")));
			gridList.remove("");
		}
		return gridList;
	}
	
	public static List<String> refinedGridIds(int page){
		List<String> refinedGridList = new ArrayList<String>();
		for(String iconName : getGridIds(page)){
			if(defaultPage0List.contains(iconName) || defaultPage1List.contains(iconName)){
				refinedGridList.add(iconName);
			}
		}
		if(refinedGridList.size() != 0 && refinedGridList.size() < 9 && page == 0){
			int diff = 9 - refinedGridList.size();
			List<String> page2List = getGridIds(page+1);
			for(int i = 0; i < diff; i++){
				refinedGridList.add(page2List.get(i));
				page2List.remove(i);
			}
			saveGridIds(1, page2List);
		}
		saveGridIds(page, refinedGridList);
		if(page == 1){
			return addNewGridIds(refinedGridList);
		}
		return refinedGridList;
	}
	
	public static List<String> addNewGridIds(List<String> refinedGridList){
		List<String> defaultGridList = new ArrayList<String>();
		defaultGridList.addAll(defaultPage0List);
		defaultGridList.addAll(defaultPage1List);
		List<String> savedGridList = new ArrayList<String>();
		savedGridList.addAll(getGridIds(0));
		savedGridList.addAll(getGridIds(1));
		if(defaultGridList.size() != savedGridList.size()){
			for(String iconName : defaultGridList){
				if(!savedGridList.contains(iconName))
					refinedGridList.add(iconName);
			}
		}
		return refinedGridList;
	}

	private int getImageForKey(String key) {
		return ((IconInfo) icons.get(key)).getImageId();
	}

	private boolean getRemoveableForKey(String key) {
		return ((IconInfo) icons.get(key)).getRemoveable();
	}

	private OnClickListener getOnClickListenerForKey(String key) {
		return ((IconInfo) icons.get(key)).getClickListener();
	}

	private static class IconInfo {
		private int imageId;
		private boolean removable;
		private OnClickListener clickListener;

		public IconInfo(int imageId, boolean removable,
				OnClickListener clickListener) {
			this.imageId = imageId;
			this.removable = removable;
			this.clickListener = clickListener;
		}

		public int getImageId() {
			return imageId;
		}

		public boolean getRemoveable() {
			return removable;
		}

		public OnClickListener getClickListener() {
			return clickListener;
		}
	}

	/**
	 * a generic long click listener used on all draggable icons to enable edit
	 * mode
	 */
	private OnLongClickListener longListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			if (!draggableGallery.isEditMode()) {
				draggableGallery.startEditMode();
				DraggableIconFrame.grow((DraggableIcon) v);
				findViewById(R.id.header_search).setVisibility(View.GONE);
				findViewById(R.id.header_cart).setVisibility(View.GONE);
				findViewById(R.id.header_cart_badge).setVisibility(View.GONE);
				findViewById(R.id.header_done).setVisibility(View.VISIBLE);
				((LinearLayout) findViewById(R.id.home_featured_ad_banner))
						.setEnabled(false);
				((Button) findViewById(R.id.dummy_btn))
						.setVisibility(View.VISIBLE);
				return true;
			} else {
				return false;
			}
		}
	};

	// //////////////////////////////////////////
	// Promotions //
	// /////////////////////////////////////////
	private class LoadPromotionsTask extends BBYAsyncTask {

		private List<Offer> promotions;

		public LoadPromotionsTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doStart() {
			promotions = new ArrayList<Offer>();
			((LinearLayout) findViewById(R.id.home_featured_ad_progress))
					.setVisibility(View.VISIBLE);
			((LinearLayout) findViewById(R.id.home_featured_ad_banner))
					.setVisibility(View.GONE);
		};

		@Override
		public void doTask() throws Exception {
			SearchRequest searchRequest = new SearchRequest();

			String channelKey = AppData.BBY_OFFERS_MOBILE_FEATURED_OFFERS_CHANNEL;
			promotions
					.addAll(searchRequest.getOffers(channelKey, null, "true"));

			while (searchRequest.getCursor() != null) {
				if (!promotions
						.containsAll(searchRequest.getOffers(channelKey))) {
					promotions.addAll(searchRequest.getOffers(channelKey));
				}
			}
		}

		@Override
		public void doError() {
			if (noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity,
						new OnReconnect() {
							public void onReconnect() {
								loadPromotionsTask = new LoadPromotionsTask(
										Home.this);
								loadPromotionsTask.execute();
							}
						}, new OnCancel() {

							public void onCancel() {
								hidePromoProgress();
							}
						});
			} else {
				hidePromoProgress();
			}
		}

		@Override
		public void doFinish() {
			if (promotions != null && promotions.size() > 0) {
				showPromo();
			} else {
				hidePromoProgress();
			}
		}

		private void hidePromoProgress() {
			((LinearLayout) findViewById(R.id.home_featured_ad_progress))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.home_featured_ad_banner))
					.setVisibility(View.VISIBLE);
			((CommerceBox) findViewById(R.id.home_featured_ad_banner))
					.getArrow().setVisibility(View.GONE);
		}

		private void showPromo() {
			((LinearLayout) findViewById(R.id.home_featured_ad_progress))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.home_featured_ad_banner))
					.setVisibility(View.VISIBLE);

			final Offer promotion = promotions.get(0);
			ImageView promotionImage = (ImageView) findViewById(R.id.home_featured_ad_banner_image);
			String imageUrl = promotion.getImageURL();
			BBYLog.e(TAG, imageUrl);
			if (imageUrl != null && imageUrl.length() > 0) {
				ImageProvider.getBitmapImageOnThread(imageUrl, promotionImage);
			}
			((TextView) findViewById(R.id.home_featured_ad_banner_text))
					.setText(promotion.getTitle());
			CommerceBox commerceBox = (CommerceBox) findViewById(R.id.home_featured_ad_banner);
			commerceBox.getArrow().setVisibility(View.VISIBLE);
			commerceBox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					List<String> promotionSkus = promotion.getSkus();
					Map<String, String> params = new HashMap<String, String>();
					params.put("value",
							AppData.BBY_OFFERS_MOBILE_FEATURED_OFFERS_CHANNEL
									+ ";" + promotion.getProductKey());
					EventsLogging.fireAndForget(
							EventsLogging.INTERNAL_CAMPAIGN_CLICK, params);

					if (promotionSkus.size() >= 1) {
						// If there is a list of skus, show the list of products
						/*
						 * SearchRequest searchRequest = new SearchRequest();
						 * searchRequest.setSkus(promotionSkus);
						 * appData.setSearchRequest(searchRequest); Intent i =
						 * new Intent(v.getContext(), SearchResultList.class);
						 * startActivity(i);
						 */

						Intent i = new Intent(v.getContext(),
								SearchResultList.class);
						i.putExtra(AppData.PRODUCT_SEARCH_QUERY,
								getSkusString(promotionSkus));
						startActivity(i);

					} else if (promotionSkus.size() == 1) {
						try {
							// load the product to show by inflating the data
							// from the remix data string
							appData.setSelectedProduct(promotion);
							/*
							 * Intent i = new Intent(v.getContext(),
							 * ProductDetail.class);
							 */
							Intent i = new Intent(v.getContext(),
									MDOTProductDetail.class);
							startActivity(i);
						} catch (Exception ex) {
							BBYLog.printStackTrace(TAG, ex);
							BBYLog.e(TAG,
									"Exception in Home:showPromotions() loading promotion: "
											+ promotion.getProductKey());
						}
					} else {

						// If there is no sku, show the website
						/*
						 * Intent i = new Intent(Intent.ACTION_VIEW,
						 * Uri.parse(promotion.getUrl())); startActivity(i);
						 */
						Intent intent = new Intent(Home.this,
								MDOTProductDetail.class);
						intent.putExtra(MDOT_URL, promotion.getUrl());
						startActivity(intent);
					}
				}
			});
		}
	}

	private String getSkusString(List<String> skuList) {
		StringBuilder idStringBuilder = new StringBuilder();
		int index = skuList.size();
		for (int i = 0; i < skuList.size(); i++) {
			idStringBuilder.append(skuList.get(i));
			if (i != --index) {
				idStringBuilder.append("+");
			}
		}

		return idStringBuilder.toString();
	}

	// //////////////////////////////////////////
	// Notifications //
	// //////////////////////////////////////////

	private class LoadNotificationsTask extends BBYAsyncTask {
		public LoadNotificationsTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
			
		}

		 protected void onPreExecute() {
			  pb = (LinearLayout) findViewById(R.id.offers_tab_progress);
			  pb.setVisibility(View.VISIBLE);
			  }
		
		@Override
		public void doFinish() {
		
			if(!isError && appData.getNotificationManager().getNewNotificationCount() > 0){
				appData.getNotificationManager().setLastNotificationRetrievedDate(new Date());
				((LinearLayout)findViewById(R.id.home_notifications_handle)).setBackgroundResource(R.drawable.bg_notifications_bar_active);
				((RelativeLayout)findViewById(R.id.home_notifications_badge)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.home_notifications_number)).setText(Integer.toString(appData.getNotificationManager().getNewNotificationCount()));
			}else{
				((LinearLayout)findViewById(R.id.home_notifications_handle)).setBackgroundResource(R.drawable.bg_notifications_bar_inactive);
				((RelativeLayout)findViewById(R.id.home_notifications_badge)).setVisibility(View.GONE);
			}
			if(!isError){
				pb.setVisibility(View.GONE);
				showList();
				enabledNotificationTray(true, true);
			}
		}

		@Override
		public void doError() {
			isError = true;
		}
		
		@Override
		public void doTask() throws Exception {	
			appData.getNotificationManager().loadNotifications();	
		}
	}

	/*private void showList() {
		lv = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(lv);
		Vector<Notification> notifications = new Vector<Notification>();
		if (CategoryUtilities.getPreferredCategoryIds().size() == 0) {
			final Notification object = Notification.notificationWithType(
					Notification.NotificationTypeDefault, new Object());
			notifications.add(object);
		}
		notifications.addAll(appData.getNotificationManager()
				.getNotifications());
		notificationAdapter = new NotificationAdapter(notifications);

		lv.setAdapter(notificationAdapter);
		lv.setOnItemClickListener(notificationClickedHandler);

		if (notifications.size() == 0)
			((LinearLayout) findViewById(R.id.no_notifications_tab))
					.setVisibility(View.VISIBLE);
	}*/

	/*private Vector<Notification> getDefaultNotification() {
		Vector<Notification> notifications = new Vector<Notification>();
		final Notification object = Notification.notificationWithType(
				Notification.NotificationTypeDefault, new Object());
		notifications.add(object);

		return notifications;
	}*/

	OnItemClickListener defaultNotificationClickedHandler = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Intent intent = new Intent(Home.this, PreferredCategories.class);
			intent.putExtra(AppData.PREFERED_CATEGORIES_ONRETURN, "Home");
			startActivity(intent);
		}
	};

	/*OnItemClickListener notificationClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Vector<Notification> notificationList = new Vector<Notification>();
			if (CategoryUtilities.getPreferredCategoryIds().size() == 0) {
				final Notification object = Notification.notificationWithType(
						Notification.NotificationTypeDefault, new Object());
				notificationList.add(object);
			}
			notificationList.addAll(appData.getNotificationManager()
					.getNotifications());
			// Vector<Notification> notificationList =
			// appData.getNotificationManager().getNotifications();
			if (notificationList != null) {
				if (notificationList.size() > 0) {
					Notification notification = notificationList.get(position);
					appData.getNotificationManager().setNew(notification);
					v.findViewById(R.id.notification_star).setVisibility(
							View.GONE);
					switch (notification.getNotificationType()) {
					case Notification.NotificationTypeAlert:
						gotoAlert(notification, id);
						break;
					case Notification.NotificationTypeOffer:
						gotoOffer(notification);
						break;
					case Notification.NotificationTypeRZCert:
						gotoCert(notification);
						break;
					case Notification.NotificationTypeDefault:
						Intent intent = new Intent(Home.this,
								PreferredCategories.class);
						intent.putExtra(AppData.PREFERED_CATEGORIES_ONRETURN,
								"Home");
						startActivity(intent);
						break;
					}
				}
			}
		}
	};*/

	private void gotoAlert(Notification notification, long id) {
		Intent i = new Intent(Home.this, NotificationDetail.class);
		i.putExtra("position", (int) id);
		startActivity(i);
	}

	private void gotoOffer(Notification notification) {
		Offer offer = (Offer) notification.getNotificationObject();
		appData.setSelectedProduct(offer);
		Intent i = new Intent(Home.this, SpecialOffersDetail.class);
		startActivity(i);
	}

	private void gotoCert(Notification notification) {
		if (appData.getOAuthAccessor() != null) {
			Intent i = new Intent(Home.this, RewardZoneCertificate.class);
			i.putExtra("position", notification.getId());
			startActivity(i);
		}
	}

	class NotificationAdapter extends ArrayAdapter<Notification> {
		Vector<Notification> notifications;

		NotificationAdapter(Vector<Notification> notifications) {
			super(Home.this, R.layout.notification_row, notifications);
			this.notifications = notifications;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater
						.inflate(R.layout.notification_row, parent, false);
			}

			Notification notification = notifications.get(position);

			ImageView icon = (ImageView) row
					.findViewById(R.id.notification_row_icon);

			switch (notification.getNotificationType()) {
			case Notification.NotificationTypeOffer:
				if (notification.getListImageURL() != null
						&& notification.getListImageURL().length() > 0) {
					ImageProvider.getBitmapImageOnThread(
							notification.getListImageURL(), icon);
				} else {
					icon.setImageResource(R.drawable.icon_offer);
				}
				break;
			case Notification.NotificationTypeRZCert:
				icon.setImageResource(R.drawable.icon_certificate);
				break;
			case Notification.NotificationTypeAlert:
				if (notification.getListImageURL() != null
						&& notification.getListImageURL().length() > 0) {
					ImageProvider.getBitmapImageOnThread(
							notification.getListImageURL(), icon);
				} else {
					icon.setImageResource(R.drawable.icon_notify_default);
				}
				break;
			}

			if (appData.getNotificationManager().isNew(notification)) {
				row.findViewById(R.id.notification_star).setVisibility(
						View.VISIBLE);
			} else {
				row.findViewById(R.id.notification_star).setVisibility(
						View.GONE);
			}

			if (notification.getNotificationType() == Notification.NotificationTypeDefault) {
				TextView title = (TextView) row
						.findViewById(R.id.notification_row_title);
				TextView subTitle = (TextView) row
						.findViewById(R.id.notification_row_subtitle);
				title.setText("Set Up Personalized Notifications");
				subTitle.setVisibility(View.VISIBLE);
				subTitle.setText("Get alerts for offers and deals on your favorite products");
				title.setTextSize(13);
				subTitle.setTextSize(11);
				icon.setImageResource(R.drawable.icon_notify_default);
			} else if(notification.getNotificationType() == Notification.NotificationRZTypeDefault) {
				TextView title = (TextView) row.findViewById(R.id.notification_row_title);
				TextView subTitle = (TextView) row.findViewById(R.id.notification_row_subtitle);
				title.setText("Get Reward Zone Offers");
				subTitle.setVisibility(View.VISIBLE);
				subTitle.setText("Log in to your Reward Zone");
				title.setTextSize(13);
				subTitle.setTextSize(11);
				icon.setImageResource(R.drawable.ic_notify_rz);
			}
			
			else {
				((TextView) row.findViewById(R.id.notification_row_title)).setText(notification.getTitle());
				((TextView)row.findViewById(R.id.notification_row_subtitle)).setText("");
			}
			return row;
		}
	}

	// ////////////////////////////////////////
	// Rate Dialog //
	// ///////////////////////////////////////

	private void showRateReminder() {
		settings = getSharedPreferences(AppData.SHARED_PREFS, 0);
		final boolean ratingReminder = settings.getBoolean(
				AppData.RATING_REMINDER, true);
		int launchCount = settings.getInt(AppData.LAUNCH_COUNT, 1);

		if (ratingReminder) {
			launchCount++;
			settings.edit().putInt(AppData.LAUNCH_COUNT, launchCount).commit(); // update
		}

		final String version = settings.getString(AppData.APP_VERSION, null);
		final String packageVersion = getPackageVersion();

		if (version == null || !version.equals(packageVersion)) {
			settings.edit().putString(AppData.APP_VERSION, packageVersion)
					.commit();
			settings.edit().putInt(AppData.LAUNCH_COUNT, 0).commit(); // reset
			settings.edit().putBoolean(AppData.RATING_REMINDER, true).commit(); // reset

		}

		if (launchCount > 5 && ratingReminder) {
			EventsLogging.fireAndForget(EventsLogging.RATE_APP_CLICK_POP_UP,
					null);
			showDialog(RATE_DIALOG);
		}
	}

	private String getPackageVersion() {
		return AppData.getVersionName(Home.class);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (this.isFinishing()) { // Use ||, Do not use |
			return null;
		}
		if (id == LOADING_DIALOG) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.setMessage("Loading...");
			return dialog;
		} else {

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Rate Best Buy")
					.setMessage(
							"If you enjoy using the Best Buy app, would you mind taking a moment to rate it? It won't take more than a minute. Thanks for your support!")
					.setCancelable(false)
					.setNeutralButton("Remind Me",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									EventsLogging
											.fireAndForget(
													EventsLogging.RATE_APP_CLICK_REMIND_ME,
													null);

									settings.edit()
											.putInt(AppData.LAUNCH_COUNT, 0)
											.commit(); // reset
								}
							})
					.setPositiveButton("Rate App",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									//EPLIB
									EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_PAGE_LOAD, EpLibUtil.ITEM_TYPE_APP_RATING);
									EventsLogging
											.fireAndForget(
													EventsLogging.RATE_APP_CLICK_SHOW_MORE,
													null);

									settings.edit()
											.putBoolean(
													AppData.RATING_REMINDER,
													false).commit(); // turn
									// off
									// rate
									// reminder.
									final Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setData(Uri
											.parse("market://details?id=com.bestbuy.android"));
									try {
										startActivity(intent);
									} catch (final ActivityNotFoundException aex) {
										BBYLog.printStackTrace(TAG, aex);
										BBYLog.e(TAG,
												"Not able to find Market Activity: "
														+ aex); // for
									}
								}
							})
					.setNegativeButton("No, Thanks",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									EventsLogging
											.fireAndForget(
													EventsLogging.RATE_APP_CLICK_NO_THANKS,
													null);

									settings.edit()
											.putBoolean(
													AppData.RATING_REMINDER,
													false).commit(); // turn

								}
							});
			final AlertDialog alert = builder.create();
			return alert;
		}
	}

	private void alertforCurrentLocationPermission() {
		BBYLog.d(TAG,
				"alertforCurrentLocationPermission(): (this==null || this.isFinishing())="
						+ (this == null || this.isFinishing()));
		if (this.isFinishing()) { // Use ||, Do not use |
			return;
		}
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
		alertDialog.setTitle("");
		alertDialog.setMessage(R.string.ISUSE_CURRENT_LOCATION);
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton(R.string.DONT_ALLOW,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Save the flag
						BBYLog.d(TAG,
								"alertforCurrentLocationPermission(): onClick():whichButton="
										+ whichButton);
						AppData.setCurrentLocationAllow(Home.this, false);
						
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_PAGE_LOAD, EpLibUtil.ITEM_TYPE_APP_LAUNCH);
						
						dialog.cancel();
					}
				});

		alertDialog.setNegativeButton(R.string.ALLOW,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Save the flag
						BBYLog.d(TAG,
								"alertforCurrentLocationPermission(): onClick():whichButton="
										+ whichButton);
						AppData.setCurrentLocationAllow(Home.this, true);

						MyLocationManager mLocationManager = new MyLocationManager(
								Home.this);
						BBYLog.d(
								TAG,
								"areLocationProvidersEnabled(): "
										+ mLocationManager
												.areLocationProvidersEnabled());
						boolean areLocationProvidersEnabled = mLocationManager
								.areLocationProvidersEnabled();
						if (!areLocationProvidersEnabled) {
							BBYLog.e(TAG,
									"alertforCurrentLocationPermission(): location providers are disabled");
							locationProviderDisabledMsg();
							
						//EPLIB
						EpLibUtil.trackEvent(Home.this, EpLibUtil.ACTION_PAGE_LOAD, EpLibUtil.ITEM_TYPE_APP_LAUNCH);
							dialog.cancel();
						}
					}
				});

		alertDialog.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface arg0) {
				BBYLog.d(TAG,
						"alertforCurrentLocationPermission(): onCancel(): Dialog canceled");
				AppData.setCurrentLocationAllow(Home.this, false);
			}
		});
		alertDialog.create();
		alertDialog.show();
	}

	private void locationProviderDisabledMsg() {

		if (this.isFinishing()) { // Use ||, Do not use |
			return;
		}
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
		alertDialog.setTitle("Location providers disabled !");
		alertDialog
				.setMessage("Location providers are disabled, Please enable it from Location & security settings");
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// launch Location & Settings activity
						ComponentName toLaunch = new ComponentName(
								"com.android.settings",
								"com.android.settings.SecuritySettings");
						Intent intent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						intent.setComponent(toLaunch);
						startActivity(intent);
						isGPSAllowed = true;
					}
				});
		alertDialog.create();
		alertDialog.show();
	}
	
	private class PushNotificationOffersTask extends BBYAsyncTask {
		String response = null;
		public PushNotificationOffersTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doTask() throws Exception {
			List<String> categories = CategoryUtilities.getPreferredCategoryIds();
			response = PushNotificationsLogic.sendOffersNotificationConfig(categories);
		}

		@Override
		public void doFinish() {
			if (response != null) {
				String status = "";
				try {
					JSONObject jsonResponse = new JSONObject(response);
					if (jsonResponse.has(PushNotifcationData.STATUS)) {
						status = jsonResponse.getString(PushNotifcationData.STATUS);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (status.equals(PushNotifcationData.STATUS_SUCCESS)) {
					AppData.getSharedPreferences().edit().putBoolean(AppData.PUSHNOTIFICATION_WEEKLYOFFERS_STATUS, true).commit();
					CategoryUtilities.clearPreferredCategoryIds();
					
					new GetPreferencesTask(Home.this).execute();
					
				} else {
					AppData.getSharedPreferences().edit().putBoolean(AppData.PUSHNOTIFICATION_WEEKLYOFFERS_STATUS, false).commit();
				}
			} else {
				doError();
			}
		}
		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new PushNotificationOffersTask(Home.this).execute();
				}
			});
		}
	}
	
	class GetPreferencesTask extends BBYAsyncTask {
		

		public GetPreferencesTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}
		 protected void onPreExecute() {
			  prefferedCatIds.clear();
			  pb = (LinearLayout) findViewById(R.id.offers_tab_progress);
			  pb.setVisibility(View.VISIBLE);
			  }
		@Override
		public void doTask() throws Exception {
			/*String urlString = AppConfig.getPushNotificationAPIHost()+"/getSelectedCategories/"+AppData.getUAid()+"?apikey="+AppConfig.getPushNotificationAPIKey();
			prefferedCatIds = PushNotificationUtil.getPrefferedCategoryIds(urlString);*/
				prefferedCatIds =PushNotifcationData.getPrefferedCategoryIds();
		}

		@Override
		public void doFinish() {
			pb.setVisibility(View.GONE);
			if (prefferedCatIds != null) {
				appData.setPreferedCategories(prefferedCatIds);
				if(prefferedCatIds.size()>0){
					appData.setWoStatus(true);
				}else{
					appData.setWoStatus(false);
				}
				
				
			//	if (loadNotificationsTask == null || !loadNotificationsTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
					
				    loadNotificationsTask = new LoadNotificationsTask(Home.this);
					loadNotificationsTask.execute();
			//	}
			}

		}
	}

	private void showList() {
		appData.setChangesInPnPreferences(false);
		lv = (ListView) findViewById(android.R.id.list);
		lv.setVisibility(View.VISIBLE);
		registerForContextMenu(lv);
		// checking for DefaultNotifications in in-app notification tray 
			boolean isDefaultNotifications=displayDefaultNotifications();
		
		if (notificationAdapter != null) {
			listDisplayed = true;
			if (appData.getNotificationManager().getNotifications().size() > 0) {
				lv.setVisibility(View.VISIBLE);
				((TextView) findViewById(R.id.no_notifications_text))
						.setVisibility(View.GONE);
				for (int i = 0; i < appData.getNotificationManager()
						.getNotifications().size(); i++) {
					notificationAdapter.add(appData.getNotificationManager()
							.getNotifications().get(i));
				}
				lv.setAdapter(notificationAdapter);
				notificationAdapter.notifyDataSetChanged();
			} else if(!isDefaultNotifications && appData.getNotificationManager().getNotifications().size()<= 0){
				lv.setVisibility(View.GONE);
				((TextView) findViewById(R.id.no_notifications_text))
						.setVisibility(View.VISIBLE);
			}
		}
			else{
			listDisplayed=true;
			notificationAdapter = new NotificationAdapter(appData.getNotificationManager().getNotifications());	
		}
		lv.setOnItemClickListener(notificationClickedHandler);
		((TextView)findViewById(R.id.home_notifications_number)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.home_notifications_number)).setText(Integer.toString(appData.getNotificationManager().getNotifications().size()));
	}
	
	private Vector<Notification> getDefaultNotification() {
		Vector<Notification> notifications = new Vector<Notification>();
		if(!appData.getWoStatus()){
			final Notification object = Notification.notificationWithType(Notification.NotificationTypeDefault, new Object());
			notifications.add(object);
		}
		if(!appData.getRzStatus()){
			final Notification rzObject = Notification.notificationWithType(Notification.NotificationRZTypeDefault, new Object());
			notifications.add(rzObject);
		}
//		else{
//			OAuthAccessor access = appData.getOAuthAccessor();
//			if(access == null){
//				final Notification rzObject = Notification.notificationWithType(Notification.NotificationRZTypeDefault, new Object());
//				notifications.add(rzObject);
//			}
//		}
		return notifications;
	}
	
	 OnItemClickListener notificationClickedHandler = new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				
				Notification notification=(Notification) parent.getAdapter().getItem(position);
				if(notification.getNotificationType() == Notification.NotificationRZTypeDefault){
					
					new AsyncTask<String, Integer, Boolean>() {
						private ProgressDialog progressDialog;

						@Override
						protected Boolean doInBackground(String... params) {
							return rewardZoneLogin();
						}

						protected void onPostExecute(Boolean result) {
							progressDialog.cancel();
							if (!result) {
								Intent i = new Intent(Home.this, RewardZoneLogin.class);
								i.putExtra("Activity", "PushNotificationGetRewardZone");
								EventsLogging.trackRZButtonClick(Home.this);
								startActivity(i);
							}else{
								Intent intent = new Intent(Home.this, PnRewardZoneActivity.class);
								startActivity(intent);	
							}
							
						};

						protected void onPreExecute() {
							progressDialog = new ProgressDialog(Home.this);
							progressDialog.setMessage("Loading...");
							progressDialog.show();
						};

					}.execute();
					
					/*if(!rewardZoneLogin()){
						Intent i = new Intent(Home.this, RewardZoneLogin.class);
						i.putExtra("Activity", "PushNotificationGetRewardZone");
						EventsLogging.trackRZButtonClick(Home.this);
						startActivity(i);
						
					}else{
						Intent intent = new Intent(Home.this, PnRewardZoneActivity.class);
						startActivity(intent);	
						
					}*/
					
				}
				else if(notification.getNotificationType() == Notification.NotificationTypeDefault){
					checkStatusPnServer();
				}else{
				appData.getNotificationManager().setNew(notification);
				v.findViewById(R.id.notification_star).setVisibility(View.GONE);
				switch(notification.getNotificationType()){
					case Notification.NotificationTypeAlert:
						gotoAlert(notification,id);
					break;
					case Notification.NotificationTypeOffer:
						gotoOffer(notification);
					break;
					case Notification.NotificationTypeRZCert:
						gotoCert(notification);
					break;
				}
				}
//				Vector<Notification> notificationList = appData.getNotificationManager().getNotifications();
//				if(notificationList!=null){
//					if(notificationList.size() > 0){
//						Notification notification = notificationList.get(position);
//						appData.getNotificationManager().setNew(notification);
//						v.findViewById(R.id.notification_star).setVisibility(View.GONE);
//						switch(notification.getNotificationType()){
//							case Notification.NotificationTypeAlert:
//								gotoAlert(notification,id);
//							break;
//							case Notification.NotificationTypeOffer:
//								gotoOffer(notification);
//							break;
//							case Notification.NotificationTypeRZCert:
//								gotoCert(notification);
//							break;
//						}
//					}
//				}
			}
		};
	

		private boolean displayDefaultNotifications() {
			if(!appData.getWoStatus()||!appData.getRzStatus() || appData.isChangesInPnPreferences()){
				notificationAdapter = new NotificationAdapter(getDefaultNotification());
				lv = (ListView) findViewById(android.R.id.list);
				registerForContextMenu(lv);
				lv.setAdapter(notificationAdapter);
				lv.setOnItemClickListener(notificationClickedHandler);
				return true;
			}		
			return false;
		}

	private void getPrefferedCatIdsFromPNServer() {
		isOnResumeCalled=true;
		if (prefferedCatIds == null || prefferedCatIds.size() == 0
				|| appData.isChangesInPnPreferences()) {
			prefferedCatIds = new ArrayList<String>();
			new GetPreferencesTask(this).execute();
		} else {
			showList();
		}

	}

	private boolean rewardZoneLogin() {
		OAuthAccessor access = appData.getOAuthAccessor();
		if (access != null) {
			ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
			OAuthClient oclient = new OAuthClient(new HttpClient4());
			try {
				String url = AppConfig.getSecureRemixURL()+ AppData.REWARD_ZONE_DATA_PATH;
				rzParser = new RZParser();
				String data = CacheManager.getCacheItem(url,CacheManager.RZ_CACHE);

				if (data == null || data.length() <= 0) {
					CacheManager.clearCache(CacheManager.RZ_CACHE);
					OAuthMessage msg = oclient.invoke(access, url, params1);
					InputStream bodyInputStream =msg.getBodyAsStream();
					data = InputStreamExtensions.InputStreamToString(bodyInputStream);
					CacheManager.setCacheItem(url, data, CacheManager.RZ_CACHE);
				} else {
					BBYLog.i("Push Notifications", "Using Cached Data");
				}

				ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(
						data.getBytes());

				rzParser.parse(Diagnostics.dumpInputStream(_context,bodyConvertInputStream, "Push Notifications",
						"Reward Zone XML: "));

				if (appData.getRzAccount().getRzTransactions().size() == 0) {

					RZAccount rzAccount = rzParser.getRzAccount();
					appData.setRzAccount(rzParser.getRzAccount());
					Map<String, String> omnitureParams = new HashMap<String, String>();
					omnitureParams.put("rz_id", rzAccount.getId());
					omnitureParams.put("rz_tier", rzAccount.getStatusDisplay());
					EventsLogging.fireAndForget(EventsLogging.RZ_LOGIN_SUCCESS,omnitureParams);
				}
				return true;
			} catch (Exception ex) {
				BBYLog.printStackTrace("Push Notifications", ex);
				if (ex.getMessage().contains("401")) {
					BBYLog.i("Push Notifications","Unauthorized Exception: Need to reauth");
				}
				return false;
			}
		}
		return false;
	}
	
	private void enabledNotificationTray(boolean isYellow,boolean displayNotifCount){
		if(isYellow){
				((LinearLayout)findViewById(R.id.home_notifications_handle)).setBackgroundResource(R.drawable.bg_notifications_bar_active);
				((RelativeLayout)findViewById(R.id.home_notifications_badge)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.home_notifications_number)).setVisibility(View.VISIBLE);
				if(!displayNotifCount){
					((TextView)findViewById(R.id.home_notifications_number)).setText("!");
				}else {
					if(appData.getNotificationManager().getNewNotificationCount() > 0)
						((TextView)findViewById(R.id.home_notifications_number)).setText(Integer.toString(appData.getNotificationManager().getNewNotificationCount()));
					else{
						((LinearLayout)findViewById(R.id.home_notifications_handle)).setBackgroundResource(R.drawable.bg_notifications_bar_inactive);
						((RelativeLayout)findViewById(R.id.home_notifications_badge)).setVisibility(View.GONE);
						((TextView)findViewById(R.id.home_notifications_number)).setVisibility(View.GONE);
					}
				}
	  }else{
			((LinearLayout)findViewById(R.id.home_notifications_handle)).setBackgroundResource(R.drawable.bg_notifications_bar_inactive);
			((RelativeLayout)findViewById(R.id.home_notifications_badge)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.home_notifications_number)).setVisibility(View.GONE);
		}
	}
	
	protected void checkStatusPnServer() {
		
		PnServerStatusAsynTask pnServerStatusAsynTask = new PnServerStatusAsynTask(Home.this);
		pnServerStatusAsynTask.execute();
		
	}
	
	private class PnServerStatusAsynTask extends BBYAsyncTask {

		String responseString = null;
		
		public PnServerStatusAsynTask(Activity activity) {
			super(activity);
			//enableLoadingDialog(false);
		}

		@Override
		public void doTask() throws Exception {
			responseString = PushNotificationsLogic.sendResponsePNServer();
		}
		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new PnServerStatusAsynTask(Home.this).execute();
				}
			});
		}

		@Override
		public void doFinish() {
			if (responseString != null) {
					String status= getResponse(responseString);
					if (status.equals("true")) {
						Intent intent = new Intent(Home.this,PushNotificationsActivity.class);
						startActivity(intent);
					}else{
						doError();
					}
			} else {
				doError();
			}
		}

	}
	/*protected void registeringDeviceToPNS(String optin) {
		AppData.setOptin(optin);
		AppData.setOptinStatus(true);
		new RegisterDevice();
	}*/


	public String getResponse(String responseString) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(responseString);
			if (jsonObject.has(PushNotifcationData.STATUS)) {
				return jsonObject.getString(PushNotifcationData.STATUS);
			}
		} catch (JSONException e) {
			return "exception";
		}
		
		return PushNotifcationData.STATUS_FALSE;
	}
	
	@Override
	protected void onRestart() {
		appData.getNotificationManager().sortNotifications();
		super.onRestart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			SlidingDrawer homeNotifcationsSlider = (SlidingDrawer) findViewById(R.id.home_notifcations_slider);
			boolean isOpened = homeNotifcationsSlider.isOpened();
			if (isOpened) {
				homeNotifcationsSlider.animateClose();
				return true;
			}

			if (!AppData.isCurrentLocationAlloweded(this)) {
				AppData.getSharedPreferences()
						.edit()
						.putInt(AppData.CURRENT_LOCATION_ALLOWEDED,
								AppData.KEY_EXIST).commit();
			}

			AppData.getSharedPreferences().edit()
					.putInt(AppData.HOME_SELECTED_PAGE, 0).commit();

			// Setting the Google Event tracker status to active
			AppData.setTrackerStatus(Home.this, true);
			// AppData.setHomeScreenBackPressed(true);
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		IcrUtil.clearIcrHashMap();
		isOnResumeCalled=false;
		super.onDestroy();
	}
}
