package com.bestbuy.android.data;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONObject;
/**
 * Store object that contains information about a Best Buy store
 * @author Recursive Awesome
 *
 */
public class Store implements Serializable{

	private static final long serialVersionUID = 1869305573334095129L;
	
	public static String STORE_ID = "storeId";
	public static String NAME = "name";
	public static String LONG_NAME = "longName";
	public static String ADDRESS = "address";
	public static String CITY = "city";
	public static String REGION = "region";
	public static String POSTAL_CODE = "postalCode";
	public static String COUNTRY = "country ";
	public static String LAT = "lat";
	public static String LNG = "lng";
	public static String PHONE = "phone";
	public static String HOURS = "hours";
	public static String DISTANCE = "distance";
	public static String STORE_IMAGE = "storeimage";
	
	private String storeId;
	private String name;
	private String address;
	private String longName;
	private String city;
	private String region;
	private String postalCode;
	private double lat;
	private double lng;
	private String phone;
	private String hours;
	private String distance;
	private String availabilityMessage;
	private String storeLink;
	private boolean shipToStore;
	private String storeImage;
	
	public String getStoreId() {
		return storeId;
	}
	public String getName() {
		return name;
	}
	public String getLongName() {
		return longName;
	}
	public String getAddress() {
		return address;
	}
	public String getCity() {
		return city;
	}
	public String getRegion() {
		return region;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	public String getPhone() {
		return phone;
	}
	public String getHours() {
		return hours;
	}
	public String getDistance() {
		return distance;
	}
	public String getStoreImage() {
		return storeImage;
	}
	
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setLongName(String longName) {
		this.longName = longName;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setHours(String hours) {
		this.hours = hours;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getAvailabilityMessage() {
		return availabilityMessage;
	}
	public void setAvailabilityMessage(String availabilityMessage) {
		this.availabilityMessage = availabilityMessage;
	}
	public String getStoreLink() {
		return storeLink;
	}
	public void setStoreLink(String storeLink) {
		this.storeLink = storeLink;
	}
	public boolean isShipToStore() {
		return shipToStore;
	}
	public void setShipToStore(boolean shipToStore) {
		this.shipToStore = shipToStore;
	}
	public void setStoreImage(String storeImage) {
		this.storeImage = storeImage;
	}
	private ArrayList<String>  availSkuLists;
	public ArrayList<String> getAvailSkuLists() {
		return availSkuLists;
	}
	public void setAvailSkuLists(ArrayList<String> availSkuLists) {
		this.availSkuLists = availSkuLists;
	}
	public void loadStoreData(JSONObject jsonObject) throws Exception {
		this.storeId = jsonObject.getString(Store.STORE_ID);
		this.name = jsonObject.getString(Store.NAME);
		this.longName = jsonObject.getString(Store.LONG_NAME);
		this.address = jsonObject.getString(Store.ADDRESS);
		this.city = jsonObject.getString(Store.CITY);
		this.region = jsonObject.getString(Store.REGION);
		this.postalCode = jsonObject.getString(Store.POSTAL_CODE);
		this.phone = jsonObject.getString(Store.PHONE);
		this.lat = jsonObject.getDouble(Store.LAT);
		this.lng = jsonObject.getDouble(Store.LNG);
		this.hours = jsonObject.getString(Store.HOURS);
		this.distance = jsonObject.optString(Store.DISTANCE);
		this.storeImage = jsonObject.optString(Store.STORE_IMAGE);
	}
	
	
}
