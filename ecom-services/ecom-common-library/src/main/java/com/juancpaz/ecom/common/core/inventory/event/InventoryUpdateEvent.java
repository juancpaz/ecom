package com.juancpaz.ecom.common.core.inventory.event;

public class InventoryUpdateEvent {

	private final Long id;

	private final String sku;

	private final Integer quantity;

	public InventoryUpdateEvent(Long id, String sku, Integer quantity) {
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
