package com.bestbuy.android.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.MdotWebView;
import com.bestbuy.android.util.QRCodeWorker;
import com.nullwire.trace.ExceptionHandler;

/**
 * Extend this to handle menus for ListActivites
 * 
 * @author Recursive Awesome
 * 
 */
public abstract class MenuListActivity extends ListActivity {
	private final String TAG = this.getClass().getName();
	protected AppData appData;
	protected final int LOADING_DIALOG = 10000;
	//private Handler trackerHandler = new Handler();
	
	//For mDOT
	protected static final String MDOT_URL = "mDotURL";

	boolean subCategory;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appData = ((BestBuyApplication) this.getApplication()).getAppData();

		if (AppData.EMAIL_ON_FORCE_CLOSE) {
			ExceptionHandler.register(this, getResources().getString(R.string.exception_handler_url));
		}
		
		if (savedInstanceState != null) {
			Diagnostics.StartMethodTracing(this);
			appData.setSearchRequest((SearchRequest) savedInstanceState.getSerializable(AppData.SEARCH_REQUEST));
			Diagnostics.StopMethodTracing(this, TAG, "Deserializing savedInstanceState took: ");
		}
	}
	
	protected void onPause(){
		super.onPause();
		
		//AppData.setTrackerStatus(this, AppData.isApplicationSentToBackground(this));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(AppData.SEARCH_REQUEST, appData.getSearchRequest());
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		MenuActivity.updateCartIcon(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (AppData.getGlobalConfig() == null 
				|| AppData.getGlobalConfig().entrySet() == null 
				|| AppData.getGlobalConfig().entrySet().isEmpty()) {
			//Make call to get global config again in background task
			MenuActivity.runGlobalConfigTask(this);
		}
		MenuActivity.updateCartIcon(this);
		
		// Google Analytics for the Application Launch
		/*trackerHandler.post(new Runnable() {

			public void run() {
				if (AppData.isTrackerActive(MenuListActivity.this)) {
					EventsLogging.fireAndForget(EventsLogging.APP_LAUNCH, null);
					// Setting the Google Event tracker status to inactive
					AppData.setTrackerStatus(MenuListActivity.this, false);
				}
			}
		});*/
	}

	@Override
	protected void onActivityResult(int iRequestCode, int iResultCode, Intent intData) {
		new QRCodeWorker().onActivityResult(iRequestCode, iResultCode, intData, this);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuActivity.buildMenu(this, menu,appData);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuActivity.launchMenuItem(this, item);
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
