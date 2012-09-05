package com.bestbuy.android.hem.library.dataobject;


public class ProductRebateSummaries {
	
	private MaxRebate maxRebate;
	private int siteSKU;
	private int rebateCount;
	private String detailsURI;
	private String detailsHtmlURL;
	
	public MaxRebate getMaxRebate() {
		return maxRebate;
	}

	public void setMaxRebate(MaxRebate maxRebate) {
		this.maxRebate = maxRebate;
	}

	public int getSiteSKU() {
		return siteSKU;
	}

	public void setSiteSKU(int siteSKU) {
		this.siteSKU = siteSKU;
	}

	public int getRebateCount() {
		return rebateCount;
	}

	public void setRebateCount(int rebateCount) {
		this.rebateCount = rebateCount;
	}
	public String getDetailsURI() {
		return detailsURI;
	}

	public void setDetailsURI(String detailsURI) {
		this.detailsURI = detailsURI;
	}

	public String getDetailsHtmlURL() {
		return detailsHtmlURL;
	}

	public void setDetailsHtmlURL(String detailsHtmlURL) {
		this.detailsHtmlURL = detailsHtmlURL;
	}
	
}
