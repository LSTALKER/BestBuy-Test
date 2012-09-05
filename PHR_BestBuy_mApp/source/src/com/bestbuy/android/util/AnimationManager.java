package com.bestbuy.android.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

import com.bestbuy.android.R;
import com.bestbuy.android.ui.Flip3dAnimation;

public class AnimationManager {
	public static Animation runBottomSlideUpOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.cart_slide_up);
		target.startAnimation(animation);
		return animation;
	}

	public static Animation runBottomSlideDownOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.cart_slide_down);
		target.startAnimation(animation);
		return animation;
	}
	
	public static Animation runTopSlideDownOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
		target.startAnimation(animation);
		return animation;
	}

	public static Animation runTopSlideUpOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
		target.startAnimation(animation);
		return animation;
	}
	
	public static Animation runFadeInAnimationOn(Context ctx, final View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.fade);
		target.startAnimation(animation);
		return animation;
	}
	
	public static Animation runFadeOutAnimationOn(Activity activity, View target) {
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
		target.startAnimation(animation);
		return animation;
	}
	
	public static Animation runFadeInAnimationOn(Context ctx, final List<View> targets) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.fade);
		for (View target : targets) {
			target.startAnimation(animation);
		}
		return animation;
	}
	
	public static Animation runFadeOutAnimationOn(Activity activity, List<View> targets) {
		Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
		for (View target : targets) {
			target.startAnimation(animation);
		}
		return animation;
	}
	
	public static Animation runFlip3dAnimationOn(Activity activity, View root, View target1, View target2) {
		return AnimationManager.runFlip3dAnimationOn(activity, root, target1, target2, root.getWidth()/2, root.getHeight()/2, true);
	}
	
	public static Animation runFlip3dAnimationOn(Activity activity, View root, View target1, View target2, int centerX, int centerY, boolean forward) {
		Animation animation = new Flip3dAnimation(target1, target2, centerX, centerY, forward);
		root.startAnimation(animation);
		return animation;
	}
	
	public static Animation runSlideInFromRightOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.right_slide_in);
		animation.setFillBefore(true);
		if (target.isShown()) {
			target.startAnimation(animation);
		}
		return animation;
	}
	
	public static Animation runSlideInFromLeftOn(Context ctx, View target) {
		Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.left_slide_in);
		animation.setFillBefore(true);
		if (target.isShown()) {
			target.startAnimation(animation);
		}
		return animation;
	}
	
	public static void delayedHideGone(int milliseconds, final View target) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				target.setVisibility(View.GONE);
			}
		}, milliseconds);
	}
	
	public static Animation jiggleLeft(final View target) {
		final Animation jiggleLeft = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_left);
		final Animation jiggleRight = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_right);
		target.startAnimation(jiggleLeft);
		jiggleLeft.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleRight);
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationStart(Animation animation) {
			}
		});
		jiggleRight.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleLeft);
			}
			
			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		return jiggleLeft;
	}
	
	public static Animation jiggleRight(final View target) {
		final Animation jiggleLeft = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_left);
		final Animation jiggleRight = AnimationUtils.loadAnimation(target.getContext(), R.anim.jiggle_right);
		target.startAnimation(jiggleRight);
		jiggleLeft.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleRight);
			}
			public void onAnimationRepeat(Animation animation) {
			}
			public void onAnimationStart(Animation animation) {
			}
		});
		jiggleRight.setAnimationListener(new AnimationListener() {
			public void onAnimationEnd(Animation animation) {
				target.startAnimation(jiggleLeft);
			}
			
			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationStart(Animation animation) {
			}
		});
		return jiggleLeft;
	}
}