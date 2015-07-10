package com.krishagni.catissueplus.core.biospecimen.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration;

public class CprSummary {
	private String cpTitle;
	
	private String cpShortTitle;
	
	private String ppid;
	
	private Long regId;
	
	private Date regDate;
	
	private String status;

	public String getCpTitle() {
		return cpTitle;
	}

	public void setCpTitle(String cpTitle) {
		this.cpTitle = cpTitle;
	}

	public String getCpShortTitle() {
		return cpShortTitle;
	}

	public void setCpShortTitle(String cpShortTitle) {
		this.cpShortTitle = cpShortTitle;
	}

	public String getPpid() {
		return ppid;
	}

	public void setPpid(String ppid) {
		this.ppid = ppid;
	}

	public Long getRegId() {
		return regId;
	}

	public void setRegId(Long regId) {
		this.regId = regId;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static CprSummary from(CollectionProtocolRegistration cpr) {
		CprSummary result = new CprSummary();
		result.setCpShortTitle(cpr.getCollectionProtocol().getShortTitle());
		result.setCpTitle(cpr.getCollectionProtocol().getTitle());
		result.setPpid(cpr.getProtocolParticipantIdentifier());
		result.setRegId(cpr.getId());
		result.setRegDate(cpr.getRegistrationDate());
		result.setStatus(cpr.getActivityStatus());
		
		return result;
	}
	
	public static List<CprSummary> from(Collection<CollectionProtocolRegistration> cprs) {
		if (cprs == null) {
			return Collections.emptyList();
		}
		
		List<CprSummary> result = new ArrayList<CprSummary>();
		for (CollectionProtocolRegistration cpr : cprs) {
			result.add(from(cpr));
		}
		
		return result;
	}
}
