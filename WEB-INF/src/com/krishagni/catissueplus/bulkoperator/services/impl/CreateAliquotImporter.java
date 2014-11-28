package com.krishagni.catissueplus.bulkoperator.services.impl;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.CreateAliquotDto;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.AliquotCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.CreateAliquotEvent;
import com.krishagni.catissueplus.core.biospecimen.services.SpecimenService;

public class CreateAliquotImporter implements ObjectImporter {
	private SpecimenService specimenService;

	public void setSpecimenService(SpecimenService specimenService) {
		this.specimenService = specimenService;
	}
	
	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof CreateAliquotDto)) {
			throw new RuntimeException("CreateAliquotDto Object expected for this operation!");
		}
		
		CreateAliquotDto dto = (CreateAliquotDto)req.getObject();
		CreateAliquotEvent request = new CreateAliquotEvent();
		request.setSessionDataBean(req.getSessionDataBean());
		request.setAliquotDetail(dto.getAliquotDetail());
		request.setSpecimenLabel(dto.getSpecimenLabel());
		
		AliquotCreatedEvent response = specimenService.createAliquot(request);
		return ObjectImportedEvent.buildResponse(response, dto);
	}
}
