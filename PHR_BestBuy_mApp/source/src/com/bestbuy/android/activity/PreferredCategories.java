package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Category;
import com.bestbuy.android.data.CategoryUtilities;
import com.bestbuy.android.util.CommerceAsyncTask;

public class PreferredCategories extends MenuActivity {

	private AppData appData;
	private List<String> selectedCategoryIds;
	private List<Category> offerCategories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		selectedCategoryIds = new ArrayList<String>();
		offerCategories = new ArrayList<Category>();
		new LoadCategoriesTask(this).execute();
	}
	
	public void showView() {
		setContentView(R.layout.preferred_categories);
		
		//Insert the categories
		LayoutInflater inflater = getLayoutInflater();
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.preferred_categories_layout);
		Button done = (Button) findViewById(R.id.done_btn);
		int offerCategoriesSize = offerCategories.size();
		int position = 0;
		
		for (final Category category : offerCategories) {
			final View categoryView = inflater.inflate(R.layout.preferred_category, categoryLayout, false);
			((TextView) categoryView.findViewById(R.id.preferred_category_text)).setText(category.getName());
			categoryLayout.addView(categoryView);
			if (offerCategories.indexOf(category) != offerCategories.size()-1) {
				inflater.inflate(R.layout.commerce_horizontal_line, categoryLayout, true);
			}
			
			//Show or hide the checkmarks according to the users preferences
			if (selectedCategoryIds.contains(category.getRemixId())) {
				categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.VISIBLE);
			} else {
				categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.INVISIBLE);
			}
			
			//Set the listener for each row
			categoryView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (selectedCategoryIds.contains(category.getRemixId())) {
						selectedCategoryIds.remove(category.getRemixId());
						categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.INVISIBLE);
					} else {
						selectedCategoryIds.add(category.getRemixId());
						categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.VISIBLE);
					}
				}
			});
			
			done.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CategoryUtilities.savePreferredCategoryIds(selectedCategoryIds);
					
					Intent prevIntent = getIntent();
					if(prevIntent != null) {
						if(prevIntent.hasExtra(AppData.PREFERED_CATEGORIES_ONRETURN)) {
							homeListener(null);
							return;
						}
					} 
					
					Intent i = new Intent(PreferredCategories.this, YourAccount.class);
					i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(i);
					finish();
				}
			});
			
			if(offerCategoriesSize == 1) {
				categoryView.setBackgroundResource(R.drawable.commerce_box);
		    } else {
		    	if(position == 0) {
		    		categoryView.setBackgroundResource(R.drawable.commerce_box_top);
			    } else if(position == (offerCategoriesSize - 1)) {
			    	categoryView.setBackgroundResource(R.drawable.commerce_box_bottom);
			    } else {
			    	categoryView.setBackgroundResource(R.drawable.commerce_box_middle);
			    }
		    }
			
			position++;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//CategoryUtilities.savePreferredCategoryIds(selectedCategoryIds);
		appData.getNotificationManager().setLastNotificationRetrievedDate(null);
		//appData.getNotificationManager().pruneNotifications();		
	}
	
	public class LoadCategoriesTask extends CommerceAsyncTask {
		public LoadCategoriesTask(Activity activity) {
			super(activity,appData.getCommerce());
		}

		@Override
		public void doFinish() {
			List<String> savedIds = CategoryUtilities.getPreferredCategoryIds();
			if (savedIds != null) {
				selectedCategoryIds.addAll(savedIds);
			}
			showView();
		}

		@Override
		public void doTask() throws Exception {
			CategoryUtilities.getOfferCategories(PreferredCategories.this, appData);
			offerCategories.addAll(CategoryUtilities.getOfferCategories(PreferredCategories.this, appData));
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
}
