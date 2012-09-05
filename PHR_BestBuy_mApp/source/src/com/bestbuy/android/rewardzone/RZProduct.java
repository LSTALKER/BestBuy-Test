package com.bestbuy.android.rewardzone;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.util.BBYLog;

/**
 * Represents a product that was purchased in a Reward Zone transaction.
 * 
 * @author Administrator
 * 
 */
public class RZProduct extends RZTransaction implements Serializable {

	private static final long serialVersionUID = 8386827857434771014L;

	String name;
	String sku;
	String purchaseDate;
	String price;
	boolean ableToLoad;

	String category;
	Product product;
	// private SearchRequest _searchRequest;

	private final String TAG = this.getClass().getName();

	public RZProduct(RZTransactionLineItem lineItem) {
		name = lineItem.getSkuDescription();
		sku = lineItem.getSku();
		BBYLog.i(TAG, "SKU: " + sku);
		price = centsToDollars(lineItem.getSalePriceCents());
		try {
			loadProduct();
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			ableToLoad = false;
		}
	}
	
	public RZProduct(Product product,HashMap<String,RZTransactionLineItem> skuRZTransactionMap) {
		sku = product.getSku();
		category = product.getCategoryName();
		this.product = product;
		RZTransactionLineItem lineItem = skuRZTransactionMap.get(sku);
		name = lineItem.getSkuDescription();
		price = centsToDollars(lineItem.getSalePriceCents());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFirstDetail() {
		return "Purchased " + purchaseDate + " SKU " + sku;
	}

	@Override
	public String getSecondDetail() {
		return "$" + price + " | " + category;
	}

	@Override
	public void clicked(Activity activity) {
		if (product != null) {
			AppData appData = ((BestBuyApplication) activity.getApplication()).getAppData();
			appData.setSelectedProduct(product);
			/*Intent i = new Intent(activity, ProductDetail.class);*/
			Intent i = new Intent(activity, MDOTProductDetail.class);
			activity.startActivity(i);
		}
		return;
	}

	@Override
	public void fetchImage(ImageView iv) {
		// if (product != null) {
		// dm.fetchBitmapOnThread(product.getImageURL(), iv);
		// }
	}

	public String getImageUrl() {
		if (product != null) {
			return product.getImageURL();
		}
		return null;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public boolean isAbleToLoad() {
		return ableToLoad;
	}

	private Product loadProduct() throws Exception {
		if (product == null) {
			SearchRequest searchRequest = new SearchRequest();
			String freeText = null;
			String results = null;
			// Load product information
			freeText = sku;
			searchRequest.setFreeText(freeText);
			searchRequest.setPage("1");
			searchRequest.run301Search();
			results = searchRequest.getResults();
			JSONObject jsonResults = new JSONObject(results);
			JSONArray products = jsonResults.getJSONArray(AppData._301_ITEMS);
			if (products.length() > 0) {
				JSONObject product = products.getJSONObject(0);
				JSONArray categoryPath = product.getJSONArray("categoryPath");
				JSONObject categoryObj = categoryPath.getJSONObject(categoryPath.length() - 1);
				category = categoryObj.getString("name");
				List<Product> productList = searchRequest.getSearchResultList();
				this.product = productList.get(0);
				ableToLoad = true;
			} else {
				ableToLoad = false;
			}

		}

		return product;
	}

	public static final Comparator<RZProduct> ORDER_NAME = new Comparator<RZProduct>() {
		public int compare(RZProduct t1, RZProduct t2) {
			if (t1.getName() != null && t2.getName() != null) {
				return t1.getName().compareTo(t2.getName());
			} else {
				return 0;
			}
		}
	};

	public static final Comparator<RZProduct> ORDER_DATE = new Comparator<RZProduct>() {
		public int compare(RZProduct t1, RZProduct t2) {
			if (t1.getPurchaseDate() != null && t2.getPurchaseDate() != null) {
				return t1.getPurchaseDate().compareTo(t2.getPurchaseDate());
			} else {
				return 0;
			}
		}
	};

	public static final Comparator<RZProduct> ORDER_PRICE = new Comparator<RZProduct>() {
		public int compare(RZProduct t1, RZProduct t2) {
			if (t1.getPrice() != null && t2.getPrice() != null) {
				return Double.valueOf(t1.getPrice()).compareTo(Double.valueOf(t2.getPrice()));
			} else {
				return 0;
			}
		}
	};

	@Override
	public void setName() {
		
	}

	@Override
	public void setFirstDetail() {
		
	}

	@Override
	public void setSecondDetail() {
		
	}
}
