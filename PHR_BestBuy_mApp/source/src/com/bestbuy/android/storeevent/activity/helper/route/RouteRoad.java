package com.bestbuy.android.storeevent.activity.helper.route;

import java.io.Serializable;

public class RouteRoad implements Serializable {

	private static final long serialVersionUID = 1L;
	public String mName;
	public String mDescription;
	public String mDuration;
	public int mColor;
	public int mWidth;
	public double mFromLat;
	public double mFromLon;
	public double mToLat;
	public double mToLon;
	public double[][] mRoute = new double[][] {};
	public RoutePoints[] mPoints = new RoutePoints[] {};
}