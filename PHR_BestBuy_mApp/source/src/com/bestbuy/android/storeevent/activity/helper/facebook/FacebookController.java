package com.bestbuy.android.storeevent.activity.helper.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.BBYLog;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.helper.BaseDialogListener;
import com.facebook.android.helper.BaseRequestListener;
import com.facebook.android.helper.SessionEvents;
import com.facebook.android.helper.SessionEvents.AuthListener;
import com.facebook.android.helper.SessionEvents.LogoutListener;
import com.facebook.android.helper.SessionStore;

/**
 * Controls the entire face book flow of the application.
 * @author lalitkumar_s
 *
 */

public class FacebookController {
	private static FacebookController _instance;
	private static boolean isInitialized = false;
	
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	private String[] mPermissions;
	private Handler mHandler;
	private Activity mActivity;
	private SessionListener mSessionListener = new SessionListener();;
	private String link;
	private String description;
	private String name;
	private String imageUrl;
	
	public static FacebookController getInstance() {
		if(_instance == null) {
			isInitialized = false;
			return new FacebookController();
		}
		
		isInitialized = true;
		return _instance;
	}
	
	/**
	 * Initialize the Face book instance
	 * @param appId : Face book application ID to be generate from Face book web site.
	 * @param activity : activity
	 * @param context : context
	 * @param permissions : Specific permissions
	 */
	public void initializeAll(String appId, Activity activity, Context context, String[] permissions) {
		if(isInitialized)
			return;
		
		this.mFacebook = new Facebook(appId);
	 	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
	 	
		SessionStore.restore(mFacebook, context);
        SessionEvents.addAuthListener(mSessionListener);
        SessionEvents.addLogoutListener(mSessionListener);
        
		this.mPermissions = permissions.clone();
		this.mHandler = new Handler();
		this.mActivity = activity;
	}
	
	/**
	 * Login to the face book
	 */
	public void loginToFacebook() {
        if (!mFacebook.isSessionValid()) {
            mFacebook.authorize(this.mActivity, this.mPermissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
        } 
    }
	
	/**
	 * Logout to the face book
	 */
	public void logoutFromFacebook() {
		if (mFacebook.isSessionValid()) {
	        SessionEvents.onLogoutBegin();
	        mAsyncRunner.logout(this.mActivity, new LogoutRequestListener());
		}
	}
	
	/**
	 * Post message on wall
	 */
	public void postMessageOnWall() {
		if (mFacebook.isSessionValid()) {
			Bundle params = new Bundle();
			
			if(link != null)
				params.putString("link", link);
			
			if(description != null) {
				description = StoreUtils.removeHtmlTag(description);
				description = StoreUtils.truncateFacebookMessage(description);
				params.putString("description", description);
			}
			
			if(name != null)
				params.putString("name", name);
			
			if(imageUrl != null)
				params.putString("picture", imageUrl);
			
			mFacebook.dialog(mActivity, "feed", params, new WallPostDialogListener());
			
		} else {
			mFacebook.authorize(this.mActivity, this.mPermissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
		}
	}	

	public class WallPostDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
            	BBYLog.d("Facebook-Example", "Dialog Success! post_id=" + postId);
                mAsyncRunner.request(postId, new WallPostRequestListener());
            } else {
            	BBYLog.d("Facebook-Example", "No wall post made");
            }
        }
    }
	
	 public class WallPostRequestListener extends BaseRequestListener {

	        public void onComplete(final String response, final Object state) {
	            String message = "<empty>";
	            try {
	                JSONObject json = Util.parseJson(response);
	                message = json.getString("message");
	            } catch (JSONException e) {
	            	BBYLog.w("Facebook-Example", "JSON Error in response");
	            } catch (FacebookError e) {
	            	BBYLog.w("Facebook-Example", "Facebook Error: " + e.getMessage());
	            }
	            final String text = "Your Wall Post: " + message;
	            mActivity.runOnUiThread(new Runnable() {
	                public void run() {
	                	BBYLog.d("LOG", text);
	                }
	            });
	        }
	    }
	 
    private final class LoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
           	postMessageOnWall();
        }

        public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
    }
    
    public class LogoutRequestListener extends BaseRequestListener {
        public void onComplete(String response, final Object state) {
            mHandler.post(new Runnable() {
                public void run() {
                    SessionEvents.onLogoutFinish();
                    SessionStore.clear(mActivity);
                }
            });
        }
    }
    
    private class SessionListener implements AuthListener, LogoutListener {
        
        public void onAuthSucceed() {
            SessionStore.save(mFacebook, mActivity);
        }

        public void onAuthFail(String error) {
        }
        
        public void onLogoutBegin() {           
        }
        
        public void onLogoutFinish() {
            SessionStore.clear(mActivity);
        }
    }

	public Facebook getFacebook() {
		return this.mFacebook;
	}
	
	public boolean isInitialized() {
		return isInitialized;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
