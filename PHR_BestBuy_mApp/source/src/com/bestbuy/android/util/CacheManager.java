package com.bestbuy.android.util;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.bestbuy.android.data.AppData;

public class CacheManager {
	private static final String TAG = "CacheManager";
	private static final String DATE_LOCATION_STRING = "_DATE";
	public static final String RZ_CACHE = "RZ_CACHE";
	public static final String GENERAL_CACHE = "GENERAL_CACHE";
	/* Cache set for one day */
	private static final long DEFAULT_EXPIRATION_MILLI = 1 * 24 * 60 * 60 * 1000;
	private static ThreadPoolExecutor executor;
	
	static {
		//Only use one thread for setting the cache.  Don't use more than one because we will get issues.
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	}
	
	//This method typically takes ~100ms to complete.
	public static String getCacheItem(String url, String cacheTitle) {
		CacheDBAdapter dbAdapter = AppData.getDBAdapter();
		Date now = new Date();
		String data = dbAdapter.getCacheData(cacheTitle, url);
		
		/* No data found return null */
		if (data == null || data.length() <= 0) {
			return null;
		}

		String expiration = dbAdapter.getCacheExpiration(cacheTitle, url);

		/* No expiration date found return null and erase data */
		if (expiration == null || expiration.length() <= 0) {
			dbAdapter.deleteCacheItem(cacheTitle, url);
			return null;
		}

		try {
			Date expirationDate = new Date(expiration);
			long diff = now.getTime() - expirationDate.getTime();

			/* Expired return null and erase cache date */
			if (diff > DEFAULT_EXPIRATION_MILLI) {
				dbAdapter.deleteCacheItem(cacheTitle, url);
				return null;
			}
		} catch (IllegalArgumentException e) {
			BBYLog.e(TAG, "Error parsing date: " + expiration);
			BBYLog.printStackTrace(TAG, e);
		}
		return data;
	}

	public static void clearCache(String cacheTitle) {
		CacheDBAdapter dbAdapter = AppData.getDBAdapter();
		boolean result = dbAdapter.deleteCacheTag(cacheTitle);
		BBYLog.d(TAG, "CLEARING RZ CACHE: " + result);
	}

	public static void clearCache() {
		clearCache(GENERAL_CACHE);
	}

	public static void setCacheItem(String url, String data) {
		setCacheItem(url, data, GENERAL_CACHE);
	}

	public static void setCacheItem(final String url, final String data, final String cacheTitle) {
		executor.execute(new SetCacheRunnable(url, data, cacheTitle));
	}
	
	private static class SetCacheRunnable implements Runnable {
		private String url;
		private String data;
		private String cacheTitle;

		public SetCacheRunnable(final String url, final String data, final String cacheTitle) {
			this.url = url;
			this.data = data;
			this.cacheTitle = cacheTitle;
		}

		public void run() {
			CacheDBAdapter dbAdapter = AppData.getDBAdapter();
			Date d = new Date();
			dbAdapter.setCacheData(cacheTitle, url, data, d.toString());
		}
	}
}
