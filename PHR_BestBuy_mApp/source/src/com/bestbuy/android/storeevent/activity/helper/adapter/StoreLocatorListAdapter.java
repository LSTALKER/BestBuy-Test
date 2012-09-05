package com.bestbuy.android.storeevent.activity.helper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.Store;

/**
 * Custom adapter to add the custom rows to the list.
 * @author lalitkumar_s
 */
public class StoreLocatorListAdapter extends BaseAdapter {
	private List<Store> storeList;
	private LayoutInflater inflater;
	
	public StoreLocatorListAdapter(Context context, List<Store> storeList) {
		this.storeList = storeList;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		if (storeList != null) {
			return storeList.size();
		}
		return 0;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public Store getItem(int position) {
		return storeList.get(position);
	}
	
	public static class ViewHolder {
		private TextView storeLocatorName, storeLocatorDistance, storeLocatorAddress;
    }
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		final ViewHolder holder;
		
	    if(convertView == null){
	    	customView = inflater.inflate(R.layout.stev_locator_row, null);
            holder = new ViewHolder();
            holder.storeLocatorName = (TextView) customView.findViewById(R.id.store_locator_city);
            holder.storeLocatorDistance = (TextView) customView.findViewById(R.id.store_locator_distance);
            holder.storeLocatorAddress = (TextView) customView.findViewById(R.id.store_locator_address);
            
            customView.setTag(holder);
            
        } else
            holder = (ViewHolder)customView.getTag();
	    
		Store store = getItem(position);
		
		holder.storeLocatorName.setText(store.getName());
		holder.storeLocatorDistance.setText(store.getDistance() + " mi");
		holder.storeLocatorAddress.setText(store.getAddress());

		return customView;
	}
}
