package com.bestbuy.android.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.activity.AppoliciousCategoriesList;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Category;
import com.bestbuy.android.data.CategoryUtilities;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.ui.CategoryBrowseWrapper;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EventsLogging;

/**
 * Lets the user select product categories and sub-categories.
 * 
 * @author Recursive Awesome
 * 
 */
public class CategoryBrowse extends MenuListActivity {

	private final String TAG = this.getClass().getName();
	public static final Void Void = null;

	private ListView myList;
	private Context _context;

	private List<Category> currentCategoryList;
	private CategoryAdapter reviewsAdapter;
	private String categoryName;
	private String categoryId;
	private static String MOBILE_APPS = "App Center";
	private boolean isCodeScan = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_context = this;
		subCategory = false;

		setContentView(R.layout.category_browse_tab_list);
		isCodeScan = this.getIntent().getBooleanExtra(AppData.IS_CODE_SCAN, false);
		if (savedInstanceState != null) {
			categoryId = savedInstanceState.getString("categoryId");
			categoryName = savedInstanceState.getString("categoryName");

			executeLoadCategoryTask(categoryId, appData);
		} else if (this.getIntent().hasExtra(AppData.CATEGORY_ID)) {
			// based on category passed in, get the current category list.
			categoryId = this.getIntent().getStringExtra(AppData.CATEGORY_ID);
			categoryName = this.getIntent().getStringExtra(AppData.CATEGORY_NAME);
			subCategory = true;
			executeLoadCategoryTask(categoryId, appData);
		} else {
			categoryId = AppData._301_ROOT_CATEGORY;
			LoadCategoriesTask lct = new LoadCategoriesTask(this);
			lct.execute();
		}
		
		TextView subHeader = (TextView) findViewById(R.id.category_browse_sub_header);
		if (this.getIntent().hasExtra(AppData.CATEGORY_NAME)) {
			String categoryName = this.getIntent().getExtras().getString(AppData.CATEGORY_NAME);
			subHeader.setText("Browsing " + categoryName);
		} else {
			subHeader.setText("Browsing Product Categories");
		}

//		LinearLayout header = (LinearLayout) this.findViewById(R.id.header);
//		if (getParent() != null) {
//			header.setVisibility(View.GONE);
//		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	class LoadCategoriesTask extends BBYAsyncTask {
		public LoadCategoriesTask(Activity activity) {
			super(activity, "Loading Categories...");
		}

		@Override
		public void doFinish() {
			showCategoryList();
		}

		@Override
		public void doTask() throws Exception {
			CategoryUtilities.loadCategory(_context, null, appData);
		}

		@Override
		public void doReconnect() {
			new LoadCategoriesTask(activity).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("categoryId", categoryId);
		outState.putString("categoryName", categoryName);
	}

	OnItemClickListener categoryBrowseClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			
			
			boolean isLeaf = ((CategoryBrowseWrapper) v.getTag()).isLeaf();

			if (isLeaf) { // at the end of the category tree, run a search
				Intent i;
				if (isCodeScan) 
					i = new Intent(v.getContext(), BrowseProductList.class);
				else 
					i = new Intent(v.getContext(), SearchResultList.class);
				SearchRequest searchRequest = new SearchRequest();
				boolean fetchedViaSearch = ((CategoryBrowseWrapper) v.getTag())
						.getFetchedViaSearch();
				if (fetchedViaSearch) {
					searchRequest.setRemixSort(false);
				} else {
					searchRequest.setRemixSort(true);
				}
				String categoryId = ((CategoryBrowseWrapper) v.getTag())
						.getId();
				searchRequest.setCategoryIdName(categoryId); // search using our
				// 301
				// cateogoryId
				appData.setSearchRequest(searchRequest);

				// (ICR Pricing)
				Category category = (Category) (parent).getAdapter().getItem(
						position);
				i.putExtra("categoryName", category.getName());

				startActivity(i);
			} else {
				if(((CategoryBrowseWrapper) v.getTag()).getNameText().equalsIgnoreCase(MOBILE_APPS)){
					Map<String, String> params = new HashMap<String, String>();
					params.put("value", EventsLogging.CUSTOM_CLICK_YOUR_APP_CENTER);
					EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
					
					Intent i = new Intent(v.getContext(), AppoliciousCategoriesList.class);
					startActivity(i);
				}else{
					Intent i = new Intent(v.getContext(), CategoryBrowse.class);
					String categoryId = ((CategoryBrowseWrapper) v.getTag()).getId();
					String categoryName = ((CategoryBrowseWrapper) v.getTag()).getNameText();
					String remixId = ((CategoryBrowseWrapper) v.getTag()).getRemixId();
					i.putExtra(AppData.CATEGORY_ID, categoryId);
					i.putExtra(AppData.CATEGORY_NAME, categoryName);
					i.putExtra(AppData.REMIX_CATEGORY_ID, remixId);
					i.putExtra(AppData.IS_CODE_SCAN, isCodeScan);
					startActivity(i);
				} 
			}
		}
	};

	/*private void runSearch(String query) {
		Intent i = new Intent(this, SearchResultList.class);

		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setCategoryIdName(categoryId);
		try {
			searchRequest.setFreeText(query); // is a value search
		} catch (NumberFormatException nfe) {
			searchRequest.setFreeText(query); // is a value search
		}

		appData.setSearchSortPosition(-1);
		appData.setSearchRequest(searchRequest);
		startActivity(i);
	}*/

	class CategoryAdapter extends ArrayAdapter<Category> {
		CategoryAdapter() {
			super(CategoryBrowse.this, R.layout.category_browse_row, currentCategoryList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;
			CategoryBrowseWrapper wrapper = null;

			// if (row == null) { // remove the caching of the rows since we
			// were getting multi-threading issues.
			LayoutInflater inflater = getLayoutInflater();
			row = inflater.inflate(R.layout.category_browse_row, parent, false);
			wrapper = new CategoryBrowseWrapper(row);
			row.setTag(wrapper);
			// } else {
			// wrapper = (CategoryBrowseWrapper) row.getTag();
			// }

			Category category = currentCategoryList.get(position);
			//ImageView icon = (ImageView) row.findViewById(R.id.category_browse_icon);

			if(position == currentCategoryList.size()){
				wrapper.setNameText(category.getName());
				wrapper.getName().setText(category.getName());
			}else{

				/*if (category.getImageURL() != null && category.getImageURL().length() > 0) {
					ImageProvider.getBitmapImageOnThread(category.getImageURL(), icon);
				}*/

				wrapper.setId(category.getId());
				wrapper.setNameText(category.getName());
				wrapper.getName().setText(category.getName());
				wrapper.setRemixId(category.getRemixId());
				wrapper.setLeaf(category.isLeaf());
				wrapper.setFetchedViaSearch(category.getFetchViaSearch());
			}
			return row;
		}
	}

	private LoadCategoryTask loadCategoryTask;

	public void executeLoadCategoryTask(String categoryId, AppData appDataLocal) {
		loadCategoryTask = new LoadCategoryTask(this, categoryId, appDataLocal);
		loadCategoryTask.execute(Void);
	}

	public void showCategoryList() {
		currentCategoryList = appData.getFromCategoryList(categoryId);
		if (this.getIntent().hasExtra(AppData.CATEGORY_ID)) {
			if(categoryName != null && categoryName.equalsIgnoreCase("Mobile Phones")){
				Category androidApp = new Category(MOBILE_APPS);
				currentCategoryList.add(androidApp);
			}
		}

		reviewsAdapter = new CategoryAdapter();
		myList = CategoryBrowse.this.getListView();
		try {
			myList.setOnItemClickListener(categoryBrowseClickedHandler);
			myList.setAdapter(reviewsAdapter);
		} catch (Exception ex) {
			BBYLog.e(TAG, "Exception connecting to get the category browse: " + ex.getMessage());
			BBYLog.printStackTrace(TAG, ex);
		}
	}

	/*@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("Loading Categories...");
		return dialog;
	}*/

	/**
	 * Loads the list of sub-categories for the specified category id
	 * 
	 * @author Recursive Awesome
	 * 
	 */
	private class LoadCategoryTask extends BBYAsyncTask {
		String categoryId;
		AppData appDataLocal;

		public LoadCategoryTask(Activity activity, String categoryId, AppData appDataLocal) {
			super(activity, "Loading Categories...");
			this.categoryId = categoryId;
			this.appDataLocal = appDataLocal;
		}

		@Override
		public void doFinish() {
			if (!appData.isCategoryListEmpty()) {
				showCategoryList();
			}
		}

		@Override
		public void doTask() throws Exception {
			CategoryUtilities.loadCategory(_context, categoryId, appDataLocal);
		}

		@Override
		public void doReconnect() {
			new LoadCategoryTask(activity, categoryId, appDataLocal).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			finish();
		}
	}

	@Override
	public boolean onSearchRequested() {
		Bundle data = new Bundle();
		if (categoryName != null) {
			data.putString("categoryName", categoryName);
		}
		appData.setSearchSortPosition(-1);
		startSearch(null, false, data, false);
		return true;
	}
}
