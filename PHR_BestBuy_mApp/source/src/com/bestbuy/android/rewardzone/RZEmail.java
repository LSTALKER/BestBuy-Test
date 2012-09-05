package com.bestbuy.android.rewardzone;

/**
 * Email information associated with a Reward Zone account
 * @author Recursive Awesome
 *
 */
public class RZEmail {

	private String id;
	private RZParty rzParty;
	private String typeCode;
	private String emailAddress;
	private boolean primary;
	
	
	public RZEmail(RZParty rzParty) {
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
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
	
}
