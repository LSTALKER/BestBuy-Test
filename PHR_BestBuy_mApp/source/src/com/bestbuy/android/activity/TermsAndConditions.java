package com.bestbuy.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.bestbuy.android.R;
import com.bestbuy.android.hem.webblock.EcoRequest;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;

public class TermsAndConditions extends MenuActivity {
	
	private String termsAndConditions = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_conditions);
		
		new TermsTask(this).execute();
	}

	private class TermsTask extends BBYAsyncTask {
		private String url = AppConfig.getBestbuyRebateURL() + "/api/config/termsOfUse.json";
		private String jsonResponse;
		
		public TermsTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			showView();
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new TermsTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else {
				showView();
			}
		}
		
		@Override
		public void doTask() throws Exception {
			jsonResponse = EcoRequest.getRequest(url);
			if (jsonResponse != null) {
				termsAndConditions = EcoRequest.parse(jsonResponse);
			} else {
				termsAndConditions = "";
			}
		}
	}

	private void showView() {
		WebView mWebView = (WebView) findViewById(R.id.webview);
		mWebView.loadDataWithBaseURL(null, termsAndConditions.replaceAll("%", "&#37;"), "text/html", "utf-8", null);
	}
}

