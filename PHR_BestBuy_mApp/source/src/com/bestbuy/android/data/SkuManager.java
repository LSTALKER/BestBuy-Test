package com.bestbuy.android.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class SkuManager {

	public static String getRecentSkus(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String recentSkus = settings.getString(AppData.RECENT_SKUS, "");
		return recentSkus;
	}
	
	public static List<String> getRecentSkusAsList(Context context){
		String recentSkus = getRecentSkus(context);
		if (recentSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(recentSkus.split(" "));
	}

	public static void removeFromRecentSkus(Context context, String sku) {
		String recentSkus = getRecentSkus(context);
		recentSkus = recentSkus.replace(sku, "");
		recentSkus = recentSkus.replace("  ", " ");
		recentSkus = recentSkus.trim();
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.RECENT_SKUS, recentSkus).commit();
	}

	public static String getScannedSkus(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String recentSkus = settings.getString(AppData.SCANNED_SKUS, "");
		return recentSkus;
	}
	
	public static List<String> getScannedSkusAsList(Context context) {
		String scannedSkus = getScannedSkus(context);
		if (scannedSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(scannedSkus.split(" "));
	}
	
	public static void removeFromScannedSkus(Context context, String sku) {
		String scannedSkus = getScannedSkus(context);
		scannedSkus = scannedSkus.replace(sku, "");
		scannedSkus = scannedSkus.replace("  ", " ");
		scannedSkus = scannedSkus.trim();
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.SCANNED_SKUS, scannedSkus).commit();
	}
	
	public static String getRatedSkus(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String ratedSkus = settings.getString(AppData.RATED_SKUS, "");
		return ratedSkus;
	}
	
	public static List<String> getRatedSkusAsList(Context context) {
		String ratedSkus = getRatedSkus(context);
		if (ratedSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(ratedSkus.split(" "));
	}
	
	public static void addToRatedSkus(Context context, String sku) {
		ArrayList<String> ratedSkus = new ArrayList<String>();
		ratedSkus.addAll(getRatedSkusAsList(context));
		if (!ratedSkus.contains(sku)) {
			ratedSkus.add(sku);
		}
		if (ratedSkus.size() == 50) {
			ratedSkus.remove(49);
		}
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.RATED_SKUS, serializeList(ratedSkus)).commit();
	}

	public static void addToRecentProducts(Context context, Product product) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String recentSkus = settings.getString(AppData.RECENT_SKUS, "");
		List<String> skuList = new ArrayList<String>();
		skuList.addAll(Arrays.asList(recentSkus.split(" ")));

		if (skuList.contains(product.getSku())) {
			// If the sku already exists, move it to the front
			skuList.remove(product.getSku());
		} else if (skuList.size() == 20) {
			// If there are 20 products, remove the last one to make room
			skuList.remove(19);
		}
		// Add the sku
		skuList.add(0, product.getSku());
		// Save the resulting string of skus in preferences
		settings.edit().putString(AppData.RECENT_SKUS, serializeList(skuList)).commit();
	}

	public static String getLikedSkus(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String likedSkus = settings.getString(AppData.LIKED_SKUS, "");
		return likedSkus;
	}
	
	public static List<String> getLikedSkusAsList(Context context) {
		String likedSkus = getLikedSkus(context);
		if (likedSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(likedSkus.split(" "));
	}
	
	public static void addToLikedSkus(Context context, String sku) {
		String likedSkus = getLikedSkus(context);
		if (!likedSkus.contains(sku)) {
			likedSkus += " " + sku;
			SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
			settings.edit().putString(AppData.LIKED_SKUS, likedSkus).commit();
		}
	}
	
	public static void removeFromLikedSkus(Context context, String sku) {
		String likedSkus = getLikedSkus(context);
		likedSkus = likedSkus.replace(sku, "");
		likedSkus = likedSkus.replace("  ", " ");
		likedSkus = likedSkus.trim();
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.LIKED_SKUS, likedSkus).commit();
	}
	
	public static String getCompareSkus(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String compareSkus = settings.getString(AppData.COMPARE_SKUS, "");
		return compareSkus;
	}
	
	public static List<String> getCompareSkusAsList(Context context) {
		String compareSkus = getCompareSkus(context);
		if (compareSkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(compareSkus.split(" "));
	}
	
	public static void addToCompareSkus(Context context, String sku) {
		ArrayList<String> compareSkus = new ArrayList<String>();
		compareSkus.addAll(getCompareSkusAsList(context));
		if (!compareSkus.contains(sku)) {
			compareSkus.add(sku);
			SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
			settings.edit().putString(AppData.COMPARE_SKUS, serializeList(compareSkus)).commit();
		}
	}
	
	public static void removeFromCompareSkus(Context context, String sku) {
		String compareSkus = getCompareSkus(context);
		compareSkus = compareSkus.replace(sku, "");
		compareSkus = compareSkus.replace("  ", " ");
		compareSkus = compareSkus.trim();
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.COMPARE_SKUS, compareSkus).commit();
	}
	
	private static String serializeList(List<String> list) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				result.append(" ");
			}
			result.append(list.get(i));
		}
		return result.toString();
	}
	
	public static String getGameLibrarySkus(Context context) {
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		String gameLibrarySkus = settings.getString(AppData.GAME_LIBRARY_SKUS, "");
		return gameLibrarySkus;
	}
	
	public static List<String> getGameLibrarySkusAsList(Context context) {
		String gameLibrarySkus = getGameLibrarySkus(context);
		if (gameLibrarySkus.equalsIgnoreCase("")) {
			return new ArrayList<String>();
		}
		return Arrays.asList(gameLibrarySkus.split(" "));
	}
	
	public static void addToGameLibrarySkus(Context context, String sku) {
		ArrayList<String> gameLibrarySkus = new ArrayList<String>();
		gameLibrarySkus.addAll(getGameLibrarySkusAsList(context));
		if (!gameLibrarySkus.contains(sku)) {
			gameLibrarySkus.add(sku);
		}
		if (gameLibrarySkus.size() == 50) {
			gameLibrarySkus.remove(49);
		}
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.GAME_LIBRARY_SKUS, serializeList(gameLibrarySkus)).commit();
	}
	
	public static void addToScannedSkus(Context context, String sku) {
		ArrayList<String> scannedSkus = new ArrayList<String>();
		scannedSkus.addAll(getScannedSkusAsList(context));
		if (!scannedSkus.contains(sku)) {
			scannedSkus.add(sku);
		}
		if (scannedSkus.size() == 50) {
			scannedSkus.remove(49);
		}
		SharedPreferences settings = context.getSharedPreferences(AppData.SHARED_PREFS, 0);
		settings.edit().putString(AppData.SCANNED_SKUS, serializeList(scannedSkus)).commit();
	}
	
}
