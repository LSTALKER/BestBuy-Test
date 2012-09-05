package com.bestbuy.android.data;

public class ReceiptDetail {
	private String _productTitle;
	private String _productCost;
	private String _productQty;

	public ReceiptDetail(String productTitle, String productCost,
			String productQty) {
		super();
		this._productTitle = productTitle;
		this._productCost = productCost;
		this._productQty = productQty;
	}

	public String getProductTitle() {
		return _productTitle;
	}

	public void setProductTitle(String productTitle) {
		_productTitle = productTitle;
	}

	public String getProductCost() {
		return _productCost;
	}

	public void setProductCost(String productCost) {
		_productCost = productCost;
	}

	public String getProductQty() {
		return _productQty;
	}

	public void setProductQty(String productQty) {
		_productQty = productQty;
	}

	public String getProductQtyFormatted() {
		return _productQty;
	}

	public String getProductCostFormatted() {
		return _productCost;
	}
}
