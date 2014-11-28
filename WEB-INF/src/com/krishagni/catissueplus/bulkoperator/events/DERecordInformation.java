package com.krishagni.catissueplus.bulkoperator.events;

import java.util.HashMap;
import java.util.Map;

import com.krishagni.catissueplus.bulkoperator.metadata.HookingInformation;

public class DERecordInformation {
	private String entityClassName;
	
	private Map<String, Object> dynExtObject = new HashMap<String, Object>();
	
	private HookingInformation hookingInformation;

	public String getEntityClassName() {
		return entityClassName;
	}

	public void setEntityClassName(String entityClassName) {
		this.entityClassName = entityClassName;
	}

	public Map<String, Object> getDynExtObject() {
		return dynExtObject;
	}

	public void setDynExtObject(Map<String, Object> dynExtObject) {
		this.dynExtObject = dynExtObject;
	}

	public HookingInformation getHookingInformation() {
		return hookingInformation;
	}

	public void setHookingInformation(HookingInformation hookingInformation) {
		this.hookingInformation = hookingInformation;
	}

	public DERecordInformation() {
		
	}
	
	public DERecordInformation(String entityClassName, Map<String, Object> dynExtObject, HookingInformation hookingInformation) {
		this.entityClassName = entityClassName;
		this.dynExtObject = dynExtObject;
		this.hookingInformation = hookingInformation;
	}
}
