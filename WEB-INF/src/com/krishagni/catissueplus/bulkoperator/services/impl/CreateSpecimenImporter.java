package com.krishagni.catissueplus.bulkoperator.services.impl;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.CreateSpecimenEvent;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.SpecimenDetail;
import com.krishagni.catissueplus.core.biospecimen.services.SpecimenService;

public class CreateSpecimenImporter implements ObjectImporter {
	private SpecimenService specimenService;

	public void setSpecimenService(SpecimenService specimenService) {
		this.specimenService = specimenService;
	}
	
	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof SpecimenDetail)) {
			throw new RuntimeException("SpecimenDetail Object expected for this operation!");
		}
	
		CreateSpecimenEvent event = new CreateSpecimenEvent();
		event.setSpecimenDetail((SpecimenDetail)req.getObject());
		event.setSessionDataBean(req.getSessionDataBean());
		
		SpecimenCreatedEvent response = specimenService.createSpecimen(event);
		return ObjectImportedEvent.buildResponse(response, response.getSpecimenDetail()); 
	}
}
