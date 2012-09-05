package com.bestbuy.android.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.SavedBitmap;

public class ShareUtils {

	private static final String TAG = "FacebookUtil";

	public static final String TEMP_SKU = "TEMP_DIRECTORY";

	public static String getBitlyUrl(String url) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		String encodedUrl = URLEncoder.encode(url);
		params.put("login", AppData.BITLY_USER);
		params.put("apiKey", AppData.BITLY_KEY);
		params.put("longUrl", encodedUrl);
		params.put("format", "txt");
		String result = APIRequest.makeGetRequest(AppData.BITLY_URL, AppData.BITLY_PATH, params, false);
		return result;
	}

	/*public static void shareSavedBitmap(Context context, SavedBitmap sb, String subject) {
		shareSavedBitmap(context, sb, subject, null);
	}*/

	/*public static void shareSavedBitmap(Context context, SavedBitmap sb, String subject, String text) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);

		Uri uri = ImageProvider.cacheBitmapToTempStore(sb.getB());

		intent.setType("image/jpg");

		String title = subject;

		if (text != null && title != null && text.length() > 0) {
			title = title + " - " + text;
		}

		intent.putExtra(Intent.EXTRA_TITLE, title);
		intent.putExtra(Intent.EXTRA_STREAM, uri);

		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		try {
			context.startActivity(Intent.createChooser(intent, "Share"));
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			Toast.makeText(context, "Unable to share!", Toast.LENGTH_LONG).show();

		}
	}*/

	public static SavedBitmap generateSavedBitmap(Bitmap b) {
		SavedBitmap sb = null;

		try {
			sb = new SavedBitmap();
			sb.setB(b);
			sb.setUriPath(ImageProvider.cacheBitmapToSD(TEMP_SKU, "", b, true));
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}

		return sb;
	}

	public static void share(Context context, String subject, String text) {
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");				
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, "Share"));
	}

}
