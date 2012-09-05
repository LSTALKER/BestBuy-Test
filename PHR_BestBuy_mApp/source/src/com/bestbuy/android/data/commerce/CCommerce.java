package com.bestbuy.android.data.commerce;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.SharedPreferences;
import android.util.Xml;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.StoreLocator;

public class CCommerce {
	private final String TAG = this.getClass().getName();
	private String loginLink;
	private String registerLink;
	private String shippingOptionLink;
	private String storesLink;
	private String userLink;
	private String orderLink;
	private String openOrderLink;
	private String logoutLink;
	private String productAvailabilityLink;
	private String giftCardLink;
	private String homeDeliveryDatesLink;
	private String warrantyValidationLink;
	private String guestOrderLink;
	private String guestOrderLookupLink;
	/*private CUser user;
	private COrder order;*/
	private List<CError> cErrors;
	private String emailAddress;
	private String password;

	public CCommerce() {
		/*user = new CUser();
		order = new COrder();*/
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginLink() {
		return loginLink;
	}

	public void setLoginLink(String loginLink) {
		this.loginLink = loginLink;
	}

	public String getRegisterLink() {
		return registerLink;
	}

	public void setRegisterLink(String registerLink) {
		this.registerLink = registerLink;
	}

	public String getShippingOptionLink() {
		return shippingOptionLink;
	}

	public void setShippingOptionLink(String shippingOptionLink) {
		this.shippingOptionLink = shippingOptionLink;
	}

	public String getStoresLink() {
		return storesLink;
	}

	public void setStoresLink(String storesLink) {
		this.storesLink = storesLink;
	}

	public String getUserLink() {
		return userLink;
	}

	public void setUserLink(String userLink) {
		this.userLink = userLink;
	}

	public String getOrderLink() {
		return orderLink;
	}

	public void setOrderLink(String orderLink) {
		this.orderLink = orderLink;
	}

	public String getOpenOrderLink() {
		return openOrderLink;
	}

	public void setOpenOrderLink(String openOrderLink) {
		this.openOrderLink = openOrderLink;
	}

	public String getLogoutLink() {
		return logoutLink;
	}

	public void setLogoutLink(String logoutLink) {
		this.logoutLink = logoutLink;
	}

	/*public CUser getUser() {
		return user;
	}

	public void setUser(CUser user) {
		this.user = user;
	}

	public COrder getOrder() {
		return order;
	}

	public void setOrder(COrder order) {
		this.order = order;
	}*/

	public String getProductAvailabilityLink() {
		return productAvailabilityLink;
	}

	public void setProductAvailabilityLink(String productAvailabilityLink) {
		this.productAvailabilityLink = productAvailabilityLink;
	}

	public String getGiftCardLink() {
		return giftCardLink;
	}

	public void setGiftCardLink(String giftCardLink) {
		this.giftCardLink = giftCardLink;
	}

	public String getHomeDeliveryDatesLink() {
		return homeDeliveryDatesLink;
	}

	public void setHomeDeliveryDatesLink(String homeDeliveryDatesLink) {
		this.homeDeliveryDatesLink = homeDeliveryDatesLink;
	}

	public String getWarrantyValidationLink() {
		return warrantyValidationLink;
	}

	public void setWarrantyValidationLink(String warrantyValidationLink) {
		this.warrantyValidationLink = warrantyValidationLink;
	}

	public String getGuestOrderLink() {
		return guestOrderLink;
	}

	public void setGuestOrderLink(String guestOrderLink) {
		this.guestOrderLink = guestOrderLink;
	}

	public String getGuestOrderLookupLink() {
		return guestOrderLookupLink;
	}

	public void setGuestOrderLookupLink(String guestOrderLookupLink) {
		this.guestOrderLookupLink = guestOrderLookupLink;
	}

	public String getOrderLinkForNumber(String orderNumber) {
		if (openOrderLink == null) {
			return null;
		}
		String orderLink = this.openOrderLink;
		orderLink = openOrderLink.substring(0, openOrderLink.indexOf("/BBY"));
		orderLink += "/" + orderNumber;
		orderLink += openOrderLink.substring(openOrderLink.indexOf("?"), openOrderLink.length());
		return orderLink;
	}

	public List<CError> getErrors() {
		return cErrors;
	}

	public void setErrors(List<CError> cErrors) {
		this.cErrors = cErrors;
	}

	public static List<NameValuePair> getRequestHeaders() {
		List<NameValuePair> requestHeaders = new ArrayList<NameValuePair>();
		requestHeaders.add(new BasicNameValuePair("Content-type", "application/xml; charset=utf-8"));
		requestHeaders.add(new BasicNameValuePair("Accept", "application/xml"));
		return requestHeaders;
	}

	public void login(String username, String password) throws Exception {
		SharedPreferences settings = AppData.getSharedPreferences();
		settings.edit().putString(AppData.COMMERCE_USERNAME, username).commit();

		APIRequest.getClient().getCredentialsProvider().setCredentials(new AuthScope(AppConfig.getCommerceDomain(), 443, AuthScope.ANY_SCHEME), new UsernamePasswordCredentials(username, password));
		parse(getLoginLink());
	}
	
	public void logout() throws Exception {
		//TODO 
		parse(getLogoutLink());
	}

	/*public void buildGuestCart(Activity activity) throws Exception {
		setOpenOrderLink(getGuestOrderLink());
		order.parse(getGuestOrderLink());

		BBYLog.d(TAG, "************ Removing items from cart ************");
		// Delete all items from cart and then add again
		for (int i = 0; i < order.getCart().getCartItems().size(); i++) {
			CCartItem cartItem = order.getCart().getCartItems().get(i);
			order.getCart().removeItem(cartItem);
			BBYLog.e(TAG, "Removing cart item");
		}

		BBYLog.d(TAG, "************ Adding items to cart ************");
		// Add app cart items to commerce cart
		List<CartProductItem> items = CartPersister.getCartProducts(activity).getItems();
		List<CartProductItem> warranties = new ArrayList<CartProductItem>();
		for (int i = 0; i < items.size(); i++) {
			try {
				CartProductItem item = items.get(i);
				if (item.isWarranty()) {
					warranties.add(item);
				} else {
					order.getCart().addItem(item);
					for (Header header : APIRequest.getResponse().getAllHeaders()) {
						if (header.getName().equalsIgnoreCase("location")) {
							order.setLocationHeader(header.getValue());
						}
					}
					order.parse(getGuestOrderLink());
				}
			} catch (APIRequestException apiEx) {
				BBYLog.printStackTrace(TAG, apiEx);
				throw apiEx;
			}
		}
		order.parse(getGuestOrderLink());

		BBYLog.d(TAG, "************ Adding warranty-items to cart ************");

		for (int i = 0; i < warranties.size(); i++) {
			try {
				order.getCart().addWarranty(warranties.get(i), order.getCart().getCartItemBySku(warranties.get(i).getParentSku()).getId());
				order.parse(getGuestOrderLink());
				BBYLog.d(TAG, "ADD TO CART LINK: " + order.getCart().getAddToCartLink());
			} catch (APIRequestException apiEx) {
				BBYLog.printStackTrace(TAG, apiEx);
			}
		}
		if (!warranties.isEmpty()) {
			order.parse(getGuestOrderLink());
		}
	}

	public void buildGuestOrder(Activity activity, CAddress address) {
		new BuildGuestOrderTask(activity, address).execute();
	}

	private class BuildGuestOrderTask extends CommerceAsyncTask {
		String errorMsg;
		CAddress address;

		public BuildGuestOrderTask(Activity activity, CAddress address) {
			super(activity, CCommerce.this);
			this.address = address;
		}

		@Override
		public void doFinish() {
			if (errorMsg != null) {
				BestBuyApplication.showToastNotification(errorMsg, activity, Toast.LENGTH_LONG);
			}
			if (!order.getCart().getCartItems().isEmpty()) {
				order.setGuestShippingAddress(address);
				if (activity.getIntent().hasExtra("Update")) {
					activity.finish();
				} else {
					order.setGuestCreditCard(new CGuestCreditCard());
					order.setBillingAddress(new CAddress());
					Intent i = new Intent(activity, CommerceAddPaymentMethod.class);
					i.putExtra("Guest", true);
					activity.startActivity(i);
					activity.finish();
				}
			} else {
				Intent i = new Intent(activity, CartList.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(i);
			}
		}

		@Override
		public void doError() {
//			AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
//			alertDialog.setTitle("Error");
//			alertDialog.setMessage("One or more of the items from your BestBuy.com cart cannot be purchased on the device app. Please go to BestBuy.com to remove the item(s) from your cart to proceed.");
//			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					return;
//				}
//			});
//			alertDialog.show();
		}

		@Override
		public void doTask() throws Exception {
			List<CCartItem> itemsToRemove = new ArrayList<CCartItem>();
			order.parse(getGuestOrderLink());

			//For all *only* in-store skus, select a default in-store pickup
			for (int i = 0; i < order.getCart().getCartItems().size(); i++) {
				List<CartProductItem> cartProductItems = CartPersister.getCartProducts(activity).getItems();
				CCartItem cartItem = order.getCart().getCartItems().get(i);
				CartProductItem localCartItem = cartItem.getCartProductItem(activity);
				if (!cartItem.isWarrantyItem()) {
					if (localCartItem != null && localCartItem.getStore() != null) {
						//Associate this item with its store
						order.associateInStorePickup(activity, localCartItem.getStore(), cartItem);
						order.parse(getGuestOrderLink());
					} else if (localCartItem != null) {
						BBYLog.d(TAG, "************ Associating in-store pickup ************");
						Product product = localCartItem.getProduct();
						if (product.isInStoreOnly()) {
							String postalCode = address.getPostalCode();
							List<Store> stores = fetchStoreAvailability(cartItem.getSku(), postalCode);
							if (stores.isEmpty()) {
								errorMsg = "Unable to add " + cartItem.getDescription() + ". There are no stores available in your area that have this item in stock.";
								itemsToRemove.add(cartItem);
							} else if (!cartItem.isWarrantyItem()) {
								Store store = stores.get(0);
								order.associateInStorePickup(activity, store, cartItem);
								order.parse(getGuestOrderLink());
							}
						} else if (cartItem.getHomeDeliveryFulfillment() != null) {
							BBYLog.d(TAG, "Adding shipping address to home delivery items");
							Date today = Calendar.getInstance().getTime();
							order.associateHomeDeliveryForItem(cartItem, address.getPhoneNumber(), address.getPhoneNumber(), address, today);
							order.parse(getGuestOrderLink());
						} else {
							fetchLosInfoForAddress(activity, address, cartItem);
							if (cartItem.getShippingOptions() != null) {
								CShippingOption option = cartItem.getShippingOptions().get(0);
								if (option != null) {
									BBYLog.d(TAG, "************ Associating shipping option " + option.getName() + " for sku: " + cartItem.getSku() + "************");
									order.associateShippingOption(address, option.getName(), cartItem);
									order.parse(getGuestOrderLink());
								}
							}
						}
					}
				}
			}

			for (CCartItem cartItem : itemsToRemove) {
				CCartItem currentItem = getOrder().getCart().getCartItemBySku(cartItem.getSku());
				getOrder().getCart().removeItem(currentItem);
				order.parse(getGuestOrderLink());
			}

			order.validateRestrictedItems(activity);

			//try to call checkout on the order
			try {
				order.checkout();
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}
			order.parse(getGuestOrderLink());
		}
	}*/

	public void changePassword(String oldPassword, String newPassword, String confirmPassword) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "user");
		serializer.startTag("", "oldpassword");
		serializer.text(oldPassword);
		serializer.endTag("", "oldpassword");
		serializer.startTag("", "password");
		serializer.text(newPassword);
		serializer.endTag("", "password");
		serializer.startTag("", "confirmpassword");
		serializer.text(confirmPassword);
		serializer.endTag("", "confirmpassword");
		serializer.endTag("", "user");
		serializer.endDocument();
		requestBody = writer.toString();
		APIRequest.makePostRequest(getUserLink(), null, CCommerce.getRequestHeaders(), requestBody);
	}

	/*public void lookupOrder(String orderId, String lastName, String phoneNumber) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "order-query");

		serializer.startTag("", "order-id");
		serializer.text(orderId);
		serializer.endTag("", "order-id");

		serializer.startTag("", "last-name");
		serializer.text(lastName);
		serializer.endTag("", "last-name");

		serializer.startTag("", "phone-number");
		serializer.text(phoneNumber);
		serializer.endTag("", "phone-number");

		serializer.endTag("", "order-query");
		serializer.endDocument();
		requestBody = writer.toString();
		String result = APIRequest.makePostRequest(getGuestOrderLookupLink(), null, CCommerce.getRequestHeaders(), requestBody);
		String url = order.getOrderLookupUrl(result);
		order.parseResult(getOrderDetails(url));
	}*/
	
	public String getOrderDetails(String url) throws Exception{
		return APIRequest.makeGetRequest(url, null, null, null);
	}

	public String checkGiftCardBalance(String number, String pin) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "balance-query");
		serializer.startTag("", "gift-card");
		serializer.attribute("", "card-num", number);
		serializer.attribute("", "pin", pin);
		serializer.endTag("", "gift-card");
		serializer.endTag("", "balance-query");
		serializer.endDocument();
		requestBody = writer.toString();
		String result = APIRequest.makePostRequest(getGiftCardLink(), null, CCommerce.getRequestHeaders(), requestBody);

		// Parse the gift card xml the easy way
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(result));
		int eventType = xpp.getEventType();
		boolean giftCardTag = false;
		float balance = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("gift-card")) {
					giftCardTag = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (xpp.getName().equalsIgnoreCase("gift-card")) {
					giftCardTag = false;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (giftCardTag) {
					balance = Float.parseFloat(xpp.getText());
				}
			}
			eventType = xpp.next();
		}

		// Return the gift card balance
		return String.format("%.2f", balance);
	}

	public void updateRewardZone(String newRewardZoneNumber) throws Exception {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "user");
		serializer.startTag("", "rewardzoneid");
		serializer.text(newRewardZoneNumber);
		serializer.endTag("", "rewardzoneid");
		serializer.endTag("", "user");
		serializer.endDocument();
		requestBody = writer.toString();
		APIRequest.makePostRequest(getUserLink(), null, CCommerce.getRequestHeaders(), requestBody);
	}

	/*public void fetchLosInfoForAddress(Context context, CAddress address, CCartItem cartItem) throws Exception {
		BBYLog.d(TAG, "In store: " + cartItem.getCartProductItem(context).getProduct().isInStoreOnly());
		BBYLog.d(TAG, "address: " + address);
		if (address == null || cartItem.getCartProductItem(context).getProduct().isInStoreOnly()) {
			return;
		}
		// Build request body
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String postData = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "shipping-query");

		serializer.startTag("", "address");

		serializer.startTag("", "address1");
		serializer.text(address.getAddress1());
		serializer.endTag("", "address1");

		serializer.startTag("", "address2");
		if (address.getAddress2() != null) {
			serializer.text(address.getAddress2());
		}
		serializer.endTag("", "address2");

		serializer.startTag("", "city");
		serializer.text(address.getCity());
		serializer.endTag("", "city");

		serializer.startTag("", "state");
		serializer.text(address.getState());
		serializer.endTag("", "state");

		serializer.startTag("", "postalcode");
		serializer.text(address.getPostalCode());
		serializer.endTag("", "postalcode");

		serializer.startTag("", "country");
		serializer.text("United States"); //TODO: Figure out what should go here
		serializer.endTag("", "country");

		serializer.endTag("", "address");
		serializer.startTag("", "item");
		serializer.attribute("", "sku", cartItem.getSku());
		serializer.endTag("", "item");

		serializer.endTag("", "shipping-query");
		serializer.endDocument();
		postData = writer.toString();
		BBYLog.e(TAG, "xml:" + postData);
		BBYLog.e(TAG, "commerce.getShippingOptionLink(): " + getShippingOptionLink());
		String result = APIRequest.makePostRequest(getShippingOptionLink(), null, CCommerce.getRequestHeaders(), postData);
		BBYLog.e(TAG, "Result: " + result);
		cartItem.parse(result);
	}*/

	public List<String> fetchDeliveryDates(String sku, String zip, int quantity) throws Exception {
		if (getHomeDeliveryDatesLink() == null) {
			parseBootstrap();
		}
		// Build request body
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "deliverydate-query");
		serializer.startTag("", "item");
		serializer.attribute("", "sku-id", sku);
		serializer.attribute("", "quantity", String.valueOf(quantity));
		serializer.endTag("", "item");
		serializer.startTag("", "start-date");
		Date today = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		serializer.text(df.format(today));
		serializer.endTag("", "start-date");
		serializer.startTag("", "zipcode");
		serializer.text(zip);
		serializer.endTag("", "zipcode");
		serializer.endTag("", "deliverydate-query");
		serializer.endDocument();
		requestBody = writer.toString();
		String result = APIRequest.makePostRequest(getHomeDeliveryDatesLink(), null, getRequestHeaders(), requestBody);
		return parseDeliveryDates(result);
	}

	public void parseBootstrap() throws Exception {
		if (AppData.getGlobalConfig().get("capiAPIKey") == null) {
			AppData.fetchGlobalConfig();
		}
		Map<String, String> globalConfig = AppData.getGlobalConfig();
		String apiKey = globalConfig.get("capiAPIKey");
		parse(AppConfig.getCommerceStartURL() + "?apiKey=" + apiKey);
	}

	public boolean isWarrantySkuValid(String sku, String state) throws Exception {
		if (getStoresLink() == null) {
			parseBootstrap();
		}
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;

		//Try the new call, if it fails then use the old call
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "warranty-validation");
		serializer.attribute("", "sku-id", sku);
		serializer.attribute("", "state", state);
		serializer.endTag("", "warranty-validation");
		serializer.endDocument();
		requestBody = writer.toString();

		String result = APIRequest.makePostRequest(getWarrantyValidationLink(), null, getRequestHeaders(), requestBody);
		return parseWarranty(result);
	}

	private boolean parseWarranty(String input) throws Exception {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(input));
		int eventType = xpp.getEventType();
		boolean allowed = false;
		boolean warranty = false;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("allowed")) {
					allowed = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (xpp.getName().equalsIgnoreCase("allowed")) {
					allowed = false;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (allowed) {
					warranty = Boolean.parseBoolean(xpp.getText());
				}
			}
			eventType = xpp.next();
		}
		return warranty;
	}

	public List<String> fetchDeliveryDates(String sku, String zip) throws Exception {
		return fetchDeliveryDates(sku, zip, 1);
	}

	public List<Store> fetchStoreAvailability(String sku, String zip) throws Exception {
		return fetchStoreAvailability(sku, zip, 5);
	}

	public List<Store> fetchStoreAvailability(String sku, String zip, int storecount) throws Exception {
		if (this.getStoresLink() == null) {
			parseBootstrap();
		}
		List<Store> stores = new ArrayList<Store>();
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		String requestBody = null;

		//Try the new call, if it fails then use the old call
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "store-query");
		serializer.startTag("", "sku-id");
		serializer.text(sku);
		serializer.endTag("", "sku-id");
		serializer.startTag("", "zipcode");
		serializer.text(zip);
		serializer.endTag("", "zipcode");
		serializer.startTag("", "storecount");
		serializer.text(String.valueOf(storecount));
		serializer.endTag("", "storecount");
		serializer.endTag("", "store-query");
		serializer.endDocument();
		requestBody = writer.toString();
		try {
			String result = APIRequest.makePostRequest(getStoresLink(), null, getRequestHeaders(), requestBody);
			stores = parseStores(result, zip);
			return stores;
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}

		serializer = Xml.newSerializer();
		writer = new StringWriter();
		requestBody = null;
		//Try the old call if the first one failed
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "store-query");
		serializer.startTag("", "skuid");
		serializer.text(sku);
		serializer.endTag("", "skuid");
		serializer.startTag("", "zipcode");
		serializer.text(zip);
		serializer.endTag("", "zipcode");
		serializer.startTag("", "storecount");
		serializer.text(String.valueOf(storecount));
		serializer.endTag("", "storecount");
		serializer.endTag("", "store-query");
		serializer.endDocument();
		requestBody = writer.toString();
		String result = APIRequest.makePostRequest(getStoresLink(), null, getRequestHeaders(), requestBody);
		stores = parseStores(result, zip);
		return stores;
	}

	private List<Store> parseStores(String input, String zip) throws Exception {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(input));
		int eventType = xpp.getEventType();
		boolean store = false;
		List<Store> stores = new ArrayList<Store>();
		Store Store = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("store")) {
					store = true;
				}
				if (store) {
					Store = StoreLocator.findStoreById(xpp.getAttributeValue("", "id"));
					Store.setAvailabilityMessage(xpp.getAttributeValue("", "availabilityMsg"));
					Store.setStoreLink(xpp.getAttributeValue("", "href"));
					Store.setShipToStore(Boolean.parseBoolean(xpp.getAttributeValue("", "ship-to-store")));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (xpp.getName().equalsIgnoreCase("store")) {
					if (!Store.getAvailabilityMessage().equalsIgnoreCase("Not Available")) {
						stores.add(Store);
					}
					store = false;
				}
			}
			eventType = xpp.next();
		}
		return stores;
	}

	private List<String> parseDeliveryDates(String input) throws Exception {
		// Parse the dates the easy way
		List<String> deliveryDates = new ArrayList<String>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(input));
		int eventType = xpp.getEventType();
		boolean availableDate = false;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName().equalsIgnoreCase("deliverydate")) {
					availableDate = true;
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (xpp.getName().equalsIgnoreCase("deliverydate")) {
					availableDate = false;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				if (availableDate) {
					deliveryDates.add(xpp.getText());
				}
			}
			eventType = xpp.next();
		}
		return deliveryDates;
	}

	public void parse(String url) throws Exception {
		try {
			String result = APIRequest.makeGetRequest(url, null, null, true);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();
			XMLReader xmlReader = sp.getXMLReader();
			CommerceParser commerceParser = new CommerceParser();
			xmlReader.setContentHandler(commerceParser);
			xmlReader.parse(new InputSource(new StringReader(result)));
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception getting SAX Parser: " + ex.getMessage());
			throw ex;
		}
	}

	/*public void changeWarranty(Warranty newWarranty, Product parent, String state, Context context) throws Exception {

		COrder order = getOrder();
		CCart cart = order.getCart();
		CCartItem parentItem = cart.getCartItemBySku(parent.getSku());
		if (parentItem != null) {
			for (int i = 0; i < cart.getCartItems().size(); i++) {
				//remove other warranty
				CCartItem cartItem = cart.getCartItems().get(i);
				if (cartItem.isWarrantyItem() && cartItem.getParentItemId().equalsIgnoreCase(parentItem.getId())) {
					cart.removeItem(cartItem);
					CartPersister.removeProducts(cartItem.getSku(), context);
				}
			}

			//add new warranty
			CartProductItem item = CartPersister.addToCart(newWarranty, parent, state, context, false);
			item.setQuantity(Integer.valueOf(parentItem.getQuantity()));
			CartPersister.persist(context);
			cart.addWarranty(item, parentItem.getId());

			order.parse(getOpenOrderLink());
		}
	}*/

	/**
	 * Handles parsing of commerce object
	 * 
	 * @author Recursive Awesome
	 */
	public class CommerceParser extends DefaultHandler {
		boolean inElement = false;
		String value;
		CCommerce commerce = CCommerce.this;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			inElement = true;
			value = new String();
			if (localName.equals("link")) {
				// Bootstrap links
				if (attributes.getValue("rel").equals("login")) {
					commerce.setLoginLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("register")) {
					commerce.setRegisterLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("guest-order")) {
					commerce.setGuestOrderLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("order-lookup")) {
					commerce.setGuestOrderLookupLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("shipping-option")) {
					commerce.setShippingOptionLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("product-availability")) {
					commerce.setProductAvailabilityLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("stores")) {
					commerce.setStoresLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("gift-card")) {
					commerce.setGiftCardLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("home-delivery-dates")) {
					commerce.setHomeDeliveryDatesLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("warranty-validation")) {
					if (commerce.getWarrantyValidationLink() == null) {
						commerce.setWarrantyValidationLink(attributes.getValue("href"));
					}
				}

				// Post-login links
				if (attributes.getValue("rel").equals("user")) {
					commerce.setUserLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("order")) {
					commerce.setOrderLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("open-order")) {
					commerce.setOpenOrderLink(attributes.getValue("href"));
				}
				if (attributes.getValue("rel").equals("logout")) {
					commerce.setLogoutLink(attributes.getValue("href"));
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String name) throws SAXException {
			inElement = false;
			value = null;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
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

	// Adds cart to commerce and sets default shipping/billing info.
	/*public void buildOrder(Activity activity) {
		new BuildOrderTask(activity).execute();
	}

	public class BuildOrderTask extends AsyncTask<Void, Void, Void> {
		boolean isInvalid = false;
		boolean isError = false;
		boolean isUnfinishedBilling = false;
		boolean isUnfinishedShipping = false;
		boolean isAddToCartError = false;
		boolean isTimedOut = false;
		String errorText;
		String shortErrorText;
		Activity activity;
		ProgressDialog dialog;

		public BuildOrderTask(Activity activity) {
			this.activity = activity;
			dialog = new ProgressDialog(activity);
			dialog.setIndeterminate(true);
			dialog.setMessage("Loading order...");
			dialog.setCancelable(true);
		}

		@Override
		protected void onPostExecute(Void result) {
			Diagnostics.StopMethodTracing(activity, TAG, "Building the order took : ");
			if (activity.isFinishing()) {
				return;
			}
			if (isError) {
				if (errorText == null || errorText.length() == 0) {
					errorText = "Error signing in.";
				}
				BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
			} else if (isInvalid) {
				String errorMessage = CErrorCodesHelper.getDesc("LOGIN_FAILED");
				// Check if email address is valid
				EditText emailField = (EditText) activity.findViewById(R.id.commerce_sign_in_email_address);
				String email = emailField.getText().toString();
				if (!email.matches(".+@.+\\.[a-z]+")) {
					errorMessage = "Please enter a valid e-mail address in this format: yourname@domain.com";
				}
				BestBuyApplication.showToastNotification(errorMessage, activity, Toast.LENGTH_LONG);
			} else if (isTimedOut) {
				getOrder().reset();
				getUser().reset();
				String errorText = "Your session timed out.  Please log in to continue.";
				BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
				Intent i = new Intent(activity, CommerceSignIn.class);
				activity.startActivity(i);
			} else if (isUnfinishedShipping) {
				Intent i = new Intent(activity, CommerceAddNewShippingAddress.class);
				activity.startActivity(i);
			} else if (isUnfinishedBilling) {
				Intent i = new Intent(activity, CommerceAddPaymentMethod.class);
				activity.startActivity(i);
			} else if (isAddToCartError) {
				if(activity == null || activity.isFinishing()) { // Use ||, Do not use | 
					return;
				}
				AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage("One or more of the items from your BestBuy.com cart cannot be purchased on the device app. Please go to BestBuy.com to remove the item(s) from your cart to proceed.");
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				alertDialog.show();
			} else {
				EventsLogging.fireAndForget(EventsLogging.CHECKOUT_LOGIN_PATH, null);
				Intent i = new Intent(activity, CommerceCheckout.class);
				activity.startActivity(i);
				activity.finish();
			}
			dialog.dismiss();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				COrder order = getOrder();
				CUser user = getUser();

				user.parse(getUserLink());
				order.parse(getOpenOrderLink());
				
				order.setRestrictedItemsValidated(false);

				// Load error codes
				String capiErrorsURL = AppData.getGlobalConfig().get("capiErrors2URL");
				String errorsJSON = APIRequest.makeGetRequest(capiErrorsURL, null, null, false);
				CErrorCodesHelper.loadErrorCodes(errorsJSON);

				BBYLog.d(TAG, "************ Checking user credit cards ************");
				// TODO: check user, not order, to determine if account creation
				// finished
				if (getUser().getCreditCards().isEmpty()) {
					// User did not finish creating an account
					isUnfinishedBilling = true;
					return null;
				}

				BBYLog.d(TAG, "************ Checking user shipping address ************");
				if (getUser().getShippingAddresses().isEmpty() && getUser().getBillingAddresses().isEmpty()) {
					// User did not finish creating an account
					isUnfinishedShipping = true;
					return null;
				}

				BBYLog.d(TAG, "************ Removing items from cart ************");
				// Delete all items from cart and then add again
				CCart cart = order.getCart();
				for (int i = 0; i < cart.getCartItems().size(); i++) {
					CCartItem cartItem = cart.getCartItems().get(i);
					cart.removeItem(cartItem);
					BBYLog.e(TAG, "Removing cart item");
				}

				BBYLog.d(TAG, "************ Adding items to cart ************");
				// Add app cart items to commerce cart
				List<CartProductItem> items = CartPersister.getCartProducts(activity).getItems();
				List<CartProductItem> warranties = new ArrayList<CartProductItem>();
				for (int i = 0; i < items.size(); i++) {
					try {
						CartProductItem item = items.get(i);
						if (item.isWarranty()) {
							warranties.add(item);
						} else {
							cart.addItem(item);
						}

					} catch (APIRequestException apiEx) {
						BBYLog.printStackTrace(TAG, apiEx);
						isAddToCartError = true;
						return null;
					}
				}
				order.parse(getOpenOrderLink());

				BBYLog.d(TAG, "************ Adding warranty-items to cart ************");
				cart = order.getCart();

				for (int i = 0; i < warranties.size(); i++) {
					try {
						cart.addWarranty(warranties.get(i), cart.getCartItemBySku(warranties.get(i).getParentSku()).getId());
					} catch (APIRequestException apiEx) {
						BBYLog.printStackTrace(TAG, apiEx);
						isAddToCartError = true;
						return null;
					}
				}
				//TODO: Conditionally parse here again only if warranty items are found.
				order.parse(getOpenOrderLink());

				// Add default credit card if there is not one associated. If there is one associated,
				// set it as the default card.
				if (getOrder().getCreditCard() == null) {
					BBYLog.d(TAG, "************ Adding default credit card ************");
					CCreditCard creditCard = getUser().getCreditCards().get(0);
					getOrder().associateBillingInfo(creditCard);
					getUser().updateCreditCardDefault(creditCard, true);
				} else {
					CCreditCard orderCard = getOrder().getCreditCard();
					CCreditCard userCard = null;
					//Find the credit card on order
					for (CCreditCard creditCard : getUser().getCreditCards()) {
						if (creditCard.matchesCard(orderCard)) {
							userCard = creditCard;
							break;
						}
					}
					if (userCard != null) {
						getUser().updateCreditCardDefault(userCard, true);
					}
				}

				// For all *only* in-store skus, select a default in-store pickup
				BBYLog.d(TAG, "************ Associating in-store pickup for skus ************");
				for (int i = 0; i < order.getCart().getCartItems().size(); i++) {
					List<CartProductItem> cartProductItems = CartPersister.getCartProducts(activity).getItems();
					CCartItem cartItem = order.getCart().getCartItems().get(i);
					CartProductItem localCartItem = cartItem.getCartProductItem(activity);
					if (localCartItem != null && localCartItem.getStore() != null) {
						//Associate this item with its store
						order.associateInStorePickup(activity, localCartItem.getStore(), cartItem);
					} else {
						Product product = localCartItem.getProduct();
						if (product.isInStoreOnly()) {
							String postalCode = user.getDefaultShippingAddress().getPostalCode();
							List<Store> stores = fetchStoreAvailability(cartItem.getSku(), postalCode);
							if (stores.isEmpty()) {
								errorText = "Unable to add " + cartItem.getDescription() + ". There are no stores available in your area that have this item in stock.";
								//								BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
								getOrder().getCart().removeItem(cartItem);
							} else if (!cartItem.isWarrantyItem()) {
								Store store = stores.get(0);
								order.associateInStorePickup(activity, store, cartItem);
							}
						}
					}
				}

				// If the user only has a billing address, make it a shipping
				// address too.
				if (getUser().getShippingAddresses().isEmpty() && getUser().getDefaultBillingAddress() != null) {
					getUser().addShippingAddress(getUser().getDefaultBillingAddress());
				}
				user.parse(getUserLink());
				order.parse(getOpenOrderLink());

				// Add default shipping address to order -- order address might
				// be null after a submit call
				if (order.getShippingAddress() == null && getUser().getDefaultShippingAddress() != null) {
					BBYLog.d(TAG, "************ Adding default shipping address to order ************");
					order.associateShippingAddressFromUser(getUser().getDefaultShippingAddress());
				}
				order.parse(getOpenOrderLink());

				// Get the shipping address in the user object that corresponds
				// to the shipping address in order
				CAddress userShippingAddress = null;
				CAddress orderShippingAddress = getOrder().getShippingAddress();
				if (orderShippingAddress != null) {
					for (int j = 0; j < getUser().getShippingAddresses().size(); j++) {
						CAddress userAddr = getUser().getShippingAddresses().get(j);
						if (orderShippingAddress.matchesAddress(userAddr)) {
							userShippingAddress = userAddr;
						}
					}
				}

				// Associate default shipping address to any home delivery items
				// and associate first shipping option to any shipping items
				for (int i = 0; i < order.getCart().getCartItems().size(); i++) {
					CCartItem cartItem = order.getCart().getCartItems().get(i);
					if (cartItem.getAddressFulfillment() != null && userShippingAddress != null && !cartItem.isInStoreOnly(activity) && !cartItem.isWarrantyItem()) {
						CCommerce.this.fetchLosInfoForAddress(activity, getOrder().getShippingAddress(), cartItem);
						int lastOptionIndex = cartItem.getShippingOptions().size() - 1;
						getOrder().associateShippingOption(userShippingAddress, cartItem.getShippingOptions().get(lastOptionIndex).getName(), cartItem);
					}
					if (cartItem.getHomeDeliveryFulfillment() != null && !cartItem.isWarrantyItem()) {
						BBYLog.d(TAG, "Adding shipping address to home delivery items");
						CAddress address = getUser().getDefaultShippingAddress();
						Date today = Calendar.getInstance().getTime();
						order.associateHomeDeliveryForItem(cartItem, address.getPhoneNumber(), address.getPhoneNumber(), address, today);
					}
				}

				order.validateRestrictedItems(activity);

				// call checkout on the order
				order.checkout();

				order.parse(getOpenOrderLink());

			} catch (APIRequestException apiEx) {
				BBYLog.printStackTrace(TAG, apiEx);
				if (apiEx.getErrors() == null) {
					// HTML came back, not XML
					if (apiEx.getResponseBody().contains("Bad credentials")) {
						isInvalid = true;
					} else if (apiEx.getStatusCode() == 403) {
						//session timed out
						isTimedOut = true;
					} else {
						isError = true;
					}
				} else {
					//cErrors = apiEx.getErrors();
					try {
						getOrder().parse(getOpenOrderLink());
					} catch (Exception e) {
						BBYLog.printStackTrace(TAG, e);
					}
				}
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				isError = true;
			}
			return null;
		}

		protected void onPreExecute() {
			if (activity.isFinishing()) {
				return;
			}
			Diagnostics.StartMethodTracing(activity);
			dialog.show();
		}
	}*/
}
