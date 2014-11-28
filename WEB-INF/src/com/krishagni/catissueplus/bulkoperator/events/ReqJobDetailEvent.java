package com.krishagni.catissueplus.bulkoperator.events;

import com.krishagni.catissueplus.core.common.events.RequestEvent;

public class ReqJobDetailEvent extends RequestEvent {
	private String trackingId;

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
}	
