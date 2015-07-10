package com.krishagni.catissueplus.core.administrative.events;

import com.krishagni.catissueplus.core.common.errors.ObjectCreationException;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class SpecimenPositionAllocatedEvent extends ResponseEvent {
	private SpecimenPositionDetail position;

	public SpecimenPositionDetail getPosition() {
		return position;
	}

	public void setPosition(SpecimenPositionDetail position) {
		this.position = position;
	}
	
	public static SpecimenPositionAllocatedEvent ok(SpecimenPositionDetail detail) {
		SpecimenPositionAllocatedEvent resp = new SpecimenPositionAllocatedEvent();
		resp.setPosition(detail);
		resp.setStatus(EventStatus.OK);
		return resp;
	}
	
	public static SpecimenPositionAllocatedEvent badRequest(Exception e) {
		SpecimenPositionAllocatedEvent resp = new SpecimenPositionAllocatedEvent();
		resp.setStatus(EventStatus.BAD_REQUEST);
		resp.setException(e);
		if (e instanceof ObjectCreationException) {
			resp.setErroneousFields(((ObjectCreationException)e).getErroneousFields());
		}
		
		return resp;
	}
	
	public static SpecimenPositionAllocatedEvent serverError(Exception e) {
		SpecimenPositionAllocatedEvent resp = new SpecimenPositionAllocatedEvent();
		resp.setStatus(EventStatus.INTERNAL_SERVER_ERROR);
		resp.setException(e);
		return resp;
	}
	
	public static SpecimenPositionAllocatedEvent from(SpecimenPositionUpdatedEvent input) {
		SpecimenPositionAllocatedEvent resp = new SpecimenPositionAllocatedEvent();
		resp.setPosition(input.getPosition());
		resp.setStatus(input.getStatus());
		resp.setException(input.getException());
		resp.setErroneousFields(input.getErroneousFields());
		return resp;
	}
}