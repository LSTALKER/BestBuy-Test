package com.bestbuy.android.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
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
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.eplib.util.EpLibUtil;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZCertificate;
import com.bestbuy.android.rewardzone.RZParser;
import com.bestbuy.android.rewardzone.RZTransaction;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheManager;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.FontLibrary;
import com.bestbuy.android.util.InputStreamExtensions;

/**
 * Displays a summary of points acquired and a list of Reward Zone certificates
 * 
 * @author Recursive Awesome
 * 
 */
public class RewardZonePoints extends MenuActivity {
	private String TAG = this.getClass().getName();
	ArrayList<RZTransaction> transactionList = new ArrayList<RZTransaction>();
	private DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private final int POINTS_DIALOG = 2;
	private int _points;

	private int wsAttempts = 0;
	private static final int LOAD_RZ_ATTEMPTS_ALLOWED = 3;
	private boolean isErrorOccured = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		executeLoadRZAccountTask();
	}

	public void showPointsView() {

		RZAccount rzAccount = appData.getRzAccount();
		setContentView(R.layout.reward_zone_point);

		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_POINTS_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay()); 
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);

		LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View footerView = li.inflate(R.layout.rz_points_list_footer, null);

		TextView silverStatusClick = (TextView) footerView.findViewById(R.id.rz_silver_click);

		String tier = "RZ_Core";
		if (rzAccount.isSilverStatus()) {
			tier = "RZ_Silver";
			silverStatusClick.setVisibility(View.VISIBLE);
			footerView.findViewById(R.id.divider_3).setVisibility(View.VISIBLE);
			silverStatusClick.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(RewardZonePoints.this, RewardZoneSilverDetails.class);
					startActivity(i);
				}
			});
		}
		EpLibUtil.trackEvent(RewardZonePoints.this, EpLibUtil.ACTION_UI_CLICK, tier );

		TextView signout = (TextView) footerView.findViewById(R.id.reward_zone_signout);

		signout.setText("Sign Out " + rzAccount.getRzParty().getRzEmails().get(0).getEmailAddress().toLowerCase());
		signout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				appData.getNotificationManager().removeRZAlerts();
				isErrorOccured = false;
				goBackToAuth();
			}
		});

		TextView memberId = (TextView) findViewById(R.id.reward_zone_member_number);
		memberId.setText("Member #: " + rzAccount.getNumber());

		TextView memberName = (TextView) findViewById(R.id.reward_zone_member_name);
		memberName.setText(rzAccount.getRzParty().getFirstName() + " " + rzAccount.getRzParty().getLastName());

		if (rzAccount.getPoints() != null) {
			_points = Integer.valueOf(rzAccount.getPoints());
		} else {
			//Didn't load correctly, get out of here.
			BestBuyApplication.showToastNotification("Unable to load RewardZone points.  Please try again later.", RewardZonePoints.this, Toast.LENGTH_LONG);
			isErrorOccured = false;
			goBackToAuth();
		}
		
		TextView totalPoints = (TextView) findViewById(R.id.reward_zone_total_points);
		totalPoints.setText(addDelimiters(rzAccount.getPoints()));
		
		TextView totalAmount = (TextView) findViewById(R.id.rz_tv_cert_total);
		StringBuffer $amount = new StringBuffer();
		$amount.append("<b><font color= '#666666'><font size='13'>My Balance: </font></font></b>");
		$amount.append("<b><font color= '#cf1b35'><font size='14'>&#36;</font></font></b>");
		$amount.append("<b><font color= '#cf1b35'><font size='14'>" + rzAccount.getTotal()+ "</font></font></b>");
		totalAmount.setText(Html.fromHtml($amount.toString()));

		TextView showCard = (TextView) findViewById(R.id.reward_zone_show_card);
		showCard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), RewardZoneCard.class);
				startActivity(i);
			}
		});
		ListView certificateList = (ListView) findViewById(R.id.reward_zone_certificate_list);
		TextView subPoints = (TextView) findViewById(R.id.reward_zone_sub_points);
		Typeface typeface = FontLibrary.getFont(R.string.droidsans, getResources());
		((TextView) findViewById(R.id.rz_points_validation)).setTypeface(typeface);
		((TextView) findViewById(R.id.convert_rz_points)).setTypeface(typeface);
		if (_points < 250) {
			findViewById(R.id.convert_rz_points).setVisibility(View.GONE);
			StringBuffer subPts = new StringBuffer();
			subPts.append("<b><font color= '#cf1b35'><font size='10'> 250 </font></font></b>");
			subPts.append("<b><font color= '#cf1b35'><font size='10'>points</font></font></b>");
			subPts.append("<b><font color= '#000000'><font size='10'> = </font></font></b>");
			subPts.append("<b><font color= '#cf1b35'><font size='10'>$5 </font></font></b>");
			subPts.append("<font color= '#000000'><font size='10'>in certificates.</font></font>");
			subPoints.setText(Html.fromHtml(subPts.toString()));
		}
		else{
			StringBuffer subPts = new StringBuffer();
			subPts.append("<b><font color= '#cf1b35'><font size='10'>"+addDelimiters(String.valueOf((_points/250) * 250))+" </font></font></b>");
			subPts.append("<b><font color= '#cf1b35'><font size='10'>points</font></font></b>");
			subPts.append("<b><font color= '#000000'><font size='10'> = </font></font></b>");
			subPts.append("<b><font color= '#cf1b35'><font size='10'>$"+addDelimiters(String.valueOf((_points/250) * 5))+" </font></font></b>");
			subPts.append("<font color= '#000000'><font size='10'>in certificates.</font></font>");
			subPoints.setText(Html.fromHtml(subPts.toString()));
		}
		
		ArrayList<RZCertificate> availableCerts = rzAccount.getAvailableCertificates();
		
		if(availableCerts == null || availableCerts.size()==0){
			findViewById(R.id.divider_1).setVisibility(View.GONE);
			findViewById(R.id.divider_2).setVisibility(View.GONE);
			findViewById(R.id.rz_rl_avalcertificates).setVisibility(View.GONE);
		}

		// Don't display voided/redeemed certificates
		certificateList.addFooterView(footerView);
		//certificateList.setFooterDividersEnabled(false);
		CertAdapter certAdapter = new CertAdapter(availableCerts);
		certificateList.setAdapter(certAdapter);
		
		certificateList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (v != null && v.getTag() != null && !Boolean.parseBoolean(v.getTag().toString())) {
					Intent i = new Intent(RewardZonePoints.this, RewardZoneCertificate.class);
					BBYLog.i(TAG, position + "");
					BBYLog.i(TAG, id + "");
					i.putExtra("position", (int) id);
					startActivity(i);
				}
			}
		});
		RewardZoneHeader.setUpHeader(rzAccount, this.getParent(), true);
	}

	private void goBackToAuth() {
		new Handler().postDelayed(new Runnable() {
    		public void run() { 		
    			if(!BaseConnectionManager.isNetAvailable(RewardZonePoints.this) || BaseConnectionManager.isAirplaneMode(RewardZonePoints.this)) {
	    				NoConnectivityExtension.noConnectivity(RewardZonePoints.this, new OnReconnect() {
	    					public void onReconnect() {
	    						goBackToAuth();
	    					}		
	    				}, new NoConnectivityExtension.OnCancel() {
	    					public void onCancel() {
	    						finish();
	    					}
	    				});
    			} else {
    				appData.setRzAccount(new RZAccount());
    				appData.clearOAuthAccessor();
    				CacheManager.clearCache(CacheManager.RZ_CACHE);

    				if (isErrorOccured) {
    					Intent i = new Intent(RewardZonePoints.this, RewardZoneLogin.class);
    					startActivity(i);
    				} else {
    					Toast.makeText(RewardZonePoints.this, "Logging out..", Toast.LENGTH_SHORT).show();
    				}
    				if (!isFinishing()) {
    					finish();
    					Intent i = new Intent(RewardZonePoints.this, RewardZoneLogin.class);
    					i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    					startActivity(i);
    				}
    			}
    		}
    	}, 0);
	}

	class CertAdapter extends ArrayAdapter<RZCertificate> {
		ArrayList<RZCertificate> certList = new ArrayList<RZCertificate>();

		CertAdapter(ArrayList<RZCertificate> certList) {
			super(RewardZonePoints.this, R.layout.reward_zone_points_row, certList);
			this.certList = certList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			RZCertificate certificate = certList.get(position);
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.reward_zone_points_row, parent, false);
			}

			row.setTag(certificate.getEmptyView());

			TextView amount = (TextView) row.findViewById(R.id.reward_zone_points_cert_amount);
			amount.setText("$" + certificate.getAmount());
			TextView number = (TextView) row.findViewById(R.id.reward_zone_points_cert_number);
			number.setText("Certificate # " + certificate.getCertificateNumber());

			TextView details = (TextView) row.findViewById(R.id.reward_zone_points_cert_details);

			ImageView rightArrow = (ImageView) row.findViewById(R.id.rz_points_header_right_arrow);

			if (certificate.getEmptyView()) {
				amount.setVisibility(View.INVISIBLE);
				number.setVisibility(View.INVISIBLE);
				details.setVisibility(View.INVISIBLE);
				rightArrow.setVisibility(View.INVISIBLE);
				row.setEnabled(false);
				row.setClickable(false);
				row.setFocusable(false);
			} else {
				try {
					/*details.setText("Awarded " + df.format(certificate.getIssuedDate()) + " Expires "
							+ df.format(certificate.getExpiredDate()));*/
					details.setText("Expires " + df.format(certificate.getExpiredDate()));
				} catch (Exception ex) {
					BBYLog.printStackTrace(TAG, ex);
					details.setText("");
				}
				amount.setVisibility(View.VISIBLE);
				number.setVisibility(View.VISIBLE);
				details.setVisibility(View.VISIBLE);
				rightArrow.setVisibility(View.VISIBLE);
			}
			return row;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setMessage("Loading points...");
		return dialog;
	}

	private void executeLoadRZAccountTask() {
		new LoadRZAccountTask(this).execute();
	}

	private class LoadRZAccountTask extends BBYAsyncTask{
		
		public LoadRZAccountTask(Activity activity){
			super(activity , "Loading points...");
		}
		
		boolean connectionSuccessful;
		boolean reauthenticate;
		String connectionErrorMessage;

		@Override
		public void doTask() throws Exception {
			OAuthAccessor access = appData.getOAuthAccessor();

			if (access != null) {
				ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
				OAuthClient oclient = new OAuthClient(new HttpClient4());
					String url = AppConfig.getSecureRemixURL() + AppData.REWARD_ZONE_DATA_PATH;

					RZParser rzParser = new RZParser();
					String data = CacheManager.getCacheItem(url, CacheManager.RZ_CACHE);

					if (data == null || data.length() <= 0) {
						BBYLog.i(TAG, "Doing WS, caching Data");
						CacheManager.clearCache(CacheManager.RZ_CACHE);
						OAuthMessage msg = oclient.invoke(access, url, params1);
						InputStream bodyInputStream = msg.getBodyAsStream();
						data = InputStreamExtensions.InputStreamToString(bodyInputStream);
						CacheManager.setCacheItem(url, data, CacheManager.RZ_CACHE);
					} else {
						BBYLog.i(TAG, "Using Cached Data");
					}

					ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(data.getBytes());

					rzParser.parse(Diagnostics.dumpInputStream(activity, bodyConvertInputStream, TAG, "Reward Zone XML: "));

					if (appData.getRzAccount().getRzTransactions().size() == 0) {
						
						RZAccount rzAccount = rzParser.getRzAccount();
						appData.setRzAccount(rzParser.getRzAccount());

						Map<String, String> omnitureParams = new HashMap<String, String>();
						omnitureParams.put("rz_id", rzAccount.getId());
						omnitureParams.put("rz_tier", rzAccount.getStatusDisplay()); 
						EventsLogging.fireAndForget(EventsLogging.RZ_LOGIN_SUCCESS, omnitureParams);
					}
					connectionSuccessful = true;
			} else {
				reauthenticate = true;
			}
		}

		@Override
		public void doFinish() {
			if (!isFinishing()) {
				if (connectionSuccessful) {
					removeDialog(POINTS_DIALOG);
					showPointsView();
				} else if (!connectionSuccessful && !reauthenticate && wsAttempts <= LOAD_RZ_ATTEMPTS_ALLOWED) {
					BBYLog.i(TAG, "Load RZ Account Unsuccessful -- Retrying");
					wsAttempts++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						BBYLog.printStackTrace(TAG, e);
					}
					executeLoadRZAccountTask();
				} else {
					removeDialog(POINTS_DIALOG);
					if (reauthenticate) {
						isErrorOccured = true;
						goBackToAuth();
					} else {
						BBYLog.i(TAG, "Load RZ Account Unsuccessful -- Giving up");
						Toast.makeText(activity, "Error getting the Reward Zone points: " + connectionErrorMessage, Toast.LENGTH_LONG).show();
					}
				}
			}
		}
		@Override
		public void doReconnect() {
			new LoadRZAccountTask(activity).execute();
		}

		@Override
		public void doCancelReconnect() {
			finish();
		}
	}

	private String addDelimiters(String points) {
		String responsePrice = "";
		if (points != null) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			responsePrice = numberFormat.format(Long.parseLong(points));
		}
		return responsePrice;
	}
}
