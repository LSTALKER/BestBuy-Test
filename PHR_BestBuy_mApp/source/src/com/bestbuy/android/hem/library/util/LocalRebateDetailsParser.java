package com.bestbuy.android.hem.library.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import com.bestbuy.android.hem.library.dataobject.Area;
import com.bestbuy.android.hem.library.dataobject.EcoRebateErrorMsg;
import com.bestbuy.android.hem.library.dataobject.EcoRebatesResponseDetails;
import com.bestbuy.android.hem.library.dataobject.MaxRebate;
import com.bestbuy.android.hem.library.dataobject.Product;
import com.bestbuy.android.hem.library.dataobject.ProductRebateDetails;
import com.bestbuy.android.hem.library.dataobject.PurchaseRebate;
import com.bestbuy.android.hem.library.dataobject.RebateProgram;
import com.bestbuy.android.hem.library.dataobject.RecyclingRebate;

public class LocalRebateDetailsParser {

	private final String stringFallback = "";

	public EcoRebatesResponseDetails parse(String JSONResponse)
			throws Exception {
		EcoRebatesResponseDetails ecoRebatesResponseDetails = new EcoRebatesResponseDetails();

		JSONObject mainResponseJSONObj = new JSONObject(JSONResponse);

		// Check for error message first
		JSONObject errorJSON = mainResponseJSONObj.optJSONObject("error");
		if (errorJSON != null) {
			EcoRebateErrorMsg error = new EcoRebateErrorMsg();
			error.setMessage(errorJSON.optString("message"));
			error.setCode(errorJSON.optString("code"));
			// add error
			ecoRebatesResponseDetails.setError(error);
			return ecoRebatesResponseDetails;
		}

		ecoRebatesResponseDetails.setEcrsessionid(mainResponseJSONObj
				.optString("ecrsessionid", stringFallback));

		JSONObject ecoRebatesResponseJSONObj = mainResponseJSONObj
				.getJSONObject("EcoRebatesResponse");
		Area area = new Area();
		JSONObject areaJSON = ecoRebatesResponseJSONObj.optJSONObject("area");
		area.setMcity(areaJSON.optString("city", stringFallback));
		area.setmState(areaJSON.optString("state", stringFallback));
		area.setmZipCode(areaJSON.optInt("zipCode", 0));
		// add area obj
		ecoRebatesResponseDetails.setArea(area);

		ecoRebatesResponseDetails.setFirstItemIdx(ecoRebatesResponseJSONObj
				.optInt("firstItemIdx", 0));
		ecoRebatesResponseDetails.setLastItemIdx(ecoRebatesResponseJSONObj
				.optInt("lastItemIdx", 0));
		ecoRebatesResponseDetails.setPageCount(ecoRebatesResponseJSONObj
				.optInt("pageCount", 0));
		ecoRebatesResponseDetails.setPageIdx(ecoRebatesResponseJSONObj.optInt(
				"pageIdx", 0));
		ecoRebatesResponseDetails.setPageSize(ecoRebatesResponseJSONObj.optInt(
				"pageSize", 0));

		ArrayList<ProductRebateDetails> productRebateDetailsList = new ArrayList<ProductRebateDetails>();

		JSONArray productRebateDetailsJSONArray = ecoRebatesResponseJSONObj
				.optJSONArray("productRebateDetails");

		for (int i = 0; i < productRebateDetailsJSONArray.length(); i++) { // Start:
																			// for
																			// loop
																			// =
																			// ProductRebateDetails
																			// list
			ProductRebateDetails productRebateDetailsObj = new ProductRebateDetails();

			JSONObject productRebateDetailsJSON = productRebateDetailsJSONArray
					.optJSONObject(i);
			// MaxRebate
			MaxRebate maxRebate = new MaxRebate();
			JSONObject maxRebateObj = productRebateDetailsJSON
					.optJSONObject("maxRebate");
			if (maxRebateObj != null) {
				maxRebate.setAmount(maxRebateObj.optString("amount",
						stringFallback));
				maxRebate.setUnits((maxRebateObj.optString("units",
						stringFallback)));
			}
			// add max rebate object
			productRebateDetailsObj.setMaxRebate(maxRebate);

			productRebateDetailsObj
					.setMaxRebateAmountLabel(productRebateDetailsJSON
							.optString("maxRebateAmountLabel", stringFallback));

			// product
			Product product = new Product();
			JSONObject productJSON = productRebateDetailsJSON
					.optJSONObject("product");
			if (productJSON != null) {
				product.setBrand(productJSON.optString("brand", stringFallback));
				product.setDetailsUrl(productJSON.optString("detailsUrl",
						stringFallback));
				product.setEfficiencyLevel(productJSON.optString(
						"efficiencyLevel", stringFallback));
				product.setImageUrl(productJSON.optString("imageUrl",
						stringFallback));
				product.setMfrSKU(productJSON.optString("mfrSKU",
						stringFallback));
				product.setPrice(productJSON.optString("price", stringFallback));
				product.setProductType(productJSON.optString("productType",
						stringFallback));
				product.setShortName(productJSON.optString("shortName",
						stringFallback));
				product.setShowMfrSku(productJSON.optString("showMfrSku",
						stringFallback));
				product.setSku(productJSON.optString("sku", stringFallback));
				product.setUpc(productJSON.optString("upc", stringFallback));
			}
			// add product
			productRebateDetailsObj.setProduct(product);

			ArrayList<RebateProgram> rebatePrograms = new ArrayList<RebateProgram>();

			JSONArray rebateProgramJSONArray = productRebateDetailsJSON
					.optJSONArray("rebatePrograms");
			for (int j = 0; j < rebateProgramJSONArray.length(); j++) { // Start:
																		// for
																		// loop
																		// =
																		// Rebate
																		// program
																		// list
				RebateProgram rebateProgram = new RebateProgram();
				JSONObject rebateProgramJSONObj = rebateProgramJSONArray
						.optJSONObject(j);

				// Start: configurations
				// Not in new API response
				// End: configurations

				rebateProgram.setAmountLabel(rebateProgramJSONObj.optString(
						"amountLabel", stringFallback));
				rebateProgram.setClaimFormURL(rebateProgramJSONObj.optString(
						"claimFormURL", stringFallback));
				rebateProgram.setHomeURL(rebateProgramJSONObj.optString(
						"homeURL", stringFallback));
				rebateProgram.setId(rebateProgramJSONObj.optInt("id", 0));
				JSONArray importantDetailsArray = rebateProgramJSONObj
						.optJSONArray("importantDetails");
				String importantDetailsString = importantDetailsArray
						.toString();

				importantDetailsString = importantDetailsString
						.replace("[", "").replace("]", "");

				String[] impDetails = importantDetailsString.split("\",");
				for (int k = 0; k < impDetails.length; k++) {
					impDetails[k] = impDetails[k].replaceAll("\"", "");
				}

				rebateProgram.setImportantDetails(impDetails);
				rebateProgram.setName(rebateProgramJSONObj.optString("name",
						stringFallback));
				rebateProgram.setName(rebateProgramJSONObj.optString("name",
						stringFallback));

				PurchaseRebate purchaseRebate = new PurchaseRebate();
				JSONObject purchaseRebateJSON = rebateProgramJSONObj
						.optJSONObject("purchaseRebate");
				if (purchaseRebateJSON != null) {
					purchaseRebate.setAmount(purchaseRebateJSON.optInt(
							"amount", 0));
					purchaseRebate.setUnits(purchaseRebateJSON.optString(
							"units", stringFallback));
				}
				rebateProgram.setPurchaseRebate(purchaseRebate);

				RecyclingRebate recyclingRebate = new RecyclingRebate();
				JSONObject recyclingRebateJSON = rebateProgramJSONObj
						.optJSONObject("recyclingRebate");
				if (recyclingRebateJSON != null) {
					recyclingRebate.setAmount(recyclingRebateJSON.optInt(
							"amount", 0));
					recyclingRebate.setUnits(recyclingRebateJSON.optString(
							"units", stringFallback));
				}
				rebateProgram.setRecyclingRebate(recyclingRebate);

				JSONArray validDatesArray = rebateProgramJSONObj
						.optJSONArray("validDates");
				String validDatesString = validDatesArray.toString();

				validDatesString = validDatesString.replace("[", "").replace(
						"]", "");

				String[] vDates = validDatesString.split("\",");
				for (int k = 0; k < vDates.length; k++) {
					vDates[k] = vDates[k].replaceAll("\"", "");
				}

				rebateProgram.setValidDates(vDates);
				// Start: SubmitMailingAddress
				// Not in new API
				// End: SubmitMailingAddress

				// add Rebate Program
				rebatePrograms.add(rebateProgram);
			} // End: for loop = Rebate program list
			productRebateDetailsObj.setRebatePrograms(rebatePrograms);

			// add productRebateDetailsObj to list
			productRebateDetailsList.add(productRebateDetailsObj);
		} // End: for loop = ProductRebateDetails list

		// add product details list
		ecoRebatesResponseDetails
				.setProductRebateDetails(productRebateDetailsList);
		return ecoRebatesResponseDetails;
	}
}
