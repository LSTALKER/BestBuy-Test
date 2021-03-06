package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.icr.util.IcrAsyncTask;
import com.bestbuy.android.icr.util.IcrUtil;
import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.EndlessAdapter;
import com.bestbuy.android.util.FontLibrary;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.SuggestionProvider;

/**
 * Displays a list of search results for a particular product category
 * 
 * @author Recursive Awesome
 * 
 */

public class BrowseProductList extends MenuActivity {

	private String sort;
	private int sortSelection;
	private SearchAdapter _iconicAdapter;
	private ListView _lv;
	private List<Product> searchResults = new ArrayList<Product>();
	
	public final int SORT_BESTMATCH = 0;
	public final int SORT_BESTSELLING = 1;
    public final int SORT_BRAND_AZ = 2;
    public final int SORT_BRAND_ZA = 3;
    public final int SORT_PRICE_HIGH_LOW = 4;
    public final int SORT_PRICE_LOW_HIGH = 5;
    
    public final String SORT_BESTMATCH_OPTION = "Best Match";
    public final String SORT_BESTSELLING_OPTION = "Best Selling";
    public final String SORT_BRAND_AZ_OPTION = "Brand A-Z";
    public final String SORT_BRAND_ZA_OPTION = "Brand Z-A";
    public final String SORT_PRICE_HIGH_LOW_OPTION = "Price High-Low";
    public final String SORT_PRICE_LOW_HIGH_OPTION = "Price Low-High";
    
    public final CharSequence[] SEARCH_SORTING_ITEMS = {	SORT_BESTMATCH_OPTION,
    														SORT_BESTSELLING_OPTION,
    														SORT_BRAND_AZ_OPTION,
    														SORT_BRAND_ZA_OPTION,
    														SORT_PRICE_HIGH_LOW_OPTION,
    														SORT_PRICE_LOW_HIGH_OPTION
    														
                                                		};
	
    public final CharSequence[] BROWSE_SORTING_ITEMS = {	SORT_BESTSELLING_OPTION,
															SORT_BRAND_AZ_OPTION,
															SORT_BRAND_ZA_OPTION,
															SORT_PRICE_HIGH_LOW_OPTION,
    														SORT_PRICE_LOW_HIGH_OPTION
												        };
    
	private Button sortOptions;
	private Button storeAvailbility;
	private ImageView header_cart;
	private RelativeLayout header_cart_badge;
	private Button narrow_btn;

	private SearchRequest searchRequest;
	
	private HashMap<String, Boolean> hidePriceMap;
	private IcrAsyncTask loadIcrPricingTask;
	
	private String categoryName = "";
	private int clickCounter = 0;
	
	private boolean isProductSearch = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchresult);
		// ICR Pricing
		getICRPricingDetails();
		
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			
			LinearLayout browsebyLayout = (LinearLayout) findViewById(R.id.searchresult_browseby_layout);
			
			TextView headerText = (TextView) findViewById(R.id.search_result_header_text);
			
			if (bundle != null && bundle.containsKey("categoryName")) {
				browsebyLayout.setVisibility(View.VISIBLE);
				categoryName = bundle.getString("categoryName");
				headerText.setText("Browsing " + categoryName);
			} else {
				browsebyLayout.setVisibility(View.GONE);
			}
			
			if(bundle != null && bundle.containsKey(StoreUtils.CLICKCOUNTER)) {
				clickCounter = bundle.getInt(StoreUtils.CLICKCOUNTER);
			}
			
			if(bundle != null && bundle.containsKey(StoreUtils.SORT_SELECTION)) {
				sortSelection = bundle.getInt(StoreUtils.SORT_SELECTION);
			}
			
			if(bundle != null && bundle.containsKey(StoreUtils.IS_PRODUCT_SEARCH)) {
				isProductSearch = bundle.getBoolean(StoreUtils.IS_PRODUCT_SEARCH);
			}
			
		}
		
		if (getIntent() != null	&& Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			
			String query = getIntent().getStringExtra(SearchManager.QUERY);
			Bundle data = getIntent().getBundleExtra(SearchManager.APP_DATA);
			searchRequest = new SearchRequest();
			if (data != null) {
				if (data.containsKey("categoryName")) {
					categoryName = data.getString("categoryName");
					searchRequest.setCategoryIdName(categoryName);
				}
			}
			if (query.matches("([0-9]* *)+")) {
				// Run remix sku search
				searchRequest.setSkus(Arrays.asList(query.split(" ")));
			} else {
				// Run 301 search
				searchRequest.setFreeText(categoryName + " " + query);
			}
			appData.setSearchRequest(searchRequest);
			
			// Save search in recent searches list
			SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
			suggestions.saveRecentQuery(query, null);
			isProductSearch = true;
			
		} else {
			searchRequest = appData.getSearchRequest();
		}
		
		sortOptions = (Button) findViewById(R.id.search_result_sort);
		storeAvailbility = (Button) findViewById(R.id.store_availbility);

		if (searchRequest != null && searchRequest.getFrequentlyPurchasedWith() != null) {
			findViewById(R.id.srchresult_header).setVisibility(View.GONE);
		}

		// Set up narrow button
		/*findViewById(R.id.search_result_narrow).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), NarrowResults.class);
						intent.putExtra(StoreUtils.SORT_SELECTION, sortSelection);
						intent.putExtra(StoreUtils.IS_PRODUCT_SEARCH, isProductSearch);
						startActivity(intent);
					}
				});*/

		header_cart = (ImageView) findViewById(R.id.header_cart);
		narrow_btn = (Button) findViewById(R.id.search_result_narrow);
		header_cart_badge = (RelativeLayout) findViewById(R.id.header_cart_badge);
		initValues();
		setUpTabs();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		int isVisible = ((Button)findViewById(R.id.search_result_narrow)).getVisibility();
		if(isVisible == View.VISIBLE) {
			header_cart.setVisibility(View.GONE);
			header_cart_badge.setVisibility(View.GONE);
		} 
	}
	
	// ICR Pricing
	private void getICRPricingDetails() {
		
		hidePriceMap = new HashMap<String, Boolean>();
		hidePriceMap = IcrUtil.getIcrHidePrice();
	
		if(hidePriceMap.size()==0){
			performIcrPricingTask();
		}
	}
   
	// ICR Pricing
	private void performIcrPricingTask() {
		
		if (loadIcrPricingTask == null || !loadIcrPricingTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
			loadIcrPricingTask = new IcrAsyncTask();
			loadIcrPricingTask.execute();
			if(loadIcrPricingTask.getStatus().equals(AsyncTask.Status.FINISHED)){
				hidePriceMap=IcrUtil.getIcrHidePrice();
			}
		}
	}	
	
	private void showZipSearchActivity(){
		showZipSearchActivity(getIntent());
	}
	
	private void showZipSearchActivity(Intent intent){
		intent.setClass(this, StoreAvailability.class);
 	   
		if(categoryName != null && !categoryName.equals(""))
			intent.putExtra("categoryName", categoryName); 
 	   
 	   intent.putExtra(StoreUtils.SORT_SELECTION, sortSelection);
 	   intent.putExtra(StoreUtils.IS_PRODUCT_SEARCH, isProductSearch);
 	  
 	   if(intent.hasExtra(StoreUtils.ZIP_CODE))
 		   intent.putExtra(StoreUtils.ZIP_CODE, intent.getStringExtra(StoreUtils.ZIP_CODE));

 	   startActivity(intent);
	}

	private void showSearchActivity() {
		LocationManager locationManager = appData.getBBYLocationManager().getLocationManager(BrowseProductList.this);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		Intent intent = this.getIntent();
		
		double latitude = 0.0;
		double longitude = 0.0;
		
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			
			intent.putExtra(StoreUtils.LATITUDE, latitude);
			intent.putExtra(StoreUtils.LONGITUDE, longitude);
		}
		
		if(clickCounter == 0) {
			intent.putExtra(StoreUtils.CLICKCOUNTER, 1);
			if (location != null) {
				intent.removeExtra(StoreUtils.ZIP_CODE);
				intent.putExtra(StoreUtils.SORT_SELECTION, sortSelection);
				intent.putExtra(StoreUtils.IS_PRODUCT_SEARCH, isProductSearch);
				startActivity(getIntent());
				finish();
			} else {
				 showZipSearchActivity(intent);
			}
		} else {
			intent.putExtra(StoreUtils.CLICKCOUNTER, 1);
			showZipSearchActivity(intent);
		}
	}
	
	private void searchAlertDialog(){
		if (this == null || this.isFinishing()) {
			return;
		}
        
		CharSequence[] items;
		if(isProductSearch)
			items = SEARCH_SORTING_ITEMS;
		else
			items = BROWSE_SORTING_ITEMS;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
            	sortResults(item);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void setSortOptionText(int sortSelection) {
		if(isProductSearch)
			sortOptions.setText(SEARCH_SORTING_ITEMS[sortSelection]);
		else
			sortOptions.setText(BROWSE_SORTING_ITEMS[sortSelection]);
	}

	private void setUpTabs() {
		sortOptions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				searchAlertDialog();
			}
		});

		storeAvailbility.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(AppData.isCurrentLocationAlloweded(BrowseProductList.this))
					showSearchActivity();
				else
					showZipSearchActivity();
			}
		});
	}

	private void initValues() {
		_lv = (ListView) findViewById(R.id.search_list);
		_lv.setOnItemClickListener(searchResultClickedHandler);
		
		setSortOptionText(sortSelection);
		
		sort = getSortString(sortSelection);
		appData.setSearchSortPosition(sortSelection);
		appData.setSearchResultSort(sort);
		searchRequest.setSort(sort);
		searchRequest.setPage("1");
		_iconicAdapter=new SearchAdapter();
		_lv.setAdapter(_iconicAdapter);
		_iconicAdapter.notifyDataSetChanged();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("Searching...");
		return dialog;
	}

	OnItemClickListener searchResultClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			if (position >= 0 && position < searchResults.size()) {
				
				//TODO: Not sure when the comparemode is enabled
				if (appData.isCompareMode()) {
					appData.addCompareProduct(searchResults.get(position));
					Intent i = new Intent(v.getContext(), ProductCompare.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
					
					appData.setCompareMode(false);
				} else {
					appData.setSelectedProduct(searchResults.get(position));
					Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
					startActivity(i);
				}
			}
		}
	};

	public void sortResults(int option) {
		if (sortSelection == option) {
			return;
		}
		
		sortSelection = option;
		sort = getSortString(option);
		
		setSortOptionText(sortSelection);
		
		appData.setSearchSortPosition(sortSelection);
		appData.setSearchResultSort(sort);

		searchRequest.setSort(sort);
		searchRequest.setPage("1");
		searchResults.clear();
		((SearchAdapter) _lv.getAdapter()).cancelLoading();
		_iconicAdapter=new SearchAdapter();
		_lv.setAdapter(_iconicAdapter);
		_iconicAdapter.notifyDataSetChanged();
	}

	public String getSortString(int option) {
		CharSequence key = "";
		
		if(isProductSearch)
			key = SEARCH_SORTING_ITEMS[option];
		else
			key = BROWSE_SORTING_ITEMS[option];
		
		String sortString = "";
		
		if (searchRequest.isRemixSort()) {
			
			if(key.equals(SORT_BESTMATCH_OPTION))
				sortString = "salesRankLongTerm.asc";
			else if(key.equals(SORT_BESTSELLING_OPTION))
				sortString = "salesRankLongTerm.asc";
			else if(key.equals(SORT_BRAND_AZ_OPTION))
				sortString = "name";
			else if(key.equals(SORT_BRAND_ZA_OPTION))
				sortString = "name.desc";
			else if(key.equals(SORT_PRICE_HIGH_LOW_OPTION))
				sortString = "salePrice.desc,name";
			else if(key.equals(SORT_PRICE_LOW_HIGH_OPTION))
				sortString = "salePrice,name";
			
		} else {
			
			if(key.equals(SORT_BESTMATCH_OPTION))
				sortString = "";
			else if(key.equals(SORT_BESTSELLING_OPTION))
				sortString = "bestselling.desc";
			else if(key.equals(SORT_BRAND_AZ_OPTION))
				sortString = "brand.asc";
			else if(key.equals(SORT_BRAND_ZA_OPTION))
				sortString = "brand.desc";
			else if(key.equals(SORT_PRICE_HIGH_LOW_OPTION))
				sortString = "price.desc";
			else if(key.equals(SORT_PRICE_LOW_HIGH_OPTION))
				sortString = "price.asc";
		}

		return sortString;
	}
	
	class SearchAdapter extends EndlessAdapter {		
		SearchAdapter() {
			super(BrowseProductList.this, new ArrayAdapter<Product>(BrowseProductList.this, R.layout.searchresult_row, searchResults) {
				// ICR Pricing
				private TextView seePriceText;
				private TextView price;
				private Typeface typeface;

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					
					System.gc();
					View row = convertView;
					if (row == null) {
						LayoutInflater inflater = getLayoutInflater();
						row = inflater.inflate(R.layout.searchresult_row, parent, false);
					}
					
					row.setVisibility(View.VISIBLE);
					Product p = (Product) searchResults.get(position);

					TextView label = (TextView) row.findViewById(R.id.row_description);
					ImageView icon = (ImageView) row.findViewById(R.id.icon);
					ImageView productRating = (ImageView) row.findViewById(R.id.row_star_rating);
					price = (TextView) row.findViewById(R.id.row_price);
					TextView productRatingText = (TextView) row.findViewById(R.id.row_product_rating_bar_text);
					TextView marketplacedesc = (TextView) row.findViewById(R.id.row_marketplace_desc);	
					TextView rowMarketplaceTitle = (TextView) row.findViewById(R.id.row_marketplace_title);	
					seePriceText = (TextView) row.findViewById(R.id.row_price_seetext);
					
					TextView storeLocation = (TextView)row.findViewById(R.id.store_location);
					TextView storeLocationHeader = (TextView)row.findViewById(R.id.store_location_header);
					
					LinearLayout planPriceLayout = (LinearLayout)row.findViewById(R.id.plan_price_layout);
					TextView planPrice = (TextView)row.findViewById(R.id.plan_price);
					
					if(p.isMarketPlaceAvailable()) {
						StringBuffer program = new StringBuffer();
						program.append("<font color= '#737474'><font size='12'>Sold & Shipped by: </font></font>");
						MarketPlaceDetails marketPlaceDetails = p.getMarketPlaceDetails();
						if(marketPlaceDetails != null) {
							String error = marketPlaceDetails.getErrorDescription();
							//If there is no error error description value is null.
							if(error == null) {
								rowMarketplaceTitle.setVisibility(View.VISIBLE);
								marketplacedesc.setVisibility(View.VISIBLE);
								String sellerName = marketPlaceDetails.getSellerDispName();
							    program.append("<font color= '#333333'><font size='12'>" + sellerName + " </font></font>");
							} 
						}
						
						marketplacedesc.setText(Html.fromHtml(program.toString()));
					} else {
					    rowMarketplaceTitle.setVisibility(View.GONE);
						marketplacedesc.setVisibility(View.GONE);
					}
			        
					// TODO: What the heck is this for
					icon.setMaxHeight(10);
					icon.setMaxWidth(10);
					icon.setMinimumHeight(10);
					icon.setMinimumWidth(10);

					label.setText(p.getName());
					productRating.setImageBitmap(p.getCustomerReviewStarImage(AppData.getContext()));
					
					// ICR Pricing
					typeface = FontLibrary.getFont(R.string.helveticabold, getResources());
				
					if(!isSKUavailable(p.getSku())){
						normalPricing(p);
					}else{
						icrPricing(p.getSku(),p);
					}
					// ICR Pricing ----------
					String reviewAvg = p.getCustomerReviewAverage();
					if(reviewAvg != null) {
						if(reviewAvg.equals("0")) {
							reviewAvg = "0";
						} else {
							if(reviewAvg.length() == 1) {
								reviewAvg += ".0"; 
							} 
						}
						productRatingText.setText(reviewAvg);
					} 
					if(!p.isInStoreAvailablity() && (p.isMarketPlaceAvailable() || p.isOnlineAvailability() || p.isPreorder()|| p.isSpecialOrder())){
						storeLocation.setText("");
						storeLocationHeader.setText("");
						storeLocation.setVisibility(View.GONE);
						storeLocationHeader.setVisibility(View.GONE);
					} else {
						if ((getIntent().getStringExtra(StoreUtils.ZIP_CODE) != null) || (searchRequest.getLatitude() != 0.0)) {
							String location = null;
							if (p.getStoreLocation().size() >= 3) {
								location = p.getStoreLocation().get(0) + ", " + p.getStoreLocation().get(1) + ", " + p.getStoreLocation().get(2);
							} else if (p.getStoreLocation().size() == 2) {
								location = p.getStoreLocation().get(0) + ", " + p.getStoreLocation().get(1);
							} else if (p.getStoreLocation().size() == 1) {
								location = p.getStoreLocation().get(0);
							} else {
								location = "No store Availability";
							}
							
							storeLocation.setVisibility(View.VISIBLE);
							storeLocation.setText(location);
							storeLocationHeader.setVisibility(View.VISIBLE);
							storeLocationHeader.setText("Available For Store Pickup At:");
							
						} else {
							storeLocation.setText("");
							storeLocationHeader.setText("");
							storeLocation.setVisibility(View.GONE);
							storeLocationHeader.setVisibility(View.GONE);
						}
					}
					
					String planPriceValue = p.getPlanPrice();
					if (planPriceValue != null) {
						if (planPriceValue.equals("0") || planPriceValue.equals(".00") || planPriceValue.equals("0.0"))
							planPriceValue = "$0.00";
						else
							planPriceValue = "$" + planPriceValue;
						
						planPriceLayout.setVisibility(View.VISIBLE);
						planPrice.setText(planPriceValue + "*");
					} else {
						planPriceLayout.setVisibility(View.GONE);
					}
					
					if (p.getImageURL() != null) {
						ImageProvider.getBitmapImageOnThread(p.getImageURL(),icon);
					}
					
					return row;
				}
				
				private void normalPricing(Product p) {
					if (p.isOnSale()) {
						
						if(p.isAdvertisedPriceRestriction()) {
							setTextinPriceText("" , getResources().getColor(R.color.icr_striked_price), 13,	0,(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)),typeface);
							seePriceText.setText("click to view price");
							seePriceText.setTextSize(11);
						} else {
							setTextinPriceText("On Sale" , getResources().getColor(R.color.icr_striked_price), 13,	0,(price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)),typeface);
							seePriceText.setTextSize(13);
							seePriceText.setText("$" + p.getSalePrice());
						}
						
						seePriceText.setTextColor(getResources().getColor(R.color.icr_striked_price));
						LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						llp.setMargins(0, 3, 0, 0);
						
						seePriceText.setTypeface(typeface);
						seePriceText.setGravity(Gravity.RIGHT);
						seePriceText.setLayoutParams(llp);
						seePriceText.setTextAppearance(BrowseProductList.this, R.style.icr_see_price_text_bold);
						
					} else {
						String salesPrice = "";
						if(p.isMarketPlaceAvailable()) {
							MarketPlaceDetails placeDetails = p.getMarketPlaceDetails();
							if(placeDetails != null) {
								String error = placeDetails.getErrorDescription();
								if(error == null)
									salesPrice = placeDetails.getPrice();
							}
						} else {
							salesPrice = p.getRegularPrice();
						}
						
						setTextinSeePriceText("$" + salesPrice, getResources().getColor(R.color.icr_normal_price), 13, 0, R.style.icr_see_price_text_bold, typeface); 
						setTextinPriceText("", 0, 0, 0, (price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)), typeface);
					}
				}
				
				private void icrPricing(String sku, Product p) {
					
					boolean isTrue=hidePriceMap.get(sku);
					if(!isTrue){
						setTextinSeePriceText("See price at checkout", getResources().getColor(R.color.black), 10, 3, R.style.icr_see_price_text_normal, typeface);
						setTextinPriceText("$" + p.getSalePrice(), getResources().getColor(R.color.icr_striked_price), 13, 0, (price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG), typeface);
					
					}else{
						setTextinPriceText("See Price at",getResources().getColor(R.color.icr_striked_price),10, 0, (price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)), typeface); 
						setTextinSeePriceText("Checkout",getResources().getColor(R.color.icr_striked_price), 10, 0, R.style.icr_see_price_text_bold, typeface);
					}
				}
				
				private boolean isSKUavailable(String sku) {
					if(hidePriceMap==null){
						hidePriceMap = IcrUtil.getIcrHidePrice();
					}
					return hidePriceMap.containsKey(sku);
				}

				private void setTextinSeePriceText(String text, int colour, int size, int marginTop, int textStyle,Typeface typeface) {

					seePriceText.setText(text);
					seePriceText.setTextColor(colour);
					seePriceText.setTextSize(size);
					LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					llp.setMargins(0, marginTop, 0, 0);
					
					seePriceText.setTypeface(typeface);
					seePriceText.setGravity(Gravity.RIGHT);
					seePriceText.setLayoutParams(llp);
					seePriceText.setTextAppearance(BrowseProductList.this, textStyle);
				}

				private void setTextinPriceText(String text, int colour, int size, int marginTop, int paintFlag,Typeface typeface) {
					if(text.equals(""))
						price.setVisibility(View.GONE);
					else
						price.setVisibility(View.VISIBLE);
					
					price.setText(text);
					price.setTextColor(colour);
					price.setTypeface(typeface);
					price.setTextSize(size);
					price.setPaintFlags(paintFlag);
				
				}
			});
		}

		@Override
		protected void appendNextPage() {
			searchResults.addAll(searchRequest.getSearchResultList());
			//Filtering the market place not orderable values
			filterSearchResult(searchResults);
			
			if(searchRequest.getFacetCategories() == null || searchRequest.getFacetCategories().size() == 0){
				header_cart.setVisibility(View.VISIBLE);
				narrow_btn.setVisibility(View.GONE);
				/* if(CartPersister.getCartProducts(BrowseProductList.this).getTotalQuantity() > 0){
					 header_cart_badge.setVisibility(View.VISIBLE);
				 }*/
			} else {
				header_cart.setVisibility(View.GONE);
				header_cart_badge.setVisibility(View.GONE);
				narrow_btn.setVisibility(View.VISIBLE);
			}
			if (searchResults.size() == 0) {
				LinearLayout header = (LinearLayout) findViewById(R.id.searchresult_header_layout);
				header.setVisibility(View.GONE);
				findViewById(R.id.search_result_empty).setVisibility(View.VISIBLE);
				findViewById(R.id.search_list).setVisibility(View.GONE);
			}
		}

		@Override
		protected boolean fetchNextPage() throws Exception {
			System.gc();
			searchRequest.setPage(String.valueOf(getCurrentPage()));
			searchRequest.setZipCode(getIntent().getStringExtra(StoreUtils.ZIP_CODE));
			searchRequest.setLatitude(getIntent().getDoubleExtra(StoreUtils.LATITUDE, 0.0));
			searchRequest.setLongitude(getIntent().getDoubleExtra(StoreUtils.LONGITUDE, 0.0));
			searchRequest.runSearch();
			
			return (getCurrentPage() != Integer.valueOf(searchRequest.getTotalPages()) || searchRequest.hasNextPage());
		}
	}

	private void filterSearchResult(List<Product> searchResults) {
		if(searchResults == null)
			return;
		List<Product> tempForFilter = new ArrayList<Product>();
		
		int size = searchResults.size();
		
		for(int i = 0; i < size; i++) {
			Product product = (Product) searchResults.get(i);
			if(product.isMarketPlaceAvailable()){
				if(product.getOrderable().equals("Available")) {
					tempForFilter.add(product);
				}
			} else
				tempForFilter.add(product);
		}
		
		this.searchResults.clear();
		this.searchResults.addAll(tempForFilter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindDrawables(findViewById(R.id.searchresult));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			if (!(view instanceof ListView)) {
				((ViewGroup) view).removeAllViews();
			}
		}
	}
	    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
