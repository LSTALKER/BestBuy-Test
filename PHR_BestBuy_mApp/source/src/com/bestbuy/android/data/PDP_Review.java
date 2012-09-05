package com.bestbuy.android.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bestbuy.android.util.BBYLog;


public class PDP_Review implements Serializable, IRatedValues {

	private final String TAG = this.getClass().getName();
	private static final long serialVersionUID = 4685215700207394012L;
	private int _id;
	private String _title;
	private String _reviewText;
	private String _rating;
	private String _displayName;
	private String _submissionTime;
	private boolean _isLoadDialog, _isLoading, _isHeader;
	private int _key;
	private List<PDP_RatingValue> _ratingValues;

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getReviewText() {
		return _reviewText;
	}

	public void setReviewText(String reviewText) {
		_reviewText = reviewText;
	}

	public String getRating() {
		return _rating;
	}

	public void setRating(String rating) {
		_rating = rating;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public String getSubmissionTime() {
		return _submissionTime;
	}
	
	public String getSubmissionTimeFormatted() {
		DateFormat originalDf = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat formattedDf = new SimpleDateFormat("MMMMM d, yyyy");
		Date date;
		try {
			date = originalDf.parse(getSubmissionTime());
			return formattedDf.format(date);
		} catch (ParseException e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return getSubmissionTime();
	}

	public void setSubmissionTime(String submissionTime) {
		_submissionTime = submissionTime;
	}

	public List<PDP_RatingValue> getRatingValues() {
		return _ratingValues;
	}

	public void setRatingValues(List<PDP_RatingValue> ratingValues) {
		_ratingValues = ratingValues;
	}

	public void setId(String id) {
		if (id == null || id.equals("")) {
			_id = 0;
		} else {
			_id = Integer.parseInt(id);
		}
	}

	public void addRatingValue(PDP_RatingValue pdpRatingValue) {
		if (_ratingValues == null) {
			_ratingValues = new ArrayList<PDP_RatingValue>();
		}

		_ratingValues.add(pdpRatingValue);
	}

	@Override
	public String toString() {
		return "PDP_Review [_displayName=" + _displayName + ", _rating=" + _rating + ", _ratingValues=" + _ratingValues + ", _reviewText="
				+ _reviewText + ", _submissionTime=" + _submissionTime + ", _title=" + _title + "]";
	}

	public boolean isHeader() {
		return _isHeader;
	}

	public void setIsHeader(boolean isHeader) {
		_isHeader = isHeader; 
	}


}
