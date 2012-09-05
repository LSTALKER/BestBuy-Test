package com.bestbuy.android.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bestbuy.android.R;

public class CommerceBox extends LinearLayout {
	TypedArray array;
	ImageView checkmark;
	ImageView arrow;
	
	public CommerceBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.setOrientation(LinearLayout.HORIZONTAL);
		array = context.obtainStyledAttributes(attrs, R.styleable.CommerceBox, 0, 0);
		
		checkmark = (ImageView) this.findViewById(R.id.commerce_box_checkmark);
		String text = array.getString(R.styleable.CommerceBox_checkmark);
		if (text == null) {
			text = "visible";
		}
		if (!text.equals("gone")) {
			inflate(this.getContext(), R.layout.commerce_box_checkmark, this);
			if (text.equals("invisible")) {
				checkmark = (ImageView) findViewById(R.id.commerce_box_checkmark);
				checkmark.setVisibility(View.INVISIBLE);
			}
		}
		arrow = (ImageView) this.findViewById(R.id.commerce_box_arrow);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		String text = array.getString(R.styleable.CommerceBox_arrow);
		if (text == null) {
			text = "visible";
		}
		if (!text.equals("gone")) {
			inflate(this.getContext(), R.layout.commerce_box_arrow, this);
			if (text.equals("invisible")) {
				arrow = (ImageView) findViewById(R.id.commerce_box_arrow);
				arrow.setVisibility(View.INVISIBLE);
			}
		}
		
		arrow = (ImageView) this.findViewById(R.id.commerce_box_arrow);

		//Set the layout weight on all ViewGroups to be 1, this fixes some weird bugs.
		int numViewGroups = 0;
		for (int i = 0; i < this.getChildCount(); i++) {
			View v = this.getChildAt(i);
			if (v instanceof LinearLayout) {
				numViewGroups++;
				LinearLayout ll = (LinearLayout) v;
				ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
			} else if (v instanceof RelativeLayout) {
				numViewGroups++;
				RelativeLayout rl = (RelativeLayout) v;
				rl.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
			}
		}
		array.recycle();		
	}
	
	public ImageView getCheckmark() {
		return checkmark;
	}
	
	public ImageView getArrow() {
		return arrow;
	}
}