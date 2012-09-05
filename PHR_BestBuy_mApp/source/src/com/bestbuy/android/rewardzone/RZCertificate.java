package com.bestbuy.android.rewardzone;

import java.util.Date;

/**
 * Represents a Reward Zone certificate
 * @author Recursive Awesome
 *
 */
public class RZCertificate {

	private String id;
	private String certificateNumber;
	private String amount;
	private String barcodeNumber;
	private RZAccount rzAccount;
	private Date issuedDate;
	private Date expiredDate;
	private boolean issued;
	private boolean expired;
	private boolean emptyView = false; 
	
	public boolean getEmptyView()
	{
		return emptyView; 
	}
	
	public void setEmptyView(boolean emptyView)
	{
		this.emptyView = emptyView; 
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCertificateNumber() {
		return certificateNumber;
	}
	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBarcodeNumber() {
		if (barcodeNumber.length() == 30) {
			barcodeNumber = 
				barcodeNumber.substring(0, 5) + " " 
				+ barcodeNumber.substring(5, 10) + " "
				+ barcodeNumber.substring(10, 15) + " "
				+ barcodeNumber.substring(15, 20) + " "
				+ barcodeNumber.substring(20, 25) + " "
				+ barcodeNumber.substring(25, 30);
		}
		return barcodeNumber;
	}
	public void setBarcodeNumber(String barcodeNumber) {
		this.barcodeNumber = barcodeNumber;
	}
	public RZAccount getRzAccount() {
		return rzAccount;
	}
	public void setRzAccount(RZAccount rzAccount) {
		this.rzAccount = rzAccount;
	}
	public Date getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}
	public Date getExpiredDate() {
		return expiredDate;
	}
	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
	public boolean isIssued() {
		return issued;
	}
	public void setIssued(boolean issued) {
		this.issued = issued;
	}
	public boolean isExpired() {
		return expired;
	}
	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((certificateNumber == null) ? 0 : certificateNumber
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RZCertificate other = (RZCertificate) obj;
		if (certificateNumber == null) {
			if (other.certificateNumber != null)
				return false;
		} else if (!certificateNumber.equals(other.certificateNumber))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
