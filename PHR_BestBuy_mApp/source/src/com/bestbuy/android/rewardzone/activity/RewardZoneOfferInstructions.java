package com.bestbuy.android.rewardzone.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZPhone;
import com.bestbuy.android.rewardzone.library.dataobject.RZOfferDetails;
import com.bestbuy.android.rewardzone.library.util.RewardZoneDateStringUtils;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.UTFCharacters.UTF;

/**
 * Displays the Instructions about opted-in offer
 * 
 * @author sharmila_j
 * 
 */
public class RewardZoneOfferInstructions extends MenuActivity {

	RZOfferDetails rzOfferDetails;
	private RZAccount rzAccount;
	boolean optInClicked;
	AppData appData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();
		
		setContentView(R.layout.reward_zone_instruction);
		RewardZoneHeader.setUpHeader(rzAccount, this, true);
		Intent intent = this.getIntent();
		if (intent.hasExtra("offer")) {
			rzOfferDetails = (RZOfferDetails) intent.getExtras()
					.getSerializable("offer");
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

		TextView promotionalTitle = (TextView) findViewById(R.id.reward_zone_offer_title);
		if (rzOfferDetails.getPromotionalTitle() != null) {
			StringBuffer titleBuffer = new StringBuffer();
			String promotionText = UTF.replaceNonUTFCharacters(rzOfferDetails.getPromotionalTitle());

			titleBuffer
					.append("<font color= '#737474'><font size='15'> Promotional:</font></font>");
			titleBuffer
					.append(String
							.format("<font color= '#231f20'><font size='15'> %s </font></font>",
									promotionText));
			promotionalTitle.setText(Html.fromHtml(titleBuffer.toString()));
			promotionalTitle.setVisibility(View.VISIBLE);
		}else{
			promotionalTitle.setVisibility(View.GONE);
		}

		Button ViewOffer = (Button) findViewById(R.id.view_offer);
		ViewOffer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(RewardZoneOfferInstructions.this,
						RewardZoneOffer.class);
				rzOfferDetails.setOptedin(true);
				i.putExtra("offer", rzOfferDetails);
				startActivity(i);

			}
		});
		LinearLayout rewardZoneCardSource = (LinearLayout)findViewById(R.id.rz_card_holder);
		TextView rzUserName = (TextView) findViewById(R.id.rz_user_name);
		TextView rzMemberid = (TextView) findViewById(R.id.rz_member_id);
		TextView rzPhoneNumber = (TextView) findViewById(R.id.rz_contact_number);
		if(rzAccount.getTier().equalsIgnoreCase("RWZ PREMIER SILVER")){
			rewardZoneCardSource.setBackgroundResource(R.drawable.rz_coupon_card_silver);
			rzUserName.setTextColor(Color.BLACK);
			rzMemberid.setTextColor(Color.BLACK);
			rzPhoneNumber.setTextColor(Color.BLACK);
		}else{
			rewardZoneCardSource.setBackgroundResource(R.drawable.rz_coupon_card);
			rzUserName.setTextColor(Color.WHITE);
			rzMemberid.setTextColor(Color.WHITE);
			rzPhoneNumber.setTextColor(Color.WHITE);
		}
		String userName = rzAccount.getRzParty().getFirstName()+" "+rzAccount.getRzParty().getLastName();
		RZPhone phone = rzAccount.getRzParty().getRzPhones().get(0);
		String phoneNumber = "(" + phone.getAreaCode() + ") "
				+ phone.getNumber().substring(0, 3) + "-" 
				+ phone.getNumber().substring(3);
		String memberId = "Member ID: "+rzAccount.getNumber();
		
		if(userName != null){
			rzUserName.setText(userName);
		}
		if(memberId != null){
			rzMemberid.setText(memberId);
		}
		if(phoneNumber != null){
			rzPhoneNumber.setText(phoneNumber);
		}

	}
	

}
