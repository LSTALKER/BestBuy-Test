package com.bestbuy.android.hem.library.util;

import java.text.NumberFormat;

public class Util {
	
	private final static String TAG = "Util.Java";
	
	public static final String[] Months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	public static String getDateFormat(String format) {
		// Purchase any time starting on Mon DD, YYYY (example: Jan 1, 2011)
		// format e.g. "startPurchaseDate" : "2011-01-01", endPurchaseDate" : "2011-12-31",
		
		try {
			String[] formatArray = format.split("-");
			String year = formatArray[0];
			String month = formatArray[1];
			String day = formatArray[2];
			
			String formated = Months[Integer.parseInt(month)-1] + " " + day + ", " + year;
			return formated;
		} catch(Exception e) {
			return "";
		}
	}
	
	/*public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			BBYLog.printStackTrace(TAG, e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {				
				BBYLog.printStackTrace(TAG, e);			
			}
		}
		return sb.toString();
	}*/
	
	public static String formatNumber2fractions(String format) {
		try {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);			
			double d = Double.parseDouble(format.trim());			
			format = nf.format(d);
			return format;
		} catch(Exception e) {
			return format;
		}
	}
	
	public static String formatNumber0fractions(String format) {
		try {
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(0);
			nf.setMinimumFractionDigits(0);	
			double d = Double.parseDouble(format.trim());			
			format = nf.format(d);
			return format;
		} catch(Exception e) {
			return format;
		}
	}
}
