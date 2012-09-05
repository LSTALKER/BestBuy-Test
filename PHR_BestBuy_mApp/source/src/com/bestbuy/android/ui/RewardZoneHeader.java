package com.bestbuy.android.ui;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.rewardzone.RZAccount;

public class RewardZoneHeader {
	public static void setUpHeader(RZAccount rzAccount, Activity parent , boolean headerLogo) {
		/*if (rzAccount.isSilverStatus()) {
			RelativeLayout header_bar = (RelativeLayout) parent
					.findViewById(R.id.header);
			header_bar.setBackgroundResource(R.drawable.header_rz_silver);
//			ImageView header_logo = (ImageView) header_bar
//					.findViewById(R.id.reward_zone_header_logo);
//			header_logo.setImageResource(R.drawable.logo_rz_silver);
		}*/
		
		
		if (rzAccount.isSilverStatus()) {
			RelativeLayout header_bar = (RelativeLayout) parent
					.findViewById(R.id.reward_zone_header_seplayout);
			header_bar.setBackgroundResource(R.drawable.header_rz_silver);
			ImageView header_logo = (ImageView) header_bar
					.findViewById(R.id.reward_zone_header_logo);

			if (headerLogo) {
				header_logo.setImageResource(R.drawable.logo_rz_silver);
			} else {
				TextView header_text = (TextView) parent
						.findViewById(R.id.header_title);
				header_text.setVisibility(View.VISIBLE);
				header_logo.setVisibility(View.GONE);
			}
		}
	}
}
