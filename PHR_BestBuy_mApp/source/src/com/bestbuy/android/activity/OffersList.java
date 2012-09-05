package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Offer;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.ui.BBYProgressDialog;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.ImageProvider;

/**
 * Displays a list of product offers.
 * 
 * @author Recursive Awesome
 * 
 */
public class OffersList extends MenuListActivity {

	private List<Offer> offersList;
	private final String TAG = this.getClass().getName();

	private ListView lv;
	private OffersAdapter offersAdapter;
	private SearchRequest searchRequest;
	private long timestamp = 0;
	private View lastRow;
	private boolean loading = true;
	private int previousTotal = 0;
	private boolean isLoadingMore = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offers_tab);
		searchRequest = new SearchRequest();
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(offersClickedHandler);
		lv.setOnScrollListener(onScrollListener);
		lv.setItemsCanFocus(false);
		findViewById(android.R.id.empty).setVisibility(View.GONE);
		offersAdapter = new OffersAdapter();		
	}

	OnItemClickListener offersClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Offer offer = offersList.get(position);
			//appData.setSelectedProduct(offer);
			List<String> promotionSkus = offer.getSkus();
			/*Intent i = new Intent(OffersList.this, SpecialOffersDetail.class);
			startActivity(i);*/
			if (promotionSkus.size() >= 1) {
				// If there is a list of skus, show the list of products
				/*SearchRequest searchRequest = new SearchRequest();
				searchRequest.setSkus(promotionSkus);
				appData.setSearchRequest(searchRequest);*/
				//Intent i = new Intent(v.getContext(), SearchResultList.class);
				appData.setSelectedProduct(offer);
				Intent i = new Intent(OffersList.this, SpecialOffersDetail.class);
				startActivity(i);
			} else if (promotionSkus.size() == 1) {
				try {
					// load the product to show by inflating the data
					// from the remix data string
					appData.setSelectedProduct(offer);
					/*Intent i = new Intent(v.getContext(), ProductDetail.class);*/
					Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
					startActivity(i);
				} catch (Exception ex) {
					BBYLog.printStackTrace(TAG, ex);
					BBYLog.e(TAG, "Exception in Home:showPromotions() loading promotion: " + offer.getProductKey());
				}
			} else {
				
				// If there is no sku, show the website
				/*Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(promotion.getUrl()));
				startActivity(i);*/
				Intent intent = new Intent(v.getContext(), MDOTProductDetail.class);
				intent.putExtra("mDotURL", offer.getUrl());
				startActivity(intent);
			}
		}
	};

	@Override          
	public void onResume() {
		super.onResume();
		if (getElapsedDays() >= 1) {
			executeOffersSearchTask();
		}
	}

	public double getElapsedDays() {
		Long currentTimestamp = new Date().getTime();
		final int msPerDay = 24 * 60 * 60 * 1000;
		Double elapsedDays = Math.floor((currentTimestamp - timestamp) / ((double)msPerDay));
		timestamp = currentTimestamp;
		return elapsedDays;
	}

	private void executeOffersSearchTask() {
		showDialog(LOADING_DIALOG);
		new SpecialOffersTask(this).execute();
	}

	private void showView() {
		isLoadingMore = false;
		
		if (lv.getChildCount() == 0) {
			LayoutInflater inflater = getLayoutInflater();
			lastRow = inflater.inflate(R.layout.list_pagination_load, null);
			if (searchRequest.getCursor() != null)
				lv.addFooterView(lastRow, null, false);
			setListAdapter(offersAdapter);
		} else
			offersAdapter.notifyDataSetChanged();
		if (searchRequest.getCursor() == null)
			lv.removeFooterView(lastRow);
		
		if(offersList != null && !offersList.isEmpty()) {
			findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
		}
	}

	private class SpecialOffersTask extends BBYAsyncTask {
		public SpecialOffersTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		protected void onPreExecute() {
			if (offersList == null) {
				offersList = new ArrayList<Offer>();
			}
		}

		@Override
		public void doFinish() {
			if(!isFinishing())
				new FeatureOffersTask(activity).execute();
		}

		@Override
		public void doTask() throws Exception {
			offersList.addAll(searchRequest.getOffers(AppData.BBY_OFFERS_MOBILE_FEATURED_OFFERS_CHANNEL));
			/** The first Featured offer is displayed in the home screen hence we are removing it **/
			offersList.remove(0);
			while (searchRequest.getCursor() != null) {
				offersList.addAll(searchRequest.getOffers(AppData.BBY_OFFERS_MOBILE_FEATURED_OFFERS_CHANNEL));
			}
		}

		@Override
		public void doError() {
			removeDialog(LOADING_DIALOG);
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					showDialog(LOADING_DIALOG);
					new SpecialOffersTask(activity).execute();
				}
			}, new OnCancel() {
				
				public void onCancel() {
					finish();
				}
			});
		}
	}

	private class FeatureOffersTask extends BBYAsyncTask {
		public FeatureOffersTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		protected void onPreExecute() {
			if (offersList == null) {
				offersList = new ArrayList<Offer>();
			}
		}

		@Override
		public void doFinish() {
			removeDialog(LOADING_DIALOG);
			showView();
		}

		@Override
		public void doTask() throws Exception {
			offersList.addAll(searchRequest.getOffers(AppData.BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL));			
		}
		
		@Override
		public void doError() {
			removeDialog(LOADING_DIALOG);
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					if(!isLoadingMore)
						showDialog(LOADING_DIALOG);
					
					new FeatureOffersTask(activity).execute();
				}
			}, new OnCancel() {
				
				public void onCancel() {
					if(isLoadingMore) {
						if(offersList == null || offersList.isEmpty())
							finish();
						else {
							if(lv != null && lastRow != null)
								lv.removeFooterView(lastRow);
						}
					} else
						finish();
				}
			});
		}
	}
	
	class OffersAdapter extends ArrayAdapter<Offer> {

		OffersAdapter() {
			super(OffersList.this, R.layout.offers_row, offersList);
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			return offersList.size();
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
				row = inflater.inflate(R.layout.offers_row, parent, false);
				row.setTag(R.id.offers_icon, row.findViewById(R.id.offers_icon));
				row.setTag(R.id.offers_description, row.findViewById(R.id.offers_description));
			}

			Offer p = offersList.get(position);
			TextView offersDescription = (TextView) row.getTag(R.id.offers_description);
			ImageView icon = (ImageView) row.findViewById(R.id.offers_icon);
			String imageUrl = null;
			if (p != null) {
				offersDescription.setText(p.getTitle());
				imageUrl = p.getImageURL();
			}

			if (imageUrl != null && imageUrl.length() > 0) {
				ImageProvider.getBitmapImageOnThread(imageUrl, icon);
			}
			return row;
		}
	}
	
	private OnScrollListener onScrollListener = new OnScrollListener() {
		 
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					previousTotal = totalItemCount;
					loading = false;
				}
			}
			if ( !loading && searchRequest.getCursor() != null && firstVisibleItem == (totalItemCount - visibleItemCount)) {
				isLoadingMore = true;
				new FeatureOffersTask(OffersList.this).execute();
				loading = true;
			}
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new BBYProgressDialog(this);
	}
}
