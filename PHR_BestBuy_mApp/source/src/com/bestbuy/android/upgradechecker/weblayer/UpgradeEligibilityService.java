package com.bestbuy.android.upgradechecker.weblayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.upgradechecker.parser.UpgradeCheckerResponseParser;
import com.bestbuy.android.upgradechecker.util.UpgradeCheckerUtils;
import com.bestbuy.android.upgradechecker.web.block.WebAccessBlock;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class UpgradeEligibilityService {
	
	private String request = null;
	private BestBuyApplication application;	
	private Context context;
	
	public UpgradeEligibilityService(Context context) {
		this.context = context;
	}
	
	public BestBuyApplication getApplication() {
		return application;
	}

	public void setApplication(BestBuyApplication application) {
		this.application = application;
	}

	public HashMap call() throws MalformedURLException, IOException{
		HashMap results = new HashMap();
		UpgradeCheckerUtils upgradeCheckerUtils = new UpgradeCheckerUtils(context);
		request = upgradeCheckerUtils.buildSOAPRequest(1);
		HttpURLConnection urlConnection = null;
		Log.i("URL===========>", application.getUpgradeCheckerURL().toString());
		Log.i("Request===========>", this.request);
		urlConnection = WebAccessBlock.getHTTPURLConnection(application.getUpgradeCheckerURL(), this.request);
		try{
			int responseCode = urlConnection.getResponseCode();		
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String response = UpgradeCheckerUtils.inputStreamToString(in).toString();	
			Log.i("Response=======>", response);
			results = UpgradeCheckerResponseParser.parseEligibilityCheckResponse(response);
			
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
}

