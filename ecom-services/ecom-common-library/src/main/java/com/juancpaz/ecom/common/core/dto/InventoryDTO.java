package com.juancpaz.ecom.common.core.dto;

public class InventoryDTO {

	private Long id;

	private String sku;

	private Integer quantity;

	public InventoryDTO(Long id, String sku, Integer quantity) {
		super();
		this.id = id;
		this.sku = sku;
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public String getSku() {
		return sku;
	}

	public Integer getQuantity() {
		return quantity;
	}
}
