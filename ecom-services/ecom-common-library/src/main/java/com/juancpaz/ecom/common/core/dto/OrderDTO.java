package com.juancpaz.ecom.common.core.dto;

import java.util.List;

public class OrderDTO {

	private String userId;
	private List<LineItemDTO> lineItems;

	public OrderDTO() {

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<LineItemDTO> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<LineItemDTO> lineItems) {
		this.lineItems = lineItems;
	}

}
