package com.bestbuy.android.util;

import java.util.List;

import com.bestbuy.android.R;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.storeevent.activity.helper.facebook.FacebookController;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ShareProduct {
	
	private String imageUrl = null;
	private String encodedUrl = null;
	private String description = null;
	private Dialog shareDialog;
	private FacebookController facebookController;
	private boolean isOpenBoxItem = false;
	private Product product = null;
	private OpenBoxItem openBoxItem = null;
	private Activity context;
	public static final int TWITTER_APP = 1;
	public static final int EMAIL_APP = 2;
	
	public ShareProduct(Activity activity){
		this.context = activity;
	}
	
	public ShareProduct(Activity activity, Product product) {
		this.context = activity;
		this.product = product;
	}
	
	public ShareProduct(Activity activity, OpenBoxItem openBoxItem, boolean isOpenBoxItem) {
		this.context = activity;
		this.openBoxItem = openBoxItem;
		this.isOpenBoxItem = isOpenBoxItem;
	}
	
	public void execute() {
		new ShareDataTask(context).execute();
	}
	
	private class ShareDataTask extends BBYAsyncTask {
	
		public ShareDataTask(Activity activity) {
			super(activity);
		}
		
		@Override
		public void doTask() throws Exception {
			String url = null;
			if(isOpenBoxItem) {
				imageUrl = openBoxItem.getItemImage();
				url = openBoxItem.getUrl();
			} else {
				imageUrl = product.getBestImageURL();
				url = product.getUrl();
			}
			
			if(url != null && !url.equals("")) {
				encodedUrl = ShareUtils.getBitlyUrl(url);
			}
		}
	
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new ShareDataTask(activity).execute();
					}		
				}, new OnCancel() {
					
					public void onCancel() {
						activity.finish();
					}
				});
			} else if(isError) {
				Toast.makeText(activity, "Error sharing item", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		public void doFinish() {
			description = "Check out this product I found at Best Buy: " + "<br />" + encodedUrl;
			if(isOpenBoxItem){
				if (openBoxItem != null) {
					String cleanProductName = openBoxItem.getItemTitle().replace("/", "-");
					description = "I added a " + cleanProductName + " to my Best Buy Gift List. " + "<br />" + encodedUrl;
				} 
			}
			else{
				if (product != null) {
					String cleanProductName = product.getName().replace("/", "-");
					description = "I added a " + cleanProductName + " to my Best Buy Gift List. " + "<br />" + encodedUrl;
				}
			}
			
			showShareDialog();
		}
	}
	
	private void showShareDialog() {
		shareDialog = new Dialog(context);
		shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		shareDialog.setContentView(R.layout.open_share_product_dialog);
		shareDialog.setCancelable(true);
		shareDialog.show();
		
		Button facebookBtn = (Button)shareDialog.findViewById(R.id.facebook_btn);
		facebookBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareFacebook();
				shareDialog.cancel();
			}
		});
		
		Button twitterBtn = (Button)shareDialog.findViewById(R.id.twitter_btn);
		twitterBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareTwitter();
				shareDialog.cancel();
			}
		});
		
		Button emailBtn = (Button)shareDialog.findViewById(R.id.email_btn);
		emailBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareOnEmail();
				shareDialog.cancel();
			}
		});
		
		Button smsBtn = (Button)shareDialog.findViewById(R.id.sms_btn);
		smsBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				shareOnSMS();
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
	 * It redirects to the native Gmail application through which the content to be share via mail.
	 */
	private void shareOnEmail() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		
		intent.setType("text/plain");
		
		intent.putExtra(Intent.EXTRA_SUBJECT, "Shared from the Best Buy Android App");
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(description));
		
		try {
			shareProduct(2, intent);	    
		} catch (Exception e) {
			context.showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		}
	}
	
	/**
	 * It redirects to the native SMS application through which the content to be share via sms.
	 */
	private void shareOnSMS() {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.putExtra("sms_body", StoreUtils.removeHtmlTag(description));  
		intent.setType("vnd.android-dir/mms-sms"); 
		context.startActivity(intent); 
	}
	
	/**
	 * It opens the face book on a dialog. 
	 * After opening the dialog the entire control is handled by Face book.
	 */
	private void shareFacebook() {
		String subject = "Shared from the Best Buy Android App";
		
		facebookController = new FacebookController();
        facebookController.initializeAll(StoreUtils.FACEBOOK_API_KEY, context, context, StoreUtils.PERMISSIONS);
        facebookController.setLink(encodedUrl);
        facebookController.setDescription(subject +" - "+ description);
        if(isOpenBoxItem)
        	facebookController.setName(openBoxItem.getItemTitle());
        else
        	facebookController.setName(product.getName());
        facebookController.setImageUrl(imageUrl);
		facebookController.postMessageOnWall();
	}

	/**
	 * This function is redirect to the installed Twitter Application.
	 * If the application is not installed in your device then it prompts as No App found.
	 */
	private void shareTwitter() {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		
		String subject = "Shared from the Best Buy Android App";
		
		description = description.replaceAll("<br />", "\n");
		
		intent.putExtra(Intent.EXTRA_TEXT, StoreUtils.truncateTwitterMessage(description));
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		
		try {
			shareProduct(1, intent);	    
		} catch (Exception e) {
			context.showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
		}
	}
	
	private void shareProduct(int id, Intent intent) {
		String extension = "";
		String appName = "";
		final PackageManager pm = context.getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		ResolveInfo best = null;
		switch (id) {
			case TWITTER_APP:
				extension = ".twitter";
				appName = "twitter";
				break;
			case EMAIL_APP:
				extension = ".gm";
				appName = "gmail";
				break;
		}
		for (final ResolveInfo info : matches) {
			if (info.activityInfo.packageName.endsWith(extension)
					|| info.activityInfo.name.toLowerCase().contains(appName))
				best = info;
		}

		if (best != null) {
			intent.setClassName(best.activityInfo.packageName,
					best.activityInfo.name);
			context.startActivity(intent);
		} else
			context.showDialog(StoreUtils.APP_NOT_FOUND_DIALOG);
	}
}