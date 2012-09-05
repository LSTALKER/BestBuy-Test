package com.bestbuy.android.openbox.library.webobject;

import java.util.ArrayList;
import java.util.HashMap;

import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;

public class SKUResponseObject {
	private HashMap<String, OpenBoxItemPrice> priceMap;
	private ArrayList<String> skuList;

	public HashMap<String, OpenBoxItemPrice> getPriceMap() {
		return priceMap;
	}

	public void setPriceMap(HashMap<String, OpenBoxItemPrice> priceMap) {
		this.priceMap = priceMap;
	}

	public ArrayList<String> getSkuList() {
		return skuList;
	}

	public void setSkuList(ArrayList<String> skuList) {
		this.skuList = skuList;
	}

}
