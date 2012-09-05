package com.bestbuy.android.pushnotifications.data;

import java.util.Date;
import java.util.TimeZone;
/**
 * 
 * @author bharathkumar_s
 *
 */
public class UpdatedTimeZone {

	public static String getTimeZoneName() {
		
		TimeZone timeZone=TimeZone.getDefault();
        Date date=new Date();
		return timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.SHORT).trim();
	}

	
}
