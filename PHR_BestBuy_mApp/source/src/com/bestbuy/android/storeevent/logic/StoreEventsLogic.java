package com.bestbuy.android.storeevent.logic;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.storeevent.data.StoreEventsData;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.util.APIRequestException;

/**
 * The user interface is only depending upon this class to fetch the data from the server.
 * @author lalitkumar_s
 *
 */

public class StoreEventsLogic {
	
	public static List<StoreEvents> getStoreEvents(String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return StoreEventsData.getStoreEvents(storeId);
	}
}
