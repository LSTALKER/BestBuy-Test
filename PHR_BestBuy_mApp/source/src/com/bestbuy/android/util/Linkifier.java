package com.bestbuy.android.util;

import java.util.regex.Pattern;

public class Linkifier {

	public static Pattern getPhoneNumberPattern() {
		String phoneNumberRegEx = "(1-[2-9]\\d{2}-\\d{3}-\\d{4})|(\\([2-9]\\d{2}\\) \\d{3}-\\d{4})|([2-9]\\d{2}-\\d{3}-\\d{4})|([2-9]\\d{2} \\d{3} \\d{4})|([2-9]\\d{2}\\.\\d{3}\\.\\d{4})";
		Pattern phoneNumPatern = Pattern.compile(phoneNumberRegEx);
		return phoneNumPatern;
	}
}