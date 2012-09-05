package com.bestbuy.android.appolicious.library.dataobject;

import java.io.Serializable;

public class Review implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String rating;
	private String body;
	private String title;
	private long creationTimeStamp;
	private String username;
	
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getCreationTimeStamp() {
		return creationTimeStamp;
	}
	public void setCreationTimeStamp(long creationTimeStamp) {
		this.creationTimeStamp = creationTimeStamp;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	

}
