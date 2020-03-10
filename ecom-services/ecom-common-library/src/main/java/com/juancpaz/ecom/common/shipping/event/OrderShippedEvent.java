package com.juancpaz.ecom.common.shipping.event;

import java.util.Date;

public class OrderShippedEvent {
	private final Long orderId;

	private final Date shippingDate;
	public OrderShippedEvent(Long orderId,Date shippingDate) {
		super();
		this.orderId = orderId;
		this.shippingDate=shippingDate;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

}
