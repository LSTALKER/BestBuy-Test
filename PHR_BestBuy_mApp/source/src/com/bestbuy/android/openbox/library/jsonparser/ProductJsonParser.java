package com.bestbuy.android.openbox.library.jsonparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;

public class ProductJsonParser {

	private StringBuilder sb;
	private BufferedReader reader;
	public HashMap<String, OpenBoxItemPrice> priceMap = new HashMap<String, OpenBoxItemPrice>();

	public ProductJsonParser() {

	}

	public ArrayList<String> parseJSON(InputStream inputStream, String productType) throws Exception {
		ArrayList<String> listOfSKU = new ArrayList<String>();

		sb = new StringBuilder();
		String line;
		reader = new BufferedReader(new InputStreamReader(inputStream,
				"UTF-8"));
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonResponse = sb.toString();
		JSONObject jObject = new JSONObject(jsonResponse);
		JSONArray productObject = jObject.getJSONArray(productType);

		for (int index = 0; index < productObject.length(); index++) {
			JSONObject skuObj = productObject.getJSONObject(index);
			listOfSKU.add(skuObj.getString("sku"));
			if (productType.equals("openBoxProducts")) {
				OpenBoxItemPrice price = new OpenBoxItemPrice();
				price.setSku(skuObj.getString("sku"));
				price.setLowestPrice(skuObj.optString("lowestPrice"));
				price.setSellingPrice(skuObj.optString("sellingPrice"));
				priceMap.put(skuObj.getString("sku"), price);
			} else {
				OpenBoxItemPrice price = new OpenBoxItemPrice();
				price.setSku(skuObj.getString("sku"));
				price.setSellingPrice(skuObj.optString("clearancePrice"));
				priceMap.put(skuObj.getString("sku"), price);
			}
		}
	
		return listOfSKU;
	}

	public HashMap<String, OpenBoxItemPrice> getPriceMap() {
		return priceMap;
	}

	public String getTotalPages(InputStream inputStream, String productType) throws Exception {
		String totalPages = "";
		sb = new StringBuilder();
		String line;
		reader = new BufferedReader(new InputStreamReader(inputStream,
				"UTF-8"));
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String jsonResponse = sb.toString();
		JSONObject jObject = new JSONObject(jsonResponse);
		totalPages = jObject.getString("totalPages");
		
		reader.close();
		return totalPages;
	}
}
