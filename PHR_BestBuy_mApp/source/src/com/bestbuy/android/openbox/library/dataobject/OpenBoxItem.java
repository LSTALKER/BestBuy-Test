package com.bestbuy.android.openbox.library.dataobject;

import java.io.Serializable;

public class OpenBoxItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String itemImage;
	private String largeImage;
	private String itemTitle;
	private String regularPrice;
	private String sellingPrice;
	private String detailsAndTerms;
	private int customerReviewCount;
	private String customerReviewAverage;
	private String sku;
	private String url;
	private String modelNumber;
	private String mlowestPrice;
	private String msellingPrice;
	private String productId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCustomerReviewCount() {
		return customerReviewCount;
	}

	public void setCustomerReviewCount(int customerReviewCount) {
		this.customerReviewCount = customerReviewCount;
	}

	public void setCustomerReviewAverage(String customerReviewAverage) {
		this.customerReviewAverage = customerReviewAverage;
	}

	public String getCustomerReviewAverage() {
		return this.customerReviewAverage;
	}
	
	public String getItemImage() {
		return itemImage;
	}

	public void setItemImage(String itemImage) {
		this.itemImage = itemImage;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getRegularPrice() {
		return regularPrice;
	}

	public void setRegularPrice(String regularPrice) {
		this.regularPrice = regularPrice;
	}

	public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public String getDetailsAndTerms() {
		return detailsAndTerms;
	}

	public void setDetailsAndTerms(String detailsAndTerms) {
		this.detailsAndTerms = detailsAndTerms;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getLargeImage() {
		return largeImage;
	}

	public void setLargeImage(String largeImage) {
		this.largeImage = largeImage;
	}

	public String getMlowestPrice() {
		return mlowestPrice;
	}

	public void setMlowestPrice(String mlowestPrice) {
		this.mlowestPrice = mlowestPrice;
	}

	public String getMsellingPrice() {
		return msellingPrice;
	}

	public void setMsellingPrice(String msellingPrice) {
		this.msellingPrice = msellingPrice;
	}
}
