package com.bestbuy.android.data.actionapi;

import java.util.ArrayList;
import java.util.Date;

public class AApiLifecycle {
	
	private String category;
	private Date inceptionDate;
	private ArrayList<AApiPhase> phases;
	private String storeId;
	private String name;
	

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setInceptionDate(Date inceptionDate) {
		this.inceptionDate = inceptionDate;
	}

	public Date getInceptionDate() {
		return inceptionDate;
	}
	
	public ArrayList<AApiPhase> getPhases() {
		return phases;
	}

	public void setPhases(ArrayList<AApiPhase> phases) {
		this.phases = phases;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
