package com.bestbuy.android.appolicious.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * This class will check all possible net connectivity in your device. 
 * We can check the wifi availbilty, GPRS availabilty and
 * also all activated network info in the device. We can check the airplane mode of the device.
 * 
 * @author lalitkumar_s
 */
public class BaseConnectionManager {

	// To check whether WIFI is available on the device or not
	public static boolean isWifiAvailable(Context context) {
		
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) {
			NetworkInfo netInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
			if(netInfo != null)
				return netInfo.isConnected();
		}
		
		return false;
	}

	// To check whether mobile connection is present
	public static boolean isMobileConnectivityAvailable(Context context) {
		
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) {
			NetworkInfo netInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if(netInfo != null)
				return netInfo.isConnected();
		}
		
		return false;
	}

	// To check whether any connectivity is present at all
	public static boolean isNetAvailable(Context pContext) {
		
		ConnectivityManager connectivity = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			
			NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
			
			if(netInfo != null) {
				if (NetworkInfo.State.CONNECTED == netInfo.getState())
					return true;
			} 
		}

		return false;
	}
	
	// To check the airplane mode of the device
	public static boolean isAirplaneMode(Context pContext){
		return Settings.System.getInt(pContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	}

}
