package com.bestbuy.android.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class StartIntents {

	public static final String telephoneURI = "tel:";

	public static void MakeCall(Context context, String phoneNumber) {
		Uri dataString = Uri.parse(telephoneURI + phoneNumber);
		Intent callIntent = new Intent(Intent.ACTION_DIAL, dataString);
		callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(callIntent);
	}

	public static void GoToMapLocation(Context context, String location) {
		String mapURL = "geo:" + 0 + "," + 0 + "?q=" + location;
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mapURL)));
	}
}
