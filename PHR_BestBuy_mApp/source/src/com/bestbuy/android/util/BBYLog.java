package com.bestbuy.android.util;

import android.util.Log;

public class BBYLog  {
	
	//TODO: Ryan, deleted disabling logging, should be disabled in the manifest 
	
	private static final String MSG_PREFIX = "[BBYLog] ";
	private static final String TAG_PREFIX = "";

	

	public static void printStackTrace(String Tag, Exception e)
	{
		if(AppConfig.isTest())
		{
			if(e != null && e.getStackTrace() != null)
			{
				StringBuilder sb = new StringBuilder(); 
				StackTraceElement[] stackTraceElements = e.getStackTrace();
				
				
				if(e.toString() != null)
				{
					BBYLog.e(Tag, 
							new StringBuilder()
								.append("------ ")
								.append(e.toString())
								.append(" -----").toString());
				}
				if(e.getCause() != null)
				{
					Throwable cause = e.getCause(); 
					String causeErrorMessage = cause.getMessage();
					BBYLog.e(Tag, 
							new StringBuilder()
								.append("------ ")
								.append(causeErrorMessage)
								.append(" -----").toString());
				}
				if(e.getMessage() != null)
				{
					BBYLog.e(Tag, 
							new StringBuilder()
								.append("------ ")
								.append(e.getMessage())
								.append(" -----").toString());
				}
				for(StackTraceElement traceElement : stackTraceElements)
				{
					sb.append(traceElement.getClassName())
						.append(" - ")
						.append(traceElement.getMethodName())
						.append(" - ")
						.append(traceElement.getLineNumber());
					
					BBYLog.e(Tag, sb.toString());
					sb = new StringBuilder(); 
				}
			}
			else 
			{
				BBYLog.e(Tag, "Error - No Message");
			}
		}
	}
	
	
	public static void v(String tag, String message) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.d(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message));
		}
	}
	
	public static void d(String tag, String message) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.d(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message));
		}
	}
	
	public static void i(String tag, String message) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.d(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message));
		}
	}
	
	public static void w(String tag, String message) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.w(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message));
		}
	}
	
	public static void e(String tag, String message) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.d(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message));
		}
	}
	
	//Message plus throwables
	public static void v(String tag, String message, Throwable tr) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.v(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message), tr);
		}
	}
	
	public static void d(String tag, String message, Throwable tr) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.d(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message), tr);
		}
	}
	
	public static void i(String tag, String message, Throwable tr) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.i(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message), tr);
		}
	}
	
	public static void w(String tag, String message, Throwable tr) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.w(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message), tr);
		}
	}
	
	public static void e(String tag, String message, Throwable tr) {
		if(AppConfig.enableLogging()){
			if(tag.length()<=100)
				Log.e(TAG_PREFIX + logMessageEncoding(tag), MSG_PREFIX + logMessageEncoding(message), tr);
		}
	}
	
	private static String logMessageEncoding(String input){
		String encodedMessage = input.replace( '\n', '_' ).replace( '\r', '_' );
		return encodedMessage;
	}
}
