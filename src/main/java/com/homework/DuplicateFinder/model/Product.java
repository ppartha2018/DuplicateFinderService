package com.homework.DuplicateFinder.model;

public class Product {

	//default value, enforcing any incorrect initialization to invalid product
	int productId = 0;
	String skuId = null;
	
	//overridden constructor which accepts productId and structureId, specific for this application
	public Product(int productId, String skuId){
		this.productId = productId;
		this.skuId = skuId;
	}
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
}
