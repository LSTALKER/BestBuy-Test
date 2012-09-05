package com.bestbuy.android.hem.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.hem.library.dataobject.BrandFinder;


public class ApplianceListAdapter extends ArrayAdapter<BrandFinder>{
	private LayoutInflater inflater;
	private List<BrandFinder> items;
	public ApplianceListAdapter(Context context, List<BrandFinder> items) {
		super(context, R.layout.local_rebate_finder_appliancelist, (ArrayList<BrandFinder>) items);
		this.items = items;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		if (items != null) {
			return items.size();
		}
		return 0;
	}

	public static class ViewHolder{
        public TextView applianceChecked;
    }
	
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		ViewHolder holder;
	   
		if(convertView == null){
            customView = inflater.inflate(R.layout.local_rebate_finder_applianceitem, null);
            holder=new ViewHolder();
            holder.applianceChecked = (TextView) customView.findViewById(R.id.rebate_finder_appliances);
            customView.setTag(holder);
        } else
            holder=(ViewHolder)customView.getTag();
	    
       BrandFinder items = getItem(position);
		
       holder.applianceChecked.setText(textFormatting(items.getProductType()));
       
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
       
		return customView;
	}

	public String textFormatting(String applianceName) {
		String responseString = "";

		if (applianceName.equals("CLOTHESWASHER")) {
			responseString = "CLOTHES WASHER";
		} else if (applianceName.equals("DISHWASHER")) {
			responseString = "DISH WASHER";
		} else if (applianceName.equals("ROOMAC")) {
			responseString = "ROOM A/C";
		} else if (applianceName.equals("REFRIGERATOR")) {
			responseString = "REFRIGERATOR";
		} else if (applianceName.equals("FREEZER")) {
			responseString = "FREEZER";
		}

		return responseString;

	}

}
