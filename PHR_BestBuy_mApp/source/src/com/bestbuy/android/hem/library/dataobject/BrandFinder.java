package com.bestbuy.android.hem.library.dataobject;

import java.io.Serializable;
import java.util.ArrayList;



public class BrandFinder implements Serializable{

	private static final long serialVersionUID = 1L;
	private ArrayList<Brands> brands;
	private String productType;
	
	
	public ArrayList<Brands> getBrands() {
		return brands;
	}
	public void setBrands(ArrayList<Brands> brands) {
		if(brands==null) {
			this.brands = new ArrayList<Brands>();
		} else {
			this.brands = brands;
		}
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
}
