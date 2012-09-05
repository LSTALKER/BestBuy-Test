package com.bestbuy.android.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.data.BestBuyAlert;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Notification;
import com.bestbuy.android.util.ImageProvider;

/**
 * Displays detailed information about a product. Lets users add to cart, see
 * reviews, and see related items.
 * 
 * @author Recursive Awesome
 * 
 */
public class NotificationDetail extends MenuActivity {



	public static final Void Void = null;

	private Notification notification;
	private ImageView _icon;
	private DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this;
		if (this.getIntent().hasExtra("position")) {
			int position = (int) this.getIntent().getExtras().getInt("position");
			notification = appData.getNotificationManager().getNotifications().get(position-1);
		}
		showView();
	}


	private void learnMore() {
		String url = ((BestBuyAlert)notification.getNotificationObject()).getUrl();
		if(url != null && !url.equalsIgnoreCase("")){
			Uri uri = Uri.parse( url);
			try{
				startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
			}catch(Exception e){
				BestBuyApplication.showToastNotification("Not a valid url", NotificationDetail.this, Toast.LENGTH_LONG);
			}
			
		}		
	}

	private void setIconImage() {
		ImageProvider.getBitmapImageOnThread(((BestBuyAlert)notification.getNotificationObject()).getDisplayImageURL(), _icon);
	}

	private void showView() {
		setContentView(R.layout.alert_detail);

		_icon = (ImageView) findViewById(R.id.alert_icon);

		setIconImage();

		TextView title = (TextView) findViewById(R.id.alert_title);
		title.setText(notification.getTitle());
		
		TextView expirationDate = (TextView) findViewById(R.id.alert_expiration_date);
		expirationDate.setText("Offer expires " + df.format(((BestBuyAlert)notification.getNotificationObject()).getEndDate()));

		RelativeLayout learnMoreRL = (RelativeLayout) findViewById(R.id.alert_learn_more_rl);
		learnMoreRL.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				learnMore();
			}
		});
		
		TextView description = (TextView) findViewById(R.id.alert_description);
		description.setText(((BestBuyAlert)notification.getNotificationObject()).getBody());
		
	}
}
