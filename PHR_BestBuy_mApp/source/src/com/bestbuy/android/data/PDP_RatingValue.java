package com.bestbuy.android.data;

import java.io.Serializable;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.StarRating;

public class PDP_RatingValue implements Serializable {

	private static final long serialVersionUID = 4916748347619098353L;
	private static final String TAG = "PDP_RatingValue.java";
	private String _rating;
	private String _label;
	
	
	
	public static void setUpView(View v, IRatedValues review)
	{
		TextView catOneTV = (TextView) v.findViewById(R.id.product_review_category_one_title);
		TextView catTwoTV = (TextView) v.findViewById(R.id.product_review_category_two_title);
		TextView catThreeTV = (TextView) v.findViewById(R.id.product_review_category_three_title);
		TextView catFourTV = (TextView) v.findViewById(R.id.product_review_category_four_title);

		ImageView catFourIV = (ImageView) v.findViewById(R.id.product_review_category_four_star_iv);
		ImageView catOneIV = (ImageView) v.findViewById(R.id.product_review_category_one_star_iv);
		ImageView catTwoIV = (ImageView) v.findViewById(R.id.product_review_category_two_star_iv);
		ImageView catThreeIV = (ImageView) v.findViewById(R.id.product_review_category_three_star_iv);
		
		RelativeLayout catOne = (RelativeLayout) v.findViewById(R.id.product_review_category_one);
		RelativeLayout catTwo = (RelativeLayout) v.findViewById(R.id.product_review_category_two);
		RelativeLayout catThree = (RelativeLayout) v.findViewById(R.id.product_review_category_three);
		RelativeLayout catFour = (RelativeLayout) v.findViewById(R.id.product_review_category_four);
		
		View line1 = v.findViewById(R.id.product_review_star_rating_line_1);
		View line2 = v.findViewById(R.id.product_review_star_rating_line_2);
		View line3 = v.findViewById(R.id.product_review_star_rating_line_3);
		
		LinearLayout starRatingLayout = (LinearLayout) v.findViewById(R.id.star_rating_details);
		ImageView shadow = (ImageView) v.findViewById(R.id.shadow);
		
		if (review.getRatingValues() == null || review.getRatingValues().size() == 0) {
			starRatingLayout.setVisibility(View.GONE);
			shadow.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
			starRatingLayout.setVisibility(View.VISIBLE);
			shadow.setVisibility(View.VISIBLE);
			if (review.getRatingValues() != null && review.getRatingValues().size() > 0) {
				catOne.setVisibility(View.VISIBLE);
				catOneTV.setText(review.getRatingValues().get(0).getLabel());
				catOneIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRatingValues().get(0).getRating(), v.getContext()));
			} else {
				catOne.setVisibility(View.GONE);
			}

			if (review.getRatingValues() != null && review.getRatingValues().size() > 1) {
				catTwo.setVisibility(View.VISIBLE);
				line1.setVisibility(View.VISIBLE);
				catTwoTV.setText(review.getRatingValues().get(1).getLabel());
				catTwoIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRatingValues().get(1).getRating(), v.getContext()));
			} else {
				catTwo.setVisibility(View.GONE);
				line1.setVisibility(View.GONE);
			}
			if (review.getRatingValues() != null && review.getRatingValues().size() > 2) {
				catThree.setVisibility(View.VISIBLE);
				line2.setVisibility(View.VISIBLE);
				catThreeTV.setText(review.getRatingValues().get(2).getLabel());
				catThreeIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRatingValues().get(2).getRating(), v.getContext()));
			} else {
				catThree.setVisibility(View.GONE);
				line2.setVisibility(View.GONE);
			}

			if (review.getRatingValues() != null && review.getRatingValues().size() > 3) {
				catFour.setVisibility(View.VISIBLE);
				line3.setVisibility(View.VISIBLE);
				catFourTV.setText(review.getRatingValues().get(3).getLabel());
				catFourIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRatingValues().get(3).getRating(), v.getContext()));
			} else {
				catFour.setVisibility(View.GONE);
				line3.setVisibility(View.GONE);
			}
		}
	}
		
	public PDP_RatingValue(String rating, String label) {
		_rating = rating;
		_label = label;
	}
	public String getRating() {
		return _rating;
	}
	public void setRating(String rating) {
		_rating = rating;
	}
	public String getLabel() {
		return _label;
	}
	public void setLabel(String label) {
		_label = label;
	} 

	
	public void log() {
		BBYLog.i(TAG, "Log: " + "PDP_RatingValue [_label=" + _label + ", _rating=" + _rating + "]"); 
	}

}
