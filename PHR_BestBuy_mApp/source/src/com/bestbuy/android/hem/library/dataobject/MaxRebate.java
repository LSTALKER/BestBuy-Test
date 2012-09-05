package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;


public class MaxRebate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String amount;
	private String units;

	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
	
}
