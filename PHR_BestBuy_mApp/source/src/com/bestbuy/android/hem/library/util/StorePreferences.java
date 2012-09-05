package com.bestbuy.android.hem.library.util;

public class StorePreferences {
	
	public long[] selectedItems;
	private static StorePreferences storePreferences;
	
	private StorePreferences() {
	}
	
	public static StorePreferences getInstance(){
		if(storePreferences == null)
			storePreferences = new StorePreferences();
		
		return storePreferences;
	}

	// This will save the sort type in memory
	public void saveToPersistence(long[] selectedItems) {
		this.selectedItems = selectedItems.clone();
	}

	// Retrieve from persistence
	public long[] retriveFromPersistence() {
		return selectedItems;
		
	}

	// Clear all persistence from local device
	public void clearPersistence() {
		selectedItems = null;
	}
}
