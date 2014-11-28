
package com.krishagni.catissueplus.core.administrative.domain;

import java.util.HashSet;
import java.util.Set;

import com.krishagni.catissueplus.core.administrative.domain.factory.BiohazardErrorCode;
import com.krishagni.catissueplus.core.biospecimen.domain.Specimen;
import com.krishagni.catissueplus.core.common.errors.CatissueException;
import com.krishagni.catissueplus.core.common.util.Status;

public class Biohazard {

	private Long id;

	private String name;

	private String comment;

	private String type;

	private String activityStatus;

	private Set<Specimen> specimens = new HashSet<Specimen>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public Set<Specimen> getSpecimens() {
		return specimens;
	}

	public void setSpecimens(Set<Specimen> specimens) {
		this.specimens = specimens;
	}

	public void update(Biohazard biohazard) {
		this.setName(biohazard.getName());
		this.setType(biohazard.getType());
		this.setComment(biohazard.getComment());
		this.setActivityStatus(biohazard.getActivityStatus());
	}

	public void delete() {
		if (!this.getSpecimens().isEmpty()) {
			throw new CatissueException(BiohazardErrorCode.ACTIVE_CHILDREN_FOUND);
		}
		else {
			this.setActivityStatus(Status.ACTIVITY_STATUS_DISABLED.getStatus());
		}

	}
	
	@Override
	public int hashCode() {
		return 31 * 1 + ((id == null) ? 0 : id.hashCode());
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
		
		Biohazard other = (Biohazard) obj;
		if (id == null || other.id == null) {
			return false;
		} else if (id.equals(other.id)) {
			return true;
		}
		return false;
	}

}
