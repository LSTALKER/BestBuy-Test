/***
Portions (c) 2008-2009 CommonsWare, LLC
Portions (c) 2009 Google, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may
not use this file except in compliance with the License. You may obtain
a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.bestbuy.android.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;

abstract public class EndlessAdapter extends AdapterWrapper implements OnLoadCancelListener {
	public final String TAG = this.getClass().getName();

	/**
	 * Override this to load the next page of results in the background.
	 */
	abstract protected boolean fetchNextPage() throws Exception;

	/**
	 * Override this to add the next page of results in the main UI thread.
	 */
	abstract protected void appendNextPage();

	private View pendingView = null;
	private AtomicBoolean keepOnAppending = new AtomicBoolean(true);
	private int pendingResource = -1;
	private int currentPage = 0;
	private Context context = null;
	private int previousCount = 0;
	private List<View> headerViews;
	private List<View> footerViews;
	private AppendTask appendTask;
	
	/**
	 * Constructor wrapping a supplied ListAdapter. Make sure to override
	 * getView in the adapter passed in here, and NOT the getView in
	 * EndlessAdapter.
	 * 
	 * @param context
	 * @param wrapped
	 */
	public EndlessAdapter(Context context, ListAdapter wrapped) {
		super(wrapped);
		this.context = context;
		this.pendingResource = R.layout.list_pagination_load;
		this.headerViews = new ArrayList<View>();
		this.footerViews = new ArrayList<View>();
	}

	@Override
	public int getCount() {
		if (keepOnAppending.get()) {
			return (super.getCount() + getHeaderCount() +getFooterCount() + 1); // one more for "pending"
		}
		return (super.getCount() + getHeaderCount()+getFooterCount());
	}

	public int getItemViewType(int position) {
		if (position == getCount()) {
			return (IGNORE_ITEM_VIEW_TYPE);
		}
		return (super.getItemViewType(position));
	}

	public int getViewTypeCount() {
		return (super.getViewTypeCount() + getHeaderCount() + 1);
	}

	private int getHeaderCount() {
		return headerViews.size();
	}

	public void addHeaderView(View v) {
		v.setTag("header");
		headerViews.add(v);
	}
	
	private int getFooterCount() {
		return footerViews.size();
	}
	
	public void addFooterView(View v) {
		v.setTag("footer");
		footerViews.add(v);
	}

	/**
	 * Handles the loading of pending rows, header rows, and adapter rows.
	 * Typically you will not override this method, and instead override the
	 * getView method in the adapter that was supplied to the constructor.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		boolean isPendingView = false;
		boolean isHeaderView = false;
		boolean isFooterView = false;
		final int adjPosition = position - getHeaderCount();

		// Figure out if this row is a header view or loading view
		if (convertView != null && convertView.getTag() != null) {
			isPendingView = ((String) convertView.getTag()).equals("pending");
			isHeaderView = ((String) convertView.getTag()).equals("header");
			isFooterView = ((String) convertView.getTag()).equals("footer");
		}

		// DEBUG: logging
//		BBYLog.d(TAG, "---------------------------------------");
//		BBYLog.d(TAG, "position: " + position);
//		BBYLog.d(TAG, "adjusted position: " + adjPosition);
//		BBYLog.d(TAG, "getCount(): " + getCount());
//		BBYLog.d(TAG, "getHeaderCount(): " + getHeaderCount());

		// Fix an issue with the loading view and header views coming back from
		// the dead
		if (isPendingView && position != getCount()) {
			convertView = null;
		}
		if (isHeaderView && position >= getHeaderCount()) {
			convertView = null;
		}
		if (isFooterView && position <= (getCount() - getFooterCount())) { //TODO: >= or >?
			convertView = null;
			}

		// Return the header views if we are at the beginning of the list
		if (position < getHeaderCount()) {
			return headerViews.get(position);
		}
		
		// If we are at the end of the list, show the loading row and load in the background
		if (position == getCount() - getFooterCount() - 1 && keepOnAppending.get()) {
			if (pendingView == null) {
				BBYLog.d(TAG, "Returning pendingView");
				pendingView = getPendingView(parent);
				previousCount = getCount();
				appendTask = new AppendTask();
				appendTask.execute();
			}
			BBYLog.d(TAG, "returning pendingView");
			return (pendingView);
		}

		//If we are at the very end, return the footer views
		if (position >= getCount() - getFooterCount()) {
			BBYLog.d(TAG, "returning footerView");
			return footerViews.get(position - (getCount() - getFooterCount()));
		}
		
		return (super.getView(adjPosition, convertView, parent));
	}

	/**
	 * Called when an exception occurs while loading the next page.
	 * 
	 * @param e
	 */
	protected void onException(Exception e) {
		BBYLog.e("EndlessAdapter", "Exception in cacheInBackground()", e);
		BBYLog.printStackTrace(TAG, e);
		
		if(e instanceof IllegalArgumentException || e instanceof APIRequestException) {
			appendNextPage();
			keepOnAppending.set(false);
			pendingView = null;
			notifyDataSetChanged();
			return;
		}
		
		NoConnectivityExtension.noConnectivity(getContext(), new OnReconnect() {
			public void onReconnect() {
				currentPage--;
				appendTask = new AppendTask();
				appendTask.execute();
				keepOnAppending.set(true);
			}
		}, new OnCancel() {
			public void onCancel() {
				keepOnAppending.set(false);
				pendingView = null;
				notifyDataSetChanged();
				onCancelLoading();
			}
		});
	}

	private class AppendTask extends AsyncTask<Void, Void, Exception> {
		@Override
		protected Exception doInBackground(Void... params) {
			Exception result = null;
			try {
				keepOnAppending.set(fetchNextPage());
			} catch (Exception e) {
				result = e;
			}
			return (result);
		}

		@Override
		protected void onPostExecute(Exception e) {
			if (e == null) {
				appendNextPage();
				pendingView = null;
				notifyDataSetChanged();
				if (getCount() == previousCount) {
					// Nothing new loaded, stop here.
					keepOnAppending.set(false);
				}
			} else {
				onException(e);
				return;
			}
		}

		@Override
		protected void onPreExecute() {
			currentPage++;
		}
	}

	/**
	 * Returns the loading row.
	 * 
	 * @param parent
	 * @return
	 */
	protected View getPendingView(ViewGroup parent) {
		if (context != null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pendingView = inflater.inflate(pendingResource, parent, false);
			pendingView.setTag("pending");
			if (super.getCount() == 0) {
				((TextView) pendingView.findViewById(R.id.last_row_description)).setText("Loading results...");
			}
			return pendingView;
		}
		throw new RuntimeException("You must either override getPendingView() or supply a pending View resource via the constructor");
	}

	protected int getCurrentPage() {
		return currentPage;
	}

	public Context getContext() {
		return context;
	}
	
	public void cancelLoading() {
		appendTask.cancel(true);
	}
	
	public void onCancelLoading() {
		
	}
}

interface OnLoadCancelListener {
	public void onCancelLoading();
}