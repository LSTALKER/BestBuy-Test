package com.bestbuy.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;

/**
 * Displays information about how to provide feedback for the app.
 * Lets users email Best Buy or call the Best Buy Helpline.
 * @author Recursive Awesome
 *
 */
public class Feedback extends MenuActivity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
		findViewById(R.id.header_cart).setVisibility(View.GONE);
		findViewById(R.id.header_cart_badge).setVisibility(View.GONE); 
        ImageButton feedbackEmailButton = (ImageButton) findViewById(R.id.feedback_launch_email);
        feedbackEmailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	          	/*Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	            emailIntent.setType("text/html");
	            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "bestbuymobileapps@bestbuy.com" } );
	            String packageVersion = AppData.getVersionName(Feedback.class);
	            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Android App Feedback (Version " + packageVersion + ")");
	            v.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
            	*/
            	
            	Intent send = new Intent(Intent.ACTION_SENDTO);
            	String packageVersion = AppData.getVersionName(Feedback.class);
            	String uriText = "mailto:bestbuymobileapps@bestbuy.com" + 
            	          "?subject=Android App Feedback (Version " + packageVersion + ")";
            	uriText = uriText.replace(" ", "%20");
            	Uri uri = Uri.parse(uriText);
            	send.setData(uri);
            	startActivity(Intent.createChooser(send, "Send mail..."));
            }
        });
        
        ImageButton callBestBuyButton = (ImageButton) findViewById(R.id.feedback_call_best_buy);
        callBestBuyButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
				Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://18882378289"));
		        startActivity(dialIntent);
        	}
        });
	}
}
