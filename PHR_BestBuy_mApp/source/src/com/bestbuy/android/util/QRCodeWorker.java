package com.bestbuy.android.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bestbuy.android.activity.BBYCode;
import com.bestbuy.android.activity.GamingSearch;
import com.bestbuy.android.activity.Home;
import com.bestbuy.android.activity.MDOTProductDetail;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.ProductCompare;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SkuManager;
import com.bestbuy.android.eplib.util.EpLibUtil;
import com.bestbuy.android.scanner.CaptureActivity;
import com.bestbuy.android.scanner.Intents;

/**
 * Allows for decoding and processing of QR Codes
 * 
 * @author Recursive Awesome
 * 
 */
public class QRCodeWorker {

	private static final int REQUEST_DECODE = 99; // Arbitrary ID for request
	private String TAG = this.getClass().getName();

	// passed to DecodeActivity
	Activity postScanActivity;
	JSONObject jsonData;
	AppData appData;
	String url;
	private static String screen_name_identify="0";
	String callingClassName;
	Home home;
	public void openQRCode(Activity activity, String screen_name) {
		// Create an Intent to start the decode activity
		screen_name_identify=screen_name;
		Intent objIntChild = new Intent(Intent.ACTION_GET_CONTENT);
		if (objIntChild != null) {
			objIntChild.putExtra("screen_name", screen_name_identify);
			objIntChild.setClassName(activity, CaptureActivity.class.getName());
			activity.startActivityForResult(objIntChild, REQUEST_DECODE);
			// startActivityForResult(objIntChild, REQUEST_DECODE);
		}
	}

	public void openURL(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		postScanActivity.startActivity(i);
	}

	public void onActivityResult(int iRequestCode, int iResultCode, Intent intData, Activity activity)  {
		postScanActivity = activity;
		if ((iResultCode == MenuActivity.RESULT_OK) && (intData != null)) {
			// get the url... not sure if we'll need it at some point
			/*
			 * String scannedUrl = intData
			 * .getStringExtra(com.mobiledatasys.decoder.Glbl.EXTRA_CODE);
			 */

			final String scanResult = intData.getExtras().getString(Intents.Scan.RESULT);
			new LoadScanResultTask(activity, scanResult).execute();
		} /*else if (iResultCode == MenuActivity.RESULT_CANCELED) {
			//BestBuyApplication.showToastNotification("Decoding cancelled.", activity, Toast.LENGTH_LONG);
		} else {
			BestBuyApplication.showToastNotification("Not able to reach Best Buy QR Code server.", activity, Toast.LENGTH_LONG);
		}*/
	}

	private void showProduct() {
		Intent i = new Intent(postScanActivity, MDOTProductDetail.class);
		postScanActivity.startActivity(i);
	}

	private void compareProducts(Activity activity) throws Exception {
		if (activity.getClass().getName().contains("ProductCompare")) {
			Intent i = new Intent(activity, ProductCompare.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			i.putExtra("Scan", true);
			activity.startActivity(i);
		} else {
			Intent i = new Intent(activity, ProductCompare.class);
			activity.startActivity(i);
		}
	}

	private class LoadScanResultTask extends BBYAsyncTask {

		String scanResult;
		boolean inApp = false;
		Product product;
		String url = "";
		String message = "";
		boolean isBBYCode = true;

		public LoadScanResultTask(Activity activity , String scanResult) {
			super(activity);
			this.scanResult = scanResult;
		}
		private void showCompare() {
			try {
				ArrayList<Product> compareProducts = appData.getCompareProducts();
				// for logging calls. pass in a list of skus being compared from scan.
				Map<String, String> params = new HashMap<String, String>();
				StringBuilder comparedSkus = new StringBuilder();
				String delimiter = "";
				for (Product product : compareProducts) {
					comparedSkus.append(delimiter);
					comparedSkus.append(product.getSku());
					delimiter = ",";
				}
				//EPLIB
				
				EpLibUtil.trackEvent(activity.getApplicationContext(), EpLibUtil.ACTION_PAGE_LOAD,EpLibUtil.ITEM_TYPE_COMPARE_CODES);
				
				params.put("value", comparedSkus.toString());
				EventsLogging.fireAndForget(EventsLogging.QR_CODES_COMPARED, params);

				//				if (compareProducts.size() == 1) {
				//					Iterator<Product> iter = compareProducts.iterator();
				//					appData.setSelectedProduct(iter.next());
				//					showProduct(postScanActivity);
				//					// viewing products, clear any scanned products.
				//					appData.clearCompareProducts();
				//				} else {
				/// need to send them off to the ProductCompare screen. We have more than 1 product scanned...
				compareProducts(postScanActivity);
			} catch (Exception ex) {
				BBYLog.e(TAG, "Exception showing the scanned item: " + ex.getMessage());
			}
		}

		private void parseProduct(JSONObject jsonProduct) throws Exception {
			if (jsonProduct != null) {
				product = new Product();
				product.loadSearchResultData(jsonProduct);
				product.loadDetailsData(jsonProduct);
			}
		}

		private void parseError(String responseBody) throws Exception{
			JSONObject jsonError = new JSONObject(responseBody);
			JSONObject jsonProduct = jsonError.optJSONObject("product");

			//If there is a product, ignore the error and show the product.
			if (jsonProduct != null) {
				parseProduct(jsonProduct);
				return;
			}
			url = jsonError.optString("url");
			message = jsonError.getString("message");
			if (message != null) {
				if (message.equalsIgnoreCase("not a Best Buy code")) {
					isBBYCode = false;
				} else if (message.equals("code expired")) {
					message = "This Best Buy code has expired.";
				} else if (message.equals("error connecting to Remix")) {
					message = "Unable to connect to the BBYScan server.  Please try again later.";
				} else if (message.equals("landing page not found")) {
					message = "This Best Buy landing page is not found.";
				} else if (message.equals("bby.us URL invalid")) {
					message = "This Best Buy URL is invalid.";
				} else {
					message = "Not a valid Best Buy code.";
				}
			} else {
				message = "Not a valid Best Buy code.";
			}
		}

		@Override
		public void doTask() throws Exception {
			BBYLog.d(TAG, "scanResult: " + scanResult);
			Log.e(TAG, "scanResult: " + scanResult);
			appData = ((BestBuyApplication) postScanActivity.getApplication()).getAppData();
			//scanResult = scanResult.replace("bbyurl.com", "bby.us");  //TODO: do we need to do this?
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("data", scanResult));
			nameValuePairs.add(new BasicNameValuePair("apikey", AppConfig.getBbyScanAPIKey()));
			nameValuePairs.add(new BasicNameValuePair("reader", AppConfig.getEncryptedDeviceId()));
			nameValuePairs.add(new BasicNameValuePair("bby_app_name", "BestBuy"));
			nameValuePairs.add(new BasicNameValuePair("bby_app_version", AppConfig.getAppVersion()));
			nameValuePairs.add(new BasicNameValuePair("platform", "Android"));
			nameValuePairs.add(new BasicNameValuePair("os_version", AppConfig.getOSVersion()));
			try {
				String result = APIRequest.makeGetRequest(AppConfig.getBbyScanHost(), nameValuePairs, null, null);
				JSONObject jsonResult = new JSONObject(result);
				url = jsonResult.optString("url");
				inApp = jsonResult.optBoolean("inapp");
				message = jsonResult.optString("message");
				
				//EPLIB
				
				EpLibUtil.trackEventByCodeScan(activity, EpLibUtil.ACTION_PAGE_LOAD, EpLibUtil.ITEM_TYPE_QR_CODE_SCAN, url);
				
				// events logging of the qr_code results
				Map<String, String> eventsParams = new HashMap<String, String>();
				eventsParams.put("qr_code", scanResult);
				eventsParams.put("qr_code_url", url);

				callingClassName = postScanActivity.getClass().getName();

				if (callingClassName != null && callingClassName.equals("com.bestbuy.android.activity.ProductCompare")) { //not from home page
					eventsParams.put("qr_code_screen", "PDP");
				} else {
					eventsParams.put("qr_code_screen", "Home Page");
				}

				EventsLogging.fireAndForget(EventsLogging.QR_CODE_SCAN, eventsParams);

				JSONObject jsonProduct = jsonResult.optJSONObject("product");
				parseProduct(jsonProduct);
			} catch (APIRequestException ex) {
				BBYLog.printStackTrace(TAG, ex);
				parseError(ex.getResponseBody());
			}
		}
		
		@Override
		public void doFinish() {
			if (url.length() > 0) {
				//Convert youtube links to mobile youtube page
				url = url.replace("www.youtube.com", "m.youtube.com");
				if (inApp) {
					//Launch the url in the app
					Intent i = new Intent(postScanActivity, BBYCode.class);
					i.putExtra("url", url);
					postScanActivity.startActivity(i);
				} else if (isBBYCode) {
					showOpenUrlDialog(url);
				} else {
					launchURLInBrowser(url);
				}
			} else if (product != null) {
				//Add sku to list of scanned skus
				appData = ((BestBuyApplication) postScanActivity.getApplication()).getAppData();
				if (!appData.getCompareProducts().contains(product)) {
					appData.addCompareProduct(product);
					SkuManager.addToCompareSkus(postScanActivity, product.getSku());
				}
				SkuManager.addToScannedSkus(postScanActivity, product.getSku());
				//System.out.println("***print pin"+product.getSku());
				if (postScanActivity.getClass().getSimpleName().equalsIgnoreCase("GamingSearch")) {
					if (product.getType().equalsIgnoreCase("game")) {
						appData.setScannedProduct(product);
						Intent i = new Intent(postScanActivity, GamingSearch.class);
						postScanActivity.startActivity(i);
					} else {
						BestBuyApplication.showToastNotification("Please scan a valid game.", postScanActivity, Toast.LENGTH_SHORT);
					}
				} else if (postScanActivity.getClass().getSimpleName().equals("ProductCompare")) {
					showCompare();
				} else {
					//Show dialog
					AlertDialog al = new AlertDialog.Builder(postScanActivity).create();
					al.setTitle("Scan Successful");
					al.setButton("View items" + " (" + appData.getCompareProducts().size() + ")", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface di, int which) {
							if (appData.getCompareProducts().size() == 1) {
								appData.setSelectedProduct(product);
								showProduct();
							} else {
								showCompare();
							}
						}
					});

					al.setButton3("Scan to Compare", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface di, int which) {
							try {
								new QRCodeWorker().openQRCode(postScanActivity,"CODE_SCAN");
							} catch (Exception ex) {
								BBYLog.e(TAG, "QRCodeWorker - Error parsing the JSON results: " + ex.getMessage());
							}
						}
					});
					al.setButton2("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface di, int which) {
							appData.getCompareProducts().clear();
						}
					});
					al.show();
				}
			} /*else if (message.length() > 0) {
				BestBuyApplication.showToastNotification(message, postScanActivity, Toast.LENGTH_SHORT);
			}*/ else {
				//Not a best buy code
				//BestBuyApplication.showToastNotification("Not a best buy code.", postScanActivity, Toast.LENGTH_SHORT);
				showNotValidCodeDialog();
			}
		}
		
		@Override
		public void doReconnect() {
			new LoadScanResultTask(activity, scanResult).execute();
		}
	}

	private void showOpenUrlDialog(final String url) {	
		AlertDialog al = new AlertDialog.Builder(postScanActivity).create();
		al.setTitle("Best Buy");
		al.setMessage(url + "\n\nThis url will be opened in the browser.  Would you like to continue?");
		al.setButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		al.setButton2("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//Launch the url in browser
				if (url.contains(".mp4")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(url), "video/mp4");
					postScanActivity.startActivity(intent);
				} else {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					postScanActivity.startActivity(i);
				}
			}
		});
		al.show();

	}

	/*private void showNotBBYCodeDialog(final String url) {
		AlertDialog al = new AlertDialog.Builder(postScanActivity).create();
		al.setTitle("Best Buy");
		al.setMessage(url + "\n\nThis is not a valid BBY Code, would you like to continue?");
		al.setButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		al.setButton2("Open URL", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//Launch the url in browser
				if (url.contains(".mp4")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse(url), "video/mp4");
					postScanActivity.startActivity(intent);
				} else {
					Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					postScanActivity.startActivity(i);
				}
			}
		});
		al.show();
	}*/
	
	private void showNotValidCodeDialog() {
		AlertDialog al = new AlertDialog.Builder(postScanActivity).create();
		al.setTitle("Best Buy");
		al.setMessage("This is not a valid QR Code");
		al.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		
		al.show();
	}
	
	private void launchURLInBrowser(String url){
		if (url.contains(".mp4")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(url), "video/mp4");
			postScanActivity.startActivity(intent);
		} else {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			postScanActivity.startActivity(i);
		}
	}
}
