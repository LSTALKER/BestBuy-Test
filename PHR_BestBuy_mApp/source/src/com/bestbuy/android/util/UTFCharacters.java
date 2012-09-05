package com.bestbuy.android.util;

import android.text.Html;
import android.text.Spanned;

public class UTFCharacters {
	
	public static class UTF {

		public static  String replaceNonUTFCharacters(String source) {
			/*
			 * For debugging only, remove the comments to see the hex value to real character mapping
			 * Use http://www.w3schools.com/tags/ref_entities.asp to get the entity values
			*/
			/*
			String hex = "";

			for(int i=0;i<source.length();i++) {
				hex += source.charAt(i) +  "-" + Integer.toHexString(source.charAt(i)) + " "; 
			}

			BBYLog.i(source, "DONE >>>>>>>>>>>>>>>> <<<<<<<<<<<<<<<<<<<<<<<<<<" + hex);
			*/
			
			source = source.replaceAll("\uffc2" + "\uffae", "&#174"); // Registered symbol
			source = source.replaceAll("\uffc2" + "\uffa1", "&#161"); // Inverted exclamation
			source = source.replaceAll("\uffc2" + "\uffbf", "&#191"); // Inverted question mark
			source = source.replaceAll("\uffc3" + "\uffa9", "&#232");
			source = source.replaceAll("\uffc3" + "\uffb3", "&#243");
			source = source.replaceAll("\uffc3" + "\uffb1", "&#241");
			source = source.replaceAll("\uffc3" + "\uffa1", "&#225");
			source = source.replaceAll("\uffc3" + "\uffad", "&#237"); // i in Spanish
			source = source.replaceAll("\uffc3" + "\uffba", "&#250"); // u in Spanish
			
			// This is removed so we know if we need to fix other things
			// source =  source.replaceAll("[^\\x20-\\x7e]", "");

			Spanned encoding = Html.fromHtml(source);
			return encoding.toString();
		}
	}


}
