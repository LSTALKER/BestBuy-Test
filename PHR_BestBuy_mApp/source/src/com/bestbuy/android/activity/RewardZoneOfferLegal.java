package com.bestbuy.android.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.ui.RewardZoneHeader;

/**
 * Displays the terms of the reward zone certificates
 * 
 * @author Recursive Awesome
 */
public class RewardZoneOfferLegal extends MenuActivity {
	private AppData appData;
	private RZAccount rzAccount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_zone_offer_legal);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();

		RewardZoneHeader.setUpHeader(rzAccount, this, true);

		String legal = "";
		if (this.getIntent().hasExtra("legal")) {
			legal = this.getIntent().getExtras().getString("legal");
		}

		TextView legalCopy = (TextView) findViewById(R.id.reward_zone_offer_legal_copy);
		legalCopy.setText(legal);
	}
}
