package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;
import java.util.ArrayList;


public class ProductRebateDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private MaxRebate maxRebate;
	private String maxRebateAmountLabel;
	private Product product;
	private ArrayList<RebateProgram> rebatePrograms;
	
	private int totalItemCount;
	
	public MaxRebate getMaxRebate() {
		return maxRebate;
	}
	public void setMaxRebate(MaxRebate maxRebate) {
		this.maxRebate = maxRebate;
	}
	
	public String getMaxRebateAmountLabel() {
		return maxRebateAmountLabel;
	}
	public void setMaxRebateAmountLabel(String maxRebateAmountLabel) {
		this.maxRebateAmountLabel = maxRebateAmountLabel;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public ArrayList<RebateProgram> getRebatePrograms() {
		return rebatePrograms;
	}
	public void setRebatePrograms(ArrayList<RebateProgram> rebatePrograms) {
		if(rebatePrograms==null) {
			rebatePrograms = new ArrayList<RebateProgram>();
		} else {
			this.rebatePrograms = rebatePrograms;
		}
	}
	
	public int getTotalItemCount() {
		return totalItemCount;
	}
	public void setTotalItemCount(int totalItemCount) {
		this.totalItemCount = totalItemCount;
	}
	
	
	
	private String siteId;
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
}
