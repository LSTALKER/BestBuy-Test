package com.bestbuy.android.activity;

import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZPhone;
import com.bestbuy.android.util.EventsLogging;

/**
 * Displays the member's Reward Zone card
 * @author Recursive Awesome
 *
 */
public class RewardZoneCard extends MenuActivity {

	private AppData appData;
	private RZAccount rzAccount;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_zone_card);
		appData = ((BestBuyApplication)this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_CARD_IMAGE_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay()); 
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
		
		showView();
	}
	
	public void showView() {		
		TextView cardName = (TextView) findViewById(R.id.reward_zone_card_name);
		cardName.setText(rzAccount.getRzParty().getFirstName() 
				+ " " + rzAccount.getRzParty().getLastName());
		
		TextView cardNumber = (TextView) findViewById(R.id.reward_zone_card_number);
		cardNumber.setText("Member ID: " + rzAccount.getNumber());
		
		TextView cardPhone = (TextView) findViewById(R.id.reward_zone_card_phone);
		RZPhone phone = rzAccount.getRzParty().getRzPhones().get(0);
		cardPhone.setText("(" + phone.getAreaCode() + ") "
				+ phone.getNumber().substring(0, 3) + "-" 
				+ phone.getNumber().substring(3));
		
		if (rzAccount.isSilverStatus()) {
			ImageView cardImage = (ImageView) findViewById(R.id.reward_zone_card_image);
			cardImage.setImageResource(R.drawable.rzsilvercard);
			cardName.setTextColor(Color.BLACK);
			cardNumber.setTextColor(Color.BLACK);
			cardPhone.setTextColor(Color.BLACK);
		}
	}
	
}
