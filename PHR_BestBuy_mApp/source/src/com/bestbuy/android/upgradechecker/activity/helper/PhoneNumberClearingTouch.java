package com.bestbuy.android.upgradechecker.activity.helper;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.upgradechecker.util.UpgradeCheckerUtils;

public class PhoneNumberClearingTouch implements OnTouchListener{

	private EditText phoneNumberEditText;
	private Drawable closeImage;
	private int clearNoFor;
	
	public PhoneNumberClearingTouch(EditText phNoeditText,Drawable closeImg,int clearNo){
			phoneNumberEditText=phNoeditText;
			closeImage=closeImg;
			clearNoFor=clearNo;
	}
	public boolean onTouch(View v, MotionEvent event) {
		 if (phoneNumberEditText.getCompoundDrawables()[2] == null) {
             return false;
         }
         if (event.getAction() != MotionEvent.ACTION_UP) {
             return false;
         }
         if (event.getX() > phoneNumberEditText.getWidth() - phoneNumberEditText.getPaddingRight() - closeImage.getIntrinsicWidth()) {
             phoneNumberEditText.setText("");
             switch (clearNoFor) {
				case UpgradeCheckerUtils.AT_AND_T_CLEAR_PH_NO:
					AppData.getSharedPreferences().edit().putString(AppData.ATT_UPGRADE_CHECKER_PHONE_NUMBER, null).commit();
					break;
				case UpgradeCheckerUtils.SPRINT_CLEAR_PH_NO:
					AppData.getSharedPreferences().edit().putString(AppData.SPRINT_UPGRADE_CHECKER_PHONE_NUMBER, null).commit();
					break;
				case UpgradeCheckerUtils.T_MOBILE_CLEAR_PH_NO:
					AppData.getSharedPreferences().edit().putString(AppData.TMOBILE_UPGRADE_CHECKER_PHONE_NUMBER, null).commit();
					break;
				case UpgradeCheckerUtils.VERIZON_CLEAR_PH_NO:
					AppData.getSharedPreferences().edit().putString(AppData.VERIZON_UPGRADE_CHECKER_PHONE_NUMBER, null).commit();
					break;

			default:
				break;
			}
             phoneNumberEditText.setCompoundDrawables(null, null, null, null);
         }
         return false;
	}


}
