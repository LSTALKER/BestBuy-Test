package com.bestbuy.android.upgradechecker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.util.AppConfig;

public class UpgradeCheckerUtils {
	
	private String userId = null;
	private String language = null;
	private String notificationStatusCode;
	private Context context;
	
	private static final int ELIGIBILITY_CHECK_REQUEST = 1;
	private static final int GET_NOTIFICATION_REQUEST = 2;
	private static final int PUT_NOTIFICATION_REQUEST = 3;
	public static final int AT_AND_T_CLEAR_PH_NO = 4;
	public static final int SPRINT_CLEAR_PH_NO = 5;
	public static final int T_MOBILE_CLEAR_PH_NO = 6;
	public static final int VERIZON_CLEAR_PH_NO = 7;

	
	private String body ="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" 
			+"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ucs=\"http://bestbuy.com/bbym/ucs\">\n"			
			+ "<soap:Body>";
			
	private String eligiblityCheckOpenXML = "<ucs:getUpgradeEligibility>" + "<upgradeEligibilityRequest>";
	private String putNotificationOpenXML = "<ucs:putNotificationStatus><notificationStatusPutRequest>";
	private String getNotificationOpenXML = "<ucs:getNotificationStatus><notificationStatusGetRequest>";
	
	
	private String mobilePhoneNumberXML = "<mobilePhoneNumber>%s</mobilePhoneNumber>";
	private String last4SSNXML = "<last4SSN>%s</last4SSN>";
	private String zipXML = "<zip>%s</zip>";
	private String carrierCodeXML = "<carrierCode>%s</carrierCode>";
	private String mobilePhoneNumbersXML="<mobilePhoneNumbers>%s</mobilePhoneNumbers>"; 
	private String notificationStatusCodeXML="<notificationStatusCode>%s</notificationStatusCode>"; 

	private String ibhXML = "<internationalBusinessHierarchy>"
			+ "<enterprise>Best Buy Enterprise</enterprise>"
			+ "<tradingArea>US</tradingArea>"
			+ "<company>Best Buy LLP</company>" + "<brand>Best Buy</brand>"
			+ "<businessUnit>Best Buy Mobile</businessUnit>"
			+ "<channel>Web</channel>" + "</internationalBusinessHierarchy>"
			+ "<sourceSystem>%s</sourceSystem>";
			   
	private String locationXML = "<locationId>%d</locationId>"; 
	private String userIdXML = "<userId>%s</userId>";
	private String passwordXML = "<password>%s</password>";
	private String languageXML = "<language>%s</language>";
	private String trainingXML = "<isTrainingMode>%s</isTrainingMode>";
	
	private String eligiblityCheckClosingXML = "</upgradeEligibilityRequest> </ucs:getUpgradeEligibility>";
	private String putNotificationClosingXML = "</notificationStatusPutRequest> </ucs:putNotificationStatus>";
	private String getNotificationClosingXML = "</notificationStatusGetRequest> </ucs:getNotificationStatus>";
	
	private String closingXML = "</soap:Body> </soap:Envelope>";
	
	public UpgradeCheckerUtils(Context context){
		this.context = context;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}
	
	public void setUserId(String userId){
		this.userId = userId;		
	}
	
	public void setNotificationStatusCode(String notificationStatusCode) {
		this.notificationStatusCode = notificationStatusCode;
	}

	public String buildSOAPRequest(int type){
		
		switch(type){
			case ELIGIBILITY_CHECK_REQUEST:
				body = body + eligiblityCheckOpenXML; 
				closingXML = eligiblityCheckClosingXML+closingXML;
				break;
			case GET_NOTIFICATION_REQUEST:
				body = body + getNotificationOpenXML; 
				closingXML = getNotificationClosingXML+closingXML;
				mobilePhoneNumberXML = mobilePhoneNumbersXML;
				break;
			case PUT_NOTIFICATION_REQUEST:
				body = body + putNotificationOpenXML; 
				closingXML = putNotificationClosingXML+closingXML;
				break;
		}
		return build();
	}
	
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) context.getApplicationContext();
	}
	
	public static StringBuilder inputStreamToString(InputStream is) throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();		
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		while ((line = rd.readLine()) != null) {
			total.append(line);
		}		
		return total;
		
	}
	
	private String build(){
		String request = body;
		request += String.format(mobilePhoneNumberXML, getApp().getPrimaryPhoneNumber());
		if(notificationStatusCode != null){
			request += String.format(notificationStatusCodeXML, notificationStatusCode);
		}
		if(getApp().getLast4SSN() != null){
			request += String.format(last4SSNXML, getApp().getLast4SSN());
		}
		request += String.format(zipXML, getApp().getZipCode());
		request += String.format(carrierCodeXML, getApp().getCarrierCode());
		request += String.format(ibhXML, AppConfig.getUpgradeCheckerSourceSystemValue());
		request += String.format(locationXML, getApp().getStoreId());
		
		if(userId != null){
			request += String.format(userIdXML, userId);
		}
		if(getApp().getPin() != null){
			request += String.format(passwordXML, getApp().getPin());
		}
		if(language != null){
			request += String.format(languageXML, language);
		}
		if(getApp().isTestMode()){
			request += String.format(trainingXML, "true");
		}else{
			request += String.format(trainingXML, "false");
		}
		request += closingXML;
		return request;
	}
}
