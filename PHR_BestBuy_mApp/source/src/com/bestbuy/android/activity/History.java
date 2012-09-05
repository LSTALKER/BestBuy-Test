package com.bestbuy.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.PhotoSearch;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.data.SkuManager;
import com.bestbuy.android.icr.util.IcrUtil;
import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EndlessAdapter;
import com.bestbuy.android.util.FontLibrary;
import com.bestbuy.android.util.ImageProvider;

/**
 * Displays a list of search results for a particular product category
 * 
 * @author Recursive Awesome
 */
public class History extends MenuActivity {

	final String TAG = this.getClass().getName();

	private ListView photoSearchList;

	private ImageView tab1;
	private ImageView tab2;
	private ImageView tab3;

	private AppData appData;
	private List<Product> searchResults;
	private ListView historyList;
	private ListView mobileScanList;
	private SearchRequest searchRequest;
	private int position;

	//ICR Prising
	private HashMap<String, Boolean> hidePriceMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		_context = this;
		setContentView(R.layout.history);

		tab1 = (ImageView) findViewById(R.id.history_tab_1);
		tab2 = (ImageView) findViewById(R.id.history_tab_2);
		tab3 = (ImageView) findViewById(R.id.history_tab_3);

		photoSearchList = (ListView) findViewById(R.id.history_photo_search_list);
		photoSearchList.setOnItemClickListener(photoSearchClickedHandler);
		historyList = (ListView) findViewById(R.id.history_search_list);
		mobileScanList = (ListView) findViewById(R.id.history_mobile_scan_list);
		historyList.setOnItemClickListener(searchResultClickedHandler);
		mobileScanList.setOnItemClickListener(searchResultClickedHandler);

		//Context Menu to Remove List Items
		registerForContextMenu(historyList);
		registerForContextMenu(mobileScanList);
		registerForContextMenu(photoSearchList);
		
		searchResults = new ArrayList<Product>();
		searchRequest = new SearchRequest();

		initValues();
		setUpTabs();
		loadHistory();
	}

	class SearchAdapter extends EndlessAdapter {
		SearchAdapter() {

			super(History.this, new ArrayAdapter<Product>(History.this, R.layout.searchresult_row, searchResults) {
				// ICR Pricing
				private TextView seePriceText;
				private TextView price;
				private Typeface typeface;

				@Override
				public View getView(int position, View convertView,	ViewGroup parent) {
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
						//BBYLog.v("BBY", "Normal Pricing");
					}else{
						icrPricing(p.getSku(),p);
						//BBYLog.v("BBY", "ICR Pricing");
					}
					// ICR Pricing ----------
					String reviewAvg = p.getCustomerReviewAverage();
					if(reviewAvg != null) {
						if(reviewAvg.equals("0")) {
							reviewAvg = "";
						} else {
							if(reviewAvg.length() == 1) {
								reviewAvg += ".0"; 
							} 
						}
						productRatingText.setText(reviewAvg);
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
						seePriceText.setTextAppearance(History.this, R.style.icr_see_price_text_bold);

					} else {
						String salesPrice = "";
						if(p.isMarketPlaceAvailable()) {
							MarketPlaceDetails placeDetails = p.getMarketPlaceDetails();
							salesPrice = placeDetails.getPrice();
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
					//Log.v("BBY","H size"+hidePriceMap.size());
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
					seePriceText.setTextAppearance(History.this, textStyle);
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
			if (searchRequest.getFacetCategories() == null || searchRequest.getFacetCategories().size() == 0) {
				findViewById(R.id.header_cart).setVisibility(View.VISIBLE);
				/*if (CartPersister.getCartProducts(History.this).getTotalQuantity() > 0) {
					findViewById(R.id.header_cart_badge).setVisibility(	View.VISIBLE);
				}*/
			} else {
				findViewById(R.id.header_cart).setVisibility(View.GONE);
				findViewById(R.id.header_cart_badge).setVisibility(View.GONE);
				findViewById(R.id.search_result_narrow).setVisibility(View.VISIBLE);
			}
			if (searchResults.size() == 0) {
				findViewById(R.id.history_search_result_empty).setVisibility(View.VISIBLE);
				historyList.setVisibility(View.GONE);
				mobileScanList.setVisibility(View.GONE);
			}
		}

		@Override
		protected boolean fetchNextPage() throws Exception {
			searchRequest.setPage(String.valueOf(getCurrentPage()));
			searchRequest.runSearch();
			return (getCurrentPage() != Integer.valueOf(searchRequest.getTotalPages()) || searchRequest.hasNextPage());
		}
		
		@Override
		public void onCancelLoading() {
			if(searchResults == null) {
				cancelLoading();
				finish();
			} else if (searchResults.isEmpty()) {
				cancelLoading();
				finish();
			}
		}
	}

	public void loadHistory() {		
		historyList.setVisibility(View.VISIBLE);
		photoSearchList.setVisibility(View.GONE);
		mobileScanList.setVisibility(View.GONE);
		searchResults.clear();
		searchRequest = new SearchRequest();
		searchRequest.setSkus(SkuManager.getRecentSkusAsList(this));
		appData.setSearchRequest(searchRequest);
		historyList.setAdapter(new SearchAdapter());
	}

	public void loadScans() {	
		historyList.setVisibility(View.GONE);
		mobileScanList.setVisibility(View.VISIBLE);
		photoSearchList.setVisibility(View.GONE);
		searchResults.clear();
		searchRequest = new SearchRequest();
		searchRequest.setSkus(SkuManager.getScannedSkusAsList(this));
		appData.setSearchRequest(searchRequest);
		mobileScanList.setAdapter(new SearchAdapter());
	}

	public void loadPhotoSearches() {
		photoSearchList.setAdapter(new PhotoSearchAdapter());
		historyList.setVisibility(View.GONE);
		mobileScanList.setVisibility(View.GONE);
		if (!appData.getPhotoSearches().isEmpty()) {
			photoSearchList.setVisibility(View.VISIBLE);
			findViewById(R.id.history_search_result_empty).setVisibility(View.GONE);
		} else {
			photoSearchList.setVisibility(View.GONE);
			findViewById(R.id.history_search_result_empty).setVisibility(View.VISIBLE);
		}
	}

	//TODO: this is duplicated from photosearchlist, can we consolidate?
	class PhotoSearchAdapter extends ArrayAdapter<PhotoSearch> {
		PhotoSearchAdapter() {
			super(History.this, R.layout.iqengines_list_row, appData.getPhotoSearches());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			PhotoSearch photoSearch = appData.getPhotoSearches().get(position);
			View row = convertView;	
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.iqengines_list_row, parent, false);
			}
			TextView name = (TextView) row.findViewById(R.id.iqengines_list_row_description);
			name.setText(photoSearch.getDescription());
			ImageView icon = (ImageView) row.findViewById(R.id.iqengines_list_row_icon);
			Bitmap bm = null;
			try {
				bm = getPhotoSearchImage(photoSearch);
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				BBYLog.e(TAG, "Exception getting the image for photo search list: " + ex.getMessage());
			}
			if (bm == null) {
				bm = BitmapFactory.decodeResource(icon.getResources(), R.drawable.comingsoonoff);
			}

			ProgressBar pb = (ProgressBar) row.findViewById(R.id.workingProgress);
			if(!photoSearch.isAnalyzing()) {
				pb.setVisibility(View.INVISIBLE);
			}
			icon.setImageBitmap(bm);
			return row;
		}
	}

	//TODO: this is duplicated from photosearchlist, can we consolidate?
	private Bitmap getPhotoSearchImage(PhotoSearch ps) {
		try {
			/*Removed Unused
			 * File f = this.getFilesDir();*/ 
			FileInputStream fis = this.openFileInput(ps.getId());
			ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
			DataOutputStream outWriter = new DataOutputStream(bufStream);
			int ch;
			while((ch = fis.read()) != -1)
				outWriter.write(ch);

			outWriter.close();
			byte[] data = bufStream.toByteArray();
			bufStream.close();
			fis.close();
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			BBYLog.e(TAG, "Exception PhotoSearchList.getPhotoSearchImage(). We have not found the image!");
		}
		return null;
	}

	private void setUpTabs() {
		tab1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab1.setImageResource(R.drawable.tab_recent_down);
				loadHistory();
				resetTab2();
				resetTab3();
			}
		});

		tab2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab2.setImageResource(R.drawable.tab_qr_down);
				loadScans();
				resetTab1();
				resetTab3();
			}
		});

		tab3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab3.setImageResource(R.drawable.tab_photo_down);
				loadPhotoSearches();
				resetTab1();
				resetTab2();
			}
		});
	}

	//TODO: Change tab images
	private void resetTab1() {
		tab1.setImageResource(R.drawable.tab_recent_norm);
	}

	private void resetTab2() {
		tab2.setImageResource(R.drawable.tab_qr_norm);
	}

	private void resetTab3() {
		tab3.setImageResource(R.drawable.tab_photo_norm);
	}

	private void initValues() {
		tab1.setImageResource(R.drawable.tab_recent_down);
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
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			if (position >= 0 && position < searchResults.size()) {
				appData.setSelectedProduct((Product) searchResults.get(position));
				/*Intent i = new Intent(v.getContext(), ProductDetail.class);*/
				Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
				startActivity(i);
			}
		}
	};

	OnItemClickListener photoSearchClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			List<PhotoSearch> photoSearches = appData.getPhotoSearches();
			if (position >= 0 && position < photoSearches.size()) {
				//SearchRequest searchRequest = new SearchRequest();
				//searchRequest.setFreeText(photoSearches.get(position).getDescription());
				//searchRequest.setPage("1");
				//appData.setSearchRequest(searchRequest);
				Intent i = new Intent(History.this, SearchResultList.class);
				i.putExtra(AppData.PRODUCT_SEARCH_QUERY, photoSearches.get(position).getDescription());
				startActivity(i);
				//i.putExtra(StoreUtils.IS_PRODUCT_SEARCH, true);
			}
		}
	};
	
	//Context Menu to Remove List Items
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem item = menu.add("Remove item");
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		position = info.position;
		View row = info.targetView;
		TextView productName = null;
		if (historyList.getVisibility() == 0 || mobileScanList.getVisibility() == 0)
			productName = (TextView) row.findViewById(R.id.row_description);
		else
			productName = (TextView) row
					.findViewById(R.id.iqengines_list_row_description);
		menu.setHeaderTitle(productName.getText());
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (historyList.getVisibility() == 0) {
					SearchAdapter adapter = (SearchAdapter) historyList
							.getAdapter();
					Product product = searchResults.get(position);
					SkuManager.removeFromRecentSkus(History.this, product.getSku());
					searchResults.remove(position);
					adapter.notifyDataSetChanged();
				}else if (mobileScanList.getVisibility() == 0) {
					SearchAdapter adapter = (SearchAdapter) mobileScanList
							.getAdapter();
					Product product = searchResults.get(position);
					SkuManager.removeFromScannedSkus(History.this, product.getSku());
					searchResults.remove(position);
					adapter.notifyDataSetChanged();
				} else {
					@SuppressWarnings("unchecked")
					ArrayAdapter<PhotoSearch> adapter = (ArrayAdapter<PhotoSearch>) photoSearchList
							.getAdapter();
					adapter.remove(adapter.getItem(position));
					adapter.notifyDataSetChanged();
				}
				return true;
			}
		});
	}
}
