package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.DealCategory;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EventsLogging;

/**
 * Displays a list of product categories that have deals, and the number of
 * deals in each category. Displayed under deals tab in the BestBuy activity.
 * 
 * @author Recursive Awesome
 * 
 */
public class DealRootCategoryList extends MenuListActivity {

	List<DealCategory> dealCategories = new ArrayList<DealCategory>();

	private String TAG = this.getClass().getName();
	private long timestamp = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_WEEKLY_AD_EVENT);
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
	}

	@Override
	public void onResume() {
		super.onResume();
		BBYLog.e(TAG, "onResume called: ");
		double elapsedDays = getElapsedDays();
		BBYLog.e(TAG, "elapsedDays" + elapsedDays);
		if (elapsedDays >= 1) {
			new DealsTask(this).execute();
		}
	}

	public double getElapsedDays() {
		Long currentTimestamp = new Date().getTime();
		final int msPerDay = 24 * 60 * 60 * 1000;
		Double elapsedDays = Math.floor((currentTimestamp - timestamp) / ((double)msPerDay));
		timestamp = currentTimestamp;
		return elapsedDays;
	}

	OnItemClickListener dealCategoryClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			appData.setSelectedDealCategory(dealCategories.get(position));
			Intent i = new Intent(v.getContext(), DealCategoryList.class);
			//if(dealCategories.get(position).getProductCount()!=0)
			startActivity(i);
		}
	};

	@Override
	public Object onRetainNonConfigurationInstance() {
		return dealCategories;
	}

	public void onRestart() {
		super.onRestart();
	}

	private class DealsTask extends BBYAsyncTask {
		public DealsTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			ListView list = (ListView) findViewById(android.R.id.list);
			DealsAdapter dealsAdapter = new DealsAdapter();
			list.setAdapter(dealsAdapter);
			list.setOnItemClickListener(dealCategoryClickedHandler);

			LinearLayout pb = (LinearLayout) DealRootCategoryList.this.findViewById(R.id.main_deals_progress);
			pb.setVisibility(View.GONE);
			LinearLayout ll = (LinearLayout) DealRootCategoryList.this.findViewById(R.id.main_deals_list);
			ll.setVisibility(View.VISIBLE);
		}

		@Override
		public void doTask() throws Exception {
			BBYLog.i(TAG, "Running ASYNC DealsTask for Root Category List");
			SearchRequest searchRequest = new SearchRequest();
			dealCategories = searchRequest.getDealsCategories();
			Collections.sort(dealCategories);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setContentView(R.layout.deals_tab);
		}
		
		@Override
		public void doError() {
			removeDialog(LOADING_DIALOG);
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new DealsTask(activity).execute();
				}
			}, new OnCancel() {
				
				public void onCancel() {
						finish();
				}
			});
		}
	}

	class DealsAdapter extends ArrayAdapter<DealCategory> {
		DealsAdapter() {
			super(DealRootCategoryList.this, R.layout.category_browse_row, dealCategories);
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.deals_list_row, parent, false);
				row.setTag(R.id.deals_list_name, row.findViewById(R.id.deals_list_name));
				row.setTag(R.id.deals_list_count, row.findViewById(R.id.deals_list_count));
			}

			TextView categoryName = (TextView) row.getTag(R.id.deals_list_name);
			categoryName.setText(dealCategories.get(position).getName());

			DealCategory dealCategory = dealCategories.get(position);
			TextView productCount = (TextView) row.getTag(R.id.deals_list_count);
			productCount.setText(String.valueOf(dealCategory.getProductCount()));
			return row;
		}
	}

}
