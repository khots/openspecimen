package com.krishagni.catissueplus.bulkoperator.repository;

public interface DaoFactory {
	public BulkOperationDao getBulkOperationDao();
	
	public BulkOperationJobDao getBulkOperationJobDao();
}
