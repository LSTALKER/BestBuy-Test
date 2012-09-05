package com.bestbuy.android.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;

import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.BitmapManager;
import com.bestbuy.android.util.ImageContentProvider;

public class SavedBitmap
{
	private static final String TAG = "SavedBitmap.java";
	private Bitmap b; 
	private String uriPath;
	private String url; 
	private boolean isHelper = false; 
	
	public SavedBitmap() {} 
	
	public boolean isHelper() {
		return isHelper;
	}
	public void setIsHelper(boolean isHelper) {
		this.isHelper = isHelper;
	}
	
	public Bitmap getB() {
		return b;
	}
	public void setB(Bitmap b) {
		this.b = b;
	}
	public String getUriPath() {

		return ImageContentProvider.ImageURI + uriPath;

	}
	
	public URI getUri()
	{
		return URI.create(getUriPath()); 
	}
	
	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	} 
	
	
	public String getUrl()
	{
		return url; 
	}
	
	public void setUrl(String url)
	{
		this.url = url;  
	}
	
	
    private ParcelFileDescriptor getPFD() {
		
		URI fileURI = URI.create("file://" + getUri().getPath());
		File file = new File(fileURI);

		ParcelFileDescriptor parcel = null;
		try {
			parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		} catch (FileNotFoundException e) {
			BBYLog.printStackTrace(TAG, e);
		}

		return parcel;
    }

	
	private BitmapFactory.Options snifBitmapOptions() {
		ParcelFileDescriptor input = getPFD();
		if (input == null)
			return null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapManager.instance().decodeFileDescriptor(
					input.getFileDescriptor(), options);
			return options;
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}
		}
	}

    public String getMimeType() {
        BitmapFactory.Options options = snifBitmapOptions();
        return (options != null && options.outMimeType != null)
                ? options.outMimeType
                : "";
    }
}
