package com.bestbuy.android.openbox.logic;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.openbox.data.OpenBoxClearanceData;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxScreenObject;
import com.bestbuy.android.openbox.library.webobject.SKUResponseObject;
import com.bestbuy.android.util.APIRequestException;


public class OpenBoxClearanceLogic {

	public static SKUResponseObject getListSKU(String categoryType, String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return OpenBoxClearanceData.getListOfSKU(categoryType, storeId);
	}

	public static OpenBoxScreenObject getAllItems(ArrayList<String> listOfSKU, String category, HashMap<String, OpenBoxItemPrice> priceMap) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return OpenBoxClearanceData.getProducts(listOfSKU, category, priceMap);
	}
}
