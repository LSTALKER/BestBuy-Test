package com.bestbuy.android.hem.activity;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuListActivity;
import com.bestbuy.android.hem.activity.helper.BrandListAdapter;
import com.bestbuy.android.hem.library.dataobject.BrandFinder;
import com.bestbuy.android.hem.library.dataobject.Brands;
import com.bestbuy.android.hem.library.util.StorePreferences;

public class LocalRebateFinderBrand extends MenuListActivity implements OnItemClickListener, OnClickListener{
	
	ListView listvw;
	BrandListAdapter brandAdapter;
	StorePreferences storePreferences;
	ArrayList<Brands> list;
	Button doneButton, allButton, noneButton;
	ArrayList<String> selectedItems=new ArrayList<String>();
	private long[] checkedItemIds;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_rebate_finder_brandlist);
		listvw = (ListView)findViewById(android.R.id.list);
		listvw.setHeaderDividersEnabled(false);
		listvw.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listvw.setOnItemClickListener(this);
		storePreferences = StorePreferences.getInstance();
		
		if(this.getIntent() != null && this.getIntent().hasExtra(LocalRebates.SELECTED_BRAND)){			
			BrandFinder selectedBrand= (BrandFinder) this.getIntent().getSerializableExtra(LocalRebates.SELECTED_BRAND);
			selectedItems=this.getIntent().getStringArrayListExtra(LocalRebates.SELECTED_BRAND_POSITIONS);
			list = selectedBrand.getBrands();			
		}
		initializeBrands();
		
		checkedItemIds = storePreferences.retriveFromPersistence();
		if (checkedItemIds != null) {
			for(int id_count = 0; id_count < checkedItemIds.length; id_count++) {
				listvw.setItemChecked((int) checkedItemIds[id_count], true);
			}
		}
		else if(selectedItems!=null){
			for(int id_count = 0; id_count < selectedItems.size(); id_count++) {
				listvw.setItemChecked(Integer.parseInt(selectedItems.get(id_count)), true);				
			}
		}
		allButton = (Button) findViewById(R.id.all_btn);
		allButton.setOnClickListener(this);
		noneButton = (Button) findViewById(R.id.none_btn);
		noneButton.setOnClickListener(this); 
		doneButton = (Button) findViewById(R.id.done_btn);
		doneButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ArrayList<Brands> selectedList = new ArrayList<Brands>();
				Intent intent = new Intent(LocalRebateFinderBrand.this, LocalRebateFinder.class);
				if(selectedItems.size()!=0){
					long[] listCheckedItems=new long[selectedItems.size()];
					for(int index=0;index<selectedItems.size();index++){
						listCheckedItems[index]=Long.parseLong(selectedItems.get(index));
					}				
					storePreferences.saveToPersistence(listCheckedItems);
					long[] checkedItems = listCheckedItems;
 					for(int i =0; i<checkedItems.length; i++){
						Brands b = (Brands) listvw.getItemAtPosition((int) checkedItems[i]);
						selectedList.add(b);
					}
				}
								
				intent.putExtra(LocalRebates.SELECTED_ITEMS, selectedList);
				intent.putExtra(LocalRebates.SELECTED_ITEMS_POSITION, selectedItems);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initializeBrands() {
		brandAdapter = new BrandListAdapter(this, list);
		listvw.setAdapter(brandAdapter);
	}
	
	
	public void onItemClick(AdapterView<?> arg0, View view, int postion, long id) {
		//New Implementation
		if(selectedItems.contains(String.valueOf(postion))){			
			selectedItems.remove(String.valueOf(postion));
			listvw.setItemChecked(postion, false);
		}
		else{			
			selectedItems.add(String.valueOf(postion));
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.all_btn:
			for (int id_count = 0; id_count < list.size(); id_count++) {
				listvw.setItemChecked(id_count, true);
				if (!selectedItems.contains(String.valueOf(id_count))) {
					selectedItems.add(String.valueOf(id_count));
				}
			}
			break;
		case R.id.none_btn:
			for (int id_count = 0; id_count < list.size(); id_count++) {
				listvw.setItemChecked(id_count, false);
			}
			selectedItems.clear();
			break;

		}
	}
}
