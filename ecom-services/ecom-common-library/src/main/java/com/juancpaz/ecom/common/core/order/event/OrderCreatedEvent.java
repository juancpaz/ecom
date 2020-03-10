package com.juancpaz.ecom.common.core.order.event;

import java.util.Date;
import java.util.List;

import com.juancpaz.ecom.common.core.dto.LineItemDTO;

public class OrderCreatedEvent {
	private final Long orderId;

	private final String userId;

	private final String orderStatus;

	private final Double total;

	private final Date orderDate;

	private final List<LineItemDTO> lineItems;

	public String getUserId() {
		return userId;
	}

	public OrderCreatedEvent(Long orderId, String userId, String orderStatus, Double total, Date orderDate,
			List<LineItemDTO> lineItems) {
		super();
		this.orderId = orderId;
		this.userId = userId;
		this.orderStatus = orderStatus;
		this.total = total;
		this.orderDate = orderDate;
		this.lineItems = lineItems;
	}

	public List<LineItemDTO> getLineItems() {
		return lineItems;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public Double getTotal() {
		return total;
	}

	public Date getOrderDate() {
		return orderDate;
	}// 4566230913
}
