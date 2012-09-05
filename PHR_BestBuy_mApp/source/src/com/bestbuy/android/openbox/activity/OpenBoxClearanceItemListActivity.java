package com.bestbuy.android.openbox.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.openbox.activity.helper.ProductItemsAdapter;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxScreenObject;
import com.bestbuy.android.openbox.logic.OpenBoxClearanceLogic;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;

public class OpenBoxClearanceItemListActivity extends MenuActivity implements OnItemClickListener {

	private ArrayList<String> lisOfSKU;
	private ArrayList<OpenBoxItem> productCollection;
	private ListView itemsListView;
	private TextView headerText;
	private LinearLayout image;
	private String productType;
	private String itemCategoryName, itemCategoryId;
	private ProductItemsAdapter productListAdapter;
	private OpenBoxScreenObject screenObject;
	private RelativeLayout statusLayout;
	private HashMap<String, OpenBoxItemPrice> priceMap = null;
	private String storeName;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openbox_list);

		initialize();
	}
	
	@SuppressWarnings("unchecked")
	private void initialize() {
		itemsListView = (ListView) findViewById(R.id.openbox_listview);
		headerText = (TextView) findViewById(R.id.openbox_header);
		statusLayout = (RelativeLayout) findViewById(R.id.statusLayout);
		image = (LinearLayout) findViewById(R.id.bg_image);
		image.setVisibility(View.GONE);

		Intent intent = getIntent();

		if (intent != null) {
			lisOfSKU = (ArrayList<String>) intent.getSerializableExtra(StoreUtils.LIST_OF_SKU);
			itemCategoryName = this.getIntent().getStringExtra(StoreUtils.CATEGORY_NAME);
			itemCategoryId = this.getIntent().getStringExtra(StoreUtils.CATEGORY_ID);
			productType = this.getIntent().getStringExtra(StoreUtils.PRODUCT_TYPE);
			priceMap = (HashMap<String, OpenBoxItemPrice>) intent.getSerializableExtra(StoreUtils.PRICE_MAP);
		}

		if (productType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)) {
			itemCategoryName = StoreUtils.OPENBOX_ITEMS;
		} else if (productType.equalsIgnoreCase(StoreUtils.CLEARANCE_ITEMS)) {
			itemCategoryName = StoreUtils.CLEARANCE_ITEMS;
		}
		
		if (this.getIntent().hasExtra(StoreUtils.STORE_NAME))
			storeName = this.getIntent().getStringExtra(StoreUtils.STORE_NAME);

		headerText.setText(itemCategoryName);
		new GetScreenObjectTask(this).execute();
		itemsListView.setOnItemClickListener(this);
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
	
	class GetScreenObjectTask extends BBYAsyncTask {

		public GetScreenObjectTask(Activity activity) {
			super(activity, "Loading content...");
		}

		@Override
		public void doTask() throws Exception {
			screenObject = OpenBoxClearanceLogic.getAllItems(lisOfSKU, itemCategoryId, priceMap);			
		}

		@Override
		public void doFinish() {
			if (screenObject != null) {
				productCollection = screenObject.getOpenBoxItems();

				if (productCollection != null && productCollection.size() > 0) {
					productListAdapter = new ProductItemsAdapter(activity, productCollection, productType);
					itemsListView.setAdapter(productListAdapter);
				} else {
					itemsListView.setVisibility(View.GONE);
					TextView statusText = (TextView) findViewById(R.id.status_text);
					if(productType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)){
						statusText.setText("There are currently no Open Box Items listed.");
						statusLayout.setVisibility(View.VISIBLE);
					}else if(productType.equalsIgnoreCase(StoreUtils.CLEARANCE_ITEMS)){
						statusText.setText("There are currently no Clearance Items listed.");
						statusLayout.setVisibility(View.VISIBLE);
					}
				}
			}
		}

		@Override
		public void doReconnect() {
			new GetScreenObjectTask(activity).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
	}
}
