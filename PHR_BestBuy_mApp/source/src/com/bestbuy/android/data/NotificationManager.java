package com.bestbuy.android.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZCertificate;
import com.bestbuy.android.rewardzone.RZParser;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheManager;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.InputStreamExtensions;

public class NotificationManager {
	
	private String TAG = this.getClass().getName();

	private Vector<Notification> notifications;
	private List<String> previousNotifications;
	private List<Offer> offers;
	private Date lastNotificationRetrievedDate;
	private AppData appData;

	public NotificationManager(AppData appData) {
		this.appData = appData;
		notifications = new Vector<Notification>();
	}

	public Vector<Notification> getNotifications(){
		return notifications;
	}

	public int getNewNotificationCount() {
		List<String> previous = getPreviousNotifications();
		int count = 0;
		for (int i = 0; i < notifications.size(); i++) {
			if (!previous.contains(String.valueOf(notifications.get(i).getHash()))) {
				count++;
			}
		}
		return count;
	}

	public void loadNotifications() throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, - 15);

		Date fifteenMin = calendar.getTime();
		if(lastNotificationRetrievedDate == null || lastNotificationRetrievedDate.before(fifteenMin)|| appData.isChangesInPnPreferences()){
			notifications = new Vector<Notification>();
			try{
				loadNotificationAlerts();
			}catch(Exception e){}
			try{
				loadNotificationOffers();
			}catch(Exception e){}
			try{
				loadNotificationCerts();
			}catch(Exception e){}
		}
		sortNotifications();
	}
	
	private void loadNotificationAlerts() throws Exception{
		
		final Location location = appData.getBBYLocationManager().getLocationManager(AppData.getContext())
		.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location != null) {
			System.out.println("Using Location for WS: [" + location.getLatitude() + ", " + location.getLongitude());
		} else {
			System.out.println("Location never updated");
		}

		double lat = 0;
		double lng = 0;
		if (location != null) {
			AppData.setLocation(location); //set in the app data.
			lat = location.getLatitude();
			lng = location.getLongitude();
		}

			final Map<String, String> urlParams = new HashMap<String, String>();
			urlParams.put("api_key", AppConfig.getSmalApiKey());
			urlParams.put("lat", String.valueOf(lat));
			urlParams.put("lng", String.valueOf(lng));

			final String result = APIRequest.makeGetRequest(AppConfig.getSmalHost(), AppData._301_ALERTS_PATH, urlParams, false, false);
			final JSONObject alerts = new JSONObject(result);
			final JSONArray alertsArray = alerts.getJSONArray("alerts");
			final int size = alertsArray.length();

			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			for (int i = 0; i < size; i++) {

				final JSONObject alertObjectItem = (JSONObject) alertsArray.get(i);
				final JSONObject alertObject = alertObjectItem.getJSONObject("alert");

				final String id = alertObject.getString("id"); // get the id of
																// the message

				final Date startDate = df.parse(alertObject.getString("start_date"));
				final Date endDate = df.parse(alertObject.getString("end_date"));
				final Date createdAt = df.parse(alertObject.getString("created_at"));
				final Date updatedAt = df.parse(alertObject.getString("updated_at"));
				@SuppressWarnings("unused")
				final Date currentDate = Calendar.getInstance().getTime();

				final String jsonMessageIDS = AppData.getSharedPreferences().getString(AppData.DISMISSED_ALERT_MESSAGE_IDS, null);

				boolean found = false;

				if (jsonMessageIDS != null) {
					final JSONArray messageIDS = new JSONArray(jsonMessageIDS);
					for (int k = 0; k < messageIDS.length(); k++) { // look
																	// through
																	// previously
																	// viewed
																	// messages
						final JSONObject obj = messageIDS.getJSONObject(k);
						final String viewedId = obj.getString("id");
						if (viewedId.equals(id)) { // if found, don't add to the
													// list of alerts to show
							found = true;
						}
					}
				}

				if (!found && (endDate == null || endDate.after(currentDate))) {
					final String title = alertObject.getString("title");
					final String body = alertObject.getString("body");
					final String url = alertObject.getString("url");
					final boolean global = Boolean.valueOf(alertObject.getString("global")).booleanValue();

					String listImageUrl = "";
					String displayImageUrl = "";
					try {
						JSONObject imagesObject = alertObject.getJSONObject("image_urls");
						listImageUrl = imagesObject.optString("list");
						displayImageUrl = imagesObject.optString("display");
					} catch (Exception e) {
						BBYLog.e(TAG, "no image urls");
					}

					final BestBuyAlert bestbuyAlert = new BestBuyAlert(id, startDate, endDate, title, body, url, listImageUrl, displayImageUrl, global, createdAt, updatedAt);

					// get the stores.
					final JSONArray storesJSONArray = alertObjectItem.getJSONArray("stores");
					for (int j = 0; j < storesJSONArray.length(); j++) {
						final JSONObject storeJSONObject = storesJSONArray.getJSONObject(j);
						final Store store = new Store();
						store.loadStoreData(storeJSONObject);
						bestbuyAlert.addStore(store);
					}

					addNotification(Notification.notificationWithType(Notification.NotificationTypeAlert,bestbuyAlert));
				}
			}

	}

	private void loadNotificationOffers() throws Exception {
		SearchRequest searchRequest = new SearchRequest();
		String channelKey = AppData.BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL;
		offers = searchRequest.getOffers(channelKey);
		while (searchRequest.getCursor() != null) {// && offers.size() < 30
			offers.addAll(searchRequest.getOffers(channelKey));
		}
		pruneNotifications();
	}

	public void pruneNotifications() {
		if (offers == null) {
			return;
		}
		// Prune out notifications that are not in the preferred categories
		for (int i = 0; i < offers.size(); i++) {//Math.min(30,offers.size())
			final Notification offerNotification = Notification.notificationWithType(Notification.NotificationTypeOffer, offers.get(i));
			//if (!notifications.contains(offerNotification)) {
				notifications.add(offerNotification);
			//}
		}
		List<String> preferredIds = appData.getPreferedCategories();
		if (preferredIds != null) {
			for (Offer offer : offers) {
				List<String> preferredIdsCopy = new ArrayList<String>();
				preferredIdsCopy.addAll(preferredIds);
				if (offer.getCategoryPathKeys() != null) {
					preferredIdsCopy.retainAll(offer.getCategoryPathKeys());
				}
				if (preferredIdsCopy.isEmpty()) {
					notifications.remove(Notification.notificationWithType(Notification.NotificationTypeOffer, offer));
				}
			}
		}
		offers.clear();
	}

	private void loadNotificationCerts() throws Exception {
		if (appData.getOAuthAccessor() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, +30);
			Date future = calendar.getTime();
			DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);

			ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
			OAuthClient oclient = new OAuthClient(new HttpClient4());

			String url = AppConfig.getSecureRemixURL() + AppData.REWARD_ZONE_DATA_PATH;

			RZParser rzParser = new RZParser();
			String data = CacheManager.getCacheItem(url, CacheManager.RZ_CACHE);

			if (data == null || data.length() <= 0) {
				BBYLog.i(TAG, "Doing WS, caching Data");
				CacheManager.clearCache(CacheManager.RZ_CACHE);
				OAuthMessage msg = oclient.invoke(appData.getOAuthAccessor(), url, params1);
				InputStream bodyInputStream = msg.getBodyAsStream();
				data = InputStreamExtensions.InputStreamToString(bodyInputStream);
				CacheManager.setCacheItem(url, data, CacheManager.RZ_CACHE);
			} else {
				BBYLog.i(TAG, "Using Cached Data");
			}

			ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(data.getBytes());

			rzParser.parse(Diagnostics.dumpInputStream(AppData.getContext(), bodyConvertInputStream, TAG, "Reward Zone XML: "));

			if (appData.getRzAccount().getRzTransactions().size() == 0) {

				RZAccount rzAccount = rzParser.getRzAccount();
				appData.setRzAccount(rzParser.getRzAccount());

				Map<String, String> omnitureParams = new HashMap<String, String>();
				omnitureParams.put("rz_id", rzAccount.getId());
				omnitureParams.put("rz_tier", rzAccount.getStatusDisplay());
				EventsLogging.fireAndForget(EventsLogging.RZ_LOGIN_SUCCESS, omnitureParams);

			}

			RZAccount account = appData.getRzAccount();
			for (int j = 0; j < account.getAvailableCertificates().size(); j++) {
				RZCertificate cert = account.getAvailableCertificates().get(j);
				System.out.println("Expirate Date: " + df.format(cert.getExpiredDate()) + " Future: " + df.format(future));
				
				if(cert.getExpiredDate().before(future)){
					addNotification(Notification.notificationWithType(Notification.NotificationTypeRZCert, cert,j));
				}

			}

		}
	}

	public void sortNotifications() {
		Collections.sort(notifications, new Comparator<Notification>() {
			public int compare(Notification e1, Notification e2) {
				List<String> previous = getPreviousNotifications();
				if (previous.contains(String.valueOf(e1.getHash())) && !previous.contains(String.valueOf(e2.getHash()))) {
					return 1;
				} else if (!previous.contains(String.valueOf(e1.getHash())) && previous.contains(String.valueOf(e2.getHash()))) {
					return -1;
				}
				return 0;
			}
		});
	}
	
	public void addNotification(Notification notification) {
		if (!notifications.contains(notification)) {
			notifications.add(notification);
		}
	}
	
	public void saveNotificationHash(){
		SharedPreferences settings = AppData.getContext().getSharedPreferences(AppData.SHARED_PREFS, 0);
		String result = "";
		for (int i = 0; i < notifications.size(); i++) {
			if (i > 0) {
				result += " ";
			}
			result += notifications.get(i).getHash();
		}
		settings.edit().putString(AppData.PREVIOUS_NOTIFICATIONS, result).commit();
		previousNotifications = createPreviousList(AppData.getContext());
	}

	private List<String> createPreviousList(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String recentSkus = settings.getString(AppData.PREVIOUS_NOTIFICATIONS, "");
		if (recentSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.asList(recentSkus.split(" ")));
	}

	private List<String> getPreviousNotifications() {
		if (previousNotifications == null) {
			previousNotifications = createPreviousList(AppData.getContext());
		}
		return previousNotifications;
	}

	public boolean isNew(Notification notification) {
		return !getPreviousNotifications().contains(String.valueOf(notification.getHash()));
	}

	public void setNew(Notification notification) {
		getPreviousNotifications().add(String.valueOf(notification.getHash()));
	}

	public Date getLastNotificationRetrievedDate() {
		return lastNotificationRetrievedDate;
	}

	public void setLastNotificationRetrievedDate(Date newDate) {
		this.lastNotificationRetrievedDate = newDate;
	}
	
	public String getNotificationAlertString(){
		return "You have " + getNewNotificationCount() + " new notification" + ((getNewNotificationCount()>1)?"s":"") + ".";
	}
	
	public void removeRZAlerts(){
		Vector<Notification> removeRZAlertsList = new Vector<Notification>();
		if(notifications.size() > 0){
			for(Notification notification : notifications){
				if(notification.getNotificationType() == Notification.NotificationTypeRZCert)
					removeRZAlertsList.add(notification);
			}
			for (int i = 0; i < removeRZAlertsList.size(); i++) {
				notifications.remove(removeRZAlertsList.get(i));
			}
		}
	}
}	
/*
	private String TAG = this.getClass().getName();

	private Vector<Notification> notifications;
	private List<String> previousNotifications;
	private List<Offer> offers;
	private Date lastNotificationRetrievedDate;
	private AppData appData;

	public NotificationManager(AppData appData) {
		this.appData = appData;
		notifications = new Vector<Notification>();
	}

	public Vector<Notification> getNotifications(){
		return notifications;
	}

	public int getNewNotificationCount() {
		List<String> previous = getPreviousNotifications();
		int count = 0;
		for (int i = 0; i < notifications.size(); i++) {
			if (!previous.contains(String.valueOf(notifications.get(i).getHash()))) {
				count++;
			}
		}
		return count;
	}

	public void loadNotifications() throws Exception {

		if (notifications == null)
			notifications = new Vector<Notification>();

		try{
			loadNotificationAlerts();
			loadNotificationOffers();
			loadNotificationCerts();
			sortNotifications();
		}catch(Exception ex){
			BBYLog.printStackTrace(TAG, ex);
		}
	}
	
	private void loadNotificationAlerts() throws Exception{
		
		final Location location = appData.getBBYLocationManager().getLocationManager(AppData.getContext())
		.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (location != null) {
			System.out.println("Using Location for WS: [" + location.getLatitude() + ", " + location.getLongitude());
		} else {
			System.out.println("Location never updated");
		}

		double lat = 0;
		double lng = 0;
		if (location != null) {
			AppData.setLocation(location); //set in the app data.
			lat = location.getLatitude();
			lng = location.getLongitude();
		}

			final Map<String, String> urlParams = new HashMap<String, String>();
			urlParams.put("api_key", AppConfig.getSmalApiKey());
			urlParams.put("lat", String.valueOf(lat));
			urlParams.put("lng", String.valueOf(lng));

			final String result = APIRequest.makeGetRequest(AppConfig.getSmalHost(), AppData._301_ALERTS_PATH, urlParams, false, false);
			final JSONObject alerts = new JSONObject(result);
			final JSONArray alertsArray = alerts.getJSONArray("alerts");
			final int size = alertsArray.length();

			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			for (int i = 0; i < size; i++) {

				final JSONObject alertObjectItem = (JSONObject) alertsArray.get(i);
				final JSONObject alertObject = alertObjectItem.getJSONObject("alert");

				final String id = alertObject.getString("id"); // get the id of
																// the message

				final Date startDate = df.parse(alertObject.getString("start_date"));
				final Date endDate = df.parse(alertObject.getString("end_date"));
				final Date createdAt = df.parse(alertObject.getString("created_at"));
				final Date updatedAt = df.parse(alertObject.getString("updated_at"));
				final Date currentDate = Calendar.getInstance().getTime();

				final String jsonMessageIDS = AppData.getSharedPreferences().getString(AppData.DISMISSED_ALERT_MESSAGE_IDS, null);

				boolean found = false;

				if (jsonMessageIDS != null) {
					final JSONArray messageIDS = new JSONArray(jsonMessageIDS);
					for (int k = 0; k < messageIDS.length(); k++) { // look
																	// through
																	// previously
																	// viewed
																	// messages
						final JSONObject obj = messageIDS.getJSONObject(k);
						final String viewedId = obj.getString("id");
						if (viewedId.equals(id)) { // if found, don't add to the
													// list of alerts to show
							found = true;
						}
					}
				}

				if (!found && (endDate == null || endDate.after(currentDate))) {
					final String title = alertObject.getString("title");
					final String body = alertObject.getString("body");
					final String url = alertObject.getString("url");
					final boolean global = Boolean.valueOf(alertObject.getString("global")).booleanValue();

					String listImageUrl = "";
					String displayImageUrl = "";
					try {
						JSONObject imagesObject = alertObject.getJSONObject("image_urls");
						listImageUrl = imagesObject.optString("list");
						displayImageUrl = imagesObject.optString("display");
					} catch (Exception e) {
						BBYLog.e(TAG, "no image urls");
					}

					final BestBuyAlert bestbuyAlert = new BestBuyAlert(id, startDate, endDate, title, body, url, listImageUrl, displayImageUrl, global, createdAt, updatedAt);

					// get the stores.
					final JSONArray storesJSONArray = alertObjectItem.getJSONArray("stores");
					for (int j = 0; j < storesJSONArray.length(); j++) {
						final JSONObject storeJSONObject = storesJSONArray.getJSONObject(j);
						final Store store = new Store();
						store.loadStoreData(storeJSONObject);
						bestbuyAlert.addStore(store);
					}
					addNotification(Notification.notificationWithType(Notification.NotificationTypeAlert,bestbuyAlert));
				}
			}

	}

	private void loadNotificationOffers() throws Exception {
		if(CategoryUtilities.getPreferredCategoryIds().size() != 0){
			SearchRequest searchRequest = new SearchRequest();
			String channelKey = AppData.BBY_OFFERS_MOBILE_SPECIAL_OFFERS_CHANNEL;
			offers = searchRequest.getOffers(channelKey);
			while (searchRequest.getCursor() != null) {// && offers.size() < 30
				offers.addAll(searchRequest.getOffers(channelKey));
			}
			pruneNotifications();
		}
	}

	public void pruneNotifications() {
		if (offers == null) {
			return;
		}
		// Prune out notifications that are not in the preferred categories
		for (int i = 0; i < offers.size(); i++) {//Math.min(30,offers.size())
			final Notification offerNotification = Notification.notificationWithType(Notification.NotificationTypeOffer, offers.get(i));
			//if (!notifications.contains(offerNotification)) {
				//notifications.add(offerNotification);
			//}
			addNotification(offerNotification);
		}
		List<String> preferredIds = CategoryUtilities.getPreferredCategoryIds();
		if (preferredIds != null) {
			for (Offer offer : offers) {
				List<String> preferredIdsCopy = new ArrayList<String>();
				preferredIdsCopy.addAll(preferredIds);
				if (offer.getCategoryPathKeys() != null) {
					preferredIdsCopy.retainAll(offer.getCategoryPathKeys());
				}
				if (preferredIdsCopy.isEmpty()) {
					notifications.remove(Notification.notificationWithType(Notification.NotificationTypeOffer, offer));
				}
			}
		}
	}

	private void loadNotificationCerts() throws Exception {
		if (appData.getOAuthAccessor() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_YEAR, +30);
			Date future = calendar.getTime();

			ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
			OAuthClient oclient = new OAuthClient(new HttpClient4());

			String url = AppConfig.getSecureRemixURL() + AppData.REWARD_ZONE_DATA_PATH;

			RZParser rzParser = new RZParser();
			String data = CacheManager.getCacheItem(url, CacheManager.RZ_CACHE);

			if (data == null || data.length() <= 0) {
				BBYLog.i(TAG, "Doing WS, caching Data");
				CacheManager.clearCache(CacheManager.RZ_CACHE);
				OAuthMessage msg = oclient.invoke(appData.getOAuthAccessor(), url, params1);
				InputStream bodyInputStream = msg.getBodyAsStream();
				data = InputStreamExtensions.InputStreamToString(bodyInputStream);
				CacheManager.setCacheItem(url, data, CacheManager.RZ_CACHE);
			} else {
				BBYLog.i(TAG, "Using Cached Data");
			}

			ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(data.getBytes());

			rzParser.parse(Diagnostics.dumpInputStream(AppData.getContext(), bodyConvertInputStream, TAG, "Reward Zone XML: "));

			if (appData.getRzAccount().getRzTransactions().size() == 0) {

				RZAccount rzAccount = rzParser.getRzAccount();
				appData.setRzAccount(rzParser.getRzAccount());

				Map<String, String> omnitureParams = new HashMap<String, String>();
				omnitureParams.put("rz_id", rzAccount.getId());
				omnitureParams.put("rz_tier", rzAccount.getStatusDisplay());
				EventsLogging.fireAndForget(EventsLogging.RZ_LOGIN_SUCCESS, omnitureParams);

			}

			RZAccount account = appData.getRzAccount();
			for (int j = 0; j < account.getAvailableCertificates().size(); j++) {
				RZCertificate cert = account.getAvailableCertificates().get(j);
				//System.out.println("Expirate Date: " + df.format(cert.getExpiredDate()) + " Future: " + df.format(future));
				
				if(cert.getExpiredDate().before(future)){
					addNotification(Notification.notificationWithType(Notification.NotificationTypeRZCert, cert,j));
				}
			}

		}
	}

	public void sortNotifications() {
		Collections.sort(notifications, new Comparator<Notification>() {
			public int compare(Notification e1, Notification e2) {
				List<String> previous = getPreviousNotifications();
				if (previous.contains(String.valueOf(e1.getHash())) && !previous.contains(String.valueOf(e2.getHash()))) {
					return 1;
				} else if (!previous.contains(String.valueOf(e1.getHash())) && previous.contains(String.valueOf(e2.getHash()))) {
					return -1;
				}
				return 0;
			}
		});

	}
	
	public void addNotification(Notification notification) {
		if (!notifications.contains(notification)) {
			notifications.add(notification);
		}
	}
	
	public void removeRZAlerts(){
		Vector<Notification> removeRZAlertsList = new Vector<Notification>();
		if(notifications.size() > 0){
			for(Notification notification : notifications){
				if(notification.getNotificationType() == Notification.NotificationTypeRZCert)
					removeRZAlertsList.add(notification);
			}
			for (int i = 0; i < removeRZAlertsList.size(); i++) {
				notifications.remove(removeRZAlertsList.get(i));
			}
		}
	}
	
	public void saveNotificationHash(){
		SharedPreferences settings = AppData.getContext().getSharedPreferences(AppData.SHARED_PREFS, 0);
		StringBuilder resultStrBuilder = new StringBuilder();
		for (int i = 0; i < notifications.size(); i++) {
			if (i > 0) {
				resultStrBuilder.append(" ");
			}
			resultStrBuilder.append(String.valueOf(notifications.get(i).getHash()));
		}
		settings.edit().putString(AppData.PREVIOUS_NOTIFICATIONS, resultStrBuilder.toString()).commit();
		previousNotifications = createPreviousList(AppData.getContext());
	}

	private List<String> createPreviousList(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String recentSkus = settings.getString(AppData.PREVIOUS_NOTIFICATIONS, "");
		if (recentSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(Arrays.asList(recentSkus.split(" ")));
	}

	private List<String> getPreviousNotifications() {
		if (previousNotifications == null || previousNotifications.size() == 0) {
			previousNotifications = createPreviousList(AppData.getContext());
		}
		return previousNotifications;
	}

	public boolean isNew(Notification notification) {
		return !getPreviousNotifications().contains(String.valueOf(notification.getHash()));
	}

	public void setNew(Notification notification) {
		getPreviousNotifications().add(String.valueOf(notification.getHash()));
	}

	public Date getLastNotificationRetrievedDate() {
		return lastNotificationRetrievedDate;
	}

	public void setLastNotificationRetrievedDate(Date newDate) {
		this.lastNotificationRetrievedDate = newDate;
	}
	
	public String getNotificationAlertString(){
		return "You have " + getNewNotificationCount() + " new notification" + ((getNewNotificationCount()>1)?"s":"") + ".";
	}
}
*/
