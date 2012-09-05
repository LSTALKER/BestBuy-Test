package com.bestbuy.android.hem.library.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bestbuy.android.hem.library.dataobject.BrandFinder;
import com.bestbuy.android.hem.library.dataobject.Brands;

public class LocalRebateFinderParser {
	private static final String BRANDS = "brands";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String PRODUCT_TYPE = "productType";

	public ArrayList<BrandFinder> parse(String responseString)
			throws JSONException {

		JSONObject results = new JSONObject(responseString);

		return createFrom(results);
	}

	private ArrayList<BrandFinder> createFrom(JSONObject source)
			throws JSONException { 
		ArrayList<BrandFinder> selectedBrandList = new ArrayList<BrandFinder>();
		JSONArray ecoRebatesArray = source.getJSONArray("EcoRebatesResponse");
		
		for (int size = 0; size < ecoRebatesArray.length(); size++) {
			JSONObject obj = ecoRebatesArray.getJSONObject(size);
			ArrayList<Brands> brandList = new ArrayList<Brands>();
			BrandFinder selectedBrand = new BrandFinder();
			JSONArray productArray = obj.optJSONArray(BRANDS);

			selectedBrand.setProductType(obj.optString(PRODUCT_TYPE, ""));
			for (int i = 0; i < productArray.length(); i++) {
				JSONObject brandSelected = (JSONObject) productArray.get(i);
				Brands brand = null;
				if (brandSelected != null) {
					brand = new Brands();
					brand.setName(brandSelected.optString(NAME, ""));
					brand.setId(brandSelected.optString(ID, ""));
				}

				brandList.add(brand);

			}
			selectedBrand.setBrands(brandList);
			selectedBrandList.add(selectedBrand);
		}
		return selectedBrandList;
	}
}