package com.bestbuy.android.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SavedBitmap;
import com.bestbuy.android.util.BBYAlertDialog;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.ShareProduct;

/**
 * @Author Recursive Awesome
 * @Email Ryan@recursiveawesome.com
 * @Edited Lalit Kumar Sahoo
 */

public class PhotoBrowser extends MenuActivity {

	private static final String TAG = "PhotoBrowser.java";
	/* Extras */
	public static final String SKU_EXTRA = "SKUEXTRA";
	public static final String PRODUCT_TITLE = "PRODUCTTITLE";
	public static final String URL_LIST = "URLLIST";
	public static final String TEMP_SKU = "TEMP_DIRECTORY";
	/* End Extras */

	private final int CAMERA_ACTIVITY = 55;
	private final int SELECT_PICTURE = 56;
	private final int IMAGE_WIDTH_PORTRAIT = 310;
	private final int IMAGE_HEIGHT_PORTRAIT = 400;

	/** The size of the widest or tallest dimension of the image */
	private static final int SCALE_SIZE = 400;

	private float scale;
	private int imageWidth = 0;
	private int imageHeight = 0;
	private int selectedItemId;

	private Product product;
	private ArrayList<SavedBitmap> bitmaps;
	private Gallery gallery;
	private Context context;
	private ImageAdapter _imageAdapter;
	private File file;
	private ImageView shareButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_browser);

		context = this;
		product = appData.getSelectedProduct();
		if (product != null) {
			scale = context.getResources().getDisplayMetrics().density;
			TextView title = (TextView) findViewById(R.id.photo_browser_title);
			title.setText(product.getName());

			imageWidth = (int) (IMAGE_WIDTH_PORTRAIT * scale);
			imageHeight = (int) (IMAGE_HEIGHT_PORTRAIT * scale);

			new ImageLoadTask(this).execute();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageDeleteTask imageDeleteTask = new ImageDeleteTask();
		imageDeleteTask.execute();
	}

	private void setUpButtons() {
		shareButton = (ImageView) findViewById(R.id.photo_browser_share_button);
		shareButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (selectedItemId > -1) {
					new ShareProduct(PhotoBrowser.this, product).execute();
				}
			}
		});
	}

	private void setUpGallery() {
		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int id, long arg3) {
				selectedItemId = id;
				if (bitmaps.get(selectedItemId).isHelper()) {
					shareButton.setVisibility(View.INVISIBLE);
				}else {
					shareButton.setVisibility(View.VISIBLE);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				selectedItemId = -1;
			}
		});
		gallery.setBackgroundColor(Color.WHITE);
		_imageAdapter = new ImageAdapter();
		gallery.setAdapter(_imageAdapter);
	}

	public class ImageAdapter extends BaseAdapter {

		public ImageAdapter() {

		}

		public int getCount() {
			return bitmaps.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(context);

			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			i.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.FILL_PARENT));
			i.setPadding(0, 0, 0, 0);
			i.setMaxWidth((int) (280 * scale));

			i.setLayoutParams(new Gallery.LayoutParams(imageWidth, imageHeight));
			if (bitmaps.get(position).getB() != null) {
				i.setImageBitmap(bitmaps.get(position).getB());
			} else {
				ImageProvider.getBitmapImageOnThread(bitmaps.get(position).getUrl(), i);
			}
			return i;
		}
	}

	private class ImageDeleteTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				boolean successfulDelete = ImageProvider.deleteFileDirectory(TEMP_SKU);
				BBYLog.i(TAG, "Images Successfully Deleted On Exit: " + successfulDelete);
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	private class ImageLoadTask extends BBYAsyncTask {
		public ImageLoadTask(Activity activity) {
			super(activity, "Loading...");;
		}

		@Override
		public void doTask() throws Exception {
			bitmaps = new ArrayList<SavedBitmap>();
			ArrayList<SavedBitmap> savedBitmaps = ImageProvider.pullImageDirectoryFromSDCard(product.getSku(), SCALE_SIZE, SCALE_SIZE);
			for (String url : product.getAllImages()) {
				if (url != null && url.length() > 0) {
					SavedBitmap sb = new SavedBitmap();
					sb.setUrl(url);
					bitmaps.add(sb);
				}
			}

			if (savedBitmaps != null) {
				bitmaps.addAll(savedBitmaps);
			}
		}

		@Override
		public void doFinish() {
			if (!isFinishing()) {
				setUpGallery();
				setUpButtons();
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_CANCELED) {
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("value", EventsLogging.CUSTOM_CLICK_USE_IMAGE_VIEWER_PHOTO_EVENT);
			EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
			
			BBYLog.i(TAG, "Images - onActivityResult");
			if (requestCode == CAMERA_ACTIVITY) {
				try {
					Bitmap photoTaken = ImageProvider.scaleBitmap(file, SCALE_SIZE, SCALE_SIZE); // scale
					if (photoTaken != null) {
						// Rotate photoTaken
						float degrees = ImageProvider.getExifRotationDegrees(file);
						Matrix rotationMatrix = new Matrix();
						rotationMatrix.setRotate(degrees);
						photoTaken = Bitmap.createBitmap(photoTaken, 0, 0, photoTaken.getWidth(), photoTaken.getHeight(), rotationMatrix,
								false);

						BBYLog.i(TAG, "Full res image taken");
						boolean successfulDeleteOfFile = file.delete();
						BBYLog.i(TAG, "Deleted High Res Photo: " + successfulDeleteOfFile);

						String newPath = ImageProvider.cacheBitmapToSD(product.getSku(), null, photoTaken, true);
						BBYLog.i(TAG, "Saved Low Res Photo");
						handleReturnedImage(newPath, photoTaken);
						file = null;
					}
				} catch (Exception e) {
					BBYLog.printStackTrace(TAG, e);
				}
			} else if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();

				if (selectedImageUri == null) {
					Toast.makeText(context, "An error has occured in image selection", Toast.LENGTH_LONG);
				} else {
					Bitmap unscaledBitmap = null;
					try {
						unscaledBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
						if (unscaledBitmap != null) {
							Bitmap scaledBitmap = ImageProvider.scaleBitmap(unscaledBitmap, SCALE_SIZE, SCALE_SIZE);
							if (scaledBitmap != null) {
								String newPath = ImageProvider.cacheBitmapToSD(product.getSku(), null, scaledBitmap, true);
								handleReturnedImage(newPath, scaledBitmap);
							}
						}
					} catch (FileNotFoundException e) {
						BBYLog.printStackTrace(TAG, e);
						Toast.makeText(context, "An error has occured in image selection", Toast.LENGTH_LONG);

					}
				}
			}
		}
	}

	private void handleReturnedImage(String path, Bitmap bitmap) {

		SavedBitmap sb = new SavedBitmap();
		sb.setB(bitmap);
		sb.setUriPath(path);
		sb.setIsHelper(false);
		bitmaps.add(sb);

		removeHelper();
		_imageAdapter.notifyDataSetChanged();
		gallery.setSelection(bitmaps.size(), true);

		shareButton.setVisibility(View.VISIBLE);
	}

	private void removeHelper() {
		for (int i = 0; i < bitmaps.size(); i++) {
			if (bitmaps.get(i).isHelper()) {
				bitmaps.remove(i);
			}
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return dialog;
		}
		
		return new BBYAlertDialog(this).createAlertDialog(id);
	}
}
