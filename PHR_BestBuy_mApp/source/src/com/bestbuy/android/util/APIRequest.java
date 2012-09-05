package com.bestbuy.android.util;

import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;


import com.bestbuy.android.data.AppData;

/**
 * Allows for making GET and POST requests to a server
 * 
 * @author Recursive Awesome
 * 
 */
public class APIRequest {

	private static String TAG = "RAAPIRequest";
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DELETE = 4;

	public final static int DEFAULT_CONNECTION_TIMEOUT = 15000;
	public final static int DEFAULT_SO_TIMEOUT = 15000;
	
	public static DefaultHttpClient httpclient;

	public static Credentials credentials;
	public static AuthScope authScope;
	private static HttpResponse response;

	public static boolean useEasySSL = true;

	public static boolean ping(String host) {
		BBYLog.d(TAG, "ping() - Log Request: " + host);
		try {
			final URLConnection conn = new URL(host).openConnection();
			conn.connect();
			return true;
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception connecting to host: " + host + " : " + ex.getMessage());
			return false;
		}
	}

	public static DefaultHttpClient getClient() {
		if (httpclient == null) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setUseExpectContinue(params, false);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "utf-8");

			params.setBooleanParameter("http.protocol.expect-continue", false);

			// HttpConnectionParams.setTcpNoDelay(params, true);
			// Ryan TODO this is why I think it was infinitely hanging?!
			HttpConnectionParams.setConnectionTimeout(params, DEFAULT_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, DEFAULT_SO_TIMEOUT);
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
				httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
				final HttpHost PROXY_HOST = new HttpHost(AppData.getProxyServer(), AppData.PROXY_PORT);
				httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, PROXY_HOST);
				httpclient.getCredentialsProvider().setCredentials(
						new AuthScope(AppData.getProxyServer(), AppData.PROXY_PORT, AuthScope.ANY_SCHEME),
						new NTCredentials(AppData.getProxyUsername(), AppData.getProxyPassword(), "test", AppData.getProxyDomain()));
			}

		}
		return httpclient;
	}

	public static String makeGetRequest(String host, String path, Map<String, String> parameters, boolean useAuth, boolean usesCache)
			throws Exception {
		return makeGetRequest(host, path, parameters, useAuth, usesCache, CacheManager.GENERAL_CACHE);
	}

	public static String makeGetRequest(String host, String path, Map<String, String> parameters, boolean useAuth) throws Exception {
		return makeGetRequest(host, path, parameters, useAuth, false, CacheManager.GENERAL_CACHE);
	}

	//CHANGED BY ME START
	public static String makeGetRequest(String host, String path, Map<String, String> parameters, List<NameValuePair> requestHeaders, boolean useAuth) throws Exception {
		return makeGetRequest(host, path, parameters, requestHeaders, useAuth, false, CacheManager.GENERAL_CACHE, true);
	}
	
	private static String makeGetRequest(String host, String path, Map<String, String> parameters, List<NameValuePair> requestHeaders, boolean useAuth, boolean usesCache,
			String cacheTitle, boolean isHeaderRequired) throws Exception {

		String url;
		if (path != null) {
			url = host + "/" + path;
		} else {
			url = host;
		}
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		if (parameters != null) {
			Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				valuePairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
			}
		}
		return makeAutherizationRequest(url, valuePairs, requestHeaders, null, APIRequest.GET, usesCache, cacheTitle, isHeaderRequired);
	}
	
	//CHANGED BY ME END
	
	public static String makeGetRequest(String host, String path, Map<String, String> parameters, boolean useAuth, boolean usesCache,
			String cacheTitle) throws Exception {

		String url;
		if (path != null) {
			url = host + "/" + path;
		} else {
			url = host;
		}
		List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		if (parameters != null) {
			Iterator<Map.Entry<String, String>> iter = parameters.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pair = iter.next();
				valuePairs.add(new BasicNameValuePair(pair.getKey(), pair.getValue()));
			}
		}
		return makeRequest(url, valuePairs, null, null, APIRequest.GET, usesCache, cacheTitle, false);
	}

	public static String makeGetRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders, String requestBody)
			throws Exception {
		return makeRequest(url, valuePairs, requestHeaders, requestBody, APIRequest.GET, false, CacheManager.GENERAL_CACHE, false);
	}

	public static String makePostRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders, String requestBody)
			throws Exception {
		return makeRequest(url, valuePairs, requestHeaders, requestBody, APIRequest.POST, false, CacheManager.GENERAL_CACHE, false);
	}

	public static String makePutRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders, String requestBody)
			throws Exception {
		return makeRequest(url, valuePairs, requestHeaders, requestBody, APIRequest.PUT, false, CacheManager.GENERAL_CACHE, false);

	}

	public static String makeDeleteRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders,
			String requestBody) throws Exception {
		return makeRequest(url, valuePairs, requestHeaders, requestBody, APIRequest.DELETE, false, CacheManager.GENERAL_CACHE, false);
	}

	public static String makeAutherizationRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders, String requestBody,
			int method, boolean cacheable, String cacheTitle, boolean isHeaderRequired) throws Exception {
		
		return makeRequest(url, valuePairs,requestHeaders, requestBody, method, cacheable, cacheTitle, isHeaderRequired);
	}
	
	private static String makeRequest(String url, List<NameValuePair> valuePairs, List<NameValuePair> requestHeaders, String requestBody,
			int method, boolean cacheable, String cacheTitle, boolean isHeaderRequired) throws Exception {

		if (!AppData.enableConnectivity) {
			// throw new APIRequestException(null);
			throw new SocketException("Network unreachable");
		}

		if (httpclient == null) {
			httpclient = getClient();
		}

		String responseBody = null;
		String requestUrl = url;

		HttpRequestBase base = null;

		if (method == APIRequest.POST || method == APIRequest.PUT) {

			if (method == APIRequest.POST) {
				base = new HttpPost(requestUrl);
			} else if (method == APIRequest.PUT) {
				base = new HttpPut(requestUrl);
			}

			if (valuePairs != null) {
				((HttpEntityEnclosingRequestBase) base).setEntity(new UrlEncodedFormEntity(valuePairs));
			}

			if (requestBody != null) {
				StringEntity se = new StringEntity(requestBody, "UTF-8");
				se.setContentType("application/xml");
				((HttpEntityEnclosingRequestBase) base).setEntity(se);
			}

		} else if (method == APIRequest.DELETE) { // create a DELETE call
			base = new HttpDelete(requestUrl);
		} else if (method == APIRequest.GET) { // create a GET call
			try {
				StringBuffer query = new StringBuffer();
				if (valuePairs != null) {
					Iterator<NameValuePair> getParamsIter = valuePairs.iterator();
					while (getParamsIter.hasNext()) {
						NameValuePair pair = (NameValuePair) getParamsIter.next();
						String name = pair.getName().replace(" ", "%20").replace("&", "+");
						String value = pair.getValue().replace(" ", "%20");
						if(!name.equals("sort"))
							value = value.replace("&", "+");
						query.append(name + "=" + value);
						if (getParamsIter.hasNext()) {
							query.append("&");
						}
					}
				}
				@SuppressWarnings("unused")
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				requestUrl = requestUrl.replace(" ", "%20");

				if (query.length() > 0) {
					requestUrl = URLManager.createQueryString(requestUrl, valuePairs);
					//BBYLog.i("Reviews URL=====>", ""+requestUrl);
				}

				if (cacheable) {
					String data = CacheManager.getCacheItem(requestUrl, cacheTitle);
					if (data != null && data.length() > 0) {
						BBYLog.i(TAG, "Using cache for: " + requestUrl);
						BBYLog.i(TAG, "Cached Data: " + data);
						return data;
					}
				}
				BBYLog.i("Bestbuy URL====================>", requestUrl);
				base = new HttpGet(requestUrl);
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
			}
		}

		if(isHeaderRequired) {
			if (requestHeaders != null) {
				Iterator<NameValuePair> iter = requestHeaders.iterator();
				while (iter.hasNext()) {
					NameValuePair nvp = (NameValuePair) iter.next();
					base.addHeader(nvp.getName(), nvp.getValue());
				}
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

		String methodName;
		switch (method) {
		case GET:
			methodName = "GET";
			break;
		case POST:
			methodName = "POST";
			break;
		case PUT:
			methodName = "PUT";
			break;
		case DELETE:
			methodName = "DELETE";
			break;
		default:
			methodName = "";
			break;
		}
		BBYLog.d(TAG, "APIRequest:makeRequest(): METHOD: " + methodName + " URL: " + requestUrl + " VALUE-PAIRS: " + valuePairs);
		if (requestBody != null) {
			BBYLog.d(TAG, "APIRequest:requestBody: " + requestBody);
		}
		Diagnostics.StartMethodTracing(AppData.getContext());
		if (AppConfig.isGZIP() && base != null) {
			base.addHeader("Accept-Encoding", "gzip");
		}
		response = httpclient.execute(base);
		if(response != null){
			int statusCode = response.getStatusLine().getStatusCode();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			HttpEntity responseEntity = response.getEntity();
	
			if (response.getStatusLine() != null) {
				BBYLog.v(TAG, "Status Message: " + response.getStatusLine().getReasonPhrase());
			} else {
				BBYLog.v(TAG, "Status or response was null");
			}
			BBYLog.d("APIRequest*******************", statusCode+"");
			
			if (statusCode >= 400) {
				BBYLog.d("APIRequest*****Entwring**************","solve");
				APIRequestException apiEx = new APIRequestException(response);
				responseBody = apiEx.getResponseBody();
				BBYLog.d(TAG, "APIRequest:makeRequest() - Response: " + responseBody);
				throw apiEx;
			}
	
			if (responseEntity != null) {
				InputStream instream = responseEntity.getContent();
	
				if (AppConfig.isGZIP() && contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					BBYLog.i(TAG, "!!!!!!!!!!!!!!!!!!! Used GZIP !!!!!!!!!!!!!!!!!!!!!!!");
					instream = new GZIPInputStream(instream);
				}
	
				responseBody = InputStreamExtensions.InputStreamToString(instream); // EntityUtils.toString(response.getEntity());
	
				if (cacheable && responseBody != null && responseBody.length() > 0) {
					BBYLog.i(TAG, "Setting cache for: " + requestUrl);
					CacheManager.setCacheItem(requestUrl, responseBody, cacheTitle);
				}
			}
	
			BBYLog.d(TAG, "APIRequest:makeRequest() - Response: " + responseBody);
		}
		return responseBody;
	}
	
	public static String makeJSONPostRequest(String host, String path, Map<String, String> params) throws Exception {
		return makeJSONPostRequest(host, path, params, null);
	}


	public static String makeJSONPostRequest(String host, String path, Map<String, String> params, Map<String, String> headers) throws Exception {
		if (httpclient == null) {
			httpclient = getClient();
		}
		String response = null;
		String requestUrl = host;

		if (path != null) {
			requestUrl = host + "/" + path;
		}

		HttpPost httpPost = new HttpPost(requestUrl);
		JSONObject holder = new JSONObject();

		if (params != null) {
			Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
				String key = (String) pairs.getKey();
				String value = (String) pairs.getValue();
				holder.put(key, value);
			}
		}
		
		BBYLog.d(TAG, "APIRequest:makeJSONPostRequest() - Request: " + requestUrl + " VALUE-PAIRS " + holder.toString());
		StringEntity se = new StringEntity(holder.toString());
		httpPost.setEntity(se);

		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		if (headers != null) {
			Iterator<Map.Entry<String, String>> iter = headers.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
				String key = (String) pairs.getKey();
				String value = (String) pairs.getValue();
				httpPost.setHeader(key, value);
			}
		}

		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		response = httpclient.execute(httpPost, responseHandler);

		BBYLog.d(TAG, "APIRequest:makeJSONPostRequest() - Response: " + response);

		return response;
		
	}
	
	/*
	 * Creating httpclient and request; Calls the WebBlock to fetch data;
	 * Process the response to convert into json then returns json string
	 */
	public static HttpResponse makeRequest(String url, int method) throws SocketTimeoutException, ConnectTimeoutException, Exception {

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
				
		Diagnostics.StartMethodTracing(AppData.getContext());
		if (AppConfig.isGZIP() && base != null) {
			base.addHeader("Accept-Encoding", "gzip");
		}
		
		// Calling Web Access Block to hit the server
		return httpclient.execute(base);
	}
	
	/*
	 * Creating httpclient and request; Calls the WebBlock to fetch data;
	 * Process the response to convert into json then returns json string
	 */
	public static HttpResponse makeRequest(String url, List<NameValuePair> requestHeaders, int method) throws SocketTimeoutException, ConnectTimeoutException, Exception {

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
	 * Creating httpclient and request; Calls the WebBlock to fetch data;
	 * Process the response to convert into json then returns json string
	 */
	public static HttpResponse makeRequest(String url, List<NameValuePair> requestHeaders, int method, String requestBody) throws SocketTimeoutException, Exception {

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
		if (requestBody != null) {
			HttpParams params = new  BasicHttpParams();
			params.setParameter("param0", requestBody);
			base.setParams(params);
		}
		/*if (requestBody != null) {
			StringEntity se = new StringEntity(requestBody, "UTF-8");
			se.setContentType("application/xml");			
			((HttpEntityEnclosingRequestBase) base).setEntity(se);
		}	*/
		
		Diagnostics.StartMethodTracing(AppData.getContext());
		if (AppConfig.isGZIP() && base != null) {
			base.addHeader("Accept-Encoding", "gzip");
		}
		
		// Calling Web Access Block to hit the server
		return httpclient.execute(base);
	}
	
	/*
	 * Creating httpclient and request; Calls the WebBlock to fetch data;
	 * Process the response to convert into json then returns json string
	 */
	public static HttpResponse makeRequest(String url,
			List<NameValuePair> requestHeaders, String requestBody, int method)
			throws SocketTimeoutException,HttpHostConnectException, Exception {
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
			String userName = requestHeaders.get(0).getValue();
			String password = requestHeaders.get(1).getValue();
			Credentials credentials = new UsernamePasswordCredentials(userName, password);
			((DefaultHttpClient) httpclient).getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
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

	public static HttpResponse getResponse() {
		return response;
	}

	/*
	 * static HttpRequestInterceptor preemptiveAuth = new
	 * HttpRequestInterceptor() { public void process(final HttpRequest request,
	 * final HttpContext context) throws HttpException, IOException { AuthState
	 * authState = (AuthState)
	 * context.getAttribute(ClientContext.TARGET_AUTH_STATE);
	 * CredentialsProvider credsProvider = (CredentialsProvider)
	 * context.getAttribute( ClientContext.CREDS_PROVIDER); HttpHost targetHost
	 * = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
	 * 
	 * if (authState.getAuthScheme() == null) { AuthScope authScope = new
	 * AuthScope(targetHost.getHostName(), targetHost.getPort()); Credentials
	 * creds = credsProvider.getCredentials(authScope); if (creds != null) {
	 * authState.setAuthScheme(new BasicScheme());
	 * authState.setCredentials(creds); } } } };
	 */
}
