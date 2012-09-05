package com.bestbuy.android.data;

import java.io.Serializable;

public class Facet implements Serializable{

	private static final long serialVersionUID = -3373413411478967806L;
	private String count;
	private String name;
	private String query;
	
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	

}
