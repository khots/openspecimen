package com.krishagni.catissueplus.bulkoperator.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.krishagni.catissueplus.bulkoperator.common.ObjectImporter;
import com.krishagni.catissueplus.bulkoperator.events.DERecordInformation;
import com.krishagni.catissueplus.bulkoperator.events.ImportObjectEvent;
import com.krishagni.catissueplus.bulkoperator.events.ObjectImportedEvent;
import com.krishagni.catissueplus.bulkoperator.metadata.HookingInformation;
import com.krishagni.catissueplus.core.de.events.AddRecordEntryEvent;
import com.krishagni.catissueplus.core.de.events.RecordEntryEventAdded;
import com.krishagni.catissueplus.core.de.services.FormService;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.MultiSelectControl;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.FormData;
import edu.common.dynamicextensions.napi.FormDataManager;
import edu.common.dynamicextensions.napi.impl.FormDataManagerImpl;
import edu.wustl.common.beans.SessionDataBean;

public class DEImporter implements ObjectImporter {
	private FormService formSvc;
	
	public FormService getFormSvc() {
		return formSvc;
	}
	public void setFormSvc(FormService formSvc) {
		this.formSvc = formSvc;
	}

	@Override
	public ObjectImportedEvent importObject(ImportObjectEvent req) {
		if (!(req.getObject() instanceof DERecordInformation)) {
			throw new RuntimeException("DERecordInformation Object expected for this operation!");
		}
		
		DERecordInformation recordInformation = (DERecordInformation)req.getObject();
		Long recordId = null;
		Container c = Container.getContainer(recordInformation.getEntityClassName());
		FormData formData = getFormData(c, recordInformation.getDynExtObject());
		final SessionDataBean sessionDataBean = req.getSessionDataBean();
		
		UserContext ctxt =  new UserContext() {
			@Override
			public String getUserName() {
				return sessionDataBean.getUserName();
			}

			@Override
			public Long getUserId() {
				return sessionDataBean.getUserId();
			}

			@Override
			public String getIpAddress() {
				return sessionDataBean.getIpAddress();
			}
		};
		
		FormDataManager formDataManager = new FormDataManagerImpl(false);
		recordId = formDataManager.saveOrUpdateFormData(ctxt, formData);
		RecordEntryEventAdded response = hookStaticDynExtObj(recordInformation.getHookingInformation(), req.getSessionDataBean(), c.getId(), recordId);
		return ObjectImportedEvent.buildResponse(response, recordId);
	}
	
	private FormData getFormData(Container c, Map<String, Object> dataValue) {
		FormData formData = new FormData(c);
		for (Control ctrl : c.getControlsMap().values()) {
			if (ctrl instanceof SubFormControl) {
				SubFormControl sfCtrl = (SubFormControl) ctrl;
                String sfKey = new StringBuilder(c.getName()).append("->")
                                .append(sfCtrl.getSubContainer().getName()).toString();
                List<Map<String, Object>> sfDataValueList = (List<Map<String, Object>>) dataValue.get(sfKey);
				
				if (sfDataValueList == null) {
					formData.addFieldValue(new ControlValue(sfCtrl, null));
					continue;
				}
				
				List<FormData> subFormsData = new ArrayList<FormData>();
				for (Map<String, Object> sfDataValue : sfDataValueList) {
                    subFormsData.add(getFormData(sfCtrl.getSubContainer(), sfDataValue));
				}
				formData.addFieldValue(new ControlValue(sfCtrl, subFormsData));
				continue;
			}

            Object value = null;
            if (ctrl instanceof MultiSelectControl) {
                List<Map<String, String>> msList = (List<Map<String, String>>) dataValue.get(ctrl.getUserDefinedName());
                value = new String[msList.size()];
                int i = 0;
                String[] msVal = new String[msList.size()];
                for (Map<String, String> msMap : msList) {
                    msVal[i++] = msMap.get(ctrl.getUserDefinedName());
                }
                value = msVal;
            } else {
                value = dataValue.get(ctrl.getUserDefinedName());
            }
			if (value != null) {
				formData.addFieldValue(new ControlValue(ctrl, value));
			} else {
				formData.addFieldValue(new ControlValue(ctrl, null));
			}
		}
		return formData;
	}
	
	private RecordEntryEventAdded hookStaticDynExtObj(HookingInformation hookingInformation, SessionDataBean sdb, Long containerId, Long recordId) {
		Map<String,Object> recIntegrationInfo = hookingInformation.getDataHookingInformation();
		AddRecordEntryEvent req = new AddRecordEntryEvent();
		req.setSessionDataBean(sdb);
		req.setRecIntegrationInfo(recIntegrationInfo);
		req.setContainerId(containerId);
		req.setRecordId(recordId);
	
		RecordEntryEventAdded resp = formSvc.addRecordEntry(req);
		return resp;
	}
}
