package com.bestbuy.android.storeevent.activity;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.storeevent.util.StoreUtils;

/**
 * This activity is responsible to display the store events as per corresponding store id
 * @author lalitkumar_s
 */

public class StoreEventsActivity extends MenuActivity {
	private List<StoreEvents> eventsList;
	private LinearLayout eventsContainer;
	private AppData appData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.stev_events);
		
		eventsContainer = (LinearLayout)findViewById(R.id.events_container);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		appData = ((BestBuyApplication)this.getApplication()).getAppData();
		eventsList = appData.getStoreEvents();
		displayStoreEvents();
	}
	
	/**
	 * After getting the data from server it designs the layout dynamically at runtime.
	 */
	private void displayStoreEvents() {
		eventsContainer.removeAllViews();
		
		Iterator<StoreEvents> iterator = eventsList.iterator();
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		eventsContainer.removeAllViews();
		String prevDate = null;
		String currentDate = null;
		boolean isPostDateRowToAdd = true;
		while (iterator.hasNext()) {
			StoreEvents dataObject = (StoreEvents) iterator.next();
			
			if(prevDate == null) {
				prevDate = StoreUtils.convertToDateTimeStringType(dataObject.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_ONE);
				currentDate = prevDate;
				isPostDateRowToAdd = true;
			} else {
				currentDate = StoreUtils.convertToDateTimeStringType(dataObject.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_ONE);
				if(prevDate.equalsIgnoreCase(currentDate)) {
					isPostDateRowToAdd = false;
				} else {
					prevDate = currentDate;
					isPostDateRowToAdd = true;
				}
			}
			
			if(isPostDateRowToAdd) {
				LinearLayout header = (LinearLayout) inflater.inflate(R.layout.stev_post_date, null);
				TextView postDate = (TextView) header.findViewById(R.id.store_events_post_date);
				postDate.setText(currentDate);
				eventsContainer.addView(header);
			} 
			
			LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.stev_events_row, null);
			TextView title = (TextView) layout.findViewById(R.id.store_event_title);
			
			title.setText(dataObject.getTitle());
			
			layout.setTag(dataObject);
			layout.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Bundle bundle = getIntent().getExtras();
					String storeImage = null;
					if(bundle.containsKey(StoreUtils.STORE_IMAGE)) {
						storeImage =   bundle.getString(StoreUtils.STORE_IMAGE);
					}
					
					StoreEvents data = (StoreEvents) v.getTag();
					Intent intent = new Intent();
					intent.setClass(StoreEventsActivity.this, StoreEventsInfoActivity.class);
					intent.putExtra(StoreUtils.EVENTS_INFO, data);
					intent.putExtra(StoreUtils.STORE_IMAGE, storeImage);
					startActivity(intent);
				}
			});

			eventsContainer.addView(layout);
			
			LinearLayout line = (LinearLayout) inflater.inflate(R.layout.stev_horizontal_line, null);
			eventsContainer.addView(line);
		}
	}
}
