
package com.krishagni.catissueplus.core.biospecimen.domain;

public class ExternalIdentifier {

	private Long id;

	private String name;

	private String value;

	private Specimen specimen;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Specimen getSpecimen() {
		return specimen;
	}

	public void setSpecimen(Specimen specimen) {
		this.specimen = specimen;
	}

	@Override
	public int hashCode() {
		return 31 * 1 
				+ ((name == null) ? 0 : name.hashCode()) 
				+ ((value == null) ? 0 : value.hashCode());
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
		
		ExternalIdentifier other = (ExternalIdentifier) obj;
		if (name != null && value != null && 
				name.equals(other.name) && value.equals(other.value)) {
			return true;
		} 
		
		return false;
	}
}
