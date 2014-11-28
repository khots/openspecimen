package com.krishagni.catissueplus.bulkoperator.events;

import com.krishagni.catissueplus.core.common.errors.CatissueErrorCode;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class BulkRecordsImportedEvent extends ResponseEvent {
	private String trackingId;
	
	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public static BulkRecordsImportedEvent ok(String trackingId) {
		BulkRecordsImportedEvent resp = new BulkRecordsImportedEvent();
		resp.setStatus(EventStatus.OK);
		resp.setTrackingId(trackingId);
		return resp;
	}
	
	public static BulkRecordsImportedEvent serverError(Throwable... t) {
		Throwable t1 = t != null && t.length > 0 ? t[0] : null;
		BulkRecordsImportedEvent resp = new BulkRecordsImportedEvent();
		resp.setStatus(EventStatus.INTERNAL_SERVER_ERROR);
		resp.setException(t1);
		resp.setMessage(t1 != null ? t1.getMessage() : null);
		return resp;
	}
	
	public static BulkRecordsImportedEvent invalidRequest(CatissueErrorCode error, Throwable t) {
		BulkRecordsImportedEvent resp = new BulkRecordsImportedEvent();
		resp.setupResponseEvent(EventStatus.BAD_REQUEST, error, t);
		return resp;
	}
}
