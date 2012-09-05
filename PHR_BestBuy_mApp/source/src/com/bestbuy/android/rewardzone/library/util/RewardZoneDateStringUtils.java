package com.bestbuy.android.rewardzone.library.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Utility class holds the methods need to convert data format
 * 
 * @author sharmila_j
 * 
 */
public class RewardZoneDateStringUtils {
	
	public static String convertStringToDate(String dateString)  {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
		String dateStr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		try{
			Date formattedDate = sdf.parse(dateString);
			dateStr = dateFormat.format(formattedDate);
		}catch(ParseException pe) {
			
		}	
		return  dateStr;
	}
	
	public static String convertStringToDateWithYear(String dateString)  {
		DateFormat dateFormatWithYear = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		String dateStr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
		try{
			Date formattedDate = sdf.parse(dateString);
			dateStr = dateFormatWithYear.format(formattedDate);
		}catch(ParseException pe) {
			
		}	
		return  dateStr;
	}
}
