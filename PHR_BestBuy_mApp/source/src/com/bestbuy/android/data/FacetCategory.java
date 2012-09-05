package com.bestbuy.android.data;

import java.io.Serializable;
import java.util.ArrayList;

public class FacetCategory implements Serializable{

	private static final long serialVersionUID = -5353418639099527867L;
	
	private String name;
	private ArrayList<Facet> facets;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Facet> getFacets() {
		return facets;
	}
	public void setFacets(ArrayList<Facet> facets) {
		this.facets = facets;
	}
	
	
}
