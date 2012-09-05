package com.bestbuy.android.storeevent.activity.helper.route;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RoadRouteProvider {

	public static RouteRoad getRoute(InputStream is) throws Exception {
		KMLParser handler = new KMLParser();
		
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		if(is != null)
			parser.parse(is, handler);
		
		return handler.mRoad;
	}
}