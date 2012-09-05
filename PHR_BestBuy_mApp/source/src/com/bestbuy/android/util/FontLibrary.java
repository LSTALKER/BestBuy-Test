package com.bestbuy.android.util;

import android.content.res.Resources;
import android.graphics.Typeface;

import com.bestbuy.android.R;
/**
 * 	Hold all the custom fonts used by the application
 * @author Lalit Kumar Sahoo
 *
 */
public class FontLibrary {
	
	private static final Typeface DEFAULT_FONT = Typeface.DEFAULT;
	
	public static Typeface FONT_DROIDSANS_BOLD = null;
	public static Typeface FONT_DROIDSANS = null;
	public static Typeface FONT_HELVETICA_BOLD = null;
	public static Typeface FONT_HELVETICA_65W = null;
	
	public static void initializeFonts(Resources resources){
		if(FontLibrary.FONT_DROIDSANS_BOLD == null)
			FontLibrary.FONT_DROIDSANS_BOLD = Typeface.createFromAsset(resources.getAssets(), resources.getString(R.string.FONT_DROIDSANS_BOLD));

		if(FontLibrary.FONT_DROIDSANS == null)
			FontLibrary.FONT_DROIDSANS = Typeface.createFromAsset(resources.getAssets(), resources.getString(R.string.FONT_DROIDSANS));

		if(FontLibrary.FONT_HELVETICA_BOLD == null)
			FontLibrary.FONT_HELVETICA_BOLD = Typeface.createFromAsset(resources.getAssets(), resources.getString(R.string.FONT_HELVETICA_BOLD));
		
		if(FontLibrary.FONT_HELVETICA_65W == null)
			FontLibrary.FONT_HELVETICA_65W = Typeface.createFromAsset(resources.getAssets(), resources.getString(R.string.FONT_HELVETICA_65W));
		
	}
	
	public static Typeface getFont(int typefaceName, Resources resources) {
		
		FontLibrary.initializeFonts(resources);
		
		Typeface typeface = null; 
		
		if (typefaceName == R.string.droidsansbold)
			typeface = FontLibrary.FONT_DROIDSANS_BOLD;
		else if(typefaceName == R.string.droidsans)
			typeface = FontLibrary.FONT_DROIDSANS;				
		else if(typefaceName == R.string.helveticabold)
			typeface = FontLibrary.FONT_HELVETICA_BOLD;		
		else if(typefaceName == R.string.helvetica65w)
			typeface = FontLibrary.FONT_HELVETICA_65W;	
		
		if (typeface == null)
			typeface = DEFAULT_FONT;

		return typeface;
	}
}
