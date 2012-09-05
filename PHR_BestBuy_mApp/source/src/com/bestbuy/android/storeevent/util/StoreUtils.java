package com.bestbuy.android.storeevent.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.bestbuy.android.util.BBYLog;

/**
 * Utility class to handle all common functionalities.
 * @author lalitkumar_s
 */
public class StoreUtils {
	
	private static final String TAG = "StoreUtils.Java";
	
	public static final String EVENTS_INFO = "EVENTS_INFO";
	public static final String STORE_INFO = "STORE_INFO";
	public static final String OPENBOX_FLAG = "OPENBOX_FLAG";
	public static final String STORE_IMAGE = "STORE_IMAGE";
	public static final String FROM_SOURCE = "FROM_SOURCE";
	public static final String STORE_ID = "STORE_ID";
	public static final String STORE_NAME = "STORE_NAME";
	public static final String OPENBOX_ITEMS = "Open Box Items";
	public static final String CLEARANCE_ITEMS = "Clearance Items";
	public static final String IS_OPENBOX = "IS_OPENBOX";
	public static final String LIST_OF_SKU = "LIST_OF_SKU";
	public static final String PRODUCT_TYPE = "PRODUCT_TYPE";
	public static final String PRICE_MAP = "PRICE_MAP";
	public static final String CATEGORY_ID = "CATEGORY_ID";
	public static final String CATEGORY_NAME = "CATEGORY_NAME";
	public static final String OPENBOX_ITEM = "OPENBOX_ITEM";
	public static final String ITEM_CATEGORY = "ITEM_CATEGORY";
	public static final String ZIP_CODE = "ZIP_CODE";
	public static final String LATITUDE = "LATITUDE";
	public static final String LONGITUDE = "LONGITUDE";
	public static final String CLICKCOUNTER = "CLICKCOUNTER";
	public static final String SORT_SELECTION = "SORT_SELECTION";
	public static final String IS_PRODUCT_SEARCH = "IS_PRODUCT_SEARCH";
	
	public static final String CALENDAR_EVENTS_URI_2_2_BEFORE = "content://calendar/events";
	public static final String CALENDAR_EVENTS_URI_2_2_AFTER = "content://com.android.calendar/events";
	public static final String CALENDAR_VENDER_EVENT_URI_2_2_AFTER = "vnd.android.cursor.item/event";
	
	public static final String DATE_FORMAT_TYPE_SERVER_ONE = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_TYPE_SERVER_TWO = "yyyy-MMM-dd HH:mm:ss";
	public static final String DATE_FORMAT_TYPE_ONE = "EEE, MMM dd yyyy"; //FORMAT IS Sat, Jun 04 2011
	public static final String DATE_FORMAT_TYPE_TWO = "EEEE, MMM dd, yyyy"; //FORMAT IS Saturday, Jun 04, 2011
	public static final String DATE_FORMAT_TYPE_THREE = "EEEE, MMMM dd, yyyy"; //FORMAT IS Saturday, June 04, 2011
	public static final String DATE_FORMAT_TYPE_FOUR = "h:mm a"; //FORMAT IS 12:30 PM
	public static final String DATE_FORMAT_TYPE_FIVE = "EEEE, MMMM dd"; //FORMAT IS Saturday, June 04
	public static final String DATE_FORMAT_TYPE_SIX = "dd yyyy"; //FORMAT IS 04 2011
	public static final String DATE_FORMAT_TYPE_EIGHT = "EEE MMM dd, yyyy hh:mm a"; //FORMAT IS Tue Aug 30, 2011 · 12:00pm
	public static final String DATE_FORMAT_TYPE_NINE = "EEE MMM dd, yyyy"; //FORMAT IS Tue Aug 30, 2011
	
	public static final int PROGRESS_DIALOG = 1000;
	public static final int NO_STORE_DATA_AVAILABLE_DIALOG = 1001;
	public static final int STORE_EVENT_CONNECTION_FAILURE_DIALOG = 1002;
	public static final int NO_ROAD_ROUTE_AVAILABLE_DIALOG = 1003;
	public static final int APP_NOT_FOUND_DIALOG = 1004;
	public static final int LOCATION_NOT_FOUND_DIALOG = 1005;
	public static final int LOCATION_FOUND_DIALOG = 1006;
	public static final int LOCATION_ENABLE_DIALOG = 1007;
	public static final int VALID_SEARCH_DIALOG = 1008;
	public static final int VALID_ZIPCODE_DIALOG = 1009;
	public static final int PROGRESS_EVENTS_DIALOG = 1010;
	
	public static final String FACEBOOK_API_KEY = "169558523072876";
	public static final String[] PERMISSIONS = new String[] {"publish_stream"};
	
	public static final int FACEBOOK_POST_LIMIT = 320;
	public static final int TWITTER_POST_LIMIT = 140;
	
	public static final Hashtable<String,String> CATEGORYMAP = new Hashtable<String, String>();
	public static final String SELECT_CATEGORY = "Select a Category";
	
	static {
		CATEGORYMAP.put("Video Games & Gadgets", "abcat0700000");
		CATEGORYMAP.put("Home & Appliances", "abcat0900000");
		CATEGORYMAP.put("GPS, Car & Marine", "abcat0300000");	
		CATEGORYMAP.put("Computers & Tablets", "abcat0500000");			
		CATEGORYMAP.put("Cameras & Camcorders", "abcat0400000");
		CATEGORYMAP.put("TV & Video", "abcat0100000");		
		CATEGORYMAP.put("Magnolia Home Theater", "pcmcat139900050002");
		CATEGORYMAP.put("Audio & MP3", "abcat0200000");
		CATEGORYMAP.put("Mobile Phones", "abcat0800000");
		CATEGORYMAP.put("Electronic Dictionaries", "abcat0512002");
		CATEGORYMAP.put("Audio", "abcat0200000");
		CATEGORYMAP.put("Office", "pcmcat245100050028");
	}
	
	/**
	 * Split the raw string to the day wise store opening time.
	 * @param source :  The raw store opening string
	 * @param lineBreakFormat : Either by "\n" or "<br />"
	 * @return converted String
	 */
	public static String splitTimeDayWise(String source, String lineBreakFormat) {
		String[] daysArray = source.split(";");
		String first = "";
		String dest = "";
		String leftDays = "";
		String rightTimes = "";
		String tempDays = "";
		
		for (int i = 0; i < daysArray.length; i++) {
			String[] timeArray = daysArray[i].split(":", 2);

			if (i == 0) {
				first = timeArray[1].trim();
				dest = timeArray[0].trim();
			} else {
				leftDays = timeArray[0].trim();
				rightTimes = timeArray[1].trim();
				if (first.equalsIgnoreCase(rightTimes)) {
					tempDays = leftDays;
					
					if(i == (daysArray.length - 1)) {
						dest += "-" + tempDays + ": " + changeToAMPMFormat(timeArray[1].trim(), " - ");
					}
					
				} else {
					if(tempDays.equals("")){
						dest += tempDays + ": " + changeToAMPMFormat(first, " - ") + lineBreakFormat;
					} else {
						dest += "-" + tempDays + ": " + changeToAMPMFormat(first, " - ") + lineBreakFormat;
					}
					first = timeArray[1].trim();
					dest += timeArray[0].trim();
					tempDays = "";
					
					if(i == (daysArray.length - 1)) {
						dest += ": " + changeToAMPMFormat(timeArray[1].trim(), " - ");
					}
				}
			}
		}

		return dest;
	}

	/**
	 * Concatenating corresponding AM PM.
	 * @param source : The Source string
	 * @param divider : Either by " - " or " to "
	 * @return converted string
	 */
	private static String changeToAMPMFormat(String source, String divider) {
		String dest = "";
		String timeArray[] = source.split("-");
		
		if(timeArray.length == 2) {
			String [] temp1 = timeArray[0].split(":");
			if(temp1.length == 2) {
				if(Integer.parseInt(temp1[0].trim()) > 12) {
					dest += temp1[0].trim() + ":" + temp1[1].trim() + " PM";
				} else {
					dest += temp1[0].trim() + ":" + temp1[1].trim() + " AM";
				}
			} else {
				if(Integer.parseInt(timeArray[0].trim()) > 12) {
					dest += timeArray[0].trim() + ":00 PM";
				} else {
					dest += timeArray[0].trim() + ":00 AM";
				}
			}
			
			dest += divider;
			
			String[] temp2 = timeArray[1].split(":");
			if(temp2.length == 2) {
				if(Integer.parseInt(temp2[0].trim()) > 12) {
					dest += temp2[0].trim() + ":" + temp2[1].trim() + " AM";
				} else {
					dest += temp2[0].trim() + ":" + temp2[1].trim() + " PM";
				}
				
			} else {
				if(Integer.parseInt(timeArray[1].trim()) > 12) {
					dest += timeArray[1].trim() + ":00 AM";
				} else {
					dest += timeArray[1].trim() + ":00 PM";
				}
			}
		}
		
		if(dest.equals(""))
			dest = "Closed";
		
		return dest;
	}
	
	/**
	 * Get todays day. 
	 * @return : Date String
	 */
	public static String getTodaysDay() {
		Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
 
        try {
            
           return  dateFormat.format(calendar.getTime());
            
        } catch (Exception e) {
        	BBYLog.printStackTrace(TAG, e);
        }
        
        return null;
	}
	
	/**
	 * Get the todays store opening time from the raw string
	 * @param source : Complete raw date string
	 * @return : Todays time String
	 */
	public static String getTodayOpeningTime(String source) {
		String today = getTodaysDay();
		String[] daysArray = source.split(";");
		String first = "";
		StringBuilder destBuilder = new StringBuilder();

		for (int i = 0; i < daysArray.length; i++) {
			String[] timesArray = daysArray[i].split(":", 2);
			
			if(timesArray[0].trim().contains(today.trim())) {
				first = timesArray[1].trim();
				destBuilder.append(changeToAMPMFormat(first, " to "));
			}
		}
		
		String dest = destBuilder.toString();
		if(dest.equals(""))
			dest = "Closed";
		
		return dest;
	}
	
	//FORMAT IS Sat, Jun 04 2011
	public static String convertToDateTimeStringType(String source, String serverType, String conversionType) {
		if(source == null)
			return "";
		
		if(source.equals(""))
			return "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(serverType);
        String dateString = null; 
        try {
        	Date date = dateFormat.parse(source); 
        	SimpleDateFormat dateFormat1 = new SimpleDateFormat(conversionType);
        	dateString = dateFormat1.format(date);
        } catch (Exception e) {
        	BBYLog.printStackTrace(TAG, e);
        }
        
        return dateString;
	}
	
	//FORMAT IS Saturday, June 04, 2011
	public static String convertToDateTimeStringType(long seconds, String conversionType) {
        Date date = new Date(seconds * 1000);
        
        String dateString = "";
		try {
        	SimpleDateFormat dateFormat = new SimpleDateFormat(conversionType);
        	dateString  = dateFormat.format(date);
        } catch (Exception e) {
        	BBYLog.printStackTrace(TAG, e);
        }
        
        return dateString;
	}
	
	/**
	 * Change the phone format to different format 
	 * @param source : phone number string
	 * @return :  Formatted string
	 */
	public static String changeToPhoneFormat(String source) {
		String numberArray[] = source.split("-");
		String dest = "";
		
		dest = "("+ numberArray[0].trim() + ") " + numberArray[1].trim() + "-" + numberArray[2].trim();
		
		return dest;
	}
	
	/**
	 * Replace all HTML tags from the string.
	 * @param htmlString : HTML String
	 * @return : String
	 */
	public static String removeHtmlTag(String htmlString) {
		return htmlString.replaceAll("\\<.*?\\>", "").trim();
	}

	/**
	 * Truncate Face Book message as per 320 characters.
	 * @param source : Message to truncate
	 * @return truncated string
	 */
	public static String truncateFacebookMessage(String source) {
		source = source.trim();
		
		if(source.length() > FACEBOOK_POST_LIMIT) {
			return source.substring(0, FACEBOOK_POST_LIMIT);
		}
		
		return source;
	}
	
	/**
	 * Truncate Twitter message as per 140 characters.
	 * @param source : Message to truncate
	 * @return truncated string
	 */
	public static String truncateTwitterMessage(String source) {
		source = source.trim();
		
		if(source.length() > TWITTER_POST_LIMIT) {
			return source.substring(0, TWITTER_POST_LIMIT);
		}
		
		return source;
	}
	
	public static String createFormatedPriceString(String price) {
		if (price == null)
			return "0.0";

		if (price.length() == 0)
			return "0.0";

		DecimalFormat myFormatter = new DecimalFormat("#,##,###.##");

		String formattedPrice = myFormatter.format(Double.parseDouble(price));
		if (formattedPrice != null) {
			if (formattedPrice.contains(".")
					&& formattedPrice.substring(formattedPrice.indexOf("."))
							.length() == 2)
				return formattedPrice + "0";
			else if (formattedPrice.contains("."))
				return formattedPrice;
			else
				return formattedPrice + ".00";
		}

		return "0.0";
	}
}
