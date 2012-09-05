package com.bestbuy.android.openbox.library.jsonparser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.openbox.library.dataobject.OpenBoxItemPrice;
import com.bestbuy.android.openbox.library.webobject.Category;
import com.bestbuy.android.openbox.library.webobject.Product;

public class ProductDetailsParser {
	private ArrayList<Product> listOfProducts=new ArrayList<Product>();
	public ProductDetailsParser(){
		
	}
	
	public ArrayList<Product> parseDetails(InputStream inputStream, HashMap<String, OpenBoxItemPrice> priceMap) throws Exception{
		StringBuilder sb;
		BufferedReader reader;		
		String line;
		sb=new StringBuilder();			
		reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
		while((line=reader.readLine())!=null){
			sb.append(line);
		}
		reader.close();
		String jsonResponse=sb.toString();			
		JSONObject resObject=new JSONObject(jsonResponse);
		JSONArray productArray=resObject.getJSONArray("products");
		for(int productIndex=0;productIndex<productArray.length();productIndex++){

			Product tempProductObj=new Product();
			JSONObject productObject=productArray.getJSONObject(productIndex);
			if(productObject!=null){
				if(priceMap!=null){
					String sku=productObject.getString("sku");
					OpenBoxItemPrice priceObj=priceMap.get(sku);
					if(priceObj!=null && priceObj.getLowestPrice()!=null && ! priceObj.getLowestPrice().equals("")){							
						Double lowestPrice=Double.parseDouble(priceObj.getLowestPrice());							
						tempProductObj.setRegularPrice(""+lowestPrice);
					}
					else{
						Double regularPrice=Double.parseDouble(productObject.getString("regularPrice"));
						tempProductObj.setRegularPrice(""+regularPrice);
					}
					if(priceObj!=null && priceObj.getSellingPrice()!=null && ! priceObj.getSellingPrice().equals("")){
						Double sellingPrice=Double.parseDouble(priceObj.getSellingPrice());
						tempProductObj.setSalesPrice(""+ sellingPrice);
					}									
				}
				else{
					Double regularPrice=Double.parseDouble(productObject.getString("regularPrice"));
					tempProductObj.setRegularPrice(""+regularPrice);
					Double salesPrice=Double.parseDouble(productObject.getString("salePrice"));
					tempProductObj.setSalesPrice(""+ salesPrice);
				}
				tempProductObj.setName(productObject.getString("name"));
				tempProductObj.setSku(productObject.getString("sku"));
				tempProductObj.setModelNumber(productObject.getString("modelNumber"));									
				tempProductObj.setLongDescriptionHtml(disableHyperLink(productObject.getString("longDescriptionHtml")));
				tempProductObj.setLargeImage(productObject.getString("largeImage"));
				tempProductObj.setImage(productObject.getString("image"));//productObject.getString("image")
				
				tempProductObj.setUrl(productObject.optString("url", ""));
				
				String customerReviewCount=productObject.getString("customerReviewCount");
				if(customerReviewCount.equals("null"))
					customerReviewCount="0";	
				
				tempProductObj.setCustomerReviewAverage(productObject.getString("customerReviewAverage"));	
				
				tempProductObj.setCustomerReviewCount(Integer.parseInt(customerReviewCount));									
				JSONArray categoryArray=productObject.getJSONArray("categoryPath");
				ArrayList<Category> categoryList=new ArrayList<Category>();					
				Category tempCategoryObj=new Category();
				JSONObject categoryObject=categoryArray.getJSONObject(1);
				tempCategoryObj.setName(categoryObject.getString("name"));
				tempCategoryObj.setId(categoryObject.getString("id"));
				categoryList.add(tempCategoryObj);
				tempProductObj.setCategoryPath(categoryList);	
				tempProductObj.setProductId(productObject.getString("productId"));
				
				listOfProducts.add(tempProductObj);
			}				
		}
	
		return listOfProducts;		
	}	
	private String disableHyperLink(String longDescriptionHtml){
		String htmlContent="";		
		htmlContent=longDescriptionHtml.replaceAll("href", "");
		return htmlContent;
	}
}
