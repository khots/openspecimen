
package com.krishagni.catissueplus.core.administrative.events;

import java.util.ArrayList;
import java.util.List;

import com.krishagni.catissueplus.core.biospecimen.events.ChildCollectionProtocolSummary;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class ChildCollectionProtocolsEvent extends ResponseEvent {

	private List<ChildCollectionProtocolSummary> childProtocols = new ArrayList<ChildCollectionProtocolSummary>();

	public List<ChildCollectionProtocolSummary> getChildProtocols() {
		return childProtocols;
	}

	public void setChildProtocols(List<ChildCollectionProtocolSummary> childProtocols) {
		this.childProtocols = childProtocols;
	}

	public static ChildCollectionProtocolsEvent ok(List<ChildCollectionProtocolSummary> list) {
		ChildCollectionProtocolsEvent event = new ChildCollectionProtocolsEvent();
		event.setChildProtocols(list);
		event.setStatus(EventStatus.OK);
		return event;
	}

}
