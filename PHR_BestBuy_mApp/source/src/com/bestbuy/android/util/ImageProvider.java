package com.bestbuy.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.SavedBitmap;

/**
 * Provides efficient caching and background downloading of images from the web.
 * 
 * @Author Recursive Awesome
 */
public class ImageProvider {

	private static final String TAG = "ImageProvider";
	private static final String FILE_EXT = ".jpg";
	private static final CompressFormat FILE_TYPE = Bitmap.CompressFormat.PNG;

	/**
	 * Implementation of a {@link LinkedHashMap} that is used for image caching.
	 * It holds urls and their corresponding Bitmaps. When the capacity as set
	 * by CACHE_SIZE is exceeded, it will remove the oldest entry for us
	 * automatically.
	 * 
	 */
	private static class CacheMap extends LinkedHashMap<String, Bitmap> {
		private static final long serialVersionUID = -3111603455030636398L;
		private final int capacity;

		public CacheMap(int capacity) {
			super(capacity + 1, 1.1f, true);
			this.capacity = capacity;
		}

		protected boolean removeEldestEntry(Entry eldest) {
			return size() > capacity;
		}
	}

	private static Bitmap loadingImage;
	private static Bitmap errorImage;

	private static final int DOWNLOAD_THREADS = 3;
	private static final int CACHE_SIZE = 50;
	private static final int MAX_RETRIES = 3;

	private static final String URL_EXTRA = "imageUrl";
	private static final String BITMAP_EXTRA = "bitmap";

	private static CacheMap imageCache = new CacheMap(CACHE_SIZE);
	private static ThreadPoolExecutor executor;
	private static HttpClient httpClient;
	private static Context _applicationContext = null;
	
	// used to support 1.6
	private static boolean exifInterfaceAvailable;

	//Hashmap with a List of ImageViews that are waiting on another thread to load this image url.  This saves us from downloading a bitmap more than once.
	private static HashMap<String, List<ImageView>> waitList = new HashMap<String, List<ImageView>>();

	/**
	 * Initializes the ImageProvider class. This method must be called before any
	 * other methods in the class. You should only need to call this once,
	 * ideally in the onCreate() of a custom Application instance.
	 * 
	 * @param context
	 *            the application context
	 */
	public static void initialize(Context context) {
		if (loadingImage == null) {
			loadingImage = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.loading_big);
		}

		if (errorImage == null) {
			errorImage = BitmapFactory.decodeResource(context.getApplicationContext().getResources(), R.drawable.comingsoonoff);
		}

		if (executor == null) {
			executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DOWNLOAD_THREADS);
		}
		
		if (_applicationContext == null) {
			_applicationContext = context.getApplicationContext();
			BBYLog.i(TAG, "Image Provider: Initializing");
		}
	}

	/**
	 * Returns the loading image to be used while the image is loading.
	 * 
	 * @return the loading bitmap
	 */
	public static Bitmap getLoadingImage() {
		return loadingImage;
	}

	/**
	 * Returns the error image to be used if the image fails to load
	 * 
	 * @return a Bitmap indicating an error occurred
	 */
	public static Bitmap getErrorImage() {
		return errorImage;
	}

	/**
	 * Loads a bitmap image from the web and assigns it to an ImageView when
	 * loading is complete. While the image is in queue to be downloaded, a
	 * loading image will be shown in the ImageView until the download is
	 * complete.
	 * 
	 * @param imageUrl
	 *            the url of the image to be download
	 * @param imageView
	 *            the ImageView instance to update
	 */
	public static void getBitmapImageOnThread(final String imageUrl, final ImageView imageView) {
		if (imageUrl != null && imageView != null) {
			imageView.setTag(imageUrl);
			if (imageCache.get(imageUrl) != null) {
				if (imageCache.get(imageUrl).equals(loadingImage)) {
					// This url is already being loaded. Don't load it again,
					// but wait for it.
					imageView.setImageBitmap(loadingImage);
					addToWaitingList(imageUrl, imageView);
				} else {
					// The image is in the cache already, set the imageView
					imageView.setImageBitmap(imageCache.get(imageUrl));
				}
			} else {
				// The image is not in cache, and the url is not already being
				// loaded. Assign a thread to download the image.
				imageView.setImageBitmap(loadingImage);
				// Cache this so we know it is being loaded.
				cacheBitmap(imageUrl, loadingImage);
				executor.execute(new ImageLoadRunnable(imageUrl, imageView));
				BBYLog.d(TAG, "size of queue: " + executor.getQueue().size());
			}
		}
	}

	/**
	 * Adds an imageView to the waiting list for the specified url. If we are
	 * already downloading the image url in a thread (for another ImageView),
	 * then when it finishes it will assign that bitmap to the the imageViews in
	 * the waiting list.
	 * 
	 * @param imageUrl
	 *            the URL that is being downloaded already
	 * @param imageView
	 *            the ImageView that is waiting for this URL.
	 */
	public static void addToWaitingList(String imageUrl, ImageView imageView) {
		List<ImageView> imageViewList = (List<ImageView>) waitList.get(imageUrl);
		if (imageViewList == null) {
			imageViewList = new ArrayList<ImageView>();
		}
		imageViewList.add(imageView);
		waitList.put(imageUrl, imageViewList);
	}

	/**
	 * The runnable to assign to our executor. Attempts to download an image and
	 * cache it. If the download fails, it will attempt to retry a specified
	 * number of times. If the image still can't be downloaded, it sets the
	 * Bitmap to an error Bitmap.
	 */
	private static class ImageLoadRunnable implements Runnable {
		private String imageUrl;
		private ImageNotifyHandler handler;
		private int retryCount;

		public ImageLoadRunnable(String imageUrl, ImageView imageView) {
			this.imageUrl = imageUrl;
			this.handler = new ImageNotifyHandler(imageUrl, imageView);
			retryCount = 0;
		}

		/**
		 * Downloads the specified bitmap, caches it, and notifies the
		 * corresponding imageView. If the download times out, it will retry. If
		 * the retry fails, sets the image to the specified error Bitmap.
		 */
		public void run() {
			Bitmap bitmap = null;
			try {
				bitmap = downloadBitmap(imageUrl);
				if (bitmap == null) {
					bitmap = getErrorImage();
				}
				cacheBitmap(imageUrl, bitmap);
				notifyImageLoaded(imageUrl, bitmap);
			} catch (Exception e) {
				// Timed out
				if (retryCount < MAX_RETRIES) {
					BBYLog.e(TAG, imageUrl + "Timed out, retryCount: " + retryCount);
					retryCount++;
					executor.execute(this);
				} else {
					// The image wouldn't load after retrying, so display the error image
					cacheBitmap(imageUrl, getErrorImage());
					notifyImageLoaded(imageUrl, getErrorImage());
				}
			}
		}

		/**
		 * Sends a message to the handler with the Bitmap and coresponding image
		 * URL.
		 * 
		 * @param url
		 *            the URL of the image
		 * @param bitmap
		 *            the Bitmap that was downloaded
		 */
		public void notifyImageLoaded(String url, Bitmap bitmap) {
			Message message = new Message();
			Bundle data = new Bundle();
			data.putString(ImageProvider.URL_EXTRA, url);
			Bitmap image = bitmap;
			data.putParcelable(ImageProvider.BITMAP_EXTRA, image);
			message.setData(data);
			handler.sendMessage(message);
		}
	}

	/**
	 * Downloads and decodes an image from the web into a Bitmap instance
	 * 
	 * @param imageUrl
	 *            the url of the image to be downloaded
	 * @return the Bitmap that was loaded
	 * @throws Exception
	 */
	private static Bitmap downloadBitmap(String imageUrl) throws Exception {
		//TODO: figure out how we can reuse HttpClient across threads, more efficient.
		if (httpClient == null) {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
	        httpClient = new DefaultHttpClient(cm, params);
		}
		HttpGet httpget = new HttpGet(imageUrl);
		httpget.setParams(httpClient.getParams());
		Bitmap bitmap = null;
		HttpResponse response = httpClient.execute(httpget, new BasicHttpContext());
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			byte[] bytes = EntityUtils.toByteArray(entity);
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		}
		return bitmap;
	}

	/**
	 * Adds a Bitmap to the cache.
	 * 
	 * @param key
	 *            the url that the Bitmap was downloaded from
	 * @param b
	 *            the Bitmap to cache
	 */
	private static void cacheBitmap(String key, Bitmap b) {
		if (key != null) {
			imageCache.put(key, b);
		}
	}

	/**
	 * Removes all items from the image cache.
	 */
	public static void clearCache() {
		if (imageCache != null) {
			imageCache.clear();
		}
	}

	/**
	 * The handler that is called when the image download completes. This sets
	 * the ImageView to the bitmap that is sent in the handler message.
	 * 
	 */
	private static class ImageNotifyHandler extends Handler {
		private ImageView imageView;
		private String imageUrl;

		public ImageNotifyHandler(String imageUrl, ImageView imageView) {
			this.imageView = imageView;
			this.imageUrl = imageUrl;
		}

		/**
		 * Get the Bitmap that was passed in the message and assign it to any
		 * ImageViews that are waiting for it.
		 */
		@Override
		public final void handleMessage(Message msg) {
			Bundle data = msg.getData();
			Bitmap bitmap = data.getParcelable(BITMAP_EXTRA);

			// Set the original imageview that we intended to load
			if (imageUrl.equals(imageView.getTag())) {
				imageView.setImageBitmap(bitmap);
			}

			// Loop through the waiting list and set any imageviews waiting on
			// this image.
			List<ImageView> imageViews = waitList.get(imageUrl);
			if (imageViews != null) {
				for (ImageView imageView : imageViews) {
					if (imageUrl.equals(imageView.getTag())) {
						imageView.setImageBitmap(bitmap);
					}
				}
				// Clear the waiting list for this url when we are done
				imageViews.clear();
				waitList.put(imageUrl, null);
			}
		}
	}
	
	/*
	 * Path is generally the SKU, localFilename is the directory count + .img
	 * returns the files new absolute path
	 */
	public static String cacheBitmapToSD(String path, String localFilename, Bitmap bitmap, boolean generateLocalFileName) {
		return cacheBitmapToSD(path, localFilename, bitmap, generateLocalFileName, false);
	}
	
	/*
	 * Path is generally the SKU, localFilename is the directory count + .img
	 * returns the files new absolute path
	 */
	public static String cacheBitmapToSD(String path, String localFilename, Bitmap bitmap, boolean generateLocalFileName,
			boolean markToDelete) {
		String retVal = "";
		if (path != null) {

			FileOutputStream fos = null;

			try {
				File outputFile = generateFileLocation(path, localFilename, generateLocalFileName);
				retVal = outputFile.getCanonicalPath();
				fos = new FileOutputStream(outputFile);
				if (markToDelete) {
					outputFile.deleteOnExit();
				}
				if (bitmap != null) {
					bitmap.compress(FILE_TYPE, 100, fos);
				}
				fos.flush();
				fos.close();
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}
		}
		return retVal;
	}

	public static File generateFileLocation(String path, String localFilename, boolean generateLocalFileName) throws Exception {
		path = "shots" + File.separator + path;
		File newDir = AppData.getAppDataDirectory(path);
		if (generateLocalFileName) {
			String[] listOfFiles = newDir.list();
			if (listOfFiles != null) {
				localFilename = listOfFiles.length + FILE_EXT;
			}
		}

		File outputFile = new File(newDir.getAbsolutePath(), localFilename);
		return outputFile;
	}

	/*public static Uri cacheBitmapToTempStore(Bitmap b) {
		FileOutputStream fos = null;
		Uri U = null;
		try {
			fos = _applicationContext.openFileOutput("temp.jpg", Context.MODE_WORLD_READABLE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			File F = _applicationContext.getFileStreamPath("temp.jpg");
			U = Uri.fromFile(F);
			fos.close();
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return U;
	}*/

	public static boolean deleteFileDirectory(String path) throws Exception {
		path = "shots" + File.separator + path;
		File newDir = AppData.getAppDataDirectory(path);
		File[] filesInDir = newDir.listFiles();
		for (File innerFile : filesInDir) {
			boolean successDeleteInnerFile = innerFile.delete();
			BBYLog.i(TAG, "Inner file: " + successDeleteInnerFile);
		}

		return newDir.delete();
	}

	private static final int IMAGE_MAX_SIZE = 400;

	public static Bitmap getUnscaledBitmap(File file) {

		Bitmap unscaled = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, o);
			double scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = Math
						.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = (int) scale;
			unscaled = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			BBYLog.printStackTrace(TAG, e);
		}

		return unscaled;
	}

	public static Bitmap scaleBitmap(File file, int scaleSizeWidth, int scaleSizeHeight) {

		Bitmap unscaled = getUnscaledBitmap(file);
		return scaleBitmap(unscaled, scaleSizeWidth, scaleSizeHeight);
	}

	public static Bitmap scaleBitmap(Bitmap unscaled, int scaleSizeWidth, int scaleSizeHeight) {
		if (unscaled == null) {
			return null;
		}

		int width = unscaled.getWidth();
		int height = unscaled.getHeight();
		float aspect = (float) width / (float) height;
		Bitmap scaled;
		if (aspect == 1f) {
			// square
			scaled = Bitmap.createScaledBitmap(unscaled, scaleSizeWidth, scaleSizeHeight, true);
		} else if (aspect < 1f) {
			// portrait
			float scale = (float) scaleSizeHeight / (float) height;
			scaled = Bitmap.createScaledBitmap(unscaled, (int) (width * scale), (int) (height * scale), true);
		} else {
			// landscape
			float scale = (float) scaleSizeWidth / (float) width;
			scaled = Bitmap.createScaledBitmap(unscaled, (int) (width * scale), (int) (height * scale), true);
		}

		unscaled.recycle();
		return scaled;
	}

	public static float getExifRotationDegrees(File file) {
		float degrees = 0;
		if (Build.VERSION.RELEASE.compareTo("2.0") >= 0) {
			if (exifInterfaceAvailable) {
				ExifInterfaceWrapper exif;
				try {
					exif = new ExifInterfaceWrapper(file.getCanonicalPath());
					String rotationAmount = exif.getAttribute(ExifInterfaceWrapper.TAG_ORIENTATION);
					if (!TextUtils.isEmpty(rotationAmount)) {
						int rotationParam = Integer.parseInt(rotationAmount);
						switch (rotationParam) {
						case ExifInterfaceWrapper.ORIENTATION_NORMAL:
							degrees = 0;
							break;
						case ExifInterfaceWrapper.ORIENTATION_ROTATE_90:
							degrees = 90;
							break;
						case ExifInterfaceWrapper.ORIENTATION_ROTATE_180:
							degrees = 180;
							break;
						case ExifInterfaceWrapper.ORIENTATION_ROTATE_270:
							degrees = 270;
							break;
						default:
							degrees = 0;
							break;
						}
					}
				} catch (IOException e) {
					BBYLog.printStackTrace(TAG, e);
				}
			}
		}
		return degrees;
	}

	public static ArrayList<SavedBitmap> pullImageDirectoryFromSDCard(String path, int scaleSizeWidth, int scaleSizeHeight) {
		ArrayList<SavedBitmap> retVal = new ArrayList<SavedBitmap>();
		if (path != null) {
			try {
				path = "shots" + File.separator + path;
				File newDir = AppData.getAppDataDirectory(path);

				File[] listOfFiles = newDir.listFiles();

				if (listOfFiles != null) {
					for (int i = 0; i < listOfFiles.length; i++) {
						Bitmap bitmap = ImageProvider.scaleBitmap(listOfFiles[i], scaleSizeWidth, scaleSizeHeight);
						SavedBitmap sb = new SavedBitmap();
						sb.setUriPath(listOfFiles[i].getCanonicalPath());
						sb.setB(bitmap);
						retVal.add(sb);
					}
				}
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}

		}
		return retVal;
	}

	public static boolean deleteFileFromSDCard(URI uri) {
		URI fileURI = URI.create("file://" + uri.getPath());
		File file = new File(fileURI);
		boolean fileDeleted = file.delete();
		BBYLog.i(TAG, "File deleted success?  " + fileDeleted);
		return fileDeleted;
	}

	public static boolean deleteFileFromSDCard(String path) {
		URI fileURI = URI.create("file://" + path);
		File file = new File(fileURI);
		boolean fileDeleted = file.delete();
		BBYLog.i(TAG, "File deleted success?  " + fileDeleted);
		return fileDeleted;
	}
	
	public static Bitmap getBitmapImageOffThread(String url) {
		try {
			HttpGet httpRequest = new HttpGet(url);
			HttpClient httpClient = new DefaultHttpClient();

			HttpResponse response = (HttpResponse) httpClient.execute(httpRequest);
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			InputStream instream = bufHttpEntity.getContent();
			return BitmapFactory.decodeStream(instream);
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
		}

		return BitmapFactory.decodeResource(_applicationContext.getResources(), R.drawable.comingsoonoff);
	}
}