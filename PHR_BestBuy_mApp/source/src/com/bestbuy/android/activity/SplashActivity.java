package com.bestbuy.android.activity;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;

public class SplashActivity extends Activity {
	
	private final String TAG = this.getClass().getName();
	
	private String maintenanceShutDownFlag = "";
	private String maintenanceSplashURL;
	private String maintenanceSplashRetinaURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		runGlobalConfigTask(this);
	}

	public void runGlobalConfigTask(Activity activity) {
		new GlobalConfigTask(activity).execute();
	}

	private class GlobalConfigTask extends BBYAsyncTask {
		public GlobalConfigTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
		}

		@Override
		public void doReconnect() {
			runGlobalConfigTask(activity);
		}

		@Override
		public void doCancelReconnect() {
			finish();
		}

		@Override
		public void doFinish() {
			maintenanceShutDownFlag = AppData.getOMSOutageData().get(AppData.MAINTANANCE_SHUTDOWN);
			maintenanceSplashURL = AppData.getOMSOutageData().get(AppData.MAINTANANCE_SPLASH);
			maintenanceSplashRetinaURL = AppData.getOMSOutageData().get(AppData.MAINTANANCE_SPLASH_RETINA);
			
			if (maintenanceShutDownFlag.equalsIgnoreCase("false")) {
				AppData.setSplashScreen(true);
				Intent i = new Intent(SplashActivity.this, Home.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
				
			} else{
				runOnUiThread(new Runnable() {
					public void run() {
						Drawable drawable = null;
						
						// Checking for medium and low density
						try {
							switch (getScreenDensity()) {
							case DisplayMetrics.DENSITY_LOW :
							case DisplayMetrics.DENSITY_MEDIUM :
									drawable = grabImageFromUrl(maintenanceSplashURL);
									break;

							case DisplayMetrics.DENSITY_HIGH :
							case DisplayMetrics.DENSITY_XHIGH :
								drawable = grabImageFromUrl(maintenanceSplashRetinaURL);
									break;
							}
							
						} catch (Exception exception) {
							BBYLog.printStackTrace(TAG, exception);
						}
						
						// Setting OMS outage splash if it is shutdown
						((LinearLayout) findViewById(R.id.splash_screen_view)).setBackgroundDrawable(drawable);
					}
				});
			}
		}
		@Override
		public void doTask() throws Exception {
			AppData.fetchGlobalConfig();
			
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (maintenanceShutDownFlag.equalsIgnoreCase("false")) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// Getting device density
	private int getScreenDensity() {
		return getResources().getDisplayMetrics().densityDpi;
	}
	
	private Drawable grabImageFromUrl(String url) throws Exception {
		return Drawable.createFromStream((InputStream) new URL(url).getContent(), "src");
	}

}
