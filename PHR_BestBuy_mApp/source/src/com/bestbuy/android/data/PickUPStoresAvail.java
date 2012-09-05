package com.bestbuy.android.data;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Store object that contains information about a Best Buy store
 * @author Recursive Awesome
 *
 */
public class PickUPStoresAvail implements Serializable{

	private static final long serialVersionUID = 1869305573334095129L;
	
	public static String STORE_ID = "storeId";
	public static String NAME = "name";
	
	
	
	private String storename;
	private ArrayList<String> skuList;
	
	
	public String getName() {
		return storename;
	}
	
	public void setName(String name) {
		this.storename = name;
	}
	
	public ArrayList<String> getSkuList() {
		return skuList;
	}
	
	public void setSKUList(ArrayList<String> name) {
		this.skuList = name;
	}
}
