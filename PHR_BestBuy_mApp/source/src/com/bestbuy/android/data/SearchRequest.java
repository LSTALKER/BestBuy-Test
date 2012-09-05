package com.bestbuy.android.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.activity.GamingSearch;
import com.bestbuy.android.rewardzone.RZOffer;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.StoreLocator;

/**
 * Allows for searching of all products in a category and generates a list of
 * Product objects from the results.
 * 
 * @author Recursive Awesome
 * 
 */
public class SearchRequest implements Serializable {

	private static final long serialVersionUID = -5210097290463402378L;

	private String freeText;
	private String sku;
	private String upc;
	private String categoryPathName;
	private String categoryIdName;
	private String sort;
	private String page;
	private String totalPages;
	private String results;
	private List<String> frequentlyPurchasedWith;
	private List<String> skus;
	private List<String> upcs;
	private boolean remixSort;
	private String cursor;
	private boolean hasNextPage;
	private List<String> platforms;
	private String zipCode;
	private ArrayList<String> skuList;
	private Double latitude;
	private Double longitude;
		

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	private static final String TOTAL_PAGES = "total_pages";
	private static final String REMIX_TOTAL_PAGES = "totalPages";
	private static final String HAS_NEXT_PAGE = "has_next_page";
	private List<Product> searchResultList;
	private List<FacetCategory> facetCategories;
	private Facet facet;

	public static final String SRCH_SHOW = Product.SKU + "," + Product.NAME
			+ "," + Product.PRODUCT_ID + "," + Product.SHORT_DESCRIPTION + ","
			+ Product.LONG_DESCRIPTION + "," + Product.PLOT + ","
			+ Product.REGULAR_PRICE + "," + Product.PLAN_PRICE + ","
			+ Product.ARTIST_NAME + "," + Product.ON_SALE + ","
			+ Product.MEDIUM_IMAGE + "," + Product.THUMBNAIL_IMAGE + ","
			+ Product.CUSTOMER_REVIEW_AVERAGE + ","
			+ Product.CUSTOMER_REVIEW_COUNT + "," + Product.RELEASE_DATE + ","
			+ Product.LARGE_IMAGE + "," + Product.IMAGE + ","
			+ Product.MODEL_NUMBER + "," + Product.FORMAT + "," + Product.SALE_PRICE + ","
			+ Product.FREQUENTLY_PURCHASED_WITH + ","
			+ Product.IN_STORE_AVAILABILITY + "," + Product.ONLINE_AVAILABILITY
			+ "," + Product.URL + "," + Product.CATEGORY_PATH + ","
			+ Product.TYPE + "," +  Product.ESRB_RATING + "," + Product.IN_STORE_PICKUP + ","
			+ Product.HOME_DELIVERY + "," + Product.FRIENDS_AND_FAMILY_PICKUP + "," 
			+ Product.SPECIAL_ORDER + "," + Product.ORDERABLE + "," + Product.ACCESSORIES_IMAGE + "," 
			+ Product.BACKVIEW_IMAGE + "," + Product.LARGE_FRONT_IMAGE + "," + Product.RIGHTVIEW_IMAGE + "," 
			+ Product.TOPVIEW_IMAGE + "," + Product.LEFTVIEW_IMAGE + "," + Product.ALTERNATE_VIEWS_IMAGE + "," + Product.REMOTE_CONTROL_IMAGE + "," + Product.ANGLE_IMAGE + ","
			+ Product.PROTECTION_PLANS_SKU + "," + Product.BUYBACK_PLANS_SKU +","+ Product.ADVERTISED_PRICE_RESTRICTION + ","+Product.PLATFORM+","+Product.ACTIVE + ","+Product.TRADE_IN_VALUE+","+Product.PREOWNED + "," 
			+ Product.PROTECTION_PLAN_TERM + "," + Product.PROTECTION_PLAN_TYPE;

	private String TAG = this.getClass().getName();

	public String getFreeText() {
		return freeText;
	}

	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}

	public void setRemixSort(boolean remixSearch) {
		this.remixSort = remixSearch;
	}

	public boolean isRemixSort() {
		return remixSort;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public void setSkus(List<String> skus) {
		this.skus = skus;
	}

	public List<String> getSkus() {
		return this.skus;
	}

	public String getSku() {
		return sku;
	}
	
	public void setPlatforms(List<String> platforms){
		this.platforms = platforms;
	}
	
	public void setUPC(String upc) {
		this.upc = upc;
	}

	public void setUPCs(List<String> upcs) {
		this.upcs = upcs;
	}

	public List<String> getUPCs() {
		return this.upcs;
	}

	public String getUPC() {
		return upc;
	}

	public String getCategoryPathName() {
		return categoryPathName;
	}

	public void setCategoryPathName(String department) {
		this.categoryPathName = department;
	}

	public List<Product> getSearchResultList() {
		if (searchResultList == null) {
			return new ArrayList<Product>();
		}
		return searchResultList;
	}

	public void setSearchResultList(List<Product> searchResultList) {
		this.searchResultList = searchResultList;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTotalPages() {
		if (totalPages == null) {
			return "0";
		}
		return totalPages;
	}
	
	public boolean hasNextPage(){
		return hasNextPage;
	}

	public void setFrequentlyPurchasedWith(List<String> frequentlyPurchasedWith) {
		this.frequentlyPurchasedWith = frequentlyPurchasedWith;
	}

	public List<String> getFrequentlyPurchasedWith() {
		return this.frequentlyPurchasedWith;
	}

	public boolean hasFrequentlyPurchasedWith() {
		return frequentlyPurchasedWith != null;
	}

	public String getCategoryIdName() {
		return categoryIdName;
	}

	public void setCategoryIdName(String categoryIdName) {
		this.categoryIdName = categoryIdName;
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public String getResults() {
		return results;
	}

	public List<FacetCategory> getFacetCategories() {
		return facetCategories;
	}

	public void setFacetCategories(List<FacetCategory> facetCategories) {
		this.facetCategories = facetCategories;
	}

	public Facet getFacet() {
		return facet;
	}

	public void setFacet(Facet facet) {
		this.facet = facet;
	}

	public void runSearch() throws Exception {
		if (getSkus() != null && !getSkus().isEmpty()) {
			runRemixSkuSearch(getSkus());
		} else if (frequentlyPurchasedWith != null) {
			runRemixSearch();
		} else if (facet != null) {
			runFacetSearch();
		} else {
			run301Search();
		}
	}

	public void run301Search() throws Exception {

		String host = AppConfig.getSmalHost();
		StringBuffer query = new StringBuffer();

		if (freeText != null) {
			freeText = freeText.trim().replace("&", "+"); // Replace & with +
			freeText = freeText.replace(" ", "%20"); // replace spaces with %20			
			query.append("api/V1/products/search/" + freeText);

		} else {
			query.append("api/V1/categories/" + categoryIdName + "/products");
		}

		String path = query.toString();

		Map<String, String> params = new HashMap<String, String>();

		params.put("page", page);
		params.put("marketplace", "*");		
		params.put("api_key", AppConfig.getSmalApiKey());

		if (sort != null && !sort.equals("")) {
			params.put("sort", sort);
		}
		try {
			BBYLog.i(TAG, "Make 301 call");
			results = APIRequest.makeGetRequest(host, path, params, false, false);

			JSONObject jsonResults = new JSONObject(results);

			if (!jsonResults.getString(TOTAL_PAGES).equals(AppData.JSON_NULL)) {
				this.totalPages = jsonResults.getString(TOTAL_PAGES);
			} else {
				this.totalPages = "1";
			}
			
			this.hasNextPage = jsonResults.optBoolean(HAS_NEXT_PAGE, false);

			JSONArray products = jsonResults.getJSONArray(AppData._301_ITEMS);

			searchResultList = new ArrayList<Product>();
			skuList = new ArrayList<String>();
			for (int i = 0; i < products.length(); i++) {
				JSONObject jsonProduct = products.getJSONObject(i);
				Product searchProduct = new Product();
				searchProduct.loadSearchResultData(jsonProduct);
				skuList.add(searchProduct.getSku());
				searchResultList.add(searchProduct);
			}
			
			if (zipCode != null) {
				List<PickUPStoresAvail> stores = StoreLocator.findStoresByLocationWithSkus(zipCode, skuList);

				for (PickUPStoresAvail store : stores) {
					for (Product product : searchResultList) {
						for (String sku : store.getSkuList()) {
							if (product.getSku().equals(sku)) {
								product.getStoreLocation().add(store.getName());
							}
						}
					}
				}

			} else if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
				List<PickUPStoresAvail> stores = StoreLocator.findStoresByLocationWithSkus(latitude, longitude,	skuList);

				for (PickUPStoresAvail store : stores) {
					for (Product product : searchResultList) {
						for (String sku : store.getSkuList()) {
							if (product.getSku().equals(sku)) {
								product.getStoreLocation().add(store.getName());
							}
						}
					}
				}

			}
		    
			  facetCategories = new ArrayList<FacetCategory>();

			JSONArray categoryJSON = jsonResults.optJSONArray("category_facets");
			if (categoryJSON != null) {
				FacetCategory fcc = new FacetCategory();
				int size = categoryJSON.length();
				if(size > 0) {
					fcc.setName("Category");
					ArrayList<Facet> fcl = new ArrayList<Facet>();
					for (int i = 0; i < size; i++) {
						fcl.add(loadFacet(categoryJSON.optJSONObject(i)));
					}
					fcc.setFacets(fcl);
					facetCategories.add(fcc);
				}
			}

			JSONArray facetsJSON = jsonResults.optJSONArray("facets");
			if (facetsJSON != null) {
				for (int i = 0; i < facetsJSON.length(); i++) {
					JSONObject facet = facetsJSON.optJSONObject(i);
					FacetCategory fcc = new FacetCategory();
					fcc.setName(facet.optString("name"));
					JSONArray filterJSON = facet.optJSONArray("filters");
					if (filterJSON != null) {
						ArrayList<Facet> fcl = new ArrayList<Facet>();
						for (int j = 0; j < filterJSON.length(); j++) {
							fcl.add(loadFacet(filterJSON.optJSONObject(j)));
						}
						fcc.setFacets(fcl);
						facetCategories.add(fcc);
					}
				}
			}

		} catch (APIRequestException apiEx) {
			if (apiEx.getStatusCode() == 404) {
				searchResultList = new ArrayList<Product>(); // create a blank list.
				return;
			}
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			searchResultList = new ArrayList<Product>(); // create a blank list.
			BBYLog.e(TAG, "SearchRequest.run301search() - Error in parsing the JSON returned: " + ex.getMessage());
			throw ex;
		}
	}

	private Facet loadFacet(JSONObject json) {
		Facet facet = new Facet();
		facet.setCount(json.optString("count"));
		facet.setName(json.optString("name"));
		facet.setQuery(json.optString("facet_query"));
		return facet;
	}

	public void runFacetSearch() throws Exception {
		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("facet_query", facet.getQuery());
			parameters.put("search_term", freeText);
			parameters.put("api_key", AppConfig.getSmalApiKey());
			parameters.put("sort", sort);
			parameters.put("page", page);
			results = APIRequest.makeJSONPostRequest(AppConfig.getSmalHost(), AppData.BBY_FACETED_PATH, parameters);

			JSONObject jsonResults = new JSONObject(results);

			if (!jsonResults.getString(TOTAL_PAGES).equals(AppData.JSON_NULL)) {
				this.totalPages = jsonResults.getString(TOTAL_PAGES);
			} else {
				this.totalPages = "1";
			}
			
			this.hasNextPage = jsonResults.optBoolean(HAS_NEXT_PAGE, false);
			JSONArray products = jsonResults.getJSONArray(AppData._301_ITEMS);

			searchResultList = new ArrayList<Product>();
			for (int i = 0; i < products.length(); i++) {
				JSONObject jsonProduct = products.getJSONObject(i);
				Product searchProduct = new Product();
				searchProduct.loadSearchResultData(jsonProduct);
				searchResultList.add(searchProduct);
			}

		} catch (APIRequestException apiEx) {
			if (apiEx.getStatusCode() == 404) {
				searchResultList = new ArrayList<Product>(); // create a blank list.
				return;
			}
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			searchResultList = new ArrayList<Product>(); // create a blank list.
			BBYLog.e(TAG, "SearchRequest.sendFacet() - Error in parsing the JSON returned: " + ex.getMessage());
			throw ex;
		}

	}

	public List<Offer> getOffers(String channelKey) throws Exception {
		return getOffers(channelKey, null, null);
	}
	
	public List<Offer> getOffers(String channelKey, String department) throws Exception {
		return getOffers(channelKey, department, null);
	}

	public List<Offer> getOffers(String channelKey, String department, String priority) throws Exception {
		final int MAX_RETRIES = 3;
		int retryCount = 0;
		int statusCode = 0;

		Map<String, String> params = new HashMap<String, String>();
		List<Offer> offersList = new ArrayList<Offer>();

		String host = AppConfig.getBbyOfferURL();
		String path = AppData.BBY_OFFERS_PATH;
		String apiKey = AppConfig.getBbyOfferApiKey();

		if (channelKey != null) {
			params.put("channel_key", channelKey);
		}

		if (priority != null) {
			params.put("priority", priority);
		}
		
		if (department != null) {
			params.put("department_key", department);
		}

		params.put("api_key", apiKey);

		if (cursor != null) {
			params.put("cursor", cursor);
		}
		results = APIRequest.makeGetRequest(host, path, params, false);

		// Retry the call 3 times if 500 error occurs
//		while (retryCount < MAX_RETRIES) {
//			try {
//				results = APIRequest.makeGetRequest(host, path, params, false);
//			} catch (APIRequestException apiEx) {
//				statusCode = apiEx.getStatusCode();
//				BBYLog.printStackTrace(TAG, apiEx);
//				throw apiEx;
//			} catch (Exception ex) {
//				BBYLog.printStackTrace(TAG, ex);
//				throw ex;
//			}
//			if (statusCode == 500) {
//				retryCount++;
//			} else {
//				break;
//			}
//		}

		JSONObject jsonResults = new JSONObject(results);
		JSONObject obj = jsonResults.getJSONObject("data");

		cursor = obj.optString("cursor", null);

		JSONArray products = obj.getJSONArray(AppData.API_OFFER_PRODUCTS);

		for (int i = 0; i < products.length(); i++) {
			JSONObject jsonProduct = products.getJSONObject(i);
			Offer offersProduct = new Offer();
			if (!jsonProduct.isNull("remix_data")) {
				offersProduct.loadSearchResultData(jsonProduct.getJSONObject("remix_data"));
			}
			offersProduct.loadOffersData(jsonProduct);

			boolean hasUrl = !offersProduct.getUrl().equals(AppData.JSON_NULL);
			boolean hasSkus = offersProduct.getSkus() != null && offersProduct.getSkus().size() > 0;
			if (hasUrl || hasSkus) {
				offersList.add(offersProduct);
			}
		}

		return offersList;
	}

	public List<RZOffer> getRZOffers(String rzLevel) throws Exception {
		final int MAX_RETRIES = 3;
		int retryCount = 0;
		int statusCode = 0;

		Map<String, String> params = new HashMap<String, String>();
		List<RZOffer> offersList = new ArrayList<RZOffer>();

		params.put("channel_key", AppData.BBY_SERV_RZ_CHANNELS);
		params.put("api_key", AppConfig.getBbyOfferApiKey());
		params.put("rz_levels", rzLevel);
		if (cursor != null) {
			params.put("cursor", cursor);
		}

		// Retry the call 3 times if 500 error occurs
		while (retryCount < MAX_RETRIES) {
			try {
				results = APIRequest.makeGetRequest(AppConfig.getBbyOfferURL(), AppData.BBY_OFFERS_PATH, params, false, false);
			} catch (APIRequestException apiEx) {
				statusCode = apiEx.getStatusCode();
				BBYLog.printStackTrace(TAG, apiEx);
				throw apiEx;
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				throw ex;
			}
			if (statusCode == 500) {
				retryCount++;
			} else {
				break;
			}
		}
		BBYLog.i(TAG, results);
		JSONObject jsonResults = new JSONObject(results);
		JSONObject offersData = jsonResults.getJSONObject(AppData.BBY_OFFERS_DATA);
		JSONArray offers = offersData.getJSONArray(AppData.API_OFFER_PRODUCTS);

		cursor = offersData.optString("cursor", null);

		for (int i = 0; i < offers.length(); i++) {
			JSONObject jsonOffer = offers.getJSONObject(i);
			RZOffer rzOffer = new RZOffer();
			rzOffer.loadData(jsonOffer);
			offersList.add(rzOffer);
		}
		return offersList;
	}

	public List<DealCategory> getDealsCategories() throws Exception {

		String host = AppConfig.getBbyOfferURL();
		String path = AppData.BBY_OFFERS_DEPARTMENT_PATH;

		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", AppConfig.getBbyOfferApiKey());
		params.put("channel_key", AppData.BBY_OFFERS_MOBILE_WEEKLY_AD_CHANNEL);

		List<DealCategory> dealCategories = new ArrayList<DealCategory>();

		String results = APIRequest.makeGetRequest(host, path, params, false);
		JSONObject jsonResults = new JSONObject(results);
		JSONObject dataObj = jsonResults.getJSONObject("data");
		JSONArray departmentsArray = dataObj.getJSONArray("departments");

		for (int i = 0; i < departmentsArray.length(); i++) {
			DealCategory dealCategory = new DealCategory();
			JSONObject departmentObj = departmentsArray.getJSONObject(i);
			dealCategory.setProductCount(departmentObj.getInt("offer_count"));
			dealCategory.setName(departmentObj.getString("name"));
			dealCategory.setKey(departmentObj.getString("key"));
			dealCategories.add(dealCategory);
		}

		return dealCategories;
	}

	public void runGamingSearch() throws APIRequestException, Exception {
		StringBuffer query = new StringBuffer();
		if (freeText != null) {
			freeText = freeText.trim().replace("&", "+"); // Replace & with +
			freeText = freeText.replace(" ", "%20"); // replace spaces with %20			
			query.append("search='" + freeText + "'");
		} else if (sku != null) {
			query.append("sku=" + sku.trim());
		} else if (upc != null){
			query.append("upc=" + upc.trim());
		} else if (skus != null){		
			StringBuilder skuQueryBuilder = new StringBuilder();
			for (String sku : skus) {
				skuQueryBuilder.append(sku + ",");
			}
			String skuQuery = skuQueryBuilder.toString();
			if (skuQuery.charAt(0) == ',') {
				skuQuery = skuQuery.substring(1);
			}
			if (skuQuery.charAt(skuQuery.length()-1) == ',') {
				skuQuery = skuQuery.substring(0, skuQuery.length()-1);
			}
			
			query.append("sku in(" + skuQuery + ")");
		} else if (upcs != null){		
			String upcQuery = "";
			for (String upc : upcs) {
				upcQuery += upc + ",";
			}
			
			if (upcQuery.charAt(0) == ',') {
				upcQuery = upcQuery.substring(1);
			}
			if (upcQuery.charAt(upcQuery.length()-1) == ',') {
				upcQuery = upcQuery.substring(0, upcQuery.length()-1);
			}
			
			query.append("upc in(" + upcQuery + ")");
		}
		
		
		
		query.append("&type=game&tradeInValue%3E0&active=*&preowned=*");
		
		if(platforms != null){
			String platformQuery = "";
			for (String platform : platforms) {
				platformQuery += GamingSearch.getAllPlatforms().get(platform) + ",";
			}
			if(platformQuery.length() > 0){
				if (platformQuery.charAt(0) == ',') {
					platformQuery = platformQuery.substring(1);
				}
				if (platformQuery.charAt(platformQuery.length()-1) == ',') {
					platformQuery = platformQuery.substring(0, platformQuery.length()-1);
				}
			
				query.append("&platform in(" + platformQuery + ")");
			}
			//query.append("&platform='"+platforms+"'");
		}


		String host = AppConfig.getRemixURL() + "/v1";
		String path = "products(" + query + ")";

		Map<String, String> params = new HashMap<String, String>();
		params.put("page", page);
		params.put("show", SRCH_SHOW);
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");
		params.put("sort", "tradeInValue.desc");
		
		try {

			results = APIRequest.makeGetRequest(host, path, params, false);
			JSONObject jsonResults = new JSONObject(results);

			if (!jsonResults.getString(REMIX_TOTAL_PAGES).equals(AppData.JSON_NULL)) {
				this.totalPages = jsonResults.getString(REMIX_TOTAL_PAGES);
			} else {
				this.totalPages = "1";
			}

			JSONArray products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

			searchResultList = new ArrayList<Product>();
			for (int i = 0; i < products.length(); i++) {
				JSONObject jsonProduct = products.getJSONObject(i);
				Product searchProduct = new Product();
				searchProduct.loadSearchResultData(jsonProduct);
				searchResultList.add(searchProduct);
			}
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception parsing JSON SearchResult.runGamingSearch(): " + ex.getMessage());
			searchResultList = new ArrayList<Product>(); // create a blank list.
			throw ex;
		}
	}
	
	public void getTopTradeIns() throws APIRequestException, Exception {

		


		String query = "products(type=game&tradeInValue%3E0&active=*&preowned=*&platform='";

		String host = AppConfig.getRemixURL() + "/v1";
		String xboxPath = query + "Xbox 360')";
		String psPath = query + "PlayStation 3')";
		String wiiPath = query + "Nintendo Wii')";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("page", page);
		params.put("show", SRCH_SHOW);
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");
		params.put("pageSize", "1");
		params.put("sort", "tradeInValue.desc");

		try {

			totalPages = "1";
			results = APIRequest.makeGetRequest(host, xboxPath, params, false);
			JSONObject jsonResults = new JSONObject(results);

			JSONArray products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

			searchResultList = new ArrayList<Product>();
			JSONObject jsonProduct = products.getJSONObject(0);
			Product searchProduct = new Product();
			searchProduct.loadSearchResultData(jsonProduct);
			searchResultList.add(searchProduct);
			
			results = APIRequest.makeGetRequest(host, psPath, params, false);
			jsonResults = new JSONObject(results);

			products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

			jsonProduct = products.getJSONObject(0);
			searchProduct = new Product();
			searchProduct.loadSearchResultData(jsonProduct);
			searchResultList.add(searchProduct);
			
			results = APIRequest.makeGetRequest(host, wiiPath, params, false);
			jsonResults = new JSONObject(results);

			products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

			jsonProduct = products.getJSONObject(0);
			searchProduct = new Product();
			searchProduct.loadSearchResultData(jsonProduct);
			searchResultList.add(searchProduct);
			
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception parsing JSON SearchResultgetTopTradeIns(): " + ex.getMessage());
			searchResultList = new ArrayList<Product>(); // create a blank list.
			throw ex;
		}
	}
	
	

	public void runRemixSearch() throws APIRequestException, Exception {

		StringBuffer query = new StringBuffer();
		if (freeText != null) {
			query.append("search='" + freeText.trim().replace(" ", "+") + "'&active=*&marketplace=*");
		} else if (sku != null) {
			query.append("sku=" + sku.trim());
		}

		if (categoryPathName != null && sku == null) {
			query.append("&");
			query.append("categoryPath.id='" + categoryIdName.trim().replace(" ", "+") + "'");
		}

		if (frequentlyPurchasedWith != null) {
			StringBuffer sb = new StringBuffer();
			Iterator<String> iter = frequentlyPurchasedWith.iterator();
			while (iter.hasNext()) {
				sb.append(iter.next());
				if (iter.hasNext()) {
					sb.append(",");
				}
			}
			query.append("sku%20in(" + sb + ")"); // only get the active
			// products
		}

		if (categoryIdName != null) {
			if (freeText != null || sku != null) {
				query.append("&");
			}
			query.append("categoryPath.id=" + categoryIdName);
		}

		String host = AppConfig.getRemixURL() + "/v1";
		String path = "products(" + query + ")";

		Map<String, String> params = new HashMap<String, String>();
		params.put("page", page);
		params.put("show", SRCH_SHOW);
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");

		try {

			results = APIRequest.makeGetRequest(host, path, params, false);
			JSONObject jsonResults = new JSONObject(results);

			if (!jsonResults.getString(REMIX_TOTAL_PAGES).equals(AppData.JSON_NULL)) {
				this.totalPages = jsonResults.getString(REMIX_TOTAL_PAGES);
			} else {
				this.totalPages = "1";
			}

			JSONArray products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

			searchResultList = new ArrayList<Product>();
			for (int i = 0; i < products.length(); i++) {
				JSONObject jsonProduct = products.getJSONObject(i);
				Product searchProduct = new Product();
				searchProduct.loadSearchResultData(jsonProduct);
				searchResultList.add(searchProduct);
			}
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception parsing JSON SearchResult.runRemixsearch(): " + ex.getMessage());
			searchResultList = new ArrayList<Product>(); // create a blank list.
			throw ex;
		}
	}

	public static String getSearchShow() {
		return SRCH_SHOW;
	}

	public void runRelatedProductsSearch(Product product) throws Exception {
		runRelatedProductsSearch(product, false, true, true);
	}

	public void runRelatedProductsSearch(Product product, boolean includeWarranty, boolean includeProducts, boolean remixPad) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", AppConfig.getSmalApiKey());
		params.put("remix_category_id", product.getParentCategory());
		params.put("remix_product_sku", product.getSku());
		params.put("pad_with_remix_recommendations", Boolean.toString(remixPad));

		String result = APIRequest.makeGetRequest(AppConfig.getSmalHost(), AppData._301_RECOMMENDATIONS_PATH, params, false, false);

		try {
			JSONObject jsonResult = new JSONObject(result);
			searchResultList = new ArrayList<Product>();
			if (includeWarranty) {
				JSONObject buyBackObject = jsonResult.optJSONObject("buyback_plan");
				if (buyBackObject != null) {
					Warranty buyBack = new Warranty();
					buyBack.loadSearchResultData(buyBackObject);
					buyBack.loadWarrantyData(buyBackObject, Warranty.BUY_BACK_PLAN);
					searchResultList.add(buyBack);
				}

				JSONObject protectionPlanObject = jsonResult.optJSONObject("protection_plan");
				if (protectionPlanObject != null) {
					Warranty protectionPlan = new Warranty();
					protectionPlan.loadSearchResultData(protectionPlanObject);
					protectionPlan.loadWarrantyData(protectionPlanObject, Warranty.PROTECTION_PLAN);
					if (!searchResultList.contains(protectionPlan)) {
						searchResultList.add(protectionPlan);
					}
				}
			}

			if (includeProducts) {
				JSONArray products = jsonResult.getJSONArray("items");
				for (int i = 0; i < products.length(); i++) {
					Product relatedProduct = new Product();
					JSONObject jsonProductObj = products.getJSONObject(i);
					relatedProduct.loadSearchResultData(jsonProductObj);
					searchResultList.add(relatedProduct);
				}
			}
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}
	}

	public void runRemixSkuSearch(List<String> skus) throws Exception {
		if (skus.isEmpty()) {
			searchResultList = new ArrayList<Product>();
			return;
		}

		
		StringBuilder skuQueryBuilder = new StringBuilder(); 
		for (String sku : skus) {
			skuQueryBuilder.append(sku + ",");
		}
		String skuQuery = skuQueryBuilder.toString();
		//Remove commas from the front and back if necessary
		if (skuQuery.charAt(0) == ',') {
			skuQuery = skuQuery.substring(1);
		}
		if (skuQuery.charAt(skuQuery.length()-1) == ',') {
			skuQuery = skuQuery.substring(0, skuQuery.length()-1);
		}
		
		String host = AppConfig.getRemixURL() + "/v1";
		String path = "products((active=*&marketplace=*)&sku in(" + skuQuery + "))";

		Map<String, String> params = new HashMap<String, String>();
		params.put("page", page);
		//params.put("show", SRCH_SHOW);
		params.put("show", "all");
		params.put("apiKey", AppConfig.getRemixApiKey());
		params.put("format", "json");

		results = APIRequest.makeGetRequest(host, path, params, false);
		JSONObject jsonResults = new JSONObject(results);

		if (!jsonResults.getString(REMIX_TOTAL_PAGES).equals(AppData.JSON_NULL)) {
			this.totalPages = jsonResults.getString(REMIX_TOTAL_PAGES);
		} else {
			this.totalPages = "1";
		}

		JSONArray products = jsonResults.getJSONArray(AppData.API_REMIX_PRODUCTS);

		searchResultList = new ArrayList<Product>();
		for (int i = 0; i < products.length(); i++) {
			JSONObject jsonProduct = products.getJSONObject(i);
			Product searchProduct = new Product();
			searchProduct.loadSearchResultData(jsonProduct);
			searchResultList.add(searchProduct);
		}
	}
}
