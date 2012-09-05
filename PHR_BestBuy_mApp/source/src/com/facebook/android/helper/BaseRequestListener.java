package com.facebook.android.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.bestbuy.android.util.BBYLog;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

/**
 * Skeleton base class for RequestListeners, providing default error 
 * handling. Applications should handle these error conditions.
 *
 */
public abstract class BaseRequestListener implements RequestListener {

    public void onFacebookError(FacebookError e, final Object state) {
    	BBYLog.e("Facebook", e.getMessage());
    }

    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
    	BBYLog.e("Facebook", e.getMessage());
    }

    public void onIOException(IOException e, final Object state) {
    	BBYLog.e("Facebook", e.getMessage());
    }

    public void onMalformedURLException(MalformedURLException e, final Object state) {
    	BBYLog.e("Facebook", e.getMessage());
    }
    
}
