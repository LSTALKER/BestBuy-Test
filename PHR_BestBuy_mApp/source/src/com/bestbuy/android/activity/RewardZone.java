package com.bestbuy.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.rewardzone.activity.RewardZoneOffers;
import com.bestbuy.android.util.FontLibrary;

/**
 * Main reward zone activity. Displays Points, Purchases, and Offers tabs.
 * 
 * @author Recursive Awesome
 * 
 */

public class RewardZone extends MenuTabActivity {

	private static final int TAB_HEIGHT = 50;
	private static final String OFFERS = "offers";
	private static final String POINTS = "points";
	private static final String PURCHASES = "purchases";

	@SuppressWarnings("unused")
	private String TAG = this.getClass().getName();
	private TabHost tabHost;
	private int selectedTab;
	private int currentTab = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_zone_main);
		intializeComponents();		
	}	
	
	private void intializeComponents(){		
		Typeface droidSansBold = FontLibrary.getFont(R.string.droidsansbold, getResources());
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		View view;

		intent = new Intent().setClass(this, RewardZonePoints.class);	
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		view = LayoutInflater.from(this).inflate(R.layout.tab_rz, null);
		view.setBackgroundResource(R.drawable.rz_mypoints_tab);
		TextView iv = (TextView) view.findViewById(R.id.tab_text);
		iv.setText("My Points");
		iv.setTypeface(droidSansBold);
		iv.setTag(POINTS);
		spec = tabHost.newTabSpec(POINTS).setIndicator(view).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, RewardZonePurchases.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		view = LayoutInflater.from(this).inflate(R.layout.tab_rz, null);
		view.setBackgroundResource(R.drawable.rz_purchases_tab);
		iv = (TextView) view.findViewById(R.id.tab_text);
		iv.setText("Purchases");
		iv.setTypeface(droidSansBold);
		iv.setTag(PURCHASES);
		spec = tabHost.newTabSpec(PURCHASES).setIndicator(view).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, RewardZoneOffers.class);	
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		view = LayoutInflater.from(this).inflate(R.layout.tab_rz, null);
		view.setBackgroundResource(R.drawable.rz_offers_tab);
		iv = (TextView) view.findViewById(R.id.tab_text);
		iv.setText("Offers");
		iv.setTypeface(droidSansBold);
		iv.setTag(OFFERS);
		spec = tabHost.newTabSpec(OFFERS).setIndicator(view).setContent(intent);
		tabHost.addTab(spec);		
		tabHost.setCurrentTab(currentTab);
		initTabs();
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabSpec) {
				initTabs();			
			}
		});
	}
	public void initTabs() {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			View view = tabHost.getTabWidget().getChildAt(i);
			view.getLayoutParams().height = TAB_HEIGHT;
			TextView tv = (TextView) view.findViewById(R.id.tab_text);
			selectedTab = tabHost.getCurrentTab();
			if (i == selectedTab) {
				tv.setTextColor(Color.rgb(0, 59, 100));
			} else {
				tv.setTextColor(view.getContext().getResources().getColor(android.R.color.black));
			}
		}
	}

}
