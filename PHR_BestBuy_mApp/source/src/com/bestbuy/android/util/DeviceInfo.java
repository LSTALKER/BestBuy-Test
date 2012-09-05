package com.bestbuy.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.bestbuy.android.data.BestBuyApplication;

public class DeviceInfo {

	private static final String TAG = "DeviceInfo.java";
	
	
	public static String uuid(Context context)
	{
		TelephonyManager mTelephonyMgr = 
	        (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getDeviceId();
	}
	
	public static String device()
	{
		return Build.MODEL;
	}
	
	public static String platform()
	{
		return "Android-" + android.os.Build.VERSION.RELEASE; 
	}
	
	public static String carrier(Context context)
	{
		return getCarrier(context); 
	}
	
	public static String appName(Context context)
	{
		return "BestBuy-Version" + getVersionName(context, BestBuyApplication.class);
	}
	

	private static String getVersionName(Context context, Class<?> cls) 
	{
	  try {
	    ComponentName comp = new ComponentName(context, cls);
	    PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
	    return pinfo.versionName;
	  } catch (android.content.pm.PackageManager.NameNotFoundException e) {
		  BBYLog.printStackTrace(TAG, e);
		  return "Version 1.0";
	  }
	}
	
	private static String getCarrier(Context context) {
		String apn = "";
		Cursor mCursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers"), 
				new String[] {"name"}, "current=1", null, null);
		if (mCursor!=null) {
			try {
				if (mCursor.moveToFirst()) {
					 apn = mCursor.getString(0);
				}
			} finally {
				mCursor.close();
			}
		}
		return apn;
	}
}
