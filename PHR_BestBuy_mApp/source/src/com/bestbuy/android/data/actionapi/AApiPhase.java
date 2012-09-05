package com.bestbuy.android.data.actionapi;

import java.util.ArrayList;

import com.bestbuy.android.util.Link;

public class AApiPhase {
	private String briefNarrative;
	private String scriptedNarrative;
	private String formattedNarrative;
	private String narrative;
	private String name;
	private ArrayList<Link> links;
	private int beginDay;
	private int endDay;
	private ArrayList<AApiProduct> products;
	private int currentDay;
	private String id;
	

	public String getBriefNarrative() {
		return briefNarrative;
	}

	public void setBriefNarrative(String briefNarrative) {
		this.briefNarrative = briefNarrative;
	}

	public String getScriptedNarrative() {
		return scriptedNarrative;
	}

	public void setScriptedNarrative(String scriptedNarrative) {
		this.scriptedNarrative = scriptedNarrative;
	}

	public String getFormattedNarrative() {
		return formattedNarrative;
	}

	public void setFormattedNarrative(String formattedNarrative) {
		this.formattedNarrative = formattedNarrative;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(String narrative) {
		this.narrative = narrative;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Link> getLinks() {
		return links;
	}

	public void setLinks(ArrayList<Link> links) {
		this.links = links;
	}

	public int getBeginDay() {
		return beginDay;
	}

	public void setBeginDay(int beginDay) {
		this.beginDay = beginDay;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public ArrayList<AApiProduct> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<AApiProduct> products) {
		this.products = products;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

	
}
