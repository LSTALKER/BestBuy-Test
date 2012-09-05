package com.bestbuy.android.storeevent.library.dataobject;

import java.io.Serializable;

/**
 * Data object class for Store events.
 * Implements Serializable in order to pass from one activity to another through intent. 
 * @author lalitkumar_s
 *
 */
public class StoreEvents implements Serializable {

	private static final long serialVersionUID = 1L;
	private String storeId;
	private String title;
	private String description;
	private String postDate;
	private String startDate;
	private String endDate;
	private String location;
	
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPostDate() {
		return postDate;
	}
	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
}
