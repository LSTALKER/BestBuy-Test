package com.bestbuy.android.upgradechecker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.upgradechecker.activity.UpgradeCheckerTermsandConditions.InternalURLSpan;

public class AllSetActivity extends MenuActivity {
	private TextView terms_lnik;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade_checker_your_all_set);
		terms_lnik= (TextView) findViewById(R.id.upgrade_checker_eligibility_dates);
		SpannableString spanText=new SpannableString(getResources().getString(R.string.eligibility_date));
		spanText.setSpan(new InternalURLSpan (new OnClickListener(){
			public void onClick(View view) {				
				Intent intent=new Intent(AllSetActivity.this,TermsAndConditionsActivity.class);
				startActivity(intent);
			}
		}), 90, 119, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		terms_lnik.setText(spanText);
		terms_lnik.setMovementMethod(LinkMovementMethod.getInstance());
		terms_lnik.setLinksClickable(true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(AllSetActivity.this, UpgradeChecker.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			
			this.finish();
			startActivity(intent);
		}
	    return super.onKeyDown(keyCode, event);
	}
}
