package com.bestbuy.android.storeevent.activity.helper.route;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parse the KML string
 * @author lalitkumar_s
 *
 */
public class KMLParser extends DefaultHandler {
	RouteRoad mRoad;
	boolean isPlacemark;
	boolean isRoute;
	boolean isItemIcon;
	private String mString;
	private StringBuffer mStringFinal = new StringBuffer();

	public KMLParser() {
		mRoad = new RouteRoad();
	}

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase("Placemark")) {
			isPlacemark = true;
			mRoad.mPoints = addPoint(mRoad.mPoints);
		} else if (localName.equalsIgnoreCase("ItemIcon")) {
			if (isPlacemark)
				isItemIcon = true;
		}
		mString = new String();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String chars = new String(ch, start, length).trim();
		mString = mString.concat(chars);
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (mString.length() > 0) {
			if (localName.equalsIgnoreCase("name")) {
				if (isPlacemark) {
					isRoute = mString.equalsIgnoreCase("Route");
					if (!isRoute) {
						mRoad.mPoints[mRoad.mPoints.length - 1].mName = cleanup(mString,"");
					}
				} else {
					mRoad.mName = mString;
				}
			} else if (localName.equalsIgnoreCase("color") && !isPlacemark) {
				mRoad.mColor = Integer.parseInt(mString, 16);
			} else if (localName.equalsIgnoreCase("width") && !isPlacemark) {
				mRoad.mWidth = Integer.parseInt(mString);
			} else if (localName.equalsIgnoreCase("description")) {
				if (isPlacemark) {
					String description = cleanup(mString, "");
					if (!isRoute)
						mRoad.mPoints[mRoad.mPoints.length - 1].mDescription = cleanup(description, "");
					else {
						mRoad.mPoints = removePoint(mRoad.mPoints);
						mRoad.mDescription = cleanup(description, "Distance");
						mRoad.mDuration = cleanup(description, "Duration");
					}
				}
			} else if (localName.equalsIgnoreCase("href")) {
				if (isItemIcon) {
					mRoad.mPoints[mRoad.mPoints.length - 1].mIconUrl = mString;
				}
			} else if (localName.equalsIgnoreCase("coordinates")) {
				if (isPlacemark) {
					if (!isRoute) {
						String[] xyParsed = split(mString, ",");
						double lon = Double.parseDouble(xyParsed[0]);
						double lat = Double.parseDouble(xyParsed[1]);
						mRoad.mPoints[mRoad.mPoints.length - 1].mLatitude = lat;
						mRoad.mPoints[mRoad.mPoints.length - 1].mLongitude = lon;
					} else {
						mStringFinal.append(mString);
					}
				}
			} else if (localName.equalsIgnoreCase("Document")) {
				String[] coodrinatesParsed = split(mStringFinal.toString(), " ");
				mRoad.mRoute = new double[coodrinatesParsed.length][2];
				for (int i = 0; i < coodrinatesParsed.length; i++) {
					String[] xyParsed = split(coodrinatesParsed[i], ",");
					for (int j = 0; j < 2 && j < xyParsed.length; j++) {
						mRoad.mRoute[i][j] = Double.parseDouble(xyParsed[j]);
						if(i==0 && j==0)
						{
							mRoad.mFromLon = Double.parseDouble(xyParsed[j]);
						}
						else if(i==0 && j==1)
						{
							mRoad.mFromLat = Double.parseDouble(xyParsed[j]);
						}
						else if(i==coodrinatesParsed.length-1 && j==0)
						{
							mRoad.mToLon = Double.parseDouble(xyParsed[j]);
						}
						else if(i==coodrinatesParsed.length-1 && j==1)
						{
							mRoad.mToLat = Double.parseDouble(xyParsed[j]);
						}
					}
				}
			}
		}
		if (localName.equalsIgnoreCase("Placemark")) {
			isPlacemark = false;
			if (isRoute)
				isRoute = false;
		} else if (localName.equalsIgnoreCase("ItemIcon")) {
			if (isItemIcon)
				isItemIcon = false;
		}

	}

	private String cleanup(String value, String want) {
		String remove = "<br/>";
		int index = value.indexOf(remove);
		if (index != -1)
			value = value.substring(0, index);
		remove = "&#160;";
		value = value.replace(remove, " ");
		remove = "&#39;";
		value = value.replace(remove, "'");
		remove = "null";
		value = value.replace(remove, " ");

		if (want.equalsIgnoreCase("Distance")) {
			int indexstart = value.indexOf(":");
			int indexend = value.indexOf("(");
			value = value.substring(indexstart + 1, indexend - 1);
		} else if (want.equalsIgnoreCase("Duration")) {
			remove = "about";
			int indexstart = value.indexOf(remove);
			int indexend = value.indexOf(")");
			value = value.substring(indexstart + remove.length() + 1, indexend);
			value = value.trim() + " (Approx)";
		}

		return value.trim();
	}

	public RoutePoints[] addPoint(RoutePoints[] points) {
		RoutePoints[] result = new RoutePoints[points.length + 1];
		for (int i = 0; i < points.length; i++)
			result[i] = points[i];
		result[points.length] = new RoutePoints();
		return result;
	}
	
	public RoutePoints[] removePoint(RoutePoints[] points) {
		RoutePoints[] result = new RoutePoints[points.length - 1];
		for (int i = 0; i < points.length-1; i++)
			result[i] = points[i];
		return result;
	}

	private static String[] split(String strString, String strDelimiter) {
		String[] strArray;
		int iOccurrences = 0;
		int iIndexOfInnerString = 0;
		int iIndexOfDelimiter = 0;
		int iCounter = 0;
		if (strString == null) {
			throw new IllegalArgumentException("Input string cannot be null.");
		}
		if (strDelimiter == null || strDelimiter.length() <= 0) {
			throw new IllegalArgumentException(
					"Delimeter cannot be null or empty.");
		}
		if (strString.startsWith(strDelimiter)) {
			strString = strString.substring(strDelimiter.length());
		}
		if (!strString.endsWith(strDelimiter)) {
			strString += strDelimiter;
		}
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			iOccurrences += 1;
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
		}
		strArray = new String[iOccurrences];
		iIndexOfInnerString = 0;
		iIndexOfDelimiter = 0;
		while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
				iIndexOfInnerString)) != -1) {
			strArray[iCounter] = strString.substring(iIndexOfInnerString,
					iIndexOfDelimiter);
			iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
			iCounter += 1;
		}

		return strArray;
	}
}