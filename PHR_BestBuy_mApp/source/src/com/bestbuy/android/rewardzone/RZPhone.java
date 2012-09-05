package com.bestbuy.android.rewardzone;

/**
 * Contains phone contact information for a Reward Zone member
 * @author Recursive Awesome
 *
 */
public class RZPhone {

	private String id;
	private RZParty rzParty;
	private String typeCode;
	private String countryCode;
	private String areaCode;
	private String number;
	private boolean primary;
	
	
	public RZPhone(RZParty rzParty) {
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
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	
}
