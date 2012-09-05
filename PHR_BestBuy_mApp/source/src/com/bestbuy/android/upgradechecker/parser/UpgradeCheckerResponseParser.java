package com.bestbuy.android.upgradechecker.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class UpgradeCheckerResponseParser {
	
	public static HashMap parseEligibilityCheckResponse(String response) throws XmlPullParserException, IOException {
		String[] skipList = new String[] {"envelope","body"};
		HashMap results = new HashMap();
		HashMap subscriber = null;
		ArrayList<HashMap> subscribers = new ArrayList<HashMap>();
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		String tagName = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = xpp.getName();
				if(tagName.equals("subscribers")){
					subscriber = new HashMap();
				}
			} else if(eventType == XmlPullParser.END_TAG) {
				if(xpp.getName().equals("subscribers")){
					subscribers.add(subscriber);
					subscriber = null;
				}				
			} else if (eventType == XmlPullParser.TEXT) {				
				String tagText = xpp.getText();
				if (!(Arrays.asList(skipList).contains(tagText.toLowerCase()))) {
					if(subscriber != null) {
						subscriber.put(tagName, xpp.getText());
					} else {
						results.put(tagName, xpp.getText());
					}					
				}				
			}
			eventType = xpp.next();
		}
		results.put("subscribers", subscribers);
		return results;
	}

	public static HashMap parsePutNotificationResponse(String response) throws XmlPullParserException, IOException {
		String[] skipList = new String[] {"envelope","body"};
		HashMap results = new HashMap();
		HashMap notificationStatus = null;		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		String tagName = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = xpp.getName();
				if(tagName.equals("notificationStatusPutResponse")){
					notificationStatus = new HashMap();
				}
			} else if (eventType == XmlPullParser.TEXT) {				
				String tagText = xpp.getText();
				if (!(Arrays.asList(skipList).contains(tagText.toLowerCase()))) {
					if(notificationStatus != null) {
						notificationStatus.put(tagName, xpp.getText());
					} else {
						results.put(tagName, xpp.getText());
					}					
				}				
			}
			eventType = xpp.next();
		}
		results.put("notificationStatus", notificationStatus);
		return results;
	}
	
	public static String parseGetNotificationResponse(String response) throws XmlPullParserException, IOException {
		String[] skipList = new String[] {"envelope","body"};	
		String result="";
		boolean flag=false;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		String tagName = null;

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				tagName = xpp.getName();
				if(tagName.equals("notificationStatusCode")){
					flag=true;
				}
			} else if (eventType == XmlPullParser.TEXT) {				
				String tagText = xpp.getText();
				if (!(Arrays.asList(skipList).contains(tagText.toLowerCase()))) {
					if(flag){
						result	=tagText;
					}
				}				
			}
			eventType = xpp.next();
		}
		
		return result;
	}
}
