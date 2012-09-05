package com.bestbuy.android.activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.ui.RewardZoneHeader;

public class RewardZoneSilverDetails extends MenuActivity {
	private RZAccount rzAccount;
	private AppData appData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();
		setContentView(R.layout.rz_silverstatus);
		RewardZoneHeader.setUpHeader(rzAccount, this, true);
		String url = "file:///android_asset/reward_zone.html";
		WebView webView = (WebView)findViewById(R.id.web_view);
		webView.loadUrl(url);
	}
}
