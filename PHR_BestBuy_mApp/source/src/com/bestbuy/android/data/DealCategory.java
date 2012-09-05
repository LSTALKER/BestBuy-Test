package com.bestbuy.android.data;


/**
 * A deal category
 * @author Recursive Awesome
 *
 */
public class DealCategory implements Comparable<Object> {

	private String name;
	private int productCount;
	private String key;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	
	public int compareTo(Object another) {
		DealCategory other = (DealCategory)another;
		if (this.name.equals(other.name)) {
			return 0;
		} else {
			return this.name.compareTo(other.name);
		}
	}
	
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		if (this.hashCode() == obj.hashCode()) {
			return true;
		}
		return false;
	}
	
	
	public int hashCode() {
		return this.name.hashCode();
	}
}
