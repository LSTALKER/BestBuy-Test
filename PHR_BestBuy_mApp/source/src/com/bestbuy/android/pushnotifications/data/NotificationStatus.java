package com.bestbuy.android.pushnotifications.data;

import java.util.List;

public class NotificationStatus {
	
	private List<String> categoryList;
	private String status;
	private int statusCode;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}