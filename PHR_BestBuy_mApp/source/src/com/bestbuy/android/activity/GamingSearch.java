package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.AuthServer;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.MdotWebView;
import com.bestbuy.android.util.QRCodeWorker;

public class GamingSearch extends MenuActivity {

	private List<Product> searchResults = new ArrayList<Product>();
	private boolean search = false;
	private String query;
	private MdotWebView mDotWebView;
	private WebView webview;
	private String url;
	
	public static final String XBOX_360 = "Xbox 360";
	public static final String PS3 = "PS3";
	public static final String WII = "Wii";
	public static final String PS2 = "Playstation 2";
	public static final String PSP = "PSP";

	private List<String> platforms;

	private static Map<String,String> allPlatforms = new HashMap<String,String>();

	static{
		allPlatforms.put(XBOX_360, XBOX_360);
		allPlatforms.put(PS3, "PlayStation 3");
		allPlatforms.put(WII, "Nintendo Wii");
		allPlatforms.put(PS2, "PlayStation 2");
		allPlatforms.put(PSP, PSP);
	}

	public static Map<String,String> getAllPlatforms(){
		return allPlatforms;
	}

	public void setPlatforms(List<String> platforms){
		this.platforms = platforms;
	}

	public List<String> getPlatforms(){
		return platforms;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		appData.setScannedProduct(null);
		platforms = null;
		showView();
	}

	@Override
	protected void onNewIntent(Intent intent){
		if (intent != null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
			platforms = null;
			appData.setScannedProduct(null);
			query = intent.getStringExtra(SearchManager.QUERY);

			if(query != null && !query.equalsIgnoreCase("")){
				search = true;
			}else{
				search = false;
			}
			showView();
			
		}else if(platforms != null || appData.getScannedProduct() != null){
			search = true;
			showView();
		}
	}

	public void showView() {
		setContentView(R.layout.gaming_search);
		Button button = ((Button) findViewById(R.id.gaming_search_btn_action));

		if(search && appData.getScannedProduct() == null){
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(AppConfig.getMdotURL())
					.append(getString(R.string.GAMINGSEARCH_URL))
					.append("?usc=All+Categories").append("&st=").append(query)
					.append("&cp=1").append("&scValue=Y");
			
			url = urlBuffer.toString();

			mDotWebView = new MdotWebView();

			isNetAvailable();

		}else if(search && appData.getScannedProduct() != null) {

			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(AppConfig.getMdotURL())
					.append(getString(R.string.GAMINGSEARCH_URL))
					.append("?usc=All+Categories").append("&st=")
					.append(appData.getScannedProduct().getSku())
					.append("&cp=1").append("&scValue=Y");
			
			url = urlBuffer.toString();

			mDotWebView = new MdotWebView();
			
			isNetAvailable();

		}else{
			button.setVisibility(View.GONE);
			(findViewById(R.id.header_cart_button_layout)).setVisibility(View.VISIBLE); 

			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(AppConfig.getMdotURL()).append(getString(R.string.GAMETRADEIN_URL)).append("?usc=All+Categories");
			
			url = urlBuffer.toString();

			mDotWebView = new MdotWebView();

			isNetAvailable();
		}

		((ImageView) findViewById(R.id.gaming_search_search_overlay_btn_open)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new QRCodeWorker().openQRCode(GamingSearch.this, "GAMING_SCAN");
			}
		});

		final Button searchBar = (Button) findViewById(R.id.gaming_search_search);
		searchBar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onSearchRequested();
			}
		});
	}

	OnItemClickListener clickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			if(search){
				if (position >= 0 && position < searchResults.size()) {
					appData.setSelectedProduct(searchResults.get(position));
					Intent i = new Intent(v.getContext(), GamingDetail.class);
					startActivity(i);
				}
			}else{				
				if (position >= 1 && position < searchResults.size()+1) {
					appData.setSelectedProduct(searchResults.get(position-1));
					Intent i = new Intent(v.getContext(), GamingDetail.class);
					startActivity(i);
				}
			}
		}
	};

	@Override
	public void onBackPressed() {
		if (webview != null && webview.canGoBack()) {
			mDotWebView.setBackButtonPressed(true);
			webview.goBack();
		} else {
			super.onBackPressed();
		}
	}

	private void isNetAvailable() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!BaseConnectionManager.isNetAvailable(GamingSearch.this) || BaseConnectionManager.isAirplaneMode(GamingSearch.this)) {
					NoConnectivityExtension.noConnectivity(GamingSearch.this,
							new OnReconnect() {
								public void onReconnect() {
									isNetAvailable();
								}
							}, new OnCancel() {

								public void onCancel() {
									finish();
								}
							});
				} else {
					isServerAvailable();
				}
			}
		}, 0);
	}

	private void isServerAvailable() {
		new AuthServerTask(GamingSearch.this).execute();
	}

	class AuthServerTask extends BBYAsyncTask {
		boolean serverStatus = false;
		public AuthServerTask(Activity activity) {
			super(activity, "Connecting...");
		}

		@Override
		public void doTask() throws Exception {
			serverStatus = AuthServer.authanticateMDotServer();
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {

				public void onReconnect() {	
					new AuthServerTask(activity).execute();
				}
			}, new OnCancel() {

				public void onCancel() {
					finish();
				}
			});
		}
		
		@Override
		public void doFinish() {
			if (serverStatus) {
				if(url.contains("?"))
					url = url + "&channel=mApp-And";
				else
					url = url + "?channel=mApp-And";
				webview = mDotWebView.showWebView(url , GamingSearch.this);
			} else {
				doError();
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (this == null || this.isFinishing())
			return null;
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("Loading...");
		
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				if (webview != null) {
					webview.stopLoading();
					if (webview.canGoBack())
						webview.goBack();
					else
						finish();
				}
			}
		});
		
		return dialog;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		appData.setScannedProduct(null);
		appData.clearCompareProducts();
	}
}
