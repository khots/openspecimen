/**
 * <p>Title: ConsentTierResponse Class>
 * <p>Description:   Class for Consent Tier Responses.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ashish Gupta
 * @version 1.00
 * Created on November 21,2006
 */

package com.krishagni.catissueplus.core.biospecimen.domain;

public class ConsentTierResponse {

	private Long id;

	private String response;

	private ConsentTier consentTier;

	private CollectionProtocolRegistration cpr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ConsentTier getConsentTier() {
		return consentTier;
	}

	public void setConsentTier(ConsentTier consentTier) {
		this.consentTier = consentTier;
	}

	public CollectionProtocolRegistration getCpr() {
		return cpr;
	}

	public void setCpr(CollectionProtocolRegistration cpr) {
		this.cpr = cpr;
	}

	@Override
	public int hashCode() {
		return 31 * 1 
				+ ((response == null) ? 0 : response.hashCode())
				+ ((consentTier == null) ? 0 : consentTier.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		ConsentTierResponse other = (ConsentTierResponse) obj;
		if (response != null && response.equals(other.response) 
				&& consentTier != null && consentTier.equals(other.consentTier)) {
			return true;
		}
		
		return false;
	}
}