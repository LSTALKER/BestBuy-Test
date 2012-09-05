package com.bestbuy.android.hem.activity.helper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.hem.activity.LocalRebates;
import com.bestbuy.android.hem.library.dataobject.EcoRebatesResponseDetails;
import com.bestbuy.android.hem.library.dataobject.Product;
import com.bestbuy.android.hem.library.dataobject.ProductRebateDetails;
import com.bestbuy.android.hem.library.util.Util;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.UTFCharacters.UTF;

public class RebateFinderListAdapter extends ArrayAdapter<ProductRebateDetails>{
	private LayoutInflater inflater;
	private List<ProductRebateDetails> finder;
	private Context mContext;
	private EcoRebatesResponseDetails ecoRebatesResponseDetails;
	
	public RebateFinderListAdapter(Context context,List<ProductRebateDetails> finder) {
		super(context, R.layout.local_rebate_finder_productlist, (ArrayList<ProductRebateDetails>) finder);
		
		mContext = context;
		if(finder==null) {
			finder = new ArrayList<ProductRebateDetails>();
		}		
		this.finder = finder;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {		
		return this.finder.size();
	}
	
	public static class ViewHolder {
		private ImageView product_item;
		private TextView productTitle, totalRebates, totalAmount;
    }
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		final ViewHolder holder;
		
	    if(convertView == null){
	    	customView = inflater.inflate(R.layout.local_rebate_finder_productitem, null);
            holder = new ViewHolder();
            holder.product_item = (ImageView) customView.findViewById(R.id.product_item);
            holder.productTitle = (TextView) customView.findViewById(R.id.product_item_title);
            holder.totalRebates = (TextView) customView.findViewById(R.id.product_rebate);
            holder.totalAmount = (TextView) customView.findViewById(R.id.product_amount);
            
            customView.setTag(holder);            
        } else {
            holder = (ViewHolder)customView.getTag();
        }
	    
	    ProductRebateDetails pDetails = getItem(position);
		 
		holder.productTitle.setText((UTF.replaceNonUTFCharacters(pDetails.getProduct().getShortName())));
		
		int programs = pDetails.getRebatePrograms().size();
		String totalRebates = "";
		if(programs>1) {
			totalRebates = programs + " rebates up to ";
		} else {
			totalRebates = programs + " rebate up to ";
		}
		
		totalRebates = totalRebates + pDetails.getMaxRebateAmountLabel();
		holder.totalRebates.setText(totalRebates);
		holder.totalAmount.setText("$" + Util.formatNumber2fractions(pDetails.getProduct().getPrice()));

		customView.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Intent i = new Intent(mContext, LocalRebates.class);
				Product product = finder.get(position).getProduct();		
				i.putExtra(LocalRebates.PRODUCT_NAME, product.getShortName());
				i.putExtra(LocalRebates.PRODUCT_LIST_PRICE, product.getPrice());		
				i.putExtra(LocalRebates.PRODUCT_MODEL, product.getMfrSKU());
				i.putExtra(LocalRebates.PRODUCT_SKU, product.getSku());
				i.putExtra(LocalRebates.ECORABTE_RESPONSE_DETAILS, ecoRebatesResponseDetails);
				i.putExtra(LocalRebates.POSITION, position);
				mContext.startActivity(i);
			}
		});
		
		if (pDetails.getProduct().getImageUrl() != null) {
			ImageProvider.getBitmapImageOnThread(pDetails.getProduct().getImageUrl(), holder.product_item );
		}
		
		return customView;
	}
	
	public void setEcoRebatesResponseDetails(EcoRebatesResponseDetails ecoRebatesResponseDetails) {
		this.ecoRebatesResponseDetails = ecoRebatesResponseDetails;
	}
}
