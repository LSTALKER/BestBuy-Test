package com.bestbuy.android.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.pushnotifications.uapush.IntentReceiver;
import com.bestbuy.android.upgradechecker.data.Subscriber;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.ImageProvider;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;

/**
 * Creates an instance of the AppData class for use throughout the app.
 * @author Recursive Awesome
 * 
 */
public class BestBuyApplication extends Application {
	
	private static AppData appData;
	@SuppressWarnings("unused")
	private String TAG = getClass().getName();
	
	// Upgrade Checker Global Values
	
	private static boolean PRIVACY_MODE = false;
	private static boolean TEST_MODE = false;
	

	private int storeId = 0;
	private String devicePhoneNumber;
	private int carrierId;
	private String carrierCode;
	private String primaryPhoneNumber;
	private String zipCode;
	private String last4SSN;
	private String pin;
	private HashMap results;
	private List<Subscriber> subscribers;
	private String email;
	private String password;
	
	public void onCreate() {
		super.onCreate();
		appData = new AppData(this);
		ImageProvider.initialize(this);
		detectDevicePhoneNumber();
		pushNotificationBlock();
	} 
	
	public AppData getAppData() {
		return appData;
	}
	
	public static void showToastNotification(String message, Activity activity, int duration) {
		Context context = activity.getApplicationContext();
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}
	
	public boolean isPrivacyMode() {
		return PRIVACY_MODE && (carrierId == R.id.sprintBtn || carrierId == R.id.tmobileBtn);
	}

	public boolean isTestMode() {
		return TEST_MODE;
	}
	
	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setCarrierId(int carrierId) {
		this.carrierId = carrierId;
	}

	public int getCarrierId() {
		return carrierId;
	}

	public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
		this.primaryPhoneNumber = PhoneNumberUtils.stripSeparators(primaryPhoneNumber);
	}

	public String getPrimaryPhoneNumber() {
		return primaryPhoneNumber;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setLast4SSN(String last4SSN) {
		this.last4SSN = last4SSN;
	}

	public String getLast4SSN() {
		return last4SSN;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getPin() {
		return pin;
	}

	public void setResults(HashMap results) {
		this.results = results;
	}

	public HashMap getResults() {
		return results;
	}

	public void resetCurrentValues() {
		setCarrierId(0);
		setPrimaryPhoneNumber(null);
		setZipCode(null);
		setLast4SSN(null);
		setPin(null);
		setResults(null);
	}

	private void detectDevicePhoneNumber() {
		TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNumber = telephony.getLine1Number();		
		if (phoneNumber != null) {
			phoneNumber = PhoneNumberUtils.stripSeparators(phoneNumber);
			if (phoneNumber.length() == 11 && phoneNumber.startsWith("1")) {
				phoneNumber = phoneNumber.substring(1);
			}
			this.devicePhoneNumber = phoneNumber;
		}
	}

	public String getDevicePhoneNumber() {
        return devicePhoneNumber;
	}

	public boolean primaryNumberMatches(String phoneNumber) {
		return primaryPhoneNumber.equals(PhoneNumberUtils.stripSeparators(phoneNumber));
	}

	public boolean primaryNumberMatchesDeviceNumber() {
		return primaryNumberMatches(devicePhoneNumber);
	}
	
	public String getEligibilityOfferURL() {
		return getResources().getString(R.string.eligibilty_offer_url);
	}
	
	public List<Subscriber> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}
		
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public URL getUpgradeCheckerURL() throws MalformedURLException	{
		if(isTestMode())
			return new URL(AppConfig.getupgradeCheckerDemoURL());						
		else
			return new URL(AppConfig.getupgradeCheckerProdURL());
	}

	public void pushNotificationBlock() {

		AirshipConfigOptions options = AirshipConfigOptions
				.loadDefaultOptions(this);
		// Optionally, customize your config at runtime:
		options.inProduction = true;
		options.developmentAppKey = "Ac1v1aGARhC5rXjGgW2Gcg";
		options.developmentAppSecret = "leK9CMboS82e6nfzGCWB2w";

		UAirship.takeOff(this, options);
		PushManager.enablePush();

		CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();
		nb.statusBarIconDrawableId = R.drawable.ic_pn;// custom status bar icon
		nb.layout = R.layout.notification;
		nb.layoutIconDrawableId = R.drawable.ic_pn;// custom layout icon
		nb.layoutIconId = R.id.icon;
		nb.layoutSubjectId = R.id.subject;
		nb.layoutMessageId = R.id.message;

		PushManager.shared().setNotificationBuilder(nb);
		PushManager.shared().setIntentReceiver(IntentReceiver.class);

	}
}
