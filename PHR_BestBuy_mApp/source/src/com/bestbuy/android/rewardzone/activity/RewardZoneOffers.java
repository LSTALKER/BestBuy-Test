package com.bestbuy.android.rewardzone.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import net.oauth.OAuthAccessor;
import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuListActivity;
import com.bestbuy.android.activity.RewardZone;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZOffer;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.rewardzone.library.util.RZOfferDetailsParser;
import com.bestbuy.android.rewardzone.library.util.RewardZoneDateStringUtils;
import com.bestbuy.android.rewardzone.library.util.TokenHelper;
import com.bestbuy.android.rewardzone.library.util.preference.RZSharedPreference;
import com.bestbuy.android.rewardzone.logic.RZOfferListLogic;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.UTFCharacters.UTF;

/**
 * Displays a list of RZOffer offers.
 * 
 * @author sharmila_j
 * 
 */
public class RewardZoneOffers extends MenuListActivity {

	private final String TAG = this.getClass().getName();
	
	private List<RZOfferDetails> offersList;
	private List<RZOfferDetails> offerDetailsList;
	private static final Void Void = null;
	private RZAccount rzAccount;
	private OAuthAccessor oauthAccessor;
	private String accessToken;
	private Context context;
	private View lastRow;
	private LinearLayout progressBar;
	private int requestPageNumber = 0;
	private String memberId;
	
	// Get BBY Offer Implementation : 24/09/2011
	
	private SearchRequest searchRequest;
	private boolean offersLoaded = false;
	private boolean reloadFlag = false;
	private boolean isLoadingOffers= false;
	private List<RZOffer> bbyOffersList;
	private List<RZOffer> sortedBBYOffersList;
	private List<Object> totalOfferList=null;
	private String cursor;
	
	//End
	private OffersAdapter offersAdapter;

	private ListView offersListView;
	private SimpleDateFormat bbyOfferdateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		rzAccount = appData.getRzAccount();
		oauthAccessor = appData.getOAuthAccessor();
		accessToken = oauthAccessor.accessToken;
		setContentView(R.layout.reward_zone_offers);
		
		TokenHelper tokenHelper = TokenHelper.getTokenInstance();
		tokenHelper.setToken(accessToken);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_OFFERS_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay());
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);

		offersListView = (ListView) findViewById(android.R.id.list);
		offersListView.setOnScrollListener(onScrollListener);
		offersListView.setOnItemClickListener(offersClickedHandler);

		if (rzAccount == null) {
			Intent i = new Intent(this, RewardZone.class);
			startActivity(i);
			finish();
		} else {
			executeOffersSearchTask();
		}

	}

	private void showView() {
		if (offerDetailsList != null && offerDetailsList.size() != 20)
		{
			offersListView.removeFooterView(lastRow);
		}
		 
		progressBar = (LinearLayout) this.findViewById(R.id.reward_zone_offers_progress);
		progressBar.setVisibility(View.GONE);

		if (totalOfferList != null && totalOfferList.size() > 0) {

			if (offersAdapter == null) {
				offersAdapter = new OffersAdapter();
				LayoutInflater inflater = getLayoutInflater();
				lastRow = inflater.inflate(R.layout.last_row, null);
				if(offersList.size() == 20)
					offersListView.addFooterView(lastRow, null, false);
				setListAdapter(offersAdapter);
			} else {
				if(reloadFlag){
					if(offersList.size() == 20){
						offersListView.addFooterView(lastRow, null, false);
						reloadFlag=false;
					}
				}
				offersAdapter.notifyDataSetChanged();
			}
		}
	}
	
	OnItemClickListener offersClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {

			// Get BBY Offer Implementation : 24/09/2011
			if(totalOfferList.get(position).getClass().getSimpleName().equals("RZOfferDetails")){
				RZOfferDetails offerDetails = (RZOfferDetails) totalOfferList.get(position);
				Intent i;
				boolean optedIn = offerDetails.isOptedin();
				i = optedIn ? new Intent(context, RewardZoneOfferInstructions.class)
						: new Intent(context, RewardZoneOffer.class);
				i.putExtra("offer", offerDetails);
				startActivity(i);
			}
			else{
				RZOffer offerDetails = (RZOffer) totalOfferList.get(position);
				Intent i=new Intent(context, RewardZoneBBYOffers.class);								
				i.putExtra("offer", offerDetails);
				startActivity(i);
			}			
			
			//End
			
			
			/*RZOfferDetails offerDetails = offersList.get(position);

			// Navigate to various activity with respect to the redemption.
			Intent i;
			boolean optedIn = offerDetails.isOptedin();
			i = optedIn ? new Intent(context, RewardZoneOfferInstructions.class)
					: new Intent(context, RewardZoneOffer.class);
			i.putExtra("offer", offerDetails);
			startActivity(i);*/
		}
	};


	@Override
	protected void onResume() {
		super.onResume();
		if (RZSharedPreference.offerOptedIn) {
			if(totalOfferList != null && offersAdapter != null) {
				reset();
				reloadFlag=true;
				totalOfferList.clear();
				sortedBBYOffersList.clear();
				offersList.clear();
				offersAdapter.notifyDataSetChanged();
			}
			RZSharedPreference.offerOptedIn = false; 
			progressBar.setVisibility(View.VISIBLE);
			executeOffersSearchTask();
		}
	}

	private void executeOffersSearchTask() {		
		new BBYOffersTask(this).execute(Void, Void, Void);
	}
	private void executeRZOffersSearchTask() {		
		new OffersTask(this).execute(Void, Void, Void);
	}

	private List<RZOfferDetails> updateOfferList(List<RZOfferDetails> offerDetailsList) throws ParseException{
		 List<RZOfferDetails> optinTrueList= new ArrayList<RZOfferDetails>();
		 List<RZOfferDetails> optinfalseList = new ArrayList<RZOfferDetails>();
		 for(int listIndex=0;listIndex<offerDetailsList.size();listIndex++){
			 RZOfferDetails rzOffer=offerDetailsList.get(listIndex);
			 if(!rzOffer.isOptedin())				
				 optinfalseList.add(rzOffer);		
			 else
				 optinTrueList.add(rzOffer);			 
		 }
		 for(int listIndex=0;listIndex<offersList.size();listIndex++){
			 RZOfferDetails rzOffer=offersList.get(listIndex);
			 if(!rzOffer.isOptedin()){
				 offersList.addAll(listIndex,optinTrueList);
				 offersList.addAll(optinfalseList);
				 break;
			 }
		 }
		 return offersList;
	}
	private class OffersTask extends BBYAsyncTask {

		public OffersTask(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			if (offersList == null) {
				offersList = new ArrayList<RZOfferDetails>();
			}
		}

		@Override
		public void doFinish() {
			if(totalOfferList==null){
				totalOfferList=new ArrayList<Object>();	
			}
			else{
				totalOfferList.clear();
			}			
			totalOfferList.addAll(offersList);
			totalOfferList.addAll(sortedBBYOffersList);
			showView();
		}

		@Override
		public void doReconnect() {
			new OffersTask(activity).execute();
		}
		@Override
		public void doCancelReconnect() {
			// TODO Auto-generated method stub
			super.doCancelReconnect();
			finish();
		}
		
		@Override
		public void doTask() throws Exception{
				offerDetailsList = RZOfferListLogic.getOfferList(requestPageNumber);//,offersList
				 if(offerDetailsList!= null){
					 if(offersList.size()==0)
						 offersList.addAll(offerDetailsList);
					 else
						 offersList=updateOfferList(offerDetailsList);
					 
				 }
				 
			}
		}

	// Get BBY Offer Implementation : 24/09/2011
	
	private class BBYOffersTask extends BBYAsyncTask {
		public BBYOffersTask(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			sortedBBYOffersList= new ArrayList<RZOffer>();
			isLoadingOffers = true;
			if (bbyOffersList == null) {
				bbyOffersList = new ArrayList<RZOffer>();
			}
		}

		@Override
		public void doFinish() {			
			// Get BBY Offer Implementation : 24/09/2011
			new OffersTask(RewardZoneOffers.this).execute(Void, Void, Void);
			//End					
			offersLoaded = true;
			isLoadingOffers = false;
			
		}

		@Override
		public void doReconnect() {
			new BBYOffersTask(activity).execute();
		}

		@Override
		public void doCancelReconnect() {
			// TODO Auto-generated method stub
			super.doCancelReconnect();
			finish();
		}

		@Override
		public void doTask() throws Exception {
			String tier = null;
			if (rzAccount.isSilverStatus()) {
				tier = "silver";
			} else {
				tier = "core";
			}
			searchRequest = new SearchRequest();
			searchRequest.setCursor(cursor);
			bbyOffersList.addAll(searchRequest.getRZOffers(tier));
			RZOfferDetailsParser parser=new RZOfferDetailsParser();
			sortedBBYOffersList=parser.getSortedBBYOfferList(bbyOffersList);
			cursor = searchRequest.getCursor();
		}
	}

	class OffersAdapter extends ArrayAdapter {

		private final LayoutInflater layoutInflater;

		OffersAdapter() {
			super(RewardZoneOffers.this, R.layout.reward_zone_offers_row,
					totalOfferList);
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		

		public class ViewHolder {
			public TextView offersDescription;
			public TextView expiration;
			public ImageView icon, tickMark;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

				if (convertView == null || convertView.getTag() == null) {
					convertView = layoutInflater.inflate(
							R.layout.reward_zone_offers_row, null);
					holder = new ViewHolder();
					holder.offersDescription = (TextView) convertView
							.findViewById(R.id.reward_zone_offers_description);
					holder.icon = (ImageView) convertView
							.findViewById(R.id.reward_zone_offers_icon);
					holder.expiration = (TextView) convertView
							.findViewById(R.id.reward_zone_offers_expiration);
					holder.tickMark = (ImageView) convertView
							.findViewById(R.id.rz_offer_redeem_checkmark);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if(totalOfferList.get(position).getClass().getSimpleName().equals("RZOfferDetails")){
					RZOfferDetails rzOfferDetails = (RZOfferDetails) totalOfferList.get(position);
					holder.offersDescription.setText(UTF.replaceNonUTFCharacters(rzOfferDetails.getCouponTitle()));
	
					if (rzOfferDetails.getEndDate() != null) {
						holder.expiration.setVisibility(View.VISIBLE);
						holder.expiration.setText("Offer expires: "
								+ RewardZoneDateStringUtils
										.convertStringToDateWithYear(rzOfferDetails
												.getCouponEndDate()));
					} else {
						holder.expiration.setVisibility(View.INVISIBLE);
					}
					if (rzOfferDetails.isOptedin()) {
						holder.tickMark.setVisibility(View.VISIBLE);
					} else {
						holder.tickMark.setVisibility(View.INVISIBLE);
					}
	
					if (rzOfferDetails.getWallImagePath() != null
							&& rzOfferDetails.getWallImagePath().length() > 0)
						try {
							ImageProvider.getBitmapImageOnThread(
									getResources().getString(
											R.string.bby_rz_offer_image_url_prefix)
											+ rzOfferDetails.getWallImagePath(),
									holder.icon);
						} catch (RejectedExecutionException e) {
	
						}
				}
				else{
					RZOffer rzOffer = (RZOffer) totalOfferList.get(position);

					holder.offersDescription.setText(rzOffer.getTitle());

					if (rzOffer.showExpiration()) {
						holder.expiration.setVisibility(View.VISIBLE);
						holder.expiration.setText("Offer expires: " + bbyOfferdateFormat.format(rzOffer.getExpiration()));
					} else {
						holder.expiration.setVisibility(View.INVISIBLE);
					}
					holder.tickMark.setVisibility(View.INVISIBLE);
					if (rzOffer.getImageUrl() != null && rzOffer.getImageUrl().length() > 0)
						ImageProvider.getBitmapImageOnThread(rzOffer.getImageUrl(), holder.icon);
				}				
				return convertView;			
		}
	}
	
    private int previousTotal = 0;
    private boolean loading = true;
    
    private void reset() {
    	this.requestPageNumber = 0;
    	this.previousTotal = 0;
    	this.loading = true;
    }
    
	private OnScrollListener onScrollListener = new OnScrollListener() {
		 
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (totalItemCount >= 20) {
				if (loading) {
					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;
						requestPageNumber++;
					}
				}

				if ( !loading && firstVisibleItem == (totalItemCount - visibleItemCount)) {
					executeRZOffersSearchTask();
					loading = true;
				}
			}
		}
	};
}

