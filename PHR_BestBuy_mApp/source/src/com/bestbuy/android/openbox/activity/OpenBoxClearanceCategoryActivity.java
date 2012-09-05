package com.bestbuy.android.openbox.activity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.openbox.activity.helper.CategoryListAdapter;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.webobject.SKUResponseObject;
import com.bestbuy.android.openbox.logic.OpenBoxClearanceLogic;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.BBYAsyncTask;

public class OpenBoxClearanceCategoryActivity extends MenuActivity implements OnItemClickListener {

	private ListView categoryList;
	private CategoryListAdapter listAdapter;
	private TextView headerTextView;
	private String productType, storeId;
	public ArrayList<String> listOfSKU = new ArrayList<String>();
	private Hashtable<String, String> categoryMap;
	private HashMap<String, OpenBoxItemPrice> priceMap;
	private String storeName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openbox_list);

		initialize();
	}

	private void initialize() {
		categoryMap = StoreUtils.CATEGORYMAP;

		headerTextView = (TextView) findViewById(R.id.openbox_header);
		categoryList = (ListView) findViewById(R.id.openbox_listview);

		if (this.getIntent().hasExtra(StoreUtils.FROM_SOURCE))
			productType = this.getIntent().getStringExtra(StoreUtils.FROM_SOURCE);

		if (this.getIntent().hasExtra(StoreUtils.STORE_ID))
			storeId = this.getIntent().getStringExtra(StoreUtils.STORE_ID);
		
		if (this.getIntent().hasExtra(StoreUtils.STORE_NAME))
			storeName = this.getIntent().getStringExtra(StoreUtils.STORE_NAME);

		headerTextView.setText(productType);
		new GetAllSKUTask(this).execute();
		categoryList.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LinearLayout parentLayout = (LinearLayout) view;
		TextView categoryView = (TextView) parentLayout.getChildAt(0);
		String key = categoryView.getText().toString();
		String categoryId = categoryMap.get(key);
		Intent intent = new Intent(view.getContext(), OpenBoxClearanceItemListActivity.class);
		intent.putExtra(StoreUtils.LIST_OF_SKU, listOfSKU);
		intent.putExtra(StoreUtils.PRODUCT_TYPE, productType);
		intent.putExtra(StoreUtils.PRICE_MAP, priceMap);
		intent.putExtra(StoreUtils.CATEGORY_NAME, key);
		intent.putExtra(StoreUtils.CATEGORY_ID, categoryId);
		intent.putExtra(StoreUtils.STORE_NAME, storeName);
		startActivity(intent);
	}
	
	class GetAllSKUTask extends BBYAsyncTask {
		public GetAllSKUTask(Activity activity) {
			super(activity, "Loading content...");
		}

		@Override
		public void doTask() throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
			SKUResponseObject skuResObj = OpenBoxClearanceLogic.getListSKU(productType, storeId);
			listOfSKU = skuResObj.getSkuList();
			priceMap = skuResObj.getPriceMap();
		}

		@Override
		public void doFinish() {
			ArrayList<String> categories = new ArrayList<String>();
			Enumeration<String> keys = categoryMap.keys();
			while (keys.hasMoreElements()) {
				categories.add(keys.nextElement().toString());
			}
			listAdapter = new CategoryListAdapter(activity, categories);
			categoryList.setAdapter(listAdapter);
		}

		@Override
		public void doReconnect() {
			new GetAllSKUTask(activity).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
	}
}
