package com.bestbuy.android.activity.commerce;

import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.commerce.CCommerce;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.CErrorCodesHelper;
import com.bestbuy.android.util.CommerceAsyncTask;
import com.bestbuy.android.util.QRCodeWorker;

/**
 * Associates a gift card with the order
 * @author Recursive Awesome
 */
public class CommerceGiftCardBalance extends MenuActivity {
	private AppData appData;
	CCommerce commerce;
	boolean hasNumbers = false;
	boolean hasPin = false;
	Button saveButton;
	String gift_id;
	ImageView camera_button;
	boolean gift_both = false;
	String cardNum;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication)this.getApplication()).getAppData();
		commerce = appData.getCommerce();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			gift_id = extras.getString("gift_id");
			if(gift_id.equals("0"))
				gift_both=true;
			}
		setContentView(R.layout.commerce_gift_card_balance);
		saveButton = (Button) findViewById(R.id.commerce_gift_card_balance_save);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputManager.hideSoftInputFromWindow(saveButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
				launchGiftCardTask();
			}
		});
		PackageManager pm = getPackageManager();
		if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
		camera_button=(ImageView)findViewById(R.id.camera_click_button);
		camera_button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				new QRCodeWorker().openQRCode(CommerceGiftCardBalance.this,"GIFT_CARD");
				CommerceGiftCardBalance.this.finish();
			}
		});
		}

		//Enable submit button when 10 characters have been entered
		
		final EditText gcPin = (EditText) findViewById(R.id.commerce_gift_card_balance_pin);
		final Button saveBtn = (Button) findViewById(R.id.commerce_gift_card_balance_save);
		final EditText gcNumber = (EditText) findViewById(R.id.commerce_gift_card_balance_number);
		if (!gift_both) {
			gcNumber.setText(gift_id);
			hasNumbers = true;
		}
		gcNumber.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable editable) {
				if (editable.toString().length() == 16 ) {
					hasNumbers = true;
				} else {
					hasNumbers = false;
				}
				if (hasNumbers && hasPin) {
					saveBtn.setEnabled(true);
				} else {
					saveBtn.setEnabled(false);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});

		gcPin.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable editable) {
				if (editable.toString().length() == 4) {
					hasPin = true;
				} else {
					hasPin = false;
				}
				if (hasNumbers && hasPin) {
					saveBtn.setEnabled(true);
				} else {
					saveBtn.setEnabled(false);
				}
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
		});
	}
	
	private void launchGiftCardTask() {
		GiftCardTask cardTask = new GiftCardTask(this);
		cardTask.setLoadingText("Checking Gift Card Balance...");
		cardTask.execute();
	}
	
	private class GiftCardTask extends CommerceAsyncTask {
		String balance;
		
		public GiftCardTask(Activity activity) {
			super(activity,commerce);
		}

		@Override
		public void doFinish() {
			//Show pop-up with gift card details
			if(activity==null || activity.isFinishing()) {  
				return;
			}
   			AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
			alertDialog.setTitle("Best Buy");
		    alertDialog.setMessage("Remaining Balance:\n$" + balance);
		    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
				  //return;
					new QRCodeWorker().openQRCode(CommerceGiftCardBalance.this,"GIFT_CARD");
					CommerceGiftCardBalance.this.finish();
		      } }); 
			alertDialog.show();
		}

		@Override
		public void doTask() throws Exception {
			if (commerce.getGiftCardLink() == null) {
				Map<String, String> globalConfig = AppData.getGlobalConfig();
				String apiKey = globalConfig.get("capiAPIKey");
				commerce.parse(AppConfig.getCommerceStartURL() +  "?apiKey=" + apiKey);
				String capiErrorsURL = AppData.getGlobalConfig().get("capiErrors2URL");
				String errorsJSON = APIRequest.makeGetRequest(capiErrorsURL, null, null, false);
				CErrorCodesHelper.loadErrorCodes(errorsJSON);
			}
			else{
				Map<String, String> globalConfig = AppData.getGlobalConfig();
				String apiKey = globalConfig.get("capiAPIKey");
				commerce.parse(AppConfig.getCommerceStartURL() +  "?apiKey=" + apiKey);
			}
			cardNum = ((EditText) findViewById(R.id.commerce_gift_card_balance_number)).getText().toString();
			String pin = ((EditText) findViewById(R.id.commerce_gift_card_balance_pin)).getText().toString();
			balance = commerce.checkGiftCardBalance(cardNum, pin);
		}
		
		@Override
		public void doCancelReconnect() {
			CommerceGiftCardBalance.this.finish();
		}
		
		@Override
		public void doReconnect() {
			new GiftCardTask(CommerceGiftCardBalance.this).execute();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
