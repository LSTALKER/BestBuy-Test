package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;

public class RecyclingRebate implements Serializable {

	private static final long serialVersionUID = 1L;
	private int amount;
	private String units;
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
}
