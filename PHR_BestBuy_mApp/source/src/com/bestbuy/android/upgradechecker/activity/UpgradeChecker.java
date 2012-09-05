package com.bestbuy.android.upgradechecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.BestBuyApplication;

public class UpgradeChecker extends MenuActivity implements OnClickListener{
	 
	private LinearLayout at_t, sprint, tMobile, verizon;
	public static String SELECTED_CARRIER="";
	public static final String PLEASE_CHECK_THE_INTERNET_CONNECTION = "Please check the internet connection";
	public static final String PLEASE_CHECK_THE_AIRPLANE_MODE = "No network connectivity";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgradecheckerlist);
		at_t=(LinearLayout)findViewById(R.id.linear_layout_at_t);
		verizon=(LinearLayout)findViewById(R.id.linear_layout_verizon);
		sprint=(LinearLayout)findViewById(R.id.linear_layout_sprint);
		tMobile=(LinearLayout)findViewById(R.id.linear_layout_tmobile);
		
		at_t.setOnClickListener(this);
		verizon.setOnClickListener(this);
		sprint.setOnClickListener(this);
		tMobile.setOnClickListener(this); 			
	}

	public BestBuyApplication getApp() {
		return (BestBuyApplication) getApplicationContext();
	}

	public void onClick(View view) {
		
		if(view==at_t){
			getApp().setCarrierId(view.getId());
			Intent attIntent = new Intent(UpgradeChecker.this,
					ATandTUpgradeChecker.class);
			//this.finish();
			startActivity(attIntent);
		}
		else if(view==verizon){
			getApp().setCarrierId(view.getId());
			Intent verizonIntent = new Intent(UpgradeChecker.this,
					VerizonUpgradeChecker.class);
			//this.finish();
			startActivity(verizonIntent);			
		}
		else if(view==sprint){
			getApp().setCarrierId(view.getId());
			Intent sprintIntent = new Intent(UpgradeChecker.this,
					SprintUpgradeChecker.class);
			//this.finish();
			startActivity(sprintIntent);
		}
		else if(view==tMobile){
			getApp().setCarrierId(view.getId());
			Intent tmIntent = new Intent(UpgradeChecker.this,
					TMobileUpgradeChecker.class);
			//this.finish();
			startActivity(tmIntent);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {	    	    
	    	this.finish();	    	
	    }
	    return super.onKeyDown(keyCode, event);
	}	
}
