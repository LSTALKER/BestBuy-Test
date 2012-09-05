package com.bestbuy.android.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZCertificate;
import com.bestbuy.android.rewardzone.activity.helper.HorizontalPager;
import com.bestbuy.android.rewardzone.activity.helper.HorizontalPager.PageFlickListener;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.FontLibrary;

public class RewardZoneCertificate extends MenuActivity implements PageFlickListener, OnClickListener{

	@SuppressWarnings("unused")
	private String TAG = this.getClass().getName();
	private RZAccount rzAccount;
	private RZCertificate rzCertificate;
	private AppData appData;
	private DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private HorizontalPager pager;
	private int position;
	private int totalNumOfCerts;
	private TextView numOfPage;
	private Typeface droidSansBold;
	private Typeface droidSansNormal;
	private RelativeLayout leftArrow;
	private RelativeLayout rightArrow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appData = ((BestBuyApplication) this.getApplication()).getAppData();
		rzAccount = appData.getRzAccount();
		setContentView(R.layout.reward_zone_certificate);
		if (this.getIntent().hasExtra("position"))
			position = (int) this.getIntent().getExtras().getInt("position");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_CERTIFICATE_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay()); 
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
		
		RewardZoneHeader.setUpHeader(rzAccount, this, true);
		totalNumOfCerts = rzAccount.getAvailableCertificates().size();
		droidSansBold = FontLibrary.getFont(R.string.droidsansbold, getResources());
		droidSansNormal = FontLibrary.getFont(R.string.droidsans, getResources());
		numOfPage = (TextView) findViewById(R.id.num_of_page);
		if(totalNumOfCerts == 1){
			LinearLayout singleCertlayout = (LinearLayout) findViewById(R.id.singleCertView);
			singleCertlayout.setVisibility(View.VISIBLE);
			singleCertlayout.addView(getView(0));
		}
		else{
			pager = (HorizontalPager) findViewById(R.id.pager);
			rightArrow = (RelativeLayout) findViewById(R.id.right_arrow);
			leftArrow = (RelativeLayout) findViewById(R.id.left_arrow);
			rightArrow.setOnClickListener(this);
			leftArrow.setOnClickListener(this);
			pager.setVisibility(View.VISIBLE);
			for (int i = 0; i < totalNumOfCerts; i++) {
				pager.addView(getView(i));
			}
			pager.setOnPageFlickListener(this);
			pager.setCurrentPage(position);
		}
		numOfPage.setText(position+1 +" of "+ totalNumOfCerts);
	}

	private View getView(int index){
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.rz_certificate_view, null);
		rzCertificate = rzAccount.getAvailableCertificates().get(index);
		TextView amount = (TextView) view.findViewById(R.id.reward_zone_certificate_amount);
		amount.setText("$" + rzCertificate.getAmount());

		TextView expiration = (TextView) view.findViewById(R.id.reward_zone_certificate_expiration);
		expiration.setTypeface(droidSansBold);
		expiration.setText("Certificate expires "
				+ df.format(rzCertificate.getExpiredDate()));

		TextView name = (TextView) view.findViewById(R.id.reward_zone_certificate_name);
		name.setTypeface(droidSansBold);
		name.setText(rzAccount.getRzParty().getFirstName() + " "
				+ rzAccount.getRzParty().getLastName());
		
		TextView memberId = (TextView) view.findViewById(R.id.reward_zone_certificate_member_id);
		memberId.setTypeface(droidSansNormal);
		memberId.setText("Member ID: " + rzAccount.getNumber());

		TextView number = (TextView) view.findViewById(R.id.reward_zone_certificate_thirty_digit_code);
		number.setTypeface(droidSansNormal);
		number.setText(rzCertificate.getBarcodeNumber());

		TextView tenDigitNum = (TextView) view.findViewById(R.id.reward_zone_certificate_10_digit_number);
		tenDigitNum.setTypeface(droidSansNormal);
		tenDigitNum.setText("10-digit Certificate #: " + rzCertificate.getCertificateNumber());
		
		return view;
	}
	public void currentPage(int page) {
		if(page == 0)
			page = 1;
		numOfPage.setText(page +" of "+ totalNumOfCerts);
	}

	public void onClick(View v) {
		if(v == leftArrow){
			if(pager.getCurrentPage() == 0)
				pager.setCurrentPage(totalNumOfCerts-1);
			else
				pager.snapToPage(pager.getCurrentPage()-1);
			
		}
		else if(v == rightArrow)
			if(pager.getCurrentPage() == totalNumOfCerts-1)
				pager.setCurrentPage(0);
			else
				pager.snapToPage(pager.getCurrentPage()+1);
	}
}
