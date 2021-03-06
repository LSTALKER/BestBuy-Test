package com.bestbuy.android.storeevent.activity.helper.route;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class RouteMapOverlay extends Overlay {
	ArrayList<GeoPoint> mPoints;

	public RouteMapOverlay(RouteRoad routeRoad, MapView mv) {
		
		mPoints = new ArrayList<GeoPoint>();
		
		if (routeRoad.mRoute.length > 0) {
			
			for (int i = 0; i < routeRoad.mRoute.length; i++) {
				mPoints.add(new GeoPoint((int) (routeRoad.mRoute[i][1] * 1E6),
						(int) (routeRoad.mRoute[i][0] * 1E6)));
			}

			int moveToLat = (mPoints.get(0).getLatitudeE6() + (mPoints.get(
					mPoints.size() - 1).getLatitudeE6() - mPoints.get(0)
					.getLatitudeE6()) / 2);
			
			int moveToLong = (mPoints.get(0).getLongitudeE6() + (mPoints.get(
					mPoints.size() - 1).getLongitudeE6() - mPoints.get(0)
					.getLongitudeE6()) / 2);

			GeoPoint moveTo = new GeoPoint(moveToLat, moveToLong);

			MapController mapController = mv.getController();
			mapController.animateTo(moveTo);
			mapController.setZoom(10);
			mv.setSatellite(true);
		}
	}
	
	@Override
    public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
            super.draw(canvas, mv, shadow);
            drawPath(mv, canvas);
            return true;
    }

    public void drawPath(MapView mapview, Canvas canvas) {
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#52beff"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        for (int i = 0; i < mPoints.size(); i++) {
            Point point = new Point();
            mapview.getProjection().toPixels(mPoints.get(i), point);
            x2 = point.x;
            y2 = point.y;
            if (i > 0) {
            	canvas.drawLine(x1, y1, x2, y2, paint);
            }
            x1 = x2;
            y1 = y2;
        }
    }
}