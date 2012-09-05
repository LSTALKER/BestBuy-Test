package com.bestbuy.android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.location.PostalCode;
import com.bestbuy.android.storeevent.util.StoreUtils;

public class StoreAvailability  extends MenuActivity {
	
	private String zipCode = null;
	private EditText enterItem;
	private final int PROGRESS_DIALOG = 1000;
	private Intent prevIntent = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store_availability);
		setupStores();
		findZipCodeFromLatLng();
	}
	
	private void findZipCodeFromLatLng() {
		prevIntent = getIntent();
		
		if(prevIntent != null) {
			final double latitude = prevIntent.getDoubleExtra(StoreUtils.LATITUDE, 0.0);
			final double longitude = prevIntent.getDoubleExtra(StoreUtils.LONGITUDE, 0.0);
		
			if(latitude != 0.0 && longitude != 0.0) {
				if(BaseConnectionManager.isNetAvailable(StoreAvailability.this))
					zipCodeTask.execute(latitude, longitude);
				else
					updateUI();
			} else
				updateUI();
		}
	}
	
	private AsyncTask<Double, Void, String> zipCodeTask = new AsyncTask<Double, Void, String>() {
		
		protected void onPreExecute() {
			showDialog(PROGRESS_DIALOG);
		};
		
		@Override
		protected String doInBackground(Double... params) {
			return new PostalCode().getZipCode(StoreAvailability.this, params[0], params[1]);
		}
		
		protected void onPostExecute(String result) {
			updateUI();
		};
		
	};
	
	private void updateUI() {
		
		removeDialog(PROGRESS_DIALOG);
		
		if(zipCode == null && prevIntent != null) {
			if(prevIntent.hasExtra(StoreUtils.ZIP_CODE))
				zipCode = prevIntent.getStringExtra(StoreUtils.ZIP_CODE);
			else
				zipCode = AppData.getSharedPreferences().getString(AppData.LAST_USED_LOCATION, null);
		}
		
		if(zipCode != null) {
			enterItem.setText(zipCode);
			enterItem.setSelection(zipCode.length());
		} 
	}
	
	private void setupStores() {
		enterItem = (EditText) findViewById(R.id.store_locator_list_zip);
		final Button findLocations = (Button) findViewById(R.id.find_store_btn);
		
		// capture the enter key as a submit key.
		enterItem.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(enterItem.getWindowToken(), 0);
					
					zipCode = enterItem.getText().toString();
					
					if (zipCode.length() < 5 || zipCode.equals("00000"))
						showNotification("Please enter a valid Zip Code");
					else
						goToSearchListActivity();
					
					return true;
				}
				return false;
			}
		});
		
		findLocations.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(findLocations.getWindowToken(), 0);
				
				zipCode = enterItem.getText().toString();
				
				if (zipCode.length() < 5 || zipCode.equals("00000"))
					showNotification("Please enter a valid Zip Code");
				else
					goToSearchListActivity();
			}
		});
	}

	private void goToSearchListActivity() {
		
		Intent searchResultIntent = new Intent(this, SearchResultList.class);
		searchResultIntent.putExtra(StoreUtils.ZIP_CODE, zipCode);
		searchResultIntent.putExtra(StoreUtils.CLICKCOUNTER, 1);
		
		AppData.getSharedPreferences().edit().putString(AppData.LAST_USED_LOCATION, zipCode).commit();
		
		Intent intent = getIntent();
		if (intent != null) {
			if(intent.hasExtra("categoryName"))
				searchResultIntent.putExtra("categoryName", intent.getExtras().getString("categoryName"));
			
			if(intent.hasExtra(StoreUtils.SORT_SELECTION))
				searchResultIntent.putExtra(StoreUtils.SORT_SELECTION, intent.getExtras().getInt(StoreUtils.SORT_SELECTION));
			
			if(getIntent().hasExtra(StoreUtils.IS_PRODUCT_SEARCH))
				searchResultIntent.putExtra(StoreUtils.IS_PRODUCT_SEARCH, getIntent().getExtras().getBoolean(StoreUtils.IS_PRODUCT_SEARCH));
		}
		
		startActivity(searchResultIntent);
		finish();
	}
	
	private void showNotification(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		switch (id) {
			case PROGRESS_DIALOG:
				ProgressDialog progressLocDialog = new ProgressDialog(this);
				progressLocDialog.setIndeterminate(true);
				progressLocDialog.setCancelable(true);
				progressLocDialog.setMessage("Loading...");
				return progressLocDialog;
		}
		
		return dialog;
	}
	
}
