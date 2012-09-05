package com.bestbuy.android.device.register;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.pushnotifications.logic.PushNotificationsLogic;
import com.bestbuy.android.util.AppConfig;
/**
 * This class is to register Device with PN server.
 * if user selected optin(Home Screen) ==>new instance will be called.
 * if application received appId from UA(IntentReceiver) ==>new instance will be called.
 *  So,
 *  if have optin,appid and user Did not reg with PN serever,the new task will perform to check server status 
 *  after LoadDevRegTask should perform
 *  
 *  
 */
public class RegisterDevice {

	private String optIn;
	private String appId;

	public RegisterDevice() {
		optIn = AppData.getOptin();
		appId = AppData.getUAid();
		if(AppData.getOptinStatus() && AppData.getapiKeyStatus() && !AppData.getRegwithPNStatus()){
			new PnServerStatusAsynTask().execute();
			
		}
	}

	
	public class PnServerStatusAsynTask extends AsyncTask<String,Integer, Boolean> {
		String responseString = null;
		@Override
		protected Boolean doInBackground(String... params) {
			responseString = PushNotificationsLogic.sendResponsePNServer();
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (responseString != null) {
				String status=getResponse(responseString);
				if(status.equals("true")){
					if(AppConfig.getHashedDeviceId()!=null)
						new LoadDevRegTask().execute();
				}
			} 
		}

	}
	
	private class LoadDevRegTask extends AsyncTask<String, Integer, Boolean> {
		private String response;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Boolean doInBackground(String... params) {
			response=RegisterDeviceUtil.getDevRegResponse(optIn,appId,AppConfig.getHashedDeviceId());
		   return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(response!=null){
				String status=getResponse(response);
				if(status.equals(PushNotifcationData.STATUS_SUCCESS)){
					setRegistrationStatus(false,false,true);
					AppData.getSharedPreferences().edit().putBoolean(AppData.OPTIN_STATE, true);
				}else{
					setRegistrationStatus(true,true,false);
				}				
			}else{
				setRegistrationStatus(true,true,false);
			}
		}
	}

	public void setRegistrationStatus(boolean b, boolean b1, boolean b2) {
		AppData.setOptinStatus(b);
		AppData.setapiKeyStatus(b1);
		AppData.setRegwithPNStatus(b2);		
	}

	public String getResponse(String responseString) {
		try {
			JSONObject jsonObject = new JSONObject(responseString);
			if (jsonObject.has(PushNotifcationData.STATUS)) {
				return jsonObject.getString(PushNotifcationData.STATUS);
			}			
		} catch (JSONException e) {
			return PushNotifcationData.STATUS_FALSE;
		}
		return PushNotifcationData.STATUS_FALSE;
	}
}