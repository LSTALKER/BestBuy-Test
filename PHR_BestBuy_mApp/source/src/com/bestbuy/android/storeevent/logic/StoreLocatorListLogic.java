package com.bestbuy.android.storeevent.logic;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.data.Store;
import com.bestbuy.android.storeevent.data.StoreLocatorListData;
import com.bestbuy.android.util.APIRequestException;

/**
 * The user interface is only depending upon this class to fetch the data from the server.
 * @author lalitkumar_s
 *
 */

public class StoreLocatorListLogic {
	
	public static List<Store> findNearbyStores(double latitude, double longitude) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return StoreLocatorListData.findNearbyStores(latitude, longitude);
	}
	
	public static List<Store> findNearbyStores(String zipCode) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return StoreLocatorListData.findNearbyStores(zipCode);
	}
	
}
