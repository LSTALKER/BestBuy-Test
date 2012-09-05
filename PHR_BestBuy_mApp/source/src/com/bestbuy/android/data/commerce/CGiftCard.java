package com.bestbuy.android.data.commerce;


public class CGiftCard {
	private String lastFourDigits;
	private String listId;
	private String balance;
	
	public String getLastFourDigits() {
		return lastFourDigits;
	}
	public void setLastFourDigits(String lastFourDigits) {
		this.lastFourDigits = lastFourDigits;
	}
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
}
