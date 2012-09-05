package com.bestbuy.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.PhotoSearch;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;

/**
 * Displays a list of the users's IQEngines searches
 * @author Recursive Awesome
 *
 */
public class PhotoSearchList extends MenuListActivity {

	private final String TAG = this.getClass().getName();

	public PhotoSearchAdapter photoSearchAdapter;
	private int position;
	private IQEnginesTask iQEnginesTask;

	@Override
    public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.iqengines_list);
			findViewById(R.id.header_cart).setVisibility(View.GONE);
			
			if ((iQEnginesTask == null || iQEnginesTask.getStatus() != AsyncTask.Status.RUNNING) && (photoSearchToBeCalled())) {			
				executeIQEnginesTask();
			}
			
			final ImageView photoSearchButton = (ImageView) findViewById(R.id.photo_search_button);
			photoSearchButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (appData.getPhotoSearches().size() < AppData.MAX_PHOTO_SEARCHES) {
						Intent i = new Intent(PhotoSearchList.this, PhotoSearchQuery.class);
						startActivity(i);
						PhotoSearchList.this.finish();
					} else {
						BestBuyApplication.showToastNotification
						("Sorry, you may only save 10 photo searches at a time.  Long press any item to delete it from the list.", PhotoSearchList.this, Toast.LENGTH_LONG);
					}
				}
			});
		
			ListView lv = (ListView) findViewById(android.R.id.list);
			registerForContextMenu(lv);
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					PhotoSearch photoSearch = appData.getPhotoSearches().get(position);
					if (!photoSearch.isAnalyzing()) {
						//SearchRequest searchRequest = new SearchRequest();
						//searchRequest.setFreeText(photoSearch.getDescription());
						//searchRequest.setPage("1");
						//appData.setSearchRequest(searchRequest);
						Intent i = new Intent(PhotoSearchList.this, SearchResultList.class);
						i.putExtra(AppData.PRODUCT_SEARCH_QUERY, photoSearch.getDescription());
						//i.putExtra(StoreUtils.IS_PRODUCT_SEARCH, true);
						startActivity(i);
					} else {
						BestBuyApplication.showToastNotification("You may select this row when it has completed analyzing. Long press to delete.", PhotoSearchList.this, Toast.LENGTH_LONG);
					}
				}
		});

		if (photoSearchAdapter == null) { // share the same adapter across different activities
			photoSearchAdapter = new PhotoSearchAdapter();
		}
		lv.setAdapter(photoSearchAdapter);
		updateAdapterAndView();
	}

	public boolean photoSearchToBeCalled() {
		Iterator<PhotoSearch> iter = appData.getPhotoSearches().iterator();
		while (iter.hasNext()) {
			PhotoSearch ps = iter.next();
			if (ps.isAnalyzing()) { //atleast one is still waiting on response.
				return true;
			}
		}
		return false;
	}

	public void onPause() {
		super.onPause();

		// want to add what is in photoSearches List to latest saved JSON.
		SharedPreferences settings = this.getSharedPreferences(AppData.SHARED_PREFS, 0);
		try {
			JSONArray photoArray = new JSONArray();

			List<PhotoSearch> photoSearches = appData.getPhotoSearches();
			for(int i=0; i<photoSearches.size(); i++) {
				JSONObject photoData = new JSONObject();
				PhotoSearch photoSearch = photoSearches.get(i);
				photoData.put("id", photoSearch.getId());
				photoData.put("qid", photoSearch.getQid());
				photoData.put("file_path", photoSearch.getFile().getName());
				photoData.put("description", photoSearch.getDescription());
				photoArray.put(photoData);
			}
			settings.edit().putString(AppData.PHOTO_SEARCHES, photoArray.toString()).commit();

		} catch (JSONException ex) {
			BBYLog.e(TAG, "Exception persisteing SavedSearches to JSON: " + ex.getMessage());
			BBYLog.printStackTrace(TAG, ex);
		}
	}

	private void deleteFromLocalStorage(PhotoSearch ps) {
		if (ps.getId() != null) {
			if (this.deleteFile(ps.getId())) {
				BBYLog.d(TAG, "Image has been deleted");
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem item = menu.add("Remove item");
		AdapterView.AdapterContextMenuInfo info;
		info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		position = info.position;
		View row = info.targetView;
		TextView productName = (TextView) row.findViewById(R.id.iqengines_list_row_description);
		menu.setHeaderTitle(productName.getText());
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				ListView lv = (ListView) findViewById(android.R.id.list);
				@SuppressWarnings("unchecked")
				ArrayAdapter<PhotoSearch> adapter = (ArrayAdapter<PhotoSearch>) lv.getAdapter();
				if (iQEnginesTask != null) {
					iQEnginesTask.cancel(true);
				}
				PhotoSearch ps = adapter.getItem(position);
				deleteFromLocalStorage(ps); // remove the photo

				adapter.remove(adapter.getItem(position)); // remove the item from the list
				adapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	private Bitmap getPhotoSearchImage(PhotoSearch ps) {
		try {
			/*Removed Unused
			 * File f = this.getFilesDir();*/ 
			FileInputStream fis = this.openFileInput(ps.getId());
			ByteArrayOutputStream bufStream = new ByteArrayOutputStream();
			DataOutputStream outWriter = new DataOutputStream(bufStream);
			int ch;
			while((ch = fis.read()) != -1)
				outWriter.write(ch);

			outWriter.close();
			byte[] data = bufStream.toByteArray();
			bufStream.close();
			fis.close();
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Exception e) {
			BBYLog.printStackTrace(TAG, e);
			BBYLog.e(TAG, "Exception PhotoSearchList.getPhotoSearchImage(). We have not found the image!");
		}
		return null;
	}

	class PhotoSearchAdapter extends ArrayAdapter<PhotoSearch> {
		PhotoSearchAdapter() {
			super(PhotoSearchList.this, R.layout.iqengines_list_row, appData.getPhotoSearches());
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			PhotoSearch photoSearch = appData.getPhotoSearches().get(position);
			View row = convertView;	
			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.iqengines_list_row, parent, false);
			}
			TextView name = (TextView) row.findViewById(R.id.iqengines_list_row_description);
			name.setText(photoSearch.getDescription());
			ImageView icon = (ImageView) row.findViewById(R.id.iqengines_list_row_icon);
			Bitmap bm = null;
			try {
				bm = getPhotoSearchImage(photoSearch);
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
				BBYLog.e(TAG, "Exception getting the image for photo search list: " + ex.getMessage());
			}
			if (bm == null) {
				bm = BitmapFactory.decodeResource(icon.getResources(), R.drawable.comingsoonoff);
			}

			ProgressBar pb = (ProgressBar) row.findViewById(R.id.workingProgress);
			if(!photoSearch.isAnalyzing()) {
				pb.setVisibility(View.INVISIBLE);
			}
			icon.setImageBitmap(bm);
			return row;
		}
	}

	private void executeIQEnginesTask() {
		iQEnginesTask = new IQEnginesTask(PhotoSearchList.this);
		iQEnginesTask.execute();

	}

	public void updateAdapterAndView() {
		if (photoSearchAdapter != null) {
			photoSearchAdapter.notifyDataSetChanged();
		}
	}

	public class IQEnginesTask extends BBYAsyncTask {
		private List<String> labelResults;
		public String qidInProcess; 

		public IQEnginesTask(Activity activity) {
			super(activity);
			enableLoadingDialog(false);
			labelResults = new ArrayList<String>();
		}

		@Override
		public void doTask() throws Exception {
			String qid = null;
			while (photoSearchToBeCalled()) {
				String updateResult = PhotoSearchQuery.iqe.update(AppConfig.getEncryptedDeviceId(), true);
				JSONObject updateObject = new JSONObject(updateResult);
				JSONObject dataObject = updateObject.getJSONObject("data");
				JSONArray resultsObjects = dataObject.getJSONArray("results");

				int resultLength = resultsObjects.length();

				for (int i = 0; i < resultLength; i++) {
					JSONObject quiData = resultsObjects.getJSONObject(i);
					qid = quiData.getString("qid");
					JSONObject qidData = quiData.getJSONObject("qid_data");
					String labels = qidData.getString("labels");
					labelResults.add(labels);
					appData.updatePhotoSearchItem(qid, labels);
				}
			}
		}
		
		@Override
		public void doError() {
			appData.deletePhotoSearchItem(qidInProcess);
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new IQEnginesTask(activity).execute();
					}
				}, new OnCancel() {
					public void onCancel() {
						finish();
					}
				});
			}
		}
		
		@Override
		public void doFinish() {
			//appData.deletePhotoSearchItem(qidInProcess);
			Iterator<String> iter = labelResults.iterator();
			StringBuilder resultString = new StringBuilder(); 
			while(iter.hasNext()) {
				String label = (String)iter.next();
				resultString.append(label);
				if (iter.hasNext()) {
					resultString.append(", ");
				}
			}
			
			CharSequence text = "Found Photo Match! Check your Photo Search List to see the results. Results : " + resultString.toString();
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(PhotoSearchList.this, text, duration);
			toast.show();
			updateAdapterAndView();
		}
	}	
}
