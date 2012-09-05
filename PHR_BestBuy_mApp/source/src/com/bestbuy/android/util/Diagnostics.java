package com.bestbuy.android.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Diagnostics
{
	
	private static long methodTraceMilliSeconds; 

	public static void StartMethodTracing(Context context)
	{
			methodTraceMilliSeconds = System.currentTimeMillis();		
	}
	
	public static void StopMethodTracing(Context context, String Tag, String leadingMessage)
	{
			BBYLog.i(Tag, leadingMessage + (System.currentTimeMillis()-methodTraceMilliSeconds) + " milliseconds");
	}
	
	
	public static InputStream dumpInputStream(Context context, InputStream is, String tag, String methodName)
			 throws IOException 
	{
			if(AppConfig.isTest())
			{
				byte[] buffer = InputStreamExtensions.inputStreamToByteArray(is);
				is.close();
				BBYLog.v(tag, methodName + " response: " + new String(buffer));
				return new ByteArrayInputStream(buffer);
			}
			else
			{
				return is; 
			}
	}
}
