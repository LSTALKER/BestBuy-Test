package com.bestbuy.android.storeevent.data;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.bestbuy.android.storeevent.activity.helper.route.RoadRouteProvider;
import com.bestbuy.android.storeevent.activity.helper.route.RouteRoad;
import com.bestbuy.android.util.AppConfig;

/**
 * This class is responsible to hit the web services and after getting the response parse 
 * to corresponding java object.
 * @author lalitkumar_s
 */

public class StoreMapData {
	
	public static RouteRoad getMapConnection(String from, String to) throws Exception {
		String url = getRouteMapUrl(from, to);
		URLConnection conn = new URL(url).openConnection();
		
		InputStream results = conn.getInputStream();
		
		if(results == null)
			return null;
		
		return parse(results);
	}
	
	private static RouteRoad parse(InputStream is) throws Exception {
		return RoadRouteProvider.getRoute(is);
	}
	
	/**
	 * Generating the route map url by using from and to field.
	 * @param from : from latitude and longitude
	 * @param to : to latitude and longitude
	 * @return : Formated URL string
	 */
	private static String getRouteMapUrl(String from, String to) {
		StringBuffer urlString = new StringBuffer();
		urlString.append(AppConfig.getStoreLocatorMapURL()+"?f=d&hl=en&dirflg=d");
		urlString.append("&saddr=");// from
		urlString.append(from);
		urlString.append("&daddr=");// to
		urlString.append(to);
		urlString.append("&ie=UTF8&0&om=0&output=kml");
		return urlString.toString();
	}
}
