package com.bestbuy.android.data;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

public class Warranty extends Product {
	private static final long serialVersionUID = -5529399365184422982L;
	private String TAG = this.getClass().getName();
	
	public static final String PROTECTION_PLAN_LOW_PRICE = "protectionPlanLowPrice";
	public static final String PROTECTION_PLAN_HIGH_PRICE = "protectionPlanHighPrice";
	public static final String CJ_AFFILIATE_ADD_TO_CART_URL = "cjAffiliateAddToCartUrl";
	public static final String START_DATE = "startDate";
	public static final String MOBILE_URL = "mobileUrl";
	public static final String SALES_RANK_LONG_TERM = "salesRankLongTerm";

	//valid warranty types
	public static final byte PROTECTION_PLAN = 1;
	public static final byte BUY_BACK_PLAN = 2;

	private String protectionPlanLowPrice;
	private String protectionPlanHighPrice;
	
	private String cjAffiliateAddToCartUrl;
	private String mobileURL;
	private String startDate;
	private String salesRankLongTerm;

	private byte warrantyType;
	
	public byte getWarrantyType() {
		return warrantyType;
	}

	public void setWarrantyType(byte warrantyType) {
		this.warrantyType = warrantyType;
	}

	public String getProtectionPlanLowPrice() {
		DecimalFormat money = new DecimalFormat("0.00");
		if (protectionPlanLowPrice != null && !protectionPlanLowPrice.equals("")) {
			return money.format(Double.parseDouble(protectionPlanLowPrice));
		}
		return protectionPlanLowPrice;
	}

	public void setProtectionPlanLowPrice(String protectionPlanLowPrice) {
		this.protectionPlanLowPrice = protectionPlanLowPrice;
	}

	public String getProtectionPlanHighPrice() {
		DecimalFormat money = new DecimalFormat("0.00");
		if (protectionPlanHighPrice != null && !protectionPlanHighPrice.equals("")) {
			return money.format(Double.parseDouble(protectionPlanHighPrice));
		}
		return protectionPlanHighPrice;
	}

	public void setProtectionPlanHighPrice(String protectionPlanHighPrice) {
		this.protectionPlanHighPrice = protectionPlanHighPrice;
	}

	

	public String getCjAffiliateAddToCartUrl() {
		return cjAffiliateAddToCartUrl;
	}

	public void setCjAffiliateAddToCartUrl(String cjAffiliateAddToCartUrl) {
		this.cjAffiliateAddToCartUrl = cjAffiliateAddToCartUrl;
	}

	public String getMobileURL() {
		return mobileURL;
	}

	public void setMobileURL(String mobileURL) {
		this.mobileURL = mobileURL;
	}

	public String getStartDate() {
		if (startDate != null && startDate.equals("null")) {
			SimpleDateFormat sdm = new SimpleDateFormat("yyyy-MM-dd");
			return sdm.format(Calendar.getInstance().getTime());
		}
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getSalesRankLongTerm() {
		return salesRankLongTerm;
	}

	public void setSalesRankLongTerm(String salesRankLongTerm) {
		this.salesRankLongTerm = salesRankLongTerm;
	}

	public void loadWarrantyData(JSONObject obj, byte warrantyType) throws Exception{
		this.warrantyType = warrantyType;
		this.salesRankLongTerm = obj.optString(SALES_RANK_LONG_TERM);
		this.startDate = obj.optString(START_DATE);
		
		this.mobileURL = obj.optString(MOBILE_URL);
		this.cjAffiliateAddToCartUrl = obj.optString(CJ_AFFILIATE_ADD_TO_CART_URL);
		

		this.protectionPlanLowPrice = obj.optString(PROTECTION_PLAN_LOW_PRICE);
		this.protectionPlanHighPrice = obj.optString(PROTECTION_PLAN_HIGH_PRICE);	
	}
}