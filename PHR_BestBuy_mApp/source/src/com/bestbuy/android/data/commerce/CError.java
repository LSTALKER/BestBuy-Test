package com.bestbuy.android.data.commerce;

public class CError {

	//Error Types used to direct error on Commerce Checkout
	public static final byte SHIPPING = 1;
	public static final byte PAYMENT = 2;

	private String message;
	private String code;
	private String shortMessage;
	private byte type;
	private String rawResult;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getRawResult() {
		return rawResult;
	}
	public void setRawResult(String rawResult) {
		this.rawResult = rawResult;
	}

	public String getShortMessage() {
		return shortMessage;
	}
	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
}
