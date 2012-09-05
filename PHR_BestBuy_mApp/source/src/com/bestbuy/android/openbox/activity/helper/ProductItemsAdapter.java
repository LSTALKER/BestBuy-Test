package com.bestbuy.android.openbox.activity.helper;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestbuy.android.R;
import com.bestbuy.android.openbox.library.dataobject.OpenBoxItem;
import com.bestbuy.android.storeevent.util.StoreUtils;
import com.bestbuy.android.util.ImageProvider;
import com.bestbuy.android.util.StarRating;
import com.bestbuy.android.util.UTFCharacters.UTF;

public class ProductItemsAdapter extends BaseAdapter {
	
	private List<OpenBoxItem> productItemList;
	private LayoutInflater inflater;
    private Context context;
    private String productType;

	public ProductItemsAdapter(Context context, List<OpenBoxItem> productItemList, String productType) {
		this.context = context;
		this.productItemList = productItemList;
		this.productType = productType;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
    
	public int getCount() {
		if (productItemList != null) {
			return productItemList.size();
		}
		return 0;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public OpenBoxItem getItem(int position) {
		return productItemList.get(position);
	}

	public static class ViewHolder {
		private TextView productTitle, productRate,productRatings;
		private ImageView productIcon, starRating;
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		View customView = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			customView = inflater.inflate(R.layout.openbox_category_list_item, null);
			holder = new ViewHolder();
			holder.productTitle = (TextView) customView.findViewById(R.id.category_title);
			holder.productRate = (TextView) customView.findViewById(R.id.prize_label);
			holder.productIcon = (ImageView) customView.findViewById(R.id.product_icon);
			holder.starRating = (ImageView) customView.findViewById(R.id.star_rating);
			holder.productRatings=(TextView) customView.findViewById(R.id.openbox_product_rating_bar_text);

			customView.setTag(holder);

		} else
			holder = (ViewHolder) customView.getTag();

		OpenBoxItem openboxItem = getItem(position);
		
		if (openboxItem.getItemTitle() != null)
			holder.productTitle.setText(UTF.replaceNonUTFCharacters(openboxItem.getItemTitle()));

		if (productType.equalsIgnoreCase(StoreUtils.OPENBOX_ITEMS)) {
			if (openboxItem.getSellingPrice() != null && !openboxItem.getSellingPrice().equals("")) {
				holder.productRate.setText("$" + StoreUtils.createFormatedPriceString(openboxItem.getSellingPrice()));
			} else {
				holder.productRate.setTextSize(13);
				holder.productRate.setText("Visit store for details");
			}
		} else {
			holder.productRate.setText("$" + StoreUtils.createFormatedPriceString(openboxItem.getSellingPrice()));
		}
		String imageUrl = openboxItem.getLargeImage();

		String rating = openboxItem.getCustomerReviewAverage();
		holder.starRating.setImageBitmap(StarRating.getAssociatedStarImage(rating, this.context));
		holder.productRatings.setText(rating);

		if (imageUrl != null && !imageUrl.equals("")) {
			ImageProvider.getBitmapImageOnThread(imageUrl, holder.productIcon);
		}

		return customView;
	}
	
	public void setItems(List<OpenBoxItem> collectionItemList) {
		this.productItemList = collectionItemList;
		this.notifyDataSetChanged();
	}
}
