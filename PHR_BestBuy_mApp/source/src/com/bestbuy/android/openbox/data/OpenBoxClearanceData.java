package com.bestbuy.android.openbox.data;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxScreenObject;
import com.bestbuy.android.openbox.library.webobject.Product;
import com.bestbuy.android.openbox.library.webobject.ResponseProducts;
import com.bestbuy.android.openbox.library.webobject.SKUResponseObject;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.UTFCharacters.UTF;

public class OpenBoxClearanceData {
	public static SKUResponseObject getListOfSKU(String categoryType, String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		return new OpenBoxWebToDataConvertion().getAllProductSKU(categoryType, storeId);
	}
	public static OpenBoxScreenObject getProducts(ArrayList<String> listOfSKU,String category, HashMap<String, OpenBoxItemPrice> priceMap) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {		
		ResponseProducts responseProduct = new OpenBoxWebToDataConvertion().getProductArray(listOfSKU,category,priceMap);		
		OpenBoxScreenObject screenObject = categorizeProducts(responseProduct.getProducts());              
		return screenObject;
	}
	private static OpenBoxScreenObject categorizeProducts(ArrayList<Product> productList) {
		ArrayList<OpenBoxItem> collectionItems = new ArrayList<OpenBoxItem>();
		OpenBoxScreenObject screenObject = new OpenBoxScreenObject();
		for(Product p: productList){
			if(p!=null){
				OpenBoxItem singleItem = convertProductToOpenBoxItem(p);
				collectionItems.add(singleItem);
			}
		}		
		screenObject.setOpenBoxItems(collectionItems);
		return screenObject;
	}

	private static OpenBoxItem convertProductToOpenBoxItem(Product p) {
		OpenBoxItem item = new OpenBoxItem();
		item.setSellingPrice(p.getSalesPrice());
		item.setDetailsAndTerms(htmlEncoding(p.getLongDescriptionHtml()));
		item.setItemImage(p.getImage());
		item.setLargeImage(p.getLargeImage());
		item.setItemTitle(UTF.replaceNonUTFCharacters(p.getName()));
		item.setRegularPrice(p.getRegularPrice());
		item.setCustomerReviewCount(p.getCustomerReviewCount());
		item.setCustomerReviewAverage(p.getCustomerReviewAverage());
		item.setSku(p.getSku());
		item.setUrl(p.getUrl());
		item.setModelNumber(p.getModelNumber());
		item.setProductId(p.getProductId());
		return item;
	}
	private static String htmlEncoding(String htmlContent){
		String responseContent=null;
		responseContent=htmlContent.replaceAll("%", "&#37;").trim();
		/*responseContent=responseContent.replaceAll("<", "&#60;");
		responseContent=responseContent.replaceAll(">", "&#62;");
		responseContent=responseContent.replaceAll("#", "&#35;");*/
		return responseContent;
	}
}
