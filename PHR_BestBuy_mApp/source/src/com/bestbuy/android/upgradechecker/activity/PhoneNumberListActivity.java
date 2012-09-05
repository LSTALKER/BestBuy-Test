package com.bestbuy.android.upgradechecker.activity;

import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.upgradechecker.activity.helper.PhoneNumberListAdapter;
import com.bestbuy.android.upgradechecker.data.Subscriber;
import com.bestbuy.android.util.FontLibrary;

public class PhoneNumberListActivity extends MenuActivity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.upgradechecker_phone_number_list);
		
		TextView eligible_phone_nbr=(TextView)findViewById(R.id.upgrade_eligible_phone_nbr); 
		ListView phoneNumberList=(ListView)findViewById(R.id.phoneNumberList);
		ImageView carrierLogo=(ImageView)findViewById(R.id.carrier_Logo);
		ImageButton upgrade_chkr_at_t_text = (ImageButton)findViewById(R.id.upgrade_chkr_at_t_text);
		Typeface headerFont=FontLibrary.getFont(R.string.droidsans, getResources());
		eligible_phone_nbr.setTypeface(headerFont);
		List<Subscriber> subscriberList=getApp().getSubscribers();
		PhoneNumberListAdapter adapter=new PhoneNumberListAdapter(this,subscriberList);
		phoneNumberList.setAdapter(adapter);
		
		if(getApp().getCarrierId() == R.id.linear_layout_at_t){
			carrierLogo.setImageResource(R.drawable.at_t_logo);
			upgrade_chkr_at_t_text.setVisibility(View.VISIBLE);
		} else if(getApp().getCarrierId() == R.id.linear_layout_verizon){
			carrierLogo.setImageResource(R.drawable.verizon_logo);
		 	upgrade_chkr_at_t_text.setVisibility(View.GONE);
		} else if(getApp().getCarrierId() == R.id.linear_layout_tmobile){
			carrierLogo.setImageResource(R.drawable.tmobile_icon);
			upgrade_chkr_at_t_text.setVisibility(View.GONE);
		} else if(getApp().getCarrierId() == R.id.linear_layout_sprint){
			carrierLogo.setImageResource(R.drawable.sprint_logo);
			upgrade_chkr_at_t_text.setVisibility(View.GONE);
		}	
	}
	
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) getApplicationContext();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	 /*Intent attIntent =null;
			 if(UpgradeChecker.SELECTED_CARRIER.equals("ATT")){
				 attIntent = new Intent(PhoneNumberListActivity.this, ATandTUpgradeChecker.class);
			 } else if(UpgradeChecker.SELECTED_CARRIER.equals("VERIZON")){
				 attIntent = new Intent(PhoneNumberListActivity.this, VerizonUpgradeChecker.class);					 
			 } else if(UpgradeChecker.SELECTED_CARRIER.equals("TMOBILE")){
				 attIntent = new Intent(PhoneNumberListActivity.this, TMobileUpgradeChecker.class);
			 } else if(UpgradeChecker.SELECTED_CARRIER.equals("SPRINT")){
				 attIntent = new Intent(PhoneNumberListActivity.this, SprintUpgradeChecker.class);
			 }*/
			 
			 PhoneNumberListActivity.this.finish();
			// startActivity(attIntent);
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
}
