package com.bestbuy.android.pushnotifications.web.block;


import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EasySSLSocketFactory;

/**
 * Hitting the server to get the response data
 * @author lalitkumar_s
 */

public class WebAccessBlock {
	
	public static DefaultHttpClient httpclient;
	public static boolean useEasySSL = true;
	private static String  TAG = "WebAccessBlock*****************";
	
	/*
	 * Creating httpclient and request; Calls the WebBlock to fetch data;
	 * Process the response to convert into json then returns json string
	 */
	public static HttpResponse makeRequest(String url, List<NameValuePair> requestHeaders, int method) throws SocketTimeoutException, Exception {

		if (!AppData.enableConnectivity) {
			throw new SocketException("Network unreachable");
		}

		if (httpclient == null) {
			httpclient = getClient();
		}
		String requestUrl = url;
		HttpRequestBase base = null;

		if (method == APIRequest.DELETE) { // create a DELETE call
			base = new HttpDelete(requestUrl);
		} else if (method == APIRequest.GET) { // create a GET call
			try {
				base = new HttpGet(requestUrl);
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
			}
		}
		
		// Add necessary credentials with the request header
		if (requestHeaders != null) {
			Iterator<NameValuePair> iter = requestHeaders.iterator();
			while (iter.hasNext()) {
				NameValuePair nvp = (NameValuePair) iter.next();
				base.addHeader(nvp.getName(), nvp.getValue());
			}
		}

		if (method != APIRequest.GET) {
			if (requestHeaders != null) {
				Iterator<NameValuePair> iter = requestHeaders.iterator();
				while (iter.hasNext()) {
					NameValuePair nvp = (NameValuePair) iter.next();
					base.addHeader(nvp.getName(), nvp.getValue());
				}
			}
		}
				
		Diagnostics.StartMethodTracing(AppData.getContext());
		if (AppConfig.isGZIP() && base != null) {
			base.addHeader("Accept-Encoding", "gzip");
		}
		
		// Calling Web Access Block to hit the server
		return httpclient.execute(base);
	}

	/*
	 * Creating a httpclient to make web service call
	 */
	public static DefaultHttpClient getClient() {
		if (httpclient == null) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setUseExpectContinue(params, false);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "utf-8");

			params.setBooleanParameter("http.protocol.expect-continue", false);

			HttpConnectionParams.setConnectionTimeout(params, 15000);
			HttpConnectionParams.setSoTimeout(params, 15000);
			HttpConnectionParams.setSocketBufferSize(params, 8192);

			// registers schemes for both http and https
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			if (useEasySSL) {
				registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
			} else {
				registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			}

			ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);

			httpclient = new DefaultHttpClient(manager, params);

			httpclient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					return 50;
				}
			});

			if (AppConfig.isProxy()) {
				final HttpHost PROXY_HOST = new HttpHost(AppData.getProxyServer(), AppData.PROXY_PORT);
				httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, PROXY_HOST);
				httpclient.getCredentialsProvider().setCredentials(new AuthScope(AppData.getProxyServer(), AppData.PROXY_PORT, AuthScope.ANY_SCHEME), new NTCredentials(AppData.getProxyUsername(), AppData.getProxyPassword(), "test", AppData.getProxyDomain()));
			}

			httpclient.addRequestInterceptor(analyticAuth);

		}
		return httpclient;
	}
	
	static HttpRequestInterceptor analyticAuth = new HttpRequestInterceptor() {
		public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
			request.addHeader("BB301-DEVICE", AppConfig.getDevice());
			request.addHeader("BB301-UUID", AppConfig.getUUID());
			request.addHeader("BB301-PLATFORM", AppConfig.getPlatform());
			request.addHeader("BB301-APP-NAME", AppConfig.getAppName());
			request.addHeader("BB301-CARRIER", AppConfig.getCarrier());
			if (AppData.getLocation() != null) {
				request.addHeader("BB301-LAT", String.valueOf(AppData.getLocation().getLatitude()));
				request.addHeader("BB301-LONG", String.valueOf(AppData.getLocation().getLongitude()));
			}
		}
	};
}
