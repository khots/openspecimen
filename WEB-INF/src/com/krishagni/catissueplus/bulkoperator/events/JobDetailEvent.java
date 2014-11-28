package com.krishagni.catissueplus.bulkoperator.events;

import com.krishagni.catissueplus.core.common.errors.CatissueErrorCode;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class JobDetailEvent extends ResponseEvent {
	private JobDetail job;

	public JobDetail getJob() {
		return job;
	}

	public void setJob(JobDetail job) {
		this.job = job;
	}
	
	public static JobDetailEvent ok(JobDetail job) {
		JobDetailEvent resp = new JobDetailEvent();
		resp.setStatus(EventStatus.OK);
		resp.setJob(job);
		return resp;
	}
	
	public static JobDetailEvent serverError(Throwable... t) {
		Throwable t1 = t != null && t.length > 0 ? t[0] : null;
		JobDetailEvent resp = new JobDetailEvent();
		resp.setStatus(EventStatus.INTERNAL_SERVER_ERROR);
		resp.setException(t1);
		resp.setMessage(t1 != null ? t1.getMessage() : null);
		return resp;
	}
	
	public static JobDetailEvent invalidRequest(CatissueErrorCode error, Throwable t) {
		JobDetailEvent resp = new JobDetailEvent();
		resp.setupResponseEvent(EventStatus.BAD_REQUEST, error, t);
		return resp;
	}
}
