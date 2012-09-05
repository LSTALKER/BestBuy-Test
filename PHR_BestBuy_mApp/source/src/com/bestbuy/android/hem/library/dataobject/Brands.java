package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;


public class Brands implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String id;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

}
