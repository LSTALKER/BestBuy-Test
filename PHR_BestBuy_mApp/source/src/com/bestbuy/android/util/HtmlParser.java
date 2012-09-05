package com.bestbuy.android.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.UberCategory;

public class HtmlParser {
	
	private final static String TAG = "HtmlParser";
	private static final String OPTION = "option";
	private static final String VALUE = "value";
	
	public static void getConnection(String url) throws Exception {
		URLConnection conn = new URL(url).openConnection();
		conn.setRequestProperty("User-Agent","Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		
		InputStream is = conn.getInputStream();

		if (is != null) {

			String result = InputStreamExtensions.InputStreamToString(is);
		
			if(result != null) {
				List<UberCategory> uberCategoryList = HtmlParser.parseHtml(result);
				new ObjectStreamUtilities<List<UberCategory>>().writeObject(uberCategoryList, AppData.BBY_BROWSE_CATEGORY_FILE);
			}
		}
	}
	
	public static List<UberCategory> parseHtml(String source) {
		int startIndex = source.indexOf("<select");
		int endIndex = source.indexOf("</select>") + "</select>".length();
		String requiredStr = "";
		
		if(startIndex != -1 && endIndex != -1) {
			requiredStr = source.substring(startIndex, endIndex);
			requiredStr = requiredStr.replace("&sbquo;", ",");
		}
		
		//InputStream inputStream = InputStreamExtensions.stringToInputStream(requiredStr);
		
		return parseXML(requiredStr);
	}
	
	private static List<UberCategory> parseXML(String requiredStr) {
		List<UberCategory> result = new ArrayList<UberCategory>();
		UberCategory uberCategory = null;
		
		String name = null;
		
		XmlPullParser parser= Xml.newPullParser();
		
		try{
			parser.setInput(new StringReader(requiredStr));
			
			int eventType=parser.getEventType();
			
			boolean done = false;
			
			while(eventType!=XmlPullParser.END_DOCUMENT && !done) {
				name = parser.getName();
				
				switch(eventType) {
					
					case XmlPullParser.START_DOCUMENT:
						result = new ArrayList<UberCategory>();
						break;
						
					case XmlPullParser.START_TAG:										
						if(name.equals(OPTION)) {
							uberCategory = new UberCategory();
							uberCategory.setCategoryValue(parser.getAttributeValue(null, VALUE));
							uberCategory.setCategoryOption(parser.nextText());
							result.add(uberCategory);
						}
						break;
					
					case XmlPullParser.END_TAG:
						break;
						
					case XmlPullParser.END_DOCUMENT:
						done=true;
						break;
				}
				
				if(done)
					eventType = XmlPullParser.END_DOCUMENT;
				else
					eventType = parser.next();					
			}
		} catch(XmlPullParserException e){
			BBYLog.printStackTrace(TAG, e);
		} catch(IOException e){
			BBYLog.printStackTrace(TAG, e);
		} catch(Exception e){
			BBYLog.printStackTrace(TAG, e);
		}
		
		return result;
	}
}
