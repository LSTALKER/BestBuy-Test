package com.bestbuy.android.appolicious.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.appolicious.util.BaseConnectionManager;
import com.bestbuy.android.ui.BBYProgressDialog;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.AppoliciousWebView;
import com.bestbuy.android.util.AuthServer;
import com.bestbuy.android.util.BBYAsyncTask;

public class AppoliciousCategoriesList extends MenuActivity {

	/**
	 * NEW IMPLEMENTATION
	 */
	private AppoliciousWebView appoliciousWebView;
	private WebView webview;
	private String url;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.appolicious_categories_list);

		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(AppConfig.getAppCenterHost()).append(getString(R.string.APPOLICIOUS_URL));
		url = urlBuffer.toString();

		appoliciousWebView = new AppoliciousWebView();

		// Check for device Internet Connectivity
		isNetAvailable();
	}

	private void isNetAvailable() {
		new Handler().postDelayed(new Runnable() {
			public void run() {	
				if(!BaseConnectionManager.isNetAvailable(AppoliciousCategoriesList.this) || BaseConnectionManager.isAirplaneMode(AppoliciousCategoriesList.this)) {
					NoConnectivityExtension.noConnectivity(AppoliciousCategoriesList.this, new OnReconnect() {
						public void onReconnect() {
							isNetAvailable();
						}		
					}, new OnCancel() {

						public void onCancel() {
							finish();
						}
					});
				} else{
					isServerAvailable();
				}
			}
		}, 0);
	}

	private void isServerAvailable() {
		new AuthServerTask(AppoliciousCategoriesList.this).execute();
	}

	class AuthServerTask extends BBYAsyncTask {
		boolean serverStatus = false;

		public AuthServerTask(Activity activity) {
			super(activity, "Connecting...");
		}

		@Override
		public void doTask() throws Exception {
			serverStatus = AuthServer.authanticateAppCenterServer(url);
		}

		@Override
		public void doError() {
			NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {

				public void onReconnect() {
					new AuthServerTask(activity).execute();
				}
			}, new OnCancel() {

				public void onCancel() {
					finish();
				}
			});
		}

		@Override
		public void doFinish() {
			if (serverStatus) {
				webview = appoliciousWebView.showWebView(url, activity);
			} else {
				doError();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if (webview != null && webview.canGoBack()) {
			if(webview.getUrl().contains("pageHistory=0"))
				super.onBackPressed();
			else {
				webview.goBack();
			}
		} else {
			super.onBackPressed();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (this == null || this.isFinishing())
			return null;
		
		return new BBYProgressDialog(this);
	}

	/**
	 * OLD IMPLEMENTATION
	 */
	public static String APPOLICIOUSID = "IdAppolicious";
	public static String GETRECENTLYADDAPPS = "recentlyAddBtn";
	public static String GETRECENTLYMENUTITLE = "recentlyMenuTitle";
	public static String KEYWORDSEARCH = "keyword";
	public static String GETCATEGORIESNAME = "categoriesName";
	public static final String PLEASE_CHECK_THE_INTERNET_CONNECTION = "Please check the internet connection";
	public static final String PLEASE_CHECK_THE_AIRPLANE_MODE = "No network connectivity";
	public static String FILE_NAME = "/data/data/com.bestbuy.android/search_history.obj";
	public static String FAILURE = "Failure";
	public static String SEARCH_FAILURE = "Search Failure";

	/*
	private static final Void Void = null;
	private ListView listview;
	private TextView headearAppolicious, categoriesName;
	private EditText et;
	public static String APPOLICIOUSID = "IdAppolicious";
	private ArrayList<Categories> categoriesList;
	private ArrayList<ApplicationInfo> hotAppsList;
	private ArrayList<ApplicationInfo> recommendationsList;
	private String categoryId;
	private String categoryName;
	private String keyWord;
	public static String FAILURE = "Failure";
	private int listPosition = 0;
	private ArrayList<Categories> recommendationAndHotApp;
	private ImageView iv;
	private Button searchButton;
	private boolean isSeachHistory = false;
	private int positionSearchHistory = 0;
	private LinearLayout layout;
	private Button chartButton, homeButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appolicious_categories_list);
		headearAppolicious = (TextView) findViewById(R.id.header_appociolus_list_view);
		listview           = (ListView) findViewById(R.id.appolicious_listview);
		et				   = (EditText) this.findViewById(R.id.home_search);
		iv     			   = (ImageView) this.findViewById(R.id.search_icon);
		searchButton       = (Button) findViewById(R.id.home_search_button);
		layout			   = (LinearLayout) findViewById(R.id.linear_blue_shirt);
		categoriesName	   = (TextView) findViewById(R.id.categories_name);
		chartButton        = (Button) findViewById(R.id.chart_button_category);
		homeButton		   = (Button) findViewById(R.id.home_button_category);

		categoriesName.setTextColor(Color.WHITE);
		categoriesName.setText("Blue Shirt Recommendations");

		homeButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent home = new Intent(AppoliciousCategoriesList.this, Home.class);
				startActivity(home);
			}
		});

		chartButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent chart = new Intent(AppoliciousCategoriesList.this, CartList.class);
				startActivity(chart);
			}
		});


		et.setVisibility(View.VISIBLE);

		layout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				blueShirt();
			}
		});

		iv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyWord = et.getText().toString().trim();
				searchAppolicious();
			}
		});

		et.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == event.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN){
					keyWord = et.getText().toString().trim();
					searchAppolicious();
				}
				return false;
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				isSeachHistory = true;
				et.setVisibility(View.VISIBLE);
				iv.setVisibility(View.VISIBLE);
				listview.setVisibility(View.VISIBLE);
				searchButton.setVisibility(View.GONE);
				layout.setVisibility(View.GONE);

				et.setFocusable(true);
				et.setFocusableInTouchMode(true);
				et.requestFocus();

				InputMethodManager imm = (InputMethodManager)getSystemService(AppoliciousCategoriesList.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(et.getId(), InputMethodManager.SHOW_FORCED);

				if(loadHistoryFromDisk().size() != 0){
					SearchHistoryAppoliciousAdapter adapter = new SearchHistoryAppoliciousAdapter(AppoliciousCategoriesList.this, loadHistoryFromDisk());
					listview.setAdapter(adapter);
				}else{
					listview.setVisibility(View.GONE);
				}

				listview.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,	int position, long arg3) {
						keyWord = loadHistoryFromDisk().get(position);
						positionSearchHistory = position;
						searchAppolicious();
					}
				});


			}
		});

		headearAppolicious.setText("Apps Zone");
	}


	@Override
	protected void onResume() {
		super.onResume();

		if(loadHistoryFromDisk().size() > 20){
			ArrayList<String> clearSearchHistory = new ArrayList<String>();
			saveHistoryToDisk(clearSearchHistory);
		}

		et.setVisibility(View.GONE);
		iv.setVisibility(View.GONE);
		searchButton.setVisibility(View.VISIBLE);
		listview.setVisibility(View.GONE);
		layout.setVisibility(View.VISIBLE);

		isSeachHistory = false;

		if(!BaseConnectionManager.isNetAvailable(this)) {
			noConnectionMessage(PLEASE_CHECK_THE_INTERNET_CONNECTION);
			return;
		}
		et.setText("");
		executeTask3();
	}

	private void searchAppolicious(){
		InputMethodManager mgr = (InputMethodManager) getSystemService(AppoliciousCategoriesList.this.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(et.getWindowToken(), 0);
		if(keyWord != null && !keyWord.equalsIgnoreCase("")){

			if(!BaseConnectionManager.isNetAvailable(this)) {
				noConnectionMessage(PLEASE_CHECK_THE_INTERNET_CONNECTION);
				return;
			}
			executeTaskSearch();
		}else{
			showNotification(SEARCH_FAILURE);
		}
	}

	private void blueShirt() {
		new Task8(this).execute(Void, Void, Void);
	}

	private class Task8 extends BBYAsyncTask {

		public Task8(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			recommendationsList = new ArrayList<ApplicationInfo>();
		}

		@Override
		public void doFinish() {
			displayBlueShirt();
		}

		@Override
		public void doReconnect() {
			new Task8(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {
			recommendationsList = (ArrayList<ApplicationInfo>)AppBlueshirtsLogic.getTopBlueShirtRecommends();
		}
	}

	private void displayBlueShirt() {
		removeDialog(Utility.PROGRESS_DIALOG);

		if(recommendationsList == null){
			showNotification(FAILURE);
			return;
		}else{
			Intent intent = new Intent(AppoliciousCategoriesList.this, AppoliciousListView.class);
			intent.putExtra(GETRECENTLYMENUTITLE, "blueshirt");
			intent.putExtra(GETRECENTLYADDAPPS, recommendationsList);
			startActivity(intent);
		}
	}

	private SearchResult searchResultList;
	private void executeTaskSearch() {
		new TaskSearch(this).execute(Void, Void, Void);
	}

	private class TaskSearch extends BBYAsyncTask {

		public TaskSearch(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			searchResultList = new SearchResult();
		}

		@Override
		public void doFinish() {
			displayOutputTaskSearch();
		}

		@Override
		public void doReconnect() {
			new TaskSearch(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {			 
			searchResultList = SearchResultLogic.getSearchResult(keyWord);
		}
	}

	private void displayOutputTaskSearch() {
		findViewById(R.id.category_review_header).setFocusable(true);
		findViewById(R.id.category_review_header).setFocusableInTouchMode(true);
		removeDialog(Utility.PROGRESS_DIALOG);

		if(searchResultList == null){
			showNotification(FAILURE);
			return;
		}else{
			System.out.println("COUNT : " + searchResultList.getResultsCount());

			if(searchResultList.getResultsCount() != 0){
				String searchKey = keyWord.toLowerCase();
				boolean isNotduplicateSearchKey = true;

				for(int i=0; i<loadHistoryFromDisk().size(); i++){
					if(searchKey.equalsIgnoreCase(loadHistoryFromDisk().get(i))){
						isNotduplicateSearchKey = false;
					}
				}

				if(loadHistoryFromDisk().size() > 0){
					if(searchKey.equalsIgnoreCase(loadHistoryFromDisk().get(positionSearchHistory))){
						isNotduplicateSearchKey = false;
					}
				}

				if(isNotduplicateSearchKey){
					ArrayList<String> searchModelList = loadHistoryFromDisk();
					searchModelList.add(0,keyWord);
					saveHistoryToDisk(searchModelList);
				}

				ArrayList<ApplicationInfo> applications = (ArrayList<ApplicationInfo>) searchResultList.getAppolApplications();

				Intent showListViewAppolicious = new Intent(this, AppoliciousListView.class);
				showListViewAppolicious.putExtra(GETRECENTLYADDAPPS, applications);
				showListViewAppolicious.putExtra(GETRECENTLYMENUTITLE, "searchResult");
				showListViewAppolicious.putExtra(KEYWORDSEARCH, keyWord);
				startActivity(showListViewAppolicious);
			}else{
				showNotification("No Result Found");
			}
		}
	}


	private void executeTask3() {
		new Task3(this).execute(Void, Void, Void);
	}

	private class Task3 extends BBYAsyncTask {

		public Task3(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			categoriesList = new ArrayList<Categories>();
		}

		@Override
		public void doFinish() {
			removeDialog(Utility.PROGRESS_DIALOG);
			if(categoriesList != null){
				getRecentlyAddApps();
			}else{
				showRecommendationAndHotAppOnly();
			}
		}

		@Override
		public void doReconnect() {
			new Task3(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {
			categoriesList = (ArrayList<Categories>) CategoriesLogic.getCategories();
		}
	}

	private void hotApp() {
		new Task4(this).execute(Void, Void, Void);
	}

	private class Task4 extends BBYAsyncTask {

		public Task4(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			hotAppsList = new ArrayList<ApplicationInfo>();
		}

		@Override
		public void doFinish() {
			displayHotApp();
		}

		@Override
		public void doReconnect() {
			new Task4(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {
			hotAppsList = (ArrayList<ApplicationInfo>) ApplicationInfoLogic.getHotApps();
		}
	}

	private void displayHotApp() {
		removeDialog(Utility.PROGRESS_DIALOG);

		if(hotAppsList == null){
			showNotification(FAILURE);
			return;
		}else{
			Intent showListViewAppolicious = new Intent(this, AppoliciousListView.class);
			showListViewAppolicious.putExtra(GETRECENTLYADDAPPS, hotAppsList);
			showListViewAppolicious.putExtra(GETRECENTLYMENUTITLE, "hotApp");
			startActivity(showListViewAppolicious);

		}
	}

	private void recommendation() {
		new Task7(this).execute(Void, Void, Void);
	}

	private class Task7 extends BBYAsyncTask {

		public Task7(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			recommendationsList = new ArrayList<ApplicationInfo>();
		}

		@Override
		public void doFinish() {
			displayRecommendation();
		}

		@Override
		public void doReconnect() {
			new Task7(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {
			recommendationsList = (ArrayList<ApplicationInfo>)ApplicationInfoLogic.getRecommendations();
		}
	}


	private void displayRecommendation() {
		removeDialog(Utility.PROGRESS_DIALOG);

		if(recommendationsList == null){
			showNotification(FAILURE);
			return;
		}else{
			Intent showListViewAppolicious = new Intent(this, AppoliciousListView.class);
			showListViewAppolicious.putExtra(GETRECENTLYADDAPPS, recommendationsList);
			showListViewAppolicious.putExtra(GETRECENTLYMENUTITLE, "recommendation");
			startActivity(showListViewAppolicious);
		}
	}

	private void categories() {
		new TaskCategories(this).execute(Void, Void, Void);
	}

	private class TaskCategories extends BBYAsyncTask {

		public TaskCategories(Activity activity) {
			super(activity);
		}

		@Override
		protected void onPreExecute() {
			showDialog(Utility.PROGRESS_DIALOG);
			recommendationsList = new ArrayList<ApplicationInfo>();
		}

		@Override
		public void doFinish() {
			displayCategories();
		}

		@Override
		public void doReconnect() {
			new TaskCategories(AppoliciousCategoriesList.this).execute();
		}

		@Override
		public void doTask() {
			recommendationsList = (ArrayList<ApplicationInfo>)ApplicationInfoLogic.getCategoryId(categoryId);
		}
	}

	private void displayCategories() {
		removeDialog(Utility.PROGRESS_DIALOG);

		if(recommendationsList == null){
			showNotification(FAILURE);
			return;
		}else{
			Intent showListViewAppolicious = new Intent(this, AppoliciousListView.class);
			showListViewAppolicious.putExtra(GETRECENTLYADDAPPS, recommendationsList);
			showListViewAppolicious.putExtra(GETRECENTLYMENUTITLE, "categories");
			showListViewAppolicious.putExtra(GETCATEGORIESNAME, categoryName);
			startActivity(showListViewAppolicious);
		}
	}

	private void getRecentlyAddApps(){
		listview.setVisibility(View.VISIBLE);

		Categories recom = new Categories();
		recom.setName("Recommendations");
		Categories hotApp = new Categories();
		hotApp.setName("Hot Apps");
		categoriesList.add(0, recom);
		categoriesList.add(1, hotApp);

		AppoliciousCategoriesAdapter adapter = new AppoliciousCategoriesAdapter(this, categoriesList);
		listview.setAdapter(adapter);

		if(listPosition > 4){
			listview.setSelection((listPosition-2));
		}

		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if(!BaseConnectionManager.isNetAvailable(AppoliciousCategoriesList.this)) {
					noConnectionMessage(PLEASE_CHECK_THE_INTERNET_CONNECTION);
					return;
				}

				listPosition = position;
				if (position == 0){
					recommendation();
				}
				else if(position == 1){
					hotApp();
				}else{
					categoryId = categoriesList.get(position).getId()+"";
					categoryName = categoriesList.get(position).getName();
					categories();
				}

			}
		});
	}


	private void showRecommendationAndHotAppOnly(){
		listview.setVisibility(View.VISIBLE);

		recommendationAndHotApp = new ArrayList<Categories>();
		Categories recom = new Categories();
		recom.setName("Recommendations");
		Categories hotApp = new Categories();
		hotApp.setName("Hot Apps");
		recommendationAndHotApp.add(0, recom);
		recommendationAndHotApp.add(1, hotApp);

		AppoliciousCategoriesAdapter adapter = new AppoliciousCategoriesAdapter(this, recommendationAndHotApp);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				if(!BaseConnectionManager.isNetAvailable(AppoliciousCategoriesList.this)) {
					noConnectionMessage(PLEASE_CHECK_THE_INTERNET_CONNECTION);
					return;
				}

				if (position == 0){
					recommendation();
				}
				else if(position == 1){
					hotApp();
				}
			}
		});
	}

	private void showNotification(String msg) {
		try{
			if(this == null || this.isFinishing()) { // Use ||, Do not use | 
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert");
			String message = "Unable to get app list";
			if(msg.equalsIgnoreCase(SEARCH_FAILURE)){
				message = "Please enter search key word";
			}else if(msg.equalsIgnoreCase("No Result Found")){
				message = "No matching app found";
			}

			builder.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return dialog;
		}
		switch (id) {
		case Utility.PROGRESS_DIALOG:
			ProgressDialog progressDialog = new AppoliciousDialog(this);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.setMessage("Loading contents...");
			return progressDialog;

		}
		return dialog;
	}

	public class AppoliciousDialog extends ProgressDialog{
		private Activity activity;

		public AppoliciousDialog(Activity activity) {
			super(activity);
			this.activity = activity;
		}

		@Override
		public void onBackPressed() {
			super.onBackPressed();
			activity.finish();
		}
	}

	private void noConnectionMessage(String msg) {
		try{
			if(this == null || this.isFinishing()) { // Use ||, Do not use | 
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Alert");
			builder.setMessage(msg)
			.setCancelable(false)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					et.setText("");
				}
			});

			AlertDialog alert = builder.create();
			alert.show();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static ArrayList<String> loadHistoryFromDisk(){
		ObjectStreamUtilities<ArrayList<String> > objectStream = new ObjectStreamUtilities<ArrayList<String>>();
		ArrayList<String> searchModelList = objectStream.readObject(FILE_NAME);
		if(searchModelList == null) {
			return new  ArrayList<String>();
		} else {
			return searchModelList;
		}
	}

	public void saveHistoryToDisk(ArrayList<String> searchModelList) {
		ObjectStreamUtilities<ArrayList<String> > objectStream = new ObjectStreamUtilities<ArrayList<String>>();
		objectStream.writeObject(searchModelList, FILE_NAME);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == event.KEYCODE_BACK){
			if(isSeachHistory){
				Intent intent = new Intent(AppoliciousCategoriesList.this, AppoliciousCategoriesList.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}else{
				finish();
				Intent intent = new Intent(AppoliciousCategoriesList.this, Home.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		}
		return false;
	}
<<<<<<< HEAD
} */
}	
