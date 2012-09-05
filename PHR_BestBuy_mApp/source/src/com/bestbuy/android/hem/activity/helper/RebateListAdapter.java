package com.bestbuy.android.hem.activity.helper;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.hem.library.dataobject.RebateProgram;
import com.bestbuy.android.hem.library.util.Util;
import com.bestbuy.android.util.BBYLog;

public class RebateListAdapter extends BaseAdapter {
	
	private final String TAG = this.getClass().getName();
	private ArrayList<RebateProgram> localRebates;
	private LayoutInflater mInflater;
    public RebateListAdapter(Context context, ArrayList<RebateProgram> localRebates)
    {
        mInflater = LayoutInflater.from(context);
        this.localRebates = localRebates;
        if(this.localRebates==null) {
        	this.localRebates = new ArrayList<RebateProgram>();
        }
        
        mExpanded = new boolean[this.localRebates.size()];
        for(int i=0; i<mExpanded.length; i++) {
        	mExpanded[i] = false;
        }       
    }       
    
    public int getCount() {
        return localRebates.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
    	final ViewHolder holder;
    	RebateProgram rebate = localRebates.get(position);
    	
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.local_rebate_row_detail, null);

        	holder = new ViewHolder();
        	holder.rebateRowPriceTv = (TextView) convertView.findViewById(R.id.rebateRowPriceTv); 
        	        	
            holder.rebateRowProgramTv = (TextView) convertView.findViewById(R.id.rebateRowProgramTv);
            holder.rebateRowAmountTv = (TextView) convertView.findViewById(R.id.rebateRowAmountTv);
            
            holder.rebateRowValidDatesTv1 = (TextView) convertView.findViewById(R.id.rebateRowValidDatesTv1);    
            holder.rebateRowValidDatesTv2 = (TextView) convertView.findViewById(R.id.rebateRowValidDatesTv2);
            holder.rebateRowValidDatesLL1 = (LinearLayout) convertView.findViewById(R.id.rebateRowValidDatesLL1);
            holder.rebateRowValidDatesLL2 = (LinearLayout) convertView.findViewById(R.id.rebateRowValidDatesLL2);
            holder.rebateRowValidDatesChildLL = (LinearLayout) convertView.findViewById(R.id.rebateRowValidDatesChildLL);
            
            holder.rebateRowImpDetailsLL = (LinearLayout) convertView.findViewById(R.id.rebateRowImpDetailsLL);
            holder.rebateRowImpDetailsTitleTv =  (TextView) convertView.findViewById(R.id.rebateRowImpDetailsTitleTv);         
                       
            holder.rebateRowViewMoreTv = (TextView) convertView.findViewById(R.id.rebateRowViewMoreTv);

            convertView.setTag(holder);
        } else {        	
        	holder = (ViewHolder) convertView.getTag();
        	
        }
        holder.setExpanded(mExpanded[position]);        
        
        // Program
        StringBuffer program = new StringBuffer();
        program.append("<b><font color= '#333333'><font size='13'>Program: </font></font></b>");
        program.append("<font color= '#333333'><font size='13'>" + rebate.getName() + "</font></font>");             
        holder.rebateRowProgramTv.setText(Html.fromHtml(program.toString()));
        // Rebate amount
        StringBuffer rebateAmount = new StringBuffer();
        rebateAmount.append("<b><font color= '#333333'><font size='13'>Rebate Amount: </font></font></b>");
        rebateAmount.append("<font color= '#333333'><font size='13'>" + rebate.getAmountLabel() + "</font></font>");             
        holder.rebateRowAmountTv.setText(Html.fromHtml(rebateAmount.toString()));
        
        String[] validDates = rebate.getValidDates();        
            
        try {
        	holder.rebateRowValidDatesTv1.setText(validDates[0]);
        	holder.rebateRowValidDatesLL1.setVisibility(View.VISIBLE);
        } catch(ArrayIndexOutOfBoundsException e) {
        	holder.rebateRowValidDatesLL1.setVisibility(View.GONE);
        }        
        
        try {
        	holder.rebateRowValidDatesTv2.setText(validDates[1]);
        	holder.rebateRowValidDatesLL2.setVisibility(View.VISIBLE);
        } catch(ArrayIndexOutOfBoundsException e) {
        	holder.rebateRowValidDatesLL2.setVisibility(View.GONE);     
        }
        
        holder.rebateRowValidDatesChildLL.removeAllViews();            
        for(int i=2; i<validDates.length; i++){            	
            LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.local_rebates_eligibility, null);           	
            TextView criteria = (TextView) view.findViewById(R.id.rebateRowImpDetailsTv);
            criteria.setText(validDates[i]);            	
            holder.rebateRowValidDatesChildLL.addView(view);
        }      
        // End: Valid dates
        
        holder.rebateRowViewMoreTv.setOnClickListener(new OnClickListener() {				
			public void onClick(View arg0) {
				toggle(position);
			}
		});
        
        try {
        	holder.rebateRowImpDetailsLL.removeAllViews();
            String[] importantDetails = rebate.getImportantDetails();
            
            for(int i=0; i<importantDetails.length; i++){            	
            	LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.local_rebates_eligibility, null);          	
            	TextView criteria = (TextView) view.findViewById(R.id.rebateRowImpDetailsTv);
            	criteria.setText(importantDetails[i]);            	
            	holder.rebateRowImpDetailsLL.addView(view);
            }            
        } catch(Exception e) {
        	BBYLog.e(TAG, "Exception inside Rebate List Adapter " + e.getMessage());
			BBYLog.printStackTrace(TAG, e);
        }
               
        float amountOnImage = rebate.getPurchaseRebate().getAmount() + rebate.getRecyclingRebate().getAmount();
        holder.rebateRowPriceTv.setText("$"+Util.formatNumber0fractions(""+amountOnImage));

        switch(holder.rebateRowPriceTv.length()){	     
	     case 0:
	    	 holder.rebateRowPriceTv.setTextSize(24); 
	    	 break;
	     case 1:
	    	 holder.rebateRowPriceTv.setTextSize(22); 
	    	 break;
	     case 2:
	    	 holder.rebateRowPriceTv.setTextSize(20); 
	    	 break;
	     case 3:
	    	 holder.rebateRowPriceTv.setTextSize(18); 
	    	 break;
	     case 4:
	    	 holder.rebateRowPriceTv.setTextSize(16); 
	    	 break;
	     case 5:
	    	 holder.rebateRowPriceTv.setTextSize(14); 
	    	 break;
	     case 6:
	    	 holder.rebateRowPriceTv.setTextSize(12); 
	    	 break;
	     case 7:
	    	 holder.rebateRowPriceTv.setTextSize(10); 
	    	 break;
	     }
        return convertView;
    }

    public void toggle(int position) {
    	boolean temp = mExpanded[position];        	
    	for(int i=0; i<mExpanded.length; i++) {
    		mExpanded[i] = false;
    	}
    	mExpanded[position] = !temp;
        notifyDataSetChanged();
    }
        
    private boolean[] mExpanded;
   
    class ViewHolder {
    	TextView rebateRowPriceTv;
        TextView rebateRowProgramTv;
        TextView rebateRowAmountTv;
        TextView rebateRowValidDatesTv1;     
        TextView rebateRowValidDatesTv2;
        LinearLayout rebateRowValidDatesLL1;
        LinearLayout rebateRowValidDatesLL2;
        LinearLayout rebateRowValidDatesChildLL;
        LinearLayout rebateRowImpDetailsLL;
        TextView rebateRowImpDetailsTitleTv;
        TextView rebateRowViewMoreTv; // View Less/View More
        
        public void setExpanded(boolean expanded) {
        	rebateRowValidDatesChildLL.setVisibility(expanded ? View.VISIBLE : View.GONE);    
        	rebateRowImpDetailsTitleTv.setVisibility(expanded ? View.VISIBLE : View.GONE);
        	rebateRowImpDetailsLL.setVisibility(expanded ? View.VISIBLE : View.GONE);
        	rebateRowViewMoreTv.setText(expanded ? "View Less" : "View More");
        }            
    }
        
}