package com.bestbuy.android.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.data.SearchRequest;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZParser;
import com.bestbuy.android.rewardzone.RZProduct;
import com.bestbuy.android.rewardzone.RZReceipt;
import com.bestbuy.android.rewardzone.RZTransaction;
import com.bestbuy.android.rewardzone.RZTransactionLineItem;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.CacheManager;
import com.bestbuy.android.util.Diagnostics;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.InputStreamExtensions;
import com.bestbuy.android.util.URLManager;

/**
 * Displays a list of reward zone purchases made. Shows receipts and products.
 * 
 * @author Recursive Awesome
 * 
 */
// public class RewardZonePurchases extends MenuActivity implements
// OnScrollListener {
public class RewardZonePurchases extends MenuActivity {

	private String TAG = this.getClass().getName();
	private RZAccount rzAccount;
	private boolean reauthenticate = false;
	PurchasesAdapter purchasesAdapter;
	int counter;
	int sortingPosition;
	int startPoint;
	View loadingView;
	boolean currentlyRunning;
	ArrayList<RZTransaction> transactionList;
	ArrayList<RZProduct> rzProductList = new ArrayList<RZProduct>();
	ArrayList<RZTransaction> productSortedList = new ArrayList<RZTransaction>();

	private Context _context;
	LoadProductsTask loadProductsTask;
	Calendar currentCalendar;
	String fromDate;
	String toDate;
	ListView lv;
	boolean stop = false;
	boolean backgroundLoad;
	boolean firstThrough = true;
	boolean results;
	boolean searchKeyword = false;
	ArrayList<RZTransaction> searchList;
	private boolean loadMoreClicked = false;
	private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
	private EditText searchPurchases;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this;

		transactionList = new ArrayList<RZTransaction>();
		
		showPurchasesView();
		setupPurchasesSearch();
		setupPurchasesSorting();

	}

	public void showPurchasesView() {
		setContentView(R.layout.reward_zone_purchases);
		lv = (ListView) findViewById(R.id.reward_zone_purchases_list);
		rzAccount = appData.getRzAccount();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_PURCHASES_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay()); 
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);

		
		executeLoadProductsTask();
	}

	private void removeFooter() {
		int footers = lv.getFooterViewsCount();
		if (footers == 1) {
			if (loadingView instanceof View) {
				lv.removeFooterView(loadingView);
			}
		}
	}

	public void loadFooter() {
		removeFooter();

		if (results) {

			LayoutInflater inflater = getLayoutInflater();
			loadingView = inflater.inflate(R.layout.reward_zone_click_row, null, true);
			lv.addFooterView(loadingView);

			loadingView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					isNetAvailable();
				}
			});
		} else { // no results found
			LayoutInflater inflater = getLayoutInflater();
			loadingView = inflater.inflate(R.layout.reward_zone_click_row, null);
			TextView text = (TextView) loadingView.findViewById(R.id.reward_zone_row);
			text.setText("No Purchase History Available.");
			View footerDivider = (View) loadingView.findViewById(R.id.footer_divider);
			footerDivider.setVisibility(View.VISIBLE);
			lv.addFooterView(loadingView, null, false);
		}
	}

	public void setupPurchasesSearch() {
		// TODO: ryan fix this
		searchPurchases = (EditText) findViewById(R.id.generic_search);
		searchPurchases.setHint("Search Purchases");
		// capture the enter key as a submit key.
		searchPurchases.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// hide keyboard and run search
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchPurchases.getWindowToken(), 0);
					String searchString = searchPurchases.getText().toString();
					ListView purchasesList = (ListView) findViewById(R.id.reward_zone_purchases_list);

					removeFooter();
					if (searchString.length() == 0) {
						loadFooter();
						setupAdapterAndNotify(false);
						searchKeyword = false;
					} else {
						productSortedList.clear();
						for (int i = 0; i < rzProductList.size(); i++) {
							RZProduct rzProduct = (RZProduct) rzProductList.get(i);
							if (rzProduct.getName().toLowerCase().contains(searchString.toLowerCase())) {
								productSortedList.add(rzProduct);
							}
						}
						if (productSortedList.isEmpty()) {
							Toast.makeText(_context, "No purchases found.", Toast.LENGTH_LONG).show();
						} 
						purchasesAdapter = new PurchasesAdapter(productSortedList);
						purchasesList.setAdapter(purchasesAdapter);
						searchKeyword = true;
					}
					return true;
				}
				return false;
			}
		});
		
		TextWatcher textWatcher = new TextWatcher(){

			public void afterTextChanged(Editable s) {
				if (searchPurchases.getText().toString().length() == 0) {
					loadFooter();
					setupAdapterAndNotify(false);
					searchKeyword = false;
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}
		};
		searchPurchases.addTextChangedListener(textWatcher);
	}

	private void setupPurchasesSorting() {
		Spinner sortBySelection = (Spinner) findViewById(R.id.searchresult_sortby);
		sortBySelection.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				sortingPosition = position;
				if (!firstThrough) {
					loadFooter();
					setupAdapterAndNotify(true);
				}
				firstThrough = false;
			}

			public void onNothingSelected(AdapterView<?> parent) {
				return;
			}
		});

		ArrayAdapter<CharSequence> adapter;
		adapter = ArrayAdapter.createFromResource(this, R.array.reward_zone_sortby, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortBySelection.setAdapter(adapter);
	}

	private void executeLoadProductsTask() {
		loadProductsTask = new LoadProductsTask(RewardZonePurchases.this,"Loading purchases...");
		loadProductsTask.loadProductsTask();
		loadProductsTask.execute();
	}

	private class PurchasesAdapter extends ArrayAdapter<RZTransaction> {
		ArrayList<RZTransaction> transactionList;
		private final LayoutInflater _inflater;

		PurchasesAdapter(ArrayList<RZTransaction> transList) {
			super(RewardZonePurchases.this, R.layout.reward_zone_purchases_row, transList);

			_inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.transactionList = transList;
		}

		public class ViewHolder {
			public TextView name;
			public TextView description;
			public TextView amount;
			public ImageView productImage;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			System.gc();
			
			ViewHolder holder;
			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.reward_zone_purchases_row, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.reward_zone_purchase_name);
				holder.description = (TextView) convertView.findViewById(R.id.reward_zone_purchase_description);
				holder.amount = (TextView) convertView.findViewById(R.id.reward_zone_purchase_amount);
				holder.productImage = (ImageView) convertView.findViewById(R.id.reward_zone_purchase_image);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			RZTransaction transaction = transactionList.get(position);

			holder.name.setText(transaction.getName());
			holder.description.setText(transaction.getFirstDetail());
			holder.amount.setText(transaction.getSecondDetail());
			
			if (transaction instanceof RZReceipt) {
				holder.productImage.setImageResource(R.drawable.rzreceipt);
			} else if (transaction instanceof RZProduct) {
				String url = ((RZProduct) transaction).getImageUrl();
				if (url != null) {
					ImageProvider.getBitmapImageOnThread(url, holder.productImage);
				}
			}
			return convertView;
		}
	}

	private void setupAdapter(boolean forceAdapterCreate) {

		if (sortingPosition == 0) { // sorting is date
			if (!backgroundLoad || forceAdapterCreate) {
				purchasesAdapter = new PurchasesAdapter(transactionList);
				lv.setAdapter(purchasesAdapter);
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(searchPurchases.getWindowToken(), 0);
						if(!searchKeyword)
							transactionList.get(position).clicked(RewardZonePurchases.this);
						else
							productSortedList.get(position).clicked(RewardZonePurchases.this);
					}
				});
			}

		} else { // sorting is other than date
			if (sortingPosition == 1) {
				Collections.sort(rzProductList, RZProduct.ORDER_NAME);
			} else if (sortingPosition == 2) {
				Collections.sort(rzProductList, RZProduct.ORDER_PRICE);
			}

			searchList = new ArrayList<RZTransaction>();
			for (int i = 0; i < rzProductList.size(); i++) {
				searchList.add((RZTransaction) rzProductList.get(i));
			}
			purchasesAdapter = new PurchasesAdapter(searchList);
			lv.setAdapter(purchasesAdapter);
			lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchPurchases.getWindowToken(), 0);
					searchList.get(position).clicked(RewardZonePurchases.this);
				}
			});
		}

	}

	private void setupAdapterAndNotify(boolean force) {
		setupAdapter(force);
		purchasesAdapter.notifyDataSetChanged();
	}

	protected void onPause() {
		super.onPause();
		appData.getRzAccount().setRzTransactions(null);
	}

	private class LoadProductsTask extends BBYAsyncTask{

		LoadProductsTask(Activity activity,String message){
			super(activity,message);
		}

		boolean initialCall;
		ArrayList<RZTransaction> tmptransactionList;
		int sizeBefore;

		void loadProductsTask() {

			counter = 0;
			initialCall = true;
			results = false;
			currentCalendar = Calendar.getInstance();
			sizeBefore = 0;
		}

		void loadProductsTask(boolean background) {
			counter = 0;
			backgroundLoad = background;
			initialCall = false;
			results = false;
		}

		private void convertTransactionsToLineItems() throws Exception {
			SearchRequest searchRequest = new SearchRequest();
			rzAccount = appData.getRzAccount();
			ArrayList<RZTransaction> currentRZTransactions = rzAccount.getRzTransactions();
			tmptransactionList = new ArrayList<RZTransaction>();

			for (int i = startPoint; i < currentRZTransactions.size(); i++) {
				List<String> skus = new ArrayList<String>();
				HashMap<String,RZTransactionLineItem> skuRZTransactionMap = new HashMap<String,RZTransactionLineItem>();
				RZTransaction transaction = currentRZTransactions.get(i);
				transaction.setName();
				transaction.setFirstDetail();
				transaction.setSecondDetail();
				tmptransactionList.add(transaction);
				// Add products listed in receipt as sales
				ArrayList<RZTransactionLineItem> lineItems = transaction.getLineItems();
				for (int j = 0; j < lineItems.size(); j++) {
					if (lineItems.get(j).getLineType().equals("SL")) {
						skus.add(lineItems.get(j).getSku());
						skuRZTransactionMap.put(lineItems.get(j).getSku(), lineItems.get(j));
					}
				}
				searchRequest.setPage("1");
				searchRequest.runRemixSkuSearch(skus);
				List<Product> products = searchRequest.getSearchResultList();
				for(Product product : products){
					RZProduct rzProduct = new RZProduct(product,skuRZTransactionMap);
					rzProduct.setPurchaseDate(dateFormat.format(transaction.getDate()));
					tmptransactionList.add(rzProduct);
				}
				startPoint++;
			}
		}

		private boolean anotherAttempt = false;

		private int wsAttempts = -1;
		private static final int LOAD_RZ_ATTEMPTS_ALLOWED = 3;

		protected void loadupTransactions() throws Exception{

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			sizeBefore = appData.getRzAccount().getRzTransactions().size();
 
			if (!anotherAttempt) {

				if (initialCall) { // start with current date
					toDate = df.format(currentCalendar.getTime());
					currentCalendar.add(Calendar.MONTH, -12);
					fromDate = df.format(currentCalendar.getTime());
				} else {
					String pattern = "yyyy-MM-dd";
					toDate = new String(fromDate); // start with last date
					currentCalendar.add(Calendar.MONTH, -12);
					fromDate = df.format(currentCalendar.getTime());
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					try {
						Date date = format.parse(fromDate);
						Calendar current_calendar = Calendar.getInstance();
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						if (current_calendar.get(Calendar.YEAR) - 2 > c.get(Calendar.YEAR)) {
							results = false;
							return;
						}
					} catch (ParseException e) {
						BBYLog.printStackTrace(TAG, e);
					}
				}
			} else {
				BBYLog.i(TAG, "Gateway Exeption or timeout, attempting connection " + wsAttempts);
			}
			if (wsAttempts >= LOAD_RZ_ATTEMPTS_ALLOWED) {
				BBYLog.i(TAG, "Giving up on web service 3 timeouts or gateway exceptions " + wsAttempts);
				results = false;
				return;
			}
			wsAttempts++;
			String url = AppConfig.getSecureRemixURL() + AppData.REWARD_ZONE_PURCHASES_PATH;
			ArrayList<Map.Entry<String, String>> params1 = new ArrayList<Map.Entry<String, String>>();
			params1.add(new OAuth.Parameter("fromDate", fromDate));
			params1.add(new OAuth.Parameter("toDate", toDate));
			List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
			valuePairs.add(new BasicNameValuePair("fromDate", fromDate));
			valuePairs.add(new BasicNameValuePair("toDate", toDate));
			String cacheURL = URLManager.createQueryString(url, valuePairs);
			RZParser rzParser = new RZParser(appData.getRzAccount());
			String data = CacheManager.getCacheItem(cacheURL, CacheManager.RZ_CACHE);
			if (data == null || data.length() <= 0) {
				BBYLog.i(TAG, "Purchases: Doing WS, caching Data");
				OAuthClient oclient = new OAuthClient(new HttpClient4());
				OAuthAccessor access = appData.getOAuthAccessor();
				OAuthMessage msg2 = oclient.invoke(access, url, params1);
				InputStream bodyInputStream = msg2.getBodyAsStream();
				data = InputStreamExtensions.InputStreamToString(bodyInputStream);
				CacheManager.setCacheItem(cacheURL, data, CacheManager.RZ_CACHE);
			} else {
				BBYLog.i(TAG, "Purchases: Using Cached Data");
			}
			
			ByteArrayInputStream bodyConvertInputStream = new ByteArrayInputStream(data.getBytes());
			rzParser.parse(Diagnostics.dumpInputStream(_context, bodyConvertInputStream, TAG, "RZ Purchaces XML: "));

			appData.setRzAccount(rzParser.getRzAccount());
			anotherAttempt = false;
			wsAttempts = -1;
			
			if (anotherAttempt && !reauthenticate) {
				BBYLog.i(TAG, "Gateway Exeption or timeout, attempting connection again");
				loadupTransactions();
			}

			if (appData.getRzAccount().getRzTransactions().size() > sizeBefore) {
				results = true;
			}
		}

		@Override
		public void doTask() throws Exception {
			while (!results && counter < 5) {
				loadupTransactions();
				counter++;
			}
			convertTransactionsToLineItems();
		}

		@Override
		public void doFinish() {
			if (!isFinishing()) {
				// add of the items from the tmp list loaded in the background.
				transactionList.addAll(tmptransactionList);
				loadFooter();
				// Build the list of products only for the sorting of the
				// products.
				rzProductList.clear();
				for (int i = 0; i < transactionList.size(); i++) {
					if (transactionList.get(i) instanceof RZProduct) {
						rzProductList.add((RZProduct) transactionList.get(i));
					}
				}
				setupAdapterAndNotify(false);
					
				currentlyRunning = false; // no longer running
			}
		}
		
		@Override
		public void doReconnect() {
			loadProductsTask = new LoadProductsTask(RewardZonePurchases.this, "Loading purchases...");
			if(loadMoreClicked)
				loadProductsTask.loadProductsTask(true);
			else
				loadProductsTask.loadProductsTask();
			loadProductsTask.execute();
		}
		@Override
		public void doCancelReconnect() {
			super.doCancelReconnect();
			finish();
		}
	}
	
	private void loadMore(){
		currentlyRunning = true;
		lv.removeFooterView(loadingView);
		LayoutInflater inflater = getLayoutInflater();
		loadingView = inflater.inflate(R.layout.reward_zone_row, null);
		lv.addFooterView(loadingView, null, false);
		// int size = loadProductsTask.sizeBefore;
		loadMoreClicked = true;
		loadProductsTask = new LoadProductsTask(RewardZonePurchases.this,"Loading purchases...");
		loadProductsTask.loadProductsTask(false);
		loadProductsTask.execute();
	}
	
	private void isNetAvailable() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (!BaseConnectionManager.isNetAvailable(RewardZonePurchases.this) || BaseConnectionManager.isAirplaneMode(RewardZonePurchases.this)) {
					NoConnectivityExtension.noConnectivity(
							RewardZonePurchases.this, new OnReconnect() {
								public void onReconnect() {
									isNetAvailable();
								}
							}, new OnCancel() {

								public void onCancel() {
									finish();
								}
							});
				} else {
					loadMore();
				}
			}
		}, 0);
	}
}
