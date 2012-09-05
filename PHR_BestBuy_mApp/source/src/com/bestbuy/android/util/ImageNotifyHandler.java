package com.bestbuy.android.util;

import android.os.Handler;
import android.os.Message;

public class ImageNotifyHandler extends Handler {
	private final OnImageUpdateListener _onImageUpdate;

	/**
	 * Initializes a new instance of the ImageNotifyHandler class.
	 * 
	 * @param onImageUpdateListener
	 *            The onImageUpdate delegate to call back when this handler it
	 *            sent a message.
	 */
	public ImageNotifyHandler(OnImageUpdateListener onImageUpdateListener) {
		_onImageUpdate = onImageUpdateListener;
	}

	@Override
	public void handleMessage(Message msg) {
		_onImageUpdate.onImageUpdate((String) msg.obj);
	}

	public interface OnImageUpdateListener {
		void onImageUpdate(String remoteImageUrl);
	}

}