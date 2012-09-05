package com.bestbuy.android.util;

import android.content.Context;

public class JSHelper {

	public static String getScript(int name, Context context){
		String script = context.getResources().getString(name);
		return script;
	}
}
