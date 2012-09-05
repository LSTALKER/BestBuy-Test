package com.bestbuy.android.upgradechecker.activity.helper;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.upgradechecker.activity.UpgradeCheckerTermsandConditions;
import com.bestbuy.android.upgradechecker.data.Subscriber;

public class PhoneNumberListAdapter extends BaseAdapter{

	private List<Subscriber> subscriberList;
	private LayoutInflater inflater;
	private Context context;
	
	public PhoneNumberListAdapter(Context context,List<Subscriber> subscriberList){		
		this.context = context;
		this.subscriberList=subscriberList;
		this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public int getCount() {		
		return subscriberList.size();
	}

	public Subscriber getItem(int position) {		
		return subscriberList.get(position);
	}

	public long getItemId(int position) {		
		return 0;
	}

	public static class ViewHolder{
        public TextView phoneNumber;
    }
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		ViewHolder holder;
		
	    if(convertView == null){
	    	customView = inflater.inflate(R.layout.upgrade_checker_phone_list_top_view, null);
            holder=new ViewHolder();
            holder.phoneNumber = (TextView) customView.findViewById(R.id.phone_number);
			
            customView.setTag(holder);
        } else
            holder = (ViewHolder)customView.getTag();
		
	    final Subscriber subscriber = getItem(position);
		String phoneNumber = changeToPhoneFormat(subscriber.getMobilePhoneNumber());	
		
		holder.phoneNumber.setText(phoneNumber);

		if(getCount() == 1) {
	    	customView.setBackgroundResource(R.drawable.commerce_box);
	    } else {
	    	if(position == 0) {
	    		customView.setBackgroundResource(R.drawable.commerce_box_top);
		    } else if(position == (getCount() - 1)) {
		    	customView.setBackgroundResource(R.drawable.commerce_box_bottom);
		    } else {
		    	customView.setBackgroundResource(R.drawable.commerce_box_middle);
		    }
	    } 
		
		customView.setOnClickListener( new OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent(context, UpgradeCheckerTermsandConditions.class);
				intent.putExtra("Subscriber", subscriber);	
				context.startActivity(intent);
			}
		});

		return customView;
	}
	
	/**
	 * Change the phone format to different format 
	 * @param source : phone number string
	 * @return :  Formatted string
	 */
	public static String changeToPhoneFormat(String source) {
		String numberArray[] = source.split("-");
		String dest = "";
		
		dest = "("+ numberArray[0].trim() + ") " + numberArray[1].trim() + "-" + numberArray[2].trim();
		
		return dest;
	}
}
