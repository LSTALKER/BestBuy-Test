package com.bestbuy.android.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.UberCategory;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.HtmlParser;
import com.bestbuy.android.util.ObjectStreamUtilities;


/**
 * Lets the user select product categories and sub-categories.
 * 
 * @author Lalit Kumar Sahoo
 * 
 */
public class BrowseCategory extends MenuListActivity implements OnItemClickListener {
	private List<UberCategory> uberList;
	private BrowseCategoryAdapter browseCategoryAdapter;
	private boolean isDialogEnabled = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_browse_tab_list);
		
		browseCategoryAdapter = new BrowseCategoryAdapter();
		setListAdapter(browseCategoryAdapter);
		getListView().setOnItemClickListener(this);
		
		uberList = new ObjectStreamUtilities<List<UberCategory>>().readObject(AppData.BBY_BROWSE_CATEGORY_FILE);
		
		if(uberList != null && !uberList.isEmpty()) {
			isDialogEnabled = false;
			showView();
		} else {
			isDialogEnabled = true;
		}
		
		new WebPageAsyncTask(this).execute();
	}
	
	public class WebPageAsyncTask extends BBYAsyncTask {

		public WebPageAsyncTask(Activity activity) {
			super(activity);
			enableLoadingDialog(isDialogEnabled);
		}

		@Override
		public void doTask() throws Exception {
			HtmlParser.getConnection(AppConfig.getMdotURL() + getResources().getString(R.string.UBER_CATEGORY_LIST) +"&channel=mApp-And");
		}

		@Override
		public void doFinish() {
			uberList = new ObjectStreamUtilities<List<UberCategory>>().readObject(AppData.BBY_BROWSE_CATEGORY_FILE);
			showView();
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						isDialogEnabled = true;
						new WebPageAsyncTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				showView();
			}
		}
	}
	
	private void showView() {
		if (uberList != null && !uberList.isEmpty()) {
			filterCategory();
			browseCategoryAdapter.notifyDataSetChanged();
			((TextView)findViewById(R.id.category_browse_sub_header)).setText("Select a Category");
		}
	}
	
	private void filterCategory() {
		int size = uberList.size();
		
		for(int i = 0; i < size; i++) {
			UberCategory category = (UberCategory) uberList.get(i);
			if(category.getCategoryValue().equals("#")) {
				uberList.remove(category);
				break;
			}
		}
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UberCategory category = (UberCategory) parent.getItemAtPosition(position);
		String url = category.getCategoryValue();

		Intent intent = new Intent(BrowseCategory.this, MDOTProductDetail.class);
		intent.putExtra(MDOT_URL, url);
		startActivity(intent);
	}
	
	public class BrowseCategoryAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public BrowseCategoryAdapter() {
			this.inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			if (uberList != null) {
				return uberList.size();
			}
			return 0;
		}

		public long getItemId(int position) {
			return position;
		}
		
		public UberCategory getItem(int position) {
			return uberList.get(position);
		}
		
		public class ViewHolder {
			private TextView options;
	    }
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View customView = convertView;
			final ViewHolder holder;
			
		    if(convertView == null){
		    	customView = inflater.inflate(R.layout.category_browse_row, null);
		    	holder = new ViewHolder();
		    	
		    	holder.options = (TextView) customView.findViewById(R.id.category_browse_category_name);
	            
	            customView.setTag(holder);
	            
	        } else
	        	holder = (ViewHolder)customView.getTag();
		    
		    UberCategory category = getItem(position);
			
		    holder.options.setText(category.getCategoryOption());

			return customView;
		}
	}
}
