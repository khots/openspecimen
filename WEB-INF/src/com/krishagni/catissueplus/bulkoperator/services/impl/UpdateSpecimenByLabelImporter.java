package com.krishagni.catissueplus.bulkoperator.services.impl;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.bulkoperator.events.UpdateSpecimenByLabelDto;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenDetail;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenUpdatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.UpdateSpecimenEvent;
import com.krishagni.catissueplus.core.biospecimen.services.SpecimenService;

public class UpdateSpecimenByLabelImporter implements ObjectImporter {
	private SpecimenService specimenService;

	public void setSpecimenService(SpecimenService specimenService) {
		this.specimenService = specimenService;
	}
	
	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof UpdateSpecimenByLabelDto)) {
			throw new RuntimeException("UpdateSpecimenDto expected for this operation!");
		}
		
		SpecimenDetail specimenDetail = (SpecimenDetail)req.getObject();
		UpdateSpecimenEvent request = new UpdateSpecimenEvent();
		request.setSessionDataBean(req.getSessionDataBean());
		request.setSpecimenDetail(specimenDetail);
		request.setId(specimenDetail.getId());
		
		SpecimenUpdatedEvent response = specimenService.updateSpecimenByLabel(request);
		return ObjectImportedEvent.buildResponse(response, response.getSpecimenDetail());
	}

}
