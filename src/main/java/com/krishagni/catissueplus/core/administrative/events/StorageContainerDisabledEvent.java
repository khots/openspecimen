
package com.krishagni.catissueplus.core.administrative.events;

import com.krishagni.catissueplus.core.common.errors.ErroneousField;
import com.krishagni.catissueplus.core.common.events.EventStatus;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;

public class StorageContainerDisabledEvent extends ResponseEvent {

	private static final String SUCCESS = "success";

	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static StorageContainerDisabledEvent ok() {
		StorageContainerDisabledEvent event = new StorageContainerDisabledEvent();
		event.setStatus(EventStatus.OK);
		event.setMessage(SUCCESS);
		return event;
	}

	public static StorageContainerDisabledEvent serverError(Throwable... t) {
		Throwable t1 = t != null && t.length > 0 ? t[0] : null;
		StorageContainerDisabledEvent resp = new StorageContainerDisabledEvent();
		resp.setStatus(EventStatus.INTERNAL_SERVER_ERROR);
		resp.setException(t1);
		resp.setMessage(t1 != null ? t1.getMessage() : null);
		return resp;
	}

	public static StorageContainerDisabledEvent notFound(Long userId) {
		StorageContainerDisabledEvent resp = new StorageContainerDisabledEvent();
		resp.setId(userId);
		resp.setStatus(EventStatus.NOT_FOUND);
		return resp;
	}

	public static StorageContainerDisabledEvent notFound(String name) {
		StorageContainerDisabledEvent resp = new StorageContainerDisabledEvent();
		resp.setName(name);
		resp.setStatus(EventStatus.NOT_FOUND);
		return resp;
	}
}
