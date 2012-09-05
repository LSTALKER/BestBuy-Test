package com.bestbuy.android.rewardzone.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.ui.RewardZoneHeader;

/**
 * Displays the Terms and Conditions of RZOffer
 * 
 * @author sharmila_j
 * 
 */
public class RewardZoneLegalTerms extends MenuActivity {

	private RZAccount rzAccount;
	private AppData appData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_and_conditions);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();
		RewardZoneHeader.setUpHeader(rzAccount, this, false);
		WebView termsText = (WebView) findViewById(R.id.rz_terms_cond_text);
		String terms = null;
		if (getIntent() != null && getIntent().hasExtra("terms")) {
			terms = getIntent().getStringExtra("terms");
		}
		termsText.loadDataWithBaseURL(null, terms.replaceAll("%", "&#37;"),	"text/html", "utf-8", null);
	}
}
