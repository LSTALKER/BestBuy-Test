package com.bestbuy.android.pushnotifications.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.Category;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.pushnotifications.logic.PushNotificationsLogic;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;

public class PnWeeklyOffersActivity extends MenuActivity {

	private ToggleButton tbWeeklyOffer;
	private TextView tvWeeklyOffer;
	private LinearLayout categoryLayout;
	private List<String> selectedCategoryIds;
	private List<Category> offerCategories;
	private int position = 0;
	private String toggleStatus;
	private boolean isdialog=true;
	private boolean isFinshedcatTask;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pushnotifications_weeklyoffers_activity);

		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			toggleStatus=bundle.getString(PushNotifcationData.STATUS);
		}
		selectedCategoryIds = new ArrayList<String>();
		offerCategories = new ArrayList<Category>();
				
		categoryLayout = (LinearLayout) findViewById(R.id.pushnotification_weeklyoffers_list_productcatagory);
	    tvWeeklyOffer = (TextView) findViewById(R.id.pushnotification_textview_weeklyoffers);
		tbWeeklyOffer = (ToggleButton) findViewById(R.id.pushnotification_togglebutton_weeklyoffers);
		tbWeeklyOffer.setChecked(false);
		
		if(toggleStatus.equalsIgnoreCase(PushNotifcationData.STATUS_TRUE)){
			tbWeeklyOffer.setChecked(true);
			new LoadCategoriesTask(PnWeeklyOffersActivity.this).execute();
			setCategoriesVisible(true);
		}else{
			tbWeeklyOffer.setChecked(false);
		}	
		tbWeeklyOffer.setOnClickListener(new OnClickListener() {	
			public void onClick(View v)	{
				categoryLayout.removeAllViews();
				if (tbWeeklyOffer.isChecked()) {
					selectedCategoryIds.clear();
					offerCategories.clear();
					setCategoriesVisible(true);
					new LoadCategoriesTask(PnWeeklyOffersActivity.this).execute();
				 } else {
					 selectedCategoryIds.clear();
					 setCategoriesVisible(false);
				 }
			}
		});
		
		((Button) findViewById(R.id.done_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				appData.setChangesInPnPreferences(true);
				if(selectedCategoryIds.size()>0){
					isdialog=false;
					
				}
				new SendPrefferedCatIdsTask(PnWeeklyOffersActivity.this).execute();
				new SendWoStatusTask(PnWeeklyOffersActivity.this).execute();
			}
		});
	}

	

	protected void setCategoriesVisible(boolean vis) {
		if(vis){
			tvWeeklyOffer.setVisibility(View.VISIBLE);
			categoryLayout.setVisibility(View.VISIBLE);
		}else{
			tvWeeklyOffer.setVisibility(View.INVISIBLE);
			categoryLayout.setVisibility(View.INVISIBLE);
		}
	}

	public class LoadCategoriesTask extends BBYAsyncTask {
		public LoadCategoriesTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doFinish() {
			isFinshedcatTask=true;
			new GetPrefferedCatIdsTask(PnWeeklyOffersActivity.this).execute();
		}

		@Override
		public void doTask() throws Exception {
			offerCategories.addAll(PushNotifcationData.getOfferCategories(PnWeeklyOffersActivity.this, appData));
		}
		@Override
		public void doReconnect() {
			new LoadCategoriesTask(PnWeeklyOffersActivity.this).execute();
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new LoadCategoriesTask(PnWeeklyOffersActivity.this).execute();
				}
			});
		}
	}

	public void showView() {
		LayoutInflater inflater = getLayoutInflater();

		categoryLayout.removeAllViews();
		for (final Category category : offerCategories) {
			final View categoryView = inflater.inflate(R.layout.preferred_category, categoryLayout, false);
			((TextView) categoryView.findViewById(R.id.preferred_category_text)).setText(category.getName());
			categoryLayout.addView(categoryView);
			if (offerCategories.indexOf(category) != offerCategories.size()-1) {
				inflater.inflate(R.layout.commerce_horizontal_line, categoryLayout, true);
			}
			
			//Show or hide the checkmarks according to the users preferences
			if (selectedCategoryIds.contains(category.getRemixId())) {
				categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.VISIBLE);
			} else {
				categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.INVISIBLE);
			}
			
			//Set the listener for each row
			categoryView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					
					if (selectedCategoryIds.contains(category.getRemixId())) {
						selectedCategoryIds.remove(category.getRemixId());
						categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.INVISIBLE);
					} else {
						selectedCategoryIds.add(category.getRemixId());
						categoryView.findViewById(R.id.preferred_category_checkmark).setVisibility(View.VISIBLE);
					}
				}
			});
			
		if(offerCategories.size()== 1) {
				categoryView.setBackgroundResource(R.drawable.commerce_box);
		    } else {
		    	if(position == 0) {
		    		categoryView.setBackgroundResource(R.drawable.commerce_box_top);
			    } else if(position == (offerCategories.size() - 1)) {
			    	categoryView.setBackgroundResource(R.drawable.commerce_box_bottom);
			    } else {
			    	categoryView.setBackgroundResource(R.drawable.commerce_box_middle);
			    }
		    }
			
			position++;
		}
	}
	class SendPrefferedCatIdsTask extends BBYAsyncTask{

		String response = null;
		public SendPrefferedCatIdsTask(Activity activity) {
			super(activity);
			if(isdialog){
				enableLoadingDialog(false);
			}
		}

		@Override
		public void doTask() throws Exception {
		response = PushNotificationsLogic.sendOffersNotificationConfig(selectedCategoryIds);
		}

		@Override
		public void doFinish() {
			if(response != null){
				String status = "";
				try {
					JSONObject jsonResponse=new JSONObject(response);
						if(jsonResponse.has(PushNotifcationData.STATUS)){
							status=jsonResponse.getString(PushNotifcationData.STATUS);
						}
				} catch (JSONException e) {}
				if(status.equals(PushNotifcationData.STATUS_SUCCESS)){
					if(selectedCategoryIds.size()>0)
						appData.setWoStatus(true);
					else
						appData.setWoStatus(false);
					finish();
				}else{
					appData.setWoStatus(false);
					doError();
				}	
			}else{
				doError();
			}
			
		}
		@Override
		public void doReconnect() {
			new SendPrefferedCatIdsTask(PnWeeklyOffersActivity.this).execute();
		}
		
		@Override
		public void doError() {			
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new SendPrefferedCatIdsTask(PnWeeklyOffersActivity.this).execute();
				}		
			});
		}
	}
	
	class SendWoStatusTask extends BBYAsyncTask{
		String status;
		String response = null;
		public SendWoStatusTask(Activity activity) {
			super(activity);
			enableLoadingDialog(isdialog);
			if(tbWeeklyOffer.isChecked()){
				status=PushNotifcationData.STATUS_TRUE;
			}else{
				status=PushNotifcationData.STATUS_FALSE;
			}
		}

		@Override
		public void doTask() throws Exception {
			response = PushNotifcationData.sendNotificationStatus(PushNotifcationData.PN_WO, status);
		}

		@Override
		public void doFinish() {
			if(response != null){
					if(isdialog){
						finish();
					}
			}else{
				doError();
			}
		}
		@Override
		public void doReconnect() {
			new SendWoStatusTask(PnWeeklyOffersActivity.this).execute();
		}
		
		@Override
		public void doError() {			
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new SendWoStatusTask(PnWeeklyOffersActivity.this).execute();
				}		
			});
		}
	}
	
	class GetPrefferedCatIdsTask extends BBYAsyncTask{
		private List<String> catIds;
		
		public GetPrefferedCatIdsTask(Activity activity) {
			super(activity);
			catIds=new ArrayList<String>();
			
		}
		@Override
		public void doTask() throws Exception {
			catIds=PushNotifcationData.getPrefferedCategoryIds();
		}
		@Override
		public void doFinish() {
			if(catIds !=null){
				selectedCategoryIds.addAll(catIds);
				showView();
				setCategoriesVisible(true);
			}else{
				if(isFinshedcatTask){
					showView();
				}
			}
		}
		
		@Override
		public void doError() {
			super.doError();
			NoConnectivityExtension.noConnectivity(_context, new OnReconnect() {
				
				public void onReconnect() {
					new GetPrefferedCatIdsTask(PnWeeklyOffersActivity.this).execute();
				}
			}, new OnCancel() {
				public void onCancel() {
					
				}
			});
		}
	}
}