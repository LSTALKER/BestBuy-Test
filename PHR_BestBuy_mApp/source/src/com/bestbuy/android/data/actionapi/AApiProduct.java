package com.bestbuy.android.data.actionapi;

import java.util.ArrayList;

import com.bestbuy.android.data.Product;

public class AApiProduct extends Product {

	private static final long serialVersionUID = 3503603109273658675L;
	private String skuType;
	private ArrayList<String> userActions;
	
	public String getSkuType(){
		return skuType;
	}
	
	public void setSkuType(String skuType){
		this.skuType = skuType;
	}

	public ArrayList<String> getUserActions() {
		return userActions;
	}

	public void setUserActions(ArrayList<String> userActions) {
		this.userActions = userActions;
	}
	
	
	
}
