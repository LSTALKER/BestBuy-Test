package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;


public class Area implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String mState;
	private int mZipCode;
	private String mcity;
	
	public String getmState() {
		return mState;
	}
	public void setmState(String mState) {
		this.mState = mState;
	}

	public int getmZipCode() {
		return mZipCode;
	}
	public void setmZipCode(int mZipCode) {
		this.mZipCode = mZipCode;
	}

	public String getMcity() {
		return mcity;
	}
	public void setMcity(String mcity) {
		this.mcity = mcity;
	}

}
