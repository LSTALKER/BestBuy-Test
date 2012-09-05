package com.bestbuy.android.rewardzone;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.bestbuy.android.util.BBYLog;

/**
 * Parses the XML data for both points and purchases into the corresponding
 * objects.
 * 
 * @author Recursive Awesome
 * 
 */
public class RZParser {

	InputStream input;
	SAXParserFactory spf;
	SAXParser sp;
	XMLReader xr;
	RZPointsParserHandler pointsHandler;
	RZAccount rzAccount;
	boolean accountAlreadyLoaded = false;

	private String TAG = this.getClass().getName();

	public RZParser() {
		rzAccount = new RZAccount();
	}

	public RZParser(RZAccount rzAccount) { // allow for creating a parser from
											// an RZAccount
		this.rzAccount = rzAccount;
	}

	public void parse(InputStream input) {
		// this.input = input;
		spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		try {
			SAXParser sp = spf.newSAXParser();
			xr = sp.getXMLReader();
			pointsHandler = new RZPointsParserHandler();
			xr.setContentHandler(pointsHandler);
			xr.parse(new InputSource(input));
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
		}
	}

	public RZAccount getRzAccount() {
		return rzAccount;
	}

	private class RZPointsParserHandler extends DefaultHandler {
		// Points data
		RZParty rzParty = rzAccount.getRzParty();
		RZCertificate rzCertificate;
		RZAddress rzAddress = new RZAddress(rzParty);
		RZEmail rzEmail = new RZEmail(rzParty);
		RZPhone rzPhone = new RZPhone(rzParty);

		boolean party = false;
		boolean email = false;
		boolean phone = false;
		boolean address = false;
		boolean certificate = false;
		boolean account = false;
		boolean inElement = false;

		// Purchases data
		RZTransaction rzTransaction = new RZReceipt();
		RZTransactionLineItem rzLineItem = new RZTransactionLineItem();

		boolean transaction = false;
		boolean transactionLineItem = false;

		String value;
		DateFormat pointsDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		DateFormat purchasesDf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

		public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
			super.startElement(namespaceURI, localName, qName, atts);
			inElement = true;
			value = new String();
			// Points data
			if (localName.equals("party")) {
				party = true;
			}
			if (localName.equals("email")) {
				email = true;
			}
			if (localName.equals("phone")) {
				phone = true;
			}
			if (localName.equals("address")) {
				address = true;
			}
			if (localName.equals("certificate")) {
				certificate = true;
				rzCertificate = new RZCertificate();
			}
			if (localName.equals("account") && !accountAlreadyLoaded) { // grab
																		// only
																		// the
																		// first
																		// account
																		// for
																		// now.
				account = true;
			}

			if (localName.equals("transaction")) {
				transaction = true;
				rzTransaction = new RZReceipt();
			}
			if (localName.equals("transactionLineItem")) {
				transactionLineItem = true;
				rzLineItem = new RZTransactionLineItem();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);
			inElement = false;
			// Points data
			if (party) {
				if (localName.equals("party")) {
					party = false;
				}
				if (localName.equals("id")) {
					rzAccount.setId(value);
				}
				if (localName.contains("firstName")) {
					String firstName = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
					rzParty.setFirstName(firstName);
				}
				if (localName.equals("lastName")) {
					String lastName = value.toString();
					lastName = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
					rzParty.setLastName(lastName);
				}
				if (localName.equals("languagePreferenceCode")) {
					rzParty.setLanguagePreferenceCode(value);
				}
				rzAccount.setRzParty(rzParty);
			}

			if (email) {
				if (localName.equals("email")) {
					email = false;
					rzParty.getRzEmails().add(rzEmail);
					rzEmail = new RZEmail(rzParty);
				}
				if (localName.equals("id")) {
					rzEmail.setId(value);
				}
				if (localName.equals("typeCode")) {
					rzEmail.setTypeCode(value);
				}
				if (localName.equals("emailAddress")) {
					rzEmail.setEmailAddress(value);
				}
				if (localName.equals("primary")) {
					rzEmail.setPrimary(Boolean.parseBoolean(value));
				}
			}

			if (phone) {
				if (localName.equals("phone")) {
					phone = false;
					rzParty.getRzPhones().add(rzPhone);
					rzPhone = new RZPhone(rzParty);
				}
				if (localName.equals("id")) {
					rzPhone.setId(value);
				}
				if (localName.equals("typeCode")) {
					rzPhone.setTypeCode(value);
				}
				if (localName.equals("countryCode")) {
					rzPhone.setCountryCode(value);
				}
				if (localName.equals("areaCode")) {
					rzPhone.setAreaCode(value);
				}
				if (localName.equals("number")) {
					rzPhone.setNumber(value);
				}
				if (localName.equals("primary")) {
					rzPhone.setPrimary(Boolean.parseBoolean(value));
				}
			}

			if (address) {
				if (localName.equals("address")) {
					address = false;
					rzParty.getRzAddresses().add(rzAddress);
					rzAddress = new RZAddress(rzParty);
				}
				if (localName.equals("id")) {
					rzAddress.setId(value);
				}
				if (localName.equals("typeCode")) {
					rzAddress.setTypeCode(value);
				}
				if (localName.equals("addressLine1")) {
					rzAddress.setAddressLine1(value);
				}
				if (localName.equals("addressLine2")) {
					rzAddress.setAddressLine2(value);
				}
				if (localName.equals("municipality")) {
					rzAddress.setMunicipality(value);
				}
				if (localName.equals("region")) {
					rzAddress.setRegion(value);
				}
				if (localName.equals("postalCode")) {
					rzAddress.setPostalCode(value);
				}
				if (localName.equals("country")) {
					rzAddress.setCountry(value);
				}
				if (localName.equals("primary")) {
					rzAddress.setPrimary(Boolean.parseBoolean(value));
				}
			}

			if (certificate) {
				if (localName.equals("certificate")) {
					certificate = false;
					rzAccount.addRzCertificate(rzCertificate);
				}
				if (localName.equals("id")) {
					rzCertificate.setId(value);
				}
				if (localName.equals("certificateNumber")) {
					rzCertificate.setCertificateNumber(value);
				}
				if (localName.equals("amount")) {
					rzCertificate.setAmount(value);
				}
				if (localName.equals("barcodeNumber")) {
					rzCertificate.setBarcodeNumber(value);
				}
				if (localName.equals("accountId")) {
					rzCertificate.setRzAccount(rzAccount);
				}
				if (localName.equals("issuedDate")) {
					try {
						rzCertificate.setIssuedDate(pointsDf.parse(value));
					} catch (ParseException e) {
						BBYLog.printStackTrace(TAG, e);
					}
				}
				if (localName.equals("expiredDate")) {
					try {
						rzCertificate.setExpiredDate(pointsDf.parse(value));
					} catch (ParseException e) {
						BBYLog.printStackTrace(TAG, e);
					}
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, -1);
					Date yesterday = cal.getTime();
					if (rzCertificate.getExpiredDate() != null && rzCertificate.getExpiredDate().after(yesterday)) {
						rzCertificate.setExpired(false);
					} else {
						rzCertificate.setExpired(true);
					}
				}
				if (localName.equals("status")) {
					if (value.equals("Issued")) {
						rzCertificate.setIssued(true);
					} else {
						rzCertificate.setIssued(false);
					}
				}
			}
			if (account) {
				if (localName.equals("account")) {
					account = false;
				}
				if (localName.equals("id")) {
					rzAccount.setId(value);
				}
				if (localName.equals("statusCode")) {
					rzAccount.setStatusCode(value);
				}
				if (localName.equals("typeCode")) {
					rzAccount.setTypeCode(value);
				}
				if (localName.equals("category")) {
					if (value.equals("RWZ")) {
						accountAlreadyLoaded = true;
					}
					rzAccount.setCategory(value);
				}
				if (localName.equals("categoryDescription")) {
					rzAccount.setCategoryDescription(value);
				}
				if (localName.equals("number")) {
					rzAccount.setNumber(value);
				}
				if (localName.equals("points")) {
					rzAccount.setPoints(value);
				}
				if (localName.equals("tier")) {
					rzAccount.setTier(value);
				}
			}

			// purchases data
			if (transaction) {
				if (localName.equals("transaction")) {
					transaction = false;
					rzAccount.getRzTransactions().add(rzTransaction);
				}
				if (localName.equals("partyId")) {
					rzTransaction.setPartyId(value);
				}
				if (localName.equals("source")) {
					rzTransaction.setSource(value);
				}
				if (localName.equals("type")) {
					rzTransaction.setType(value);
				}
				if (localName.equals("key")) {
					rzTransaction.setKey(value);
				}
				if (localName.equals("location")) {
					rzTransaction.setLocation(value);
				}
				if (localName.equals("date")) {
					try {
						rzTransaction.setDate(purchasesDf.parse(value));
					} catch (ParseException e) {
						BBYLog.printStackTrace(TAG, e);
						BBYLog.w(TAG, "Exception parsing date: " + e.getMessage());
					}
				}
				if (transactionLineItem) {
					// Transaction line items
					if (localName.equals("transactionLineItem")) {
						transactionLineItem = false;
						rzTransaction.getLineItems().add(rzLineItem);
					}
					if (localName.equals("lineNumber")) {
						rzLineItem.setLineNumber(value);
					}
					if (localName.equals("lineType")) {
						rzLineItem.setLineType(value);
					}
					if (localName.equals("skuDescription")) {
						rzLineItem.setSkuDescription(value);
					}
					if (localName.equals("unitQuantity")) {
						rzLineItem.setUnitQuantity(value);
					}
					if (localName.equals("salePriceCents")) {
						rzLineItem.setSalePriceCents(Integer.valueOf(value));
					}
					if (localName.equals("sku")) {
						rzLineItem.setSku(value);
					}
					if (localName.equals("skuPluText")) {
						rzLineItem.setSkuPluText(value);
					}
				}
			}
			value = null;
		}

		public void characters(char ch[], int start, int length) {
			if (inElement) { // only grab the characters if in and element
				if (length > 0) {
					if (value != null) { // might be called a couple of times
						value = value.concat(new String(ch.clone(), start, length)); // append
					} else {
						value = new String(ch.clone(), start, length); // new item
					}
				}
			}
		}
	}
}
