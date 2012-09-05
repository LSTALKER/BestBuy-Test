package com.bestbuy.android.rewardzone;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Represents a single line on the transaction receipt.  May correspond
 * to either a purchased item or tax.
 * @author Recursive Awesome
 *
 */
public class RZTransactionLineItem implements Serializable {
	private static final long serialVersionUID = -8486878424992966494L;
	private String transactionId;
	private String lineNumber;
	private String lineType;
	private String skuDescription;
	private String unitQuantity;
	private int salePriceCents;
	private String sku;
	private String skuPluText;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String getLineType() {
		return lineType;
	}
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}
	public String getSkuDescription() {
		return skuDescription;
	}
	public void setSkuDescription(String skuDescription) {
		this.skuDescription = skuDescription;
	}
	public String getUnitQuantity() {
		return unitQuantity;
	}
	public void setUnitQuantity(String unitQuantity) {
		this.unitQuantity = unitQuantity;
	}
	public int getSalePriceCents() {
		return salePriceCents;
	}
	public void setSalePriceCents(int salePriceCents) {
		this.salePriceCents = salePriceCents;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public String getSkuPluText() {
		return skuPluText;
	}
	public void setSkuPluText(String skuPluText) {
		this.skuPluText = skuPluText;
	}
	public String getSalePrice() {
     	NumberFormat formatter = new DecimalFormat("0.00");
     	double cents = Double.parseDouble(Integer.toString(getSalePriceCents()));
     	String output = formatter.format(cents/100);
		return output;
	}
}