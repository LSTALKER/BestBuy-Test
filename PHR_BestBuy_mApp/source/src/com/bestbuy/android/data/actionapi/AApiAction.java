package com.bestbuy.android.data.actionapi;

import java.util.ArrayList;

public class AApiAction {
	
	private String theme;
	private ArrayList<AApiProduct> offers;
	private AApiTarget target;
	private ArrayList<AApiLifecycle> lifecycles;
	
	
	
	public String getTheme(){
		return theme;
	}
	
	public void setTheme(String theme){
		this.theme = theme;
	}

	public ArrayList<AApiProduct> getOffers(){
		return offers;
	}
	
	public void setOffers(ArrayList<AApiProduct> offers){
		this.offers = offers;
	}	
	
	public AApiTarget getTarget(){
		return target;
	}
	
	public void setTarget(AApiTarget target){
		this.target = target;
	}
	
	public ArrayList<AApiLifecycle> getLifecycles(){
		return lifecycles;
	}
	
	public void setLifecycles(ArrayList<AApiLifecycle> lifecycles){
		this.lifecycles = lifecycles;
	}
	

}
