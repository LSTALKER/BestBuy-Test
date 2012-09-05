package com.bestbuy.android.data;

import java.io.Serializable;

public class UberCategory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String categoryOption;
	private String categoryValue;
	
	public String getCategoryOption() {
		return categoryOption;
	}
	public void setCategoryOption(String categoryOption) {
		this.categoryOption = categoryOption;
	}
	
	public String getCategoryValue() {
		return categoryValue;
	}
	public void setCategoryValue(String categoryValue) {
		this.categoryValue = categoryValue;
	}
}
