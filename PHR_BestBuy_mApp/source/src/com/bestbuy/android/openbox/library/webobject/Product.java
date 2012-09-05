package com.bestbuy.android.openbox.library.webobject;

import java.util.ArrayList;

public class Product {

	private String sku;
	private String name;
	private ArrayList<Category> categoryPath;
	private int customerReviewCount;
	private String customerReviewAverage;
	private String image;	
	private String largeImage;
	private String url;
	private String modelNumber;
	private String regularPrice;
	private String salesPrice;
	private String productId;

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLargeImage() {
		return largeImage;
	}

	public void setLargeImage(String largeImage) {
		this.largeImage = largeImage;
	}

	private String longDescriptionHtml;

	public String getLongDescriptionHtml() {
		return longDescriptionHtml;
	}

	public void setLongDescriptionHtml(String longDescriptionHtml) {
		this.longDescriptionHtml = longDescriptionHtml;
	}

	@Override
	public String toString() {
		String s = "sku: " + sku + "\n" + "name: " + name + "\n"
				+ "RegularPrice: " + regularPrice + "\n" + "SalePrice: "
				+ salesPrice + "\n" + "CustomerReviewAverage: "
				+ customerReviewAverage + "\n" + "CustomerReviewCount"
				+ customerReviewCount + "\n";
		StringBuilder categoryStringBuilder = new StringBuilder("Categories: \n");
		for (Category c : categoryPath) {
			categoryStringBuilder.append("Id: " + c.getId() + "\t" + "Name: " + c.getName()
					+ "\n");
		}
		s += categoryStringBuilder.toString();
		return s;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Category> getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(ArrayList<Category> categoryPath) {
		this.categoryPath = categoryPath;
	}

	public int getCustomerReviewCount() {
		return customerReviewCount;
	}

	public void setCustomerReviewCount(int customerReviewCount) {
		this.customerReviewCount = customerReviewCount;
	}

	public void setCustomerReviewAverage(String customerReviewAverage) {
		if (customerReviewAverage == null || customerReviewAverage.equals("null") || customerReviewAverage.length() == 0) {
			this.customerReviewAverage = "0.0";
		} else {
			this.customerReviewAverage = customerReviewAverage;
		}
	}

	public String getCustomerReviewAverage() {
		return customerReviewAverage;
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getRegularPrice() {
		return regularPrice;
	}

	public void setRegularPrice(String regularPrice) {
		this.regularPrice = regularPrice;
	}

	public String getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(String salesPrice) {
		this.salesPrice = salesPrice;
	}
}
