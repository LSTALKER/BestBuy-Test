package com.bestbuy.android.marketplace.library.dataobject;

import java.io.Serializable;

public class MarketPlaceDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private String modelNumber;
	private String skuShortLabel; 
	private String skuId; 
	private String numberOfReviews; 
	private String price;
	private String productId;
	private String ratings;
	private boolean isMarketPlaceSku;
	private String sellerDispName;
	private String displayName;
	private String errorDescription;
	private String sellerId;
	
	public String getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	
	public String getSkuShortLabel() {
		return skuShortLabel;
	}
	public void setSkuShortLabel(String skuShortLabel) {
		this.skuShortLabel = skuShortLabel;
	}
	
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
	
	public String getNumberOfReviews() {
		return numberOfReviews;
	}
	public void setNumberOfReviews(String numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getRatings() {
		return ratings;
	}
	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	
	public boolean isMarketPlaceSku() {
		return isMarketPlaceSku;
	}
	public void setMarketPlaceSku(boolean isMarketPlaceSku) {
		this.isMarketPlaceSku = isMarketPlaceSku;
	}
	
	public String getSellerDispName() {
		return sellerDispName;
	}
	public void setSellerDispName(String sellerDispName) {
		this.sellerDispName = sellerDispName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
}
