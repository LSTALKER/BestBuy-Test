package com.bestbuy.android.hem.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuListActivity;
import com.bestbuy.android.activity.TermsAndConditions;
import com.bestbuy.android.hem.activity.helper.RebateFinderListAdapter;
import com.bestbuy.android.hem.library.dataobject.EcoRebatesResponseDetails;
import com.bestbuy.android.hem.library.dataobject.ProductRebateDetails;

public class LocalRebateFinderproduct extends MenuListActivity {
	
	private TextView localRebateFinderTitleTV;
	private LinearLayout rebateFooterLL;
	private TextView terms;
	private ArrayList<ProductRebateDetails> productRebateDetailsList;
	
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_rebate_finder_productlist);
		
		localRebateFinderTitleTV = (TextView)findViewById(R.id.localRebateFinderTitleTV);
		rebateFooterLL = (LinearLayout)findViewById(R.id.rebateFooterLL);
		terms = (TextView)findViewById(R.id.terms);

		terms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(LocalRebateFinderproduct.this, TermsAndConditions.class);
				startActivity(i);
			}
		});
		
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				Bundle extras = getIntent().getExtras();
				String message = extras.getString(LocalRebates.DISPLAY_MESSAGE);		
				EcoRebatesResponseDetails ecoRebatesResponseDetails = (EcoRebatesResponseDetails) extras.getSerializable(LocalRebates.ECORABTE_RESPONSE_DETAILS);
				localRebateFinderTitleTV.setText(message);
				productRebateDetailsList = (ArrayList<ProductRebateDetails>) extras.getSerializable(LocalRebates.ECORABTE_PRODUCT_DETAILS);
				RebateFinderListAdapter rebateAdapter = new RebateFinderListAdapter(LocalRebateFinderproduct.this, productRebateDetailsList);
				rebateAdapter.setEcoRebatesResponseDetails(ecoRebatesResponseDetails);
				setListAdapter(rebateAdapter);
				LayoutInflater mLayoutInflater = LayoutInflater.from(LocalRebateFinderproduct.this);
				View v = mLayoutInflater.inflate(R.layout.local_rebate_finder_productlist_footer, null);
				getListView().addFooterView(v);	
				((View)findViewById(R.id.rebateFooterLine)).setVisibility(View.VISIBLE);
				rebateFooterLL.setVisibility(View.VISIBLE);
			}
		}, 100);
	}	
}
