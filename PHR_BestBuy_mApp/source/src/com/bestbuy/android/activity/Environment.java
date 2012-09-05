package com.bestbuy.android.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;

/**
 * Lets users choose either saved items list or scanned items list
 * 
 * @author Recursive Awesome
 * 
 */
public class Environment extends Activity {
	//TODO: define this array in XML
	CheckBox proxyToggle;
	EditText proxyHost;
	EditText proxyDomain;
	EditText proxyUsername;
	EditText proxyPassword;

	RadioButton capiProduction;
	RadioButton capiSmoke;
	RadioButton capiStaging;
	RadioButton capiStaging2;
	RadioButton capiStaging3;

	RadioButton smalProduction;
	RadioButton smalStaging;

	RadioButton bbyOfferProduction;
	RadioButton bbyOfferStaging;
	
	RadioButton mDotProduction;
	RadioButton mDotStaging;
	RadioButton mDotQa1;
	RadioButton mDotQa2;
	RadioButton mDotQa3;

	Map<String, String> productionGlobalConfig;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.environment);
		
		proxyToggle = (CheckBox) findViewById(R.id.environment_proxy_toggle);
		proxyHost = (EditText) findViewById(R.id.environment_proxy_host);
		proxyDomain = (EditText) findViewById(R.id.environment_proxy_domain);
		proxyUsername = (EditText) findViewById(R.id.environment_proxy_username);
		proxyPassword = (EditText) findViewById(R.id.environment_proxy_password);

		capiProduction = (RadioButton) findViewById(R.id.environment_capi_production);
		capiSmoke = (RadioButton) findViewById(R.id.environment_capi_smoke);
		capiStaging = (RadioButton) findViewById(R.id.environment_capi_staging);
		capiStaging2 = (RadioButton) findViewById(R.id.environment_capi_staging_2);
		capiStaging3 = (RadioButton) findViewById(R.id.environment_capi_staging_3);

		smalProduction = (RadioButton) findViewById(R.id.environment_smal_production);
		smalStaging = (RadioButton) findViewById(R.id.environment_smal_staging);

		bbyOfferProduction = (RadioButton) findViewById(R.id.environment_bbyoffer_production);
		bbyOfferStaging = (RadioButton) findViewById(R.id.environment_bbyoffer_qa);
		
		 mDotProduction = (RadioButton) findViewById(R.id.environment_mdot_production);
		 mDotStaging = (RadioButton) findViewById(R.id.environment_mdot_staging);
		 mDotQa1 = (RadioButton) findViewById(R.id.environment_mdot_qa1);
		 mDotQa2 = (RadioButton) findViewById(R.id.environment_mdot_qa2);
		 mDotQa3 = (RadioButton) findViewById(R.id.environment_mdot_qa3);

		//Proxy Settings
		if (AppConfig.isProxy()) {
			proxyToggle.setChecked(true);
			setProxySettingsEnabled(true);
		} else {
			proxyToggle.setChecked(false);
			setProxySettingsEnabled(false);
		}

		proxyHost.setText(AppData.getProxyServer());
		proxyDomain.setText(AppData.getProxyDomain());
		proxyUsername.setText(AppData.getProxyUsername());
		proxyPassword.setText(AppData.getProxyPassword());

		//Commerce settings
		if (AppConfig.getCommerceDomain().equals(getAppString(R.string.commerce_domain_production))) {
			capiProduction.setChecked(true);
		} else if (AppConfig.getCommerceDomain().equals(getAppString(R.string.commerce_domain_smoke))) {
			capiSmoke.setChecked(true);
		} else if (AppConfig.getCommerceDomain().equals(getAppString(R.string.commerce_domain_staging))) {
			capiStaging.setChecked(true);
		} else if (AppConfig.getCommerceDomain().equals(getAppString(R.string.commerce_domain_staging_2))) {
			capiStaging2.setChecked(true);
		} else if (AppConfig.getCommerceDomain().equals(getAppString(R.string.commerce_domain_staging_3))) {
			capiStaging3.setChecked(true);
		}

		//SMAL
		if (AppConfig.getSmalHost().equals(getAppString(R.string.SMAL_service_url_production))) {
			smalProduction.setChecked(true);
		} else if (AppConfig.getSmalHost().equals(getAppString(R.string.SMAL_service_url_staging))) {
			smalStaging.setChecked(true);
		}

		//BBY Offer
		if (AppConfig.getBbyOfferURL().equals(getAppString(R.string.bby_offer_url_production))) {
			bbyOfferProduction.setChecked(true);
		} else if (AppConfig.getBbyOfferURL().equals(getAppString(R.string.bby_offer_url_staging))) {
			bbyOfferStaging.setChecked(true);
		}

		//Proxy settings
		proxyToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setProxySettingsEnabled(isChecked);
			}
		});
		
		//mDot
		if (AppConfig.getMdotURL().equals(getAppString(R.string.mdot_url_production))) {
			mDotProduction.setChecked(true);
		} else if (AppConfig.getMdotURL().equals(getAppString(R.string.mdot_url_staging))) {
			mDotStaging.setChecked(true);
		} else if (AppConfig.getMdotURL().equals(getAppString(R.string.mdot_url_qa1))) {
			mDotQa1.setChecked(true);
		} else if (AppConfig.getMdotURL().equals(getAppString(R.string.mdot_url_qa2))) {
			mDotQa2.setChecked(true);
		} else if (AppConfig.getMdotURL().equals(getAppString(R.string.mdot_url_qa3))) {
			mDotQa3.setChecked(true);
		}

		findViewById(R.id.environment_save).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				//Proxy settings
				AppConfig.setUseProxy(proxyToggle.isChecked());
				APIRequest.httpclient = null;
				AppData.setProxyServer(proxyHost.getText().toString());
				AppData.setProxyDomain(proxyDomain.getText().toString());
				AppData.setProxyUsername(proxyUsername.getText().toString());
				AppData.setProxyPassword(proxyPassword.getText().toString());

				//Check capi configuration
				if (capiProduction.isChecked()) {
					AppConfig.setCommerceDomain(getAppString(R.string.commerce_domain_production));
					AppConfig.setCommerceHost(getAppString(R.string.commerce_api_url_production));
					AppConfig.setCommercePath(getAppString(R.string.commerce_path_production));
				} else if (capiSmoke.isChecked()) {
					AppConfig.setCommerceDomain(getAppString(R.string.commerce_domain_smoke));
					AppConfig.setCommerceHost(getAppString(R.string.commerce_api_url_smoke));
					AppConfig.setCommercePath(getAppString(R.string.commerce_path_smoke));
				} else if (capiStaging.isChecked()) {
					AppConfig.setCommerceDomain(getAppString(R.string.commerce_domain_staging));
					AppConfig.setCommerceHost(getAppString(R.string.commerce_api_url_staging));
					AppConfig.setCommercePath(getAppString(R.string.commerce_path_staging));
				} else if (capiStaging2.isChecked()) {
					AppConfig.setCommerceDomain(getAppString(R.string.commerce_domain_staging_2));
					AppConfig.setCommerceHost(getAppString(R.string.commerce_api_url_staging_2));
					AppConfig.setCommercePath(getAppString(R.string.commerce_path_staging_2));
				} else if (capiStaging3.isChecked()) {
					AppConfig.setCommerceDomain(getAppString(R.string.commerce_domain_staging_3));
					AppConfig.setCommerceHost(getAppString(R.string.commerce_api_url_staging_3));
					AppConfig.setCommercePath(getAppString(R.string.commerce_path_staging_3));
				}

				//SMAL
				if (smalProduction.isChecked()) {
					AppConfig.setSmalHost(getAppString(R.string.SMAL_service_url_production));
					AppConfig.setSmalApiKey(getAppString(R.string.SMAL_api_key_production));
					AppConfig.setSmalSecureHost(getAppString(R.string.SMAL_service_secure_url_production));
					AppConfig.setSmalSecureApiKey(getAppString(R.string.SMAL_secure_api_key_production));
				} else if (smalStaging.isChecked()) {
					AppConfig.setSmalHost(getAppString(R.string.SMAL_service_url_staging));
					AppConfig.setSmalApiKey(getAppString(R.string.SMAL_api_key_staging));
					AppConfig.setSmalSecureHost(getAppString(R.string.SMAL_service_secure_url_staging));
					AppConfig.setSmalSecureApiKey(getAppString(R.string.SMAL_secure_api_key_staging));
				}

				//BBYOffer
				if (bbyOfferProduction.isChecked()) {
					AppConfig.setBbyOfferApiKey(getAppString(R.string.bby_offer_api_key_production));
					AppConfig.setBbyOfferHost(getAppString(R.string.bby_offer_url_production));
				} else if (bbyOfferStaging.isChecked()) {
					AppConfig.setBbyOfferApiKey(getAppString(R.string.bby_offer_api_key_staging));
					AppConfig.setBbyOfferHost(getAppString(R.string.bby_offer_url_staging));
				}				
				
				//mDot
				if (mDotProduction.isChecked()) {
					AppConfig.setmDotURL(getAppString(R.string.mdot_url_production));
					AppConfig.setmDotHost(getAppString(R.string.mdot_host));
					AppConfig.setmDotSignInHost(getAppString(R.string.mdot_signin_url_production));
				} else if (mDotStaging.isChecked()) {
					AppConfig.setmDotURL(getAppString(R.string.mdot_url_staging));
					AppConfig.setmDotHost(getAppString(R.string.mdot_host_staging));
					AppConfig.setmDotSignInHost(getAppString(R.string.mdot_signin_url_staging));
				} else if (mDotQa1.isChecked()) {
					AppConfig.setmDotURL(getAppString(R.string.mdot_url_qa1));
					AppConfig.setmDotHost(getAppString(R.string.mdot_host_qa1));
					AppConfig.setmDotSignInHost(getAppString(R.string.mdot_signin_url_qa1));
				} else if (mDotQa2.isChecked()) {
					AppConfig.setmDotURL(getAppString(R.string.mdot_url_qa2));
					AppConfig.setmDotHost(getAppString(R.string.mdot_host_qa2));
					AppConfig.setmDotSignInHost(getAppString(R.string.mdot_signin_url_qa2));
				} else if (mDotQa3.isChecked()) {
					AppConfig.setmDotURL(getAppString(R.string.mdot_url_qa3));
					AppConfig.setmDotHost(getAppString(R.string.mdot_host_qa3));
					AppConfig.setmDotSignInHost(getAppString(R.string.mdot_signin_url_qa3));
				}
				
				new SaveSettingsTask(Environment.this).execute();
			}
		});
	}

	public String getAppString(int resId) {
		return AppData.getContext().getString(resId);
	}

	public void setProxySettingsEnabled(boolean enabled) {
		if (enabled) {
			proxyHost.setEnabled(true);
			proxyDomain.setEnabled(true);
			proxyUsername.setEnabled(true);
			proxyPassword.setEnabled(true);
		} else {
			proxyHost.setEnabled(false);
			proxyDomain.setEnabled(false);
			proxyUsername.setEnabled(false);
			proxyPassword.setEnabled(false);
		}
	}
	
	private class SaveSettingsTask extends BBYAsyncTask {
		public SaveSettingsTask(Activity activity) {
			super(activity);
			getDialog().setCancelable(false);
		}
		@Override
		public void doReconnect() {
			new SaveSettingsTask(activity).execute();
		}
		
		@Override
		public void doCancelReconnect() {
			return;
		}
		
		@Override
		public void doFinish() {
			//BestBuyApplication.showToastNotification("Changes saved.", Environment.this, Toast.LENGTH_LONG);
			Intent i = new Intent(Environment.this, Home.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}
		@Override
		public void doTask() throws Exception {
			AppData.fetchGlobalConfig();
			
			//Save off preferences
			SharedPreferences prefs = AppData.getSharedPreferences();
			prefs.edit().putString("bbyListHost", AppConfig.getBbyListHost()).commit();
			prefs.edit().putString("bbyListApiKey", AppConfig.getBbyListAPIKey()).commit();
			prefs.edit().putString("bbyOfferHost", AppConfig.getBbyOfferURL()).commit();
			prefs.edit().putString("bbyOfferApiKey", AppConfig.getBbyOfferApiKey()).commit();
			prefs.edit().putString("smalHost", AppConfig.getSmalHost()).commit();
			prefs.edit().putString("smalSecureHost", AppConfig.getSmalSecureHost()).commit();
			prefs.edit().putString("smalApiKey", AppConfig.getSmalApiKey()).commit();
			prefs.edit().putString("smalSecureApiKey", AppConfig.get301SecureAPIKey()).commit();
			prefs.edit().putString("bbyScanHost", AppConfig.getBbyScanHost()).commit();
			prefs.edit().putString("bbyScanApiKey", AppConfig.getBbyScanAPIKey()).commit();
			prefs.edit().putString("commerceDomain", AppConfig.getCommerceDomain()).commit();
			prefs.edit().putString("commerceHost", AppConfig.getCommerceHost()).commit();
			prefs.edit().putString("commercePath", AppConfig.getCommercePath()).commit();
			prefs.edit().putString("mDotUrl", AppConfig.getMdotURL()).commit();
			prefs.edit().putString("mDotSignInHost", AppConfig.getMdotSignInURL()).commit();
			prefs.edit().putString("mDotHost", AppConfig.getmDotHost()).commit();
			
			prefs.edit().putBoolean("useProxy", AppConfig.isProxy()).commit();
			prefs.edit().putString("proxyDomain", AppData.getProxyDomain()).commit();
			prefs.edit().putString("proxyPassword", AppData.getProxyPassword()).commit();
			prefs.edit().putString("proxyServer", AppData.getProxyServer()).commit();
			prefs.edit().putString("proxyUsername", AppData.getProxyUsername()).commit();
			
			//If we are using STAGING SMAL and CAPI Production, overwrite the capi API key with prod.
			if (smalStaging.isChecked() && capiProduction.isChecked()) {
				fetchProductionConfig();
				String prodCapiKey = productionGlobalConfig.get("capiAPIKey");
				AppData.getGlobalConfig().put("capiAPIKey", prodCapiKey);
			}
		}
		
		public void fetchProductionConfig() throws Exception {
			Map<String, String> secureUrlParams = new HashMap<String, String>();
			secureUrlParams.put("api_key", getAppString(R.string.SMAL_secure_api_key_production));
			String configResult = APIRequest.makeGetRequest(getAppString(R.string.SMAL_service_secure_url_production), AppData._301_CONFIG_GLOBAL_HASH, secureUrlParams, false, false);

			JSONObject configArray = new JSONObject(configResult);
			Iterator<String> iter = configArray.keys();
			productionGlobalConfig = new HashMap<String, String>();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = configArray.getString(key);
				productionGlobalConfig.put(key, value);
			}
		}
	}
}
