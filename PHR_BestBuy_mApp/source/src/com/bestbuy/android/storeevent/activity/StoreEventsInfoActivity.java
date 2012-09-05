package com.bestbuy.android.storeevent.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.storeevent.activity.helper.facebook.FacebookController;
import com.bestbuy.android.storeevent.library.dataobject.StoreEvents;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAlertDialog;
import com.bestbuy.android.util.ImageProvider;

/**
 * This activity is responsible to display the store events details as per corresponding event.
 * Here no need of any API Call.
 * The data is passed from StoreEventsActivity.java
 * @author lalitkumar_s
 */

public class StoreEventsInfoActivity extends MenuActivity implements OnClickListener {
	private StoreEvents storeEvents;
	private Dialog shareDialog;
	private FacebookController facebookController;
	private String imageUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.stev_events_info);
		
		initializePage();
	}

	/**
	 * Initialize the entire view and display of corresponding data on corresponding UI element.
	 */
	private void initializePage() {
		ImageView storeLocatorImage = (ImageView)findViewById(R.id.store_locator_image);
		TextView storeLocatorCity = (TextView)findViewById(R.id.store_locator_city);
		TextView storeLocatorTitle = (TextView)findViewById(R.id.store_locator_title);
		WebView storeLocatorDesc = (WebView)findViewById(R.id.store_locator_desc);
		
		Bundle bundle = getIntent().getExtras();
		
		if(bundle != null && bundle.containsKey(StoreUtils.EVENTS_INFO)) {
			storeEvents = (StoreEvents)bundle.get(StoreUtils.EVENTS_INFO);
			storeLocatorCity.setText(storeEvents.getLocation());
			storeLocatorTitle.setText(storeEvents.getTitle());
			storeLocatorDesc.loadDataWithBaseURL(null, getEventScheduledDate() + storeEvents.getDescription(), "text/html", "utf-8", null);
			
			if(bundle.containsKey(StoreUtils.STORE_IMAGE)) {
				imageUrl = bundle.getString(StoreUtils.STORE_IMAGE);;
				
				if(imageUrl != null && !imageUrl.equals("")) {
					ImageProvider.getBitmapImageOnThread(imageUrl, storeLocatorImage);
				} else {
					storeLocatorImage.setImageResource(R.drawable.store_image);
				}
			} else {
				storeLocatorImage.setImageResource(R.drawable.store_image);
			}
		}
	}
	
	public void onClick(View v) {
		int id = v.getId();
		
		switch (id) {
			case R.id.store_locator_share_events:
				showShareEventDialog();
				break;
		}
	}
	
	private StringBuffer getEventScheduledDate() {
		String eventDate = "";
		String eventStartDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_THREE);
		String eventEndDate = StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_THREE);
			
		if(eventStartDate.equals(eventEndDate)) {
		
			eventDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_NINE)
							+ " at " + StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_FOUR)
							+ " - " + StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_FOUR);
		} else {
			
			eventDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_EIGHT)
					+ " - " + StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_EIGHT);
		}
		
		StringBuffer changedFormat = new StringBuffer();
		changedFormat.append("<font color='#737474'><strong>Schedule: " + eventDate + "</strong></font><br /><br />");
		
		return changedFormat;
	}
	
	/**
	 * Opens the share event dialog.
	 * Here you can share the event through multiple applications like Email, Facebook and Twitter.
	 */
	private void showShareEventDialog() {
		shareDialog = new Dialog(this);
		shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		shareDialog.setContentView(R.layout.stev_share_event_dialog);
		shareDialog.setCancelable(true);
		shareDialog.show();
		
		Button facebookBtn = (Button)shareDialog.findViewById(R.id.store_locator_facebook);
		facebookBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareFacebook();
				shareDialog.cancel();
			}
		});
		
		Button twitterBtn = (Button)shareDialog.findViewById(R.id.store_locator_twitter);
		twitterBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareTwitter();
				shareDialog.cancel();
			}
		});
		
		Button emailBtn = (Button)shareDialog.findViewById(R.id.store_locator_email);
		emailBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareOnEmail();
				shareDialog.cancel();
			}
		});
		
		Button cancelBtn = (Button)shareDialog.findViewById(R.id.store_locator_cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareDialog.cancel();
			}
		});
	}
	
	/**
	 * This function is redirect to the installed Twitter Application.
	 * If the application is not installed in your device then it prompts as No App found.
	 */
	private void shareTwitter() {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		
		String subject = storeEvents.getTitle();
		String eventDate = "";
		String eventStartDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_THREE);
		String eventEndDate = StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_THREE);
		
		if(eventStartDate.equals(eventEndDate)) {
			
			eventDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_NINE)
							+ " at " + StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_FOUR)
							+ " - " + StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_FOUR);
		} else {
			
			eventDate = StoreUtils.convertToDateTimeStringType(storeEvents.getStartDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_EIGHT)
					+ " - " + StoreUtils.convertToDateTimeStringType(storeEvents.getEndDate(), StoreUtils.DATE_FORMAT_TYPE_SERVER_TWO, StoreUtils.DATE_FORMAT_TYPE_EIGHT);
		}
		
		String url = AppConfig.getBestbuyStoresURL() + "/" + storeEvents.getStoreId() + AppData.BBY_EVENT_SHARE_PATH;
		
		String description = subject + "\n" + url + "\n" + eventDate;
			
		description = StoreUtils.truncateTwitterMessage(description);
		
		intent.putExtra(Intent.EXTRA_TEXT, description);
		
		try {
			
		 	final PackageManager pm = getPackageManager();
		    final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		    ResolveInfo best = null;
		    
		    for (final ResolveInfo info : matches) {
		    	if (info.activityInfo.packageName.endsWith(".twitter") || info.activityInfo.name.toLowerCase().contains("twitter")) 
		    		best = info;
		    }
		    
		    if (best != null) {
		    	intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		    	startActivity(intent);
		 	} else
		    	showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		    
		} catch (Exception e) {
			showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		}
		
	}
	
	/**
	 * It opens the face book on a dialog. 
	 * After opening the dialog the entire control is handled by Face book.
	 */
	private void shareFacebook() {
		facebookController = new FacebookController();
        facebookController.initializeAll(StoreUtils.FACEBOOK_API_KEY, this, this, StoreUtils.PERMISSIONS);
        facebookController.setLink(AppConfig.getFacebookPostDefaultURL());/*StoreUtils.FACEBOOK_POST_DEFAULT_LINK*/
        facebookController.setDescription(storeEvents.getDescription());
        facebookController.setName(storeEvents.getTitle());
        facebookController.setImageUrl(imageUrl);
		facebookController.postMessageOnWall();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.facebookController.getFacebook().authorizeCallback(requestCode, resultCode, data);
	}
	
	/**
	 * It redirects to the native Gmail application through which the content to be share via mail.
	 */
	private void shareOnEmail() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");

		intent.putExtra(Intent.EXTRA_SUBJECT, storeEvents.getTitle());
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(storeEvents.getDescription()));
		
		try {
			
		 	final PackageManager pm = getPackageManager();
		    final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		    ResolveInfo best = null;
		    
		    for (final ResolveInfo info : matches) {
		    	if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) 
		    		best = info;
		    }
		    
		    if (best != null) {
		    	intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		    	startActivity(intent);
		 	} else
		 		showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		    
		} catch (Exception e) {
			showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return dialog;
		}
		
		return new BBYAlertDialog(this).createAlertDialog(id);
	}
}
