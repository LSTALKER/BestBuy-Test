package com.bestbuy.android.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


/**
 * IQEngines Java API
 * 
 * @author Vincent Garrigues
 *
 */
public class IQEApi implements Serializable {
	private final String TAG = this.getClass().getName();
	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructor
	 * 
	 * @param key
	 *            A non-<code>null</code> {@link String} : Your API key.
	 * @param secret
	 *            A non-<code>null</code> {@link String} : Your API secret.
	 */
	public IQEApi(String key, String secret) {
		this.key = key;
		this.secret = secret;
	}
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Upload an image to the IQ Engines' server.
	 * 
	 * @param image
	 *            A non-<code>null</code> {@link File} : The image file you
	 *            would like to get labels for. The image's height and width
	 *            should be less than 640 pixels.
	 * @return A non-<code>null</code> {@link IQEQuery}
	 */
	public IQEQuery query(File image) {
		return query(image, null, null, null, null, null);
	}
	
	/**
	 * Upload an image to the IQ Engines' server.
	 * 
	 * @param image
	 *            A non-<code>null</code> {@link File} : The image file you
	 *            would like to get labels for. The image's height and width
	 *            should be less than 640 pixels.
	 * @param webhook
	 *            A possibly-<code>null</code> {@link String} : The URL where
	 *            the results are sent via HTTP POST once the labels have been
	 *            computed.
	 * @param device_id
	 *            A possibly-<code>null</code> {@link String} : The unique
	 *            identification of the device that is querying the API.
	 * @param gps_altitude
	 *            A possibly-<code>null</code> {@link String} : The altitude of
	 *            your GPS coordinates.
	 * @param gps_longitude
	 *            A possibly-<code>null</code> {@link String} : The longitude of
	 *            your GPS coordinates.
	 * @param gps_latitude
	 *            A possibly-<code>null</code> {@link String} : The latitude of
	 *            your GPS coordinates.
	 * @return A non-<code>null</code> {@link IQEQuery}
	 */
	public IQEQuery query(File image, String webhook, String device_id,
			String gps_altitude, String gps_longitude,
			String gps_latitude) {
		TreeMap<String, String> fields = new TreeMap<String, String>();
		
		// Optional parameters
		if (webhook != null)
			fields.put("webhook", webhook);
		if (device_id != null)
			fields.put("device_id", device_id);
		if (gps_altitude != null)
			fields.put("gps_altitude", gps_altitude);
		if (gps_longitude != null)
			fields.put("gps_longitude", gps_longitude);
		if (gps_latitude != null)
				fields.put("gps_latitude", gps_latitude);
		
		// Required parameters
		fields.put("img", image.getPath());
		fields.put("time_stamp", now());
		fields.put("api_key", key);
		fields.put("api_sig", buildSignature(fields));
		
		return new IQEQuery(post(IQESelector.query, fields), fields.get("api_sig"));
	}
	
	/**
	 * The Update API is a long polling request to our server that returns a
	 * list of qids along with the labels that have been successfully processed
	 * by our image labeling engine. The Update API times out after 90 seconds.
	 * 
	 * @return A non-<code>null</code> {@link String}
	 */
	public String update() {
		return update(null);
	}
	
	/**
	 * The Update API is a long polling request to our server that returns a
	 * list of qids along with the labels that have been successfully processed
	 * by our image labeling engine. The Update API times out after 90 seconds.
	 * 
	 * @param device_id
	 *            A possibly-<code>null</code> {@link String} : The unique
	 *            identification of the device that is querying the API. If you
	 *            are using the API on multiple mobile devices, you should pass
	 *            the device_id as a parameter to the Query API and Update API.
	 *            This ensures that the Update API returns only results
	 *            corresponding to image queries sent by the device.
	 * @return A non-<code>null</code> {@link String}
	 */
	public String update(String device_id) {
		TreeMap<String, String> fields = new TreeMap<String, String>();
		
		// Optional parameter
		if (device_id != null)
			fields.put("device_id", device_id);
		
		// Required parameters
		fields.put("time_stamp", now());
		fields.put("api_key", key);
		fields.put("api_sig", buildSignature(fields));
		
		return post(IQESelector.update, fields);
	}
	
	/**
	 * The Result API is used to retrieve the labels
	 * 
	 * @param qid
	 *            A non-<code>null</code> {@link String} : The unique identifier
	 *            of the image for which you want to retrieve the results.
	 * @return A non-<code>null</code> {@link String}
	 */
	public String result(String qid) {
		TreeMap<String, String> fields = new TreeMap<String, String>();
		
		// Required parameters
		fields.put("time_stamp", now());
		fields.put("api_key", key);
		fields.put("qid", qid);
		fields.put("api_sig", buildSignature(fields));
		
		return post(IQESelector.result, fields);
	}
	
	/*
	 * PRIVATE METHODS
	 */
	
	/**
	 * Returns the current time stamp using the following formatting :
	 * "YYYYmmDDHHMMSS"
	 * 
	 * @return a non null {@link String}
	 */
	private String now() {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String date_format = "yyyyMMddkkmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(date_format);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(c.getTime());
	}
	
	/**
	 * Computes the signature of the API call
	 * 
	 * @param fields
	 *            A non-<code>null</code> {@link TreeMap} that contains the
	 *            arguments of the request.
	 * @return A non-<code>null</code> {@link String} : The message
	 *         authentication code
	 */
	private String buildSignature(TreeMap<String, String> fields) {
		// join key value pairs together
		String result = null;
		String raw_string = "";
		Iterator<String> i = fields.keySet().iterator();
		while (i.hasNext()) {
			String key = i.next();
			String value = fields.get(key);
			if (key.equals("img")) {
				// if the argument is an image, only keep the name of the file
				File image = new File(value);
				raw_string += "img" + image.getName();
			} else {
				raw_string += key + value;
			}
		}
		try {
			String HMAC_SHA1_ALGORITHM = "HmacSHA1";
			byte[] secret_bytes = secret.getBytes();			
			SecretKeySpec secret_key = new SecretKeySpec(secret_bytes, HMAC_SHA1_ALGORITHM);
			
			Mac m = Mac.getInstance("HmacSHA1");
			m.init(secret_key);
			byte[] signature_raw = m.doFinal(raw_string.getBytes());
			
			// Convert raw bytes to Hex
			byte[] signature_hex = new Hex().encode(signature_raw);
			
			//  Covert array of Hex bytes to a String
			result = new String(signature_hex, "ISO-8859-1");
		} catch (NoSuchAlgorithmException e) {
			BBYLog.printStackTrace(TAG, e);
			return "";
		} catch (InvalidKeyException e) {
			BBYLog.printStackTrace(TAG, e);
			return "";
		} catch (UnsupportedEncodingException e) {
			BBYLog.printStackTrace(TAG, e);
			return "";
		}
		return result;
	}
	
	/**
	 * Post fields and files to an http host as multipart/form-data.
	 * 
	 * @param selector
	 *            A non-<code>null</code> {@link IQESelector} : The type of post
	 *            message.
	 * @param fields
	 *            A non-<code>null</code> {@link TreeMap} : The fields
	 * @return A non-<code>null</code> {@link String} : The server's response.
	 */
	private String post(IQESelector selector, TreeMap<String, String> fields) {
		String result = "error";
		//String url = "http://api.iqengines.com/v1.2/"+selector+"/";
		String url = AppConfig.getBBYIQEnginesURL()+selector+"/";
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpClient client = new DefaultHttpClient(params);
		
		HttpPost httppost = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity();
		
		try {
			Iterator<String> i = fields.keySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				if (key.equals("img")) {
					File img = new File(fields.get(key));
					entity.addPart(key, new FileBody(img));
				} else {
					entity.addPart(key, new StringBody(fields.get(key)));
				}
			}
			httppost.setEntity(entity);
	        BBYLog.e(TAG, "POSSIBLE URI: " + httppost.getURI());
			
			HttpResponse response = client.execute(httppost);
	        HttpEntity resEntity = response.getEntity();

	        if (resEntity != null) {
	            result = EntityUtils.toString(resEntity);
	            resEntity.consumeContent();
	        }
	        
		} catch (UnsupportedEncodingException e) {
			BBYLog.printStackTrace(TAG, e);
			result = "Exception : UnsupportedEncodingException";
		} catch (IOException e) {
			BBYLog.printStackTrace(TAG, e);
			result = "Exception : IOException";
		}
		return result;
	}
	
	/**
	 * This class is used to store the results of the Query API : The unique
	 * identifier of the image and the server's response.
	 * 
	 * @author Vincent Garrigues
	 */
	public class IQEQuery implements Serializable {
		/**
		 * Creates a new {@link IQEQuery}.
		 * 
		 * @param result A non-<code>null</code> {@link String}.
		 * @param qid A non-<code>null</code> {@link String}.
		 */
		public IQEQuery(String result, String qid) {
			this.result = result;
			this.qid = qid;
		}
		
		/**
		 * The server's response.
		 * 
		 * @return A non-<code>null</code> {@link String}.
		 */
		public String getResult() {
			return result;
		}
		
		/**
		 * The unique identifier of the image for which you want to retrieve the
		 * results.
		 * 
		 * @return A non-<code>null</code> {@link String}.
		 */
		public String getQID() {
			return qid;
		}
		
		
		/** The result */
		private String result;
		/** The query id */
		private String qid;
		/** Generated serial id */
		private static final long serialVersionUID = 1930709349669617215L;
	}
	
	/**
	 * An enumeration for the different urls.
	 * 
	 * @author Vincent Garrigues
	 */
	public enum IQESelector {
		query,
		update,
		result
	}
	
	/** Your API key */
	private final String key;
	
	/** Your API secret */
	private final String secret;
	
	/** Generated serial id */
	private static final long serialVersionUID = -8870882783562183990L;

}
