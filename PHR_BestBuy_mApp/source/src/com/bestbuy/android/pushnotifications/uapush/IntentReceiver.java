package com.bestbuy.android.pushnotifications.uapush;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.bestbuy.android.activity.Home;
import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.device.register.RegisterDevice;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.util.BBYLog;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

public class IntentReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)){
			doUpDateTimeZoneTask();
		}else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
			doUpDateTimeZoneTask();
		}else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
			doUpDateTimeZoneTask();
		} else if (action.equals(PushManager.ACTION_PUSH_RECEIVED)) {}
		else if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {
			showPushNotification(context,intent);
		} else if (action.equals(PushManager.ACTION_REGISTRATION_FINISHED)) {
			PushPreferences pushNotifPrefs = PushManager.shared().getPreferences();
			AppData.setUAid(pushNotifPrefs.getPushId());
			AppData.setapiKeyStatus(true);
			new RegisterDevice();
			BBYLog.v("Push ID", pushNotifPrefs.getPushId());
		}else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			BBYLog.v("BBY APP", "DEVICE BOOT COMPLETED");
			AppData.setOptinStatus(true);
			AppData.setRegwithPNStatus(false);
		}
	}
	private void doUpDateTimeZoneTask() {
		
		new AsyncTask<String, Integer, Boolean>(){
			
			private String resultString;
			@Override
			protected Boolean doInBackground(String... params) {
				
			resultString=PushNotifcationData.sendUpdatedUserTimeZone();
			if (resultString!=null) {
				return true;
			}	
			return false;
			}
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				if (result) {

					try {
						JSONObject jsonObject = new JSONObject(resultString);
						String statusCode = jsonObject.getString("code");
						if (statusCode.equals("200")) {
						} else {
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
				}
			}
		}.execute();
		
		
	}
	/*private void logPushExtras(Intent intent) {
        Set<String> keys = intent.getExtras().keySet();
        for (String key : keys) {

            //ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>)Arrays.asList(
                    "collapse_key",//c2dm collapse key
                    "from",//c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                    PushManager.EXTRA_PUSH_ID,//internal UA push id
                    PushManager.EXTRA_ALERT);//ignore alert
            if (ignoredKeys.contains(key)) {
                continue;
            }
            String extraContentUA = intent.getStringExtra(key);
            BBYLog.v("Key", key);
            BBYLog.v("Value", extraContentUA);
            Log.v("logTag", "Push Notification Extra: ["+key+" : " + intent.getStringExtra(key) + "]");
            Log.v("PushManager.EXTRA_ALERT==>", intent.getStringExtra(PushManager.EXTRA_ALERT));
            Log.v("EXTRA==============>", intent.getStringExtra(key));
            Log.v("extraContentUA",extraContentUA);
            
        }
	}*/
	private void showPushNotification(Context context, Intent intent) {
		String keyName="keyname";
		String valueOfKeyName = null;
		Set<String> keys = intent.getExtras().keySet();
        for (String extraKey : keys) {
            //ignore standard C2DM extra keys
            List<String> ignoredKeys = (List<String>)Arrays.asList(
                    "collapse_key",//c2dm collapse key
                    "from",//c2dm sender
                    PushManager.EXTRA_NOTIFICATION_ID,//int id of generated notification (ACTION_PUSH_RECEIVED only)
                    PushManager.EXTRA_PUSH_ID,//internal UA push id
                    PushManager.EXTRA_ALERT);//ignore alert
            if (ignoredKeys.contains(extraKey)) {
                continue;
            }
            keyName=extraKey;
            valueOfKeyName= intent.getStringExtra(extraKey);
         }
		if (keyName.equals("RZ")) {
			//TODO 
		} else if (keyName.equals("WO")) {
			showHomePage(context);
		}else if(keyName.equals("DOD")){
			showWebPage(context,valueOfKeyName);
		}
		else {
		}
				
	}
	private void showWebPage(Context context,String mDotUrl) {
			if(mDotUrl!=null){
				
				Intent intentPn = new Intent(context, MDOTProductDetail.class);
				intentPn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentPn.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intentPn.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
				intentPn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intentPn.putExtra("mDotURL",mDotUrl);
				intentPn.putExtra("fromIR", true);
				context.startActivity(intentPn);	
			}
		
	}
	private void showHomePage(Context context) {
		
		Intent intentPn = new Intent(context, Home.class);
		intentPn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentPn.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intentPn.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		intentPn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intentPn.putExtra("fromIR", "pushnotification");
		AppData.setPNtrayopen(true);
		context.startActivity(intentPn);
		
	}
}