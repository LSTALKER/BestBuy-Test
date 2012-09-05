package com.bestbuy.android.upgradechecker.activity;
 
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.upgradechecker.activity.helper.PhoneNumberClearingTouch;
import com.bestbuy.android.upgradechecker.activity.helper.PhoneNumberFormattingTextWatcher;
import com.bestbuy.android.upgradechecker.data.Subscriber;
import com.bestbuy.android.upgradechecker.logic.UpgradeCheckerDataLogic;
import com.bestbuy.android.upgradechecker.util.UpgradeCheckerUtils;
import com.bestbuy.android.upgradechecker.weblayer.UpgradeEligibilityService;
import com.bestbuy.android.util.BBYAsyncTask;

public class ATandTUpgradeChecker extends MenuActivity implements OnLongClickListener {

	private String zip; 
	private EditText phoneNumberEditText, last4SSNEditText, zipCodeEditText;	
	private boolean clearFieldOnResume = false;
	private Drawable closeImage;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgradechecker_at_t);
		final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		phoneNumberEditText = (EditText) findViewById(R.id.phone_number_field);
		phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());	
		phoneNumberEditText.setFocusableInTouchMode(true);			 
		last4SSNEditText = (EditText) findViewById(R.id.last4_ssn_field);
		last4SSNEditText.setTypeface(Typeface.DEFAULT);
		last4SSNEditText.setFocusableInTouchMode(true);
		zipCodeEditText = (EditText) findViewById(R.id.billing_zipcode_field);
		zipCodeEditText.setFocusableInTouchMode(true);
		phoneNumberEditText.setOnLongClickListener(this);
		last4SSNEditText.setOnLongClickListener(this);
		zipCodeEditText.setOnLongClickListener(this);	
		Button continueBtn=(Button)findViewById(R.id.Continue_btn_blue);
		continueBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				imm.hideSoftInputFromWindow(last4SSNEditText.getWindowToken(), 0);
				if (phoneNumberEditText.getText().toString().trim().length() == 0) {
					popupDialog("Phone number cannot be left empty");
				} else if (phoneNumberEditText.getText().toString().trim().length() < 12|| (!phoneNumberEditText.getText().toString().trim().contains("-"))) {
					popupDialog("Please enter a valid phone number");
				} else if (zipCodeEditText.getText().toString().trim().length() == 0) {
					popupDialog("Zip code cannot be left empty");
				} else if (zipCodeEditText.getText().toString().trim().length() < 5) {
					popupDialog("Please enter a valid zip code");
				} else if (last4SSNEditText.getText().toString().trim().length() == 0) {
					popupDialog("SSN cannot be left empty");
				} else if (last4SSNEditText.getText().toString().trim().length() < 4) {
					popupDialog("Please enter a valid SSN Number");
				} else {
					new UpgradeCheckerTask(ATandTUpgradeChecker.this).execute();
				}
			}
		});
		
		closeImage = getResources().getDrawable(R.drawable.clear_text);
		closeImage.setBounds(0, 0, closeImage.getIntrinsicWidth(), closeImage.getIntrinsicHeight());
		phoneNumberEditText.setOnTouchListener(new PhoneNumberClearingTouch(phoneNumberEditText,closeImage,UpgradeCheckerUtils.AT_AND_T_CLEAR_PH_NO));
		phoneNumberEditText.setPadding(15, 0,5, 0);
	}
	
	public void onResume(){
		super.onResume();
		if(clearFieldOnResume){
			clearInputField();
			clearFieldOnResume = false;
		}	
		String cachedPhoneNumber = AppData.getSharedPreferences().getString(AppData.ATT_UPGRADE_CHECKER_PHONE_NUMBER, null);
		if(cachedPhoneNumber != null){
			phoneNumberEditText.setText(cachedPhoneNumber);
			phoneNumberEditText.setCompoundDrawables(null, null, closeImage, null);
			
		}
	}
	
	private void popupDialog(String message){
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return;
		}
		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		alert.setMessage(message);
		alert.setNegativeButton("OK", new DialogInterface.OnClickListener(){

			public void onClick(DialogInterface dialog, int which) {
				if((phoneNumberEditText.getText().toString().trim().length()>0)&&(last4SSNEditText.getText().toString().trim().length()>0)&&(zipCodeEditText.getText().toString().trim().length()>0))
				{
					if (!(phoneNumberEditText.getText().toString().trim().length() < 12)||!(last4SSNEditText.getText().toString().trim().length()<4)||!(zipCodeEditText.getText().toString().trim().length()<5))
						clearInputField();
				}
			}
		});
		alert.create();
		alert.show();
	}

	protected BestBuyApplication getApp() {
		return (BestBuyApplication) getApplicationContext();
	}
	
	public boolean onLongClick(View v) {		
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		else if (keyCode == KeyEvent.KEYCODE_HOME) {
			clearFieldOnResume = false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void clearInputField(){
		phoneNumberEditText.setText("");
		last4SSNEditText.setText("");
		zipCodeEditText.setText("");
		phoneNumberEditText.clearFocus();
		last4SSNEditText.clearFocus();
		zipCodeEditText.clearFocus();
		String cachedPhoneNumber = AppData.getSharedPreferences().getString(AppData.ATT_UPGRADE_CHECKER_PHONE_NUMBER, null);
		if(cachedPhoneNumber != null)
			phoneNumberEditText.setText(cachedPhoneNumber);
	}
	
	class UpgradeCheckerTask extends BBYAsyncTask{
		UpgradeEligibilityService service = null;
		BestBuyApplication application = null;
		
		public UpgradeCheckerTask(Activity activity) {
			super(activity, "Checking eligibility...");			
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();	
			application = getApp();
			application.setPrimaryPhoneNumber(phoneNumberEditText.getText().toString());
			zip=zipCodeEditText.getText().toString();
			application.setZipCode(zip);
			application.setLast4SSN(last4SSNEditText.getText().toString());
			application.setPin(null);
			application.setCarrierCode("ATT");
			service = new UpgradeEligibilityService(ATandTUpgradeChecker.this);		    		
			service.setApplication(application);
		}

		@Override
		public void doTask() throws Exception {
			HashMap results = service.call();
			application.setResults(results);
		}

		@Override
		public void doFinish() {
			UpgradeChecker.SELECTED_CARRIER="ATT";
			HashMap results = getApp().getResults();
			if (results != null && results.get("error") != null && ((String) results.get("error")).length() > 0) {
				
				popupDialog("Please double-check your account info and try again!");
				return;
			}
			UpgradeCheckerDataLogic dataLogic=new UpgradeCheckerDataLogic(ATandTUpgradeChecker.this);
			List<Subscriber> subscriberList=dataLogic.getSubcriberList(zip);
			getApp().setSubscribers(subscriberList);
			clearFieldOnResume = true;
			AppData.getSharedPreferences().edit().putString(AppData.ATT_UPGRADE_CHECKER_PHONE_NUMBER, phoneNumberEditText.getText().toString()).commit();
			if(subscriberList.size()==1){
				Intent intent = new Intent(ATandTUpgradeChecker.this, UpgradeCheckerTermsandConditions.class);
				intent.putExtra("Subscriber", subscriberList.get(0));
				startActivity(intent);
			}
			else{
				Intent intent = new Intent(ATandTUpgradeChecker.this, PhoneNumberListActivity.class);		
				startActivity(intent);
			}
		}
		
		@Override
		public void doReconnect() {
			new UpgradeCheckerTask(ATandTUpgradeChecker.this).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
		
		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new UpgradeCheckerTask(ATandTUpgradeChecker.this).execute();
				}
			}, new OnCancel() {

				public void onCancel() {
					finish();
				}
			});
		}
	}
}
