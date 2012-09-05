package com.bestbuy.android.util;

import android.content.SearchRecentSuggestionsProvider;
	
public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    /**
     * This is the provider authority identifier.  The same string must appear in your
     * Manifest file, and any time you instantiate a 
     * {@link android.provider.SearchRecentSuggestions} helper class. 
     */
    public final static String AUTHORITY = "com.bestbuy.android.util.SuggestionProvider";
    /**
     * These flags determine the operating mode of the suggestions provider.  This value should 
     * not change from run to run, because when it does change, your suggestions database may 
     * be wiped.
     */
    public final static int MODE = DATABASE_MODE_QUERIES;

    /**
     * The main job of the constructor is to call {@link #setupSuggestions(String, int)} with the
     * appropriate configuration values.
     */
    public SuggestionProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}