package com.bestbuy.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.KeyEvent;

public class BBYProgressDialog extends ProgressDialog {
	private Activity activity;
	
	public BBYProgressDialog(Activity activity) {
		super(activity);
		initialize(activity, "Loading...");
	}
	
	public BBYProgressDialog(Activity activity, String message) {
		super(activity);
		initialize(activity, message);
	}
	
	private void initialize(Activity activity, String message) {
		this.activity = activity;
		this.setMessage(message);
		this.setIndeterminate(true);
		this.setCancelable(true);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	
	    	if (!activity.isFinishing()) {
	    		this.dismiss();
			}
	    	
    		this.activity.finish();
	    	
	        return true;
	    }
	    
	    return super.onKeyDown(keyCode, event);
	}
}
