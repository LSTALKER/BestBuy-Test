package com.bestbuy.android.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Config;

import com.bestbuy.android.util.BBYLog;

/**  
 * Location Listener class informs on location changed.
 * It uses two location providers for retrieving the location,
 * viz "GPS provider" and second one is "Network provider" also known as "Assistant GPS"
 * 
 * The GPS provider gives fine accuracy but constrained to clear sky, 
 * If device is not under clear sky the LocationManager uses "Network provider"
 * Which is based on cell tower.
 * 
 * @author Jalandar
 */
public class MyLocationManager {
	
	private static final String TAG = "MyLocationManager";
	
    private Context mContext = null;
    private LocationManager mLocationManager = null;
    
    public MyLocationManager(Context context)
    {
    	this.mContext = context;
    	
    	if(this.mContext != null)
    	{
    		this.mLocationManager = (LocationManager) this.mContext.getSystemService(Context.LOCATION_SERVICE);
    	}
    }
	
	LocationListener [] mLocationListeners = new LocationListener[] 
	{
	    new LocationListener(LocationManager.GPS_PROVIDER),
	    new LocationListener(LocationManager.NETWORK_PROVIDER)
	};
	
	private class LocationListener implements android.location.LocationListener 
	{
        Location mLastLocation;
        boolean mValid = false;
        String mProvider;

        public LocationListener(String provider)
        {
            mProvider = provider;
            mLastLocation = new Location(mProvider);
        }

        public void onLocationChanged(Location newLocation)
        {
        	  if (newLocation != null)
              {     
	        	if (newLocation.getLatitude() == 0.0 && newLocation.getLongitude() == 0.0) 
	            {
	                // Hack to filter out 0.0,0.0 locations
	        		BBYLog.v(TAG, "Hack to filter out 0.0,0.0 locations");
	                return;
	            }            
                 	
    			if(newLocation.getTime() == 0) 
    				newLocation.setTime(System.currentTimeMillis()); 
            	
    			BBYLog.v(TAG, "Latitude: " + newLocation.getLatitude() + " Longitude: " + newLocation.getLongitude());
    			
            	if(Config.DEBUG)
            		BBYLog.i(TAG, "onLocationChanged in loc mgnr");	
            }            
            mLastLocation.set(newLocation);
            mValid = true;
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) 
        {
            mValid = false;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) 
        {
            if (status == LocationProvider.OUT_OF_SERVICE) 
            {
                mValid = false;
            }
        }

        public Location current() 
        {
            return mValid ? mLastLocation : null;
        }
    };
    
    public void startLocationReceiving() 
    {
        if (this.mLocationManager != null) 
        {
            try 
            {
                this.mLocationManager.requestLocationUpdates
                (
                        LocationManager.NETWORK_PROVIDER,
                        0, // milli second
                        0F,
                        this.mLocationListeners[1]);
                
                this.mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0, // milli second
                        0F,
                        this.mLocationListeners[0]);
            } 
            catch (java.lang.SecurityException ex) 
            {
            	if(Config.DEBUG)
            	{
            		BBYLog.e(TAG, "SecurityException " + ex.getMessage());
				}
            } 
        }
    }
    
    public void stopLocationReceiving() 
    {
        if (this.mLocationManager != null) 
        {
            for (int i = 0; i < this.mLocationListeners.length; i++)
            {
                try 
                {                	
                    this.mLocationManager.removeUpdates(mLocationListeners[i]);               
                    
                } 
                catch (Exception ex) 
                {
                	BBYLog.v(TAG, "Error in stopLocationReceiving "+ex.getMessage());
                }
            }
        }
    }
    
    public Location getCurrentLocation() 
    {
        Location l = null;

        // go in best to worst order
        for (int i = 0; i < this.mLocationListeners.length; i++) 
        {
            l = this.mLocationListeners[i].current();
            if (l != null)
                break;
        }      
        
        if(l==null) {
        	// Try for last known location
        	l = getLastKnownLocation();
        }
        // check for best location
        BBYLog.i(TAG, "getCurrentLocation():(l==null)="+(l==null));
        /*if(l!=null) {
        	if(isBetterLocation(l, getCurrentBestLocation())) {
        		// new location is best
        		setCurrentBestLocation(l);
        		BBYLog.i(TAG, "getCurrentLocation(): new location is best");
        	} else {
        		l = getCurrentBestLocation();
        		BBYLog.i(TAG, "getCurrentLocation(): current best location is best");
        	}
        }*/
        return l;
    }
    
    public Location getLastKnownLocation()
    {
    	Location l = null;
    	
    	if(this.mLocationManager!=null)
    	{
    		try
    		{
    			l = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		}
    		catch(SecurityException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "getLastKnownLocation(): " + e.toString());
    		}
    		catch(IllegalArgumentException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "getLastKnownLocation(): " + e.toString());
    		}
    		
    		if(l==null)
    		{
    			try
    			{
    				l = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    			}
    			catch(SecurityException e)
        		{
        			if(Config.DEBUG)
        				BBYLog.e(TAG, "getLastKnownLocation(): " + e.toString());
        		}
        		catch(IllegalArgumentException e)
        		{
        			if(Config.DEBUG)
        				BBYLog.e(TAG, "getLastKnownLocation(): " + e.toString());
        		}
    		}
    	}
    	BBYLog.v(TAG, "getLastKnownLocation obtained - "+l);
    	return l;
    }
    
    public boolean isGPSProviderEnabled()
    {
    	boolean enabled = false;
    	
    	if(this.mLocationManager!=null)
    	{
    		try
    		{
    			enabled = this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		}
    		catch(SecurityException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "isGPSProviderEnabled(): " + e.toString());
    		}
    		catch(IllegalArgumentException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "isGPSProviderEnabled(): " + e.toString());
    		}
    	}
    	
    	return enabled;
    }
    
    public boolean isNetworkProviderEnabled()
    {
    	boolean enabled = false;
    	
    	if(this.mLocationManager!=null)
    	{
    		try
    		{
    			enabled = this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    		}
    		catch(SecurityException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "isNetworkProviderEnabled(): " + e.toString());
    		}
    		catch(IllegalArgumentException e)
    		{
    			if(Config.DEBUG)
    				BBYLog.e(TAG, "isNetworkProviderEnabled(): " + e.toString());
    		}
    	}
    	
    	return enabled;
    }
    
    /**
     * Returns true if any one is enabled else false
     * @return
     */
    public boolean areLocationProvidersEnabled()
    {
    	boolean enabled = false;
    	
    	enabled = isGPSProviderEnabled() | isNetworkProviderEnabled();
    	BBYLog.i(TAG, "areLocationProvidersEnabled(): enabled="+enabled);
    	BBYLog.i(TAG, "areLocationProvidersEnabled(): isGPSProviderEnabled()="+isGPSProviderEnabled());
    	BBYLog.i(TAG, "areLocationProvidersEnabled(): isNetworkProviderEnabled()="+isNetworkProviderEnabled());
    	return enabled;
    }
    
    
    // Start: get current best location
    private static Location CURRENT_BEST_LOCATION = null;
	public static Location getCurrentBestLocation() {
		if(CURRENT_BEST_LOCATION!=null) {
			BBYLog.i(TAG, "getCurrentBestLocation(): latitude=" + CURRENT_BEST_LOCATION.getLatitude());
			BBYLog.i(TAG, "getCurrentBestLocation(): longitude=" + CURRENT_BEST_LOCATION.getLongitude());
		}
		return CURRENT_BEST_LOCATION;
	}
	public static void setCurrentBestLocation(Location location) {
		CURRENT_BEST_LOCATION = location;
		if(CURRENT_BEST_LOCATION!=null) {
			BBYLog.i(TAG, "setCurrentBestLocation(): latitude=" + CURRENT_BEST_LOCATION.getLatitude());
			BBYLog.i(TAG, "setCurrentBestLocation(): longitude=" + CURRENT_BEST_LOCATION.getLongitude());
		}
	}
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
      * @param location  The new Location that you want to evaluate
      * @param currentBestLocation  The current Location fix, to which you want to compare the new one
      */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
        	BBYLog.i(TAG, "isBetterLocation():isSignificantlyNewer="+isSignificantlyNewer);
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
        	BBYLog.i(TAG, "isBetterLocation():isSignificantlyOlder="+isSignificantlyOlder);
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
        	BBYLog.i(TAG, "isBetterLocation():isMoreAccurate="+isMoreAccurate);
            return true;
        } else if (isNewer && !isLessAccurate) {
        	BBYLog.i(TAG, "isBetterLocation():(isNewer && !isLessAccurate)="+(isNewer && !isLessAccurate));
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
        	BBYLog.i(TAG, "isBetterLocation():(isNewer && !isSignificantlyLessAccurate && isFromSameProvider)="
        			+(isNewer && !isSignificantlyLessAccurate && isFromSameProvider));
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    // End: get current best location
} 
