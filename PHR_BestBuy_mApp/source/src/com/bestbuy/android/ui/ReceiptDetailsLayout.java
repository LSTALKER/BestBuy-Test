package com.bestbuy.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.ReceiptDetail;

public class ReceiptDetailsLayout extends LinearLayout {

	private int _viewIndex;
	private LayoutInflater _layoutInflater;
	private boolean _hideLastDivider = true;
	
	private View _divider; 

	public ReceiptDetailsLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReceiptDetailsLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		_layoutInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		_viewIndex = 0;
		this.setBackgroundResource(R.drawable.commerce_whitebox);
	}

	public void addReceiptDetail(ReceiptDetail receiptDetail) {
		
		if(_divider != null)
		{
			_divider.setVisibility(View.VISIBLE);
			this.requestLayout();
		}
		
		View receiptRow = _layoutInflater.inflate(
				R.layout.rz_product_summary_item, null);

		TextView productTitleTV = (TextView) receiptRow
				.findViewById(R.id.reward_zone_reciept_item_label);
		TextView productCostTV = (TextView) receiptRow
				.findViewById(R.id.reward_zone_reciept_cost);
		TextView productQTYTV = (TextView) receiptRow
				.findViewById(R.id.reward_zone_reciept_qty);
		
		_divider = (View) receiptRow.findViewById(R.id.rz_v_seperator_points);

		productTitleTV.setText(receiptDetail.getProductTitle());
		productCostTV.setText(receiptDetail.getProductCostFormatted());
		productQTYTV.setText(receiptDetail.getProductQtyFormatted());
		
		if(_hideLastDivider)
		{
			_divider.setVisibility(View.GONE);
		}

		this.addView(receiptRow, _viewIndex);
		_viewIndex++;
		
		this.requestLayout();
	}

	public void setHideLastDivider(Boolean hide) {
		_hideLastDivider = hide;
	}
}
