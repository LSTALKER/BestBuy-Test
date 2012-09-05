package com.bestbuy.android.storeevent.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * The same edit text is able to handle the zip code as well as city name with certain scenarios
 * So for every entered character the word is validated
 * 
 * 1. Restrict to write special symbols.
 * 2. If first character is a number then the entered string is considered as Zip Code and restricted
 *    to length 5.
 * 3. If first character is an alphabet then the entered string is considered as City Name and restricted
 *    to length 28.
 * 4. For city name the user can not enter the numeric value.
 * 5. For Zip Code the user can not enter the alphabet.
 * 
 * @author lalitkumar_s
 */

public class CityZipCodeTextValidator implements TextWatcher {
	private EditText mEditText;
	private String currentWord;
	private boolean isNumber = false;
	private int previousStatus = -1;
	
	public CityZipCodeTextValidator(EditText e) {
		mEditText = e;
	}

	public void afterTextChanged(Editable s) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		currentWord = s.toString();
		if(s.length() == 0)
			previousStatus = -1;
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		try {
			int ascii = -1;
			int length = s.length();
			
			for (int i = 0; i < length; i++) {
				if(s.length() == 1) {
					InputFilter[] filterArray = new InputFilter[1];
					if(Character.isDigit(s.charAt(0))) {
						isNumber = true;
						filterArray[0] = new InputFilter.LengthFilter(5);
					} else {
						isNumber = false;
						filterArray[0] = new InputFilter.LengthFilter(28);
					}
					mEditText.setFilters(filterArray);
				} else if(s.length() != 0) {
					if(isNumber || !isNumber)
						previousStatus = -1;
					else 
						previousStatus = 1;
				}
				ascii = (int) s.charAt(i);
				if ((isNumber && !isValidNumber(ascii)) || (!isNumber && !isValidCharacter(ascii))) {
					mEditText.setText(currentWord);
					if(currentWord.length() == 0)
						mEditText.setSelection(0);
					else
						mEditText.setSelection((currentWord.length() - 1));
					}
				} 
	
		} catch (Exception e) {
		}
	}

	private boolean isValidNumber(int source) {
		if (source >= 48 && source <= 57)
			return true;

		return false;
	}

	private boolean isValidCharacter(int source) {
		if ((source >= 97 && source <= 122)
				|| (source >= 65 && source <= 90) || source == 32)
			return true;

		return false;
	}
	
	public boolean isNumber() {
		return isNumber;
	}
}
