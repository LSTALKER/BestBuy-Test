package com.bestbuy.android.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Offer;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.ImageProvider;


public class SpecialOffersDetail extends MenuListActivity {

	private static final Void Void = null;
	private Offer _offer;
	private ImageView _icon;
	private DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private ListView lv;
	private SpecialOffersAdapter specialOffersAdapter;
	List<String> promotionSkus;
	List<Product> promotions;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_offer = (Offer)appData.getSelectedProduct();
		promotionSkus = _offer.getSkus();
		promotions = new ArrayList<Product>();
		showView();
		executeSpecialOffersSearchTask();
	}

	private void setIconImage() {
		if (_icon != null && _offer != null && !isFinishing()) {
			ImageProvider.getBitmapImageOnThread(_offer.getBestImageURL(), _icon);
		}
	}
	
	OnItemClickListener specialOffersClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			if (position >= 0 && position < promotions.size()) {
				appData.setSelectedProduct((Product) promotions.get(position));
				/*Intent i = new Intent(v.getContext(), ProductDetail.class);*/
				Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
				startActivity(i);
			}
		}
	};

	private void showView() {
		setContentView(R.layout.special_offers_detail);

		_icon = (ImageView) findViewById(R.id.special_offers_icon);

		setIconImage();
		
		if(_offer.getChannelKeys().contains(Offer.GAMING_CHANNEL)){
			((TextView) findViewById(R.id.special_offers_gaming_label)).setVisibility(View.VISIBLE);
		}else{
			((TextView) findViewById(R.id.special_offers_gaming_label)).setVisibility(View.GONE);
		}

		//((TextView) findViewById(R.id.special_offers_header)).setText(_offer.getTitle());	
		((TextView) findViewById(R.id.special_offers_title)).setText(_offer.getTitle());
		//((TextView) findViewById(R.id.special_offers_expiration_date)).setText(_offer.getPlatform());
		((TextView) findViewById(R.id.special_offers_expiration_date)).setText("Offer expires " + df.format(_offer.getEndDate()));	
		
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(specialOffersClickedHandler);
		lv.setItemsCanFocus(false);
		specialOffersAdapter = new SpecialOffersAdapter();
		setListAdapter(specialOffersAdapter);
		
		((TextView) findViewById(android.R.id.empty)).setVisibility(View.GONE);
		
	}
	
	private void executeSpecialOffersSearchTask() {
		new SpecialOffersTask(this).execute(Void, Void, Void);
	}
	
	private class SpecialOffersTask extends BBYAsyncTask {
		SearchRequest searchRequest;
		public SpecialOffersTask(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			if (promotions == null) {
				promotions = new ArrayList<Product>();
			}
		}

		@Override
		public void doFinish() {
			if (!isFinishing()) {		
				promotions.addAll(searchRequest.getSearchResultList());		
				showView();
				((LinearLayout) findViewById(R.id.special_offers_tab_progress)).setVisibility(View.GONE);
				if(promotions.size() == 0){
					((TextView) findViewById(android.R.id.empty)).setVisibility(View.VISIBLE);
				}
			}
		}

		@Override
		public void doTask() throws Exception {
			searchRequest = new SearchRequest();
			searchRequest.setSkus(promotionSkus);
			appData.setSearchRequest(searchRequest);
			searchRequest.setPage(String.valueOf(1));
			searchRequest.runSearch();
		}
		
		@Override
		public void doReconnect() {
			new SpecialOffersTask(activity).execute();
		}

		@Override
		public void doCancelReconnect() {
			// TODO Auto-generated method stub
			super.doCancelReconnect();
			finish();
		}
	}

	class SpecialOffersAdapter extends ArrayAdapter<Product> {

		SpecialOffersAdapter() {
			super(SpecialOffersDetail.this, R.layout.row, promotions);
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			return promotions.size();
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.row, parent, false);
			}
			Product promotion = promotions.get(position);

			TextView label = (TextView) row.findViewById(R.id.row_description);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			ImageView productRating = (ImageView) row.findViewById(R.id.row_star_rating);
			TextView price = (TextView) row.findViewById(R.id.row_price);
			TextView productRatingText = (TextView) row.findViewById(R.id.row_product_rating_bar_text);

			// TODO: What the heck is this for
			icon.setMaxHeight(10);
			icon.setMaxWidth(10);
			icon.setMinimumHeight(10);
			icon.setMinimumWidth(10);

			label.setText(promotion.getName());
			productRating.setImageBitmap(promotion.getCustomerReviewStarImage(AppData.getContext()));
			productRating.setVisibility(View.VISIBLE);
			productRatingText.setVisibility(View.VISIBLE);

			if (promotion.isOnSale()) {
				price.setText("On Sale\n$" + promotion.getSalePrice());
				price.setTextSize(13);
				price.setTextColor(Color.RED);
			} else {
				price.setTextColor(Color.BLUE);
				price.setTextSize(13);
				price.setText("$" + promotion.getRegularPrice());
			}
			
			String reviewAvg = promotion.getCustomerReviewAverage();
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
			
			if (promotion.getImageURL() != null) {
				ImageProvider.getBitmapImageOnThread(promotion.getImageURL(), icon);
			}
			return row;
		}
	}
}
