package com.bestbuy.android.upgradechecker.weblayer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

import android.content.Context;

import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.upgradechecker.parser.UpgradeCheckerResponseParser;
import com.bestbuy.android.upgradechecker.util.UpgradeCheckerUtils;
import com.bestbuy.android.upgradechecker.web.block.WebAccessBlock;

public class PutNotificationService {

	private String notificationStatusCode=null;
	private String request = null;
	private Context context;

	public PutNotificationService(Context context, String notificationStatusCode){
		this.notificationStatusCode=notificationStatusCode;
		this.context = context;
	}
	
	public HashMap call(){
		HashMap results = new HashMap();
		UpgradeCheckerUtils upgradeCheckerUtils = new UpgradeCheckerUtils(context);
		upgradeCheckerUtils.setNotificationStatusCode(notificationStatusCode);
		request = upgradeCheckerUtils.buildSOAPRequest(3);
		HttpURLConnection urlConnection = null;
		try{
			urlConnection = WebAccessBlock.getHTTPURLConnection(getApp().getUpgradeCheckerURL(), this.request);
			int responseCode = urlConnection.getResponseCode();		
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String response = UpgradeCheckerUtils.inputStreamToString(in).toString();			
			results = UpgradeCheckerResponseParser.parsePutNotificationResponse(response);		
			
			if (responseCode == 200) {											
				results.put("error", "");
			} else {
				results.put("error", Integer.toString(responseCode));				
			}
		}
		catch(Exception e){
			results.put("error", e.getMessage());			
		} finally {
			if(urlConnection != null){
				urlConnection.disconnect();
			}
		}
		return results;
	}
	
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) context.getApplicationContext();
	}
}
