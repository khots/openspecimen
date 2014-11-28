package com.krishagni.catissueplus.bulkoperator.repository.impl;

import org.hibernate.SessionFactory;

import com.krishagni.catissueplus.bulkoperator.repository.BulkOperationDao;
import com.krishagni.catissueplus.bulkoperator.repository.BulkOperationJobDao;
import com.krishagni.catissueplus.bulkoperator.repository.DaoFactory;

public class DaoFactoryImpl implements DaoFactory {
	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public BulkOperationDao getBulkOperationDao() {
		BulkOperationDaoImpl bulkOperationDao = new BulkOperationDaoImpl();
		bulkOperationDao.setSessionFactory(sessionFactory);
		return bulkOperationDao;
	}

	@Override
	public BulkOperationJobDao getBulkOperationJobDao() {
		BulkOperationJobDaoImpl bulkOperationJobDao = new BulkOperationJobDaoImpl();
		bulkOperationJobDao.setSessionFactory(sessionFactory);
		return bulkOperationJobDao;
	}
}
