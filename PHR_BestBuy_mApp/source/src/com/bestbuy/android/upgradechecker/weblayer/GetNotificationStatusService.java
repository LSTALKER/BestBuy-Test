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

public class GetNotificationStatusService {
	
	private String request = null;
	private Context context;
	
	public GetNotificationStatusService(Context context){
		this.context = context;
	}

	public HashMap call(){
		HashMap results = new HashMap();
		UpgradeCheckerUtils upgradeCheckerUtils = new UpgradeCheckerUtils(context);
		request = upgradeCheckerUtils.buildSOAPRequest(2);
		HttpURLConnection urlConnection = null;
		try{
			urlConnection = WebAccessBlock.getHTTPURLConnection(getApp().getUpgradeCheckerURL(), this.request);
			int responseCode = urlConnection.getResponseCode();		
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String response = UpgradeCheckerUtils.inputStreamToString(in).toString();			
			String status = UpgradeCheckerResponseParser.parseGetNotificationResponse(response);		
			
			if (responseCode == 200) {											
				results.put("error", "");
			} else {
				results.put("error", Integer.toString(responseCode));				
			}
			results.put("status", status);
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
