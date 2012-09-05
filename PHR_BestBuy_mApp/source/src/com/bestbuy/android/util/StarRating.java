package com.bestbuy.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bestbuy.android.R;

/**
 * Includes methods for returning a star bitmap according to the product rating
 * 
 * Also statically caches star rating images to avoid redundant decoding and memory allocations
 * 
 * @author Recursive Awesome
 * 
 */
public class StarRating {

	@SuppressWarnings("unused")
	private static String TAG = "StarRating";

	private static Object mutex = new Object();
	private static Bitmap starCache[] = null;

	public static Bitmap getAssociatedStarImage(String rating, Context context) {
		float reviewAverageFloat = 0;

		if(rating != null && !rating.equalsIgnoreCase(""))
		{			
			reviewAverageFloat = Float.parseFloat(rating);
		}

		
		if (starCache == null) {
				loadStarCache(context);
			}
			if (reviewAverageFloat == 0) {
				return starCache[0];
			} else if (reviewAverageFloat > 0 && reviewAverageFloat <= .5) {
				return starCache[1];
			} else if (reviewAverageFloat > 0 && reviewAverageFloat <= 1.0) {
				return starCache[2];
			} else if (reviewAverageFloat <= 1.5) {
				return starCache[3];
			} else if (reviewAverageFloat <= 2.0) {
				return starCache[4];
			} else if (reviewAverageFloat <= 2.5) {
				return starCache[5];
			} else if (reviewAverageFloat <= 3.0) {
				return starCache[6];
			} else if (reviewAverageFloat <= 3.5) {
				return starCache[7];
			} else if (reviewAverageFloat <= 4.0) {
				return starCache[8];
			} else if (reviewAverageFloat <= 4.5) {
				return starCache[9];
			} else {
				return starCache[10];
			}
		}

	//TODO: is there a more efficient way to build these images? We have about 40KB worth of star drawables.
	private static void loadStarCache(Context context) {
		starCache = new Bitmap[11];
		starCache[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars0);
		starCache[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.halfstars);
		starCache[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars1);
		starCache[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.halfstars1);
		starCache[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars2);
		starCache[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.halfstars2);
		starCache[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars3);
		starCache[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.halfstars3);
		starCache[8] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars4);
		starCache[9] = BitmapFactory.decodeResource(context.getResources(), R.drawable.halfstars4);
		starCache[10] = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars5);
	}

	public static void release() {
		synchronized (mutex) {
			if (starCache != null) {
				for (int i = 0; i < starCache.length; i++) {
					starCache[i].recycle();
					starCache[i] = null;
				}
			}
			starCache = null;
		}
	}
}
