package com.bestbuy.android.openbox.library.dataobject;

import java.io.Serializable;

public class OpenBoxItemPrice implements Serializable{

	private static final long serialVersionUID = 1L;
	String sku;
	String lowestPrice;
	String sellingPrice;
	
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getLowestPrice() {
		return lowestPrice;
	}
	public void setLowestPrice(String lowestPrice) {
		this.lowestPrice = lowestPrice;
	}
	public String getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
}
