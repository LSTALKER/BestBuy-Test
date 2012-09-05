package com.bestbuy.android.pushnotifications.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.device.register.RegisterDeviceUtil;
import com.bestbuy.android.pushnotifications.data.NotificationStatus;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.FontLibrary;

public class PushNotificationsActivity extends MenuActivity {
	
	private LinearLayout cat_statusLayout;
	private List<String> statusList;
	private int position = 0;
	private String rzStatus,woStatus,dodStatus;
	private boolean isRestarted;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushnotifications_activity);
		isRestarted=false;
	}
	
	private void getNotificationStatus() {
		cat_statusLayout=(LinearLayout)findViewById(R.id.pushnotification_category_status);
		if(!AppData.getRegwithPNStatus()&&!isRestarted){
				new RegisterDevicetask(this).execute();
		}else{
			new PushNotificationStatusTask(this).execute();
		}
	}

	public void showView(NotificationStatus nStatus) {

		if(rzStatus.equals(PushNotifcationData.STATUS_TRUE)&& PnRewardZoneActivity.rz_id!=null){
			appData.setRZStatus(true);
		}else{
			appData.setRZStatus(false);
		}
		
		LayoutInflater inflater = getLayoutInflater();

		Typeface typeface = FontLibrary.getFont(R.string.helveticabold, getResources());
		cat_statusLayout.removeAllViews();
		for (int i=0;i<nStatus.getCategoryList().size();i++) {
			final String status=nStatus.getCategoryList().get(i);
			final View nStatusView = inflater.inflate(R.layout.pn_categories_status, cat_statusLayout, false);
			((TextView) nStatusView.findViewById(R.id.category)).setText(status);
			((TextView) nStatusView.findViewById(R.id.status)).setTypeface(typeface);
			
			if(statusList.size()>0){			
				if(statusList.get(i).equalsIgnoreCase(PushNotifcationData.STATUS_TRUE)){
					((TextView) nStatusView.findViewById(R.id.status)).setText("On");
				}else{ 
					((TextView) nStatusView.findViewById(R.id.status)).setText("Off");
				}
			}else{
				((TextView) nStatusView.findViewById(R.id.status)).setText("Off");
			}	
			cat_statusLayout.addView(nStatusView);
			if (nStatus.getCategoryList().indexOf(status) != nStatus.getCategoryList().size()-1) {
				inflater.inflate(R.layout.commerce_horizontal_line, cat_statusLayout, true);
			}
				
			//Set the listener for each row
			nStatusView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent;
					if(status.equalsIgnoreCase(PushNotifcationData.STATUS_PN_RZ)){
						intent=new Intent(PushNotificationsActivity.this,PnRewardZoneActivity.class);
						intent.putExtra(PushNotifcationData.STATUS, rzStatus);
						startActivity(intent);
					}else if(status.equalsIgnoreCase(PushNotifcationData.STATUS_PN_WO)){
						intent= new Intent(PushNotificationsActivity.this,PnWeeklyOffersActivity.class);
						intent.putExtra(PushNotifcationData.STATUS, woStatus);
						startActivity(intent);
					}else if(status.equalsIgnoreCase(PushNotifcationData.STATUS_PN_DOD)){
						intent= new Intent(PushNotificationsActivity.this,PnDealOfTheDayActivity.class);
						intent.putExtra(PushNotifcationData.STATUS, dodStatus);
						startActivity(intent);
					}
					
				}
			});
			
		if(nStatus.getCategoryList().size()== 1) {
			nStatusView.setBackgroundResource(R.drawable.commerce_box);
		    } else {
		    	if(position == 0) {
		    		nStatusView.setBackgroundResource(R.drawable.commerce_box_top);
			    } else if(position == (nStatus.getCategoryList().size() - 1)) {
			    	nStatusView.setBackgroundResource(R.drawable.commerce_box_bottom);
			    } else {
			    	nStatusView.setBackgroundResource(R.drawable.commerce_box_middle);
			    }
		    }
			
			position++;
		}
	}
	
	class PushNotificationStatusTask extends BBYAsyncTask{

		NotificationStatus  nStatus;
		public PushNotificationStatusTask(Activity activity) {
			super(activity);
			/*if(isRestarted)
				enableLoadingDialog(false);*/
		}

		@Override
		public void doTask() throws Exception {
		nStatus= PushNotifcationData.getNotificationStatus();
		}

		@Override
		public void doFinish() {
			if (nStatus != null) {
				if (nStatus.getStatusCode() == 200) {
					statusList = new ArrayList<String>();
					if (nStatus.getStatus() != null) {
						String[] status = nStatus.getStatus().split(",");
						for (int i = 0; i < status.length; i++) {
							statusList.add(status[i]);
						}
						if (statusList.size() > 0) {
							rzStatus = statusList.get(0);
							woStatus = statusList.get(1);
							dodStatus=statusList.get(2);
						}
					}
					showView(nStatus);
				}else{
					doError();
				}
			} else{
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						isRestarted=false;
						new PushNotificationStatusTask(PushNotificationsActivity.this).execute();
					}
				}, new OnCancel() {
					public void onCancel() {
					finish();
					}
				});
			}
			
		}
		@Override
		public void doReconnect() {
			new PushNotificationStatusTask(PushNotificationsActivity.this).execute();
		}
		
		@Override
		public void doError() {			
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					isRestarted=false;
					new PushNotificationStatusTask(PushNotificationsActivity.this).execute();
				}
			}, new OnCancel() {
				public void onCancel() {
				finish();
				}
			});
		}
	}
	
	class RegisterDevicetask extends BBYAsyncTask{
			String response;
		public RegisterDevicetask(Activity activity) {
			super(activity);
			
		}

		@Override
		public void doTask() throws Exception {
			response=RegisterDeviceUtil.getDevRegResponse(AppData.getOptin(),AppData.getUAid(),AppConfig.getHashedDeviceId());
			
		}
		@Override
		public void doError() {			
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					isRestarted=false;
					new RegisterDevicetask(PushNotificationsActivity.this).execute();
				}
			}, new OnCancel() {
				public void onCancel() {
				finish();
				}
			});
		}	
		@Override
		public void doFinish() {
			if(response!=null){
				String status=getResponseStatus(response);
				if(status.equals(PushNotifcationData.STATUS_SUCCESS)){
					setRegistrationStatus(false,false,true);
					AppData.getSharedPreferences().edit().putBoolean(AppData.OPTIN_STATE, true);
					new PushNotificationStatusTask(PushNotificationsActivity.this).execute();
				}else{
					doError();
					setRegistrationStatus(true,true,false);
				}				
			}else{
				doError();
				setRegistrationStatus(true,true,false);
			}
			
			
			
		}
		
		public String getResponseStatus(String responseString) {
			try {
				JSONObject jsonObject = new JSONObject(responseString);
				if (jsonObject.has(PushNotifcationData.STATUS)) {
					return jsonObject.getString(PushNotifcationData.STATUS);
				}			
			} catch (JSONException e) {
				return PushNotifcationData.STATUS_FALSE;
			}
			return PushNotifcationData.STATUS_FALSE;
		}
		
		public void setRegistrationStatus(boolean b, boolean b1, boolean b2) {
			AppData.setOptinStatus(b);
			AppData.setapiKeyStatus(b1);
			AppData.setRegwithPNStatus(b2);		
		}
	}
	@Override
	protected void onStart() {
		position=0;
		getNotificationStatus();
		super.onStart();
	}
	@Override
	protected void onRestart() {
		isRestarted=true;
		super.onRestart();
	}
}