package com.bestbuy.android.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class BestBuyAlert {

	private String id;
	private Date startDate;
	private Date endDate;
	private Date createdAt;
	private Date updatedAt;
	private String title;
	private String body;
	private String url;
	private String listImageURL;
	private String displayImageURL;
	private boolean global;
	private List<Store> stores;
	private boolean displayed;
	
	static final Comparator<Store> STORE_DISTANCE_SORT_ORDER =
        new Comparator<Store>() {
		public int compare(Store s1, Store s2) {
			double d1 = Double.parseDouble(s1.getDistance());
			double d2 = Double.parseDouble(s2.getDistance());
			if (d1 < d2) {
				return 1;
			} else if ( d2 > d1) {
				return -1;
			} else {
				return 0;
			}
		}
	};
	
	public BestBuyAlert(String id, Date startDate, Date endDate, String title, String body, String url,String listImageURL,String displayImageURL, boolean global, Date createdAt, Date updatedAt) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
		this.body = body;
		this.url = url;
		this.listImageURL = listImageURL;
		this.displayImageURL = displayImageURL;
		this.global = global;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	public String getId() {
		return id;
	}
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate(){
		return endDate;
	}
	
	public Date getCreateAt(){
		return createdAt;
	}
	
	public Date getUpdatedAt(){
		return updatedAt;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getBody() {
		return body;
	}
	public String getUrl() {
		return url;
	}
	
	public String getListImageURL(){
		return listImageURL;
	}
	
	public String getDisplayImageURL(){
		return displayImageURL;
	}
	
	public boolean isGlobal() {
		return global;
	}
	public void setDisplayed(boolean displayed){
		this.displayed = displayed;
	}
	public boolean isDisplayed() {
		return displayed;
	}
	
	public void addStore(Store store) {
		if (stores == null) {
			stores = new ArrayList<Store>();
		}
		stores.add(store);
	}
	
	public Store getClosestStore() {
		Store result= null;
		if (stores != null) {
			Collections.sort(stores, STORE_DISTANCE_SORT_ORDER);
			result = stores.get(0);
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BestBuyAlert other = (BestBuyAlert) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	
	
}
