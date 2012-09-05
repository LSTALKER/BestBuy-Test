package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;
	private String brand;
	private String detailsUrl;
	private String efficiencyLevel;
	private String imageUrl;
	private String mfrSKU;
	private String price;
	private String productType;
	private String shortName;
	private String showMfrSku;
	private String sku;
	private String upc;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDetailsUrl() {
		return detailsUrl;
	}

	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}

	public String getEfficiencyLevel() {
		return efficiencyLevel;
	}

	public void setEfficiencyLevel(String efficiencyLevel) {
		this.efficiencyLevel = efficiencyLevel;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getMfrSKU() {
		return mfrSKU;
	}

	public void setMfrSKU(String mfrSKU) {
		this.mfrSKU = mfrSKU;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShowMfrSku() {
		return showMfrSku;
	}

	public void setShowMfrSku(String showMfrSku) {
		this.showMfrSku = showMfrSku;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}
}
