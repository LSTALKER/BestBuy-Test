package com.bestbuy.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.bestbuy.android.R;

/**
 * Displays the terms of use for the application
 * @author Recursive Awesome
 *
 */
public class Terms extends MenuActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		findViewById(R.id.header_cart).setVisibility(View.GONE);
		findViewById(R.id.header_cart_badge).setVisibility(View.GONE); 
		String url = this.getIntent().getStringExtra("URL");
		if (url == null) {
			url = "file:///android_asset/legalterms.html";
		}
		WebView webView = (WebView)findViewById(R.id.web_view);
		webView.setWebViewClient(new BBYWebViewClient()); //set the webView client
		webView.loadUrl(url);
	}
	
	private class BBYWebViewClient extends WebViewClient {	   
		ProgressBar pb = (ProgressBar) findViewById(R.id.workingProgress);
		
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	if (url.startsWith("tel:") || url.startsWith("mailto:")) { // if it's a phone number, start new activity.
	    		Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(url)); 
	    		startActivity(intent); 
	    	} else {
	    		//Need to launched in browser and not inside application
	    	    Uri uriUrl = Uri.parse(url);
	    	    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl); 
	    	    startActivity(launchBrowser);  
	    	}
	    	return true;
	    }
	    
	    public void onPageFinished(WebView view, String url) {	    	
	    	pb.setVisibility(View.INVISIBLE);
	    }
	    
	    
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			pb.setVisibility(View.VISIBLE);
		}
	}
}
