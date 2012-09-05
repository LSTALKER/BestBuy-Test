package com.bestbuy.android.rewardzone;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.widget.ImageView;

import com.bestbuy.android.util.BBYLog;

/**
 * A single Reward Zone transaction.  Contains general information about
 * the transaction, including information to be displayed on receipts.
 * @author Administrator
 *
 */
abstract public class RZTransaction implements Serializable {

	private static final long serialVersionUID = -9045195491139520622L;
	private String partyId;
	private String source;
	private String type;
	private String key;
	private String location;
	private Date date;
	private ArrayList<RZTransactionLineItem> lineItems = new ArrayList<RZTransactionLineItem>();
	private int transactionType;
	
	
	

	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public ArrayList<RZTransactionLineItem> getLineItems() {
		return lineItems;
	}
	public void addLineItem(RZTransactionLineItem lineItem) {
		this.lineItems.add(lineItem);
	}
	public int getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}
	
	public ArrayList<RZTransactionLineItem> getSaleLineItems() {
		//Return the list of items purchased in the receipt as transactions
		ArrayList<RZTransactionLineItem> result = new ArrayList<RZTransactionLineItem>();
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.get(i).getLineType().equals("SL") &&
					!lineItems.get(i).getSkuPluText().contains("RZ CARD")) {
				result.add(lineItems.get(i));
			}
		}
		return result;
	}
	
	public String getShippingPrice() {
		RZTransactionLineItem lineItem = new RZTransactionLineItem();
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.get(i).getLineType().equals("SH")) {
				lineItem = lineItems.get(i);
			}
		}

     	String shippingPrice = centsToDollars(lineItem.getSalePriceCents());		
		return shippingPrice;
	}
	
	public String getShippingTax() {
		RZTransactionLineItem lineItem = new RZTransactionLineItem();
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.get(i).getLineType().equals("ST")) {
				lineItem = lineItems.get(i);
			}
		}

     	String shippingTax = centsToDollars(lineItem.getSalePriceCents());		
		return shippingTax;
	}

	public String getSalesTax() {
		RZTransactionLineItem lineItem = new RZTransactionLineItem();
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.get(i).getLineType().equals("TX")) {
				lineItem = lineItems.get(i);
			}
		}
     	String salesTax = centsToDollars(lineItem.getSalePriceCents());		
		return salesTax;
	}
	
	public String getSubtotal() {
		BBYLog.e(this.getClass().getName(), "Getting subtotal");
		int amount = 0;
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.get(i).getLineType().equals("SL") ||
				lineItems.get(i).getLineType().equals("SH") ||
				lineItems.get(i).getLineType().equals("ST")) {
				amount += lineItems.get(i).getSalePriceCents();
			}
		}
		BBYLog.e(this.getClass().getName(), "Amount: " + amount);

     	String subtotal = centsToDollars(amount);		
		return subtotal;
	}
	
	public String getTotal() {
		double total = Double.valueOf(getSubtotal()) + Double.valueOf(getSalesTax());
		NumberFormat formatter = new DecimalFormat("0.00");
		return formatter.format(total);
	}
	
	public String centsToDollars(int cents) {
     	NumberFormat formatter = new DecimalFormat("0.00");
     	double input = Double.parseDouble(Integer.toString(cents));
     	String result = formatter.format(input/100);	
     	return result;
	}
	
	public abstract String getName();
	public abstract String getFirstDetail();
	public abstract String getSecondDetail();
	public abstract void setName();
	public abstract void setFirstDetail();
	public abstract void setSecondDetail();
	public abstract void clicked(Activity activity);
	public abstract void fetchImage(ImageView iv);
	

}
