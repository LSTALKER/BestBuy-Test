package com.bestbuy.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bestbuy.android.R;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.JSHelper;



public class BBYCode extends MenuActivity {
	@SuppressWarnings("unused")
	private final int LOADING_DIALOG = 0;
	private final String TAG = this.getClass().getName();
	private String url = "";
	private WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.code_scan_web_view);
		webView = (WebView) findViewById(R.id.web_view);
		webView.setWebViewClient(new BBYCodeClient()); // set the webView client
		webView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(BBYCode.this)
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
			}
			    
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if(newProgress <70) {
					view.loadUrl(JSHelper.getScript(R.string.main_css, view.getContext()));
				} 
			}
			
		}); 

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);

		if (this.getIntent().hasExtra("url")) {
			url = this.getIntent().getExtras().getString("url");
			BBYLog.d(TAG, "Url coming in: " + url);
			if (url.contains("youtube.com") && url.contains("watch?")) {
				String videoId = url.substring(url.indexOf("v=") + 2, url.indexOf("v=") + 13);
				String videoUrl = "vnd.youtube:" + videoId;
				BBYLog.d(TAG, "video url: " + videoUrl);
				Uri uri = Uri.parse(videoUrl);
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(i);
				this.finish();
			} else {
				webView.loadUrl(url);
			}
		}
	}

	private class BBYCodeClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// YouTube video link
			if (url.startsWith("vnd.youtube:")) {
				int n = url.indexOf("?");
				if (n > 0) {
					Uri videoUri = Uri.parse(String.format("http://www.youtube.com/v/%s", url.substring("vnd.youtube:".length(), n)));
					startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
				}
				return (false);
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			removeDialog(0);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			showDialog(0);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (this == null || this.isFinishing()) {
			return null;
		}
		
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("Loading...");
		
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (webView.canGoBack() == true) {
					webView.goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
}
