package com.bestbuy.android.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;

/**
 * Wrapper class for product categories
 * @author Recursive Awesome
 *
 */
public class CategoryBrowseWrapper {

	private View base;
	private TextView name;
	private String id;
	private String nameText;
	private ImageView categoryIcon;
	private String remixId;
	private boolean leaf;
	
	 
	private boolean fetchedViaSearch ;
	
	public CategoryBrowseWrapper(View base) {
		this.base=base;
	}

	public TextView getName() {
		if (name==null) {
			name=(TextView)base.findViewById(R.id.category_browse_category_name);
		}
		return name;
	}
	
	public ImageView getIcon() {
		if (categoryIcon==null) {
			categoryIcon=(ImageView)base.findViewById(R.id.category_browse_icon);
		}
		return categoryIcon;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setNameText(String nameText) {
		this.nameText = nameText;
	}
	
	public String getNameText() {
		return nameText;
	}
	
	public void setRemixId(String remixId) {
		this.remixId = remixId;
	}
	
	public String getRemixId() {
		return remixId;
	}
	
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setFetchedViaSearch(boolean viaSearch) {
		this.fetchedViaSearch = viaSearch;
	}
	public boolean getFetchedViaSearch() {
		return fetchedViaSearch;
	}
}
