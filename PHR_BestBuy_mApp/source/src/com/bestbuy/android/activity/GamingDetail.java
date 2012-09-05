package com.bestbuy.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.util.ImageProvider;


public class GamingDetail extends MenuActivity {

	private Product _product;
	private ImageView _icon;

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_context = this;
		_product = appData.getSelectedProduct();
		showView();
	}




	private void setIconImage() {
		if (_icon != null && _product != null && !isFinishing()) {
			ImageProvider.getBitmapImageOnThread(_product.getBestImageURL(), _icon);
		}
	}

	private void showView() {
		setContentView(R.layout.trade_in_detail);

		_icon = (ImageView) findViewById(R.id.trade_in_details_icon);

		setIconImage();

		TextView title = (TextView) findViewById(R.id.trade_in_details_title);
		title.setText(_product.getName());
		
		TextView platform = (TextView) findViewById(R.id.trade_in_details_platform);
		platform.setText(_product.getPlatform());
		
		TextView price = (TextView) findViewById(R.id.trade_in_details_price);
		price.setText("$" + _product.getTradeInValue());

		RelativeLayout nearbystores = (RelativeLayout) findViewById(R.id.trade_in_details_stores_rl);
		nearbystores.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), StoreLocatorList.class);
				startActivity(i);
			}
		});
		
		
	}
}
