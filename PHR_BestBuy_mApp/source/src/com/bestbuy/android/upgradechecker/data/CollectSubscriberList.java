package com.bestbuy.android.upgradechecker.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.util.BBYLog;

public class CollectSubscriberList {
	
	private final String TAG = this.getClass().getName();
	
	private Context context;	
	private String zip=null;
	public CollectSubscriberList(Context context,String zip){
		this.context=context;
		this.zip=zip;
	}
	public ArrayList<Subscriber> getSubscriberList(){
		ArrayList<Subscriber> subscriberList=new ArrayList<Subscriber>();		
 	    HashMap results = getApp().getResults();	  
	    String tradeInDisclaimer = (String) results.get("tradeInDisclaimer");	    
        boolean primaryNumberMatchesDeviceNumber = getApp().primaryNumberMatchesDeviceNumber();
        //zip=(String) results.get("zip");       
        ArrayList viewableSubscribers = new ArrayList();
        ArrayList allSubscribers = (ArrayList) results.get("subscribers");
        if(allSubscribers!=null){
			Iterator iter = allSubscribers.iterator();
		    while (iter.hasNext()) {
				HashMap subscriberHash = (HashMap) iter.next();
				boolean isPrimaryNumber = getApp().primaryNumberMatches((String) subscriberHash.get("mobilePhoneNumber"));
		    	if (primaryNumberMatchesDeviceNumber || !getApp().isPrivacyMode() || isPrimaryNumber) {
		    		viewableSubscribers.add(subscriberHash);
		    	}
		    }
	
			iter = viewableSubscribers.iterator();  
			
		    while (iter.hasNext()) {
				HashMap subscriberHash = (HashMap) iter.next();
				Subscriber subscriber=createSubscriberView(subscriberHash, tradeInDisclaimer, (viewableSubscribers.size() > 1));
				subscriberList.add(subscriber);
		    }     
        }
        return subscriberList;
	}
	private Subscriber createSubscriberView(HashMap subscriberHash,
			String tradeInDisclaimer, Boolean setAsAccordian) {
		Subscriber subscriber = new Subscriber();
		String subscriberPhoneNumber = (String) subscriberHash
				.get("mobilePhoneNumber");		
		String areaCode = subscriberPhoneNumber.substring(0, 3);
		String firstNum = subscriberPhoneNumber.substring(3, 6);
		String secoNum = subscriberPhoneNumber.substring(6,
				subscriberPhoneNumber.length());
		String displayPhoneNumber = areaCode + "-" + firstNum + "-" + secoNum;
		String tradeInPhoneName = (String) subscriberHash
				.get("tradeInPhoneName");
		String upgradeEligibilityType = (String) subscriberHash
				.get("upgradeEligibilityType");
		String upgradeEligibilityDateString = (String) subscriberHash
				.get("upgradeEligibilityDate");
		String contractEndDateString = (String) subscriberHash
				.get("contractEndDate");
		contractEndDateString = formatDate(contractEndDateString);
		upgradeEligibilityDateString = formatDate(upgradeEligibilityDateString);
		String tradeInValue = (String) subscriberHash.get("tradeInValue");
		if (null == tradeInValue) {
			tradeInValue = "";
		} else {
			tradeInValue = "$" + tradeInValue;
		}
		String upgradeEligibilityMessage = (String) subscriberHash
				.get("upgradeEligibilityMessage");
		String upgradeEligibilityFootnote = (String) subscriberHash
				.get("upgradeEligibilityFootnote");
		String earlyTerminationWarning = (String) subscriberHash
				.get("earlyTerminationWarning");
		String upgradeEligibilityFlag = (String) subscriberHash
				.get("upgradeEligibilityFlag");
		subscriber.setMobilePhoneNumber(displayPhoneNumber);
		subscriber.setZip(zip);
		subscriber.setTradeInPhoneName(tradeInPhoneName);
		subscriber.setUpgradeEligibilityType(upgradeEligibilityType);
		subscriber.setUpgradeEligibilityDate(upgradeEligibilityDateString);
		subscriber.setContractEndDate(contractEndDateString);
		subscriber.setTradeInValue(tradeInValue);
		subscriber.setUpgradeEligibilityMessage(upgradeEligibilityMessage);
		subscriber.setUpgradeEligibilityFootnote(upgradeEligibilityFootnote);
		if(upgradeEligibilityFlag.equals("true"))
			subscriber.setUpgradeEligibilityFlag(true);
		else
			subscriber.setUpgradeEligibilityFlag(false);
		subscriber.setEarlyTerminationWarning(earlyTerminationWarning);
		return subscriber;

	}
	private String formatDate(String dateString){			
		if(dateString!=null){			
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat format=new SimpleDateFormat("MM/dd/yyyy");
			try {				
				Date date=dateFormatter.parse(dateString);				
				return format.format(date);
			} catch (ParseException e) {			
				BBYLog.printStackTrace(TAG, e);
			}			
		}
		return "";
	}
	protected BestBuyApplication getApp() {
		return (BestBuyApplication) context.getApplicationContext();
	}
}
