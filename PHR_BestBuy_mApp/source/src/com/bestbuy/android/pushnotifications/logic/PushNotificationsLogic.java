package com.bestbuy.android.pushnotifications.logic;

import java.util.List;

import com.bestbuy.android.pushnotifications.data.PushNotifcationData;

public class PushNotificationsLogic {
	
	public static String sendOffersNotificationConfig(List<String> selectedCategoryIds){
		return PushNotifcationData.sendOffersNotificationConfig(selectedCategoryIds);
	}
	
	public static String sendRZNotificationConfig(String rz_id){
		return PushNotifcationData.sendRZNotificationConfig(rz_id);
	}
	
	public static String sendResponsePNServer(){
		return PushNotifcationData.sendResponsePNServer();
	}
}