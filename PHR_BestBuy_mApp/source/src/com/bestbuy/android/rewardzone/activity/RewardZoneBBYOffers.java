package com.bestbuy.android.rewardzone.activity;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.RewardZoneOfferLegal;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZOffer;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.Linkifier;

public class RewardZoneBBYOffers extends MenuActivity {
	
	private String TAG = this.getClass().getName();

	private RZAccount rzAccount;
	private AppData appData;

	private RZOffer rzOffer;
	private SimpleDateFormat bbyOfferdateFormat = new SimpleDateFormat("MM/dd/yyyy");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();

		setContentView(R.layout.reward_zone_bby_offer);

		RewardZoneHeader.setUpHeader(rzAccount, this, true);
		if (this.getIntent().hasExtra("offer")) {
			rzOffer = (RZOffer) this.getIntent().getExtras().getSerializable("offer");
		}

		BBYLog.w(TAG, rzOffer.toString());

		TextView click_label = (TextView) findViewById(R.id.rz_offer_label);
		RelativeLayout click_layout = (RelativeLayout) findViewById(R.id.rz_certificate_rl_whitebox_view_details_click);

		ImageView image = (ImageView) findViewById(R.id.reward_zone_offer_image);
		if (rzOffer.getImageUrl() != null && !rzOffer.getImageUrl().equals("") && !rzOffer.getImageUrl().equals("null")) {
			ImageProvider.getBitmapImageOnThread(rzOffer.getImageUrl(), image);
		} else {
			if (rzAccount.getTier().contains("SILVER")) {
				image.setImageResource(R.drawable.rz_defaultimage_detail_core);
			} else {
				image.setImageResource(R.drawable.rz_defaultimage_detail_silver);
			}
		}

		if (rzOffer.getUrl() == null || rzOffer.getUrl().equals("") || rzOffer.getUrl().equals("null")) {
			click_layout.setVisibility(View.GONE);
			click_label.setVisibility(View.GONE);
		} else {
			click_layout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(rzOffer.getUrl()));
					startActivity(i);
				}
			});
		}

		TextView expiration = (TextView) findViewById(R.id.reward_zone_offer_expiration);
		if (rzOffer.showExpiration()) {
			expiration.setVisibility(View.VISIBLE);
			expiration.setText("Expires: " + bbyOfferdateFormat.format(rzOffer.getExpiration()));
		} else {
			expiration.setVisibility(View.GONE);
		}


		TextView title = (TextView) findViewById(R.id.reward_zone_offer_title);
		title.setText(rzOffer.getTitle());

		TextView descripLabel = (TextView) findViewById(R.id.rz_offer_rl_whitebox_label);
		LinearLayout descripLayout = (LinearLayout) findViewById(R.id.rz_offer_rl_whitebox_click);

		TextView longCopyTv = (TextView) findViewById(R.id.reward_zone_offer_long_copy);
		TextView shortCopyTv = (TextView) findViewById(R.id.reward_zone_offer_short_copy);
		//View shortCopyLine = findViewById(R.id.reward_zone_offer_short_copy_line);
		String shortCopy = "";
		String longCopy = "";

		if (rzOffer.getShortDescription() != null && !rzOffer.getShortDescription().equals("null") && rzOffer.getShortDescription().length() > 0) {
			shortCopy = rzOffer.getShortDescription();
			shortCopyTv.setText(shortCopy);
		} else {
			shortCopyTv.setVisibility(View.GONE);
			//shortCopyLine.setVisibility(View.GONE);
		}

		if (rzOffer.getLongDescription() != null && !rzOffer.getLongDescription().equals("null") && rzOffer.getLongDescription().length() > 0) {
			longCopy = rzOffer.getLongDescription();
			longCopyTv.setText(longCopy);
		} else {
			longCopyTv.setVisibility(View.GONE);
			//shortCopyLine.setVisibility(View.GONE);
		}

		BBYLog.d(TAG, "short copy: " + shortCopy);
		BBYLog.d(TAG, "long copy: " + longCopy);
		if (shortCopy.length() <= 0 && longCopy.length() <= 0) {
			descripLabel.setVisibility(View.GONE);
			descripLayout.setVisibility(View.GONE);
		}
		
		Linkify.addLinks(longCopyTv, Linkify.WEB_URLS);
		Linkify.addLinks(longCopyTv, Linkifier.getPhoneNumberPattern(), "tel:");

		TextView legalButton = (TextView) findViewById(R.id.reward_zone_legal_terms);
		if (rzOffer.getLegalCopy() == null || rzOffer.getLegalCopy().equals("") || rzOffer.getLegalCopy().equals("null")) {
			legalButton.setVisibility(View.GONE);
		} else {
			legalButton.setVisibility(View.VISIBLE);
			legalButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(v.getContext(), RewardZoneOfferLegal.class);
					i.putExtra("legal", rzOffer.getLegalCopy());
					startActivity(i);
				}
			});
		}
	}
}
