package com.bestbuy.android.upgradechecker.web.block;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.bestbuy.android.util.AppConfig;

public class WebAccessBlock {
	
	public static HttpURLConnection getHTTPURLConnection(URL url, String SOAPrequest) throws IOException{
		HttpURLConnection urlConnection = null;
		
		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setFixedLengthStreamingMode(SOAPrequest.length());
		urlConnection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
		urlConnection.setRequestProperty("apiKey", AppConfig.getUgradeCheckerApiKey());
		urlConnection.setReadTimeout(2000);
		int readTimeOut = urlConnection.getReadTimeout();
		Log.i("timeOut=========>", ""+readTimeOut);
		urlConnection.connect();			
		DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
		out.writeBytes(SOAPrequest);
		out.close();
		return urlConnection;
	}
}
