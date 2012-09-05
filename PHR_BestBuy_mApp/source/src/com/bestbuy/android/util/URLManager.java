package com.bestbuy.android.util;

import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;

public class URLManager {
	public static String createQueryString(String url,
			List<NameValuePair> valuePairs) {
		if (valuePairs == null) {
			return url;
		}

		StringBuffer query = new StringBuffer();
		Iterator<NameValuePair> getParamsIter = valuePairs.iterator();
		while (getParamsIter.hasNext()) {
			NameValuePair pair = (NameValuePair) getParamsIter.next();
			String name = pair.getName().replace(" ", "%20").replace("&", "+");
			String value = pair.getValue().replace(" ", "%20");
			if (!name.equals("sort"))
				value = value.replace("&", "+");
			query.append(name + "=" + value);
			if (getParamsIter.hasNext()) {
				query.append("&");
			}
		}

		return url + "?" + query;
	}
}
