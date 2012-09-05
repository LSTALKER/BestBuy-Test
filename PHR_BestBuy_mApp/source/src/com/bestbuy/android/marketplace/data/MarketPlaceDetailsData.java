package com.bestbuy.android.marketplace.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.marketplace.library.parser.MarketPlaceDataParser;
import com.bestbuy.android.marketplace.web.layer.WebAccessCaller;
import com.bestbuy.android.util.APIRequestException;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;

/**
 * This class is responsible to hit the web services and after getting the response parse 
 * to corresponding java object.
 * @author lalitkumar_s
 */

public class MarketPlaceDetailsData {
	private static String TAG = "MarketPlaceDetailsData**************";
	
	public static MarketPlaceDetails getMarketPlaceDetails(String skuId, String productId) throws APIRequestException, SocketTimeoutException, Exception {
		String results = null;
		
		try {
			String host = AppConfig.getMdotMainURL();
			String path = "m/r/pdp/pdpservice.jsp";
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("skuId", skuId);
			params.put("pid", productId);
			
			results = WebAccessCaller.makeGetRequest(host, path, params, null);
			
		} catch (APIRequestException apiEx) {
			BBYLog.printStackTrace(TAG, apiEx);
			throw apiEx;
		} catch (SocketTimeoutException sEx) {
			BBYLog.printStackTrace(TAG, sEx);
			throw sEx;
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			throw ex;
		}
		
		if(results == null) {
			//results = "<product><productattribute name=\"modelNumber\" value=\"N4B2ND4\"/><productattribute name=\"skuShortLabel\" value=\"LG - LG N4B2ND4 Server Short Name\"/><productattribute name=\"skuId\" value=\"3914779\"/><productattribute name=\"numberOfReviews\" value=\"0\"/><productattribute name=\"price\" value=\"60.0\"/><productattribute name=\"productId\" value=\"1218340378789\"/><productattribute name=\"ratings\" value=\"2.0\"/><productattribute name=\"supplierSellerId\" value=\"1218340404367\"/><productattribute name=\"isMarketPlaceSku\" value=\"true\"/><productattribute name=\"sellerDispName\" value=\"Adorama Camera\"/><productattribute name=\"displayName\" value=\"LG N4B2ND4 Server Short Name\"/></product>";						
			return null;
		}
		
		return parseFromXML(results);
	}
	
	private static MarketPlaceDetails parseFromXML(String results) {
		InputStream inputStream = new ByteArrayInputStream(results.getBytes());
		
		MarketPlaceDataParser parser = new MarketPlaceDataParser();
		return parser.parse(inputStream);
	}
}
