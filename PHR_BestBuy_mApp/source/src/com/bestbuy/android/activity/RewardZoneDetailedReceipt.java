package com.bestbuy.android.activity;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.ReceiptDetail;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.rewardzone.RZAccount;
import com.bestbuy.android.rewardzone.RZReceipt;
import com.bestbuy.android.rewardzone.RZTransactionLineItem;
import com.bestbuy.android.ui.ReceiptDetailsLayout;
import com.bestbuy.android.ui.RewardZoneHeader;
import com.bestbuy.android.util.StartIntents;

public class RewardZoneDetailedReceipt extends MenuActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_zone_detailed_receipt);

		RZReceipt receipt = (RZReceipt) getIntent().getExtras().getSerializable("receipt");
		if (receipt != null) {

			AppData appData = ((BestBuyApplication) this.getApplication()).getAppData();
			RZAccount rzAccount = appData.getRzAccount();
			RewardZoneHeader.setUpHeader(rzAccount, this, true);
			Store store = receipt.getStore();

			ReceiptDetailsLayout rdl = (ReceiptDetailsLayout) findViewById(R.id.reward_zone_pdl_product_summary);
			ArrayList<RZTransactionLineItem> transactionLineItems = receipt.getSaleLineItems();
			for (int i = 0; i < transactionLineItems.size(); i++) {
				RZTransactionLineItem lineItem = transactionLineItems.get(i);
				ReceiptDetail rd = new ReceiptDetail(lineItem.getSkuDescription(), lineItem.getSalePrice(), "Qty: "
						+ lineItem.getUnitQuantity());

				rdl.addReceiptDetail(rd);
			}

			TextView storeDetailsHeader = (TextView) findViewById(R.id.reward_zone_reciept_product_view_store_details_header);
			TextView productTotalTV = (TextView) findViewById(R.id.reward_zone_reciept_product_total);
			TextView shTotalTV = (TextView) findViewById(R.id.reward_zone_reciept_product_sandh_total);
			TextView salesTaxTV = (TextView) findViewById(R.id.reward_zone_reciept_product_salestax_total);
			TextView orderTotalTV = (TextView) findViewById(R.id.reward_zone_reciept_order_total);
			LinearLayout mapClickyLL = (LinearLayout) findViewById(R.id.reward_zone_reciept_product_view_store_clicky_layout);
			LinearLayout callClickyLL = (LinearLayout) findViewById(R.id.reward_zone_reciept_product_call_store_clicky_layout);
			TextView viewStoreTV = (TextView) findViewById(R.id.reward_zone_reciept_product_view_store_tv);
			TextView callStoreTV = (TextView) findViewById(R.id.reward_zone_reciept_product_call_store_tv);

			calculateSH(shTotalTV, receipt);
			productTotalTV.setText(receipt.getSubtotal());
			salesTaxTV.setText("$" + receipt.getSalesTax());
			orderTotalTV.setText(receipt.getTotal());

			if (store != null && store.getPhone() != null) {
				callStoreTV.setText("Call " + store.getPhone().toUpperCase());
				callClickyLL.setTag(store.getPhone().toUpperCase());

				callClickyLL.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						StartIntents.MakeCall(v.getContext(), v.getTag().toString());
					}
				});
				
			} else {
				callClickyLL.setVisibility(View.GONE);
			}

			if (store != null && store.getName() != null && store.getStoreId() != null && store.getRegion() != null
					&& store.getPostalCode() != null) {

				viewStoreTV.setText("View " + store.getName().toUpperCase());
				String address = "BEST BUY #" + store.getStoreId() + "\n" + store.getName().toUpperCase() + ", " + store.getRegion() + " "
						+ store.getPostalCode();

				mapClickyLL.setTag(address);

				mapClickyLL.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						StartIntents.GoToMapLocation(v.getContext(), v.getTag().toString());
					}
				});
			} else {
				mapClickyLL.setVisibility(View.GONE);
				storeDetailsHeader.setVisibility(View.GONE);
			}

		} else {
			Toast.makeText(this, "A problem has occured with this record.", Toast.LENGTH_SHORT).show();
		}
	}

	private void calculateSH(TextView shTotalTV, RZReceipt receipt) {
		double shippingPrice = 0.0;
		double shippingTax = 0.0;
		double shippingTotal = 0.0;
		if (receipt.getShippingPrice() != null && receipt.getShippingPrice().length() > 0) {
			shippingPrice = Double.parseDouble(receipt.getShippingPrice());
		}

		if (receipt.getShippingTax() != null && receipt.getShippingTax().length() > 0) {
			shippingTax = Double.parseDouble(receipt.getShippingTax());
		}

		shippingTotal = shippingPrice + shippingTax;
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		shTotalTV.setText(currencyFormatter.format(shippingTotal));
	}
}
