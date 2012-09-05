package com.bestbuy.android.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.LocationResult;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.openbox.activity.OpenBoxClearanceActivity;
import com.bestbuy.android.storeevent.activity.StoreInfoActivity;
import com.bestbuy.android.storeevent.activity.helper.adapter.StoreLocatorListAdapter;
import com.bestbuy.android.storeevent.data.CheckDealsNearMeItems;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.storeevent.logic.StoreEventsLogic;
import com.bestbuy.android.storeevent.logic.StoreLocatorListLogic;
import com.bestbuy.android.storeevent.util.CityZipCodeTextValidator;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.BBYProgressDialog;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;

/**
 * Displays a list of store locations based on the input city/zip
 * @author Recursive Awesome
 * @Edited Lalit Kumar Sahoo
 * @Date 19th July 2011 4PM
 */

public class StoreLocatorList extends MenuListActivity implements OnItemClickListener {
	
	private String TAG = this.getClass().getName();
	
	private static final int LOCATION_FIND_IN_MILLISECOND = 0;
	
	private CharSequence errorMessage;
	private String openBoxFlag = null;
	private boolean isOpenBox = false;
	private String searchWord = null;
	private double latitude;
	private double longitude;
	
	private Store store = null;
	private List<Store> storeList;
	private List<StoreEvents> eventsList;
	private InputMethodManager inputMethodManager;
	private CityZipCodeTextValidator cityZipCodeTextValidator;
	
	private EditText enterItem;
	private TextView messageTextView;
	private LinearLayout listLayout;
	private Handler mHandler = new Handler();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stev_locator_list);
		
		//Initialize UI
		initializeUI();
		
		//Set up initial stores
		setupStores();
		
		//find stores while launch the page
		findStoreOnLaunch();
	}
	
	//Initialize UI
	private void initializeUI() {
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		messageTextView = (TextView)findViewById(android.R.id.empty);
		listLayout = (LinearLayout)StoreLocatorList.this.findViewById(R.id.store_locator);
		
		ListView list = (ListView) findViewById(android.R.id.list);
		list.setOnItemClickListener(this);
		
		if(this.getIntent().hasExtra(StoreUtils.IS_OPENBOX))
			isOpenBox = this.getIntent().getBooleanExtra(StoreUtils.IS_OPENBOX, false);
	}
	
	private void setupStores() {
		enterItem = (EditText)findViewById(R.id.store_locator_list_zip);
		cityZipCodeTextValidator = new CityZipCodeTextValidator(enterItem);
		enterItem.addTextChangedListener(cityZipCodeTextValidator);

		// capture the enter key as a submit key.
		enterItem.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					inputMethodManager.hideSoftInputFromWindow(enterItem.getWindowToken(), 0);
					
					searchWord = enterItem.getText().toString();
					
					if(searchWord.length() > 0) {
						if(cityZipCodeTextValidator.isNumber()) {
							
							if(searchWord.length() == 5) {
								findStoresFromInput(true);
							} 
						} else
							findStoresFromInput(false);
					} else
						showDialog(StoreUtils.VALID_SEARCH_DIALOG);
					
					return true;
				}
				
				return false;
			}
		});
		
		Button zipButton = (Button)findViewById(R.id.store_locator_list_zip_button);
		zipButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hide keyboard and run search
				inputMethodManager.hideSoftInputFromWindow(enterItem.getWindowToken(), 0);
				searchWord = enterItem.getText().toString();
				
				if(searchWord.length() > 0) {
					if(cityZipCodeTextValidator.isNumber()) {
						
						if(searchWord.length() == 5) 
							findStoresFromInput(true);
						else
							showDialog(StoreUtils.VALID_ZIPCODE_DIALOG);
					} else 
						findStoresFromInput(false);
				} else 
					showDialog(StoreUtils.VALID_SEARCH_DIALOG);
			}
		});
	}
	
	//find stores while launch the page
	private void findStoreOnLaunch() {
		if(AppData.isCurrentLocationAlloweded(this)) {
	    	mHandler.postDelayed(new Runnable() {
	    		public void run() { 		
	    			if(!BaseConnectionManager.isNetAvailable(StoreLocatorList.this) || BaseConnectionManager.isAirplaneMode(StoreLocatorList.this)) {
		    				NoConnectivityExtension.noConnectivity(StoreLocatorList.this, new OnReconnect() {
		    					public void onReconnect() {
		    						findStoreOnLaunch();
		    					}		
		    				}, new NoConnectivityExtension.OnCancel() {
		    					public void onCancel() {
		    						finish();
		    					}
		    				});
	    			} else {
	    				findStores(true);
	    			}
	    		}
	    	}, LOCATION_FIND_IN_MILLISECOND);
		} else {
			listLayout.setVisibility(View.VISIBLE);
			messageTextView.setText("");
		}
	}
	
	private void findStoresFromInput(boolean isZipCode) {
		if(isZipCode) {
			new StoreLocateZipCodeTask(StoreLocatorList.this).execute();
			
		} else {
			mHandler.postDelayed(new Runnable() {
	    		public void run() { 		
	    			
	    			if(!BaseConnectionManager.isNetAvailable(StoreLocatorList.this) || BaseConnectionManager.isAirplaneMode(StoreLocatorList.this)) {
	    				NoConnectivityExtension.noConnectivity(StoreLocatorList.this, new OnReconnect() {
	    					public void onReconnect() {
	    						findStoresFromInput(false);
	    					}		
	    				});
	    				
	    			} else {
	    			
	    				try {
	    					Geocoder geoCoder = new Geocoder(StoreLocatorList.this, Locale.US);
	    		
	    					List<Address> addressList = geoCoder.getFromLocationName(searchWord, 5);
	    		
	    					Iterator<Address> iterator = addressList.iterator();
	    					Address address = null;
	    		
	    					while (iterator.hasNext()) {
	    						Address tempaddress = (Address) iterator.next();
	    		
	    						if (tempaddress.getCountryCode() != null && tempaddress.getCountryCode().equals("US")) {
	    							address = tempaddress;
	    							break;
	    						}
	    					}
	    		
	    					if (address != null) {
	    						latitude = address.getLatitude();
	    						longitude = address.getLongitude();
	    						findStores(false);
	    					} else {
	    						errorMessage = "The location you entered '" + searchWord + "' was not found. Please enter a different location.";
	    						showDialog(StoreUtils.LOCATION_NOT_FOUND_DIALOG);
	    					}
	    					
	    				} catch (IOException e) {
	    					BBYLog.printStackTrace(TAG, e);
	    					BBYLog.e(TAG, "Exception in StoreLocatorList:findStoresFromInput: " + searchWord + " EX: " + e.getMessage());
	    					errorMessage = "The location you entered '" + searchWord + "' was not found. Please enter a different location.";
	    					showDialog(StoreUtils.LOCATION_NOT_FOUND_DIALOG);
	    				}
	    			}
	    		}
	    	}, 0);
		}
	}
	
	private void findStores(boolean isOnLoad) {
		showDialog(StoreUtils.PROGRESS_DIALOG);
		
		if(isOnLoad) {
			boolean isLocate = appData.getBBYLocationManager().getLocation(this, locationResult);
			
			if(!isLocate) {
				removeDialog(StoreUtils.PROGRESS_DIALOG);
				if(AppData.isCurrentLocationAlloweded(this)) {
					listLayout.setVisibility(View.VISIBLE);
					messageTextView.setText("");
				} else {
					listLayout.setVisibility(View.VISIBLE);
					messageTextView.setText("");
				}
			}
		} else {
			new StoreLocateTask(StoreLocatorList.this).execute();
		}
	}
	
	public LocationResult locationResult = new LocationResult(){
	    @Override
	    public void gotLocation(final Location location){
	    	AppData.setLocation(location);
	    	
	    	if(location != null) {
	    		latitude = location.getLatitude();
	    		longitude = location.getLongitude();
	    	}
	    	
	    	new StoreLocateTask(StoreLocatorList.this).execute();
	    }
	};
	
	private class StoreLocateTask extends BBYAsyncTask {

		public StoreLocateTask(Activity activity) {
			super(activity, "Finding Locations...");
			enableLoadingDialog(false);
		}
		
		@Override
		public void doTask() throws Exception {
			if(latitude != 0.0 && longitude != 0.0)
				storeList = StoreLocatorListLogic.findNearbyStores(latitude, longitude);
		}

		@Override
		public void doFinish() {
			showView();
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				removeDialog(StoreUtils.PROGRESS_DIALOG);
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						showDialog(StoreUtils.PROGRESS_DIALOG);
						new StoreLocateTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				storeList = null;
				showView();
			}
		}
	}
	
	private class StoreLocateZipCodeTask extends BBYAsyncTask {
		
		public StoreLocateZipCodeTask(Activity activity) {
			super(activity, "Finding Locations...");
		}
		
		@Override
		public void doTask() throws Exception {
			storeList = StoreLocatorListLogic.findNearbyStores(searchWord);
		}

		@Override
		public void doFinish() {
			showView();
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				removeDialog(StoreUtils.PROGRESS_DIALOG);
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						showDialog(StoreUtils.PROGRESS_DIALOG);
						new StoreLocateZipCodeTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				storeList = null;
				showView();
			}
		}
	}
	
	private void showView() {
		removeDialog(StoreUtils.PROGRESS_DIALOG);
		
		inputMethodManager.hideSoftInputFromWindow(enterItem.getWindowToken(), 0);
		
		listLayout.setVisibility(View.VISIBLE); // now show the results
		
		messageTextView.setText("No stores found. Please enter your location above to find stores.");
		
		StoreLocatorListAdapter storeLocatorListAdapter = new StoreLocatorListAdapter(StoreLocatorList.this, storeList);
		setListAdapter(storeLocatorListAdapter);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		store = (Store) parent.getItemAtPosition(position);
		inputMethodManager.hideSoftInputFromWindow(enterItem.getWindowToken(), 0);
		showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
		executeStoreEventsTask();
	}
	
	/*
	 * STORE EVENTS FUNCTIONALITY
	 */
	
	/**
	 * Load Store events on clicking of store item
	 */
	private void executeStoreEventsTask(){
		new StoreEventsTask(this).execute();
	}

	private class StoreEventsTask extends BBYAsyncTask {
		
		public StoreEventsTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}
		
		@Override
		protected void onPreExecute() {
			eventsList = new ArrayList<StoreEvents>();
		}

		@Override
		public void doFinish() {
			executeOpenBoxTask();
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
						new StoreEventsTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else 
				executeOpenBoxTask();
		}
		
		@Override
		public void doTask() throws Exception {
			eventsList = StoreEventsLogic.getStoreEvents(store.getStoreId());
		}
	}
	
	/*
	 * OPEN BOX FUNCTIONALITY
	 */
	private void executeOpenBoxTask(){
		new CheckOpenBoxItemExistTask(StoreLocatorList.this).execute();
	}
	
	private class CheckOpenBoxItemExistTask extends BBYAsyncTask {
		
		public CheckOpenBoxItemExistTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}
		
		@Override
		public void doFinish() {
			onLoadFinish();
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
						new CheckOpenBoxItemExistTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else 
				onLoadFinish();
		}
		
		@Override
		public void doTask() throws Exception {
			openBoxFlag = new CheckDealsNearMeItems().hasDealsNearMeItems(store.getStoreId());
		}
	}

	private void onLoadFinish() {
		Intent intent = new Intent();
		
		if(isOpenBox){
			intent.setClass(StoreLocatorList.this, OpenBoxClearanceActivity.class);
			intent.putExtra(StoreUtils.STORE_ID, store.getStoreId());
			intent.putExtra(StoreUtils.STORE_NAME, store.getName());
		}else{
			intent.setClass(StoreLocatorList.this, StoreInfoActivity.class);
			intent.putExtra(StoreUtils.STORE_INFO, store);
			intent.putExtra(StoreUtils.OPENBOX_FLAG, openBoxFlag);
		}
		appData.setStores(storeList);
		appData.setStoreEvents(eventsList);
		startActivity(intent);
		
		removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
	}
	
	/*
	 * DILOGS AND OTHER ACTIVITIES
	 */
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		AlertDialog alertDialog = null;
		
		switch (id) {
			case StoreUtils.LOCATION_NOT_FOUND_DIALOG:
				if(this==null || this.isFinishing()) {  
					return;
				}
				alertDialog = (AlertDialog) dialog;
				alertDialog.setTitle("");
				alertDialog.setMessage(errorMessage);
				
				LinearLayout mainLayout = (LinearLayout)StoreLocatorList.this.findViewById(R.id.store_locator);
				mainLayout.setVisibility(View.VISIBLE); // now show the results
				removeDialog(StoreUtils.PROGRESS_DIALOG);
				
				if(storeList != null) {
					storeList.clear();
					StoreLocatorListAdapter storeLocatorListAdapter = new StoreLocatorListAdapter(StoreLocatorList.this, storeList);
					setListAdapter(storeLocatorListAdapter);
				}
				
				break;
				
			case StoreUtils.LOCATION_ENABLE_DIALOG:
				if(this==null || this.isFinishing()) {  
					return;
				}
				alertDialog = (AlertDialog) dialog;
				alertDialog.setTitle("");
				alertDialog.setMessage(getResources().getString(R.string.ENABLE_GPS_SETTINGS));
				
				break;
				
			case StoreUtils.VALID_SEARCH_DIALOG:
				if(this==null || this.isFinishing()) {  
					return;
				}
				alertDialog = (AlertDialog) dialog;
				alertDialog.setTitle("");
				alertDialog.setMessage(getResources().getString(R.string.VALID_SEARCH));
				
				break;
				
			case StoreUtils.VALID_ZIPCODE_DIALOG:
				if(this==null || this.isFinishing()) {  
					return;
				}
				alertDialog = (AlertDialog) dialog;
				alertDialog.setTitle("");
				alertDialog.setMessage(getResources().getString(R.string.VALID_ZIPCODE));
				
				break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		switch (id) {
			case StoreUtils.PROGRESS_DIALOG:
				return new BBYProgressDialog(this, "Finding Locations...");
			
			case StoreUtils.PROGRESS_EVENTS_DIALOG:
				return new BBYProgressDialog(this, "Loading content...");
				
			case StoreUtils.LOCATION_NOT_FOUND_DIALOG:
				if(this==null || this.isFinishing()) {  
					return dialog;
				}
				dialog = new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(errorMessage)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								enterItem.setText("");
								dialog.cancel();
							}
						})
						.create();
				
				LinearLayout mainLayout = (LinearLayout)StoreLocatorList.this.findViewById(R.id.store_locator);
				mainLayout.setVisibility(View.VISIBLE); // now show the results
				removeDialog(StoreUtils.PROGRESS_DIALOG);
				
				if(storeList != null) {
					storeList.clear();
					StoreLocatorListAdapter storeLocatorListAdapter = new StoreLocatorListAdapter(StoreLocatorList.this, storeList);
					setListAdapter(storeLocatorListAdapter);
				}
				
				break;
				
			case StoreUtils.LOCATION_ENABLE_DIALOG:
				if(this==null || this.isFinishing()) {  
					return dialog;
				}
				dialog = new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(getResources().getString(R.string.ENABLE_GPS_SETTINGS))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
				break;
				
			case StoreUtils.VALID_SEARCH_DIALOG:
				if(this==null || this.isFinishing()) {  
					return dialog;
				}
				dialog = new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(getResources().getString(R.string.VALID_SEARCH))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
				break;
				
			case StoreUtils.VALID_ZIPCODE_DIALOG:
				if(this==null || this.isFinishing()) {  
					return dialog;
				}
				dialog = new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(getResources().getString(R.string.VALID_ZIPCODE))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
				break;
		}
		
		return dialog;
	}
}
