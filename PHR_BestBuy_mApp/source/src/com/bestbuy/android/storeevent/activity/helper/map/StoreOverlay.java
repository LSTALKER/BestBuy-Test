package com.bestbuy.android.storeevent.activity.helper.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Store;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * Draws store locations on the map
 * @author Recursive Awesome
 *
 */
public class StoreOverlay extends StoreLocatorBalloonItemizedOverlay<OverlayItem>{

	private List<OverlayItem> items=new ArrayList<OverlayItem>(); 
	
	public StoreOverlay(Drawable defaultMarker, MapView mapView, Store store, boolean isInteractable) {
		super(boundCenter(defaultMarker), mapView, store, isInteractable);
		mapView.getContext();
	}
	
	public void showStore(int index) {
		this.onTap(index);
	}
	
	@Override
	protected boolean onBalloonTap(int index) {
		super.onBalloonTap(index);
		return true;
	}
	
	public void drawStores(Store store, boolean isInteractable) {
		items.add(new OverlayItem(getPoint(store.getLat(), store.getLng()), store.getLongName(), store.getAddress()));
		
		if(isInteractable) {
			Location location = AppData.getLocation();
			
			if(location != null) {
				items.add(new OverlayItem(getPoint(location.getLatitude(), location.getLongitude()), "Current Location", "Current Location"));
			}
		}
		
		populate();
	}
	
	public void drawStores(List<Store> stores) {
		for(int i=0; i< stores.size(); i++) {
			Store store = stores.get(i);
			items.add(new OverlayItem(getPoint(store.getLat(), store.getLng()), store.getLongName(), store.getAddress()));
		}
		
		Location location = AppData.getLocation();
		
		if(location != null)
			items.add(new OverlayItem(getPoint(location.getLatitude(), location.getLongitude()), "Current Location", "Current Location"));
		
		populate();
	}
	  
	@Override
	protected OverlayItem createItem(int i) {
		return(items.get(i)); 
	}
	
	public void draw(Canvas canvas, MapView mapView,  boolean shadow) { 
	    super.draw(canvas, mapView, false); //turn off the shadow and use the image shadow 
	  }

	@Override
	public int size() {
		return items.size();
	}
	
	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat*1000000.0), (int)(lon*1000000.0)));
	}	

}
