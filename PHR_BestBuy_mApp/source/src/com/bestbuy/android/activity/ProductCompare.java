package com.bestbuy.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Detail;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AnimationManager;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.BBYAsyncTask;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.QRCodeWorker;
import com.bestbuy.android.util.StarRating;

/**
 * Compares two products side by side and allows for viewing details. Users can
 * choose details to compare on and click through a list of comparable products.
 * 
 * @author Recursive Awesome
 * 
 */
public class ProductCompare extends MenuActivity {
	private final String TAG = this.getClass().getName();
	private Product productLeft;
	private Product productRight;
	private int productIndex = -1;

	private final int MODE_OVERVIEW = 0;
	private final int MODE_TECH_SPECS = 1;
	private int mode = MODE_OVERVIEW;
	private View headerView;
	private GestureDetector gestureDetector;
	private OnTouchListener gestureListener;
	private List<Detail> detailsLeft;
	private List<Detail> detailsRight;
	public static final int MAX_PRODUCTS = 4;
	private Map<String, String> categoryMap;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BBYLog.d(TAG, "oncreate called");
		if (savedInstanceState != null) {
			this.finish(); //Don't try to restore state, there's too much crap.
			return;
		}

		setupCategoryMap();
		setContentView(R.layout.product_compare);
		productLeft = getNextProduct();
		productRight = getNextProduct();
		showView();
		setListeners();
		new LoadProductSpecsTask(this, productLeft).execute();
	}

	public void setupCategoryMap() {
		categoryMap = new HashMap<String, String>();
		categoryMap.put("abcat0100000", "2");
		categoryMap.put("abcat0200000", "3");
		categoryMap.put("abcat0300000", "4");
		categoryMap.put("abcat0400000", "5");
		categoryMap.put("abcat0500000", "6");
		categoryMap.put("abcat0600000", "7");
		categoryMap.put("abcat0700000", "8");
		categoryMap.put("abcat0801000", "9");
		categoryMap.put("abcat0900000", "11");
		categoryMap.put("abcat0800000", "10");
		categoryMap.put("abcat0207000", "12");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			appData.setCompareMode(false);
			appData.getCompareProducts().clear();
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		//Coming back from browsing for a product or a scan
		super.onNewIntent(intent);
		if (!intent.hasExtra("Scan")) {
			//User browsed for product instead of scanning
			if (appData.getCompareProducts().size() == 1) {
				productLeft = appData.getCompareProducts().get(0);
				productRight = null;
				showView();
				new LoadProductSpecsTask(this, productLeft).execute();
			} else {
				productRight = appData.getCompareProducts().get(appData.getCompareProducts().size() - 1);
				if (productRight.equals(productLeft)) {
					productRight = null;
				} else {
					showView();
					new LoadProductSpecsTask(this, productRight).execute();
				}
			}
		} else {
			//User scanned product
			if (appData.getCompareProducts().size() == 1) {
				productLeft = appData.getCompareProducts().get(0);
				productRight = null;
			} else {
				productRight = appData.getCompareProducts().get(appData.getCompareProducts().size()-1);
				if (productRight.equals(productLeft)) {
					productRight = null;
				}
			}
			showView();
		}
	}

	public void showView() {
		ImageView previous = (ImageView) findViewById(R.id.product_compare_button_prev);
		ImageView next = (ImageView) findViewById(R.id.product_compare_button_next);
		ListView specsList = (ListView) findViewById(R.id.product_compare_listview);

		if (specsList.getHeaderViewsCount() == 0) {
			headerView = getLayoutInflater().inflate(R.layout.product_compare_header, null);
			specsList.addHeaderView(headerView);
		}

		if (appData.getCompareProducts().isEmpty()) {
			findViewById(R.id.product_compare_left).setVisibility(View.GONE);
			findViewById(R.id.product_compare_left_title).setVisibility(View.GONE);
		} else {
			findViewById(R.id.product_compare_left).setVisibility(View.VISIBLE);
			findViewById(R.id.product_compare_left_title).setVisibility(View.VISIBLE);
		}

		if (appData.getCompareProducts().isEmpty() || productIndex <= 0 || productLeft.getSku().equals(appData.getCompareProducts().get(0).getSku())) {
			previous.setVisibility(View.INVISIBLE);
		} else {
			previous.setVisibility(View.VISIBLE);
		}

		if (appData.getCompareProducts().isEmpty() || productIndex == appData.getCompareProducts().size() || productIndex == MAX_PRODUCTS-1) {
			next.setVisibility(View.INVISIBLE);
		} else {
			next.setVisibility(View.VISIBLE);
		}

		if (mode == MODE_TECH_SPECS) {
			showTechSpecs();
		} else if (mode == MODE_OVERVIEW) {
			showOverview();
		}

		findViewById(R.id.product_compare_listview).setOnTouchListener(gestureListener);
	}

	public void setListeners() {
		ImageView previous = (ImageView) findViewById(R.id.product_compare_button_prev);
		ImageView next = (ImageView) findViewById(R.id.product_compare_button_next);
		ImageView techSpecs = (ImageView) findViewById(R.id.product_compare_tech_specs);
		ImageView overview = (ImageView) findViewById(R.id.product_compare_overview);
		ImageView productImageLeft = (ImageView) findViewById(R.id.product_compare_left_image);
		ImageView productImageRight = (ImageView) findViewById(R.id.product_compare_right_image);
		Button scanButton = (Button) findViewById(R.id.product_compare_right_button_scan);
		Button rightRemoveButton = (Button) findViewById(R.id.product_compare_right_button_remove);
		Button leftRemoveButton = (Button) findViewById(R.id.product_compare_left_button_remove);

		gestureDetector = new GestureDetector(new SimpleOnGestureListener() {
			private static final int SWIPE_MIN_DISTANCE = 120;
			private static final int SWIPE_MAX_OFF_PATH = 250;
			private static final int SWIPE_THRESHOLD_VELOCITY = 200;

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				ImageView previous = (ImageView) findViewById(R.id.product_compare_button_prev);
				ImageView next = (ImageView) findViewById(R.id.product_compare_button_next);
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
					return false;
				}
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					BBYLog.d(TAG, "right to left swipe");
					if (next.isShown()) {
						next.performClick();
					}
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					BBYLog.d(TAG, "left to right swipe");
					if (previous.isShown()) {
						previous.performClick();
					}
				}
				return false;
			}
		});

		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		productImageLeft.setOnTouchListener(gestureListener);
		productImageRight.setOnTouchListener(gestureListener);
		scanButton.setOnTouchListener(gestureListener);
		rightRemoveButton.setOnTouchListener(gestureListener);
		leftRemoveButton.setOnTouchListener(gestureListener);

		previous.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (appData.getCompareProducts().size() > 1) {
					productRight = productLeft;
					productLeft = getPreviousProduct();
					//If we last hit 'next', we need to go one more back.
					if (productLeft != null && productLeft.getSku().equals(productRight.getSku())) {
						productLeft = getPreviousProduct();
					}
					AnimationManager.runSlideInFromLeftOn(v.getContext(), findViewById(R.id.product_compare_left_title));
					Animation anim = AnimationManager.runSlideInFromLeftOn(v.getContext(), findViewById(R.id.product_compare_right_title));
					anim.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
						}

						public void onAnimationRepeat(Animation animation) {
						}

						public void onAnimationStart(Animation animation) {
							showView();
						}
					});
					AnimationManager.runSlideInFromLeftOn(v.getContext(), findViewById(R.id.product_compare_left));
					AnimationManager.runSlideInFromLeftOn(v.getContext(), findViewById(R.id.product_compare_right));
				}
			}
		});

		next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (appData.getCompareProducts().size() > 1) {
					productLeft = productRight;
					productRight = getNextProduct();
					//If we last hit 'previous', we need to go one more back.
					if (productRight != null && productRight.getSku().equals(productLeft.getSku())) {
						productRight = getNextProduct();
					}
					AnimationManager.runSlideInFromRightOn(v.getContext(), findViewById(R.id.product_compare_left_title));
					Animation anim = AnimationManager.runSlideInFromRightOn(v.getContext(), findViewById(R.id.product_compare_right_title));
					anim.setAnimationListener(new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
						}

						public void onAnimationRepeat(Animation animation) {
						}

						public void onAnimationStart(Animation animation) {
							showView();
						}
					});
					AnimationManager.runSlideInFromRightOn(v.getContext(), findViewById(R.id.product_compare_left));
					AnimationManager.runSlideInFromRightOn(v.getContext(), findViewById(R.id.product_compare_right));
				}
			}
		});

		techSpecs.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mode = MODE_TECH_SPECS;
				showView();
			}
		});

		overview.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mode = MODE_OVERVIEW;
				showView();
			}
		});
	}

	public Product getNextProduct() {
		if (productIndex < appData.getCompareProducts().size()) {
			productIndex++;
		}
		if (productIndex == appData.getCompareProducts().size()) {
			return null;
		} else {
			return appData.getCompareProducts().get(productIndex);
		}
	}

	public Product getPreviousProduct() {
		if (productIndex > 0) {
			productIndex--;
		}
		return appData.getCompareProducts().get(productIndex);
	}

	public void showLeftProduct() {
		TextView title = (TextView) findViewById(R.id.product_compare_left_title);
		TextView model = (TextView) findViewById(R.id.product_compare_left_model);
		TextView sku = (TextView) findViewById(R.id.product_compare_left_sku);
		ImageView productImage = (ImageView) findViewById(R.id.product_compare_left_image);
		TextView price = (TextView) findViewById(R.id.product_compare_left_price);
		ImageView rating = (ImageView) findViewById(R.id.product_compare_left_review_stars);
		TextView ratingScore = (TextView) findViewById(R.id.product_compare_left_review_score);
		Button removeButton = (Button) findViewById(R.id.product_compare_left_button_remove);

		if (productLeft != null) {
			//Fill in data
			title.setText(productLeft.getName());
			ImageProvider.getBitmapImageOnThread(productLeft.getBestImageURL(), productImage);
			model.setText(productLeft.getModelNumber());
			sku.setText(productLeft.getSku());
			price.setText("$" + productLeft.getSaleOrRegularPrice());
			rating.setImageBitmap(StarRating.getAssociatedStarImage(productLeft.getCustomerReviewAverage(), this));
			ratingScore.setText(productLeft.getCustomerReviewAverage());

			//Set listener on product image
			productImage.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					appData.setSelectedProduct(productLeft);
					Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
					startActivity(i);
				}
			});
			removeButton.setVisibility(View.VISIBLE);
		}

		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				appData.getCompareProducts().remove(productLeft);
				if (appData.getCompareProducts().isEmpty()) {
					productLeft = null;
				} else {
					productIndex = -1;
					productLeft = getNextProduct();
					productRight = getNextProduct();
				}
				showView();
			}
		});
	}

	public void showRightProduct() {
		TextView title = (TextView) findViewById(R.id.product_compare_right_title);
		TextView model = (TextView) findViewById(R.id.product_compare_right_model);
		TextView sku = (TextView) findViewById(R.id.product_compare_right_sku);
		ImageView productImage = (ImageView) findViewById(R.id.product_compare_right_image);
		TextView price = (TextView) findViewById(R.id.product_compare_right_price);
		ImageView rating = (ImageView) findViewById(R.id.product_compare_right_review_stars);
		TextView ratingScore = (TextView) findViewById(R.id.product_compare_right_review_score);
		Button browseButton = (Button) findViewById(R.id.product_compare_right_button_browse);
		Button scanButton = (Button) findViewById(R.id.product_compare_right_button_scan);
		Button removeButton = (Button) findViewById(R.id.product_compare_right_button_remove);
		ImageView defaultImage = (ImageView) findViewById(R.id.product_compare_right_default_image);
		TextView defaultText = (TextView) findViewById(R.id.product_compare_right_default_text);

		OnClickListener browseListener = new OnClickListener() {
			public void onClick(View v) {
				String smalCategory = null;
				String remixCategory = null;
				if (productLeft != null) {
					for (String category : productLeft.getCategoryPath()) {
						if (categoryMap.containsKey(category)) {
							smalCategory = categoryMap.get(category);
							remixCategory = category;
						}
					}
				}
				Intent i = new Intent(v.getContext(), CategoryBrowse.class);
				if (smalCategory != null) {
					i.putExtra(AppData.CATEGORY_ID, smalCategory);
					i.putExtra(AppData.REMIX_CATEGORY_ID, remixCategory);
				}
				i.putExtra(AppData.IS_CODE_SCAN, true);
				startActivity(i);
				appData.setCompareMode(true);
			}
		};

		browseButton.setOnClickListener(browseListener);
		defaultImage.setOnClickListener(browseListener);

		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new QRCodeWorker().openQRCode(ProductCompare.this,"CODE_SCAN");
			}
		});

		removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				appData.getCompareProducts().remove(productRight);
				productRight = getNextProduct();
				if (productRight != null && productRight.equals(productLeft)) {
					productRight = getNextProduct();
				}
				showView();
			}
		});

		if (productRight != null) {
			title.setVisibility(View.VISIBLE);
			removeButton.setVisibility(View.VISIBLE);
			scanButton.setVisibility(View.GONE);
			browseButton.setVisibility(View.GONE);
			findViewById(R.id.product_compare_right_info).setVisibility(View.VISIBLE);

			title.setText(productRight.getName());
			ImageProvider.getBitmapImageOnThread(productRight.getBestImageURL(), productImage);
			model.setText(productRight.getModelNumber());
			sku.setText(productRight.getSku());
			price.setText("$" + productRight.getSaleOrRegularPrice());
			rating.setImageBitmap(StarRating.getAssociatedStarImage(productRight.getCustomerReviewAverage(), this));
			ratingScore.setText(productRight.getCustomerReviewAverage());

			productImage.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					appData.setSelectedProduct(productRight);
					Intent i = new Intent(v.getContext(), MDOTProductDetail.class);
					startActivity(i);
				}
			});
			removeButton.setVisibility(View.VISIBLE);
			defaultImage.setVisibility(View.GONE);
			defaultText.setVisibility(View.GONE);
		} else {
			defaultImage.setVisibility(View.VISIBLE);
			defaultText.setVisibility(View.VISIBLE);
			scanButton.setVisibility(View.VISIBLE);
			browseButton.setVisibility(View.VISIBLE);
			title.setText("");
			removeButton.setVisibility(View.GONE);
			findViewById(R.id.product_compare_right_info).setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Loads the details for the list view for both products
	 * 
	 * @author Recursive Awesome
	 * 
	 */
	private class LoadProductSpecsTask extends BBYAsyncTask {
		private Product product;

		public LoadProductSpecsTask(Activity activity, Product product) {
			super(activity);
			this.product = product;
			setLoadingSpinner(headerView.findViewById(R.id.product_compare_loading));
		}

		@Override
		public void doFinish() {
			showView();
		}

		@Override
		public void doReconnect() {
			new LoadProductSpecsTask(activity, product).execute();
		}

		@Override
		public void doTask() throws Exception {
			final String srchShow = Product.SKU + "," + Product.MANUFACTURER + "," + Product.DETAILS + "," + Product.CATEGORY_PATH;
			String query = "products(sku in(" + product.getSku() + "))";
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("show", srchShow);
			parameters.put("apiKey", AppConfig.getRemixApiKey());
			parameters.put("format", "json");
			String results = APIRequest.makeGetRequest(AppConfig.getRemixURL() + "/v1", query, parameters, false);
			JSONObject jsonResults = new JSONObject(results);
			JSONArray products = jsonResults.getJSONArray("products");

			//Load product details for compare products
			JSONObject jsonProductObj = products.optJSONObject(0);
			if (jsonProductObj != null) {
				product.loadDetailsData(jsonProductObj);
			}
		}
	}

	public void showOverview() {
		ImageView techSpecsTab = (ImageView) findViewById(R.id.product_compare_tech_specs);
		ImageView overviewTab = (ImageView) findViewById(R.id.product_compare_overview);
		//LinearLayout whatsDifferent = (LinearLayout) findViewById(R.id.product_compare_header_whats_different);
		ListView specsList = (ListView) findViewById(R.id.product_compare_listview);
		overviewTab.setImageResource(R.drawable.btn_overview_down);
		techSpecsTab.setImageResource(R.drawable.btn_techspecs_norm);

		/*if (productLeft == null) {
			//No products in compare.  Hide the "What's different" and GTFO
			whatsDifferent.setVisibility(View.GONE);
			return;
		} else if (productRight == null) {
			//One product in compare.  Hide the "What's different"
			whatsDifferent.setVisibility(View.GONE);
		} else {
			whatsDifferent.setVisibility(View.VISIBLE);
		}*/
		
		specsList.setClickable(false);

		detailsLeft = new ArrayList<Detail>();
		detailsRight = new ArrayList<Detail>();
		
		if(productLeft != null)
			detailsLeft.addAll(productLeft.getDetails());
		
		if (productRight != null) {
			detailsRight.addAll(productRight.getDetails());
			detailsRight.retainAll(detailsLeft);
			detailsLeft.retainAll(detailsRight);

			//Trim out any details that match
			ArrayList<Detail> detailsToRemoveLeft = new ArrayList<Detail>();
			ArrayList<Detail> detailsToRemoveRight = new ArrayList<Detail>();
			for (int i = 0; i < detailsLeft.size(); i++) {
				Detail detailLeft = detailsLeft.get(i);
				Detail detailRight = detailsRight.get(i);
				if (detailLeft.getValue().equals(detailRight.getValue())) {
					detailsToRemoveLeft.add(detailLeft);
					detailsToRemoveRight.add(detailRight);
				}
			}
			detailsLeft.removeAll(detailsToRemoveLeft);
			detailsRight.removeAll(detailsToRemoveRight);
		} /*else {
			detailsLeft.clear();
		}*/

		specsList.setAdapter(new CategoryAdapter());

		//Show overview specs
		RelativeLayout leftOverview = (RelativeLayout) findViewById(R.id.product_compare_left_overview);
		RelativeLayout rightOverview = (RelativeLayout) findViewById(R.id.product_compare_right_overview);
		leftOverview.setVisibility(View.VISIBLE);
		rightOverview.setVisibility(View.VISIBLE);

		showLeftProduct();
		showRightProduct();
	}

	public void showTechSpecs() {
		ImageView techSpecsTab = (ImageView) findViewById(R.id.product_compare_tech_specs);
		ImageView overviewTab = (ImageView) findViewById(R.id.product_compare_overview);
		techSpecsTab.setImageResource(R.drawable.btn_techspecs_down);
		overviewTab.setImageResource(R.drawable.btn_overview_norm);
		LinearLayout whatsDifferent = (LinearLayout) findViewById(R.id.product_compare_header_whats_different);
		whatsDifferent.setVisibility(View.GONE);
		ListView specsList = (ListView) findViewById(R.id.product_compare_listview);
		
		detailsLeft = new ArrayList<Detail>();
		detailsRight = new ArrayList<Detail>();
		
		if (productLeft == null) {
			specsList.setAdapter(new CategoryAdapter());
			return;
		}

		specsList.setClickable(false);
		
		detailsLeft.addAll(productLeft.getDetails());
		if (productRight != null) {
			for (Detail detailLeft : detailsLeft) {
				//For each detail on the lhs, if the right product has the same detail include it, else show an M-dash
				if (productRight.getDetails().contains(detailLeft)) {
					int detailIndex = productRight.getDetails().indexOf(detailLeft);
					detailsRight.add(productRight.getDetails().get(detailIndex));
				} else {
					detailsRight.add(new Detail(detailLeft.getName(), "-"));
				}
			}
		}

		specsList.setAdapter(new CategoryAdapter());

		//Hide overview specs
		RelativeLayout leftOverview = (RelativeLayout) findViewById(R.id.product_compare_left_overview);
		RelativeLayout rightOverview = (RelativeLayout) findViewById(R.id.product_compare_right_overview);
		leftOverview.setVisibility(View.GONE);
		rightOverview.setVisibility(View.GONE);

		showLeftProduct();
		showRightProduct();
	}

	class CategoryAdapter extends ArrayAdapter<Detail> {
		public CategoryAdapter() {
			super(ProductCompare.this, R.layout.product_compare_row, detailsLeft);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				row = getLayoutInflater().inflate(R.layout.product_compare_row, parent, false);
			}
			TextView rightDetail = (TextView) row.findViewById(R.id.product_compare_row_right_detail);

			if (detailsLeft != null && detailsLeft.size() > position) {
				Detail detailLeft = detailsLeft.get(position);
				((TextView) row.findViewById(R.id.product_compare_row_header)).setText(detailLeft.getName());
				((TextView) row.findViewById(R.id.product_compare_row_left_detail)).setText(detailLeft.getValue());
			}

			if (detailsRight == null) {
				rightDetail.setVisibility(View.GONE);
			} else if (detailsRight.size() > position) {
				Detail detailRight = detailsRight.get(position);
				rightDetail.setText(detailRight.getValue());
				rightDetail.setVisibility(View.VISIBLE);
			}
			return row;
		}

		public boolean areAllItemsEnabled() {
			return false;
		}

		public boolean isEnabled(int position) {
			return false;
		}
	}
}