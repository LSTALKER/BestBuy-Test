package com.bestbuy.android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.bestbuy.android.R;
import com.bestbuy.android.storeevent.util.StoreUtils;

public class BBYAlertDialog {
	
	private Context context;
	
	public BBYAlertDialog(Context context){
		this.context = context;
	}

	public Dialog createAlertDialog(int id){
		AlertDialog dialog = null;
		
		switch (id) {

			case StoreUtils.APP_NOT_FOUND_DIALOG:
				dialog = new AlertDialog.Builder(context)
						.setTitle("")
						.setMessage(context.getResources().getString(R.string.APP_NOT_FOUND))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						})
						.create();
				break;
		}
		
		return dialog;
	}
}
