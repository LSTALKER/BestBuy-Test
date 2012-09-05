package com.bestbuy.android.util;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.commerce.CCommerce;
import com.bestbuy.android.data.commerce.CError;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

public abstract class CommerceAsyncTask extends AsyncTask<Void, Void, Void> {
	String TAG = this.getClass().getName();
	String errorText;
	String customErrorText;
	protected boolean isError = false;
	protected boolean timedOut = false;
	protected Activity activity;
	ProgressDialog dialog;
	CCommerce commerce;
	protected boolean noConnectivity = false;
	protected boolean isRunning;

	//For mDOT
	protected static final String MDOT_URL = "mDotURL";

	public CommerceAsyncTask(Activity activity,CCommerce commerce) {
		this.activity = activity;
		this.commerce = commerce;
		isRunning = false;
		dialog = new ProgressDialog(activity);
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setMessage("Loading...");
	}

	public abstract void doTask() throws Exception;

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
		if (noConnectivity) {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
				public void onReconnect() {
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

	public void setLoadingText(String loadingText) {
		dialog.setMessage(loadingText);
	}

	public void setCustomErrorText(String errorMessage) {
		this.customErrorText = errorMessage;
	}

	protected void doTimedOut() {
		BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
		Intent i = new Intent(activity, MDOTProductDetail.class);
		i.putExtra(MDOT_URL, MdotWebView.getUrl(activity, MdotWebView.ADD_TO_CART_URL));
		activity.startActivity(i);
		activity.overridePendingTransition(R.anim.slide_up_slow, R.anim.no_animation);
	}

	public boolean isRunning() {
		return isRunning;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if (noConnectivity) {
//			BestBuyApplication.showToastNotification("Unable to connect to the Best Buy checkout system.  Please try again.", activity, Toast.LENGTH_LONG);
			doError();
		} else if (isError) {
			if (customErrorText != null) {
				errorText = customErrorText;
			}
			if (errorText == null) {
				errorText = "Error occured.";
			}
			if (timedOut) {
				doTimedOut();
			}
			/*if(!(activity instanceof CommerceCheckout)){

				if (errorText.length() > 120) {
					AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
					alertDialog.setTitle("Error");
					alertDialog.setMessage(errorText);
					alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						} }); 
					alertDialog.show();
				} else {
					BestBuyApplication.showToastNotification(errorText, activity, Toast.LENGTH_LONG);
				}
				if(commerce != null){
					commerce.setErrors(null);
				}
			}*/
			doError();
		} 
		else {
			doFinish();
		}
		isRunning = false;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			if(commerce != null){
				commerce.setErrors(null);
			}
			doTask();
		} catch (ConnectTimeoutException connectTimeoutEx) {
			noConnectivity = true;
			BBYLog.printStackTrace(TAG, connectTimeoutEx);
		} catch (SocketTimeoutException socketTimeoutEx) {
			noConnectivity = true;
			BBYLog.printStackTrace(TAG, socketTimeoutEx);
		} catch (SocketException socketEx) {
			noConnectivity = true;
			BBYLog.printStackTrace(TAG, socketEx);
		} catch (UnknownHostException unknownHostEx) {
			noConnectivity = true;
			BBYLog.printStackTrace(TAG, unknownHostEx);
		}	catch (APIRequestException apiEx) {
			BBYLog.printStackTrace(TAG, apiEx);

			List<CError> errors = apiEx.getErrors();
			if(commerce != null){
				commerce.setErrors(errors);
			}

			if (errors != null && errors.get(0) != null && errors.get(0).getMessage() != null) {
				errorText = errors.get(0).getMessage();
			}
			if (apiEx.getStatusCode() == 403 && errors == null) {
				//session timed out
				errorText = "Your session timed out.  Please select checkout to login and continue.";
				timedOut = true;
			}
			isError = true;
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			isError = true;
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		isRunning = true;
		dialog.show();
	}
}
