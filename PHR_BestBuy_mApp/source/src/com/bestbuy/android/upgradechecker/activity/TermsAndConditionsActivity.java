package com.bestbuy.android.upgradechecker.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.BestBuyApplication;

public class TermsAndConditionsActivity extends MenuActivity {
	private ImageView carrierLogo;
	private ImageButton upgrade_chkr_at_t_text;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgradechecker_terms_conditions);
		carrierLogo=(ImageView)findViewById(R.id.carrier_Logo);
		upgrade_chkr_at_t_text=(ImageButton)findViewById(R.id.upgrade_chkr_at_t_text);
		if(getApp().getCarrierId()==R.id.linear_layout_at_t){
			carrierLogo.setImageResource(R.drawable.at_t_logo);
			upgrade_chkr_at_t_text.setVisibility(0);
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_verizon){
			carrierLogo.setImageResource(R.drawable.verizon_logo);
		 	upgrade_chkr_at_t_text.setVisibility(View.GONE);
		 
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_tmobile){
			carrierLogo.setImageResource(R.drawable.tmobile_icon);
			upgrade_chkr_at_t_text.setVisibility(View.GONE);
		}
		else if(getApp().getCarrierId()==R.id.linear_layout_sprint){
			carrierLogo.setImageResource(R.drawable.sprint_logo);
			upgrade_chkr_at_t_text.setVisibility(View.GONE);
		}		
	}
	
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) getApplicationContext();
	}
	
}
