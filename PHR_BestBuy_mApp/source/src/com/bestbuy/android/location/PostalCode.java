package com.bestbuy.android.location;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.bestbuy.android.util.BBYLog;

public class PostalCode {

	private String TAG = getClass().getSimpleName();
	
	/*
	 for US location(New York)
	getZipCode(): addr=Address[addressLines=[0:"280 Broadway",1:"Manhattan, NY 10007",2:"USA"],feature=280,admin=New York,sub-admin=null,locality=null,thoroughfare=Broadway,postalCode=10007,countryCode=US,countryName=United States,hasLatitude=true,latitude=40.714321,hasLongitude=true,longitude=-74.00579,phone=null,url=null,extras=null]
	getZipCode(): zipCode=10007
	downloadLocalRebates(): pZipCode for current location=10007


	for India location(Chennai)
	getZipCode(): mAddressList.size()=3
	getZipCode(): index=0
	getZipCode(): addr=Address[addressLines=[0:"2nd Main Rd, Krishnaveni Nagar, Manapakkam",1:"Tamil Nadu",2:"India"],feature=2nd Main Rd,admin=Tamil Nadu,sub-admin=Thiruvallur,locality=null,thoroughfare=2nd Main Rd,postalCode=null,countryCode=IN,countryName=India,hasLatitude=true,latitude=13.0240776,hasLongitude=true,longitude=80.1723717,phone=null,url=null,extras=null]
	 */
	
	// Call this method only in thread
	 public String getZipCode(Context context, double latitude, double longitude) {
	    	String zipCode = "";
	    	
	    	BBYLog.i(TAG, "@@@ getZipCode(): latitude="+latitude);
	    	BBYLog.i(TAG, "@@@ getZipCode(): longitude="+longitude);
	    	
	    	BBYLog.d(TAG, "getZipCode(): (latitude==0.0 && longitude==0.0)="+(latitude==0.0 && longitude==0.0));
			if(latitude==0.0 && longitude==0.0) {			
				return null;
			}
			
	    	try {
		    	Geocoder geocoder = new Geocoder(context);    	
		    	List<Address> mAddressList = geocoder.getFromLocation(latitude, longitude, 3); // 2
		    	BBYLog.i(TAG, "getZipCode(): mAddressList.size()="+mAddressList.size());
		    	
		    	int index = 0;
		    	for(Address addr : mAddressList) {
		    		BBYLog.i(TAG, "getZipCode(): index="+index);
		    		BBYLog.i(TAG, "getZipCode(): addr="+addr);
		    		
		    		if(addr!=null) {	    			
		    			zipCode=addr.getPostalCode();	    			
		    			BBYLog.i(TAG, "getZipCode(): zipCode="+zipCode);	    			
		    			break;
		    		}	    		
		    		index++;
		    	}
		    	
		    	// last		    	
		    	return zipCode;
	    	} catch(Exception e) {
	    		BBYLog.e(TAG, "getZipCode(): "+e);
	    		return null;
	    	}
	    }
}
