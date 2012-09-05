package com.bestbuy.android.upgradechecker.logic;

import java.util.ArrayList;

import android.content.Context;

import com.bestbuy.android.upgradechecker.data.CollectSubscriberList;
import com.bestbuy.android.upgradechecker.data.Subscriber;

public class UpgradeCheckerDataLogic {

	private Context context;
	public UpgradeCheckerDataLogic(Context context){
		this.context=context;
	}
	public ArrayList<Subscriber> getSubcriberList(String zip){
		CollectSubscriberList collectList=new CollectSubscriberList(context,zip);
		return collectList.getSubscriberList();
	}
}
