package com.bestbuy.android.rewardzone;

import java.util.ArrayList;

/**
 * Represents a member's Reward Zone account. Includes points and purchases
 * information.
 * 
 * @author Recursive Awesome
 * 
 */
public class RZAccount {

	private String id;
	private RZParty rzParty;
	private String statusCode;
	private String typeCode;
	private String category;
	private String categoryDescription;
	private String number;
	private String points;
	private String tier;
	private ArrayList<RZCertificate> rzCertificates;
	private ArrayList<RZTransaction> rzTransactions;

	public RZAccount() {
		rzCertificates = new ArrayList<RZCertificate>();
		rzParty = new RZParty();

	}

	public boolean isSilverStatus() {
		if (this.getTier() != null) {
			if (this.getTier().length() > 0
					&& this.getTier().contains("SILVER")) {
				return true;
			}
		}
		return false;
	}
	
	public String getStatusDisplay() {
		if (isSilverStatus()) {
			return "silver";
		} else {
			return "core";
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RZParty getRzParty() {
		if (rzParty == null) {
			rzParty = new RZParty();
		}
		return rzParty;
	}

	public void setRzParty(RZParty rzParty) {
		this.rzParty = rzParty;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategoryDescription() {
		return categoryDescription;
	}

	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public ArrayList<RZCertificate> getRzCertificates() {
		return rzCertificates;
	}

	public ArrayList<RZCertificate> getAvailableCertificates() {
		ArrayList<RZCertificate> availableCerts = new ArrayList<RZCertificate>();
		for (int i = 0; i < rzCertificates.size(); i++) {
			RZCertificate certificate = rzCertificates.get(i);
			if (certificate.isIssued() && !certificate.isExpired()) {
				availableCerts.add(certificate);
			}
		}
		return availableCerts;
	}

	public void addRzCertificate(RZCertificate rzCertificate) {
		this.rzCertificates.add(rzCertificate);
	}

	public String getTotal() {
		int total = 0;
		for (int i = 0; i < rzCertificates.size(); i++) {
			RZCertificate cert = rzCertificates.get(i);
			if (cert.isIssued() && !cert.isExpired()) {
				total += Integer.valueOf(cert.getAmount());
			}
		}
		return String.valueOf(total);
	}

	public ArrayList<RZTransaction> getRzTransactions() {
		if (rzTransactions == null) {
			rzTransactions = new ArrayList<RZTransaction>();
		}
		return rzTransactions;
	}

	public void setRzTransactions(ArrayList<RZTransaction> rzTransactions) {
		this.rzTransactions = rzTransactions;
	}

	@Override
	public String toString() {
		return "RZAccount [id=" + id + ", rzParty=" + rzParty + ", statusCode="
				+ statusCode + ", typeCode=" + typeCode + ", category="
				+ category + ", categoryDescription=" + categoryDescription
				+ ", number=" + number + ", points=" + points + ", tier="
				+ tier + ", rzCertificates=" + rzCertificates
				+ ", rzTransactions=" + rzTransactions + "]";
	}

}
