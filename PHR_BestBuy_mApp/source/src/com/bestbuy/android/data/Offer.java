package com.bestbuy.android.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class Offer extends Product {
	private static final long serialVersionUID = 65895309934503095L;
	private String TAG = this.getClass().getName();
	
	public static final String IMAGE_URL = "image_url";
	public static final String KEY = "key";
	public static final String CATEGORY_PATH_KEYS = "category_path_keys";
	public static final String REMIX_DATA = "remix_data";
	public static final String END_DATE = "end";
	public static final String CHANNEL_KEYS = "channel_keys";
	
	public static final String GAMING_CHANNEL="bby-mobile-game-tradein";

	private ArrayList<String> skus;
	private String productKey = "";
	private String remixDataString;
	private String marketingCopy;
	private String price;
	private Date endDate;
	private ArrayList<String> categoryPathKeys;
	private ArrayList<String> channelKeys;

	public ArrayList<String> getSkus() {
		return skus;
	}

	public void setSkus(ArrayList<String> skus) {
		this.skus = skus;
	}

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getRemixDataString() {
		return remixDataString;
	}

	public void setRemixDataString(String remixDataString) {
		this.remixDataString = remixDataString;
	}
	
	public ArrayList<String> getCategoryPathKeys() {
		return categoryPathKeys;
	}
	
	public ArrayList<String> getChannelKeys(){
		return channelKeys;
	}
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public Date getEndDate(){
		return endDate;
	}

	public void loadOffersData(JSONObject obj) throws Exception{
		this.skus = new ArrayList<String>();
		JSONArray jsonSkus = obj.getJSONArray(SKUS);
		for (int i = 0; i < jsonSkus.length(); i++) {
			this.skus.add(jsonSkus.getString(i));
		}
		
		JSONArray jsonCategoryPathKeys = obj.optJSONArray(CATEGORY_PATH_KEYS);
		this.categoryPathKeys = new ArrayList<String>();
		if (jsonCategoryPathKeys != null) {
			for (int i = 0; i < jsonCategoryPathKeys.length(); i++) {
				this.categoryPathKeys.add((String) jsonCategoryPathKeys.opt(i));
			}
		}
		
		JSONArray jsonChannelKeys = obj.optJSONArray(CHANNEL_KEYS);
		this.channelKeys = new ArrayList<String>();
		if (jsonChannelKeys != null) {
			for (int i = 0; i < jsonChannelKeys.length(); i++) {
				this.channelKeys.add((String) jsonChannelKeys.opt(i));
			}
		}
		
		JSONObject remixData = obj.optJSONObject(REMIX_DATA);
		if (remixData != null) {
			remixDataString = remixData.toString();
			loadSearchResultData(new JSONObject(this.remixDataString));
			if (skus.size() == 0) { // we had no skus in in the offers data,
				// get it from the remix product data.
				this.skus.add(remixData.getString(SKU));
			}
			this.customerReviewAverage = remixData.getString(CUSTOMER_REVIEW_AVERAGE);
		}
		this.imageURL = obj.optString(IMAGE_URL, AppData.JSON_NULL);
		this.productKey = obj.optString(KEY, "");
		this.title = obj.optString(TITLE, "No title for this offer.");
		this.url = obj.optString(URL, AppData.JSON_NULL);
		this.title = replaceXMLCharacters(title);
		this.price = obj.optString(PRICE, "0.0");
		
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String end = obj.optString(END_DATE);
		if(!end.equalsIgnoreCase("")){
			this.endDate = df.parse(end);
		}
		
		if (this.price.equals(AppData.JSON_NULL)) {
			this.price = "0.0";
		}
		
	}

	public String getOffersMarketingCopy() {
		int marketingCopyLength = marketingCopy.length();
		if (marketingCopyLength > 200) {
			return marketingCopy.substring(0, 200) + "...";
		}
		return marketingCopy;
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		Offer offer = (Offer) obj;
		if (title != null) {
			if (this.hashCode() == offer.hashCode()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getTitle().hashCode();
	}
}