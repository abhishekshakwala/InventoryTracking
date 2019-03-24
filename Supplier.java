package org.supplyhouse.model;

public class Supplier {
	private String supplierID;
	private String productID;
	private int quantity;
	
	public Supplier(String supplierID, String productID, int quantity) {
		this.supplierID = supplierID;
		this.productID = productID;
		this.quantity = quantity;
	}
	
	public String getSupplierID() {
		return supplierID;
	}
	public void setSupplierID(String supplierID) {
		this.supplierID = supplierID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "Supplier [supplierID=" + supplierID + ", productID=" + productID + ", quantity=" + quantity + "]";
	}
}
