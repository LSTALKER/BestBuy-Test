package com.bestbuy.android.rewardzone;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import com.bestbuy.android.activity.RewardZoneDetailedReceipt;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.BestBuyApplication;
import com.bestbuy.android.data.Store;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EventsLogging;
import com.bestbuy.android.util.StoreLocator;

/**
 * Represents a Reward Zone transaction receipt
 * @author Recursive Awesome
 *
 */
public class RZReceipt extends RZTransaction implements Serializable {

	private static final long serialVersionUID = -3824434399898448485L;
	private String TAG = this.getClass().getName();
	private Store store;
	private String name = "";
	private String firstDetail = "";
	private String secondDetail = "";

	public Store getStore() {
		if (store == null) {
			try {
				BBYLog.e(TAG, "Loading store for id: " + getLocation());
				store = StoreLocator.findStoreById(getLocation());
			} catch (Exception e) {
				BBYLog.printStackTrace(TAG, e);
				BBYLog.e(TAG, "Exception getting store information for store id " 
						+ getLocation() + ": " + e.getMessage());
			}
		}
		return store;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
		ArrayList<RZTransactionLineItem> transactionLineItems = getSaleLineItems();
		name += "Receipt " + dateFormat.format(getDate()) + 
			" - Sale of " + transactionLineItems.size();
		if (transactionLineItems.size() == 1) {
			name += " item";
		} else {
			name += " items";
		}
	}
		
	@Override
	public String getFirstDetail() {
		return firstDetail;
	}
	
	@Override
	public String getSecondDetail() {
		return secondDetail;
	}
	
	@Override
	public void clicked(Activity activity) {
		
		AppData appData = ((BestBuyApplication) activity.getApplication()).getAppData();
		RZAccount rzAccount = appData.getRzAccount();
		Map<String, String> params = new HashMap<String, String>();
		params.put("value", EventsLogging.CUSTOM_CLICK_RZ_RECEIPTS_EVENT);
		params.put("rz_id", rzAccount.getId());
		params.put("rz_tier", rzAccount.getStatusDisplay()); 
		EventsLogging.fireAndForget(EventsLogging.CUSTOM_CLICK_ACTION, params);
		
		Intent intent = new Intent(activity, RewardZoneDetailedReceipt.class);
		intent.putExtra("receipt", this);
		activity.startActivity(intent);
	}

	@Override
	public void fetchImage(ImageView iv) {
		
	}

	@Override
	public void setFirstDetail() {
		if (getSource().equals("Store")) {
			if (getStore() != null) {
				firstDetail = getStore().getLongName();
			} else {
				firstDetail = "Best Buy";
			}
		} else {
			firstDetail = "bestbuy.com - ship to home";
		}
	}

	@Override
	public void setSecondDetail() {
		secondDetail = "Subtotal $" + super.getSubtotal() + " plus $" + super.getSalesTax() + " sales tax";
	}
}
