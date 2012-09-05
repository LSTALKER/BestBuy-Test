package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.DealCategory;
import com.bestbuy.android.data.Offer;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.icr.util.IcrUtil;
import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.util.EndlessAdapter;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.FontLibrary;
import com.bestbuy.android.util.ImageProvider;

/**
 * Lists all deal categories
 * 
 * @author Recursive Awesome
 * 
 */
public class DealCategoryList extends MenuListActivity {
	private String cursor;
	private List<Offer> offers;
	private DealCategory selectedDealCategory;
	
	//ICR Prising
	private HashMap<String, Boolean> hidePriceMap;

	static final Comparator<Product> ALPHA_NAME_PRODUCT_ORDER = new Comparator<Product>() {
		public int compare(Product e1, Product e2) {
			return e1.getName().compareTo(e2.getName());
		}
	};

	OnItemClickListener productClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			if (position >= 0 && position < offers.size()) {
				final Offer offer = offers.get(position);

				Map<String, String> params = new HashMap<String, String>();
				params.put("value", AppData.BBY_OFFERS_MOBILE_WEEKLY_AD_CHANNEL + ";" + offer.getProductKey());
				EventsLogging.fireAndForget(EventsLogging.INTERNAL_CAMPAIGN_CLICK, params);
				
				if (offer.getSkus().size() > 1) {
					// If there is a list of skus, show the list of products
					/*SearchRequest searchRequest = new SearchRequest();
					searchRequest.setSkus(offer.getSkus());
					appData.setSearchRequest(searchRequest);
					Intent i = new Intent(v.getContext(), SearchResultList.class);
					startActivity(i);*/
					
					Intent i = new Intent(v.getContext(), SearchResultList.class);
					i.putExtra(AppData.PRODUCT_SEARCH_QUERY, getSkusString(offer.getSkus()));
					startActivity(i);
					
				} else if (offer.getSkus().size() == 1) {
					appData.setSelectedProduct(offer);
					/*Intent i = new Intent(v.getContext(), ProductDetail.class);*/
					Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
					startActivity(i);
				} else {
					// If there is no sku, show the website
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(offer.getUrl()));
					startActivity(i);
				}
			}
		}
	};
	
	private String getSkusString(List<String> skuList) {
		StringBuilder idString = new StringBuilder();
		int index = skuList.size();
		for (int i = 0; i < skuList.size(); i++) {
			idString.append(skuList.get(i));
			if (i != --index) {
				idString.append("+");
			}
		}
		
		return idString.toString();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		offers = new ArrayList<Offer>();
		setContentView(R.layout.deal_category_list);

		if (savedInstanceState != null) {
			finish();
			return;
		}

		selectedDealCategory = appData.getDealCategory();

		final TextView dealCategoryHeader = (TextView) findViewById(R.id.deal_category_header);
		dealCategoryHeader.setText("Deals From " + selectedDealCategory.getName());

		final ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setAdapter(new DealsAdapter());
		lv.setOnItemClickListener(productClickedHandler);
	}

	class DealsAdapter extends EndlessAdapter {
		List<Offer> loadedDeals;

		DealsAdapter() {
			super(DealCategoryList.this, new ArrayAdapter<Offer>(DealCategoryList.this, R.layout.searchresult_row, offers) {
		
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
					Offer offer = (Offer) offers.get(position);

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
					
					if(offer.isMarketPlaceAvailable()) {
						StringBuffer program = new StringBuffer();
						program.append("<font color= '#737474'><font size='12'>Sold & Shipped by: </font></font>");
						MarketPlaceDetails marketPlaceDetails = offer.getMarketPlaceDetails();
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
			        
					
					icon.setMaxHeight(10);
					icon.setMaxWidth(10);
					icon.setMinimumHeight(10);
					icon.setMinimumWidth(10);
					label.setText(offer.getName());
					productRating.setImageBitmap(offer.getCustomerReviewStarImage(AppData.getContext()));
					
					// ICR Pricing
					typeface = FontLibrary.getFont(R.string.helveticabold, getResources());
				
					if(offer.getSkus() != null && offer.getSkus().size()>0){
						if (offer.getSkus().size() == 1) {
							productRating.setImageBitmap(offer.getCustomerReviewStarImage(AppData.getContext()));
							productRating.setVisibility(View.VISIBLE);
							productRatingText.setVisibility(View.VISIBLE);
						} else {
							productRating.setVisibility(View.GONE);
							productRatingText.setVisibility(View.GONE);
						}
						if(!isSKUavailable(offer.getSku())){
							normalPricing(offer);
						}else{
							icrPricing(offer.getSku(),offer);
						}
					}
					// ICR Pricing ----------
					String reviewAvg = offer.getCustomerReviewAverage();
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
					

					if (offer.getImageURL() != null) {
						ImageProvider.getBitmapImageOnThread(offer.getImageURL(),icon);
					}
					
					String planPriceValue = offer.getPlanPrice();
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
						seePriceText.setTextAppearance(DealCategoryList.this, R.style.icr_see_price_text_bold);
						
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
					seePriceText.setTextAppearance(DealCategoryList.this, textStyle);
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
			loadedDeals = new ArrayList<Offer>();
		}

		@Override
		protected void appendNextPage() {
			offers.addAll(loadedDeals);
		}

		@Override
		protected boolean fetchNextPage() throws Exception {
			final SearchRequest searchRequest = new SearchRequest();
			searchRequest.setCursor(cursor);
			loadedDeals = searchRequest.getOffers(AppData.BBY_OFFERS_MOBILE_WEEKLY_AD_CHANNEL, selectedDealCategory.getKey());
			cursor = searchRequest.getCursor();
			return (cursor != null);
		}
		
		public void onCancelLoading() {
			if(offers == null) {
				cancelLoading();
				DealCategoryList.this.finish();
			} else if (offers.isEmpty()) {
				cancelLoading();
				DealCategoryList.this.finish();
			}
		}
	}
}
