package com.bestbuy.android.storeevent.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuMapActivity;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.storeevent.activity.helper.map.StoreOverlay;
import com.bestbuy.android.storeevent.activity.helper.route.RouteMapOverlay;
import com.bestbuy.android.storeevent.activity.helper.route.RouteRoad;
import com.bestbuy.android.storeevent.data.StoreMapData;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.ui.NoConnectivityExtension;
import com.bestbuy.android.ui.NoConnectivityExtension.OnCancel;
import com.bestbuy.android.ui.NoConnectivityExtension.OnReconnect;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.StoreLocator;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

/**
 * Displays a map showing the location of the selected store
 * @author Recursive Awesome
 * @Edited Lalit Kumar Sahoo
 */

public class StoreLocatorMapActivity extends MenuMapActivity {

	private FrameLayout frame;
	private MapView mapView;
	private MapController controller;
	private RouteMapOverlay routeMapOverlay;
	private RouteRoad mRoad;
	
	private LocationManager locationManager;
	private MyLocationOverlay overlay;
	
	private AppData appData;
	private Store store;
	private String from;
	private String to;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stev_locator_map);
		
		appData = ((BestBuyApplication)this.getApplication()).getAppData();
		locationManager = appData.getBBYLocationManager().getLocationManager(StoreLocatorMapActivity.this);
		
		store = (Store) getIntent().getExtras().get(StoreUtils.STORE_INFO);
		
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		AppData.setLocation(location);
		
		initRoute();
		initLocationUpdates();
		initMapView();
		initZoomControls();
		initMyLocation();
	}
	
	/**
	 * Initialize the road route between source and destination.
	 * If there is no road route is available between the source and destination 
	 * then the route is not drawn on the map else a custom line draw between source and destination.
	 */
	private void initRoute() {
		
		LocationManager locationManager = appData.getBBYLocationManager().getLocationManager(StoreLocatorMapActivity.this);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	if(location != null) {
    		AppData.setLocation(location);
			
    		from = location.getLatitude() + "," + location.getLongitude();
    		to = store.getLat() + "," + store.getLng();
    		new RouteMapTask(this).execute();
    		
    	} else
    		Toast.makeText(StoreLocatorMapActivity.this, getResources().getString(R.string.UNABLE_TO_ACCESS_LOCATION), Toast.LENGTH_LONG).show();
	}
	
	private class RouteMapTask extends BBYAsyncTask {
		
		public RouteMapTask(Activity activity) {
			super(activity, "Locating Route...");
		}
		
		@Override
		public void doFinish() {
			showView();
		}

		@Override
		public void doError() {
			if(noConnectivity) {
				NoConnectivityExtension.noConnectivity(activity, new OnReconnect() {
					public void onReconnect() {
						new RouteMapTask(activity).execute();
					}		
				}, new OnCancel() {
					
					public void onCancel() {
						finish();
					}
				});
			} else
				showView();
		}
		
		@Override
		public void doTask() throws Exception {
			mRoad = StoreMapData.getMapConnection(from, to);
		}
	}

	private void showView() {
		if(mRoad != null) {
			if(mRoad.mRoute != null) {
				if(mRoad.mRoute.length != 0)
					initMyLocation();
				else
					showDialog(StoreUtils.NO_ROAD_ROUTE_AVAILABLE_DIALOG);
			} else
				showDialog(StoreUtils.NO_ROAD_ROUTE_AVAILABLE_DIALOG);
		} else
			showDialog(StoreUtils.NO_ROAD_ROUTE_AVAILABLE_DIALOG);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(onLocationChange);
	}
	
	public void onResume() {
		super.onResume();
		initLocationUpdates();
		if(overlay != null)
			overlay.enableMyLocation();
	}
	
	public void onPause() {
		super.onPause();
		locationManager.removeUpdates(onLocationChange);
		overlay.disableMyLocation();
	}
	
	private void initLocationUpdates() {
		StoreLocator.setLocationUpdates(locationManager, onLocationChange);
	}
	
	/** Find and initialize the map view. */
	private void initMapView() {
		frame = (FrameLayout) findViewById(R.id.store_locator_frame);
		mapView = (MapView) findViewById(R.id.store_locator_map);
		controller = mapView.getController();
		mapView.setSatellite(false);
	}

	/** Get the zoom controls and add them to the bottom of the map. */
	private void initZoomControls() {
		@SuppressWarnings("deprecation")
		View zoomControls = mapView.getZoomControls();
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL);
		frame.addView(zoomControls, p);
	}

	/** Start tracking the position on the map. */
	private void initMyLocation() {
		
		controller.setZoom(14);
		Location myLocation = AppData.getLocation();
		if (myLocation != null) {
			controller.animateTo(StoreLocator.getPoint(myLocation.getLatitude(), myLocation.getLongitude()));
		} else {
			controller.animateTo(StoreLocator.getPoint(store.getLat(), store.getLng()));
		}
		
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		
		overlay = new MyLocationOverlay(this, mapView);
		overlay.enableMyLocation();
		
		Drawable marker=getResources().getDrawable(R.drawable.redpin);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
		StoreOverlay storeOverlay = new StoreOverlay(marker, mapView, store, true);
		storeOverlay.drawStores(store, true);
		
		listOfOverlays.add(storeOverlay);
		listOfOverlays.add(overlay);
		
		if(mRoad != null) {
			routeMapOverlay = new RouteMapOverlay(mRoad, mapView);
			listOfOverlays.add(routeMapOverlay);
		}
		
		mapView.postInvalidate();
		
		storeOverlay.showStore(0);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	LocationListener onLocationChange = new LocationListener() {
		public void onLocationChanged(Location location) {
			AppData.setLocation(location);
		}

		public void onProviderDisabled(String provider) { }

		public void onProviderEnabled(String provider) { }

		public void onStatusChanged(String provider, int status, Bundle extras) { }
	};

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		AlertDialog alertDialog = null;
		
		switch (id) {
			case StoreUtils.PROGRESS_DIALOG:
				ProgressDialog progressDialog = (ProgressDialog) dialog;
				progressDialog.setMessage("Locating Route...");
				break;
				
			case StoreUtils.NO_ROAD_ROUTE_AVAILABLE_DIALOG:
				if(this==null || this.isFinishing()) { 
					return;
				}
				alertDialog = (AlertDialog) dialog;
				alertDialog.setTitle("");
				alertDialog.setMessage(getResources().getString(R.string.NO_ROAD_ROUTE_AVAILABLE));
				break;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(isFinishing()) return null;
		
		Dialog dialog = null;
		
		switch (id) {
			case StoreUtils.NO_ROAD_ROUTE_AVAILABLE_DIALOG:
				dialog = new AlertDialog.Builder(this)
						.setCancelable(true)
						.setTitle("")
						.setMessage(getResources().getString(R.string.NO_ROAD_ROUTE_AVAILABLE))
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
		}
		
		return dialog;
	}
}
