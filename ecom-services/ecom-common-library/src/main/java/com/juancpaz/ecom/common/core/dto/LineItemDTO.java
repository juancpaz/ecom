package com.juancpaz.ecom.common.core.dto;

import java.io.Serializable;

public class LineItemDTO implements Serializable {

	private static final long serialVersionUID = -4225876594730754619L;
	private Long inventoryId;
	private Double price;
	private int quantity;
	private String productId;

	public LineItemDTO() {

	}

	public LineItemDTO(String productId, Double price, int quantity, Long inventoryId) {
		this.productId = productId;
		this.price = price;
		this.quantity = quantity;
		this.inventoryId = inventoryId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

}
