package com.bestbuy.android.openbox.activity.helper;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.util.FontLibrary;

public class CategoryListAdapter extends BaseAdapter{

	
	private List<String> openBoxItemList;
	private LayoutInflater inflater;
	private Context context;
	public CategoryListAdapter(Context context, List<String> openBoxItems) {
		this.openBoxItemList = openBoxItems;
		this.context=context;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/*for(OpenBoxItemsCollection i: openBoxItemList){
			System.out.println("Category: " + i.getCategory()+ "ItemCount: " + i.getItemsCount());
		}*/
	}

	public int getCount() {
		if (openBoxItemList != null) {
			return openBoxItemList.size();
		}
		return 0;
	}
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getItem(int position) {
		return openBoxItemList.get(position);
	}
	
	public static class ViewHolder {
		private TextView  categoryLabel;
    }
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		final ViewHolder holder;
		
	    if(convertView == null){
	    	customView = inflater.inflate(R.layout.openbox_category_count_list, null);
            holder = new ViewHolder();
           
            holder.categoryLabel = (TextView) customView.findViewById(R.id.category_name);
            Typeface tf = FontLibrary.getFont(R.string.droidsansbold, context.getResources());	  
            holder.categoryLabel.setTypeface(tf);
            customView.setTag(holder);
            
        } else
            holder = (ViewHolder)customView.getTag();
	    
	   // OpenBoxItemsCollection openBoxitemsCollection = getItem(position);
		
		
		holder.categoryLabel.setText(getItem(position));
		
		return customView;
	}

	
	

}
