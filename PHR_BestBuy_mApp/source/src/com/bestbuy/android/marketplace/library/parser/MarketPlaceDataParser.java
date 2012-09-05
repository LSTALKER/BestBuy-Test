package com.bestbuy.android.marketplace.library.parser;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.bestbuy.android.marketplace.library.dataobject.MarketPlaceDetails;
import com.bestbuy.android.util.BBYLog;

public class MarketPlaceDataParser {
	
	private final String TAG = this.getClass().getName();
	
	private MarketPlaceDetails marketPlaceDetails = null;
	
	private final String PRODUCT_ATTRIBUTE = "productattribute";
	private final String MODEL_NUMBER = "modelNumber";
	private final String SKU_SHORT_LABEL = "skuShortLabel";
	private final String SKU_ID = "skuId";
	private final String NUMBER_OF_REVIEWS = "numberOfReviews";
	private final String PRICE = "price";
	private final String PRODUCT_ID = "productId";
	private final String RATINGS = "ratings";
	private final String SUPPLIER_SELLER_ID = "supplierSellerId";
	private final String SMARKET_PLACE_SKU = "sMarketPlaceSku";
	private final String SELLER_DISP_NAME = "sellerDispName";
	private final String DISPLAY_NAME = "displayName";
	
	private final String ERROR_DESCRIPTION = "errordescription";
	
	public MarketPlaceDetails parse(InputStream inputStream) {
		
		String name = null;
		
		marketPlaceDetails = new MarketPlaceDetails();
		
		XmlPullParser parser= Xml.newPullParser();
		
		try{
			parser.setInput(inputStream,null);
			
			int eventType=parser.getEventType();
			
			boolean done = false;
			
			while(eventType!=XmlPullParser.END_DOCUMENT && !done) {
				switch(eventType) {
					
					case XmlPullParser.START_DOCUMENT:
						break;
						
					case XmlPullParser.START_TAG:										
						name = parser.getName();
						if (name.equals(PRODUCT_ATTRIBUTE))
							getAttributes(parser);
						else if(name.equals(ERROR_DESCRIPTION))
							getTagValue(parser);
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
		
		return marketPlaceDetails;
	}
	
	private void getTagValue(XmlPullParser parser) throws XmlPullParserException, IOException {
		marketPlaceDetails.setErrorDescription(parser.nextText());
	}

	private void getAttributes(XmlPullParser parser) {
		int attributeCount=parser.getAttributeCount();
		int NEXT_ATTR = 1;
		
		if (attributeCount > 0) {
			for (int i = 0; i < attributeCount; i++) {
				String propertyName = parser.getAttributeValue(i);
				if (propertyName.equals(MODEL_NUMBER)) {
					marketPlaceDetails.setModelNumber(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(SKU_SHORT_LABEL)) {
					marketPlaceDetails.setSkuShortLabel(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(SKU_ID)) {
					marketPlaceDetails.setSkuId(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(NUMBER_OF_REVIEWS)) {
					marketPlaceDetails.setNumberOfReviews(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(PRICE)) {
					marketPlaceDetails.setPrice(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(PRODUCT_ID)) {
					marketPlaceDetails.setProductId(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(RATINGS)) {
					marketPlaceDetails.setRatings(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(SUPPLIER_SELLER_ID)) {
					marketPlaceDetails.setSellerId(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(SMARKET_PLACE_SKU)) {
					boolean marketPlaceFlag;
					String booleanString = parser.getAttributeValue(NEXT_ATTR);
					if (booleanString.equals("true"))
						marketPlaceFlag = true;
					else
						marketPlaceFlag = false;
					marketPlaceDetails.setMarketPlaceSku(marketPlaceFlag);
					break;
				} else if (propertyName.equals(SELLER_DISP_NAME)) {
					marketPlaceDetails.setSellerDispName(parser.getAttributeValue(NEXT_ATTR));
					break;
				} else if (propertyName.equals(DISPLAY_NAME)) {
					marketPlaceDetails.setDisplayName(parser.getAttributeValue(NEXT_ATTR));
					break;
				}
			}
		}
	}
}
