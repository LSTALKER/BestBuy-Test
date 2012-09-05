package com.bestbuy.android.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.InputStreamExtensions;

public class PDP_ReviewSummary implements IRatedValues {

	@SuppressWarnings("unused")
	private static final String TAG = "PDP_ReviewSummary.java";

	private List<PDP_Review> _reviews;
	private List<PDP_RatingValue> _overallRatingValues;
	private String _averageRating, _sku;
	private int _recommendCount, _notRecommendCount, _curPage, totalPages = 0;

	public PDP_ReviewSummary(String sku) {
		_sku = sku;
		_reviews = new ArrayList<PDP_Review>();
	}

	public String getSKU() {
		return _sku;
	}

	public int getCurPage() {
		return _curPage;
	}

	public void setCurPage(String curPage) {

		if (curPage == null || curPage.equals("")) {
			_curPage = 0;
		} else {
			_curPage = Integer.parseInt(curPage);
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setNumOfPages(String numOfPages) {
		if (numOfPages == null || numOfPages.equals("")) {
			totalPages = 0;
		} else {
			totalPages = Integer.parseInt(numOfPages);
		}
	}

	public void setRecommendCount(String recommendCount) {

		if (recommendCount == null || recommendCount.equals("")) {
			_recommendCount = 0;
		} else {
			_recommendCount = Integer.parseInt(recommendCount);
		}
	}

	public void setNotRecommendCount(String notRecommendCount) {

		if (notRecommendCount == null || notRecommendCount.equals("")) {
			_notRecommendCount = 0;
		} else {
			_notRecommendCount = Integer.parseInt(notRecommendCount);
		}
	}

	public boolean showRecommendationCount() {
		if (getTotalRecommendations() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getTotalRecommendations() {
		return _notRecommendCount + _recommendCount;
	}

	public String getAverageRating() {
		return _averageRating;
	}

	public double getAverageRatingAsDouble() {
		if (getAverageRating() == null || getAverageRating().length() == 0) {
			return 0.0;
		} else {
			return Double.parseDouble(getAverageRating());
		}

	}

	public void setAverageRating(String averageRating) {

		_averageRating = averageRating;
	}

	public double getRecommendToFriendPercentageString() {

		double retVal = 0.0;
		if (showRecommendationCount()) {
			retVal = ((double) _recommendCount / (double) getTotalRecommendations());
		} else {
			retVal = 0.0;
		}

		return retVal;
	}

	public List<PDP_RatingValue> getRatingValues() {
		return _overallRatingValues;
	}

	public void setOverallRatingValues(List<PDP_RatingValue> overallRatingValues) {
		_overallRatingValues = overallRatingValues;
	}

	public List<PDP_Review> getReviews() {
		return _reviews;
	}

	public void setReviews(List<PDP_Review> reviews) {
		_reviews = reviews;
	}

	public void addRatingValue(PDP_RatingValue pdpRatingValue) {
		if (_overallRatingValues == null) {
			_overallRatingValues = new ArrayList<PDP_RatingValue>();
		}

		_overallRatingValues.add(pdpRatingValue);
	}

	public void addReview(PDP_Review pdpReview) {
		_reviews.add(pdpReview);
	}

	public void loadReviews(int pageToLoad) throws Exception {
		_reviews.clear();
		String host = AppConfig.getReviewsURL();
		StringBuffer query = new StringBuffer();
		query.append("/3545a/");
		query.append(_sku);
		query.append("/reviews.xml");

		String path = query.toString();

		Map<String, String> params = new HashMap<String, String>();
		params.put("apiversion", "4.3");
		params.put("format", "embedded");
		params.put("num", String.valueOf("10"));
		params.put("sort", "helpfulness");
		params.put("dir", "desc");
		params.put("page", pageToLoad + "");

		String results = APIRequest.makeGetRequest(host, path, params, false, false);

		if (results != null && !results.equalsIgnoreCase("")) {
			InputStream is = InputStreamExtensions.stringToInputStream(results);
			PDP_ReviewParser pdpRP = new PDP_ReviewParser();
			pdpRP.setReviewSummary(this);
			pdpRP.parse(is);
		}
	}
	
	public class PDP_ReviewParser {

		private static final String TAG = "PDP_ReviewParser.java";

		private SAXParserFactory _spf;
		private XMLReader _xr;
		private PDP_ReviewSummary _pdpReviewSummary;
		private RZReviewsHandler _reviewsHandler;

		public void parse(InputStream input) {
			_spf = SAXParserFactory.newInstance();
			_spf.setNamespaceAware(true);
			try {
				SAXParser sp = _spf.newSAXParser();
				_xr = sp.getXMLReader();
				_reviewsHandler = new RZReviewsHandler();
				_xr.setContentHandler(_reviewsHandler);
				_xr.parse(new InputSource(input));
			} catch (Exception ex) {
				BBYLog.printStackTrace(TAG, ex);
			}
		}

		public PDP_ReviewSummary getPDPReviewSummary() {
			return _pdpReviewSummary;
		}

		public void setReviewSummary(PDP_ReviewSummary pdpReviewSummary) {
			_pdpReviewSummary = pdpReviewSummary;
		}

		private class RZReviewsHandler extends DefaultHandler {
			private boolean inElement = false;
			private boolean inAverageRatingValues = false;
			private boolean inAverageRatingValue = false;
			private boolean inReviewStatistics = false;
			private boolean inReviews = false;
			private boolean inReview = false;
			private boolean inRatingValues = false;
			private boolean inRatingValue = false;
			private String value;
			private PDP_Review _pdpReview;

			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
				super.startElement(namespaceURI, localName, qName, atts);
				inElement = true;

				value = new String();

				if (localName.equals("ReviewStatistics")) {
					inReviewStatistics = true;
				}

				if (localName.equals("AverageRatingValues")) {
					inAverageRatingValues = true;
				}
				if (localName.equals("AverageRatingValue")) {
					inAverageRatingValue = true;
				}

				if (localName.equals("Reviews")) {
					inReviews = true;
					_pdpReviewSummary.setNumOfPages(atts.getValue("numPages"));
					_pdpReviewSummary.setCurPage(atts.getValue("page"));

				}
				if (localName.equals("Review")) {
					_pdpReview = new PDP_Review();
					_pdpReview.setId(atts.getValue("id"));
					inReview = true;
				}
				if (localName.equals("RatingValues")) {

					inRatingValues = true;
				}
				if (localName.equals("RatingValue")) {
					inRatingValue = true;
				}
			}

			String reviewCategory = "";
			String reviewCategoryRating = "";

			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				super.endElement(uri, localName, qName);
				inElement = false;

				if (inReviewStatistics) {

					if (inAverageRatingValues) {

						if (localName.equals("AverageRatingValues")) {
							inAverageRatingValues = false;
						}

						if (inAverageRatingValue) {

							if (localName.trim().equalsIgnoreCase("AverageRating")) {
								reviewCategoryRating = value;
							}

							if (localName.trim().equalsIgnoreCase("Label1")) {
								reviewCategory = value;
							}

							if (localName.trim().equalsIgnoreCase("AverageRatingValue")) {
								if (!reviewCategory.equals("") && !reviewCategoryRating.equals("")) {
									PDP_RatingValue pdpRV = new PDP_RatingValue(reviewCategoryRating, reviewCategory);
									reviewCategory = "";
									reviewCategoryRating = "";
									_pdpReviewSummary.addRatingValue(pdpRV);
									inAverageRatingValue = false;
								}
							}
						}
					} else {
						if (localName.equals("AverageOverallRating")) {
							_pdpReviewSummary.setAverageRating(value);
						}

						if (localName.equals("RecommendedCount")) {
							_pdpReviewSummary.setRecommendCount(value);
						}

						if (localName.equals("NotRecommendedCount")) {
							_pdpReviewSummary.setNotRecommendCount(value);
						}
					}

					if (localName.equals("ReviewStatistics")) {
						inReviewStatistics = false;
					}
				} else if (inReviews) {

					if (inReview) {
						if (inRatingValues) {

							if (inRatingValue) {
								if (localName.equals("Rating")) {
									reviewCategoryRating = value;
								}
								if (localName.trim().equalsIgnoreCase("Label1")) {
									reviewCategory = value;
								}
								if (localName.equals("RatingValue")) {
									PDP_RatingValue pdpRV = new PDP_RatingValue(reviewCategoryRating, reviewCategory);
									_pdpReview.addRatingValue(pdpRV);
									reviewCategory = "";
									reviewCategoryRating = "";
									inRatingValue = false;

								}
							} else {
								if (localName.equals("RatingValues")) {
									inRatingValues = false;
								}
							}

						} else {

							if (localName.equals("UserNickname")) {
								_pdpReview.setDisplayName(value);
							}
							if (localName.equals("Title")) {
								_pdpReview.setTitle(value);
							}
							if (localName.equals("ReviewText")) {
								_pdpReview.setReviewText(value);
							}
							if (localName.equals("SubmissionTime")) {
								_pdpReview.setSubmissionTime(value);
							}
							if (localName.equals("Rating")) {
								_pdpReview.setRating(value);
							}
							if (localName.equals("Review")) {
								_pdpReviewSummary.addReview(_pdpReview);
								inReview = false;
							}
						}
					}
				}

				value = null;
			}

			public void characters(char ch[], int start, int length) {
				if (inElement) {
					if (length > 0) {
						if (value != null) {
							value = value.concat(new String(ch.clone(), start, length));
						} else {
							value = new String(ch.clone(), start, length);
						}
					}
				}
			}

		}
	}

}
