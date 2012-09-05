package com.bestbuy.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

/**
 * @Author Recursive Awesome
 * @Email Ryan@recursiveawesome.com	
 */

/**
 * Provides access to images in the private application directory
 */

public class ImageContentProvider extends ContentProvider {

	private static final String TAG = "ImageContentProvider.java";
	
	
	public static final String ImageURI = "content://com.bestbuy.android/";
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) {

		BBYLog.i("ImageContentProvider", "openFile: " + uri.getPath());
		
		URI fileURI = URI.create("file://" + uri.getPath());
		File file = new File(fileURI);

		ParcelFileDescriptor parcel = null;
		try {
			parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (FileNotFoundException e) {
			BBYLog.printStackTrace(TAG, e);
		}

		return parcel;
	}

	
	
	

}
