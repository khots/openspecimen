package com.krishagni.catissueplus.bulkoperator.services.impl;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.CreateRegistrationEvent;
import com.krishagni.catissueplus.core.biospecimen.events.RegistrationCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;

public class CreateRegistrationImporter implements ObjectImporter {
	CollectionProtocolRegistrationService cprSvc;
	
	public void setCprSvc(CollectionProtocolRegistrationService cprSvc) {
		this.cprSvc = cprSvc;
	}

	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof CollectionProtocolRegistrationDetail)) {
			throw new RuntimeException("CollectionProtocolRegistrationDetail detail expected for this operation!");
		}
		
		CreateRegistrationEvent request = new CreateRegistrationEvent();
		request.setSessionDataBean(req.getSessionDataBean());
		request.setCprDetail((CollectionProtocolRegistrationDetail)req.getObject());
		
		RegistrationCreatedEvent response = cprSvc.createRegistration(request);		
		return ObjectImportedEvent.buildResponse(response, response.getCprDetail());
	}
}