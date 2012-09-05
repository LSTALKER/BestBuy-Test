package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.pushnotifications.activity.PnRewardZoneActivity;
import com.bestbuy.android.ui.BBYProgressDialog;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;

public class RewardZoneLogin extends MenuActivity {

	private static final int TOKEN_ATTEMPT_LIMIT = 3;
	private String TAG = this.getClass().getName();
	private String _requestToken;
	private int OAUTH_DIALOG = 0;
	private int LOADING_DIALOG = 1;
	
	private OAuthAccessor _oAuthAccessor;
	private OAuthClient _oClient;
	private int _tokenAttemptCount = 0;

	private WebView _webView;
	private String failedUrl;
	private String loginUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rz_login);
		
		_webView = (WebView) findViewById(R.id.reward_zone_login_web_view);
		_webView.setWebViewClient(new RewardZoneClient());
		_webView.addJavascriptInterface(new RewardZoneJavaScriptInterface(), "HTMLOUT");
		_webView.getSettings().setJavaScriptEnabled(true);
		
		showDialog(OAUTH_DIALOG);
		new OAuthTask(this).execute();
	}

	private class OAuthTask extends BBYAsyncTask{
		public OAuthTask(Activity activity){
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doTask() throws Exception {
			_oAuthAccessor = AppData.createOAuthAccessor();
			_oClient = new OAuthClient(new HttpClient4());
			_oClient.getRequestToken(_oAuthAccessor, OAuthMessage.POST);
			_requestToken = _oAuthAccessor.requestToken; 
		}
		
		@Override
		public void doFinish() {
			if (_requestToken != null) {
				loginUrl = getResources().getString(R.string.rz_login_url)+"?oauth_token=" + _requestToken;
				BBYLog.i(TAG, "URL + TOKEN: " + _requestToken);
				_webView.loadUrl(loginUrl);
			}
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				removeDialog(OAUTH_DIALOG);
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						showDialog(OAUTH_DIALOG);
						new OAuthTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else if(isError) {
				BBYLog.i(TAG, "Request Token Failed To Load, Trying Again.");
				_tokenAttemptCount++;
				
				if (_tokenAttemptCount >= TOKEN_ATTEMPT_LIMIT) {
					removeDialog(OAUTH_DIALOG);
					Toast.makeText(activity, "Reward zone servers are unresponsive", Toast.LENGTH_LONG).show();
					finish();
				} else {
					takeBreak(100);
					new OAuthTask(activity).execute();
				}
			}
		}
	}

	private void sendToRewardZone() {
		/*if (!isFinishing()) {
			finish();
			Intent i = new Intent(RewardZoneLogin.this, RewardZone.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}*/
		
		if (!isFinishing()) {
			Intent i = null;
			Intent intent = getIntent();
			if(intent.hasExtra("Activity")){
				if(intent.getStringExtra("Activity").equals("PushNotificationGetRewardZone")){
					i = new Intent(_context, PnRewardZoneActivity.class);
					i.putExtra("status","true");
				}
			}
			else
				i = new Intent(_context, RewardZone.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}
	}

	private class RewardZoneJavaScriptInterface {
		@SuppressWarnings("unused")
		public void showHTML(String html) {
			if (html.contains("oauth_verifier")) {
				int start = html.indexOf("\"oauth_verifier\"");
				String sub = html.substring(start, start + 40);
				String oAuthTokenVerifier = sub.substring(sub.indexOf(">") + 1, sub.indexOf("<"));

				_oAuthAccessor.requestToken = _requestToken;

				ArrayList<Map.Entry<String, String>> params = new ArrayList<Map.Entry<String, String>>();
				params.add(new OAuth.Parameter("oauth_verifier", oAuthTokenVerifier));
				if (!isFinishing()) {
					try {
						// get the access token
						_oClient.getAccessToken(_oAuthAccessor, OAuthMessage.POST, params);
						appData.setOAuthAccessor(_oAuthAccessor);
						appData.getNotificationManager().setLastNotificationRetrievedDate(null);
						sendToRewardZone();

					} catch (Exception ex) {
						BBYLog.printStackTrace(TAG, ex);
						NoConnectivityExtension.noConnectivity(RewardZoneLogin.this);
					}
					removeDialog(LOADING_DIALOG);
				}
			}

		}
	}

	private class RewardZoneClient extends WebViewClient {
		
		@Override
		public void onLoadResource(WebView view, String url) {

			super.onLoadResource(view, url);

			BBYLog.i(TAG, "Resource: " + view.getUrl());

		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showDialog(LOADING_DIALOG);
		}

		public void onPageFinished(WebView view, String url) {
			// This call inject JavaScript into the page which just finished
			// loading to get the content
			removeDialog(OAUTH_DIALOG);
			removeDialog(LOADING_DIALOG);
			
			if (url.contains(getResources().getString(R.string.rz_login_url)+"/sessions/me/authorize?oauth_token=")) {
				view.setVisibility(View.INVISIBLE);
				showDialog(LOADING_DIALOG);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			view.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
		}

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//if (url.contains("/sessions/me") || url.contains("passwordmemberinfo")) {
				view.loadUrl(url); // keep them in the app.
			//}
			return true;
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			failedUrl = failingUrl;
			
			if(errorCode == -6) {
				NoConnectivityExtension.noConnectivity(RewardZoneLogin.this, new OnReconnect() {
					public void onReconnect() {
						showDialog(OAUTH_DIALOG);
						if(failedUrl.equals(getResources().getString(R.string.rz_login_url)+"/sessions")) {
							if(!BaseConnectionManager.isNetAvailable(RewardZoneLogin.this) || BaseConnectionManager.isAirplaneMode(RewardZoneLogin.this)) {
								_webView.loadUrl(failedUrl);
							} else {
								_webView.loadUrl(loginUrl);
							}
						} else {
							_webView.loadUrl(failedUrl);
						}
					}		
				}, new NoConnectivityExtension.OnCancel() {
					public void onCancel() {
						finish();
					}
				});
			}
		}
	}

	private void takeBreak(int milli) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			BBYLog.printStackTrace(TAG, e);
		}
	}

	//handle back button inside the webview
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (_webView.canGoBack() && !_webView.getUrl().contains(getResources().getString(R.string.rz_login_url)+"/?oauth_token=")) {
				_webView.goBack();
				return true;
			} else {
				RewardZoneLogin.this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case 0:
			return new BBYProgressDialog(this, "Initializing Reward Zone...");
		case 1:
			return new BBYProgressDialog(this, "Loading...");
		default:
			return null;
		}
	}
}
