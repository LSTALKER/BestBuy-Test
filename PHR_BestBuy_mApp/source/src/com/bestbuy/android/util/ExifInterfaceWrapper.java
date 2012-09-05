package com.bestbuy.android.util;

import java.io.IOException;

import android.media.ExifInterface;

public class ExifInterfaceWrapper {
	
	private final String TAG = this.getClass().getName();
	
	public static final int ORIENTATION_FLIP_HORIZONTAL = ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
	public static final int ORIENTATION_FLIP_VERTICAL = ExifInterface.ORIENTATION_FLIP_VERTICAL;	
	public static final int ORIENTATION_NORMAL = ExifInterface.ORIENTATION_NORMAL;	
	public static final int ORIENTATION_ROTATE_180 = ExifInterface.ORIENTATION_ROTATE_180;	
	public static final int ORIENTATION_ROTATE_270 = ExifInterface.ORIENTATION_ROTATE_270; 	
	public static final int ORIENTATION_ROTATE_90 = ExifInterface.ORIENTATION_ROTATE_90; 	
	public static final int ORIENTATION_TRANSPOSE = ExifInterface.ORIENTATION_TRANSPOSE;	
	public static final int ORIENTATION_TRANSVERSE = ExifInterface.ORIENTATION_TRANSVERSE;	
	public static final int ORIENTATION_UNDEFINED = ExifInterface.ORIENTATION_UNDEFINED;
	
	public static final String TAG_DATETIME = ExifInterface.TAG_DATETIME;
	public static final String TAG_FLASH 	= ExifInterface.TAG_FLASH;
	public static final String TAG_GPS_LATITUDE = ExifInterface.TAG_GPS_LATITUDE;
	public static final String TAG_GPS_LATITUDE_REF = ExifInterface.TAG_GPS_LATITUDE_REF;
	public static final String TAG_GPS_LONGITUDE = ExifInterface.TAG_GPS_LONGITUDE;
	public static final String TAG_GPS_LONGITUDE_REF = ExifInterface.TAG_GPS_LONGITUDE;
	public static final String TAG_IMAGE_LENGTH = ExifInterface.TAG_IMAGE_LENGTH;
	public static final String TAG_IMAGE_WIDTH = ExifInterface.TAG_IMAGE_WIDTH;
	public static final String TAG_MAKE = ExifInterface.TAG_MAKE;
	public static final String TAG_MODEL = ExifInterface.TAG_MODEL;
	public static final String TAG_ORIENTATION = ExifInterface.TAG_ORIENTATION;
	public static final String TAG_WHITE_BALANCE = ExifInterface.TAG_WHITE_BALANCE;
	
	public static final int WHITEBALANCE_AUTO = ExifInterface.WHITEBALANCE_AUTO; 	
	public static final int WHITEBALANCE_MANUAL = ExifInterface.WHITEBALANCE_MANUAL;
	
	private ExifInterface exifInterface;
	
	
	
	static {
		try {
			Class.forName("ExifInterface");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void checkAvailable() {}

	
	public ExifInterfaceWrapper(String filename){
		try{
			exifInterface = new ExifInterface(filename);
		} catch (IOException ex) {
			BBYLog.printStackTrace(TAG, ex);
		}
	}
	
	public String getAttribute(String tag){
		return exifInterface.getAttribute(tag);
	}
	public int getAttributeInt(String tag, int defaultValue){
		return exifInterface.getAttributeInt(tag,defaultValue);
	}
	public boolean getLatLong(float[] output){
		return exifInterface.getLatLong(output);
	}
	public byte[] 	getThumbnail(){
		return exifInterface.getThumbnail();
	}
	public boolean hasThumbnail(){
		return exifInterface.hasThumbnail();
	}
	public void saveAttributes(){
		try {
			exifInterface.saveAttributes();
		} catch (IOException ex) {
			BBYLog.printStackTrace(TAG, ex);
		}
		
	}
	public void setAttribute(String tag, String value){
		exifInterface.setAttribute(tag,value);
	}

}
