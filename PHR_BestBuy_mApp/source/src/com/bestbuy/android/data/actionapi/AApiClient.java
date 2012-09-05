package com.bestbuy.android.data.actionapi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bestbuy.android.data.AppData;
import com.bestbuy.android.data.Product;
import com.bestbuy.android.util.APIRequest;
import com.bestbuy.android.util.AppConfig;
import com.bestbuy.android.util.Link;

public class AApiClient {

	private ArrayList<AApiAction> actions;

	// valid user actions
	public static final String UA_VIEWED = "viewed";
	public static final String UA_CLICKED = "clicked";
	public static final String UA_LIKED = "liked";
	public static final String UA_DISLIKED = "disliked";
	public static final String UA_PURCHASED = "purchased";

	public AApiClient() {
		actions = new ArrayList<AApiAction>();
	}

	// TODO decide if we need to retain actions here or change the load methods
	// to get methods and return the actions that way.
	public ArrayList<AApiAction> getActionsList() {
		return actions;
	}

	public void loadActionsForCustomerAccount(String custId) throws Exception {
		loadActionsForFormattedId(custId);
	}

	public void loadActionsForRewardZoneId(String rzId) throws Exception {
		loadActionsForFormattedId(formatRZId(rzId));
	}

	public void loadActionsForATGId(String atgId) throws Exception {
		loadActionsForFormattedId(formatATGId(atgId));
	}

	public void loadActionsForEmail(String email) throws Exception {
		loadActionsForFormattedId(email.toUpperCase());
	}

	public void loadActionsForPhoneNumber(String phone) throws Exception {
		loadActionsForFormattedId(formatPhoneNumber(phone));
	}

	public void postProductRecommendationForCustomerAccount(String custId, String sku, String action) throws Exception {
		postProductRecommendation(custId, sku, action);
	}

	public void postProductRecommendationForRewardZoneId(String rzId, String sku, String action) throws Exception {
		postProductRecommendation(formatRZId(rzId), sku, action);
	}

	public void postProductRecommendationForATGId(String atgId, String sku, String action) throws Exception {
		postProductRecommendation(formatATGId(atgId), sku, action);
	}

	public void postProductRecommendationForEmail(String email, String sku, String action) throws Exception {
		postProductRecommendation(email.toUpperCase(), sku, action);
	}

	public void postProductRecommendationForPhoneNumber(String phone, String sku, String action) throws Exception {
		postProductRecommendation(formatPhoneNumber(phone), sku, action);
	}

	public void postPhaseFeedbackForCustomerAccount(String custId, String sku, String action) throws Exception {
		postPhaseFeedback(custId, sku, action);
	}

	public void postPhaseFeedbackForRewardZoneId(String rzId, String sku, String action) throws Exception {
		postPhaseFeedback(formatRZId(rzId), sku, action);
	}

	public void postPhaseFeedbackForATGId(String atgId, String sku, String action) throws Exception {
		postPhaseFeedback(formatATGId(atgId), sku, action);
	}

	public void postPhaseFeedbackForEmail(String email, String sku, String action) throws Exception {
		postPhaseFeedback(email.toUpperCase(), sku, action);
	}

	public void postPhaseFeedbackForPhoneNumber(String phone, String sku, String action) throws Exception {
		postPhaseFeedback(formatPhoneNumber(phone), sku, action);
	}
	
	private void loadActionsForFormattedId(String id) throws Exception {

		final Map<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("apiKey", AppConfig.getActionApiKey());

		final String result = APIRequest.makeGetRequest(AppConfig.getActionApiURL(), AppData.BBY_ACTION_API_PATH + id + AppData.BBY_ACTION_API_GET_PATH, urlParams, false);
		final JSONObject actionsObject = new JSONObject(result);
		final JSONArray actionsArray = actionsObject.getJSONArray("actions");

		actions = new ArrayList<AApiAction>();

		for (int i = 0; i < actionsArray.length(); i++) {
			final AApiAction action = new AApiAction();
			final JSONObject actionObject = (JSONObject) actionsArray.get(i);
			final String theme = actionObject.optString("theme", "");
			action.setTheme(theme);
			if (actionObject.has("offers")) {
				action.setOffers(parseOffers(actionObject.getJSONObject("offers")));
			}
			if (actionObject.has("target")) {
				action.setTarget(parseTarget(actionObject.getJSONObject("target")));
			}
			if (actionObject.has("lifecycles")) {
				action.setLifecycles(parseLifecycles(actionObject.getJSONArray("lifecycles")));
			}
			actions.add(action);
		}
	}

	private ArrayList<AApiProduct> parseOffers(JSONObject offersObject) throws Exception {
		final JSONArray productsArray = offersObject.getJSONArray("products");
		final ArrayList<AApiProduct> products = new ArrayList<AApiProduct>();
		for (int i = 0; i < productsArray.length(); i++) {
			final JSONObject productObject = (JSONObject) productsArray.get(i);
			AApiProduct product = new AApiProduct();
			product.setSku(productObject.optString("sku"));
			product.setName(Product.replaceXMLCharacters(productObject.optString("name")));
			product.setLargeImageURL(productObject.optString("largeImage"));
			product.setUrl(productObject.optString("url"));
			product.setImageURL(productObject.optString("image"));
			product.setSkuType(productObject.optString("skuType"));
			product.setThumbnailImageURL(productObject.optString("thumbnailImage"));
			product.setRegularPrice(productObject.optString("regularPrice"));
			product.setSalePrice(productObject.optString("salePrice"));

			// TODO Test this...Dont have an example of the data here but assume
			// it is a string array?
			final JSONArray userActionsJSON = productObject.optJSONArray("userActions");
			final ArrayList<String> userActions = new ArrayList<String>();
			if (userActionsJSON != null) {
				for (int j = 0; j < userActionsJSON.length(); j++) {
					userActions.add(userActionsJSON.getString(j));
				}
			}
			product.setUserActions(userActions);

			product.setShortDescription(productObject.optString("shortDescription"));
			if (product.getName() != null && !product.getName().equals("null")) {
				products.add(product);
			}
		}
		return products;

	}

	private AApiTarget parseTarget(JSONObject targetObject) throws Exception {
		final AApiTarget target = new AApiTarget();
		target.setFirstName(targetObject.optString("firstName"));
		target.setLastName(targetObject.optString("lastName"));
		target.setAtgId(targetObject.optString("atgId"));
		target.setRewardZone(targetObject.optString("rewardZone"));
		target.setId(targetObject.optString("id"));
		target.setPhone(targetObject.optString("phone"));
		target.setAddress(targetObject.optString("address"));
		target.setEmail(targetObject.optString("email"));
		return target;
	}

	private ArrayList<AApiLifecycle> parseLifecycles(JSONArray lifecyclesArray) throws Exception {
		final SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		final ArrayList<AApiLifecycle> lifecycles = new ArrayList<AApiLifecycle>();
		for (int i = 0; i < lifecyclesArray.length(); i++) {
			final JSONObject lifecycleObject = (JSONObject) lifecyclesArray.get(i);
			final AApiLifecycle lifecycle = new AApiLifecycle();
			lifecycle.setCategory(lifecycleObject.optString("category"));
			lifecycle.setInceptionDate(df.parse(lifecycleObject.optString("inception")));
			lifecycle.setPhases(parsePhases(lifecycleObject.optJSONArray("phases")));
			lifecycle.setStoreId(lifecycleObject.optString("storeId"));
			lifecycle.setName(lifecycleObject.optString("name"));
			lifecycles.add(lifecycle);
		}
		return lifecycles;
	}

	private ArrayList<AApiPhase> parsePhases(JSONArray phasesArray) throws Exception {
		final ArrayList<AApiPhase> phases = new ArrayList<AApiPhase>();
		for (int i = 0; i < phasesArray.length(); i++) {
			final JSONObject phaseObject = (JSONObject) phasesArray.get(i);
			final AApiPhase phase = new AApiPhase();
			final JSONObject narrativeObject = phaseObject.optJSONObject("narratives");
			if (narrativeObject != null) {
				phase.setBriefNarrative(narrativeObject.optString("briefNarrative"));
				phase.setScriptedNarrative(narrativeObject.optString("scriptedNarrative"));
				phase.setFormattedNarrative(narrativeObject.optString("formattedNarrative"));
				phase.setNarrative(narrativeObject.optString("narrative"));
				phase.setName(phaseObject.optString("name"));
				phase.setLinks(parseLinks(phaseObject.optJSONArray("links")));
				phase.setBeginDay(phaseObject.optInt("beginDay"));
				phase.setEndDay(phaseObject.optInt("endDay"));
				phase.setProducts(parsePhaseProducts(phaseObject.optJSONArray("products")));
				phase.setCurrentDay(phaseObject.optInt("currentDay"));
				phase.setId(phaseObject.optString("id"));
			}
			phases.add(phase);
		}
		return phases;
	}

	private ArrayList<Link> parseLinks(JSONArray linksArray) throws Exception {
		final ArrayList<Link> links = new ArrayList<Link>();
		for (int i = 0; i < linksArray.length(); i++) {
			final JSONObject linkObject = (JSONObject) linksArray.get(i);
			final String url = linkObject.optString("url");
			final String linkName = linkObject.optString("name");
			final Link link = new Link(linkName, url);
			links.add(link);
		}
		return links;
	}

	private ArrayList<AApiProduct> parsePhaseProducts(JSONArray productsArray) throws Exception {
		final ArrayList<AApiProduct> products = new ArrayList<AApiProduct>();
		for (int i = 0; i < productsArray.length(); i++) {
			final JSONObject productObject = (JSONObject) productsArray.get(i);
			AApiProduct product = new AApiProduct();
			product.setSku(productObject.optString("sku"));
			product.setName(productObject.optString("name"));
			product.setLargeImageURL(productObject.optString("largeImage"));
			product.setUrl(productObject.optString("url"));
			product.setImageURL(productObject.optString("image"));
			product.setSkuType(productObject.optString("skuType"));
			product.setThumbnailImageURL(productObject.optString("thumbnailImage"));
			product.setRegularPrice(productObject.optString("regularPrice"));
			product.setSalePrice(productObject.optString("salePrice"));
			product.setShortDescription(productObject.optString("shortDescription"));
			products.add(product);
		}
		return products;
	}

	private void postProductRecommendation(String id, String sku, String action) throws Exception {
//		if (id == null || id.equalsIgnoreCase("") || sku == null || sku.equalsIgnoreCase("") || validAction(action)) {
//			return;
//		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("userAction", action);
		APIRequest.makeJSONPostRequest(AppConfig.getActionApiURL(), AppData.BBY_ACTION_API_PATH + id + AppData.BBY_ACTION_API_RP_PATH + sku + AppData.BBY_ACTION_API_UA_JSON_PATH + "?apiKey=" + AppConfig.getActionApiKey(), params);
	}

	private void postPhaseFeedback(String id, String phase, String action) throws Exception {
//		if (id == null || id.equalsIgnoreCase("") || phase == null || phase.equalsIgnoreCase("") || validAction(action)) {
//			return;
//		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("userAction", action);
		APIRequest.makeJSONPostRequest(AppConfig.getActionApiURL(), AppData.BBY_ACTION_API_PATH + id + AppData.BBY_ACTION_API_LP_PATH + phase + AppData.BBY_ACTION_API_UA_JSON_PATH + "?apiKey=" + AppConfig.getActionApiKey(), params);
	}

	private String formatRZId(String rzId) {
		StringBuilder rzIdBuilder = new StringBuilder(rzId);
		if (rzId.length() < 10) {
			int pad = 10 - rzId.length();
			for (int i = 0; i < pad; i++) {
				rzIdBuilder.append("0");
			}
		}
		return "RZ" + rzIdBuilder.toString();
	}

	private String formatATGId(String atgId) {
		if (atgId.startsWith("ATG")) {
			return atgId;
		} else {
			return "ATG" + atgId;
		}
	}

	private String formatPhoneNumber(String phone) {
		if (phone.length() == 11 && phone.charAt(0) == '1') {
			phone = phone.substring(1);
		} else if (phone.length() == 10) {
			phone = String.format("%s-%s-%s", phone.substring(0, 3), phone.substring(3, 6), phone.substring(6, 10));
		}
		return phone;
	}
}
