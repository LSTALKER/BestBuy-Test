package com.bestbuy.android.openbox.data;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.jsonparser.ProductDetailsParser;
import com.bestbuy.android.openbox.library.jsonparser.ProductJsonParser;
import com.bestbuy.android.openbox.library.webobject.Product;
import com.bestbuy.android.openbox.library.webobject.ResponseProducts;
import com.bestbuy.android.openbox.library.webobject.SKUResponseObject;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;

public class OpenBoxWebToDataConvertion{
	
	public HashMap<String, OpenBoxItemPrice> priceMap = null;
	private int numOfSKU;
	private int masterIterationCount;
	private int startIndex;
	private int endIndex;
	private String sku="";
	private ResponseProducts products= null;
	private int itrationLimit=90;
	private ArrayList<Product> arrayListOfProducts=new ArrayList<Product>();		
	private String itemInfoBaseUrl;
	private String requiredInfo = "sku,name,regularPrice,salePrice,categoryPath,customerReviewCount,customerReviewAverage,mediumImage,largeImage,image,longDescriptionHtml,url,modelNumber,productId";
		
	public SKUResponseObject getAllProductSKU(String categoryType, String storeId) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {

		ArrayList<String> responseList = new ArrayList<String>();
		String totalPages = "";
		String currentPage = "";
		int iteration = 1;
		String actualUrl = "";
		String productType = null;
		InputStream inputStream = null;
		ProductJsonParser parser = new ProductJsonParser();
		
		if (categoryType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)) {
			productType = "openBoxProducts";
		} else if (categoryType.equalsIgnoreCase(StoreUtils.CLEARANCE_ITEMS)) {
			productType = "clearanceProducts";
		}
		
		actualUrl = AppConfig.getRemixURL();

		String tempUrl = actualUrl + "/v1/" + productType + "(storeId%20in("
				+ storeId + "))" + "?format=json&show=all&pageSize=100"
				+ "&apiKey=" + AppConfig.getBestbuyOpenboxAPIKey() + "&page=";
		inputStream = GetProductStream.getInputStream(tempUrl + "1");
		totalPages = parser.getTotalPages(inputStream, productType);
		
		do {
			currentPage = "" + iteration;
			inputStream = GetProductStream
					.getInputStream(tempUrl + currentPage);
			ArrayList<String> skuList = parser.parseJSON(inputStream,
					productType);
			responseList.addAll(skuList);
			if (priceMap == null)
				priceMap = parser.getPriceMap();
			else
				priceMap.putAll(parser.getPriceMap());
			iteration++;
		} while (!currentPage.equals(totalPages));
		
		SKUResponseObject responseObj = new SKUResponseObject();
		responseObj.setSkuList(responseList);
		responseObj.setPriceMap(priceMap);
		
		return responseObj;
	}
	
	public HashMap<String, OpenBoxItemPrice> getPriceMap(){
		return priceMap;
	}
	
	public ResponseProducts getProductArray(ArrayList<String> listOfSKU,String category, HashMap<String, OpenBoxItemPrice> priceMap) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		arrayListOfProducts.clear();
		this.priceMap = priceMap;
		products=getProductInfo(listOfSKU,category);		
		return products;
	}
	
	private ResponseProducts getProductInfo(ArrayList<String> listOfSKU, String category) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {
		String itemDetailUrl = null;
		numOfSKU = listOfSKU.size();
		
		BBYLog.i("Num of SKU's", ""+numOfSKU);	
		
		if (numOfSKU > itrationLimit) {
			masterIterationCount = numOfSKU / itrationLimit;
			if ((numOfSKU % itrationLimit) != 0) {
				++masterIterationCount;
			}
		} else {
			masterIterationCount = 1;
		}
		
		for (int masterInterationIndex = 0; masterInterationIndex < masterIterationCount; masterInterationIndex++) {
			
			if (masterInterationIndex == masterIterationCount - 1) {
				startIndex = itrationLimit * masterInterationIndex;
				endIndex = numOfSKU;
			} else {
				startIndex = itrationLimit * masterInterationIndex;
				endIndex = startIndex + itrationLimit - 1;
				if (endIndex > numOfSKU) {
					endIndex = numOfSKU;
				}
			}
			
			for (; startIndex < endIndex; startIndex++) {
				sku = sku + listOfSKU.get(startIndex) + ",";
			}
			
			if (!sku.equals("") && !sku.equals(" ")) {
				sku = sku.substring(0, sku.lastIndexOf(','));
				itemInfoBaseUrl = AppConfig.getRemixURL();
				itemDetailUrl = itemInfoBaseUrl + "/v1/products" + "(sku%20in("
						+ sku + ")&categoryPath.id=" + category + ")?show="
						+ requiredInfo + "&format=json" + "&pageSize=100"
						+ "&apiKey=" + AppConfig.getBestbuyOpenboxAPIKey();
				
				InputStream inputStream = GetProductStream.getInputStream(itemDetailUrl);
				BBYLog.d("OpenBox Get Product Detail**************=================>", "CAME HERE:"+itemDetailUrl);
				ProductDetailsParser infoParser = new ProductDetailsParser();
				
				ArrayList<Product> productList = infoParser.parseDetails(inputStream, priceMap);
				
				if (productList != null) {
					arrayListOfProducts.addAll(productList);
				}
				sku = "";
			}
		}
		
		ResponseProducts responseProducts = new ResponseProducts();
		responseProducts.setProducts(arrayListOfProducts);

		BBYLog.d("OpenBox Add Product List to Object and Num Of Products**************", ""+arrayListOfProducts.size());		
		return responseProducts;
	}
}