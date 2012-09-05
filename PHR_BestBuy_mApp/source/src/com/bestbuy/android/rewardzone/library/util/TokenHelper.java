package com.bestbuy.android.rewardzone.library.util;


public class TokenHelper {
	private static String token;
	private static TokenHelper tokenHelper;
	
	public static TokenHelper getTokenInstance(){
		if(tokenHelper == null){
			tokenHelper = new TokenHelper();
		}
		return tokenHelper;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		TokenHelper.token = token;
	}

}