package com.bestbuy.android.storeevent.activity.helper.map;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public abstract class StoreLocatorBalloonItemizedOverlay<Item> extends ItemizedOverlay<OverlayItem> {

	private static final String TAG = "StoreLocatorBalloonItemizedOverlay.java";
	private MapView mapView;
	private StoreLocatorBalloonOverlay balloonView;
	private View clickRegion;
	private int viewOffset;
	final MapController mc;
	
	private Store store;
	
	public StoreLocatorBalloonItemizedOverlay(Drawable defaultMarker, MapView mapView, Store store, boolean isInteractable) {
		super(defaultMarker);
		this.store = store;
		this.mapView = mapView;
		viewOffset = 25;
		mc = mapView.getController();
	}
	
	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	
	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index) {
		ImageButton mapDirections = (ImageButton) balloonView.findViewById(R.id.balloon_map_directions);
		if(index == 1)
			mapDirections.setVisibility(View.GONE);
		else
			mapDirections.setVisibility(View.VISIBLE);
		
		mapDirections.setTag(Integer.valueOf(index));
		mapDirections.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				double storeLat = 0;
				double storeLng = 0;
				
				storeLat = store.getLat();
				storeLng = store.getLng();
				
				Location currentLocation = AppData.getLocation();
				
				if (currentLocation != null) {
					double currentLat = currentLocation.getLatitude();
					double currentLng = currentLocation.getLongitude();
					v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.getStoreLocatorMapURL()+"?f=d&saddr=" + currentLat + "," + currentLng + "&daddr=" + storeLat + "," + storeLng)));
				} else {
					v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.getStoreLocatorMapURL()+"?f=d&daddr=" + storeLat + "," + storeLng)));
				}
			}
		});
		
		ImageButton mapCallStore = (ImageButton) balloonView.findViewById(R.id.balloon_map_callstore);
		if(index == 1)
			mapCallStore.setVisibility(View.GONE);
		else
			mapCallStore.setVisibility(View.VISIBLE);
		
		mapCallStore.setTag(Integer.valueOf(index));
		mapCallStore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + store.getPhone()));
		        v.getContext().startActivity(dialIntent);
			}
		});
		
		return true;
	}

	@Override
	protected final boolean onTap(int index) {
		
		boolean isRecycled;
		final int thisIndex;
		GeoPoint point;
		
		thisIndex = index;
		point = createItem(index).getPoint();
		
		if (balloonView == null) {
			balloonView = new StoreLocatorBalloonOverlay(mapView.getContext(), viewOffset);
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			isRecycled = false;
		} else {
			isRecycled = true;
			// close down the buttons so they have to click on the view to expand it.
			ImageButton mapDirections = (ImageButton) balloonView.findViewById(R.id.balloon_map_directions);
			mapDirections.setVisibility(View.GONE);
			ImageButton mapCallStore = (ImageButton) balloonView.findViewById(R.id.balloon_map_callstore);
			mapCallStore.setVisibility(View.GONE);
		}
	
		balloonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}
		
		balloonView.setData(createItem(index));
		
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		setBalloonTouchListener(thisIndex);
		
		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}
		
		mc.animateTo(point);
		
		return true;
	}
	
	/**
	 * Sets the visibility of this overlay's balloon view to GONE. 
	 */
	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof StoreLocatorBalloonItemizedOverlay && overlay != this) {
				((StoreLocatorBalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}
		
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden onBalloonTap if implemented.
	 * 
	 * @param thisIndex - The index of the item whose balloon is tapped.
	 */
	private void setBalloonTouchListener(final int thisIndex) {
		
		try {
			clickRegion.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					
					View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
					Drawable d = l.getBackground();
					
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						int[] states = {android.R.attr.state_pressed};
						if (d.setState(states)) {
							d.invalidateSelf();
						}
						return true;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						int newStates[] = {};
						if (d.setState(newStates)) {
							d.invalidateSelf();
						}
						// call overridden method
						onBalloonTap(thisIndex);
						return true;
					} else {
						return false;
					}
					
				}
			});
			
		} catch (SecurityException e) {
			BBYLog.e("BalloonItemizedOverlay", "setBalloonTouchListener reflection SecurityException");
			BBYLog.printStackTrace(TAG, e);
			return;
		} 
	}
}
