package com.juancpaz.ecom.common.core.order.event;

import java.util.Date;

public class OrderUpdatedEvent {

	private final Long orderId;

	private final String orderStatus;

	private final Date date;

	private final String description;

	public Long getOrderId() {
		return orderId;
	}

	public OrderUpdatedEvent(Long orderId, String orderStatus,Date date, String description) {
		super();
		this.orderId = orderId;
		this.orderStatus = orderStatus;
		this.date=date;
		this.description=description;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public Date getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}
}
