package com.bestbuy.android.hem.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.TermsAndConditions;
import com.bestbuy.android.hem.activity.helper.ApplianceListAdapter;
import com.bestbuy.android.hem.library.dataobject.BrandFinder;
import com.bestbuy.android.hem.library.dataobject.Brands;
import com.bestbuy.android.hem.library.dataobject.EcoRebatesResponseDetails;
import com.bestbuy.android.hem.library.dataobject.ProductRebateDetails;
import com.bestbuy.android.hem.library.util.LocalRebateDetailsParser;
import com.bestbuy.android.hem.library.util.LocalRebateFinderParser;
import com.bestbuy.android.hem.library.util.StorePreferences;
import com.bestbuy.android.hem.webblock.EcoRequest;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;

public class LocalRebateFinder extends MenuActivity {
	
	private TextView appliance; 
	private TextView brand;
	private TextView terms;
	private RelativeLayout rebate_finder_RR1;
	private RelativeLayout rebate_finder_RR2;
	private EditText zipcode;
	private Button  findrebate_active;
	private boolean zipCodeFlag = false;
	private boolean applianceFlag = false;
	private boolean brandFlag = false;
	private String message="",resultMessage="";
	private TextView errorMessage;
	private ArrayList<String> selectedItems=new ArrayList<String>();
	private ArrayList<BrandFinder> brandList;
	private BrandFinder selectedBrand = null;
	private StorePreferences storePreferences = null;
	private EcoRebatesResponseDetails ecoRebatesResponseDetails; 
	private ArrayList<ProductRebateDetails> productRebateDetailsList;
	private String productTypes;
	private String brandIDs;
	private boolean brandNavigation;
	private InputMethodManager inputMethodManager;
	public static final int APPLIANCE_REQ_CODE = 1000;
	public static final int BRAND_REQ_CODE = 2000;
	
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_rebate_finder);
		inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		downloadApplianceBrand();
		rebate_finder_RR1= (RelativeLayout)findViewById(R.id.rebate_finder_RR1);
		rebate_finder_RR2= (RelativeLayout)findViewById(R.id.rebate_finder_RR2);
		appliance = (TextView)findViewById(R.id.rebate_finder_select_appliance);
		brand = (TextView)findViewById(R.id.rebate_finder_select_brand);
		zipcode = (EditText)findViewById(R.id.rebate_finder_select_zipcode);
		findrebate_active =(Button)findViewById(R.id.findrebates_btn);
		terms = (TextView) findViewById(R.id.terms);
		errorMessage=(TextView)findViewById(R.id.errorMessage);
		storePreferences = StorePreferences.getInstance();

		zipcode.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {				
				zipcode.setCursorVisible(true);
			}
		});

		zipcode.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					inputMethodManager.hideSoftInputFromWindow(zipcode.getWindowToken(), 0);
					if(zipCodeFlag&&applianceFlag&&brandFlag) {
						new RebateProductsTask(LocalRebateFinder.this).execute();
						return true;
					}
				}
				return false;
			}
		});

		zipcode.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			public void afterTextChanged(Editable s) {
				if(s.length() == 5) {
					zipCodeFlag = true;
					enableRebateFinderBtn();

					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(zipcode.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 

				} else {
					zipCodeFlag = false;
					enableRebateFinderBtn();
				}
			}
		});


		brand.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			public void afterTextChanged(Editable s) {
				brandFlag = true;
				enableRebateFinderBtn();
			}
		});

		appliance.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			public void afterTextChanged(Editable s) {
				applianceFlag = true;
				enableRebateFinderBtn();
			}
		});


		rebate_finder_RR1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				selectedItems.clear();
				Intent i = new Intent(LocalRebateFinder.this, LocalRebateFinderAppliance.class);
				i.putExtra(LocalRebates.PRODUCT_NAME, brandList);
				startActivityForResult(i, APPLIANCE_REQ_CODE);
			}
		});

		rebate_finder_RR2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LocalRebateFinder.this, LocalRebateFinderBrand.class);
				if(selectedBrand != null){
					intent.putExtra(LocalRebates.SELECTED_BRAND, selectedBrand);
					intent.putExtra(LocalRebates.SELECTED_BRAND_POSITIONS, selectedItems);					
					startActivityForResult(intent, BRAND_REQ_CODE);
				}else{
					showBrandDialog();
				}
			}
		});

		findrebate_active.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (zipCodeFlag && applianceFlag && brandFlag) {
					new RebateProductsTask(LocalRebateFinder.this).execute();
				}
			}
		});

		terms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LocalRebateFinder.this, TermsAndConditions.class);
				startActivity(i);
			}
		});
	}

	protected void onStop() {
		super.onStop();
		StorePreferences sp = StorePreferences.getInstance();
		sp.clearPersistence();
	}

	private void enableRebateFinderBtn() {
		if(zipCodeFlag&&applianceFlag&&brandFlag) {
			findrebate_active.setEnabled(true);
			findrebate_active.setBackgroundResource(R.drawable.findrebates_btn_active);
		} else {
			findrebate_active.setEnabled(false);
			findrebate_active.setBackgroundResource(R.drawable.findrebates_btn);
		}
	}

	@SuppressWarnings("unchecked")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == APPLIANCE_REQ_CODE && resultCode == RESULT_OK && data.hasExtra(LocalRebates.APPLIANCE_TEXT) ){
			BrandFinder selectedAppliance = (BrandFinder) data.getSerializableExtra(LocalRebates.APPLIANCE_TEXT);
			ApplianceListAdapter appl = new ApplianceListAdapter(getBaseContext(), brandList);
			appliance.setText(appl.textFormatting(selectedAppliance.getProductType()));
			selectedBrand = selectedAppliance;
			productTypes = selectedAppliance.getProductType();
			if(brandNavigation){
				storePreferences.clearPersistence();
				brand.setText("Select a brand");
				brandNavigation = false;
				brandFlag=false;
				enableRebateFinderBtn();
			}
		}else if(requestCode == BRAND_REQ_CODE && resultCode == RESULT_OK ) {
			ArrayList<Brands> selectedBrands = (ArrayList<Brands>) data.getSerializableExtra(LocalRebates.SELECTED_ITEMS);
			selectedItems = (ArrayList<String>) data.getSerializableExtra(LocalRebates.SELECTED_ITEMS_POSITION);
			if (selectedBrands.size() == 0) {
				brand.setText("Select a brand");
				brandFlag=false;
				enableRebateFinderBtn();
			} else if (selectedItems.size() == 0) {
				brand.setText("Select a brand");
				brandFlag=false;
				selectedBrands.clear();
				enableRebateFinderBtn();
			} else {
				brandNavigation = true;
				if (selectedBrands.size() == 1) {
					brand.setText(selectedBrands.get(0).getName());
					brandIDs = selectedBrands.get(0).getId();
				} else if (selectedBrands.size() > 1) {
					brandIDs = selectedBrands.get(0).getId();
					for(int i=1; i<selectedBrands.size(); i++) {
						brandIDs = brandIDs + "," + selectedBrands.get(i).getId();
					}					
					if (selectedBrand.getBrands().size() == selectedBrands.size()) {
						brand.setText("All selected");
					} else {
						brand.setText(selectedBrands.size() + " " + "selected");
					}
				}				
				brandFlag=true;
				enableRebateFinderBtn();
			}

		}
	}

	private void downloadApplianceBrand() {
		new EcoRebateTask(LocalRebateFinder.this).execute();
	}

	private class EcoRebateTask extends BBYAsyncTask {
		String url = AppConfig.getBestbuyRebateURL()+ "/api/config/bestbuy/productTypes.json";
		
		public EcoRebateTask(Activity activity) {
			super(activity, "Loading please wait...");
		}
		
		@Override
		public void doTask() throws Exception {
			
			String JSONResponse = EcoRequest.getRequest(url);

			LocalRebateFinderParser obj = new LocalRebateFinderParser();
			brandList = obj.parse(JSONResponse);
		}

		@Override
		public void doFinish() {
			
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
			} 
		}
	}
	
	class RebateProductsTask extends BBYAsyncTask {

		public RebateProductsTask(Activity activity) {
			super(activity,"Loading please wait...");
			getDialog().setCancelable(false);
			message = "";
		}

		@Override
		public void doTask() throws Exception {
			String url = AppConfig.getBestbuyRebateURL()
					+ "/api/search/bestbuy/productRebateDetails.json"
					+ "?"
					+ "productTypes="
					+ productTypes
					+ "&brandIDs="
					+ brandIDs + "&zip=" + zipcode.getText().toString();

			String JSONResponse = EcoRequest.getRequest(url);
			LocalRebateDetailsParser parser = new LocalRebateDetailsParser();
			ecoRebatesResponseDetails = parser.parse(JSONResponse);

			if (ecoRebatesResponseDetails == null) {
				message = "Unable to reach network please try again later";
			}
			else{
				if (ecoRebatesResponseDetails.getError() != null) {
					message = ecoRebatesResponseDetails.getError().getMessage();
				}
				productRebateDetailsList = ecoRebatesResponseDetails.getProductRebateDetails();
				if (productRebateDetailsList.size() == 0) {
					String zip = zipcode.getText().toString();
					message = getString(R.string.eco_rebates_no_rebates_msg) + " " + zip + ".";
				}

				ApplianceListAdapter appl = new ApplianceListAdapter(getBaseContext(), brandList);

				if (productRebateDetailsList.size() < 2) {
					resultMessage = "Found " + productRebateDetailsList.size()
							+ " " + appl.textFormatting((productTypes))
							+ " in " + "\n"
							+ ecoRebatesResponseDetails.getArea().getMcity()
							+ ", "
							+ ecoRebatesResponseDetails.getArea().getmState();
				} else {
					String tempProductTypes = appl.textFormatting(productTypes);
					tempProductTypes = tempProductTypes + "S";
					resultMessage = "Found " + productRebateDetailsList.size()
							+ " " + tempProductTypes + " in " + "\n"
							+ ecoRebatesResponseDetails.getArea().getMcity()
							+ ", "
							+ ecoRebatesResponseDetails.getArea().getmState();
				}
			}
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
	
					public void onReconnect() {	
						new RebateProductsTask(activity).execute();
					}
				}, new OnCancel() {
	
					public void onCancel() {
						finish();
					}
				});
			} else {
				if (message != null && !message.equals("")) {
					((LinearLayout) errorMessage.getParent()).setVisibility(View.VISIBLE);
					errorMessage.setText(message);
				} 
			}
		}
		
		@Override
		public void doFinish() {
			if (message != null && !message.equals("")) {
				((LinearLayout) errorMessage.getParent()).setVisibility(View.VISIBLE);
				errorMessage.setText(message);
			} else if (resultMessage != null && !resultMessage.equals("")) {
				((LinearLayout) errorMessage.getParent()).setVisibility(View.GONE);
				errorMessage.setText("");
				Intent b = new Intent(LocalRebateFinder.this, LocalRebateFinderproduct.class);
				b.putExtra(LocalRebates.DISPLAY_MESSAGE, resultMessage);
				b.putExtra(LocalRebates.ECORABTE_RESPONSE_DETAILS, ecoRebatesResponseDetails);
				b.putExtra(LocalRebates.ECORABTE_PRODUCT_DETAILS, productRebateDetailsList);
				startActivity(b);
			}
		}
	}

	private void showBrandDialog(){
		if(this==null || this.isFinishing()) { 
			return;
		}
		AlertDialog al = new AlertDialog.Builder(LocalRebateFinder.this).create();
		al.setTitle("");
		al.setMessage("Please select an appliance first.");
		al.setButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		al.show();
	}

}
