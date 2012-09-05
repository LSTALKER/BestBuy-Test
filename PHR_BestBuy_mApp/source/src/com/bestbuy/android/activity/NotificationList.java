package com.bestbuy.android.activity;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.Notification;
import com.bestbuy.android.data.Offer;
import com.bestbuy.android.util.AnimationManager;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.ImageProvider;

public class NotificationList extends MenuListActivity {

	private ListView lv;
	private NotificationAdapter notificationAdapter;

	List<String> skuList;
	private boolean newShown;
	private boolean clear;
	
	OnItemClickListener notificationClickedHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			clear = false;
			Notification notification = appData.getNotificationManager().getNotifications().get(position);
			appData.getNotificationManager().setNew(notification);
			v.findViewById(R.id.notification_star).setVisibility(View.GONE);
			switch(notification.getNotificationType()){
				case Notification.NotificationTypeAlert:
					gotoAlert(notification,id);
				break;
				case Notification.NotificationTypeOffer:
					gotoOffer(notification);
				break;
				case Notification.NotificationTypeRZCert:
					gotoCert(notification);
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_list);
		newShown = true;
		if (savedInstanceState != null) {
			appData.getNotificationManager().setLastNotificationRetrievedDate(null);
			this.finish();
		}
		showList();
	}
	
		
	@Override
	protected void onStart() {
		super.onStart();
		clear = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (notificationAdapter != null) {
			notificationAdapter.notifyDataSetChanged();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		if(clear){
			appData.getNotificationManager().saveNotificationHash();
		}
	}
	
	private void gotoAlert(Notification notification,long id){
		Intent i = new Intent(NotificationList.this, NotificationDetail.class);
		i.putExtra("position", (int)id);
		startActivity(i);
	}
	
	private void gotoOffer(Notification notification) {
		Offer offer = (Offer)notification.getNotificationObject();
		appData.setSelectedProduct(offer);
		Intent i = new Intent(NotificationList.this, SpecialOffersDetail.class);
		startActivity(i);
	}
	
	private void gotoCert(Notification notification){
		Intent i = new Intent(NotificationList.this, RewardZoneCertificate.class);
		i.putExtra("position", notification.getId());
		startActivity(i);
	}

	private void showList() {
		if (appData.getNotificationManager().getNewNotificationCount() > 0) {
			final View newNotificationView = findViewById(R.id.notification_list_new);
			if (newShown) {
				newNotificationView.setVisibility(View.VISIBLE);
				AnimationManager.runTopSlideDownOn(this, newNotificationView);

				TextView newNotificationText = (TextView) findViewById(R.id.notification_list_new_text);
				newNotificationText.setText(appData.getNotificationManager().getNotificationAlertString());

				newNotificationView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						newShown = false;
						AnimationManager.runTopSlideUpOn(v.getContext(), newNotificationView);
						newNotificationView.setVisibility(View.GONE);
					}
				});
			}

			Handler handler = null;
			handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if (newShown) {
						newShown = false;
						newNotificationView.setVisibility(View.GONE);
						AnimationManager.runTopSlideUpOn(NotificationList.this, newNotificationView);
					}
				}
			}, 3000); // 3 seconds

		} else {
			findViewById(R.id.notification_list_new).setVisibility(View.GONE);
		}
		lv = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(lv);

		notificationAdapter = new NotificationAdapter();

		lv.setAdapter(notificationAdapter);
		lv.setOnItemClickListener(notificationClickedHandler);
	}

	class NotificationAdapter extends ArrayAdapter<Notification> {
		NotificationAdapter() {
			super(NotificationList.this, R.layout.notification_row, appData.getNotificationManager().getNotifications());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.notification_row, parent, false);
			}
			
			Notification notification = appData.getNotificationManager().getNotifications().get(position);
			
			ImageView icon = (ImageView) row.findViewById(R.id.notification_row_icon);
			
			switch(notification.getNotificationType()){
				case Notification.NotificationTypeOffer:
					icon.setImageResource(R.drawable.icon_offer);
					break;
				case Notification.NotificationTypeRZCert:
					icon.setImageResource(R.drawable.icon_certificate);
					break;
				case Notification.NotificationTypeAlert:	
					if(notification.getListImageURL() != null && notification.getListImageURL().length() > 0){
						ImageProvider.getBitmapImageOnThread(notification.getListImageURL(), icon);
					}else{
						icon.setImageResource(R.drawable.icon_notify_default);
					}
					break;
			}
			
			if(appData.getNotificationManager().isNew(notification)){
				row.findViewById(R.id.notification_star).setVisibility(View.VISIBLE);
			}else{
				row.findViewById(R.id.notification_star).setVisibility(View.GONE);
			}
			
			((TextView) row.findViewById(R.id.notification_row_title)).setText(notification.getTitle());
			return row;
		}
	}
	
	/*private class LoadNotificationsTask extends BBYAsyncTask {
		public LoadNotificationsTask(Activity activity) {
			super(activity);
		}


		@Override
		public void doFinish() {
			if(!isError && appData.getNotificationManager().getNewNotificationCount() > 0){
				appData.getNotificationManager().setLastNotificationRetrievedDate(new Date());
				showList();
			}
		}

		@Override
		public void doError() {
			isError = true;
		}
		
		@Override
		public void doTask() throws Exception {	
			appData.getNotificationManager().loadNotifications();	
		}
	}*/
}

	


