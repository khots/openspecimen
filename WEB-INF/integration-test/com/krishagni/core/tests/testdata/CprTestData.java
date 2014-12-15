package com.krishagni.core.tests.testdata;

import java.util.ArrayList;
import java.util.HashSet;

import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantDetail;
import com.krishagni.catissueplus.core.biospecimen.events.ParticipantMedicalIdentifierNumberDetail;

import edu.wustl.common.beans.SessionDataBean;

public class CprTestData {
	public static ParticipantDetail getParticipant() {
		ParticipantDetail p = new ParticipantDetail();
		p.setFirstName("dimelos");
		p.setLastName("yadav");
		p.setGender("MALE");
		p.setRace(new HashSet<String>());
		p.getRace().add("Asian");
		p.setActivityStatus("Active");
		p.setPmis(new ArrayList<ParticipantMedicalIdentifierNumberDetail>());	
		return p;
	}
	
	public static CollectionProtocolRegistrationDetail getCprDetail() {
		CollectionProtocolRegistrationDetail cpr = new CollectionProtocolRegistrationDetail();
		cpr.setActivityStatus("Active");
		cpr.setBarcode("default-barcode");
		cpr.setParticipant(new ParticipantDetail());
		cpr.getParticipant().setId(1L);
		cpr.setPpid("default-gen-ppid");
		cpr.setCpId(1L);
		return cpr;
	}
	
	public static SessionDataBean getSessionDataBean() {
		SessionDataBean sessionDataBean = new SessionDataBean();
		sessionDataBean.setAdmin(true);
		sessionDataBean.setCsmUserId("1");
		sessionDataBean.setFirstName("admin");
		sessionDataBean.setIpAddress("127.0.0.1");
		sessionDataBean.setLastName("admin");
		sessionDataBean.setUserId(1L);
		sessionDataBean.setUserName("admin@admin.com");
		return sessionDataBean;
	}
}
