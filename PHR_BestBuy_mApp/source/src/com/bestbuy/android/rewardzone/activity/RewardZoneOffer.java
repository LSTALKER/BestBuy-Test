package com.bestbuy.android.rewardzone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.rewardzone.library.util.RewardZoneDateStringUtils;
import com.bestbuy.android.rewardzone.library.util.preference.RZSharedPreference;
import com.bestbuy.android.rewardzone.logic.RZOfferLogic;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.UTFCharacters.UTF;

/**
 * Displays the details of RZOffer
 * 
 * @author sharmila_j
 * 
 */
public class RewardZoneOffer extends MenuActivity {

	private String TAG = this.getClass().getName();

	private RZAccount rzAccount;
	private AppData appData;

	RZOfferDetails rzOfferDetails;

	public Activity currentActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();

		setContentView(R.layout.reward_zone_offer);
		currentActivity = RewardZoneOffer.this;

		RewardZoneHeader.setUpHeader(rzAccount, this, true);
		
		if (this.getIntent().hasExtra("offer")) {
			rzOfferDetails = (RZOfferDetails) this.getIntent().getExtras().getSerializable("offer");
		}

		BBYLog.w(TAG, rzOfferDetails.toString());

		ImageView image = (ImageView) findViewById(R.id.reward_zone_offer_image);
		if (rzOfferDetails.getWallImagePath() != null
				&& !rzOfferDetails.getWallImagePath().equals("")
				&& !rzOfferDetails.getWallImagePath().equals("null")) {
			ImageProvider.getBitmapImageOnThread(
					getResources().getString(
							R.string.bby_rz_offer_image_url_prefix)
							+ rzOfferDetails.getWallImagePath(), image);
		}
		
		TextView expiration = (TextView) findViewById(R.id.reward_zone_offer_expiration);
		if (rzOfferDetails.getStartDate() != null
				&& rzOfferDetails.getEndDate() != null) {
			expiration.setVisibility(View.VISIBLE);
			expiration.setText("Valid: "
					+ RewardZoneDateStringUtils.convertStringToDate(rzOfferDetails
							.getCouponStartDate())
					+ " - "
					+ RewardZoneDateStringUtils.convertStringToDate(rzOfferDetails
							.getCouponEndDate()));
		} else {
			expiration.setVisibility(View.GONE);
		}

		String	couponTitle = UTF.replaceNonUTFCharacters(rzOfferDetails.getCouponTitle());

		TextView title = (TextView) findViewById(R.id.reward_zone_offer_title);
		title.setText(couponTitle);

		WebView longCopyTv = (WebView) findViewById(R.id.reward_zone_offer_long_copy);
		longCopyTv.loadDataWithBaseURL(null, rzOfferDetails.getLongDescription().replaceAll("%", "&#37;"), "text/html", "utf-8", null);

		/*
		 * Showing options with respect to opted in offer...
		 */
		LinearLayout rz_offer_want_layout = (LinearLayout) findViewById(R.id.rz_offer_want_layout);
		TextView redeemedText = (TextView) findViewById(R.id.rz_redeem_Text);
		if ((rzOfferDetails.isOptedin())) {
			redeemedText.setVisibility(View.VISIBLE);
			ImageView tickMark = (ImageView) findViewById(R.id.rz_offer_redeem_checkmark);
			tickMark.setVisibility(View.VISIBLE);
			rz_offer_want_layout.setVisibility(View.GONE);
		} else {
			rz_offer_want_layout.setVisibility(View.VISIBLE);
			redeemedText.setVisibility(View.GONE);
			Button claimOfferButton = (Button) findViewById(R.id.rz_i_want_it_button);
			Button noThanksButton = (Button) findViewById(R.id.rz_no_thanks_button);

			// Making OptIn Web Service Call
			claimOfferButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					new ClaimOfferTask(RewardZoneOffer.this).execute();
				}
			});
			
			noThanksButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
		}
		TextView termsAndConditions = (TextView) findViewById(R.id.legalterm_instructions);
		if (rzOfferDetails.getTermsAndConditions() != null && (!rzOfferDetails.getTermsAndConditions().equals(""))) {
			termsAndConditions.setVisibility(View.VISIBLE);
			termsAndConditions.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intent = new Intent(RewardZoneOffer.this, RewardZoneLegalTerms.class);
					intent.putExtra("terms", rzOfferDetails.getTermsAndConditions());
					startActivity(intent);
				}
			});
		} else {
			termsAndConditions.setVisibility(View.GONE);
		}

	}
	
	private void showAlertPopup(String title, String message) {
		if(this == null || this.isFinishing())
			return;

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
        
		alertDialog.create();
		alertDialog.show();
	} 
	
	private class ClaimOfferTask extends BBYAsyncTask {
		int optInResponseStatus;
		
		public ClaimOfferTask(Activity activity) {
			super(activity, "Opting for offer");
		}
		
		@Override
		public void doFinish() {
			if(optInResponseStatus == 1) {
				RZSharedPreference.offerOptedIn = Boolean.TRUE;
				Intent intent = new Intent(RewardZoneOffer.this, RewardZoneOfferInstructions.class);
				intent.putExtra("offer", rzOfferDetails);
				startActivity(intent);
				finish();
			} else{
				showAlertPopup("Error Occurred", "Could not claim offer. Please try again later.");
			}
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new ClaimOfferTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else {
				showAlertPopup("Error Occurred", "Could not claim offer. Please try again later.");
			}
		}
		
		@Override
		public void doTask() throws Exception {
			optInResponseStatus = RZOfferLogic.sendOptInOffer(rzOfferDetails.getId());
		}
	}
}
