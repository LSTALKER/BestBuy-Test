package com.facebook.android.helper;

import com.bestbuy.android.util.BBYLog;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

/**
 * Skeleton base class for RequestListeners, providing default error 
 * handling. Applications should handle these error conditions.
 *
 */
public abstract class BaseDialogListener implements DialogListener {
	
	private final String TAG = this.getClass().getName();
	
    public void onFacebookError(FacebookError e) {
    	BBYLog.e(TAG, e.getMessage());
    }

    public void onError(DialogError e) {
    	BBYLog.e(TAG, e.getMessage());    
    }

    public void onCancel() {        
    }
    
}
