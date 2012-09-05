package com.bestbuy.android.util;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

/**
 * BBYAsyncTask extends {@link AsyncTask} to automatically handle loading
 * dialogs, error messages, and no connectivity errors in background tasks.
 * 
 * @author phil@recursiveawesome.com
 * 
 */
public abstract class BBYAsyncTask extends AsyncTask<Void, Void, Void> {
	private final String TAG = this.getClass().getName();
	private String errorText;
	private String errorMessage;
	protected boolean isError;
	protected boolean noConnectivity;
	protected Activity activity;
	private BBYDialog dialog;
	private View loadingSpinner;
	private boolean enableLoadingDialog;
	private boolean enableReconnect;
	private boolean isCancelable = true;

	private Exception exception;

	public BBYAsyncTask(Activity activity) {
		initialize(activity, "Loading...");
	}
	
	public BBYAsyncTask(Activity activity, String loadingMessage) {
		initialize(activity, loadingMessage);
	}
	public BBYAsyncTask(Activity activity, String loadingMessage, boolean isCancelable) {
		this.isCancelable = isCancelable;
		initialize(activity, loadingMessage);
	}
	
	private void initialize(Activity activity, String loadingMessage) {
		this.activity = activity;
		isError = false;
		noConnectivity = false;
		enableLoadingDialog = true;
		enableReconnect = true;
		dialog = new BBYDialog(activity);
		dialog.setIndeterminate(true);
		dialog.setCancelable(isCancelable);
		dialog.setMessage(loadingMessage);
	}
	
	private class BBYDialog extends ProgressDialog {
		public BBYDialog(Context context) {
			super(context);
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)  {
		    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		    	if(isCancelable){
			    	dialog.dismiss();
					activity.finish();
			        return true;
		    	}
		    	else
		    		return false;
		    }
		    return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * Override this method to execute code in a background thread. Displays a
	 * loading dialog or spinner until execution is complete.
	 * 
	 * @throws Exception
	 */
	public abstract void doTask() throws Exception;

	/**
	 * Called when <code>doTask</code> has completed successfully, without any
	 * exceptions being thrown. Runs in the main UI thread.
	 * <p>
	 * This method won't be called if any exceptions occurred in the
	 * <code>doTask</code> method, in which case <code>doError</code> will be
	 * called instead.
	 */
	public abstract void doFinish();

	/**
	 * Called when clicking 'Retry' in a no connectivity error dialog. If there
	 * is no connectivity when executing <code>doTask</code>, this method
	 * specifies the retry action. Typically this can be overriden to create a
	 * new instance of the calling task and try to run it again.
	 */
	public void doReconnect() {
	}
	
	/**
	 * Called when clicking 'Cancel' in a no connectivity error dialog.
	 */
	public void doCancelReconnect() {
		
	}

	/**
	 * Called on the Main UI thread after the loading dialog or spinner is shown
	 * and before <code>doTask</code> is run
	 */
	public void doStart() {
	}

	/**
	 * Called to indicate that an exception was thrown while executing
	 * <code>doTask</code>.
	 * <p>
	 * If no connectivity was detected in <code>doTask</code>, a connectivity
	 * alert dialog is displayed.
	 * <p>
	 * If an exception was thrown in <code>doTask</code> unrelated to
	 * connectivity, a Toast is displayed with an error message. The error
	 * message to display can be specified with <code>setErrorMessage</code>. If
	 * it is not specified, an error message will be determined by the HTTP
	 * response if possible.
	 */
	public void doError() {
		if (enableReconnect && noConnectivity) {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
					if (loadingSpinner != null) {
						loadingSpinner.setVisibility(View.GONE);
					}
					doReconnect();
				}
			}, new OnCancel() {
				public void onCancel() {
					doCancelReconnect();
				}
			});
		} else {
			if (errorText.length() > 120) {
				AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
				alertDialog.setTitle("Error");
				alertDialog.setMessage(errorText);
				alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				});
				alertDialog.show();
			} else {
				BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
			}
		}
	}

	/**
	 * Sets a message to be used in the loading dialog that displays while the
	 * task is being run
	 * 
	 * @param loadingText
	 *            the loading message to be displayed
	 */
	public void setLoadingText(String loadingText) {
		dialog.setMessage(loadingText);
	}

	/**
	 * Sets a custom error message to be shown in case of an error.
	 * 
	 * @param errorMessage
	 *            the error message to be displayed
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * Sets a View (typically a {@link ProgressDialog}) to be displayed while
	 * the task is running.
	 * 
	 * @param loadingSpinner
	 *            the
	 */
	public void setLoadingSpinner(View loadingSpinner) {
		this.loadingSpinner = loadingSpinner;
	}
	

	/**
	 * Determines whether or not to enable the loading dialog pop-up.  This is enabled by default.
	 * @param enableLoadingDialog if false, disables the loading dialog.  If true, enables the loading dialog.
	 */
	public void enableLoadingDialog(boolean enableLoadingDialog) {
		this.enableLoadingDialog = enableLoadingDialog;
	}
	
	/**
	 * Determines whether or not we are checking for connectivity when running the task.  This is enabled by default.
	 * @param enableReconnect if false, disables the no connectivity alert.  if true, enables the no connectivity alert.
	 */
	public void enableReconnect(boolean enableReconnect) {
		this.enableReconnect = enableReconnect;
	}
	
	public ProgressDialog getDialog() {
		return dialog;
	}
	
	public Exception getException() {
		return exception;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (activity.isFinishing()) {
			return;
		}
		// Dismiss the loading dialog or loading spinner
		if (loadingSpinner != null) {
			loadingSpinner.setVisibility(View.GONE);
		} else {
			if (!activity.isFinishing()) {
				dialog.dismiss();
			}
		}
		if (noConnectivity || isError) {
			doError();
		} else {
			doFinish();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			doTask();
		} catch (ConnectTimeoutException connectTimeoutEx) {
			noConnectivity = true;
		} catch (SocketTimeoutException socketTimeoutEx) {
			noConnectivity = true;
		} catch (SocketException socketEx) {
			noConnectivity = true;
		} catch (UnknownHostException unknownHostEx) {
			noConnectivity = true;
		} catch (APIRequestException apiEx) {
			BBYLog.printStackTrace(TAG, apiEx);
			errorText = apiEx.getResponse().getStatusLine().getReasonPhrase();
			isError = true;
			exception = apiEx;
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			isError = true;
			exception = e;
		}
		if (errorMessage != null) {
			errorText = errorMessage;
		}
		if (errorText == null) {
			if (!BaseConnectionManager.isNetAvailable(activity) || BaseConnectionManager.isAirplaneMode(activity))
				noConnectivity = true;
			errorText = "Error occured.";
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (loadingSpinner != null) {
			loadingSpinner.setVisibility(View.VISIBLE);
		} else {
			if (enableLoadingDialog) {
				dialog.show();
			}
		}
		doStart();
	}	
}
