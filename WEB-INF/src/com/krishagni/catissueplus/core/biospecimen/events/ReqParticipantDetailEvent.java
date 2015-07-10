
package com.krishagni.catissueplus.core.biospecimen.events;

import com.krishagni.catissueplus.core.common.events.RequestEvent;

public class ReqParticipantDetailEvent extends RequestEvent {

	private Long participantId;
	
	private boolean includeRegistrations = false;

	public Long getParticipantId() {
		return participantId;
	}

	public void setParticipantId(Long participantId) {
		this.participantId = participantId;
	}

	public boolean isIncludeRegistrations() {
		return includeRegistrations;
	}

	public void setIncludeRegistrations(boolean includeRegistrations) {
		this.includeRegistrations = includeRegistrations;
	}
}
