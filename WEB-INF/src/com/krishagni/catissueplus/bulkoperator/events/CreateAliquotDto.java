package com.krishagni.catissueplus.bulkoperator.events;

import com.krishagni.catissueplus.core.biospecimen.events.AliquotDetail;

public class CreateAliquotDto {
	private Long specimenId;
	
	private String specimenLabel;
	
	private AliquotDetail aliquotDetail;

	public Long getSpecimenId() {
		return specimenId;
	}

	public void setSpecimenId(Long specimenId) {
		this.specimenId = specimenId;
	}

	public String getSpecimenLabel() {
		return specimenLabel;
	}

	public void setSpecimenLabel(String specimenLabel) {
		this.specimenLabel = specimenLabel;
	}

	public AliquotDetail getAliquotDetail() {
		return aliquotDetail;
	}

	public void setAliquotDetail(AliquotDetail aliquotDetail) {
		this.aliquotDetail = aliquotDetail;
	}
}
