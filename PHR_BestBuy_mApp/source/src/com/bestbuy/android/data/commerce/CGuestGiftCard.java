package com.bestbuy.android.data.commerce;


public class CGuestGiftCard extends CGiftCard {
	private String digits;
	private String pin;
	
	public String getDigits() {
		return digits;
	}
	public void setDigits(String digits) {
		this.digits = digits;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CGuestGiftCard other = (CGuestGiftCard) obj;
		if (digits == null) {
			if (other.digits != null)
				return false;
		} else if (this.hashCode() != other.hashCode())
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return digits.hashCode();
	}
}
