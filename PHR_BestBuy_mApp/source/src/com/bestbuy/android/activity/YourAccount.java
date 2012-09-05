package com.bestbuy.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.commerce.CCommerce;
import com.bestbuy.android.device.register.RegisterDeviceUtil;
import com.bestbuy.android.pushnotifications.activity.PushNotificationsActivity;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.pushnotifications.logic.PushNotificationsLogic;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.MdotWebView;

public class YourAccount extends MenuActivity {
	final String TAG = this.getClass().getName();
	private final String REMOVE_SEARCH_BAR = "RemoveSearchBar";
	/*private static boolean launchOrderStatus = false;
	private static boolean launchEditShipping = false;
	private static boolean launchEditPayment = false;*/
	//private CCommerce commerce;
	public static String emailAddress = null;
	public static String password = null;
	//private boolean firstAttempt = true;
/*	private boolean isInvalid = false;
	private BestBuyApplication application ;*/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	/*	application = (BestBuyApplication) this.getApplication();
		if(appData.getCommerce().getUserLink() == null){
			commerce = new CCommerce();
			appData.setCommerce(commerce);
		}
		else
			commerce = appData.getCommerce();*/
	}
	
	public void onResume(){
		super.onResume();
		/*if(appData.getCommerce().getUserLink() == null){
			if(emailAddress != null && password != null){
				new CommerceLoginTask(this, "loading...", false).execute();
			}
    	}*/
		showView();
	}
	
/*	class OrderStatusTask extends BBYAsyncTask {
		public OrderStatusTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			Intent i = new Intent(activity, CommerceOrderStatusLookup.class);
			startActivity(i);
		}
		
		@Override
		public void doError() {
			Intent i = new Intent(activity, CommerceSignIn.class);
			i.putExtra("OrderStatus", true);
			i.putExtra("HideGuest", true);
			startActivity(i);
		}

		@Override
		public void doTask() throws Exception {
			CCommerce commerce = appData.getCommerce();
			APIRequest.makeGetRequest(commerce.getUserLink(), null, null, true);
		}
	}
	
	class EditShippingTask extends BBYAsyncTask {
		public EditShippingTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			Intent i = new Intent(activity, CommerceShippingAddress.class);
			i.putExtra("EditOnly", true);
			startActivity(i);
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new EditShippingTask(activity).execute();
					}		
				}, new OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				Intent i = new Intent(activity, CommerceSignIn.class);
				i.putExtra("EditShipping", true);
				i.putExtra("HideGuest", true);
				startActivity(i);
			}
		}

		@Override
		public void doTask() throws Exception {
			CCommerce commerce = appData.getCommerce();
			APIRequest.makeGetRequest(commerce.getUserLink(), null, null, true);
		}
	}
	
	class EditPaymentTask extends BBYAsyncTask {
		public EditPaymentTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			Intent i = new Intent(activity, CommercePaymentInformation.class);
			i.putExtra("EditOnly", true);
			startActivity(i);
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new EditPaymentTask(activity).execute();
					}		
				}, new OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				Intent i = new Intent(activity, CommerceSignIn.class);
				i.putExtra("EditPayment", true);
				i.putExtra("HideGuest", true);
				startActivity(i);
			}
		}

		@Override
		public void doTask() throws Exception {
			CCommerce commerce = appData.getCommerce();
			APIRequest.makeGetRequest(commerce.getUserLink(), null, null, true);
		}
	}
	
	class LogOutTask extends BBYAsyncTask {
		public LogOutTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			//TODO do something
		}
		
		@Override
		public void doError() {
			//TODO do the same thing?
		}

		@Override
		public void doTask() throws Exception {
			//TODO log out
		}
	}*/
	
	/*class CommerceLoginTask extends BBYAsyncTask {
		public CommerceLoginTask(Activity activity, String loadingMessage, boolean cancelable) {
			super(activity,loadingMessage, cancelable);
		}
		
		@Override
		public void doFinish() {
			Diagnostics.StopMethodTracing(YourAccount.this, TAG, "Signing in took: ");
			
			if(appData.getCommerce().getUserLink() != null){
				appData.updateCartItemCount(appData.getCommerce());
			}
			appData.getCommerce().setEmailAddress(emailAddress);
			appData.getCommerce().setPassword(password);
			emailAddress = null;
			password = null;
			if(launchOrderStatus){
				Intent intent = new Intent(YourAccount.this, MDOTProductDetail.class);
				intent.putExtra(MDOT_URL, MdotWebView.getUrl(YourAccount.this, MdotWebView.VIEW_ORDER_URL));
				intent.putExtra(REMOVE_SEARCH_BAR, true);
				startActivity(intent);
			}
			else if(launchEditPayment){
				Intent i = new Intent(activity, CommercePaymentInformation.class);
				i.putExtra("EditOnly", true);
				startActivity(i);
			}
			else if(launchEditShipping){
				Intent i = new Intent(activity, CommerceShippingAddress.class);
				i.putExtra("EditOnly", true);
				startActivity(i);
			}
		}
		
		@Override
		public void doError() {
			emailAddress = null;
			password = null;
			commerceRelogin();
		}

		@Override
		public void doTask() throws Exception {
			Diagnostics.StartMethodTracing(YourAccount.this);
			try {
				commerce.parseBootstrap();
				String capiErrorsURL = AppData.getGlobalConfig().get("capiErrors2URL");
				String errorsJSON = APIRequest.makeGetRequest(capiErrorsURL, null, null, false);
				CErrorCodesHelper.loadErrorCodes(errorsJSON);

				commerce.login(emailAddress, password);
				commerce.getUser().parse(commerce.getUserLink());
				commerce.getOrder().parse(commerce.getOpenOrderLink());
			} catch (APIRequestException apiEx) {
				BBYLog.printStackTrace(TAG, apiEx);
				if (apiEx.getErrors() == null) {
					if (apiEx.getResponseBody().contains("Bad credentials")) {
					//	isInvalid = true;
					}
				} else {
					isError = true;
				}
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				isError = true;
			}
		}
	}
	
	class UpdateCommerceDetailsTask extends BBYAsyncTask{

		public UpdateCommerceDetailsTask(Activity activity, String loadingMessage, boolean cancelable) {
			super(activity, loadingMessage, cancelable);
		}

		@Override
		public void doTask() throws Exception {
			isInvalid= false;
			Diagnostics.StartMethodTracing(YourAccount.this);
			try {
				commerce.getUser().parse(commerce.getUserLink());
				commerce.getOrder().parse(commerce.getOpenOrderLink());
			} catch (APIRequestException apiEx) {
				BBYLog.printStackTrace(TAG, apiEx);
				isError = true;
				if (apiEx.getErrors() == null) {
					if (apiEx.getResponseBody().contains("Bad credentials")) {
						isInvalid = true;
					}
					else if (apiEx.getResponseBody().contains("Invalid user session")) {
						isInvalid = true;
					}
				} else {
					isError = true;
				}
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				isError = true;
			}
		}

		@Override
		public void doFinish() {
			if(isInvalid){
				emailAddress = appData.getCommerce().getEmailAddress();
				password = appData.getCommerce().getPassword();
				new CommerceLoginTask(YourAccount.this, "loading...", false).execute();
			}
			else{
				emailAddress = null;
				password = null;
				if(launchOrderStatus){
					Intent intent = new Intent(YourAccount.this, MDOTProductDetail.class);
					intent.putExtra(MDOT_URL, MdotWebView.getUrl(YourAccount.this, MdotWebView.VIEW_ORDER_URL));
					intent.putExtra(REMOVE_SEARCH_BAR, true);
					startActivity(intent);
				}
				else if(launchEditPayment){
					Intent i = new Intent(activity, CommercePaymentInformation.class);
					i.putExtra("EditOnly", true);
					startActivity(i);
				}
				else if(launchEditShipping){
					Intent i = new Intent(activity, CommerceShippingAddress.class);
					i.putExtra("EditOnly", true);
					startActivity(i);
				}
			}
		}
		
		@Override
		public void doError() {
			emailAddress = appData.getCommerce().getEmailAddress();
			password = appData.getCommerce().getPassword();
			new CommerceLoginTask(YourAccount.this, "loading...", false).execute();
		}
	}*/
	
	public void showView() {
		setContentView(R.layout.your_account);
		//String rzEmailAddress = AppData.getSharedPreferences().getString(AppData.COMMERCE_USERNAME, null);
		//String rzNumber = null;
		//if (appData.getRzAccount() != null) {
		//	rzNumber = appData.getRzAccount().getNumber();
		//}
//		if (rzNumber == null && rzEmailAddress == null) {
//			findViewById(R.id.your_account_personalize_recommendations).setVisibility(View.GONE);
//			findViewById(R.id.your_account_personalize_recommendations_divider).setVisibility(View.GONE);
//		} else {
//			findViewById(R.id.your_account_personalize_recommendations).setVisibility(View.VISIBLE);
//			findViewById(R.id.your_account_personalize_recommendations_divider).setVisibility(View.VISIBLE);
//		}
		
		CCommerce commerce = appData.getCommerce();
		boolean isLogedIn = false;
		if (commerce.getUserLink() != null) {
			isLogedIn = true;
		}
		
		if(isLogedIn){ //TODO is there a condition here?			
    		Button logoutButton = (Button)findViewById(R.id.header_done); 
    		logoutButton.setText("Log Out");
    		logoutButton.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				//new LogOutTask(YourAccount.this).execute();
    			}
    		});
		}		
		findViewById(R.id.your_account_order_status).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(YourAccount.this, MDOTProductDetail.class);
				intent.putExtra(MDOT_URL, MdotWebView.getUrl(YourAccount.this, MdotWebView.VIEW_ORDER_URL));
				intent.putExtra(REMOVE_SEARCH_BAR, true);
				startActivity(intent);
			}
		});
		
		/*findViewById(R.id.your_account_edit_shipping).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//new EditShippingTask(YourAccount.this).execute();
				CCommerce commerce = appData.getCommerce();
				if(commerce.getUserLink() == null){
					emailAddress = application.getEmail();
					password = application.getPassword();
					if(emailAddress != null && password != null){
						launchEditShipping = true;
						new CommerceLoginTask(YourAccount.this, "loading...", false).execute();
					}
					else{
						launchEditShipping = true;
						Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
						i.putExtra(MDOT_URL, getSignInURL());
						i.putExtra("IsFromMyAccountPage", true);
						startActivity(i);
					}
				}
				else{
					resetValues();
					launchEditShipping = true;
					new UpdateCommerceDetailsTask(YourAccount.this,"loading...", false).execute();
					/*Intent i = new Intent(YourAccount.this, CommerceShippingAddress.class);
					i.putExtra("EditOnly", true);
					startActivity(i);*/
				/*}
			}
		});*/
		
		/*findViewById(R.id.your_account_edit_payment).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CCommerce commerce = appData.getCommerce();
				if(commerce.getUserLink() == null){
					launchEditPayment = true;
					emailAddress = application.getEmail();
					password = application.getPassword();
					if(emailAddress != null && password != null){
						launchEditPayment = true;
						new CommerceLoginTask(YourAccount.this, "loading...", false).execute();
					}
					else{
						Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
						i.putExtra(MDOT_URL, getSignInURL());
						i.putExtra("IsFromMyAccountPage", true);
						startActivity(i);
					}
				}
				else{
					resetValues();
					launchEditPayment = true;
					new UpdateCommerceDetailsTask(YourAccount.this,"loading...", false).execute();
					/*Intent i = new Intent(YourAccount.this, CommercePaymentInformation.class);
					i.putExtra("EditOnly", true);
					startActivity(i);*/
				/*}
				//new EditPaymentTask(YourAccount.this).execute();
			}
		});*/
		
		/*findViewById(R.id.your_account_preferred_categories).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), PreferredCategories.class);
				startActivity(i);
			}
		});*/
		
		findViewById(R.id.your_account_feedback).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), Feedback.class);
				startActivity(i);
			}
		});

		findViewById(R.id.your_account_terms).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), Terms.class);
				i.putExtra("url", "file:///android_asset/legalterms.html");
				startActivity(i);
			}
		});
		
		findViewById(R.id.your_account_manage_pushnotifications).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			new PnServerStatusAsynTask(YourAccount.this).execute();
			}
		});
		
		//Determine whether or not to show the recommendations row
//		String emailAddress = AppData.getSharedPreferences().getString(AppData.COMMERCE_USERNAME, "");
//		if (appData.getRzAccount() == null && appData.getRzAccount().getNumber() == null && emailAddress.length() == 0) {
//			findViewById(R.id.home_view_product_recommendations).setVisibility(View.GONE);
//		}
//		
//		findViewById(R.id.your_account_personalize_recommendations).setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				SharedPreferences prefs = AppData.getSharedPreferences();
//				Boolean showRecommendationsInstructions = prefs.getBoolean("showRecommendationsInstructions", true);
//				if (showRecommendationsInstructions) {
//					Intent i = new Intent(v.getContext(), RecommendationsInterstitial.class);
//					startActivity(i);
//				} else {
//					Intent i = new Intent(v.getContext(), Recommendations.class);
//					startActivity(i);
//				}
//			}
//		});
	}
	
	/*private String getSignInURL(){
		String url = null;
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(AppConfig.getMdotURL()).append(getString(R.string.SIGNIN_URL));
		url = urlBuffer.toString();
		return url;
	}*/
	
	/*private void resetValues(){
		launchOrderStatus = false;
		launchEditShipping = false;
		launchEditPayment = false;
	}
	
	private void commerceRelogin(){
		CookieSyncManager.createInstance(this); 
	    CookieManager cookieManager = CookieManager.getInstance();
	    cookieManager.removeAllCookie();
	    AppData.getSharedPreferences().edit().putInt(AppData.CART_QTY, 0).commit();
		commerce = new CCommerce();
		appData.setCommerce(commerce);
		launchEditPayment = true;
		Intent i = new Intent(YourAccount.this, MDOTProductDetail.class);
		i.putExtra(MDOT_URL, getSignInURL());
		i.putExtra("IsFromMyAccountPage", true);
		startActivity(i);
	}*/
	
	private class PnServerStatusAsynTask extends BBYAsyncTask {

		String response = null;

		public PnServerStatusAsynTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doTask() throws Exception {
			response = PushNotificationsLogic.sendResponsePNServer();
		}

		@Override
		public void doFinish() {

			JSONObject jsonObject;
			if (response != null) {
				try {
					jsonObject = new JSONObject(response);
					if (jsonObject.has("status")) {
						String status = jsonObject.getString("status");
						if (status.equals("true")) {

							if (!AppData.getRegwithPNStatus()) {
								new RegisterDevicetask(YourAccount.this)
										.execute();
							} else {
								Intent i = new Intent(getApplicationContext(),
										PushNotificationsActivity.class);
								startActivity(i);
							}

						} else {
							doError(); 
						}
					}
				} catch (JSONException e) {}
			} else {
				doError();
			}
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(_context, new OnReconnect() {
				
				public void onReconnect() {
					new PnServerStatusAsynTask(YourAccount.this).execute();
					
				}
			}, new OnCancel() {
				
				public void onCancel() {
					finish();
					
				}
			});	
		}
	}
	
	class RegisterDevicetask extends BBYAsyncTask{
		String response;
	public RegisterDevicetask(Activity activity) {
		super(activity);
		
	}

	@Override
	public void doTask() throws Exception {
		response=RegisterDeviceUtil.getDevRegResponse(AppData.getOptin(),AppData.getUAid(),AppConfig.getHashedDeviceId());
		
	}
	@Override
	public void doError() {			
		NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
			public void onReconnect() {
				new PnServerStatusAsynTask(YourAccount.this).execute();
			}		
		});
	}
	@Override
	public void doFinish() {
		if(response!=null){
			String status=getResponseStatus(response);
			if(status.equals(PushNotifcationData.STATUS_SUCCESS)){
				setRegistrationStatus(false,false,true);
				AppData.getSharedPreferences().edit().putBoolean(AppData.OPTIN_STATE, true);
				Intent i = new Intent(getApplicationContext(),PushNotificationsActivity.class);
				startActivity(i);
			}else{
				doError();
				setRegistrationStatus(true,true,false);
			}				
		}else{
			setRegistrationStatus(true,true,false);
			doError();			
		}
		
		
	}
	
	public String getResponseStatus(String responseString) {
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			if (jsonObject.has(PushNotifcationData.STATUS)) {
				return jsonObject.getString(PushNotifcationData.STATUS);
			}			
		} catch (JSONException e) {
		}
		return PushNotifcationData.STATUS_FALSE;
	}
	
	public void setRegistrationStatus(boolean b, boolean b1, boolean b2) {
		AppData.setOptinStatus(b);
		AppData.setapiKeyStatus(b1);
		AppData.setRegwithPNStatus(b2);		
	}
}
}


