package com.bestbuy.android.icr.util;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.BBYLog;

/**
 * This IcrAsyncTask to get responseString from webservice.
 * 
 * @author bharathkumar_s
 * 
 */

public class IcrAsyncTask extends AsyncTask<String, Integer, Boolean> {
	private String TAG = this.getClass().getName();
	private String resultString;

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			resultString = APIRequest.makeJSONPostRequest(
					AppData.BBY_ICR_PRICING_HOST, AppData.BBY_ICR_PRICING_PATH,
					null);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (result) {
			String jsonResponse = refactorResponse(resultString);
			try {
				JSONArray array = new JSONArray(jsonResponse);
				IcrUtil.putJSonArray(array);
			} catch (JSONException e) {
				BBYLog.printStackTrace(TAG, e);
			}
		}
	}

	/**
	 * by using this method i am getting jsonArray format form resultString.
	 * 
	 * @param source
	 * @return JSON array response from source(resultString).
	 */
	private String refactorResponse(String source) {
		if (source == null)
			return "";

		if (source.length() == 0)
			return "";

		String refactored = "";
		int firstIndex = firstIndexOf(source, "[");
		int lastIndex = source.length() - 2;

		if (firstIndex == -1)
			return "";

		refactored = source.substring(firstIndex, lastIndex);

		return refactored;
	}

	/**
	 * by using this method ,getting the first index(where the jsonArray format
	 * starts).
	 * 
	 * @param source
	 * @param searchChars
	 * @return if source consists -> "[" ->return index of that char..
	 *         else,return -1.
	 */
	public static int firstIndexOf(String source, String searchChars) {
		String charAtIndex;
		for (int i = 0; i < source.length(); i++) {
			charAtIndex = String.valueOf(source.charAt(i));

			if (charAtIndex.contains(searchChars))
				return i;
		}
		return -1;
	}
}