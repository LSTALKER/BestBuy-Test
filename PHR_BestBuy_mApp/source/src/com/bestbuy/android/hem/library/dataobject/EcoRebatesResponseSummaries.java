package com.bestbuy.android.hem.library.dataobject;

import java.util.ArrayList;



public class EcoRebatesResponseSummaries {
	
	private String mSiteId;
	private Area mArea;
	private int pageSize;
	private int pageCount;
	private int pageIdx;
	private int totalItemCount;
	private int firstItemIdx;
	private int lastItemIdx;
	private ArrayList<ProductRebateSummaries> mProductRebateSummaries;
	
	public String getmSiteId() {
		return mSiteId;
	}
	public void setmSiteId(String mSiteId) {
		this.mSiteId = mSiteId;
	}
	public Area getmArea() {
		return mArea;
	}
	public void setmArea(Area mArea) {
		this.mArea = mArea;
	}
	public ArrayList<ProductRebateSummaries> getmProductRebateSummaries() {
		return mProductRebateSummaries;
	}
	public void setmProductRebateSummaries(ArrayList<ProductRebateSummaries> mProductRebateSummaries) {
		this.mProductRebateSummaries = mProductRebateSummaries;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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
	public int getTotalItemCount() {
		return totalItemCount;
	}
	public void setTotalItemCount(int totalItemCount) {
		this.totalItemCount = totalItemCount;
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
	
}
