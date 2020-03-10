package com.juancpaz.ecom.common.delivery.event;

import java.util.Date;

public class OrderDeliveredEvent {
	private final Long orderId;

	private final Date deliveredDate;
	public OrderDeliveredEvent(Long orderId,Date deliveredDate) {
		super();
		this.orderId = orderId;
		this.deliveredDate=deliveredDate;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Date getDeliveredDate() {
		return deliveredDate;
	}

}
