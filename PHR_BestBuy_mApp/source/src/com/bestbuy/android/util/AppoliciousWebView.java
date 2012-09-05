package com.bestbuy.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

public class AppoliciousWebView implements GeolocationPermissions.Callback{
	public static final int MDOT_LOADING_DIALOG = 1978;
	public static final int WHISH_LIST = 1;
	public static final int ADD_TO_CART_URL = 2;
	public static final int VIEW_ORDER_URL = 3;
	private Activity mContext;
	public Callback callbackObj = null;
	public String urlOrigin = null;
	private static String currentDisplayedUrl = "";
	private WebView mDotWebview;
	private boolean isLoaded = false;
	private String failedUrl;
	
	public WebView showWebView(String url, final Activity context) {
		mContext = context;
		// Call the webview
		mDotWebview  = (WebView) context.findViewById(R.id.webview);
		/* JavaScript must be enabled to display extended version */
		mDotWebview.getSettings().setJavaScriptEnabled(true);
		mDotWebview.getSettings().setPluginsEnabled(true);
		mDotWebview.getSettings().setGeolocationEnabled(true);
		mDotWebview.getSettings().setUserAgentString(mDotWebview.getSettings().getUserAgentString()/*+"Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"*/+ " (MappAndroid)");
		
		mDotWebview.addJavascriptInterface(AppoliciousWebView.this, "MdotWebView");

		handler.postDelayed(new Runnable(){

			public void run() {
				if(!isLoaded){
					isLoaded = false;
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
			        alertDialog.setTitle("");
					alertDialog.setMessage("Cannot load this page");	
					alertDialog.setCancelable(false);
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {          	
			            	dialog.cancel();
			            	mDotWebview.stopLoading();
			            	mContext.finish();
			            	
			            }            
			        });
					alertDialog.create();
					alertDialog.show();
				}
			}
		}, 30000);
		
		mDotWebview.setWebViewClient(new WebViewClient() {
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				context.showDialog(MDOT_LOADING_DIALOG);
			}
	      		
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(!context.isFinishing())
					context.removeDialog(MDOT_LOADING_DIALOG);
				isLoaded = true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				isLoaded = true;
				
				failedUrl = failingUrl;
				
				if(errorCode == -6) {
					NoConnectivityExtension.noConnectivity(mContext, new OnReconnect() {
    					public void onReconnect() {
    						mContext.showDialog(MDOT_LOADING_DIALOG);
    						mDotWebview.loadUrl(failedUrl);
    					}		
    				}, new NoConnectivityExtension.OnCancel() {
    					public void onCancel() {
    						mContext.finish();
    					}
    				});
				}
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("market://")) {
					Intent market_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					mContext.startActivity(market_intent);
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
		
		AppoliciousWebChromeClient geo = new AppoliciousWebChromeClient();
	    mDotWebview.setWebChromeClient(geo);        
		mDotWebview.loadUrl(url);
		return mDotWebview;
	}
	
	public static void updateHeaderBar(String url, Context context) {
		//Set in order to get the current URL
		
		int header_search = View.VISIBLE;
		int header_home = View.VISIBLE;
		int header_cart = View.VISIBLE;
		int header_logo = View.VISIBLE;
		int my_cart = View.INVISIBLE;
		
		Button headerSearchBtn = (Button) ((Activity) context).findViewById(R.id.header_search);
		if (headerSearchBtn != null) {
			headerSearchBtn.setVisibility(header_search);
		}

		ImageView headerHome = (ImageView) ((Activity) context).findViewById(R.id.header_home);
		if (headerHome != null) {
			headerHome.setVisibility(header_home);
		}

		ImageView headerCart = (ImageView) ((Activity) context).findViewById(R.id.header_cart);
		if (headerCart != null) {
			headerCart.setVisibility(header_cart);
		}

		ImageView headerLogo = (ImageView) ((Activity) context).findViewById(R.id.header_logo);
		if (headerLogo != null) {
			headerLogo.setVisibility(header_logo);
		}

		TextView myCart = (TextView) ((Activity) context).findViewById(R.id.my_cart);
		if (myCart != null) {
			myCart.setVisibility(my_cart);
		}
	}
	
	public void showHTML(final String html,final int display) {
		 
		BBYLog.d("MdotWebView>>>",html);
		 
		if (html !=null && html.trim().length() != 0) {
        	//Extract the cart count alone and strip off other texts
        	int qty = Integer.parseInt(html.replaceAll("\\D+",""));
        	//Store it in Preference
        	AppData.getSharedPreferences().edit().putInt(AppData.CART_QTY, qty).commit();
		}else{
			//If there are no values assume that there is no item in the cart, so reset it to 0
			AppData.getSharedPreferences().edit().putInt(AppData.CART_QTY, 0).commit();
		}
		
		handler.sendEmptyMessage(display);
	}
	
	Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {
			
			int what  = msg.what;
			
			final int cartQty = AppData.getSharedPreferences().getInt(AppData.CART_QTY, 0);
			View cartBadge = mContext.findViewById(R.id.header_cart_badge);
			TextView cartBadgeText = (TextView) mContext.findViewById(R.id.header_cart_badge_text);
			ImageView headerCart = (ImageView) mContext.findViewById(R.id.header_cart);
			
	  
			if(cartBadge != null && cartBadgeText != null){
				if(cartQty > 0 && headerCart.getVisibility() == View.VISIBLE){
					if(what  == 1) {
						cartBadge.setVisibility(View.VISIBLE);
						cartBadgeText.setVisibility(View.VISIBLE);
					}
					if(cartQty > 9)
						cartBadge.setBackgroundResource(R.drawable.btn_cart_badge_oval);
					else
						cartBadge.setBackgroundResource(R.drawable.btn_cart_badge);
					cartBadgeText.setText(String.valueOf(cartQty));
				}else{
					cartBadge.setVisibility(View.INVISIBLE);
					cartBadgeText.setVisibility(View.INVISIBLE);
				}
			}

		};
	};
	
//	private void set(){
//		callbackObj.
//	}

	public static String getCurrentDisplayedUrl() {
		return currentDisplayedUrl;
	}
	
	final class AppoliciousWebChromeClient extends WebChromeClient {
		
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final android.webkit.JsResult result) {
			new AlertDialog.Builder(mContext)
					.setTitle("BestBuy")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									result.confirm();
								}
							}).setCancelable(false).create().show();

			return true;
		};
		    
	    @Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			
			view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
			
			if(!(view.getUrl().contains(mContext.getResources().getString(R.string.GAMETRADEIN_URL)) || view.getUrl().contains(mContext.getResources().getString(R.string.GAMINGSEARCH_URL)))) {
				updateHeaderBar(view.getUrl(),mContext);
			}
		}
		    
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if(newProgress <70) {
				view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
			} 
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
		Callback callback) {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsShowPrompt(origin, callback);
			callbackObj = callback;
			urlOrigin = origin;
			if(AppData.isCurrentLocationAlloweded(mContext))
				callback.invoke(origin, true, false);
			else
				//alertforCurrentLocationPermission();
				callbackObj.invoke(urlOrigin, false, false);
		}	
	}

	public void invoke(String origin, boolean allow, boolean remember) {
		
	}
	
	
	/*private void alertforCurrentLocationPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("");
		alertDialog.setMessage(R.string.ISUSE_CURRENT_LOCATION);	
		alertDialog.setCancelable(false);
		alertDialog.setPositiveButton(R.string.DONT_ALLOW, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {          	
            	AppData.setCurrentLocationAllow(mContext, false);
            	dialog.cancel();
            	callbackObj.invoke(urlOrigin, false, false);
            }            
        });
		
        alertDialog.setNegativeButton(R.string.ALLOW, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {            	
            	AppData.setCurrentLocationAllow(mContext, true);
            	
            	MyLocationManager mLocationManager = new MyLocationManager(mContext);
        		boolean areLocationProvidersEnabled = mLocationManager.areLocationProvidersEnabled();
            	if(!areLocationProvidersEnabled) {
            		locationProviderDisabledMsg();
            		dialog.cancel();
            		callbackObj.invoke(urlOrigin, true, false);
            	}
            }
        });	
        
        alertDialog.setOnCancelListener(new OnCancelListener() {			
			public void onCancel(DialogInterface arg0) {				
				AppData.setCurrentLocationAllow(mContext, false);
			}
		});
		alertDialog.create();
		alertDialog.show();
	}
	
	private void locationProviderDisabledMsg() {
		
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Location providers disabled !");
		alertDialog.setMessage("Location providers are disabled, Please enable it from Location & security settings");	
		alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {          	
            	// launch Location & Settings activity 
            	ComponentName toLaunch = new ComponentName("com.android.settings", "com.android.settings.SecuritySettings"); 
			    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS); 	               
			    intent.setComponent(toLaunch); 	                 
			    mContext.startActivity(intent);   
			  //  isGPSAllowed = true;
            }            
        });        		
		alertDialog.create();
		alertDialog.show();
	}*/
}
