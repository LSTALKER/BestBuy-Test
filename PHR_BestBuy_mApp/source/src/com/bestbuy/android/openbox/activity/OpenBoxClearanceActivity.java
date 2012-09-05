package com.bestbuy.android.openbox.activity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.openbox.activity.helper.ProductItemsAdapter;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxScreenObject;
import com.bestbuy.android.openbox.library.webobject.SKUResponseObject;
import com.bestbuy.android.openbox.logic.OpenBoxClearanceLogic;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.BBYProgressDialog;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;

public class OpenBoxClearanceActivity extends MenuActivity implements OnItemClickListener {

	private boolean isOpenBoxItemsLoaded;
	private boolean isClearanceItemsLoaded;
	
	private final int OPENBOX_TAB = 0;
	private final int CLEARANCE_TAB = 1;
	private int currentTabSelection = -1;
	private int lastCategorySelection = -1;
	
	private String storeId;
	private String storeName;
	private String productType;
	private String selectedCategoryKey = StoreUtils.SELECT_CATEGORY;
	private CharSequence[] categoryList;
	
	private Button categoryButton;
	private Button openBoxItemButton;
	private Button clearenceItemButton;
	private ListView itemsListView;
	
	private OpenBoxScreenObject screenObject;
	private SKUResponseObject skuResponseObject;
	private ProductItemsAdapter productItemsAdapter;
	
	private ArrayList<String> openBoxSkuList;
	private ArrayList<String> clearanceSkuList;
	private ArrayList<OpenBoxItem> openBoxProductsList;
	private ArrayList<OpenBoxItem> clearanceProductsList;
	
	private HashMap<String, OpenBoxItemPrice> openBoxPriceMap;
	private HashMap<String, OpenBoxItemPrice> clearancePriceMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.openbox_interstitial);		
		
		initilizePage();
		
		initilizeCategoryItems();
		
		initilizeTabSelection();
		
		initilizeListeners();
		
		initializeSkuList();
	}

	private void initilizePage() {
		categoryButton = (Button) findViewById(R.id.openbox_category_btn);
		openBoxItemButton = (Button)findViewById(R.id.openbox_button);
		clearenceItemButton = (Button)findViewById(R.id.clearance_button);
		
		itemsListView = (ListView) findViewById(R.id.openbox_listview);
		hideErrorMessage();
		
		if (this.getIntent().hasExtra(StoreUtils.STORE_ID))
			storeId = this.getIntent().getStringExtra(StoreUtils.STORE_ID);
		
		if (this.getIntent().hasExtra(StoreUtils.STORE_NAME))
			storeName = this.getIntent().getStringExtra(StoreUtils.STORE_NAME);
		
		if (this.getIntent().hasExtra(StoreUtils.FROM_SOURCE))
			productType = this.getIntent().getStringExtra(StoreUtils.FROM_SOURCE);
	}
	
	private void initilizeCategoryItems() {
		Iterator<String> iterator = StoreUtils.CATEGORYMAP.keySet().iterator();
		
		categoryList = new CharSequence[StoreUtils.CATEGORYMAP.size() + 1];
		categoryList[0] = StoreUtils.SELECT_CATEGORY;
		int i = 1;
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			categoryList[i++] = string;
		}
	}
	
	private void initilizeTabSelection() {
		if(productType == null)
			productType = StoreUtils.OPENBOX_ITEMS;
		
		if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
			openBoxItemButton.setBackgroundResource(R.drawable.left_white_box);
			clearenceItemButton.setBackgroundResource(R.drawable.right_grey_box);
			
			openBoxProductsList = new ArrayList<OpenBoxItem>();
			productItemsAdapter = new ProductItemsAdapter(this, openBoxProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
			
			currentTabSelection = OPENBOX_TAB;
			
		} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
			openBoxItemButton.setBackgroundResource(R.drawable.left_grey_box);
			clearenceItemButton.setBackgroundResource(R.drawable.right_white_box);
			
			clearanceProductsList = new ArrayList<OpenBoxItem>();
			productItemsAdapter = new ProductItemsAdapter(this, clearanceProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
			
			currentTabSelection = CLEARANCE_TAB;
		}
			
		categoryButton.setText(StoreUtils.SELECT_CATEGORY);
		
		((TextView) findViewById(R.id.openbox_header)).setText(productType + " At: " + storeName);
	}
	
	private void initilizeListeners() {
		itemsListView.setOnItemClickListener(this);
		
		categoryButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				openCategoryItemDialog();
			}
		});
		
		openBoxItemButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				swapTabSelection(OPENBOX_TAB);
			}
		});
		
		clearenceItemButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				swapTabSelection(CLEARANCE_TAB);
			}
		});
	}
	
	private void initializeSkuList() {
		new OnPageLoadTask(this).execute();
	}
	
	private void swapTabSelection(int tabSelected) {
		if(tabSelected == currentTabSelection)
			return;
		
		currentTabSelection = tabSelected;
		
		hideErrorMessage();
		
		if(currentTabSelection == OPENBOX_TAB) {
			productType = StoreUtils.OPENBOX_ITEMS;
			openBoxItemButton.setBackgroundResource(R.drawable.left_white_box);
			clearenceItemButton.setBackgroundResource(R.drawable.right_grey_box);
			
			if(openBoxProductsList == null) {
				openBoxProductsList = new ArrayList<OpenBoxItem>();
				getCategorizedProducts();
			} else if(openBoxProductsList.isEmpty() && !isOpenBoxItemsLoaded) 
				getCategorizedProducts();
			else {
				if(selectedCategoryKey.equals(StoreUtils.SELECT_CATEGORY)) {
					
					if(openBoxProductsList.isEmpty())
						showErrorMessage();
				}
			}
			productItemsAdapter = new ProductItemsAdapter(this, openBoxProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
			
		} else if(currentTabSelection == CLEARANCE_TAB) {
			productType = StoreUtils.CLEARANCE_ITEMS;
			openBoxItemButton.setBackgroundResource(R.drawable.left_grey_box);
			clearenceItemButton.setBackgroundResource(R.drawable.right_white_box);
			
			if(clearanceProductsList == null) {
				clearanceProductsList = new ArrayList<OpenBoxItem>();
				getCategorizedProducts();
			} else if(clearanceProductsList.isEmpty() && !isClearanceItemsLoaded) 
				getCategorizedProducts();
			else {
				if(selectedCategoryKey.equals(StoreUtils.SELECT_CATEGORY)) {
					if(clearanceProductsList.isEmpty())
						showErrorMessage();
				} 
			}
			productItemsAdapter = new ProductItemsAdapter(this, clearanceProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
		}
	}
	
	private void openCategoryItemDialog() {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		alertDialogBuilder.setItems(categoryList,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						getCategorizedProducts(which);
					}
				});
		
		alertDialogBuilder.create().show();
	}
	
	private void getCategorizedProducts(int which) {
		if(lastCategorySelection == which)
			return;
			
		categoryButton.setText(categoryList[which]);
		selectedCategoryKey = categoryList[which].toString();
		
		lastCategorySelection = which;
		
		initializeVars();
		
		getCategorizedProducts();
	}

	private void getCategorizedProducts() {
		if(selectedCategoryKey.equals(StoreUtils.SELECT_CATEGORY)) {
			initializeVars();
			return;
		}
		
		showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
		
		if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
			if(openBoxSkuList == null)
				new SKUListTask(this).execute();
			else 
				new GetProductTask(this).execute();
			
		} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
			if(clearanceSkuList == null)
				new SKUListTask(this).execute();
			else 
				new GetProductTask(this).execute();
		}
	}
	
	private void initializeVars() {
		itemsListView = (ListView) findViewById(R.id.openbox_listview);
		hideErrorMessage();
		
		isOpenBoxItemsLoaded = false;
		isClearanceItemsLoaded = false;
		openBoxProductsList = new ArrayList<OpenBoxItem>();
		clearanceProductsList = new ArrayList<OpenBoxItem>();
		
		if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
			productItemsAdapter = new ProductItemsAdapter(this, openBoxProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
		} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
			productItemsAdapter = new ProductItemsAdapter(this, clearanceProductsList, productType);
			itemsListView.setAdapter(productItemsAdapter);
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		

		String searchType = "1";
		String sellingPrice = "0.0";
		String regularPrice = "0.0";
		
		if (productType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS))
			searchType = "0";
		
		OpenBoxItem item = (OpenBoxItem) parent.getItemAtPosition(position);
		if(item.getSellingPrice() !=null && !item.getSellingPrice().equals("")){
			sellingPrice = item.getSellingPrice();
			regularPrice = item.getRegularPrice();
		}
		
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(AppConfig.getMdotURL()).append(getString(R.string.PDP_URL)).append("?skuId=");
		urlBuffer.append(item.getSku()).append("&pid=").append(item.getProductId()).append("&pageSrc=dnmPage&DNMHeader=true");
		urlBuffer.append("&searchType=").append(searchType).append("&storeName=Best Buy - ").append(storeName);
		urlBuffer.append("&lowPrice=").append(regularPrice).append("&price=").append(sellingPrice);
		String url = urlBuffer.toString();
		Intent i = new Intent(this, MDOTProductDetail.class);
		i.putExtra(MDOT_URL,url);
		startActivity(i);
	}	
	
	class OnPageLoadTask extends BBYAsyncTask {

		public OnPageLoadTask(Activity activity) {
			super(activity, "Loading content...");
		}

		@Override
		public void doTask() throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
			skuResponseObject = OpenBoxClearanceLogic.getListSKU(productType, storeId);
		}

		@Override
		public void doFinish() {
			if (skuResponseObject != null) {
				if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
					openBoxSkuList = skuResponseObject.getSkuList();
					openBoxPriceMap = skuResponseObject.getPriceMap();
					
					//System.out.println("openBoxSkuList : " + openBoxSkuList.size());
					//System.out.println("openBoxPriceMap : " + openBoxPriceMap.size());
					
				} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
					clearanceSkuList = skuResponseObject.getSkuList();
					clearancePriceMap = skuResponseObject.getPriceMap();
					
					//System.out.println("clearanceSkuList : " + clearanceSkuList.size());
					//System.out.println("clearancePriceMap : " + clearancePriceMap.size());
				}
			}
		}

		@Override
		public void doCancelReconnect() {
			finish();
		}
		
		@Override
		public void doReconnect() {
			new OnPageLoadTask(activity).execute();
		}
	}
	
	class SKUListTask extends BBYAsyncTask {

		public SKUListTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doTask() throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
			skuResponseObject = OpenBoxClearanceLogic.getListSKU(productType, storeId);
		}

		@Override
		public void doFinish() {
			if (skuResponseObject != null) {
				if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
					openBoxSkuList = skuResponseObject.getSkuList();
					openBoxPriceMap = skuResponseObject.getPriceMap();
					
					//System.out.println("openBoxSkuList : " + openBoxSkuList.size());
					//System.out.println("openBoxPriceMap : " + openBoxPriceMap.size());
					
				} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
					clearanceSkuList = skuResponseObject.getSkuList();
					clearancePriceMap = skuResponseObject.getPriceMap();
					
					//System.out.println("clearanceSkuList : " + clearanceSkuList.size());
					//System.out.println("clearancePriceMap : " + clearancePriceMap.size());
				}
				
				new GetProductTask(OpenBoxClearanceActivity.this).execute();
			}
		}

		@Override
		public void doError() {
			removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
					new SKUListTask(activity).execute();
				}
			}, new OnCancel() {
				
				public void onCancel() {
					cancelLoading();
				}
			});
		}
	}
	
	class GetProductTask extends BBYAsyncTask {

		public GetProductTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doTask() throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
			String categoryId = StoreUtils.CATEGORYMAP.get(selectedCategoryKey);
			if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
				screenObject = OpenBoxClearanceLogic.getAllItems(openBoxSkuList, categoryId, openBoxPriceMap);
			} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
				screenObject = OpenBoxClearanceLogic.getAllItems(clearanceSkuList, categoryId, clearancePriceMap);
			}
		}

		@Override
		public void doFinish() {
			removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
			
			if (screenObject != null) {
				if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
					openBoxProductsList = screenObject.getOpenBoxItems();
					if (openBoxProductsList != null) {
						isOpenBoxItemsLoaded = true;
						productItemsAdapter.setItems(openBoxProductsList);
						
						if(openBoxProductsList.isEmpty())
							showErrorMessage();
					} else
						showErrorMessage();
					
				} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
					clearanceProductsList = screenObject.getOpenBoxItems();
					if (clearanceProductsList != null) {
						isClearanceItemsLoaded = true;
						productItemsAdapter.setItems(clearanceProductsList);
						
						if(clearanceProductsList.isEmpty())
							showErrorMessage();
						
					} else
						showErrorMessage();
				}
			} else 
				showErrorMessage();
		}

		@Override
		public void doError() {
			removeDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					showDialog(StoreUtils.PROGRESS_EVENTS_DIALOG);
					new GetProductTask(OpenBoxClearanceActivity.this).execute();
				}
			}, new OnCancel() {
				
				public void onCancel() {
					cancelLoading();
				}
			});
		}
	}
	
	private void showErrorMessage() {
		itemsListView.setVisibility(View.GONE);
		TextView emptyView = (TextView)findViewById(R.id.result_empty);
		emptyView.setVisibility(View.VISIBLE);
		emptyView.setText("There are currently no " + productType + " listed.");
	}
	
	private void hideErrorMessage() {
		itemsListView.setVisibility(View.VISIBLE);
		TextView emptyView = (TextView)findViewById(R.id.result_empty);
		emptyView.setVisibility(View.VISIBLE);
	}
	
	private void cancelLoading() {
		if(productType.equals(StoreUtils.OPENBOX_ITEMS)) {
			if(clearanceProductsList != null) {
				if(clearanceProductsList.isEmpty()) {
					if(isClearanceItemsLoaded)
						swapTabSelection(CLEARANCE_TAB);
					else
						finish();
				} else
					swapTabSelection(CLEARANCE_TAB);
			} else
				finish();
			
		} else if(productType.equals(StoreUtils.CLEARANCE_ITEMS)) {
			if(openBoxProductsList != null) {
				if(openBoxProductsList.isEmpty()) {
					if(isOpenBoxItemsLoaded)
						swapTabSelection(OPENBOX_TAB);
					else
						finish();
				} else
					swapTabSelection(OPENBOX_TAB);
			} else
				finish();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (this == null || this.isFinishing()) {
			return dialog;
		}
		
		switch (id) {
			case StoreUtils.PROGRESS_EVENTS_DIALOG:
				return new BBYProgressDialog(this, "Loading content..."); 
		}
		
		return dialog;
	}
}