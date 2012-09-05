package com.bestbuy.android.util;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * This Class is used if Server is accessible or not. Using pinging base URL
 * @author Kavya Soni
 * @Edited Lalit Kumar Sahoo
 */

public class AuthServer {
	private final static String TAG = "AuthServer";

	public static boolean authanticateMDotServer() throws Exception {
		return authanticateServer(AppConfig.getMdotURL(), false);
	}

	public static boolean authanticateMDotSignInServer(String url) throws Exception {
		return authanticateServer(url, true);
	}
	
	public static boolean authanticateAppCenterServer(String url) throws Exception {
		return authanticateServer(url, false);
	}
	
	public static boolean authanticateServer(String domain, boolean isSSL)
			throws Exception {
		int responseCode = 0;

		URL url = new URL(domain);
		if (isSSL) {
			if (android.os.Build.VERSION.RELEASE.contains("2.2")
					|| android.os.Build.VERSION.RELEASE.contains("2.1")) {
				boolean flag = APIRequest.ping(domain);
				if (flag)
					responseCode = HttpStatus.SC_OK;
			} else {
				HttpURLConnection httpsURLConnection = (HttpURLConnection) url
						.openConnection();
				httpsURLConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				httpsURLConnection.setRequestProperty("Content-Language",
						"en-US");
				httpsURLConnection.connect();
				responseCode = httpsURLConnection.getResponseCode();
			}

		} else {
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("Content-Language", "en-US");
			httpURLConnection.connect();
			responseCode = httpURLConnection.getResponseCode();
		}

		if (responseCode == HttpStatus.SC_OK) // 200
			return true;
		else if (responseCode == HttpStatus.SC_MOVED_TEMPORARILY) // 302
			return true;

		return false;
	}
}
