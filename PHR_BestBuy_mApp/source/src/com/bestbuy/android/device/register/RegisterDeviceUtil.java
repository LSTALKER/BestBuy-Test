package com.bestbuy.android.device.register;

import java.util.HashMap;
import java.util.Map;

import com.bestbuy.android.pushnotifications.data.UpdatedTimeZone;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;

/**
 * device reg. with PN server.
 * 
 * 
 */
public class RegisterDeviceUtil {

	public static String getDevRegResponse(String optIn, String appIdFromUa,
			String deviceId) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceId);
		params.put("deviceType", "1");
		params.put("optIn", optIn);
		params.put("urbanAirShipId", appIdFromUa);
		params.put("apikey", AppConfig.getPushNotificationAPIKey());
		params.put("timeZone", UpdatedTimeZone.getTimeZoneName());
		String host = AppConfig.getPushNotificationAPIHost();
		String path = "registerDevice";
		try {
			return APIRequest.makeGetRequest(host, path, params, false);
		} catch (Exception e) {
			return null;
		}
	}
}