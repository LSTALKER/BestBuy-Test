package com.bestbuy.android.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.bestbuy.android.data.commerce.CError;

public class CErrorCodesHelper {

	private static Map<String, CErrorDesc> errorCodes;
	
	private static String TAG = "CErrorCodesHelper";
	
	public static void loadErrorCodes(String jsonString) throws Exception {
		
		errorCodes = new HashMap<String, CErrorDesc>();
		
		JSONObject errorObject = new JSONObject(jsonString);
		JSONArray errors = errorObject.getJSONArray("errors");
		for (int i = 0; i < errors.length(); i++) {
			JSONObject jsonErrorObj = errors.getJSONObject(i);
			String name = jsonErrorObj.optString("name").toUpperCase();
			byte type = (byte)jsonErrorObj.optInt("type");
			String shortDesc = jsonErrorObj.optString("short_desc");
			String desc = jsonErrorObj.optString("desc");
			errorCodes.put(name, new CErrorDesc(type,shortDesc,desc));
		}
	}
	
	public static String getDesc(String key) {
		return errorCodes.get(key).getDesc();
	}
	
	public static byte getType(String key){
		return errorCodes.get(key).getType();
	}
	
	public static String getShortDesc(String key){
		return errorCodes.get(key).getShortDesc();
	}
	
	public static boolean keyFound(String key){
		return errorCodes.get(key) != null;
	}
	
	public static List<CError> parseAndGetValue(String errorString) {
		//System.out.println("Input String: " + errorString);
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			SAXParser sp = spf.newSAXParser();
			XMLReader xmlReader = sp.getXMLReader();
			CErrorsParser cErrorsParser = new CErrorsParser();
			xmlReader.setContentHandler(cErrorsParser);
			xmlReader.parse(new InputSource(new StringReader(errorString)));
			return cErrorsParser.cErrors;
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception getting SAX Parser: " + ex.getMessage());
		}
		return null;
	}
	
	private static class CErrorDesc{
		private byte type;
		private String shortDesc;
		private String desc;
		
		public CErrorDesc(byte type,String shortDesc, String desc){
			this.type= type;
			this.shortDesc = shortDesc;
			this.desc = desc;
		}

		public byte getType() {
			return type;
		}

		public void setType(byte type) {
			this.type = type;
		}

		public String getShortDesc() {
			return shortDesc;
		}

		public void setShortDesc(String shortDesc) {
			this.shortDesc = shortDesc;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	
		
		
	}
	
	public static class CErrorsParser extends DefaultHandler {
		boolean inElement = false;
		String value;
		String errorText;
		List<CError> cErrors;
		
		public CErrorsParser() {
			cErrors = new ArrayList<CError>();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			inElement = true;
			value = new String();
			CError cError = new CError();
			if (localName.equals("error")) {
				String codeValue = attributes.getValue("code").toUpperCase();
				cError.setCode(codeValue);
				if(keyFound(codeValue)){
					cError.setType(getType(codeValue));
					cError.setShortMessage(getShortDesc(codeValue));
					cError.setMessage(getDesc(codeValue));
				}
				cErrors.add(cError);
			}
			if (localName.equals("message")&& cErrors.get(cErrors.size()-1) != null) {
				if (cErrors.get(cErrors.size()-1).getMessage() == null) {
					// set the <message> as the value
					cErrors.get(cErrors.size()-1).setShortMessage("Error Occurred");
					cErrors.get(cErrors.size()-1).setMessage(value);
				}
			}
			
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			if (localName.equals("message") && cErrors.get(cErrors.size()-1) != null) {
				if (cErrors.get(cErrors.size()-1).getMessage() == null || cErrors.get(cErrors.size()-1).getMessage().equals("")) {
					// set the <message> as the value
					int htmlStart = value.indexOf("<!--");
					int htmlStop = -1;
					if (value.lastIndexOf("-->") > 0) {
					 htmlStop = value.lastIndexOf("-->") + 2;
					}
				
					StringBuilder buffer = new StringBuilder();
					for (int i=0;i<value.length();i++) {
					  char c=value.charAt(i);
					  if (i < htmlStart || i > htmlStop) { 
						  // add the char
						  buffer.append(c);
					  }
					}
					cErrors.get(cErrors.size()-1).setMessage(buffer.toString());
				}
			}
			inElement = false;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (inElement) { //only grab the characters if in and element
				if (length > 0) {
					if (value != null) { //might be called a couple of times
						value = value.concat(new String(ch.clone(), start, length)); // append
					} else {
						value = new String(ch.clone(), start, length); // new item
					}
				}
			}
		}
	}
}
