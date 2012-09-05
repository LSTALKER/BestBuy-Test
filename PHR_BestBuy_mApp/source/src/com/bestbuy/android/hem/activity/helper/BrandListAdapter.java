package com.bestbuy.android.hem.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.bestbuy.android.R;
import com.bestbuy.android.hem.library.dataobject.Brands;

public class BrandListAdapter extends ArrayAdapter<Brands> {
	private LayoutInflater inflater;
	private List<Brands> items;

	public BrandListAdapter(Context context, List<Brands> items) {
		super(context, R.layout.local_rebate_finder_brandlist, (ArrayList<Brands>) items);
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		if (items != null) {
			return items.size();
		}
		return 0;
	}

	public static class ViewHolder {
		public CheckedTextView brandChecked;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		ViewHolder holder;

		if (convertView == null) {
			customView = inflater.inflate(R.layout.local_rebate_finder_branditem, null);
			holder = new ViewHolder();
			holder.brandChecked = (CheckedTextView) customView.findViewById(R.id.brand_ChkTextView);
			customView.setTag(holder);
		} else
			holder = (ViewHolder) customView.getTag();

		Brands brand = getItem(position);

		holder.brandChecked.setText(brand.getName());
		
		if (getCount() == 1) {
			customView.setBackgroundResource(R.drawable.commerce_box);
		} else {
			if (position == 0) {
				customView.setBackgroundResource(R.drawable.commerce_box_top);
			} else if (position == (getCount() - 1)) {
				customView.setBackgroundResource(R.drawable.commerce_box_bottom);
			} else {
				customView.setBackgroundResource(R.drawable.commerce_box_middle);
			}
		}
		
		customView.setPadding(21, 16, 24, 16);
		
		return customView;
	}
}
