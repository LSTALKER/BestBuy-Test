package com.bestbuy.android.pushnotifications.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
/**
 * 
 * @author bharathkumar_s
 *
 */
public class PnDealOfTheDayActivity extends MenuActivity implements OnClickListener{
	
	private ToggleButton tbDod;
	private String tbDodStatus;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushnotifications_dod_activity);
		
		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
		  	tbDodStatus=bundle.getString(PushNotifcationData.STATUS);
		}
		
		 tbDod=(ToggleButton) findViewById(R.id.tb_dod_status);
		((Button)findViewById(R.id.done_btn)).setOnClickListener(this);
		
		if(tbDodStatus.equalsIgnoreCase(PushNotifcationData.STATUS_TRUE)){
			tbDod.setChecked(true);
		}else{
			tbDod.setChecked(false);
		}
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.done_btn:
			 new sendDodStatusToPnServerTask(PnDealOfTheDayActivity.this).execute();
			break;

		default:
			break;
		}
	}
   public class	sendDodStatusToPnServerTask extends BBYAsyncTask{

	private String status;
	private String response ;
	public sendDodStatusToPnServerTask(Activity activity) {
		super(activity);
		if(tbDod.isChecked()){
			status=PushNotifcationData.STATUS_TRUE;
		}else{
			status=PushNotifcationData.STATUS_FALSE;
		}
	}

	@Override
	public void doTask() throws Exception {
		response = PushNotifcationData.sendNotificationStatus(PushNotifcationData.PN_DOD, status);
		
	}

	@Override
	public void doFinish() {
			if (response != null) {
				String status = "";
				try {
					JSONObject jsonResponse = new JSONObject(response);
					if (jsonResponse.has(PushNotifcationData.STATUS)) {
						status = jsonResponse.getString(PushNotifcationData.STATUS);
					}
				} catch (JSONException e) {
					doError();
				}
				
				if (status.equals(PushNotifcationData.STATUS_SUCCESS)) {
					finish();
				} else {
					doError();
				}
			} else {
				doError();
			}
		
	}
	@Override
	public void doReconnect() {
		 new sendDodStatusToPnServerTask(PnDealOfTheDayActivity.this).execute();
	}
	
	@Override
	public void doError() {			
		NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
			public void onReconnect() {
				 new sendDodStatusToPnServerTask(PnDealOfTheDayActivity.this).execute();
			}		
		});
	}	
	}
}
