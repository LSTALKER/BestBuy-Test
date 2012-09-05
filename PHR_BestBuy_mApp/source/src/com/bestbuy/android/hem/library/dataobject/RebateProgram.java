package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;

public class RebateProgram implements Serializable {

	private static final long serialVersionUID = 1L;
	private String amountLabel;
	private String claimFormURL;
	private String homeURL;
	private int id;
	private String[] importantDetails;
	private String name;	
	private PurchaseRebate purchaseRebate;
	private RecyclingRebate recyclingRebate;
	private String[] validDates;
	
	public String getAmountLabel() {
		return amountLabel;
	}
	public void setAmountLabel(String amountLabel) {
		this.amountLabel = amountLabel;
	}
	
	public String getClaimFormURL() {
		return claimFormURL;
	}
	public void setClaimFormURL(String claimFormURL) {
		this.claimFormURL = claimFormURL;
	}

	public String getHomeURL() {
		return homeURL;
	}
	public void setHomeURL(String homeURL) {
		this.homeURL = homeURL;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String[] getImportantDetails() {
		return importantDetails.clone();
	}
	public void setImportantDetails(String[] importantDetails) {
		if(importantDetails!=null) {
			this.importantDetails = importantDetails.clone();
		} else {
			this.importantDetails = new String[0];
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
		
	public PurchaseRebate getPurchaseRebate() {
		return purchaseRebate;
	}
	public void setPurchaseRebate(PurchaseRebate purchaseRebate) {
		this.purchaseRebate = purchaseRebate;
	}

	public RecyclingRebate getRecyclingRebate() {
		return recyclingRebate;
	}
	public void setRecyclingRebate(RecyclingRebate recyclingRebate) {
		this.recyclingRebate = recyclingRebate;
	}

	public String[] getValidDates() {
		return validDates;
	}
	public void setValidDates(String[] validDates) {
		if(validDates!=null) {
			this.validDates = validDates;
		} else {
			this.validDates = new String[0];
		}
	}
}
