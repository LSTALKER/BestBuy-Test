package com.bestbuy.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestbuy.android.R;
import com.bestbuy.android.appolicious.library.dataobject.Review;
import com.bestbuy.android.data.PDP_RatingValue;
import com.bestbuy.android.data.PDP_Review;
import com.bestbuy.android.util.StarRating;

public class ProductReviewDetails extends MenuActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "ProductReviewDetails.java";

	public final static String BLUE_SHIRT_LIST = "blueShirtList";
	public final static String REVIEW = "review";
	private Context _context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MenuActivity.updateCartIcon(this);
		_context = this;

		PDP_Review review = null;
		Review reviewAppolicious = null;

		if (getIntent() != null && getIntent().getExtras() != null) {
			if(getIntent().hasExtra(REVIEW) || getIntent().hasExtra(BLUE_SHIRT_LIST)){
				setContentView(R.layout.product_review_blue_shirt);
				reviewAppolicious  = (Review) getIntent().getSerializableExtra(REVIEW);
			}
			else{
				setContentView(R.layout.product_review_details);
				Object passObj = getIntent().getExtras().get(ProductReview.PASSING_DETAILS_INTENT);

				if (passObj != null && passObj instanceof PDP_Review) {
					review = (PDP_Review) passObj;
				}
			}
		}

		if (review != null) {
			setUpViews(review);

		} 
		else if (reviewAppolicious != null){
			setUpReviewAppolicious(reviewAppolicious);
		}
		else {

			Toast.makeText(_context, "An error has occured with that review", Toast.LENGTH_LONG).show();
		}
	}

	private void setUpViews(PDP_Review review) {
		TextView titleTV = (TextView) findViewById(R.id.product_review_rating_title);
		TextView averageStarRatingTV = (TextView) findViewById(R.id.product_review_customer_average_rating);
		TextView customerDescriptionTV = (TextView) findViewById(R.id.product_review_customer_description);

		ImageView customerRatingIV = (ImageView) findViewById(R.id.product_review_customer_average_star_rating_image);

		titleTV.setText(review.getTitle());
		averageStarRatingTV.setText(review.getRating() + ".0");
		customerDescriptionTV.setText(review.getReviewText());

		customerRatingIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRating(), _context));

		View ratedValuesContainer = findViewById(R.id.rating_value_container);

		if (review.getRatingValues() == null || review.getRatingValues().isEmpty()) {
			findViewById(R.id.product_review_details_star_rating).setVisibility(View.GONE);
		} else {
			PDP_RatingValue.setUpView(ratedValuesContainer, review);
		}
	}

	private void setUpReviewAppolicious(Review review){
		TextView titleTV = (TextView) findViewById(R.id.product_review_rating_title);
		TextView averageStarRatingTV = (TextView) findViewById(R.id.product_review_customer_average_rating);
		TextView customerDescriptionTV = (TextView) findViewById(R.id.product_review_customer_description);

		ImageView customerRatingIV = (ImageView) findViewById(R.id.product_review_customer_average_star_rating_image);

		titleTV.setText(review.getTitle());
		customerDescriptionTV.setText(review.getBody());

		if(review.getRating()!= null&&!review.getRating().contains("null")){
			customerRatingIV.setImageBitmap(StarRating.getAssociatedStarImage(review.getRating(), _context));
			averageStarRatingTV.setText(review.getRating() + ".0");
		}else{
			customerRatingIV.setImageBitmap(StarRating.getAssociatedStarImage("0", _context));
			averageStarRatingTV.setText("0.0");
		}

		findViewById(R.id.product_review_details_star_rating).setVisibility(View.GONE);

	}
}
