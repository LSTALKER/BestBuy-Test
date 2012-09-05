package com.bestbuy.android.hem.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuListActivity;
import com.bestbuy.android.activity.TermsAndConditions;
import com.bestbuy.android.hem.activity.helper.RebateListAdapter;
import com.bestbuy.android.hem.library.dataobject.EcoRebatesResponseDetails;
import com.bestbuy.android.hem.library.dataobject.ProductRebateDetails;
import com.bestbuy.android.hem.library.dataobject.RebateProgram;
import com.bestbuy.android.hem.library.util.LocalRebateDetailsParser;
import com.bestbuy.android.hem.webblock.EcoRequest;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.UTFCharacters.UTF;

public class LocalRebates extends MenuListActivity {

	public static final String PRODUCT_NAME = "PRODUCT_NAME";
	public static final String PRODUCT_LIST_PRICE = "PRODUCT_LIST_PRICE";
	public static final String PRODUCT_MODEL = "PRODUCT_MODEL";
	public static final String PRODUCT_SKU = "PRODUCT_SKU";
	public static final String ECORABTE_RESPONSE_DETAILS = "ECORABTE_RESPONSE_DETAILS";
	public static final String ECORABTE_PRODUCT_DETAILS = "ECORABTE_PRODUCT_DETAILS";
	public static final String DISPLAY_MESSAGE = "DISPLAY_MESSAGE";
	public static final String SELECTED_BRAND = "SELECTED_BRAND";
	public static final String SELECTED_BRAND_POSITIONS = "SELECTED_BRAND_POSITIONS";
	public static final String APPLIANCE_TEXT = "APPLIANCE_TEXT";
	public static final String SELECTED_ITEMS = "SELECTED_ITEMS";
	public static final String SELECTED_ITEMS_POSITION = "SELECTED_ITEMS_POSITION";
	
	public static final String POSITION = "position";
	
	private String productName = "";
	private String productSalePrice = "";
	private String productModelNumber = "";
	private String productSku = "";
	private TextView rebateProductNameTv;
	private TextView rebateProductPriceTv;
	private TextView rebateProductModelTv;
	private EditText rebateZipEt;
	private Button rebateGoBtn;
	private TextView rebateSearchResultTv;
	private TextView rebateNotAvailableMsgTv;
	private TextView rebateProductEmailTv2;
	private TextView terms;
	private LinearLayout localrebateEmailLL;
	private LinearLayout rebateFooterLL;
	private LinearLayout rebateZipLL;
	private int position;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_rebates);
				
		rebateProductNameTv = (TextView)findViewById(R.id.rebateProductNameTv);
		rebateProductPriceTv = (TextView)findViewById(R.id.rebateProductPriceTv);
		rebateProductModelTv = (TextView)findViewById(R.id.rebateProductModelTv);
		rebateZipEt = (EditText)findViewById(R.id.rebateZipEt);
		rebateGoBtn = (Button)findViewById(R.id.rebateGoBtn);
		rebateSearchResultTv = (TextView)findViewById(R.id.rebateSearchResultTv);
		rebateNotAvailableMsgTv = (TextView)findViewById(R.id.rebateNotAvailableMsgTv);	
		rebateProductEmailTv2 = (TextView)findViewById(R.id.rebateProductEmailTv2); 
		localrebateEmailLL = (LinearLayout)findViewById(R.id.localrebateEmailLL);
		rebateFooterLL = (LinearLayout)findViewById(R.id.rebateFooterLL);
		rebateZipLL = (LinearLayout)findViewById(R.id.rebateZipLL);
		terms = (TextView) findViewById(R.id.terms);

		Intent intent = getIntent();
		if(intent != null){
			Bundle b = intent.getExtras();
			if(intent.hasExtra(PRODUCT_NAME))
				productName = b.getString(PRODUCT_NAME);
			if(intent.hasExtra(PRODUCT_LIST_PRICE))
				productSalePrice = b.getString(PRODUCT_LIST_PRICE);
			if(intent.hasExtra(PRODUCT_MODEL))
				productModelNumber = b.getString(PRODUCT_MODEL);
			if(intent.hasExtra(PRODUCT_SKU))
				productSku = b.getString(PRODUCT_SKU);
			if(intent.hasExtra(ECORABTE_RESPONSE_DETAILS))
				ecoRebatesResponseDetails =  (EcoRebatesResponseDetails) b.getSerializable(ECORABTE_RESPONSE_DETAILS);
			if(intent.hasExtra(LocalRebates.POSITION))
				position = b.getInt(LocalRebates.POSITION, 0);
			if(productSku==null)
				productSku="";
		}
		
		rebateProductNameTv.setText((UTF.replaceNonUTFCharacters(productName)));
		rebateProductPriceTv.setText("List: $" + productSalePrice);
		rebateProductModelTv.setText("Model: " + productModelNumber + " | SKU: " + productSku);
		
		rebateZipEt.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {				
				rebateZipEt.setCursorVisible(true);
			}
		});
		
		rebateProductEmailTv2.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				sendEmail();
			}
		});
		
		terms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LocalRebates.this, TermsAndConditions.class);
				startActivity(i);
			}
		});
		
		rebateGoBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {		
				
				rebateZipEt.setCursorVisible(false);
				rebateNotAvailableMsgTv.setVisibility(View.GONE);
				
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(rebateGoBtn.getWindowToken(), 0);
				
				pZipCode = rebateZipEt.getText().toString().trim();
				
				if(pZipCode.length()!=5) {
					Toast.makeText(LocalRebates.this, "Please enter a valid zip code", Toast.LENGTH_SHORT).show();
					return;
				}							
				new EcoRebateTask(LocalRebates.this);
			}
		});
		
		pZipCode = String.valueOf(ecoRebatesResponseDetails.getArea().getmZipCode());
		showListView();
		rebateZipEt.setText(pZipCode);
	}
	
	 public boolean onKeyUp(int keyCode, KeyEvent event) {		
		if(keyCode==KeyEvent.KEYCODE_ENTER) {
			pZipCode = rebateZipEt.getText().toString().trim();
			if(pZipCode.length()!=5) {
				Toast.makeText(LocalRebates.this, "Please enter a valid zip code", Toast.LENGTH_SHORT).show();
				return false;
			}
			InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(rebateZipEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			
			new EcoRebateTask(LocalRebates.this);
			
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	/*
	 	Email Claim link would open up the native mail compose which will display the following information
	*  Subject line of the mail should be labeled as "Rebate Claim Forms from BestBuy.com"
	*  "The following rebates apply to this product:" labeled with the product URL which will navigate the user to the specific PDP
	*  Rebate "$value" with Program Name
	*  URL to navigate to rebate for
	 */
	private String getEmailBody() {
		StringBuilder mailBodyBuilder = new StringBuilder();
		try {
			ProductRebateDetails productRebateDetails = ecoRebatesResponseDetails.getProductRebateDetails().get(0);
			
			mailBodyBuilder.append("The following rebates apply to this product:" + "\n" 
					+ productRebateDetails.getProduct().getDetailsUrl());		
			
			for(RebateProgram rProgram : productRebateDetails.getRebatePrograms()) {
				mailBodyBuilder.append("\nRebate " + rProgram.getAmountLabel() + " with " + rProgram.getName() + "\n" + rProgram.getHomeURL());				
			}
						
			return mailBodyBuilder.toString();
		} catch(Exception e) {
			return "Data not available";
		}
	}
	
	private void sendEmail(){		
		try {						
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "Rebate Claim Forms from BestBuy.com");
			emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, getEmailBody());
			startActivity(Intent.createChooser(emailIntent, "E-Mail sending ..."));
		} catch(ActivityNotFoundException e) {
			Toast.makeText(LocalRebates.this, "No application can perform this action!", Toast.LENGTH_LONG).show();
		}
	}
	
	private void setRebateSearchResultText(String rebateCount) {
		if(rebateCount==null) {
			rebateCount = "0";
		}
		if(pZipCode==null || pZipCode.equals("")) {
			if(ecoRebatesResponseDetails!=null)
				pZipCode = "" + ecoRebatesResponseDetails.getArea().getmZipCode();
			else 
				pZipCode="00000";
		}
		rebateZipLL.setVisibility(View.GONE);
		rebateSearchResultTv.setVisibility(View.VISIBLE);
		if(rebateCount.equals("1")) {
			rebateSearchResultTv.setText(rebateCount+ " Rebate found near " + "\"" + pZipCode + "\"");
		} else {
			rebateSearchResultTv.setText(rebateCount+ " Rebates found near " + "\"" + pZipCode + "\"");
		}
	}
	
	private void showListView() {
		rebateFooterLL.setVisibility(View.GONE);
		
		if(getListView()!=null) {
			getListView().setVisibility(View.GONE);
		}
		
		if(pZipCode==null) {
			rebateSearchResultTv.setVisibility(View.GONE);
			rebateNotAvailableMsgTv.setVisibility(View.VISIBLE);
			rebateNotAvailableMsgTv.setText("Zip code not found for current location");
			localrebateEmailLL.setVisibility(View.GONE);
			return;
		}
		
		if(ecoRebatesResponseDetails==null) {							
			rebateSearchResultTv.setVisibility(View.GONE);
			rebateNotAvailableMsgTv.setVisibility(View.VISIBLE);
			rebateNotAvailableMsgTv.setText("Unable to reach network please try again later");
			localrebateEmailLL.setVisibility(View.GONE);
			return;
		}

		// Handle error message
		if(ecoRebatesResponseDetails.getError()!=null) {			
			rebateSearchResultTv.setVisibility(View.GONE);
			rebateNotAvailableMsgTv.setVisibility(View.VISIBLE);
			rebateNotAvailableMsgTv.setText(ecoRebatesResponseDetails.getError().getMessage());
			localrebateEmailLL.setVisibility(View.GONE);
			return;
		}
		
		try {
			productRebateDetailsList = ecoRebatesResponseDetails.getProductRebateDetails();  
			productRebateDetails = productRebateDetailsList.get(position); 		
			localRebates = productRebateDetails.getRebatePrograms();
		} catch(Exception e) {
			localRebates = null;
		}
		
		RebateListAdapter adapter = new RebateListAdapter(LocalRebates.this, localRebates);
		setListAdapter(adapter);
		
		if(localRebates!=null && localRebates.size()>0) {
			// Size of local rebate list is the rebate count
			String rebateCount = "" + localRebates.size();
						
			getListView().setVisibility(View.VISIBLE);			
			setRebateSearchResultText(rebateCount);
			rebateNotAvailableMsgTv.setVisibility(View.GONE);
			localrebateEmailLL.setVisibility(View.VISIBLE);
		} else {
			getListView().setVisibility(View.GONE);				
			rebateSearchResultTv.setVisibility(View.GONE);
			
			// if size of list is zero, show msg of no rebates found
			rebateNotAvailableMsgTv.setVisibility(View.VISIBLE);
			String label= getString(R.string.eco_rebates_no_rebates_msg);
            String msg = label + " " +pZipCode.toString() + ".";
			rebateNotAvailableMsgTv.setText(msg);
			localrebateEmailLL.setVisibility(View.GONE);
		}
		
		rebateFooterLL.setVisibility(View.VISIBLE);
	}
	
	private EcoRebatesResponseDetails ecoRebatesResponseDetails; 
	private ProductRebateDetails productRebateDetails; 
	private ArrayList<ProductRebateDetails> productRebateDetailsList;
	private ArrayList<RebateProgram> localRebates = new ArrayList<RebateProgram>();
	
	private String pZipCode = null;
	private class EcoRebateTask extends BBYAsyncTask {

		public EcoRebateTask(Activity activity) {
			super(activity, "Loading please wait...");
		}
		
		@Override
		public void doTask() throws Exception {
			String url = AppConfig.getBestbuyRebateURL() + "/api/search/bestbuy/productRebateDetails.JSON" + "?" + "skus=" + productSku + "&zip=" + pZipCode;

			String JSONResponse = EcoRequest.getRequest(url);
			LocalRebateDetailsParser parser = new LocalRebateDetailsParser();
			ecoRebatesResponseDetails = parser.parse(JSONResponse);
		}

		@Override
		public void doFinish() {
			showListView();  
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new EcoRebateTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				showListView();  
			}
		}
	}
}
