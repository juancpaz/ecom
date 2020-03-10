package com.juancpaz.ecom.common.delivery.event;

public class OrderDeliveryFailedEvent {
	private final Long orderId;
	private final String failureReason;

	public OrderDeliveryFailedEvent(Long orderId, String failureReason) {
		super();
		this.orderId = orderId;
		this.failureReason=failureReason;
	}

	public Long getOrderId() {
		return orderId;
	}


	public String getFailureReason() {
		return failureReason;
	}

}
