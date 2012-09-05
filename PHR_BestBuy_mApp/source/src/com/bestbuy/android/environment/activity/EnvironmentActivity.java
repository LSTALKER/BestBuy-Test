/*
 * EnvironmentActivity.java
 * © Photon Infotech
 * Confidential and proprietary.
 */
package com.bestbuy.android.environment.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.Home;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.environment.dataobject.EnvironmentBean;
import com.bestbuy.android.environment.dataobject.Module;
import com.bestbuy.android.environment.dataobject.ProxyInfoBean;
import com.bestbuy.android.environment.dataobject.TagInfoBean;
import com.bestbuy.android.environment.parser.ModuleXmlHandler;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EnvironmentConstants;

/**
 * Main controller activity for the environment selection application. This
 * Activity class takes care of creating a window and place UI by parsing the
 * res/raw/environment.xml
 * 
 * @author Arun Jyothis
 * Created On 22/05/2012
 */
public class EnvironmentActivity extends Activity {
	private static final String TAG = EnvironmentActivity.class.getSimpleName();
	private static HashMap<String, String> hmSelValues = new HashMap<String, String>();
	private static Editor prefsEditor;

	private static EditText hostView;
	private static EditText domainView;
	private static EditText userName;
	private static EditText passWord;

	private static CheckBox proxyToggle;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		BBYLog.d(TAG, "onCreate() started");
		super.onCreate(savedInstanceState);
		 getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		try {
			AppConfig.printValues();
			initUI();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BBYLog.d(TAG, "onCreate() ended");

	}

	private void initUI() throws ParserConfigurationException, SAXException,
			IOException {
		BBYLog.d(TAG, "initUI() started");
		// Creates UI Containers, sets orientation
		ScrollView scrollView = new ScrollView(this);

		LinearLayout headerLayout = new LinearLayout(this);
		headerLayout.setBackgroundResource(R.drawable.bg_app_header);

		TextView headerText = new TextView(this);
		headerText.setTextColor(Color.WHITE);
		headerText.setTextSize(18);
		headerText.setTypeface(null, Typeface.BOLD);
		headerText.setText(R.string.test_envi_text);
		headerText.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams headerTextParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		headerTextParams.gravity = Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL;
		headerText.setLayoutParams(headerTextParams);
		headerLayout.addView(headerText);

		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.addView(headerLayout);

		LinearLayout secondaryLayout = new LinearLayout(this);
		secondaryLayout.setOrientation(LinearLayout.VERTICAL);
		secondaryLayout.setPadding(10, 0, 10, 0);
		secondaryLayout.setBackgroundResource(R.drawable.whitebox);

		LinearLayout proxy = new LinearLayout(this);
		proxy.setFocusable(false);
		proxy.setOrientation(LinearLayout.VERTICAL);
		proxy.setPadding(10, 0, 10, 0);
		proxy.setBackgroundResource(R.drawable.proxybox);

		TextView proxyHeader = new TextView(this);
		proxyHeader.setText(R.string.proxy_settings_text);

		proxyHeader.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		proxyHeader.setTextColor(R.color.black);
		proxyHeader.setTypeface(null, Typeface.BOLD);
		LinearLayout.LayoutParams proxyHeaderparams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		proxyHeaderparams.setMargins(0, 15, 0, 15);
		proxyHeader.setLayoutParams(proxyHeaderparams);

		LinearLayout textprefs = new LinearLayout(this);
		textprefs.setDuplicateParentStateEnabled(true);
		proxyToggle = new CheckBox(this);
		proxyToggle.setChecked(true);
		proxyToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				setProxySettingsEnabled(isChecked);
			}

		});

		TextView proxyEnaDisable = new TextView(this);
		proxyEnaDisable.setText("Enable proxy");
		textprefs.addView(proxyToggle);
		textprefs.addView(proxyEnaDisable);

		proxy.addView(textprefs);
		LinearLayout.LayoutParams EditTextparams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		EditTextparams.setMargins(0, 10, 0, 10);
		hostView = new EditText(this);
		hostView.setHint("Host");

		domainView = new EditText(this);
		domainView.setHint("Domain");

		userName = new EditText(this);
		userName.setHint("UserName");

		passWord = new EditText(this);
		passWord.setHint("Password");

		// Proxy Settings
		if (AppConfig.isProxy()) {
			proxyToggle.setChecked(true);
			setProxySettingsEnabled(true);
		} else {
			proxyToggle.setChecked(false);
			setProxySettingsEnabled(false);
		}
		proxy.addView(hostView);
		proxy.addView(domainView);
		proxy.addView(userName);
		proxy.addView(passWord);

		secondaryLayout.addView(proxyHeader);
		secondaryLayout.addView(proxy);
		mainLayout.addView(secondaryLayout);
		scrollView.addView(mainLayout);

		// Creates UI Components
		// Creates top button 'skip', its onClick() listener
		Button skipButton = new Button(this);
		skipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		skipButton.setText(EnvironmentConstants.BUTTON_TXT_SKIP);
		// ll.addView(skipButton);

		// Reads res/raw/environment.xml
		final SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
		final XMLReader reader = sp.getXMLReader();
		final ModuleXmlHandler handler = new ModuleXmlHandler();
		reader.setContentHandler(handler);
		InputStream is = this.getResources().getAssets()
				.open("sample_env_selection_V1.xml");
		reader.parse(new InputSource(is));
		final ArrayList<Module> module = handler.getRecords();
		final ProxyInfoBean proxys = handler.getProxyInfo();
		if (proxys != null) {
			if (proxys.getHost() != null) {
				hostView.setText(proxys.getHost());
			}
			if (proxys.getDomain() != null) {
				domainView.setText(proxys.getDomain());

			}
			if (proxys.getUsername() != null) {
				userName.setText(proxys.getUsername());

			}
			if (proxys.getPassword() != null) {
				passWord.setText(proxys.getPassword());

			}

		}
		int numResults = module.size();
		if ((numResults <= 0)) {
			Toast.makeText(EnvironmentActivity.this,
					EnvironmentConstants.TXT_NO_ENV, Toast.LENGTH_LONG).show();
			finish();
		}
		for (int i = 0; i < module.size(); i++) {
			Module eachModule = module.get(i);
			ArrayList<EnvironmentBean> lists = module.get(i).getFiles();
			RadioGroup radioGroup = new RadioGroup(this);
			radioGroup.setBackgroundResource(R.drawable.commerce_box);
			LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.FILL_PARENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(10, 0, 10, 0);

			TextView tvModuleName = new TextView(this);
			tvModuleName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			tvModuleName.setText(module.get(i).getModuleName());
			tvModuleName.setTextColor(R.color.black);
			tvModuleName.setTypeface(null, Typeface.BOLD);
			LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			para.setMargins(0, 15, 0, 15);
			tvModuleName.setLayoutParams(para);
			secondaryLayout.addView(tvModuleName);
			for (int j = 0; j < lists.size(); j++) {
				EnvironmentBean individualEnv = lists.get(j);

				TagInfoBean eachTagInfo = new TagInfoBean();
				eachTagInfo.setModuleName(eachModule.getModuleName());
				eachTagInfo.setModuleTitle(eachModule.getModuleTitle());
				eachTagInfo.setIsProxy(eachModule.getIsProxy());

				eachTagInfo.setEnvName(individualEnv.getName());
				eachTagInfo.setEnvTitle(individualEnv.getTitle());
				eachTagInfo.setEnvHost(individualEnv.getHost());
				eachTagInfo.setEnvDomain(individualEnv.getDomain());
				eachTagInfo.setEnvPath(individualEnv.getPath());
				eachTagInfo.setEnvSecureHost(individualEnv.getSecureHost());
				eachTagInfo.setEnvApikey(individualEnv.getApiKey());
				eachTagInfo.setEnvSecureApiKey(individualEnv.getSecureApiKey());

				RadioButton radioButton = new RadioButton(this);
				LinearLayout.LayoutParams radioButtonParams = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				radioButton.setLayoutParams(radioButtonParams);
				View lineview = new View(this);
				lineview.setMinimumHeight(1);
				lineview.setBackgroundColor(R.color.black);

				radioButton
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									TagInfoBean tag = (TagInfoBean) buttonView
											.getTag();

									String host = tag.getEnvHost();
									BBYLog.v("host" + tag.getModuleName(),
											"----" + host);
									if (host != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_HOST;
										hmSelValues.put(key, host);
									}
									String path = tag.getEnvPath();
									BBYLog.v("path-" + tag.getModuleName(),
											"----" + path);
									if (path != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_PATH;
										hmSelValues.put(key, path);
									}
									String domain = tag.getEnvDomain();
									BBYLog.v("domain-" + tag.getModuleName(),
											"----" + domain);
									if (domain != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_DOMAIN;
										hmSelValues.put(key, domain);
									}
									String apiKey = tag.getEnvApikey();
									BBYLog.v("apiKey-" + tag.getModuleName(),
											"----" + apiKey);
									if (apiKey != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_APIKEY;
										hmSelValues.put(key, apiKey);
									}
									String secureHost = tag.getEnvSecureHost();
									BBYLog.v(
											"secureHost-" + tag.getModuleName(),
											"----" + secureHost);
									if (secureHost != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_SECUREHOST;
										hmSelValues.put(key, secureHost);
									}

									String secureApiKey = tag
											.getEnvSecureApiKey();
									BBYLog.v(
											"secureApiKey-"
													+ tag.getModuleName(),
											"----" + secureApiKey);
									if (secureApiKey != null) {
										String key = tag.getModuleName()
												+ EnvironmentConstants.TAG_SECURE_APIKEY;
										hmSelValues.put(key, secureApiKey);
									}

								}

							}
						});

				String text = individualEnv.getTitle();
				radioButton.setText(text);
				radioButton.setTextColor(R.color.black);
				radioButton.setTag(eachTagInfo);
				radioGroup.addView(radioButton, layoutParams);
				if (j + 1 == lists.size()) {

				} else {
					radioGroup.addView(lineview);
				}
				if (j == EnvironmentConstants.ENVIRONMENT1) {
					radioButton.setChecked(true);
				}

			}
			secondaryLayout.addView(radioGroup);
		}

		Button saveButton = new Button(this);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SaveSettingsTask(EnvironmentActivity.this).execute();
				// finish();

			}
		});
		saveButton.setText("Save Changes");
		saveButton.setTextColor(Color.WHITE);
		saveButton.setBackgroundResource(R.drawable.btn_blue_3);
		LinearLayout.LayoutParams saveButtonparams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		saveButtonparams.setMargins(0, 10, 20, 10); // left,top,right, bottom
		saveButtonparams.gravity = Gravity.RIGHT;
		saveButton.setLayoutParams(saveButtonparams);

		secondaryLayout.addView(saveButton);
		this.setContentView(scrollView);
		BBYLog.d(TAG, "initUI() ended");
	}

	/**
	 * Saves the selected modules as keys and label(text) & values as values in
	 * the SharedPreferences file that preferences managed by this will use
	 * 
	 * @param context
	 *            The application context.
	 */

	public void setProxySettingsEnabled(boolean enabled) {
		if (enabled) {
			hostView.setEnabled(true);
			domainView.setEnabled(true);
			userName.setEnabled(true);
			passWord.setEnabled(true);
		} else {
			hostView.setEnabled(false);
			domainView.setEnabled(false);
			userName.setEnabled(false);
			passWord.setEnabled(false);
		}
	}

	private static void saveProxy() {
		hmSelValues.put(EnvironmentConstants.PROXY_SELECTION,
				"" + proxyToggle.isChecked());
		hmSelValues.put(EnvironmentConstants.PROXY_HOST, hostView.getText()
				.toString().trim());
		hmSelValues.put(EnvironmentConstants.PROXY_DOMAIN, domainView.getText()
				.toString());
		hmSelValues.put(EnvironmentConstants.PROXY_USERNAME, userName.getText()
				.toString().trim());
		hmSelValues.put(EnvironmentConstants.PROXY_PASSWORD, passWord.getText()
				.toString().trim());
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
			// BestBuyApplication.showToastNotification("Changes saved.",
			// Environment.this, Toast.LENGTH_LONG);
			Intent i = new Intent(EnvironmentActivity.this, Home.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}

		@Override
		public void doTask() throws Exception {

			saveProxy();
			// Save off preferences
			SharedPreferences prefs = AppData.getSharedPreferences();
			prefsEditor = prefs.edit();
			Iterator<String> iterator = hmSelValues.keySet().iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String value = hmSelValues.get(key);
				BBYLog.v("==================>>>", key + "  : " + value);
				prefsEditor.putString(key, value);

			}
			prefsEditor.commit();
			AppConfig.getPreferenceValues();
			AppData.fetchGlobalConfig();
			AppConfig.printValues();

		}
	}

}