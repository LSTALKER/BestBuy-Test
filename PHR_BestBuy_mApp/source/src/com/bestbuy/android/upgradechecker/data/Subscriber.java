package com.bestbuy.android.upgradechecker.data;

import java.io.Serializable;

public class Subscriber implements Serializable{
	
	private String mobilePhoneNumber;
	private String zip;
	private String contractEndDate;
	private boolean upgradeEligibilityFlag;
    private String upgradeEligibilityDate;
    private String upgradeEligibilityType;
    private String upgradeEligibilityMessage;
    private String upgradeEligibilityFootnote;
    private String earlyTerminationWarning;
    private String optInAllowed;
    private String tradeInMessage;
    private String tradeInValue;
    private String tradeInPhoneName;
    private String imei;
	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}
	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}
	public String getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(String contractEndDate) {
		this.contractEndDate = contractEndDate;
	}
	public boolean isUpgradeEligibilityFlag() {
		return upgradeEligibilityFlag;
	}
	public void setUpgradeEligibilityFlag(boolean upgradeEligibilityFlag) {
		this.upgradeEligibilityFlag = upgradeEligibilityFlag;
	}
	public String getUpgradeEligibilityDate() {
		return upgradeEligibilityDate;
	}
	public void setUpgradeEligibilityDate(String upgradeEligibilityDate) {
		this.upgradeEligibilityDate = upgradeEligibilityDate;
	}
	public String getUpgradeEligibilityType() {
		return upgradeEligibilityType;
	}
	public void setUpgradeEligibilityType(String upgradeEligibilityType) {
		this.upgradeEligibilityType = upgradeEligibilityType;
	}
	public String getUpgradeEligibilityMessage() {
		return upgradeEligibilityMessage;
	}
	public void setUpgradeEligibilityMessage(String upgradeEligibilityMessage) {
		this.upgradeEligibilityMessage = upgradeEligibilityMessage;
	}
	public String getUpgradeEligibilityFootnote() {
		return upgradeEligibilityFootnote;
	}
	public void setUpgradeEligibilityFootnote(String upgradeEligibilityFootnote) {
		this.upgradeEligibilityFootnote = upgradeEligibilityFootnote;
	}
	public String getEarlyTerminationWarning() {
		return earlyTerminationWarning;
	}
	public void setEarlyTerminationWarning(String earlyTerminationWarning) {
		this.earlyTerminationWarning = earlyTerminationWarning;
	}
	public String getOptInAllowed() {
		return optInAllowed;
	}
	public void setOptInAllowed(String optInAllowed) {
		this.optInAllowed = optInAllowed;
	}
	public String getTradeInMessage() {
		return tradeInMessage;
	}
	public void setTradeInMessage(String tradeInMessage) {
		this.tradeInMessage = tradeInMessage;
	}
	public String getTradeInValue() {
		return tradeInValue;
	}
	public void setTradeInValue(String tradeInValue) {
		this.tradeInValue = tradeInValue;
	}
	public String getTradeInPhoneName() {
		return tradeInPhoneName;
	}
	public void setTradeInPhoneName(String tradeInPhoneName) {
		this.tradeInPhoneName = tradeInPhoneName;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
}
