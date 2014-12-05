package com.krishagni.catissueplus.bulkoperator.services.impl;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.bulkoperator.events.UpdateCprDto;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.RegistrationUpdatedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.UpdateRegistrationEvent;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;

public class UpdateRegistrationImporter implements ObjectImporter {
	private CollectionProtocolRegistrationService cprSvc;
	
	public void setCprSvc(CollectionProtocolRegistrationService cprSvc) {
		this.cprSvc = cprSvc;
	}

	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof UpdateCprDto)) {
			throw new RuntimeException("UpdateCprDot detail expected for this operation!");
		}
		
		UpdateRegistrationEvent request = new UpdateRegistrationEvent();
		request.setSessionDataBean(req.getSessionDataBean());
		request.setCprDetail((CollectionProtocolRegistrationDetail)req.getObject());
		
		RegistrationUpdatedEvent response = cprSvc.updateRegistration(request);		
		return ObjectImportedEvent.buildResponse(response, response.getCprDetail());		
	}

}
