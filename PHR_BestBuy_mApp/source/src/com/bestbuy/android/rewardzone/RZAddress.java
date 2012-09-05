package com.bestbuy.android.rewardzone;

/**
 * Address object associated with a Reward Zone account
 * @author Administrator
 *
 */
public class RZAddress {

	private String id;
	private RZParty rzParty;
	private String typeCode;
	private String addressLine1;
	private String addressLine2;
	private String municipality;
	private String region;
	private String postalCode;
	private String country;
	private boolean primary;
	
	public RZAddress(RZParty rzParty) {
		this.rzParty = rzParty;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public RZParty getRzParty() {
		return rzParty;
	}
	public void setRzParty(RZParty rzParty) {
		this.rzParty = rzParty;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getMunicipality() {
		return municipality;
	}
	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	
}
