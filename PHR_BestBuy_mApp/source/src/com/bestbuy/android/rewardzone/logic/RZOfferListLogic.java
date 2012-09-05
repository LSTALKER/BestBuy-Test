package com.bestbuy.android.rewardzone.logic;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;

import com.bestbuy.android.rewardzone.data.RZOfferListData;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.util.BBYLog;
/**
 * Logical operations on obtained response before sending for screen mapping (If
 * needed)
 * 
 * @author sharmila_j
 * 
 */
public class RZOfferListLogic {

	/*
	 * Calling Data layer to fetch the RZOffers
	 */
	public static List<RZOfferDetails> getOfferList(int requestPageNumber) throws SocketTimeoutException, HttpHostConnectException, Exception {
		try{
			return RZOfferListData.getOfferDetailsList(requestPageNumber);
		}catch(JSONException e){
			BBYLog.printStackTrace("RZOfferListLogic.Java", e);
		}
		return null;
	}	
}
