package com.krishagni.catissueplus.core.biospecimen.domain;

public class ConsentTier {
	private Long id;
	
	private String statement;
	
	private CollectionProtocol collectionProtocol;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public CollectionProtocol getCollectionProtocol() {
		return collectionProtocol;
	}

	public void setCollectionProtocol(CollectionProtocol collectionProtocol) {
		this.collectionProtocol = collectionProtocol;
	}
	
	@Override
	public int hashCode() {
		return 31 * 1 + ((statement == null) ? 0 : statement.hashCode());
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
		
		ConsentTier other = (ConsentTier) obj;
		if (statement != null && statement.equals(other.statement)) {
			return true;
		}
		
		return false;
	}
}
