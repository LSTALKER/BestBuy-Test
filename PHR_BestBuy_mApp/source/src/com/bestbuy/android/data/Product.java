package com.bestbuy.android.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;

import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.marketplace.logic.MarketPlaceLogic;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.StarRating;

/**
 * Contains information about a product
 * 
 * @author Recursive Awesome
 * 
 */
public class Product implements Serializable 
{
	
	private static final long serialVersionUID = 3992436606276386746L;
	public static final String SKU = "sku";
	public static final String SKUS = "skus";
	public static final String PRODUCT_ID = "productId";
	public static final String DEPARTMENT_ID = "departmentId";
	public static final String CATEGORY_PATH = "categoryPath";
	public static final String NAME = "name";
	public static final String TITLE = "title";
	public static final String VALUE = "value";
	public static final String URL = "url";
	public static final String ARTIST_NAME = "artistName";
	public static final String SHORT_DESCRIPTION = "shortDescription";
	public static final String PLOT = "plot";
	public static final String CUSTOMER_REVIEW_AVERAGE = "customerReviewAverage";
	public static final String CUSTOMER_REVIEW_COUNT = "customerReviewCount";
	public static final String REGULAR_PRICE = "regularPrice";
	public static final String ON_SALE = "onSale";
	public static final String MEDIUM_IMAGE = "mediumImage";
	public static final String RELEASE_DATE = "releaseDate";
	public static final String SALE_PRICE = "salePrice";
	public static final String PLAN_PRICE = "planPrice";
	public static final String PRICE = "price";
	public static final String MODEL_NUMBER = "modelNumber";
	public static final String FORMAT = "format";
	public static final String DESCRIPTION = "description";
	public static final String MARKETING_COPY = "marketing_copy";
	public static final String LONG_DESCRIPTION = "longDescription";
	public static final String FREQUENTLY_PURCHASED_WITH = "frequentlyPurchasedWith";
	public static final String IN_STORE_AVAILABILITY = "inStoreAvailability";
	public static final String ONLINE_AVAILABILITY = "onlineAvailability";
	public static final String DETAILS = "details";
	public static final String MANUFACTURER = "manufacturer";
	public static final String TYPE = "type";
	public static final String ESRB_RATING = "esrbRating";
	public static final String IN_STORE_PICKUP = "inStorePickup";
	public static final String HOME_DELIVERY = "homeDelivery";
	public static final String FRIENDS_AND_FAMILY_PICKUP = "friendsAndFamilyPickup";
	public static final String SPECIAL_ORDER = "specialOrder";
	public static final String ORDERABLE = "orderable";
	
	public static final String PROTECTION_PLANS_SKU = "protectionPlans.sku";
	public static final String BUYBACK_PLANS_SKU = "buybackPlans.sku";
	public static final String PROTECTION_PLANS = "protectionPlans";
	public static final String BUYBACK_PLANS = "buybackPlans";
	public static final String PROTECTION_PLAN_TERM = "protectionPlanTerm";
	public static final String PROTECTION_PLAN_TYPE = "protectionPlanType";

	public static final String ACCESSORIES_IMAGE = "accessoriesImage";
	public static final String BACKVIEW_IMAGE = "backViewImage";
	public static final String LARGE_FRONT_IMAGE = "largeFrontImage";
	public static final String RIGHTVIEW_IMAGE = "rightViewImage";
	public static final String TOPVIEW_IMAGE = "topViewImage";
	public static final String LEFTVIEW_IMAGE = "leftViewImage";
	public static final String ALTERNATE_VIEWS_IMAGE = "alternateViewsImage";
	public static final String REMOTE_CONTROL_IMAGE = "remoteControlImage";
	public static final String ANGLE_IMAGE = "angleImage";
	public static final String THUMBNAIL_IMAGE = "thumbnailImage";
	public static final String LARGE_IMAGE = "largeImage";
	public static final String IMAGE = "image";
	public static final String MARKET_PLACE_ITEMS="marketplace";
	
	public static final String ADVERTISED_PRICE_RESTRICTION="advertisedPriceRestriction";
	public static final String PLATFORM = "platform";
	public static final String TRADE_IN_VALUE = "tradeInValue";
	public static final String ACTIVE = "active";
	public static final String PREOWNED = "preowned";

	private String TAG = this.getClass().getName();
	protected String sku;
	protected String productId;
	protected String departmentId;
	protected String categoryId;
	protected String modelNumber;
	protected String format;
	protected String name;
	protected String title;
	protected String url;
	protected String artistName;
	protected String shortDescription;
	protected String customerReviewAverage;
	protected String customerReviewCount;
	protected String regularPrice;
	protected String salePrice;
	protected String planPrice;
	protected boolean onSale;
	protected boolean deal;
	protected String accessoriesImageURL;
	protected String mediumImageURL;
	protected String imageURL;
	protected String thumbnailImageURL;
	protected String largeImageURL;
	protected String marketingCopy;
	protected String longDescription;
	protected String releaseDate;
	protected boolean inStoreAvailablity;
	protected boolean onlineAvailability;
	protected ArrayList<String> frequentlyPurchasedWith;
	protected ArrayList<Detail> details;
	protected String largeFrontImageURL;
	protected String backViewImageURL;
	protected String rightViewImageURL;
	protected String topViewImageURL;
	protected String leftViewImageURL;
	protected String alternateViewsImageURL;
	protected String remoteControlImageURL;
	protected String angleImageURL;
	protected ArrayList<String> categoryPath;
	protected String categoryName;
	protected String type;
	protected String esrbRating;
	protected boolean inStorePickup;
	protected boolean homeDelivery;
	protected boolean friendsAndFamilyPickup;
	protected boolean specialOrder;
	protected boolean marketPlaceAvailable;
	

	protected String orderable;
	protected boolean advertisedPriceRestriction;
	protected ArrayList<String> protectionPlans;
	protected ArrayList<String> buybackPlans;

	protected String platform;
	protected String tradeInValue;
	protected boolean active;
	protected boolean preowned;

	private String protectionPlanTerm;
	private String protectionPlanType;
	private MarketPlaceDetails marketPlaceDetails;
	private ArrayList<String> storeLocation = new ArrayList<String>();
	
	private boolean zipCodeFlag;
	
	public boolean isZipCodeFlag() {
		return zipCodeFlag;
	}

	public void setZipCodeFlag(boolean zipCodeFlag) {
		this.zipCodeFlag = zipCodeFlag;
	}

	public  ArrayList<String> getStoreLocation() {
		return storeLocation;
	}

	public void setStoreLocation( ArrayList<String> storeLocation) {
		this.storeLocation = storeLocation;
	}

	public static DecimalFormat getDollarFormat() {
		DecimalFormat dollarFormat;
		dollarFormat = new DecimalFormat("###,###.00");
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator(',');
		dollarFormat.setDecimalFormatSymbols(dfs);
		return dollarFormat;
	}
	public MarketPlaceDetails getMarketPlaceDetails() {
		return marketPlaceDetails;
	}
	public void setMarketPlaceDetails(MarketPlaceDetails marketPlaceDetails) {
		this.marketPlaceDetails = marketPlaceDetails;
	}
	public boolean isMarketPlaceAvailable() {
		return marketPlaceAvailable;
	}

	public String getOffersImageUrl() {
		return this.imageURL;
	}

	public String getOffersMarketingCopy() {
		int marketingCopyLength = marketingCopy.length();
		if (marketingCopyLength > 200) {
			return marketingCopy.substring(0, 200) + "...";
		}
		return marketingCopy;
	}

	public String getAccessoriesImageURL() {
		return accessoriesImageURL;
	}

	public void setAccessoriesImageURL(String accessoriesImageURL) {
		this.accessoriesImageURL = accessoriesImageURL;
	}

	public String getBackViewImageURL() {
		return backViewImageURL;
	}

	public void setBackViewImageURL(String backViewImageURL) {
		this.backViewImageURL = backViewImageURL;
	}
	
	public String getLargeFrontImageURL() {
		return largeFrontImageURL;
	}
	
	public void setLargeFrontImage(String largeFrontImageURL) {
		this.largeFrontImageURL = largeFrontImageURL;
	}

	public String getRightViewImageURL() {
		return rightViewImageURL;
	}

	public void setRightViewImageURL(String rightViewImageURL) {
		this.rightViewImageURL = rightViewImageURL;
	}

	public String getTopViewImageURL() {
		return topViewImageURL;
	}

	public void setTopViewImageURL(String topViewImageURL) {
		this.topViewImageURL = topViewImageURL;
	}

	public String getLeftViewImageURL() {
		return leftViewImageURL;
	}

	public void setLeftViewImageURL(String leftViewImageURL) {
		this.leftViewImageURL = leftViewImageURL;
	}

	public String getAlternateViewsImageURL() {
		return alternateViewsImageURL;
	}

	public void setAlternateViewsImageURL(String alternateViewsImageURL) {
		this.alternateViewsImageURL = alternateViewsImageURL;
	}

	public String getRemoteControlImageURL() {
		return remoteControlImageURL;
	}

	public void setRemoteControlImageURL(String remoteControlImageURL) {
		this.remoteControlImageURL = remoteControlImageURL;
	}

	public String getAngleImageURL() {
		return angleImageURL;
	}

	public void setAngleImageURL(String angleImageURL) {
		this.angleImageURL = angleImageURL;
	}

	public void setMediumImageURL(String mediumImageURL) {
		this.mediumImageURL = mediumImageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public void setThumbnailImageURL(String thumbnailImageURL) {
		this.thumbnailImageURL = thumbnailImageURL;
	}

	public void setLargeImageURL(String largeImageURL) {
		this.largeImageURL = largeImageURL;
	}

	public ArrayList<String> getCategoryPath() {
		if (categoryPath != null) {
			return categoryPath;
		}
		return new ArrayList<String>();
	}

	public String getParentCategory() {
		if (categoryPath != null) {
			int lastCategoryIndex = categoryPath.size() - 1;
			return categoryPath.get(lastCategoryIndex);
		}
		return null;
			
	}
	
	public String getSku() {
		if(sku != null)
			return sku.trim();
		else return "";
	}

	public String getName() {
		if (artistName != null && !artistName.equals("null")) {
			return artistName + " - " + name;
		}
		return name;
	}
	
	
	
	
	public void setName(String name){
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}

	public String getShortDescription() {
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription){
		this.shortDescription = shortDescription;
	}

	public ArrayList<Detail> getDetails() {
		if (details != null) {
			return details;
		}
		return new ArrayList<Detail>();
	}

	public String getLongDescription() {
		if (longDescription != null && !longDescription.equals(AppData.JSON_NULL)) {
			//Get rid of the stupid "Synopsis" that shows up in front of almost every long description
			if (longDescription.indexOf("Synopsis") == 0) {
				longDescription = longDescription.substring("Synopsis".length());
			}
			return longDescription;
		}
		return "N/A";
	}

	public String getCategoryId() {
		return categoryId;
	}
	

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public ArrayList<String> getFrequentlyPurchasedWith() {
		if (frequentlyPurchasedWith != null) {
			return frequentlyPurchasedWith;
		}
		return new ArrayList<String>();
	}
	
	public boolean hasPSP(){
		return protectionPlans != null || buybackPlans != null;
	}
	
	public boolean isProtectionPlan(String sku){
		if(protectionPlans != null && protectionPlans.contains(sku)){
			return true;
		}
		return false;
	}
	
	public boolean isBuyBackPlan(String sku){
		if(buybackPlans != null && buybackPlans.contains(sku)){
			return true;
		}
		return false;
	}

	public String getProtectionPlanTerm() {
		return protectionPlanTerm;
	}

	public void setProtectionPlanTerm(String protectionPlanTerm) {
		this.protectionPlanTerm = protectionPlanTerm;
	}

	public String getProtectionPlanType() {
		return protectionPlanType;
	}

	public void setProtectionPlanType(String protectionPlanType) {
		this.protectionPlanType = protectionPlanType;
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getLargeImageURL() {
		return largeImageURL;
	}

	public String getCustomerReviewAverage() {
		String reviewAverage = "0";
		if (customerReviewAverage != null && !customerReviewAverage.equals(AppData.JSON_NULL)) {
			reviewAverage = customerReviewAverage;
		}
		return reviewAverage;
	}

	public String getRegularPrice() {
		if (regularPrice != null && !regularPrice.equals("")) {
			return getDollarFormat().format(Double.parseDouble(regularPrice));
		}
		return regularPrice;
	}
	
	public void setRegularPrice(String regularPrice){
		this.regularPrice = regularPrice;
	}
	
	public void setSalePrice(String salePrice){
		this.salePrice = salePrice;
	}

	public String getType() {
		return type;
	}

	public boolean isOnSale() {
		if (getSalePrice() == null || getSalePrice().equalsIgnoreCase("")) {
			return false;
		}
		return onSale;
	}

	public void setDeal(boolean deal) {
		this.deal = deal;
	}

	public boolean isDeal() {
		return deal;
	}

	public String getModelNumber() {
		String model = "N/A";
		if (modelNumber != null && !modelNumber.equals("") && !modelNumber.equals(AppData.JSON_NULL)) {
			model = modelNumber.toUpperCase();
		} else if (format != null && !format.equalsIgnoreCase("") && !format.equals(AppData.JSON_NULL)) {
			model = format.toUpperCase();
		}
		return model;
	}

	public String getReleaseDate() {
		if (releaseDate != null && releaseDate.equals("null")) {
			SimpleDateFormat sdm = new SimpleDateFormat("yyyy-MM-dd");
			return sdm.format(Calendar.getInstance().getTime());
		}
		return releaseDate;
	}

	public String getSalePrice() {
		if (salePrice != null) {
			return getDollarFormat().format(Double.parseDouble(salePrice));
		}
		return salePrice;
	}

	public String getPlanPrice() {
		if (planPrice != null && planPrice.length() > 0 && !planPrice.equals("null")) {
			return getDollarFormat().format(Double.parseDouble(planPrice));
		} else {
			return null;
		}
	}

	public String getPlatform() {
		return platform;
	}


	public String getTradeInValue() {
		if (tradeInValue != null && !tradeInValue.equals("")) {
			return getDollarFormat().format(Double.parseDouble(tradeInValue));
		}		
		return tradeInValue;
	}
	
	public double getTradeInValueAsDouble(){
		return Double.parseDouble(tradeInValue);
	}
	
	
	public boolean isActive() {
		return active;
	}


	public boolean isPreowned() {
		return preowned;
	}


	public boolean isInStoreAvailablity() {
		return inStoreAvailablity;
	}

	public boolean isOnlineAvailability() {
		return onlineAvailability;
	}

	public String getCustomerReviewCount() {
		String reviewCount = "0";
		if (customerReviewAverage != null && !customerReviewAverage.equals(AppData.JSON_NULL)) {
			reviewCount = customerReviewCount;
		}
		return reviewCount;
	}

	public String getMediumImageURL() {
		return mediumImageURL;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getEsrbRating() {
		return esrbRating;
	}

	public boolean isInStorePickup() {
		return inStorePickup;
	}

	public boolean isHomeDelivery() {
		return homeDelivery;
	}

	public boolean isFriendsAdnFamilyPickup() {
		return friendsAndFamilyPickup;
	}

	public boolean isSpecialOrder() {
		return specialOrder;
	}

	public String getOrderable() {
		if (orderable == null) {
			return "";
		}
		return orderable;
	}
	
	public boolean isPreorder() {
		boolean preorder = false;
		DateFormat releaseDf = new SimpleDateFormat("yyyy-MM-dd");
		if (getOrderable().toLowerCase().contains("preorder")) {
			preorder = true;
		}
		try {
			if (releaseDate != null && !releaseDate.equals(AppData.JSON_NULL) && releaseDf.parse(releaseDate).after(new Date())) {
				preorder = true;
			}
		} catch (ParseException e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return preorder;
	}
	
	public boolean isSoldOut() {
		if (orderable != null) {
			return orderable.toLowerCase().contains("soldout");
		} else {
			return true;
		}
	}

	public boolean isComingSoon() {
		if (orderable != null) {
			return orderable.toLowerCase().contains("comingsoon");
		} else {
			return false;
		}
	}
	

	public String getBestImageURL() {
		if (getLargeImageURL() != null && !getLargeImageURL().equals(AppData.JSON_NULL)) {
			return getLargeImageURL();
		} else if (getMediumImageURL() != null && !getMediumImageURL().equals(AppData.JSON_NULL)) {
			return getMediumImageURL();
		} else if (getImageURL() != null && !getImageURL().equals(AppData.JSON_NULL)) {
			return getImageURL();
		} else {
			return getThumbnailImageURL();
		}
	}

	public String getSaleOrRegularPrice() {
		if (onSale) {
			return salePrice;
		}
		return regularPrice;
	}

	public String getThumbnailImageURL() {
		return thumbnailImageURL;
	}

	public String getProductId() {
		return productId;
	}
	
	public boolean isInStoreOnly() {
		return (!this.isOnlineAvailability() && this.isInStoreAvailablity() && this.isInStorePickup() && !this.isSpecialOrder() && !this.isHomeDelivery());
	}

	public boolean isAdvertisedPriceRestriction() {
		return advertisedPriceRestriction;
	}

	public void setAdvertisedPriceRestriction(boolean advertisedPriceRestriction) {
		this.advertisedPriceRestriction = advertisedPriceRestriction;
	}

	public Bitmap getCustomerReviewStarImage(Context context) {
		String reviewAverage = "0";
		if (customerReviewAverage != null && !customerReviewAverage.equals(AppData.JSON_NULL)) {
			reviewAverage = customerReviewAverage;
		}

		return StarRating.getAssociatedStarImage(reviewAverage, context);
	}

	public void loadDetailsData(JSONObject obj) throws Exception {
		this.details = new ArrayList<Detail>();
		String manufacturer = obj.optString(MANUFACTURER);
		if (manufacturer.equals("") || manufacturer.equals("null")) {
			manufacturer = "N/A";
		}
		Detail priceDetail = new Detail("Price", "$" + this.getSaleOrRegularPrice());
		Detail mfrDetail = new Detail("Manufacturer", replaceXMLCharacters(manufacturer));
		this.details.add(priceDetail);
		this.details.add(mfrDetail);
		JSONArray details = obj.optJSONArray(DETAILS);
		if (details != null) {
			for (int j = 0; j < details.length(); j++) {
				JSONObject jsonDetailObj = details.optJSONObject(j);
				Detail detail = new Detail(replaceXMLCharacters(jsonDetailObj.optString(NAME)), replaceXMLCharacters(jsonDetailObj
						.optString(VALUE)));
				if (!detail.getName().equals("USB 2.0 Ports")) { // TODO: remove
																	// this once
																	// remix is
																	// fixed
					this.details.add(detail);
				}
			}
		}
	}

	protected void loadImages(JSONObject obj) throws Exception {
		this.accessoriesImageURL = obj.optString(ACCESSORIES_IMAGE);
		this.backViewImageURL = obj.optString(BACKVIEW_IMAGE);
		this.largeFrontImageURL = obj.optString(LARGE_FRONT_IMAGE);
		this.rightViewImageURL = obj.optString(RIGHTVIEW_IMAGE);
		this.topViewImageURL = obj.optString(TOPVIEW_IMAGE);
		this.leftViewImageURL = obj.optString(LEFTVIEW_IMAGE);
		this.alternateViewsImageURL = obj.optString(ALTERNATE_VIEWS_IMAGE);
		this.remoteControlImageURL = obj.optString(REMOTE_CONTROL_IMAGE);
		this.largeImageURL = obj.optString(LARGE_IMAGE);
		this.imageURL = obj.optString(IMAGE);
		this.angleImageURL = obj.optString(ANGLE_IMAGE);
	}

	public void loadSearchResultData(JSONObject obj) throws Exception {
		this.sku = obj.optString(SKU);
		this.productId = obj.optString(PRODUCT_ID);
		this.name = obj.optString(NAME);
		this.artistName = obj.optString(ARTIST_NAME, null);
		this.shortDescription = obj.optString(SHORT_DESCRIPTION);
		this.customerReviewAverage = obj.optString(CUSTOMER_REVIEW_AVERAGE, "0");
		this.customerReviewCount = obj.optString(CUSTOMER_REVIEW_COUNT, "0");
		this.regularPrice = obj.optString(REGULAR_PRICE);
		this.salePrice = obj.optString(SALE_PRICE);
		this.planPrice = obj.optString(PLAN_PRICE);
		this.onSale = obj.optBoolean(ON_SALE);
		this.thumbnailImageURL = obj.optString(THUMBNAIL_IMAGE);
		this.largeImageURL = obj.optString(LARGE_IMAGE);
		this.modelNumber = obj.optString(MODEL_NUMBER);
		this.format = obj.optString(FORMAT);
		this.longDescription = obj.optString(LONG_DESCRIPTION, null);
		if (longDescription == null) { // no long desc
			if (!shortDescription.equals(AppData.JSON_NULL)) {
				this.longDescription = shortDescription; // use short desc
			}
		}
		if (longDescription == null) { // still no long desc
			this.longDescription = obj.optString(PLOT, null); // use plot
		}
		if (longDescription == null) { // still no long desc
			this.longDescription = "No Description Available."; 
		}
		this.mediumImageURL = obj.optString(MEDIUM_IMAGE); 
		this.imageURL = obj.optString(IMAGE, null);
		this.releaseDate = obj.optString(RELEASE_DATE);
		this.inStoreAvailablity = obj.optBoolean(IN_STORE_AVAILABILITY);
		this.onlineAvailability = obj.optBoolean(ONLINE_AVAILABILITY);
		this.url = obj.optString(URL, "Mobile Product URL Not Found.");
		this.esrbRating = obj.optString(ESRB_RATING);
		this.inStorePickup = obj.optBoolean(IN_STORE_PICKUP);
		this.homeDelivery = obj.optBoolean(HOME_DELIVERY);
		this.friendsAndFamilyPickup = obj.optBoolean(FRIENDS_AND_FAMILY_PICKUP);
		this.specialOrder = obj.optBoolean(SPECIAL_ORDER);
		this.orderable = obj.optString(ORDERABLE);
		this.advertisedPriceRestriction = obj.optBoolean(ADVERTISED_PRICE_RESTRICTION);
		this.platform = obj.optString(PLATFORM);
		this.tradeInValue = obj.optString(TRADE_IN_VALUE);
		this.active = obj.optBoolean(ACTIVE);
		this.preowned = obj.optBoolean(PREOWNED);
		this.type = obj.optString(TYPE);
		this.marketPlaceAvailable=obj.optBoolean(MARKET_PLACE_ITEMS);
		this.protectionPlanType = obj.optString(PROTECTION_PLAN_TYPE);
		this.protectionPlanTerm = obj.optString(PROTECTION_PLAN_TERM);

		JSONArray frequentlyPurchasedWithJSON = obj.optJSONArray(FREQUENTLY_PURCHASED_WITH);
		frequentlyPurchasedWith = new ArrayList<String>();
		if(frequentlyPurchasedWithJSON != null){
			for (int i = 0; i < frequentlyPurchasedWithJSON.length(); i++) {
				JSONObject jsonProduct = (JSONObject) frequentlyPurchasedWithJSON.get(i);
				frequentlyPurchasedWith.add(jsonProduct.optString(SKU));
			}
		}
		JSONArray protectionPlansJSON = obj.optJSONArray(PROTECTION_PLANS);
		if(protectionPlansJSON != null){
			protectionPlans = new ArrayList<String>();
			for (int i = 0; i < protectionPlansJSON.length(); i++) {
				JSONObject jsonProduct = (JSONObject) protectionPlansJSON.get(i);
				protectionPlans.add(jsonProduct.optString(SKU));
			}
		}
		JSONArray buybackPlansJSON = obj.optJSONArray(BUYBACK_PLANS);
		if(buybackPlansJSON != null){
			buybackPlans= new ArrayList<String>();
			for (int i = 0; i < buybackPlansJSON.length(); i++) {
				JSONObject jsonProduct = (JSONObject) buybackPlansJSON.get(i);
				buybackPlans.add(jsonProduct.optString(SKU));
			}
		}
		this.categoryPath = new ArrayList<String>();
		JSONArray categoryPathArray = obj.optJSONArray("categoryPath");
		if (categoryPathArray != null && categoryPathArray.length() > 0) {
			JSONObject jsonCategoryPathObj;
			for (int i = 0; i < categoryPathArray.length(); i++) {
				jsonCategoryPathObj = categoryPathArray.optJSONObject(i);
				this.categoryPath.add(jsonCategoryPathObj.optString("id"));
				if (i == 1) {
					this.categoryName = jsonCategoryPathObj.optString("name");
					this.categoryId = jsonCategoryPathObj.optString("id");
				}
			}
		}
		
		//clean up xml chars
		this.name = replaceXMLCharacters(this.name);
		this.title = replaceXMLCharacters(this.title);
		this.shortDescription = replaceXMLCharacters(this.shortDescription);
		this.longDescription = replaceXMLCharacters(this.longDescription);

		loadImages(obj);
		
		loadMarketPlaceDetails();
	}

	private void loadMarketPlaceDetails() {
		if(this.marketPlaceAvailable){
			this.marketPlaceDetails = MarketPlaceLogic.getMarketPlaceDetails(this.sku,this.productId);								
		}
	}
	
	public ArrayList<String> getAllImages() {
		ArrayList<String> retVal = new ArrayList<String>();

		//Get the largeFrontImage first.  If it's not there, check the angleImage which is the same thing.
		boolean addedLargeFrontImage = CheckStringAndAdd(retVal, this.largeFrontImageURL);
		if (!addedLargeFrontImage) {
			boolean addedAngleImage = CheckStringAndAdd(retVal, this.angleImageURL);
			if (!addedAngleImage) {
				CheckStringAndAdd(retVal, getBestImageURL());
			}
		}
		CheckStringAndAdd(retVal, this.accessoriesImageURL);
		CheckStringAndAdd(retVal, this.alternateViewsImageURL);
		CheckStringAndAdd(retVal, this.backViewImageURL);
		CheckStringAndAdd(retVal, this.leftViewImageURL);
		CheckStringAndAdd(retVal, this.remoteControlImageURL);
		CheckStringAndAdd(retVal, this.rightViewImageURL);
		CheckStringAndAdd(retVal, this.topViewImageURL);
		return retVal;
	}

	private boolean CheckStringAndAdd(ArrayList<String> retVal, String url) {
		if (url != null && !url.equalsIgnoreCase(AppData.JSON_NULL) && url.length() > 0) {
			BBYLog.i(TAG, "Adding URL: " + url);
			retVal.add(url);
			return true;
		}
		return false;
	}
	
	public String getAvailabilityText() {
		if (isSpecialOrder()) {
			return "Special Order";
		} else if (isHomeDelivery()) {
			return "Home Delivery";
		} else if (isInStoreAvailablity() && isOnlineAvailability()) {
			return "Store & Online";
		} else if (isInStoreAvailablity() && !isOnlineAvailability()) {
			return "In Store Only";
		} else if (!isInStoreAvailablity() && isOnlineAvailability()) {
			return "Online Only";
		} else if (isSoldOut()) {
			return "Sold Out";
		} else if (isComingSoon()) {
			return "Coming Soon";
		} else if (isMarketPlaceAvailable()) {
			 if(orderable.equals("Available"))
		    	 return "Online Only";
			 else
				 return "No Availability";
		} else {
			return "No Availability";
		}		
	}

	public static String replaceXMLCharacters(String string) {
		if (string != null) {
			string = Html.fromHtml(string).toString();
		}
		return string;
	}

	
	/* IPaginated Implementation */ 
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		Product product = (Product) obj;
		if (sku != null) {
			if (this.sku.equals(product.getSku())) {
				return true;
			}
		}
		return false;
	}
}
