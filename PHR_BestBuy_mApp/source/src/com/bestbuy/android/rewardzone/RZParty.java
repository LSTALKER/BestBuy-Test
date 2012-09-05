package com.bestbuy.android.rewardzone;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all contact information for a Reward Zone member.
 * @author Recursive Awesome
 *
 */
public class RZParty {

	private String id;
	private String firstName;
	private String lastName;
	private String languagePreferenceCode;	
	private List<RZEmail> rzEmails;
	private List<RZPhone> rzPhones;
	private List<RZAddress> rzAddresses;
	private List<RZAccount> rzAccounts;
	
	public RZParty() {
		rzEmails = new ArrayList<RZEmail>();
		rzPhones = new ArrayList<RZPhone>();
		rzAddresses = new ArrayList<RZAddress>();
		rzAccounts = new ArrayList<RZAccount>();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLanguagePreferenceCode() {
		return languagePreferenceCode;
	}
	public void setLanguagePreferenceCode(String languagePreferenceCode) {
		this.languagePreferenceCode = languagePreferenceCode;
	}
	public List<RZEmail> getRzEmails() {
		return rzEmails;
	}
	public void addRzEmail(RZEmail rzEmail) {
		rzEmails.add(rzEmail);
	}
	public List<RZPhone> getRzPhones() {
		return rzPhones;
	}
	public void addRzPhone(RZPhone rzPhone) {
		rzPhones.add(rzPhone);
	}
	public List<RZAddress> getRzAddresses() {
		return rzAddresses;
	}
	public void addRzAddress(RZAddress rzAddress) {
		rzAddresses.add(rzAddress);
	}
	public List<RZAccount> getRzAccounts() {
		return rzAccounts;
	}
	public void addRzAccount(RZAccount rzAccount) {
		rzAccounts.add(rzAccount);
	}
	
	
}
