package com.bestbuy.android.rewardzone.library.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestbuy.android.rewardzone.RZOffer;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.util.BBYLog;

/**
 * Json to Data Object convertor
 * 
 * @author sharmila_j
 * 
 */
public class RZOfferDetailsParser {
	
	private static final String SHORT_DESCRIPTION = "shortDescription";
	private static final String LONG_DESCRIPTION = "longDescription";
	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String DISPLAY_DATE = "displayEndDate";
	private static final String WALL_IMAGE_PATH = "wallImagePath";
	private static final String BUSINESS_PRIORITY = "businessPriority";
	private static final String SILVER_OFFER = "silverOffer";
	private static final String GAME_OFFER = "gamerOffer";
	private static final String CORE_OFFER = "coreOffer";
	private static final String RZM_OFFER = "rzmcOffer";
	private static final String RZCC_OFFER = "rzccOffer";
	private static final String SCHOOL_OFFER = "schoolOffer";
	private static final String ANONYMOUS_OFFER = "anonymousOffer";
	private static final String WALL_OFFER = "wallOffer";
	private static final String TERMS_CONDITIONS = "termsAndConditions";
	private static final String ANON_AUTH_OFFER = "anonAuthOffer";
	private static final String COUPON_START_DATE = "couponStartDate";
	private static final String COUPON_END_DATE = "couponEndDate";
	private static final String COUPON_TITLE = "couponTitle";
	private static final String COUPON_SUB_TITLE = "couponSubTitle";
	private static final String COUPON_DETAIL = "couponDetail";
	private static final String COUPON_SKU = "couponSku";
	private static final String PROMOTIONAL_TITLE = "promotionalTitle";
	private static final String CASHIER_INSTRUCTIONS = "cashierInstructions";
	private static final String CONFIRMATION_MESSAGE = "confirmationMessage";
	private static final String OPTEDIN= "optedin";
	private static final String ID = "id";
	private static final String TITLE = "title";
	
	
	/*
	 * Parsing the giving json string to RZOffer List
	 */
	public List<RZOfferDetails> parse(String responseString) throws JSONException, ParseException {
		
		JSONArray results = null;
		List<RZOfferDetails> dests = null;
		List<RZOfferDetails> unSortedList = null;
		//System.out.println("RESPONSE IN JSON PARSER : "+ responseString);
		results = new JSONArray(responseString);
		
		if(results != null) {
			int arrayLength = results.length();
			
			JSONObject source = null;			
			unSortedList = new ArrayList<RZOfferDetails>();

			for(int i=0; i<arrayLength; i++){
				source = results.getJSONObject(i);
				RZOfferDetails rzOffer=createFrom(source);
				unSortedList.add(rzOffer);
				//dests=sortOffers(dests,rzOffer);
			}
		}
		dests=sortOffers(unSortedList);
		return dests;
	}
	/*
	 * Fetching each element from json object and preparing RZOffer object
	 */
	private RZOfferDetails createFrom(JSONObject source) throws JSONException{
		
		BBYLog.d("JSON : ", source.toString());
		
		RZOfferDetails result = new RZOfferDetails();
		
		if(source.has(SHORT_DESCRIPTION))
			result.setShortDescription(source.getString(SHORT_DESCRIPTION));
		if(source.has(LONG_DESCRIPTION))
			result.setLongDescription(source.getString(LONG_DESCRIPTION));
		if(source.has(START_DATE))
			result.setStartDate(source.getString(START_DATE));
		if(source.has(END_DATE))
			result.setEndDate(source.getString(END_DATE));
		if(source.has(DISPLAY_DATE))
			result.setDisplayEndDate(source.getBoolean(DISPLAY_DATE));
		if(source.has(WALL_IMAGE_PATH))
			result.setWallImagePath(source.getString(WALL_IMAGE_PATH));
		if(source.has(BUSINESS_PRIORITY))
			result.setBusinessPriority(source.getInt(BUSINESS_PRIORITY));
		if(source.has(SILVER_OFFER))
			result.setSilverOffer(source.getBoolean(SILVER_OFFER));
		if(source.has(GAME_OFFER))
			result.setGamerOffer(source.getBoolean(GAME_OFFER));
		if(source.has(CORE_OFFER))
			result.setCoreOffer(source.getBoolean(CORE_OFFER));
		if(source.has(RZM_OFFER))
			result.setRzmcOffer(source.getBoolean(RZM_OFFER));
		if(source.has(RZCC_OFFER))
			result.setRzccOffer(source.getBoolean(RZCC_OFFER));
		if(source.has(SCHOOL_OFFER))
			result.setSchoolOffer(source.getBoolean(SCHOOL_OFFER));
		if(source.has(ANONYMOUS_OFFER))
			result.setAnonymousOffer(source.getBoolean(ANONYMOUS_OFFER));
		if(source.has(WALL_OFFER))
			result.setWallOffer(source.getBoolean(WALL_OFFER));
		if(source.has(TERMS_CONDITIONS))
			result.setTermsAndConditions(source.getString(TERMS_CONDITIONS));
		if(source.has(ANON_AUTH_OFFER))
			result.setAnonAuthOffer(source.getBoolean(ANON_AUTH_OFFER));
		if(source.has(COUPON_START_DATE))
			result.setCouponStartDate(source.getString(COUPON_START_DATE));
		if(source.has(COUPON_END_DATE))
			result.setCouponEndDate(source.getString(COUPON_END_DATE));
		if(source.has(COUPON_TITLE))
			result.setCouponTitle(source.getString(COUPON_TITLE));
		if(source.has(COUPON_SUB_TITLE))
			result.setCouponSubTitle(source.getString(COUPON_SUB_TITLE));
		if(source.has(COUPON_DETAIL))
			result.setCouponDetail(source.getString(COUPON_DETAIL));
		if(source.has(COUPON_SKU))
			result.setCouponSku(source.getLong(COUPON_SKU));
		if(source.has(PROMOTIONAL_TITLE))
			result.setPromotionalTitle(source.getString(PROMOTIONAL_TITLE));
		if(source.has(CASHIER_INSTRUCTIONS))
			result.setCashierInstructions(source.getString(CASHIER_INSTRUCTIONS));
		if(source.has(CONFIRMATION_MESSAGE))
			result.setConfirmationMessage(source.getString(CONFIRMATION_MESSAGE));
		if(source.has(OPTEDIN))
			result.setOptedin(source.getBoolean(OPTEDIN));
		if(source.has(ID))
			result.setId(source.getString(ID));
		if(source.has(TITLE))
			result.setTitle(source.getString(TITLE));
		
		return result;
	}
	/*private List<RZOfferDetails> sortOffers(List<RZOfferDetails> listOfRZOffers,RZOfferDetails rzOffer) throws ParseException{		
		int listIndexPos = 0;
		boolean isAdded=false;
		int listIndex=0;
		List<RZOfferDetails> optinTrueList= null;
		List<RZOfferDetails> optinfalseList = null;
		List<RZOfferDetails> sortedOfferList = new ArrayList<RZOfferDetails>();
		if(listOfRZOffers.size()==0){
			sortedOfferList.add(rzOffer);
		}else{
			for(;listIndex<listOfRZOffers.size();listIndex++){
				RZOfferDetails rzOfferDetails=listOfRZOffers.get(listIndex);				
				if(!rzOfferDetails.isOptedin()){
					if(rzOffer.isOptedin())
						listOfRZOffers.add(0,rzOffer);
					else
						listOfRZOffers.add(listIndex,rzOffer);
					isAdded=true;
					break;
				}				
			}
			if(isAdded){
				optinTrueList=listOfRZOffers.subList(0, listIndex);
				optinfalseList=listOfRZOffers.subList(listIndex,listOfRZOffers.size());
			}
			else{
				if(rzOffer.isOptedin()){
					listOfRZOffers.add(0,rzOffer);
					optinTrueList=listOfRZOffers.subList(0, listOfRZOffers.size());
				}
				else{
					listOfRZOffers.add(rzOffer);
					optinTrueList=listOfRZOffers.subList(0, listOfRZOffers.size()-1);
					optinfalseList=listOfRZOffers.subList(listOfRZOffers.size()-1,listOfRZOffers.size());					
				}
			}	
			sortedOfferList = new ArrayList<RZOfferDetails>();
			if(optinTrueList!=null){
				sortedOfferList.addAll(prioritySort(optinTrueList));
			}
			if(optinfalseList!=null){
				sortedOfferList.addAll(prioritySort(optinfalseList));
			}
		}
		
		return sortedOfferList;
		
	}*/
	
	private List<RZOfferDetails> sortOffers(List<RZOfferDetails> listOfRZOffers) throws ParseException{
		int listIndex=0;
		List<RZOfferDetails> optinTrueList= new ArrayList<RZOfferDetails>();
		List<RZOfferDetails> optinfalseList = new ArrayList<RZOfferDetails>();
		List<RZOfferDetails> sortedOfferList = new ArrayList<RZOfferDetails>();
		for(;listIndex<listOfRZOffers.size();listIndex++){
			RZOfferDetails rzOfferDetails=listOfRZOffers.get(listIndex);				
			if(rzOfferDetails.isOptedin()){
				optinTrueList.add(rzOfferDetails);
			}
			else{
				optinfalseList.add(rzOfferDetails);
			}
		}
		sortedOfferList.addAll(prioritySort(optinTrueList));
		sortedOfferList.addAll(prioritySort(optinfalseList));
		return sortedOfferList;
	}
	
	public List<RZOfferDetails> prioritySort(List<RZOfferDetails> subList) throws ParseException{
		int sortingIndex= 0;
		if(!subList.isEmpty()) {
			RZOfferDetails rzOfferDetails=subList.get(sortingIndex);
			for(int subListIndex=1;subListIndex<subList.size();subListIndex++){
				RZOfferDetails rzOfferCompare=subList.get(subListIndex);
				if(rzOfferDetails.getBusinessPriority()>rzOfferCompare.getBusinessPriority()){				
					Collections.swap(subList, sortingIndex, subListIndex);
					sortingIndex=subListIndex;
					rzOfferDetails=subList.get(sortingIndex);				
				}
				else if(rzOfferCompare.getBusinessPriority()==rzOfferDetails.getBusinessPriority()){
					SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");				
					Date actualDate=dateFormatter.parse(rzOfferDetails.getEndDate().replace("CDT", ""));
					Date compareDate=dateFormatter.parse(rzOfferCompare.getEndDate().replace("CDT", ""));
					int flag=actualDate.compareTo(compareDate);
					if(flag>0){
						Collections.swap(subList, sortingIndex, subListIndex);
						sortingIndex=subListIndex;
						rzOfferDetails=subList.get(sortingIndex);
					}
					else if(flag==0){
						Collections.swap(subList, sortingIndex, subListIndex);
						sortingIndex=subListIndex;
						rzOfferDetails=subList.get(sortingIndex);
					}
				}			
			}
		}
		return subList;
	}
	
	// Get BBY Offer Implementation : 24/09/2011
	
	public List<RZOffer> getSortedBBYOfferList(List<RZOffer> bbyOfferList){
		List<RZOffer> sortedList= null;
		Collections.sort(bbyOfferList, new Comparator(){			 
            public int compare(Object o1, Object o2) {
            	RZOffer p1 = (RZOffer) o1;
            	RZOffer p2 = (RZOffer) o2;
               return p1.getExpiration().compareTo(p2.getExpiration());
            } 
        });
		sortedList=bbyOfferList;
		return sortedList;
	}
	
	//End
}
