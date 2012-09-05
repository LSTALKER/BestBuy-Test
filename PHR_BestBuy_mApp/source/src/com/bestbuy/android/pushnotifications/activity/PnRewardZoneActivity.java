package com.bestbuy.android.pushnotifications.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.RewardZoneLogin;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.pushnotifications.data.PushNotifcationData;
import com.bestbuy.android.pushnotifications.logic.PushNotificationsLogic;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZParser;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheManager;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.InputStreamExtensions;

public class PnRewardZoneActivity extends MenuActivity {

	private ToggleButton tbRewardZone;
	private TextView tvPnRewardZone;
	private LinearLayout layoutRZ;
	private boolean connectionSuccessful = false;
	private RZParser rzParser = null;
	public static String rz_id = null;
	private String rzLoginStatus;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pushnotifications_rewardzone_activity);

		/**
		 * intent call from PushnotificationActivity and RewardzoneLogin
		 */
		if (getIntent().getExtras() != null) {
			rzLoginStatus = getIntent().getExtras().getString(PushNotifcationData.STATUS);
		} else {
			rzLoginStatus = PushNotifcationData.STATUS_FALSE;
		}
	}
	
	@Override
	protected void onStart() {
		new AsyncTask<String, Integer, Boolean>() {
			private ProgressDialog progressDialog;

			@Override
			protected Boolean doInBackground(String... params) {
				return rewardZoneLogin();
			}

			protected void onPostExecute(Boolean result) {
				progressDialog.cancel();
				if (!result) {
					rz_id = null;
				}
				initViews();
			};

			protected void onPreExecute() {
				progressDialog = new ProgressDialog(PnRewardZoneActivity.this);
				progressDialog.setMessage("Loading...");
				progressDialog.show();
			};

		}.execute();

		super.onStart();
	}


	private void initViews() {
		tvPnRewardZone = (TextView) findViewById(R.id.pushnotification_rewardzone_textview);
		layoutRZ = (LinearLayout) findViewById(R.id.push_notification_rewardzone_login);
		tbRewardZone = (ToggleButton) findViewById(R.id.pushnotification_togglebutton_rewardzone);
		
		if (rzLoginStatus.equalsIgnoreCase(PushNotifcationData.STATUS_TRUE) && rz_id!=null) {
			tbRewardZone.setChecked(true);
		} else if (rzLoginStatus.equalsIgnoreCase(PushNotifcationData.STATUS_TRUE) && rz_id==null) {
			tbRewardZone.setChecked(true);
			setRewardZoneViewVisible();
		}  else {
			tbRewardZone.setChecked(false);
		}

		tbRewardZone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (tbRewardZone.isChecked()) {
					if (rz_id == null)
						setRewardZoneViewVisible();

				} else {
					tvPnRewardZone.setVisibility(View.INVISIBLE);
					layoutRZ.setVisibility(View.INVISIBLE);
				}
			}
		});

		((Button) findViewById(R.id.done_btn)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				appData.setChangesInPnPreferences(true);
				new SendRzStatusTask(PnRewardZoneActivity.this).execute();
			}
		});

		findViewById(R.id.push_notification_rewardzone_login).setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (!rewardZoneLogin()) {
							Intent i = new Intent(_context,RewardZoneLogin.class);
							i.putExtra("Activity", "PushNotificationGetRewardZone");
							startActivity(i);
						}

					}
				});
		
		if (connectionSuccessful || rz_id!=null) {
			tvPnRewardZone.setVisibility(View.GONE);
			layoutRZ.setVisibility(View.GONE);
		}
		
	}

	protected void setRewardZoneViewVisible() {
		tvPnRewardZone.setVisibility(View.VISIBLE);
		layoutRZ.setVisibility(View.VISIBLE);
	}

	private boolean rewardZoneLogin() {
		OAuthAccessor access = appData.getOAuthAccessor();
		if (access != null) {
			ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
			OAuthClient oclient = new OAuthClient(new HttpClient4());
			try {
				String url = AppConfig.getSecureRemixURL()
						+ AppData.REWARD_ZONE_DATA_PATH;
				rzParser = new RZParser();
				String data = CacheManager.getCacheItem(url,
						CacheManager.RZ_CACHE);

				if (data == null || data.length() <= 0) {
					BBYLog.i("Push Notifications", "Doing RZ, caching Data");
					CacheManager.clearCache(CacheManager.RZ_CACHE);
					OAuthMessage msg = oclient.invoke(access, url, params1);
					InputStream bodyInputStream = msg.getBodyAsStream();
					data = InputStreamExtensions
							.InputStreamToString(bodyInputStream);
					CacheManager.setCacheItem(url, data, CacheManager.RZ_CACHE);
				} else {
					BBYLog.i("Push Notifications", "Using Cached Data");
				}

				ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(
						data.getBytes());

				rzParser.parse(Diagnostics.dumpInputStream(_context,
						bodyConvertInputStream, "Push Notifications",
						"Reward Zone XML: "));

				if (appData.getRzAccount().getRzTransactions().size() == 0) {

					RZAccount rzAccount = rzParser.getRzAccount();
					appData.setRzAccount(rzParser.getRzAccount());
					rz_id = rzAccount.getId();
					Map<String, String> omnitureParams = new HashMap<String, String>();
					omnitureParams.put("rz_id", rzAccount.getId());
					omnitureParams.put("rz_tier", rzAccount.getStatusDisplay());
					EventsLogging.fireAndForget(EventsLogging.RZ_LOGIN_SUCCESS,
							omnitureParams);
				}
				connectionSuccessful = true;
				return true;
			} catch (Exception ex) {
				BBYLog.printStackTrace("Push Notifications", ex);
				if (ex.getMessage().contains("401")) {
					BBYLog.i("Push Notifications",
							"Unauthorized Exception: Need to reauth");
				}
				connectionSuccessful = false;
				return false;
			}
		}
		return false;
	}

	class SendRzStatusTask extends BBYAsyncTask {
		String status;
		String response = null;

		public SendRzStatusTask(Activity activity) {
			super(activity);

			if (tbRewardZone.isChecked()) 
				status = PushNotifcationData.STATUS_TRUE;
			 else 
				status = PushNotifcationData.STATUS_FALSE;
			
		}

		@Override
		public void doTask() throws Exception {

			response = PushNotifcationData.sendNotificationStatus(PushNotifcationData.PN_RZ, status);
		}

		@Override
		public void doFinish() {
			if (response != null) {
				if (tbRewardZone.isChecked() && rz_id !=null) {
					appData.setRZStatus(true);
				} else {
					appData.setRZStatus(false);
				}
				finish();
			} else {
				doError();
			}

		}

		@Override
		public void doReconnect() {
			new SendRzStatusTask(PnRewardZoneActivity.this).execute();
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new SendRzStatusTask(PnRewardZoneActivity.this).execute();
				}
			});
		}
	}

	class SendRzCatIdTask extends BBYAsyncTask {

		String response = null;

		public SendRzCatIdTask(Activity activity) {
			super(activity);
		}

		@Override
		public void doTask() throws Exception {
			if (rz_id != null) {
				response = PushNotificationsLogic.sendRZNotificationConfig(rz_id);
			}
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
				} catch (JSONException e) {}
				
				if (status.equals(PushNotifcationData.STATUS_SUCCESS)) {
					getSharedPreferences(AppData.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(AppData.PUSHNOTIFICATION_REWARDZONE_STATUS, true).commit();
					finish();
				} else {
					doError();
					getSharedPreferences(AppData.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(AppData.PUSHNOTIFICATION_REWARDZONE_STATUS, false).commit();
				}
			} else {
				doError();
			}

		}

		@Override
		public void doReconnect() {
			new SendRzCatIdTask(PnRewardZoneActivity.this).execute();
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					new SendRzCatIdTask(PnRewardZoneActivity.this).execute();
				}
			});
		}
	}
}