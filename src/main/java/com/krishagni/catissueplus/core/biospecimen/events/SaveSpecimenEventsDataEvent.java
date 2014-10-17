package com.krishagni.catissueplus.core.biospecimen.events;

import java.util.List;

import com.krishagni.catissueplus.core.common.events.RequestEvent;

import edu.common.dynamicextensions.napi.FormData;

public class SaveSpecimenEventsDataEvent extends RequestEvent {
	
	private Long formId;
	
	private List<FormData> formDataList;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public List<FormData> getFormDataList() {
		return formDataList;
	}

	public void setFormDataList(List<FormData> formDataList) {
		this.formDataList = formDataList;
	}
	
}
