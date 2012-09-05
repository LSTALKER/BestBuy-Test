package com.bestbuy.android.rewardzone;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.util.BBYLog;

/**
 * Reward zone offer object
 * 
 * @author Recursive Awesome
 * 
 */
public class RZOffer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String TAG = this.getClass().getName();
	private String title;
	private Date expiration;
	private Date start;
	private String shortDescription;
	private String longDescription;
	private String url;
	private String imageUrl;
	private String legalCopy;
	private boolean isCoreOffer;
	private boolean isSilverOffer;
	private String key;
		

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageUrl() {
		return this.imageUrl;
		//return AppConfig.getBBYServURL() + this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
	public boolean showExpiration() {
		if (getExpiration() == null) {
			return false;
		}
		DateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		DateFormat outFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.US);
		try {
			Date outputDate = outFormat.parse(inFormat.format(getExpiration()));
			Calendar fifteenDaysFromNow = Calendar.getInstance();
			fifteenDaysFromNow.add(Calendar.DAY_OF_YEAR, 15);
			return (outputDate.getTime() <= fifteenDaysFromNow.getTime().getTime());
		} catch (ParseException e) {
			BBYLog.printStackTrace(TAG, e);
		}
		return false;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public void setLegalCopy(String legalCopy) {
		this.legalCopy = legalCopy;
	}

	public String getLegalCopy() {
		return legalCopy;
	}

	public void loadData(JSONObject obj) {
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			DateFormat outFormat = new SimpleDateFormat("MM/dd/yyyy"); 
			this.title = obj.optString("title", "No title for this offer.");
			this.expiration = outFormat.parse(outFormat.format(df.parse(obj.optString("end"))));
			this.start = df.parse(obj.optString("begin"));
			this.shortDescription = Product.replaceXMLCharacters(obj.optString("short_marketing_copy"));
			this.longDescription = Product.replaceXMLCharacters(obj.optString("description"));
			this.url = obj.optString("url");
			this.imageUrl = obj.optString("image_url", AppData.JSON_NULL);
			this.legalCopy = Product.replaceXMLCharacters(obj.optString("legal"));
			this.key = obj.optString("key", "");
			JSONArray jsonTiers = obj.getJSONArray("rz_levels");
			if (jsonTiers.toString().contains("core")) {
				this.isCoreOffer = true;
			}
			if (jsonTiers.toString().contains("silver")) {
				this.isSilverOffer = true;
			}
		} catch (Exception ex) {
			BBYLog.printStackTrace(TAG, ex);
			BBYLog.e(TAG, "Exception in loadOffersData() : " + ex.getMessage());
		}
	}

	public void setSilverOffer(boolean isSilverOffer) {
		this.isSilverOffer = isSilverOffer;
	}

	public boolean isSilverOffer() {
		return isSilverOffer;
	}

	public void setCoreOffer(boolean isCoreOffer) {
		this.isCoreOffer = isCoreOffer;
	}

	public boolean isCoreOffer() {
		return isCoreOffer;
	}

	@Override
	public String toString() {
		return "RZOffer [TAG=" + TAG + ", title=" + title + ", expiration=" + expiration + ", shortDescription=" + shortDescription
				+ ", longDescription=" + longDescription + ", url=" + url + ", imageUrl=" + imageUrl + ", legalCopy=" + legalCopy
				+ ", isCoreOffer=" + isCoreOffer + ", isSilverOffer=" + isSilverOffer + "]";
	}

}
