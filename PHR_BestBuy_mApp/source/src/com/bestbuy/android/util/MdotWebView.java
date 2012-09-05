package com.bestbuy.android.util;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.Home;
import com.bestbuy.android.activity.YourAccount;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SkuManager;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

public class MdotWebView implements GeolocationPermissions.Callback{

	
	public static final int MDOT_LOADING_DIALOG = 1978;
	public static final int PN_DOD_LOADING_DIALOG = 1988;
	public static final int WHISH_LIST = 1;
	public static final int ADD_TO_CART_URL = 2;
	public static final int VIEW_ORDER_URL = 3;
	public static final int HIDE_CART_BADGE = 2000;
	private static final String GET_ITEM_COUNT_AND_DISPLAY = "javascript:window.MdotWebView.showHTML(document.getElementById('cart').innerHTML,1);void(0);";
	private static final String GET_ITEM_COUNT = "javascript:window.MdotWebView.showHTML(document.getElementById('cart').innerHTML,2);void(0);";
	private static final String GET_ITEM_COUNT_CLASSNAME = "javascript:window.MdotWebView.showHTML(document.getElementsByClassName('cart')[0].innerHTML,1);void(0);";
	private static final String GET_LOGIN_CRADENTIAL = "javascript:window.MdotWebView.getLoginCredential(document.forms('frmSignIn').elements(4).value,document.forms('frmSignIn').elements(6).value,1);";
	private static final String ORDER_GET_LOGIN_CRADENTIAL = "javascript:window.MdotWebView.getLoginCredential(document.getElementsByName('email2').item(0).value,document.getElementsByName('password2').item(0).value,1);void(0);";
	private static final String GET_CHECKOUT_LOGIN_CRADENTIAL = "javascript:window.MdotWebView.getLoginCredential(document.forms('frmSignIn').elements(2).value,document.forms('frmSignIn').elements(4).value,1);";
	private static final String BLANK_PDP_CHECK = "javascript:window.MdotWebView.checkBlankPDP(document.getElementById('content-wrapper').innerHTML,1);void(0);";
	private Activity mContext;
	private boolean backButtonPressed;
	//private static View cartBadge = null;
	//private static TextView cartBadgeText =null;
	private static String currentDisplayedUrl = ""; 
	public static boolean isFromMyAccountPage = false;
	//private static boolean isWishListSignin = false;
	private static String email = null;
	private static String password = null;
	private String signinURL = null;
	//private CCommerce commerce;
	//private AppData appData;
	private WebView mDotWebview;
	private int loadURLCount = 0;
	private Resources stringResource;
	private String failedUrl;
	private BestBuyApplication app;
	private String skuSearch = null;
	public Callback callbackObj = null;
	public String urlOrigin = null;
	public static boolean isLoadingDod;
	private boolean isNewWindow = false;
	private String currentURL;
	
	public WebView showWebView(String url, final Activity context) {
		mContext = context;
		//appData = ((BestBuyApplication) mContext.getApplication()).getAppData();
		app = (BestBuyApplication) mContext.getApplication();;
		//commerce = new CCommerce();
		stringResource = context.getResources();
		// Call the webview
		mDotWebview  = (WebView) context.findViewById(R.id.webview);
		/* JavaScript must be enabled to display extended version */
		mDotWebview.getSettings().setJavaScriptEnabled(true);
		mDotWebview.getSettings().setSupportMultipleWindows(true);
		//Adding the user agent to identify the revenue from mDOT for site catalyst
        //We are going to change the append string.
		//Please do not remove the below comment until the testing is finished with DEV box.
		mDotWebview.getSettings().setUserAgentString(mDotWebview.getSettings().getUserAgentString()/*+"Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"*/+ " (MappAndroid)");
		mDotWebview.addJavascriptInterface(MdotWebView.this, "MdotWebView");
		
		final WebViewClient webviewClient = new WebViewClient() {
			//Getting SSL error when testing with BBY dev box for Sitecatalyst since the changes are available ONLY there. This might not be needed for 
			//production push though this wont harm anything.
			public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
				 handler.proceed() ;
				 }
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				failedUrl = failingUrl;
				
				if(errorCode == -6 || errorCode == -2) {
					NoConnectivityExtension.noConnectivity(mContext, new OnReconnect() {
    					public void onReconnect() {
    						if(!isLoadingDod){
    							mContext.showDialog(MDOT_LOADING_DIALOG);
    						}else{
    							isLoadingDod=false;
    							context.showDialog(PN_DOD_LOADING_DIALOG);
    						}
    							mDotWebview.loadUrl(failedUrl);
    					}		
    				}, new NoConnectivityExtension.OnCancel() {
    					public void onCancel() {
    						mContext.onBackPressed();
    					}
    				});
				}
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.startsWith("tel:")){
					view.stopLoading();
					//view.goBackOrForward(-0);
					Intent dial = new Intent();
					dial.setAction("android.intent.action.CALL");
					dial.setData(Uri.parse(url));
					context.startActivity(dial); 
					 return true;
				}else if (url.startsWith(MailTo.MAILTO_SCHEME)) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
					context.startActivity(intent);
				    return true;
				}
				else if(url.startsWith("market://")) {
					Intent market_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					mContext.startActivity(market_intent);
					return true;
				}
				else if(url.contains("error.jsp")){
					if(getSkuSearch()!=null){
						AlertDialog dialog = new AlertDialog.Builder(mContext)
						.setTitle("Error")
						.setMessage("SKU " +getSkuSearch()+ " was not found!" )
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								context.finish();
							}
						}).create();
						dialog.show();
						return true;
					}
					else
						return false;
				}
				else{
					//Needed when pointing to any QA environment
					if(AppConfig.isTest())
						url = url.replaceAll(AppData.getContext().getString(R.string.mdot_url_production), AppConfig.getMdotURL());
					
					if(url.contains("?"))
						url = url + "&channel=mApp-And";
					else
						url = url + "?channel=mApp-And";
					currentURL = url;
					if(!url.contains(AppData.getContext().getString(R.string.appcenter_terms_conditions)))
						view.loadUrl(url);
					if((!mContext.isFinishing() && url.contains("compare"))){
						return true;
					}
					else
						return false;
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if(!isLoadingDod){
					context.showDialog(MDOT_LOADING_DIALOG);
				}else{
					context.showDialog(PN_DOD_LOADING_DIALOG);
				}
				if (url.contains("skuId")) {
					try {
						List<NameValuePair> list = URLEncodedUtils.parse(
								URI.create(url), "UTF-8");
						for (NameValuePair param : list) {
							if (param.getName().equals("skuId")) {
								Product product = new Product();
								product.setSku(param.getValue());
								SkuManager.addToRecentProducts(context, product);
							}
						}
					} catch (Exception e) {
						// Throw run time exception for Production.
						BBYLog.printStackTrace("Sku Id : ", e);
					}
					
					if(url.contains(stringResource.getString(R.string.REVIEW_CONFIRMATION_URL))) {
						view.stopLoading();
						AppData.setSplashScreen(true);
						Intent i = new Intent(context, Home.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(i);
					}
				} else if (url.contains(stringResource.getString(R.string.INDEX_PAGE))) {
					view.stopLoading();
					AppData.setSplashScreen(true);
					if(isFromMyAccountPage){
						if(email != null && password != null){
							isFromMyAccountPage = false;
							YourAccount.emailAddress = email;
							YourAccount.password = password;
							email = null;
							password = null;
							context.finish();
						}
						else{
							if(loadURLCount < 2)
								loadURLCount++;
							else{
								CookieSyncManager.createInstance(mContext); 
							    CookieManager cookieManager = CookieManager.getInstance();
							    cookieManager.removeSessionCookie();
							    loadURLCount = 0;
							}
							mDotWebview.loadUrl(signinURL);
						}
					}
					else{
						Intent i = new Intent(mContext, Home.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						mContext.startActivity(i);
					}
				}
				
				else if(url.contains(stringResource.getString(R.string.SIGNIN_BTN_SIGNIN_PAGE))){
					view.loadUrl(GET_LOGIN_CRADENTIAL);
				}
				
				else if(url.contains(stringResource.getString(R.string.CHECKOUT_SIGNIN))){
					view.loadUrl(GET_CHECKOUT_LOGIN_CRADENTIAL);
				}
				
				else if(url.contains(stringResource.getString(R.string.ORDER_STATUS_SIGNIN))){
					view.loadUrl(ORDER_GET_LOGIN_CRADENTIAL);
				}
					
				else if(url.contains("signin.jsp"))
					signinURL = url;
			}
	      		
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				//Update the header bar. We can update the cart count once the page is finished its loading because we can get the correct data
				//if the page loaded properly.
				if(url.contains(stringResource.getString(R.string.ACCESSORIES_ADD_TO_CART_PAGE)) ){
					view.loadUrl(GET_ITEM_COUNT_AND_DISPLAY);
				}else if(!backButtonPressed && url.contains(stringResource.getString(R.string.ADDTOCART_PAGE))) {
					isFromMyAccountPage = false;
					view.loadUrl(GET_ITEM_COUNT);
				}else if(url.contains(stringResource.getString(R.string.CONTINUE_BTN_REVIEW_PAGE))) {
					view.loadUrl(GET_ITEM_COUNT_AND_DISPLAY);  
				}else if(url.contains(stringResource.getString(R.string.SIGNIN_BTN_SHIPPICUP_PAGE))) {
					view.loadUrl(GET_ITEM_COUNT_CLASSNAME);  
				}
				else if(url.contains(stringResource.getString(R.string.THANKYOU_PAGE))) {
					handler.sendEmptyMessage(HIDE_CART_BADGE);
					AppData.getSharedPreferences().edit().putInt(AppData.CART_QTY, 0).commit();
				}
				else {
					handler.sendEmptyMessage(1);
				}
				backButtonPressed = false;
				
				if(url.contains("catalog/category.jsp") || url.contains("catalog/list.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.browse_css, view.getContext()));
				
				if(url.contains("catalog/list.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.browse_plp_css, view.getContext()));
				
				if(url.contains("search/results.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.search_plp_css , view.getContext()));
				
				if(url.contains("compare"))
					view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
				
				if(url.contains("tradeingames"))
					view.loadUrl(JSHelper.getScript(R.string.game_tradein_css, view.getContext()));
				
				if(url.contains("catalog/toggle.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.browse_plp_css, view.getContext()));
				
				if(url.contains(mContext.getResources().getString(R.string.MDOT_SKUSEARCH_URL))){
					view.loadUrl(BLANK_PDP_CHECK);
				}
				
				if(!context.isFinishing() && !url.contains(stringResource.getString(R.string.INDEX_PAGE)))
					if(!isLoadingDod){
						context.removeDialog(MDOT_LOADING_DIALOG);
					}else{
						context.removeDialog(PN_DOD_LOADING_DIALOG);
						isLoadingDod=false;
					}	
				}
		};
		
		mDotWebview.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(context)
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
				
				if(view.getUrl().contains("search")){
					view.loadUrl(JSHelper.getScript(R.string.plp_css , view.getContext()));
				} else {
					view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
				}
				
				//view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
				view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
				
				if(view.getUrl().contains("catalog/category.jsp") || view.getUrl().contains("catalog/list.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.browse_css, view.getContext()));
				
				if(view.getUrl().contains("catalog/list.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.browse_plp_css, view.getContext()));
				
				if(view.getUrl().contains("search/results.jsp"))
					view.loadUrl(JSHelper.getScript(R.string.search_plp_css , view.getContext()));
				
				if(!(view.getUrl().contains(context.getResources().getString(R.string.GAMETRADEIN_URL)) || view.getUrl().contains(context.getResources().getString(R.string.GAMINGSEARCH_URL)))) {
					updateHeaderBar(view.getUrl(),context);
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
			
			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg){
				WebView childView = new WebView(mContext);
				FrameLayout container = (FrameLayout) mContext.findViewById(R.id.webview_container);
				final WebSettings settings = childView.getSettings();
				settings.setJavaScriptEnabled(true);
				childView.setWebChromeClient(this);
				childView.setWebViewClient(webviewClient);
				childView.getSettings()
						.setJavaScriptCanOpenWindowsAutomatically(true);
				childView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				container.addView(childView);
				childView.loadUrl(currentURL);
				resultMsg.sendToTarget();
				setNewWindow(true);
				return true;
			}
		});
		
		mDotWebview.setWebViewClient(webviewClient);
		mDotWebview.loadUrl(url);
		return mDotWebview;
	}
	
	public void setBackButtonPressed(boolean backButtonPressed) {
		this.backButtonPressed = backButtonPressed;
	}
	
	public static String getUrl(Context context, int urlKey){
		
		/*String hostPath= "https://ssl.m.bestbuy.com/m/e";
		String addToCartHost = "http://m.bestbuy.com/m/e";*/
		String url = "";
		switch(urlKey){
			case WHISH_LIST:
					//url = String.format("%s/%s", hostPath,"wishlist/searchwishlist.jsp");
					url = getmDotURL(AppConfig.getMdotSignInURL(),context.getResources().getString(R.string.WISHLIST_URL),null);
				break;
			case ADD_TO_CART_URL:
				//url = String.format("%s/%s", addToCartHost,"cart/cart.jsp?ev=scView");
				url = getmDotURL(AppConfig.getMdotURL(),context.getResources().getString(R.string.CART_URL),null);
				break;
			case VIEW_ORDER_URL:
				//url = String.format("%s/%s", hostPath,"myaccount/vieworder.jsp");
				url = getmDotURL(AppConfig.getMdotSignInURL(),context.getResources().getString(R.string.ORDER_URL),null);
				break;
		}
		return url;
		
	}
	
	public static String getmDotURL(String host, String path, Map<String,String> urlParams) {
		StringBuffer mDotURL = new StringBuffer();
		mDotURL.append(host);
		if( path!=null ) {
			mDotURL.append(path);
		}
		
		if (urlParams != null) {
			Iterator<Map.Entry<String, String>> iter = urlParams.entrySet().iterator();
			if(iter.hasNext()) {
				mDotURL.append("?");
			}
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				mDotURL.append(pair.getKey() + "=" + pair.getValue());
				if (iter.hasNext()) {
					mDotURL.append("&");
				}
			}
		}
		
		return mDotURL.toString();
	}
	public static void updateHeaderBar(String url, Context context) {
		BBYLog.d("MdotWebView**************update called :" , url);
		
		//Set in order to get the current URL
		currentDisplayedUrl = url;
		
		Resources stringResource = context.getResources();
		int header_search = View.VISIBLE;
		int header_home = View.VISIBLE;
		int header_cart = View.VISIBLE;
		int header_logo = View.VISIBLE;
		int my_cart = View.INVISIBLE;
		if(url.contains( stringResource.getString(R.string.ADDTOCART_PAGE))){
			header_cart= View.INVISIBLE;
			header_logo=View.INVISIBLE;
			header_search = View.INVISIBLE;
			my_cart = View.VISIBLE;
		}else  if(url.contains(stringResource.getString(R.string.THANKYOU_PAGE)) 
				|| url.contains(stringResource.getString(R.string.PDP_PAGE)) 
				|| url.contains(stringResource.getString(R.string.PROMOTION_URL))
				|| url.contains(stringResource.getString(R.string.DETAIL_PAGE)) 
				|| url.contains(stringResource.getString(R.string.ACCESSORIES_ADD_TO_CART_PAGE))
				|| url.contains(stringResource.getString(R.string.VIEWWISHLIST_PAGE))
				|| url.contains(stringResource.getString(R.string.COMPARE_PAGE))
				|| url.contains(stringResource.getString(R.string.CREATEWISHLIST_PAGE)) ){
			
			header_logo=View.INVISIBLE;
			my_cart = View.INVISIBLE;
		}else if(url.contains(stringResource.getString(R.string.SIGNIN_BTN_SCHDELIVERY_PAGE)) 
				|| url.contains(stringResource.getString(R.string.CREATE_BILLING_ADDRESS_PAGE))
				|| url.contains(stringResource.getString(R.string.SIGNIN_BTN_SHIPPICUP_PAGE)) 
				|| url.contains(stringResource.getString(R.string.CONTINUE_BTN_REVIEW_PAGE))
				|| url.contains(stringResource.getString(R.string.SIGNIN_URL))){
			header_search=View.INVISIBLE;
			header_home=View.INVISIBLE;
			my_cart = View.INVISIBLE;
		}else if(url.contains(stringResource.getString(R.string.CHECKOUT_PAGE))){
			header_search=View.INVISIBLE;
			my_cart = View.INVISIBLE;
			header_home = View.INVISIBLE;
		}
		
		else if(url.contains(stringResource.getString(R.string.ACCESSORIES_URL)) || 
				 url.contains(stringResource.getString(R.string.GOOGLE_MAPS)) || 
				 url.contains(stringResource.getString(R.string.PROTECTION_PLANS)) || 
				 url.contains(stringResource.getString(R.string.CREATE_WISHLIST_URL))){
			header_home = View.INVISIBLE;
		}
		
		else if(url.contains(stringResource.getString(R.string.ORDER_URL)) || 
				url.contains(stringResource.getString(R.string.ORDER_STATUS))||
				url.contains(stringResource.getString(R.string.ORDER_DETAILS))
				){
			header_cart = View.INVISIBLE;
		}
		
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
		
		View cartBadge = ((Activity) context).findViewById(R.id.header_cart_badge);
		if( cartBadge!=null && cartBadge.getVisibility() == View.VISIBLE  && header_cart == View.INVISIBLE) {
			((Activity) context).findViewById(R.id.header_cart_badge).setVisibility(View.INVISIBLE);
			((Activity) context).findViewById(R.id.header_cart_badge_text).setVisibility(View.INVISIBLE);
		}
		if (cartBadge != null && cartBadge.getVisibility() == View.VISIBLE
				&& my_cart == View.VISIBLE) {
			((Activity) context).findViewById(R.id.header_cart_badge)
					.setVisibility(View.INVISIBLE);
			((Activity) context).findViewById(R.id.header_cart_badge_text)
					.setVisibility(View.INVISIBLE);
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

	public void getLoginCredential(final String html, final String html1, final int display){
		
		if (html !=null && html1 !=null && html.trim().length() != 0 && html1.trim().length() != 0) {
	    	email = html;
	    	password = html1;
	    	app.setEmail(email);
	    	app.setPassword(password);
		}
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
				
					if(what == HIDE_CART_BADGE){
						cartBadge.setVisibility(View.INVISIBLE);
						cartBadgeText.setVisibility(View.INVISIBLE);
					}else{
						if(what  == 1) {
							cartBadge.setVisibility(View.VISIBLE);
							cartBadgeText.setVisibility(View.VISIBLE);
						}
						if(cartQty > 9)
							cartBadge.setBackgroundResource(R.drawable.btn_cart_badge_oval);
						else
							cartBadge.setBackgroundResource(R.drawable.btn_cart_badge);
						cartBadgeText.setText(String.valueOf(cartQty));
					}
					
					
				}else{
					cartBadge.setVisibility(View.INVISIBLE);
					cartBadgeText.setVisibility(View.INVISIBLE);
				}
			}

		};
	};

	public static String getCurrentDisplayedUrl() {
		return currentDisplayedUrl;
	}
	
	public void refreshCurrentUrl() {
		if(mDotWebview != null){
			currentDisplayedUrl = mDotWebview.getUrl();
			if(currentDisplayedUrl != null)
				updateHeaderBar(currentDisplayedUrl,mContext);
		}
	}
	
	public String getSkuSearch() {
		return skuSearch;
	}

	public void setSkuSearch(String skuSearch) {
		this.skuSearch = skuSearch;
	}
	
	public boolean isNewWindow() {
		return isNewWindow;
	}

	public void setNewWindow(boolean isNewWindow) {
		this.isNewWindow = isNewWindow;
	}

	public void invoke(String arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	public void setLoadingDod(boolean isLoadingDod) {
	
		this.isLoadingDod=isLoadingDod;
	}
	
	public void checkBlankPDP(final String html,final int display){
		
		if(html != null && html.equals("\n")){
			AlertDialog dialog = new AlertDialog.Builder(mContext)
			.setTitle("Error")
			.setMessage("SKU " +getSkuSearch()+ " was not found!" )
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					mContext.finish();
				}
			}).create();
			dialog.show();
		}
	}
}
