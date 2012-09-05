package com.bestbuy.android.rewardzone.logic;

import com.bestbuy.android.rewardzone.data.RZOfferData;

public class RZOfferLogic {
	/*
	 * Calling Data layer for Making OptIn request
	 */
		public static int sendOptInOffer(String offerId){
			 int status =0;
			 int STATUS_TRUE = 1;
			 int STATUS_NO_NETWORK = 2;
			 int STATUS_FALSE = 3;
			 String responseString = RZOfferData.getOptInOfferResponseStatus(offerId);
			 if(responseString!=null){
				 if(responseString.equals("true")){
					 status = STATUS_TRUE;
				 }else if(responseString.equals("NO_NETWORK_CONNECTION")){
					 status = STATUS_NO_NETWORK;
				 }else if(responseString.equals("ERROR")){
					 status = STATUS_FALSE;
				 }
			 }			
			 return status;
		}
}
