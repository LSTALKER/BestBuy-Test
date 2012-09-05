package com.bestbuy.android.data;

import java.io.Serializable;
import java.util.List;

/**
 * A category of products
 * 
 * @author Recursive Awesome
 * 
 */
public class Category implements Serializable {

	private static final long serialVersionUID = -6804474238168457609L;

	private String id;
	private String name;
	private List<Category> subCategories;
	private String imageURL;
	private String remixId;
	private boolean leaf;
	private boolean fetchedViaSearch;

	public Category(String id, String name, String imageURL, String remixId,
			boolean leaf, List<Category> subCategories, boolean fetchedViaSearch) {
		this.id = id;
		this.name = name;
		this.imageURL = imageURL;
		this.subCategories = subCategories;
		this.remixId = remixId;
		this.leaf = leaf;
		this.fetchedViaSearch = fetchedViaSearch;
	}
	
	public Category(String name){
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getRemixId() {
		return remixId;
	}

	public void setRemixId(String remixId) {
		this.remixId = remixId;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<Category> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}

	public boolean getFetchViaSearch() {
		return fetchedViaSearch;
	}

}
