package com.bestbuy.android.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.AuthServer;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.MdotWebView;

/**
 * Class used to display the mDOT pages in webview for PDP, Cart, SignIn and Checkout.
 * @author Rajesh G
 * @version 1.0
 */

public class MDOTProductDetail extends MenuActivity {
	
	private  WebView webview;
	private Product _product;
	private MdotWebView mDotWebView;
	private final String REMOVE_SEARCH_BAR = "RemoveSearchBar";
	private String url;
	private boolean isFromIr;
	private Dialog dialog;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		url = (String)getIntent().getSerializableExtra(MDOT_URL);
		isFromIr=getIntent().getBooleanExtra("fromIR", false);
		setContentView(R.layout.mdotproductdetail);
		// We are not setting the URL apart from HOME. So check the URL existence else assume that the request is from result pages across app.
		if(url == null){
			_product = appData.getSelectedProduct();
			Map<String, String> params = new HashMap<String, String>();
			params.put("sku", _product.getSku());
			EventsLogging.fireAndForget(EventsLogging.PRODUCT_DETAIL_VIEW_PATH,
					params);
			StringBuffer urlBuffer = new StringBuffer();
			urlBuffer.append(AppConfig.getMdotURL()).append(getString(R.string.PDP_URL)).append("?skuId=");
			urlBuffer.append(_product.getSku()).append("&pid=").append(_product.getProductId()).append("&catId=&ev=prodView");
			url = urlBuffer.toString();
		}
		//We need webview instance, so we can implement on backpresed and onCreate to display the earlier page and dialogs respectively.
		mDotWebView = new MdotWebView();
		/*if(getIntent().getBooleanExtra("IsFromMyAccountPage", false))
			MdotWebView.isFromMyAccountPage = true;*/
		
		// Check for device Internet Connectivity
		isNetAvailable();
				
		if(getIntent().hasExtra(REMOVE_SEARCH_BAR)){
			findViewById(R.id.title_header).setVisibility(View.VISIBLE);
			findViewById(R.id.searchbar_header).setVisibility(View.GONE);  
		}
	}
	
	private void isNetAvailable() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!BaseConnectionManager.isNetAvailable(MDOTProductDetail.this) || BaseConnectionManager.isAirplaneMode(MDOTProductDetail.this)) {
					NoConnectivityExtension.noConnectivity(
							MDOTProductDetail.this, new OnReconnect() {
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
		new AuthServerTask(this).execute();
	}

	class AuthServerTask extends BBYAsyncTask {
		boolean serverStatus = false;
		

		public AuthServerTask(Activity activity) {
			super(activity, "Connecting...");
			if(isFromIr){
				enableLoadingDialog(false);
				showDialog(MdotWebView.PN_DOD_LOADING_DIALOG);
			}else{
				enableLoadingDialog(true);
			}
		}

		@Override
		public void doTask() throws Exception {
			if(url != null && url.contains(AppConfig.getMdotSignInURL()))
				serverStatus = AuthServer.authanticateMDotSignInServer(url);
			else
				serverStatus = AuthServer.authanticateMDotServer();
		}

		@Override
		public void doError() {
			if(dialog!=null && dialog.isShowing()){
				dialog.cancel();
			}
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
				if(isFromIr){
					mDotWebView.setLoadingDod(isFromIr);
				}else{
					mDotWebView.setLoadingDod(false);
				}
				if(!url.contains("ssl.m.bestbuy.com") && !url.contains("bbyoffer.appspot.com")){
					if(url.contains("?"))
						url = url + "&channel=mApp-And";
					else
						url = url + "?channel=mApp-And";
				}
					
				webview = mDotWebView.showWebView(url, MDOTProductDetail.this);
				
			} else {
				doError();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mDotWebView != null)
			mDotWebView.refreshCurrentUrl();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case MdotWebView.MDOT_LOADING_DIALOG:
			if(!isFromIr ||!MdotWebView.isLoadingDod){
				ProgressDialog dialog = new ProgressDialog(this);
				dialog.setIndeterminate(true);
				dialog.setCancelable(true);
				dialog.setMessage("Loading...");
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						if (webview != null) {
							webview.stopLoading();
							if (webview.canGoBack()){
								if(mDotWebView.isNewWindow()){
									removeChildWebView();
								}
								else
									webview.goBack();
							}
							else
								finish();
						}
					}
				});
				
				return dialog;
			}else if(isFromIr && !dialog.isShowing()) {
				//isFromIr=false;
				return getDodLoadingDialog();
			}
			break;
		case MdotWebView.PN_DOD_LOADING_DIALOG:	
				return getDodLoadingDialog();
		
		default:
			break;
		}
		
		if (this == null || this.isFinishing())
			return null;
		return null;
	}

	
	private Dialog getDodLoadingDialog() {
	
		dialog = new Dialog(this,android.R.style.Theme_Translucent);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_progress_dialog);
		dialog.setCancelable(true);
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
	public void onBackPressed() {
		if(isFromIr){
			Intent intent=new Intent(MDOTProductDetail.this,Home.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			mDotWebView.setBackButtonPressed(true);
			webview.goBack();
			finish();
		}else if(webview !=null && webview.canGoBack()){
			if(mDotWebView.isNewWindow()){
				removeChildWebView();
			}
			else{
				mDotWebView.setBackButtonPressed(true);
				webview.goBack();
			}
		}else{
			super.onBackPressed();
		}
	}
	
	private void removeChildWebView(){
		FrameLayout container = (FrameLayout) findViewById(R.id.webview_container);
		WebView newView = (WebView) container.getChildAt(1);
		if(newView.canGoBack())
			newView.goBack();
		else{
			container.removeViewAt(1);
			mDotWebView.setNewWindow(false);
		}
	}
}
