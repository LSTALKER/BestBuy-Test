package com.bestbuy.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.PhotoSearch;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.IQEApiJSON;
import com.bestbuy.android.util.IQEApiJSON.IQEQuery;
import com.bestbuy.android.util.ImageProvider;

/**
 * Lets the user take a picture and get product results via IQEngines
 * 
 * @author Recursive Awesome
 * 
 */
public class PhotoSearchQuery extends MenuActivity {
	private final String TAG = this.getClass().getName();

	/** The size of the widest or tallest dimension of the image */
	private static final int SCALE_SIZE = 500;

	private final int CAMERA_ACTIVITY = 0;
	private PhotoSearch photoSearch;
	public static IQEApiJSON iqe;
	private boolean isRetry = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Date date = new Date();
		File file;

		try {
			file = new File(AppData.getHiddenDataDirectory(), "photosearch-" + date.getTime() + ".jpg");
			photoSearch = new PhotoSearch(String.valueOf(date.getTime()));
			photoSearch.setTmpFile(file); // because of a bug in Android, we need to create a temporary file on the SDCard

		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			//Check if sdcard is accessible
			CharSequence text = "Unable to access SD Card";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(PhotoSearchQuery.this, text, duration);
			toast.show();
			this.finish();
			return;

		}
		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(i, CAMERA_ACTIVITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_CANCELED) {
			//BestBuyApplication.showToastNotification("Photo Search Canceled", PhotoSearchQuery.this, Toast.LENGTH_SHORT);
			//finish();
			Intent i = new Intent(PhotoSearchQuery.this, PhotoSearchList.class);
			i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);
			PhotoSearchQuery.this.finish();
			return;
		}
		if (requestCode == CAMERA_ACTIVITY) {
			new IQEnginesQueryTask(PhotoSearchQuery.this).execute(); //do all of the initial copy and uploading in a task.
		}
	}

	private void resizeImage() {
		float degrees = ImageProvider.getExifRotationDegrees(photoSearch.getTmpFile());
		Matrix rotationMatrix = new Matrix();
		rotationMatrix.setRotate(degrees);
		Bitmap bm = ImageProvider.scaleBitmap(photoSearch.getTmpFile(), SCALE_SIZE, SCALE_SIZE); //scale the tmp bitmap down
		if (bm != null && rotationMatrix != null) {
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), rotationMatrix, false);
		}
		if (bm == null) {
			BBYLog.e(TAG, "Error loading/decoding " + photoSearch.getTmpFile());
			return;
		}
		try {
			OutputStream out = openFileOutput(photoSearch.getId(), Context.MODE_PRIVATE); //save to internal storage
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			// get a handle to the internal storage file
			File localStorageFile = new File(getFilesDir(), photoSearch.getId());
			photoSearch.setFile(localStorageFile);
			// delete and null the tmp bitmap
			File tmpFile = new File(photoSearch.getTmpFile().getAbsolutePath());
			tmpFile.delete();
			photoSearch.setTmpFile(null);
		} catch (FileNotFoundException e) {
			BBYLog.printStackTrace(TAG, e);
			BBYLog.e(TAG, "File Not Found Exception in PhotoSearchQuery: " + e.getMessage());
		} catch (IOException e) {
			BBYLog.printStackTrace(TAG, e);
			BBYLog.e(TAG, "IO Exception in PhotoSearchQuery: " + e.getMessage());
		}

		if (iqe == null) { //build API object just once.
			iqe = new IQEApiJSON(AppData.IQENGINES_KEY, AppData.IQENGINES_SECRET);
		}
	}

	/**
	 * Scales a bitmap to SCALE_SIZE preserving aspect ratio
	 * 
	 * @param file
	 * @return
	 */

	public class IQEnginesQueryTask extends BBYAsyncTask {

		public IQEnginesQueryTask(Activity activity) {
			super(activity , "Uploading Image...");
		}

		@Override
		public void doTask() throws Exception {
			if(!isRetry)
				resizeImage();
			IQEQuery query = iqe.query(photoSearch.getFile(), null, null, AppConfig.getEncryptedDeviceId(), true, null, null, null);
			photoSearch.setQid(query.getQID());
			appData.addPhotoSearch(photoSearch, true);
		}

		@Override
		public void doFinish() {
			Intent i = new Intent(PhotoSearchQuery.this, PhotoSearchList.class);
			startActivity(i);
			finish(); 
		}
		
		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						isRetry = true;
						new IQEnginesQueryTask(activity).execute();
					}		
				}, new NoConnectivityExtension.OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} 
		}
	}
}
