package com.bestbuy.android.storeevent.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuMapActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.openbox.activity.OpenBoxClearanceCategoryActivity;
import com.bestbuy.android.storeevent.activity.helper.map.StoreOverlay;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAlertDialog;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.StoreLocator;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 * This activity displays the store related information.
 * @author lalitkumar_s
 *
 */
public class StoreInfoActivity extends MenuMapActivity implements OnClickListener {
	private Store store;
	private Dialog shareDialog, callDialog;
	private MapView mapView;
	private MapController controller;
	private LocationManager locationManager;
	private AppData appData;
	private String mSmsBody;
	private String mEmailBody;
	private final String OPENBOX_EXIST_FLAG = "1";
	private String openBoxFlag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.stev_store_info);

		initializePage();
	}

	/**
	 * Initialize the MAP to display on the restricted region of the store info
	 */
	private void initializeMap() {
		locationManager = appData.getBBYLocationManager().getLocationManager(StoreInfoActivity.this);
			
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		AppData.setLocation(location);
		
		initLocationUpdates();
		initMapView();
		initMyLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(onLocationChange);
	}
	
	public void onResume() {
		super.onResume();
		initializeMap();
	}
	
	private void initLocationUpdates() {
		StoreLocator.setLocationUpdates(locationManager, onLocationChange);
	}
	
	/** Find and initialize the map view. */
	private void initMapView() {
		mapView = (MapView) findViewById(R.id.store_locator_map);
		mapView.setSatellite(false);
		controller = mapView.getController();
	}

	/** Start tracking the position on the map. */
	private void initMyLocation() {
		
		controller.setZoom(12);
		Location myLocation = AppData.getLocation();
		if (myLocation != null) {
			controller.animateTo(StoreLocator.getPoint(myLocation.getLatitude(), myLocation.getLongitude()));
		} else {
			controller.animateTo(StoreLocator.getPoint(store.getLat(), store.getLng()));
		}
		
		Drawable marker=getResources().getDrawable(R.drawable.redpin);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		StoreOverlay storeOverlay = new StoreOverlay(marker, mapView, store, false);
		storeOverlay.drawStores(store, false);
		mapView.getOverlays().add(storeOverlay);
		mapView.postInvalidate();
		
		storeOverlay.showStore(0);
		storeOverlay.hideBalloon();
	}

	LocationListener onLocationChange = new LocationListener() {
		public void onLocationChanged(Location location) {
			AppData.setLocation(location);
		}

		public void onProviderDisabled(String provider) {}

		public void onProviderEnabled(String provider) {}

		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Initialize the entire view and display of corresponding data on corresponding UI element.
	 */
	private void initializePage() {
		ImageView storeLocatorImage = (ImageView)findViewById(R.id.store_locator_image);
		TextView storeLocatorCityState = (TextView)findViewById(R.id.store_locator_city_state);
		TextView storeLocatorTodayTime = (TextView)findViewById(R.id.store_locator_today_time);
		TextView storeLocatorPhone = (TextView)findViewById(R.id.store_locator_phone);
		TextView storeLocatorAddress1 = (TextView)findViewById(R.id.store_locator_address_1);
		TextView storeLocatorAddress2 = (TextView)findViewById(R.id.store_locator_address_2);
		TextView storeLocatorAddress3 = (TextView)findViewById(R.id.store_locator_address_3);
		TextView storeLocatorHours = (TextView)findViewById(R.id.store_locator_hours);
		TextView storeTitle = (TextView)findViewById(R.id.store_title);
		
		appData = ((BestBuyApplication)this.getApplication()).getAppData();
		List<StoreEvents> eventsList = appData.getStoreEvents();
		
		RelativeLayout storeLocatorDirectionsLayout = (RelativeLayout)findViewById(R.id.store_locator_directions_layout);
		RelativeLayout storeLocatorEventsLayout = (RelativeLayout)findViewById(R.id.store_locator_events_layout);
		View line4 = (View)findViewById(R.id.line_4);

		if(eventsList != null && !eventsList.isEmpty()) {
			storeLocatorDirectionsLayout.setVisibility(View.VISIBLE);
			storeLocatorDirectionsLayout.setBackgroundResource(R.drawable.commerce_box_middle);
			
			storeLocatorEventsLayout.setVisibility(View.VISIBLE);
			storeLocatorEventsLayout.setBackgroundResource(R.drawable.commerce_box_bottom);
			
			line4.setVisibility(View.VISIBLE);
			
		} else {
			storeLocatorDirectionsLayout.setVisibility(View.VISIBLE);
			storeLocatorDirectionsLayout.setBackgroundResource(R.drawable.commerce_box_bottom);
			
			storeLocatorEventsLayout.setVisibility(View.GONE);
			line4.setVisibility(View.GONE);
		}
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null && bundle.containsKey(StoreUtils.STORE_INFO)) {
			store = (Store)bundle.get(StoreUtils.STORE_INFO);
			storeLocatorCityState.setText(store.getName());
			storeLocatorPhone.setText(StoreUtils.changeToPhoneFormat(store.getPhone()));
			storeLocatorHours.setText(StoreUtils.splitTimeDayWise(store.getHours(), "\n"));
			storeLocatorAddress1.setText(store.getName());
			storeLocatorAddress2.setText(store.getAddress());
			storeLocatorAddress3.setText(store.getCity() + ", " + store.getRegion() + " " + store.getPostalCode());
			storeLocatorTodayTime.setText(StoreUtils.getTodayOpeningTime(store.getHours()));
			
			storeTitle.setText(store.getLongName());
			
			String imageUrl = store.getStoreImage();
			
			if(imageUrl != null && !imageUrl.equals("")) {
				ImageProvider.getBitmapImageOnThread(imageUrl, storeLocatorImage);
			} else {
				storeLocatorImage.setImageResource(R.drawable.store_image);
			}
			
			mSmsBody = storeTitle.getText() + "\n"
					 	+ storeLocatorPhone.getText() + "\n"
					 	+ "Address: " + storeLocatorAddress1.getText() + "\n"
					 	+ storeLocatorAddress2.getText() + "\n"
					 	+ storeLocatorAddress3.getText();
			
			String linkUrl =AppConfig.getStoreLocatorMapURL()+"?f=d&daddr=" + store.getLat() + "," + store.getLng();
				
			mEmailBody = "<b>" + storeTitle.getText() + "</b>" + "<br />"
					+ "<b>" + "Phone:" + "</b>" + "<br />"
				 	+ storeLocatorPhone.getText() + "<br /><br />"
				 	+ "<b>" + "Address: " + "</b>" + "<br />"
				 	+ store.getLongName() + "<br />"
				 	+ storeLocatorAddress2.getText() + "<br />"
				 	+ storeLocatorAddress3.getText() + "<br /><br />"
				 	+ "<b>" + "Hours: " + "</b>" + "<br />"
				 	+ StoreUtils.splitTimeDayWise(store.getHours(), "<br />") + "<br /><br />"
				 	+ "<b>" + "Directions:" + "</b>" + "<br />"
				 	+ "<a href=\""+ linkUrl + "\">Click for directions</a>" + "<br /><br />";
		}
		
		if(bundle != null && bundle.containsKey(StoreUtils.OPENBOX_FLAG)){
			openBoxFlag = bundle.getString(StoreUtils.OPENBOX_FLAG);
		}
	
		RelativeLayout openbox = (RelativeLayout)findViewById(R.id.store_locator_openbox_items_layout);
		RelativeLayout clearancebox = (RelativeLayout)findViewById(R.id.store_locator_clearance_items_layout);
		View line5 = (View) findViewById(R.id.line_5);
		View line6 = (View) findViewById(R.id.line_6);
		if (openBoxFlag != null) {
			if (openBoxFlag.equals(OPENBOX_EXIST_FLAG)) {
				storeLocatorDirectionsLayout.setBackgroundResource(R.drawable.commerce_box_middle);
				storeLocatorEventsLayout.setBackgroundResource(R.drawable.commerce_box_middle);
				openbox.setVisibility(View.VISIBLE);
				clearancebox.setVisibility(View.VISIBLE);
			} else {
				openbox.setVisibility(View.GONE);
				clearancebox.setVisibility(View.GONE);
				line5.setVisibility(View.GONE);
				line6.setVisibility(View.GONE);
			}
		} else {
			openbox.setVisibility(View.GONE);
			clearancebox.setVisibility(View.GONE);
			line5.setVisibility(View.GONE);
			line6.setVisibility(View.GONE);
		}
	}
	
	private String getSmsMessage() {
		return mSmsBody;
	}
	
	private Spanned getEmailMessage() {
		return Html.fromHtml(mEmailBody);
	}
	
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		
		switch (id) {
			case R.id.store_locator_share_info:
				showShareInfoDialog();
				break;
				
			case R.id.store_locator_call_stores:
				showCallDialog();
				break;	
				
			case R.id.store_locator_directions_layout:
				intent = new Intent();
				intent.setClass(StoreInfoActivity.this, StoreLocatorMapActivity.class);
				intent.putExtra(StoreUtils.STORE_INFO, store);
				startActivity(intent);
				
				break;
				
			case R.id.store_locator_events_layout:
				intent = new Intent();
				intent.setClass(StoreInfoActivity.this, StoreEventsActivity.class);
				intent.putExtra(StoreUtils.STORE_IMAGE, store.getStoreImage());
				startActivity(intent);
				break;
				
			case R.id.store_locator_openbox_items_layout:
				intent = new Intent();
				intent.setClass(StoreInfoActivity.this, OpenBoxClearanceCategoryActivity.class);
				intent.putExtra(StoreUtils.FROM_SOURCE, StoreUtils.OPENBOX_ITEMS);
				intent.putExtra(StoreUtils.STORE_ID, store.getStoreId());
				intent.putExtra(StoreUtils.STORE_NAME, store.getName());
				startActivity(intent);
				break;
			case R.id.store_locator_clearance_items_layout:
				intent = new Intent();
				intent.setClass(StoreInfoActivity.this, OpenBoxClearanceCategoryActivity.class);
				intent.putExtra(StoreUtils.FROM_SOURCE, StoreUtils.CLEARANCE_ITEMS);
				intent.putExtra(StoreUtils.STORE_ID, store.getStoreId());
				intent.putExtra(StoreUtils.STORE_NAME, store.getName());
				startActivity(intent);
				break;
		}
	}
	
	/**
	 * Opens the share info dialog.
	 * Here you can share the store info through multiple applications like Email and SMS.
	 */
	private void showShareInfoDialog() {
		shareDialog = new Dialog(this);
		shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		shareDialog.setContentView(R.layout.stev_share_info_dialog);
		shareDialog.setCancelable(true);
		shareDialog.show();
		
		Button cancelBtn = (Button)shareDialog.findViewById(R.id.store_locator_cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareDialog.cancel();
			}
		});
		
		Button emailBtn = (Button)shareDialog.findViewById(R.id.store_locator_email);
		emailBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareOnEmail();
				shareDialog.cancel();
			}
		});
		
		Button smsBtn = (Button)shareDialog.findViewById(R.id.store_locator_sms);
		smsBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareOnSMS();
				shareDialog.cancel();
			}
		});
	}
	
	/**
	 * It redirects to the native Gmail application through which the content to be share via mail.
	 */
	private void shareOnEmail() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/html");

		intent.putExtra(Intent.EXTRA_SUBJECT, store.getLongName());
		intent.putExtra(Intent.EXTRA_TEXT, getEmailMessage());
		
		try {
			
		 	final PackageManager pm = getPackageManager();
		    final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		    ResolveInfo best = null;
		    
		    for (final ResolveInfo info : matches) {
		    	if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) 
		    		best = info;
		    }
		    
		    if (best != null) {
		    	intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		    	startActivity(intent);
		 	} else
		 		showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		    
		} catch (Exception e) {
			showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		}
	}
	
	/**
	 * It redirects to the native SMS application through which the content to be share via sms.
	 */
	private void shareOnSMS() {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.putExtra("sms_body", getSmsMessage());  
		intent.setType("vnd.android-dir/mms-sms"); 
		startActivity(intent); 
	}

	/**
	 * Opens the call to store number dialog.
	 * Here you can call to the store directly.
	 */
	private void showCallDialog() {
		callDialog = new Dialog(this);
		callDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		callDialog.setContentView(R.layout.stev_call_dialog);
		callDialog.setCancelable(true);
		callDialog.show();
		
		TextView numberText = (TextView)callDialog.findViewById(R.id.store_locator_call_number);
		Button cancelBtn = (Button)callDialog.findViewById(R.id.store_locator_cancel);
		Button callBtn = (Button)callDialog.findViewById(R.id.store_locator_call);
		
		numberText.setText(StoreUtils.changeToPhoneFormat(store.getPhone()));
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				callDialog.cancel();
			}
		});
		
		callBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				callDialog.cancel();
				Intent dialIntent = new Intent();
				dialIntent.setAction(Intent.ACTION_CALL);
				dialIntent.setData(Uri.parse("tel:" + store.getPhone()));
				startActivity(dialIntent);
			}
		});	
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return dialog;
		}
		
		return new BBYAlertDialog(this).createAlertDialog(id);
	}
}
