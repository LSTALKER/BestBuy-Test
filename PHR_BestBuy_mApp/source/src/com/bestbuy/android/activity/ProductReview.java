package com.bestbuy.android.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.bestbuy.android.R;
import com.bestbuy.android.data.PDP_RatingValue;
import com.bestbuy.android.data.PDP_Review;
import com.bestbuy.android.data.PDP_ReviewSummary;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.EndlessAdapter;
import com.bestbuy.android.util.StarRating;

/**
 * Shows a list of reviews for the selected product
 * 
 * @author Recursive Awesome
 * 
 */
public class ProductReview extends MenuActivity {

	private static final String TAG = "ProductReview.java";
	public static final String PASSING_DETAILS_INTENT = "ASDF1234PASS";

	private String _sku;
	private Context _context;
	private PDP_ReviewSummary reviewSummary;
	private ListView reviewsList;
	private List<PDP_Review> reviews = new ArrayList<PDP_Review>();
	private View listViewHeader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_review);

		_context = this;
		reviewsList = (ListView) findViewById(R.id.reviews_list);
		_sku = this.getIntent().getStringExtra(Product.SKU);
		reviewSummary = new PDP_ReviewSummary(_sku);
		reviewsList.setAdapter(new ReviewAdapter());
		reviewsList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				if (pos >= 0 && pos <= reviews.size()) {
					Intent i = new Intent(_context, ProductReviewDetails.class);
					int adjustedPos = pos-1; //minus 1 for headerview
					if (adjustedPos >= 0 && adjustedPos < reviews.size()) {
						PDP_Review curReview = reviews.get(adjustedPos); //minus 1 for headerview
						i.putExtra(PASSING_DETAILS_INTENT, curReview);
						startActivity(i);
					}
				}
			}
		});
	}
	
	public class ReviewAdapter extends EndlessAdapter {
		boolean loadedHeader = false;		
		
		public ReviewAdapter() {	
			super(ProductReview.this, new ArrayAdapter<PDP_Review>(ProductReview.this, R.layout.product_review_row, reviews) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {					
					View row = convertView;
					if (row == null) {
						LayoutInflater inflater = getLayoutInflater();
						row = inflater.inflate(R.layout.product_review_row, parent, false);
					}

					BBYLog.d(ProductReview.TAG, "reviews size: " + reviews.size());
					PDP_Review review = (PDP_Review) reviews.get(position);
					BBYLog.d(ProductReview.TAG, "review: " + review);
					
					TextView title = (TextView) row.findViewById(R.id.product_review_rating_title);
					ImageView productRating = (ImageView) row.findViewById(R.id.product_review_star_rating);
					TextView text = (TextView) row.findViewById(R.id.product_review_rating_text);
					TextView submittedby = (TextView) row.findViewById(R.id.product_review_submitted_by);
					TextView rating = (TextView) row.findViewById(R.id.product_review_rating);

					productRating.setImageBitmap(StarRating.getAssociatedStarImage(review.getRating(), ProductReview.this));
					text.setText(review.getReviewText());
					title.setText(review.getTitle());
					submittedby.setText(review.getSubmissionTimeFormatted() + " by " + review.getDisplayName());
					rating.setText(review.getRating() + ".0");
					return row;
				}
			});
			listViewHeader = getLayoutInflater().inflate(R.layout.product_review_header, null);
			updateListViewHeader();
			this.addHeaderView(listViewHeader);
		}

		@Override
		protected void appendNextPage() {
			reviews.addAll(reviewSummary.getReviews());		
			if (!loadedHeader) {
				updateListViewHeader();
				loadedHeader = true;
			}
		}

		@Override
		protected boolean fetchNextPage() throws Exception {
			reviewSummary.loadReviews(getCurrentPage());
			return (getCurrentPage() != reviewSummary.getTotalPages()) ;
		}
	}
	
	public void updateListViewHeader() {
		TextView percentRecommendedTV = (TextView) listViewHeader.findViewById(R.id.product_review_percent_recommended);

		TextView reviewAverageRatingTV = (TextView) listViewHeader.findViewById(R.id.product_review_average_rating);
		ImageView reviewAverageStars = (ImageView) listViewHeader.findViewById(R.id.product_review_average_star_rating);

		DecimalFormat percent = new DecimalFormat("########00.0%");
		String formattedPercent = percent.format(reviewSummary.getRecommendToFriendPercentageString());
		percentRecommendedTV.setText(formattedPercent);

		DecimalFormat ratingDecimal = new DecimalFormat("########0.0");
		String formattedRating = ratingDecimal.format(reviewSummary.getAverageRatingAsDouble());
		reviewAverageRatingTV.setText(formattedRating);

		reviewAverageStars.setImageBitmap(StarRating.getAssociatedStarImage(reviewSummary.getAverageRating(), listViewHeader.getContext()));
		PDP_RatingValue.setUpView(listViewHeader, reviewSummary);
	}
}
