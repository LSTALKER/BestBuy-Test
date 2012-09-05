package com.bestbuy.android.hem.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuListActivity;
import com.bestbuy.android.hem.activity.helper.ApplianceListAdapter;
import com.bestbuy.android.hem.library.dataobject.BrandFinder;

public class LocalRebateFinderAppliance extends MenuListActivity implements OnItemClickListener	{
	
	private ListView lv;
	private ApplianceListAdapter applianceAdapter;
	private ArrayList<BrandFinder> list;
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_rebate_finder_appliancelist);
		
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
		if(this.getIntent() != null && this.getIntent().hasExtra(LocalRebates.PRODUCT_NAME)){
			 list = (ArrayList<BrandFinder>) this.getIntent().getSerializableExtra(LocalRebates.PRODUCT_NAME);
		}
		initializeAppliances();
	}
	
	private void initializeAppliances() {
		applianceAdapter = new ApplianceListAdapter(this, list);
		lv.setAdapter(applianceAdapter);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent i = new Intent(LocalRebateFinderAppliance.this, LocalRebateFinder.class);
		BrandFinder selectedItem = (BrandFinder) parent.getItemAtPosition(position);
		i.putExtra(LocalRebates.APPLIANCE_TEXT, selectedItem);
		setResult(RESULT_OK, i);
		finish();
	}
}

