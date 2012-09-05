package com.bestbuy.android.openbox.data;

import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import com.bestbuy.android.openbox.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequestException;

public class GetProductStream {

	public static InputStream getInputStream(String urlString) throws SocketTimeoutException, ConnectTimeoutException, APIRequestException, Exception {				
		return WebAccessCaller.makeGetRequest(urlString);
	}
}
