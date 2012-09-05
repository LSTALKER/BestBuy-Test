package com.bestbuy.android.openbox.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.activity.MenuActivity;
import com.bestbuy.android.activity.PhotoBrowser;
import com.bestbuy.android.activity.ProductReview;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.BBYAlertDialog;
import com.bestbuy.android.util.BBYLog;
import com.bestbuy.android.util.FontLibrary;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.ShareProduct;
import com.bestbuy.android.util.StarRating;
import com.bestbuy.android.util.UTFCharacters.UTF;

public class OpenBoxClearanceItemDetailsActivity extends MenuActivity {
	private OpenBoxItem openBoxItem;
	private String itemCategoryType;
	private ImageView itemImage;
	private TextView itemCategory;
	private TextView itemTitle;
	private ImageView reviewStars;
	private TextView previousPrice;
	private TextView currentPrice;
	private WebView detailsAndTerms;
	private TextView customerReviewAvgText;
	private TextView regdata;
	private TextView reviewTitle;
	private TextView disclamier;
	private RelativeLayout reviews;
	private ImageView arrow;
	private ImageButton helpIb;
	private ImageButton shareIb;
	
   @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openbox_product_item);
		
		Intent intent = getIntent();
		if(intent != null){
			openBoxItem = (OpenBoxItem) intent.getSerializableExtra(StoreUtils.OPENBOX_ITEM);
			itemCategoryType = intent.getStringExtra(StoreUtils.PRODUCT_TYPE);
		}
		
		itemImage = (ImageView) findViewById(R.id.product_item);
		itemCategory = (TextView) findViewById(R.id.product_item_category);
		itemTitle = (TextView) findViewById(R.id.product_item_title);
		previousPrice = (TextView) findViewById(R.id.previous_price);
		currentPrice= (TextView) findViewById(R.id.current_price);
		detailsAndTerms= (WebView) findViewById(R.id.details_and_terms);
		reviewStars = (ImageView) findViewById(R.id.openbox_star_rating);
		customerReviewAvgText = (TextView) findViewById(R.id.openbox_customer_review_avg_text);
		reviewTitle = (TextView) findViewById(R.id.openbox_customer_reviews); 
		arrow = (ImageView)findViewById(R.id.openbox_customer_reviews_carrot);
		regdata = (TextView) findViewById(R.id.reg_data);
		disclamier = (TextView)findViewById(R.id.disclaimer_text);
		helpIb = (ImageButton)findViewById(R.id.openbox_pdp_help_ib);
		shareIb =(ImageButton)findViewById(R.id.openbox_pdp_share_ib);
		

		itemImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadDescription();
			}
		});
		
		helpIb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(OpenBoxClearanceItemDetailsActivity.this == null || OpenBoxClearanceItemDetailsActivity.this.isFinishing()) { // Use ||, Do not use | 
					return;
				}
				AlertDialog alertDialog = new AlertDialog.Builder(_context).create();
				alertDialog.setTitle("Call Best Buy?");
				alertDialog.setMessage("Would you like to call Best Buy?");
				alertDialog.setButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:18882378289"));
								startActivity(dialIntent);
							}
						});
				alertDialog.setButton2("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								return;
							}
						});
				alertDialog.show();
			}
		});
		
		shareIb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new ShareProduct(OpenBoxClearanceItemDetailsActivity.this, openBoxItem, true).execute();
			}
		});
		
		StringBuffer  disclaimer_txt = new StringBuffer();
		disclaimer_txt.append("<b><font color='#231f20'><font size='13'>See store for availability & details. No Rainchecks. </font></font></b>");
		disclaimer_txt.append("<font color='#231f20'><font size='13'>See store for warranty and return policy questions. Items will be sold on a first come, first served basis.</font></font>");             
		disclamier.setText(Html.fromHtml(disclaimer_txt.toString()));
		
		reviews = (RelativeLayout) findViewById(R.id.openbox_reviews);
		if (!(openBoxItem.getCustomerReviewCount() == 0)) {
		reviews.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), ProductReview.class);
				i.putExtra(Product.SKU, openBoxItem.getSku());
				startActivity(i);
			}
		});
		}else{
			arrow.setVisibility(View.GONE);
		}
		
		String rating = openBoxItem.getCustomerReviewAverage();
		reviewStars.setImageBitmap(StarRating.getAssociatedStarImage(rating, this));
		
		int htmlContentHeight=detailsAndTerms.getContentHeight();
		BBYLog.i("Html Content Height", ""+htmlContentHeight);
		detailsAndTerms.setVerticalFadingEdgeEnabled(false);				
		detailsAndTerms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY) ;
		WebSettings webSettings = detailsAndTerms.getSettings();
		webSettings.setDefaultFontSize(13);
		
		if(openBoxItem != null){
			setValues(openBoxItem);
		}
	}
	
	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		reader.close();
		return sb.toString();
	}
	
	private void setValues(OpenBoxItem openBoxItem) {
		Typeface tf = FontLibrary.getFont(R.string.droidsansbold, getResources());	  
		itemCategory.setTypeface(tf);
		if(itemCategoryType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)){			     
			itemCategory.setText("Open Box Item");
			 regdata.setText("Reg. Price is price in store (last 30 days).");
		}else if(itemCategoryType.equalsIgnoreCase(StoreUtils.CLEARANCE_ITEMS)){
			itemCategory.setText("Clearance Item");
			regdata.setText("Reg. Price is the BestBuy.com Reg. Price.");
		}
		if(openBoxItem.getItemTitle() != null)
			itemTitle.setText(UTF.replaceNonUTFCharacters(openBoxItem.getItemTitle()));       		
		if(itemCategoryType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)){			
			if(openBoxItem.getSellingPrice() !=null && !openBoxItem.getSellingPrice().equals("")){
				currentPrice.setText(String.format("$%s",StoreUtils.createFormatedPriceString(openBoxItem.getSellingPrice())));
				previousPrice.setText(String.format("Regular Price: $%s",StoreUtils.createFormatedPriceString(openBoxItem.getRegularPrice())));
			}else{
				currentPrice.setTextSize(14);
				currentPrice.setText("Visit store for details");
			    previousPrice.setVisibility(View.GONE);
			}
		}else{			
			currentPrice.setText(String.format("$%s",StoreUtils.createFormatedPriceString(openBoxItem.getSellingPrice())));
			previousPrice.setText("Regular Price: $"+StoreUtils.createFormatedPriceString(openBoxItem.getRegularPrice()));
		}				

		if(openBoxItem.getDetailsAndTerms() != null) {        	        
			BBYLog.d("OpenBox HTML Content**************", "CAME HERE:"+openBoxItem.getDetailsAndTerms());        	
        	String s="<head><meta name=viewport content=target-densitydpi=device-dpi</head>";  
        	String actualContent = openBoxItem.getDetailsAndTerms().replaceAll("%", "&#37;");
        	String content = s+"<style>.contentStyle { font-family: 'Helvetica'; font-size: 10pt; }</style><div class='contentStyle'>"+actualContent+"</div>";
        	detailsAndTerms.loadDataWithBaseURL(null,content,"text/html" , "utf-8",null);
        }
		
        StringBuffer reviewCount =new StringBuffer();
        if(openBoxItem.getCustomerReviewCount()<=1){
        	reviewCount.append("Review ");
        	reviewCount.append(String.format("(%s)",openBoxItem.getCustomerReviewCount()));
        	reviewTitle.setText(reviewCount.toString());
		} else {
        	reviewCount.append("Reviews ");
        	reviewCount.append(String.format("(%s)",openBoxItem.getCustomerReviewCount()));
        	reviewTitle.setText(reviewCount.toString());
        }
        
        customerReviewAvgText.setText(String.valueOf(openBoxItem.getCustomerReviewAverage()));
        String imageUrl = openBoxItem.getLargeImage();
        if(imageUrl != null && !imageUrl.equals("")) {
			ImageProvider.getBitmapImageOnThread(imageUrl, itemImage);
			BBYLog.d("Image URL=========!!!!!!=============>", imageUrl);
		} else {
			String alternateImageUrl = openBoxItem.getItemImage();
			  if(alternateImageUrl != null && (!alternateImageUrl.equals(""))){
                  ImageProvider.getBitmapImageOnThread(alternateImageUrl, itemImage);   
			  }
		}
	}
	
	private void loadDescription() {
		Product product = new Product();
		product.setSku(openBoxItem.getSku());
		product.setName(openBoxItem.getItemTitle());
		product.setImageURL(openBoxItem.getLargeImage());
		product.setUrl(openBoxItem.getUrl());
		appData.setSelectedProduct(product);
		Intent i = new Intent(this, PhotoBrowser.class);
		startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		
		if(this == null || this.isFinishing()) { // Use ||, Do not use | 
			return dialog;
		}
			
		return new BBYAlertDialog(this).createAlertDialog(id);
	}
}
