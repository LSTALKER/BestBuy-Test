package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;
import java.util.ArrayList;

public class EcoRebatesResponseDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Area area;
	private int firstItemIdx;
	private int lastItemIdx;
	private int pageCount;
	private int pageIdx;
	private int pageSize;
	private String productDetailsMobileURLBase;
	private String ecrsessionid;
	
	private EcoRebateErrorMsg error;
	
	private ArrayList<ProductRebateDetails> productRebateDetails;
	
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	
	public int getFirstItemIdx() {
		return firstItemIdx;
	}
	public void setFirstItemIdx(int firstItemIdx) {
		this.firstItemIdx = firstItemIdx;
	}
	
	public int getLastItemIdx() {
		return lastItemIdx;
	}
	public void setLastItemIdx(int lastItemIdx) {
		this.lastItemIdx = lastItemIdx;
	}
	
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
	public int getPageIdx() {
		return pageIdx;
	}
	public void setPageIdx(int pageIdx) {
		this.pageIdx = pageIdx;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getProductDetailsMobileURLBase() {
		return productDetailsMobileURLBase;
	}
	public void setProductDetailsMobileURLBase(String productDetailsMobileURLBase) {
		this.productDetailsMobileURLBase = productDetailsMobileURLBase;
	}
	
	public String getEcrsessionid() {
		return ecrsessionid;
	}
	public void setEcrsessionid(String ecrsessionid) {
		this.ecrsessionid = ecrsessionid;
	}
	
	public ArrayList<ProductRebateDetails> getProductRebateDetails() {
		return productRebateDetails;
	}
	public void setProductRebateDetails(ArrayList<ProductRebateDetails> productRebateDetails) {
		if(productRebateDetails==null) {
			this.productRebateDetails = new ArrayList<ProductRebateDetails>();
		} else {
			this.productRebateDetails = productRebateDetails;
		}
	}
	
	public EcoRebateErrorMsg getError() {
		return error;
	}
	public void setError(EcoRebateErrorMsg error) {
		this.error = error;
	}	
	
}
