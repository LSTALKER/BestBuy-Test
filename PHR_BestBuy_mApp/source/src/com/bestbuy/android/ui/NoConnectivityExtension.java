package com.bestbuy.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.util.AppConfig;

public class NoConnectivityExtension {
	
	private static Dialog dialog = null;
	//private static AlertDialog alertDialog;
	private static AlertDialog connectivityDialog;

	public static void noConnectivity(final Context context) {

		constructConnectionAlertDialog(context, new Retry() {
			public void onRetry() {
				Activity a = (Activity) context;
				context.startActivity(a.getIntent());
				a.finish();
			}
		}, new OnCancel() {
			public void onCancel() {
				return;
			}
		});

		alertConnectivity(context);

	}

	public static void noConnectivity(final Context context, final OnReconnect onReconnect) {

		constructConnectionAlertDialog(context, new Retry() {
			public void onRetry() {
				onReconnect.onReconnect();
			}
		}, new OnCancel() {
			public void onCancel() {
				return;
			}
		});

		alertConnectivity(context);
	}

	public static void noConnectivity(final Context context, final OnReconnect onReconnect, final OnCancel onCancel) {

		constructConnectionAlertDialog(context, new Retry() {
			public void onRetry() {
				onReconnect.onReconnect();
			}
		}, new OnCancel() {
			public void onCancel() {
				onCancel.onCancel();
			}
		});

		alertConnectivity(context);
	}

	private static void alertConnectivity(Context context) {
		// Second alert dialog for turning on/off connectivity
		if (AppConfig.isTest() && !AppData.enableConnectivity) {
			if (connectivityDialog == null || !connectivityDialog.isShowing()) {
				if(context==null || ((Activity) context).isFinishing()) {  
					return;
				}
				connectivityDialog = new AlertDialog.Builder(context).create();
				connectivityDialog.setTitle("Connectivity");
				String connectivityStatus;
				if (AppData.enableConnectivity) {
					connectivityStatus = "On";
				} else {
					connectivityStatus = "Off";
				}
				connectivityDialog.setMessage("Connectivity is currently: " + connectivityStatus);
				connectivityDialog.setButton("Turn on", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AppData.enableConnectivity = true;
						return;
					}
				});
				connectivityDialog.setButton2("Turn off", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						AppData.enableConnectivity = false;
						return;
					}
				});
				connectivityDialog.show();
			}
		}
	}

	private static void constructConnectionAlertDialog(Context context, final Retry onRetry, final OnCancel onCancel) {
		if (dialog == null || !dialog.isShowing()) {
			if(context==null || ((Activity) context).isFinishing()) {  
				return;
			}
			String title;
			dialog = new Dialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.connection_error_dialog);
			((TextView)dialog.findViewById(R.id.title)).setText("Connection Error");
			if (!BaseConnectionManager.isNetAvailable(context) || BaseConnectionManager.isAirplaneMode(context))
				title = "Unable to Connect to the Internet.\nPlease check your connection and try again.";
			else
				title = "Unable to connect.\nPlease try again.";
			((TextView)dialog.findViewById(R.id.message)).setText(title);
			
			Button tryAgainBtn = (Button)dialog.findViewById(R.id.tryagain_btn);
			tryAgainBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
					onRetry.onRetry();
					return;
				}
			});
			
			Button cancelBtn = (Button)dialog.findViewById(R.id.cancel_btn);
			cancelBtn.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					dialog.dismiss();
					onCancel.onCancel();
					return;
				}
			});
			
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					dialog.dismiss();
					onCancel.onCancel();
					return;
				}
			});
			
			dialog.show();
			/*alertDialog.setTitle("Connection Error");
			alertDialog.setMessage("Unable to connect.\nPlease try again.");
			alertDialog.setButton("Try Again", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onRetry.onRetry();
					return;
				}
			});
			alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onCancel.onCancel();
					return;
				}
			});
			
			alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				public void onCancel(DialogInterface dialog) {
					onCancel.onCancel();
					return;
				}
			});*/
			
			if (getActivity(context) != null) {
				if (!getActivity(context).isFinishing()) {
					dialog.show();
				}
			} else {
				dialog.show();
			}
			
/*			//Setting the message center aligned
			TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
			if(messageText != null)
				messageText.setGravity(Gravity.CENTER);
*/		}
	}

	public interface Retry {
		void onRetry();
	}

	public interface OnCancel {
		void onCancel();
	}

	public interface OnReconnect {
		void onReconnect();
	}
	
	private static Activity getActivity(Context context) {
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			return activity;
		}
		return null;
	}
}
