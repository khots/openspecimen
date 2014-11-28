package com.krishagni.catissueplus.bulkoperator.services.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.krishagni.catissueplus.core.common.CommonValidator.isBlank;

import com.krishagni.catissueplus.bulkoperator.common.BulkImporterTask;
import com.krishagni.catissueplus.bulkoperator.domain.BulkOperation;
import com.krishagni.catissueplus.bulkoperator.domain.BulkOperationJob;
import com.krishagni.catissueplus.bulkoperator.domain.factory.BulkOperationErrorCode;
import com.krishagni.catissueplus.bulkoperator.events.BulkImportRecordsEvent;
import com.krishagni.catissueplus.bulkoperator.events.BulkOperationDetail;
import com.krishagni.catissueplus.bulkoperator.events.BulkOperationsEvent;
import com.krishagni.catissueplus.bulkoperator.events.BulkRecordsImportedEvent;
import com.krishagni.catissueplus.bulkoperator.events.JobDetail;
import com.krishagni.catissueplus.bulkoperator.events.JobDetailEvent;
import com.krishagni.catissueplus.bulkoperator.events.JobsDetailEvent;
import com.krishagni.catissueplus.bulkoperator.events.LogFileContentEvent;
import com.krishagni.catissueplus.bulkoperator.events.ReqJobDetailEvent;
import com.krishagni.catissueplus.bulkoperator.events.ReqJobsDetailEvent;
import com.krishagni.catissueplus.bulkoperator.events.ReqLogFileContentEvent;
import com.krishagni.catissueplus.bulkoperator.repository.DaoFactory;
import com.krishagni.catissueplus.bulkoperator.services.BulkOperationService;
import com.krishagni.catissueplus.bulkoperator.services.ObjectImporterFactory;
import com.krishagni.catissueplus.bulkoperator.util.BulkOperationException;
import com.krishagni.catissueplus.core.administrative.repository.UserDao;
import com.krishagni.catissueplus.core.common.PlusTransactional;
import com.krishagni.catissueplus.core.common.events.RequestEvent;

public class BulkOperationServiceImpl implements BulkOperationService {
	private DaoFactory daoFactory;
	
	private UserDao userDao;
	
	private ObjectImporterFactory importerFactory;
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setImporterFactory(ObjectImporterFactory importerFactory) {
		this.importerFactory = importerFactory;
	}
	
	@Override
	@PlusTransactional
	public BulkOperationsEvent getBulkOperations(RequestEvent req) {
		try {
			List<BulkOperation> operations = daoFactory.getBulkOperationDao().getBulkOperations();
			return BulkOperationsEvent.ok(BulkOperationDetail.from(operations));
		} catch (Exception e) {
			return BulkOperationsEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public BulkRecordsImportedEvent bulkImportRecords(BulkImportRecordsEvent req) {
		try {
			BulkOperation bulkOperation = daoFactory.getBulkOperationDao().getBulkOperation(req.getOperationName());
			if (bulkOperation == null) {
				return BulkRecordsImportedEvent.invalidRequest(BulkOperationErrorCode.INVALID_OPERATION_NAME, null);
			}
			
			String jobTrackingId = UUID.randomUUID().toString();
			BulkImporterTask bulkImporterTask = new BulkImporterTask(importerFactory, userDao, daoFactory.getBulkOperationJobDao(), 
					bulkOperation, jobTrackingId, req.getSessionDataBean(), req.getFileIn());
			bulkImporterTask.validateBulkOperation();
			
			threadPool.execute(bulkImporterTask);
			return BulkRecordsImportedEvent.ok(jobTrackingId);
		} catch (BulkOperationException be) {
			return BulkRecordsImportedEvent.invalidRequest(BulkOperationErrorCode.INVALID_CSV_TEMPLATE, null);
		} catch (Exception e) {
			return BulkRecordsImportedEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public JobsDetailEvent getJobs(ReqJobsDetailEvent req) {
		try {
			int startAt = req.getStartAt();
			if (startAt < 0) {
				startAt = 0;
			}
			
			int maxRecs = req.getMaxRecords();
			if (maxRecs <= 0) {
				maxRecs = 50;
			}
			
			List<BulkOperationJob> jobs = daoFactory.getBulkOperationJobDao().getJobs(startAt, maxRecs);
			return JobsDetailEvent.ok(JobDetail.from(jobs));
		} catch (Exception e) {
			return JobsDetailEvent.serverError(e);
		}
	}
	
	@Override
	@PlusTransactional
	public LogFileContentEvent getLogFileContent(ReqLogFileContentEvent req) {
		try {
			String trackingId = req.getTrackingId();
			if (isBlank(trackingId)) {
				return LogFileContentEvent.invalidRequest(BulkOperationErrorCode.MISSING_TRACKING_ID, null);
			}
			
			BulkOperationJob job = daoFactory.getBulkOperationJobDao().getJobByTrackingId(trackingId);
			if (job == null) {
				return LogFileContentEvent.invalidRequest(BulkOperationErrorCode.INVALID_TRACKING_ID, null);
			}
			
			return LogFileContentEvent.ok(job.getLogFile().getBinaryStream(), job.getLogFileName()); 
		} catch (Exception e) {
			return LogFileContentEvent.serverError(e);
		}
	}

	@Override
	@PlusTransactional
	public JobDetailEvent getJobDetail(ReqJobDetailEvent req) {
		try {
			String trackingId = req.getTrackingId();
			if (isBlank(trackingId)) {
				return JobDetailEvent.invalidRequest(BulkOperationErrorCode.MISSING_TRACKING_ID, null);
			}
			
			BulkOperationJob bulkOperationJob = daoFactory.getBulkOperationJobDao().getJobByTrackingId(trackingId);
			if (bulkOperationJob == null) {
				return JobDetailEvent.invalidRequest(BulkOperationErrorCode.INVALID_TRACKING_ID, null);
			}
			
			return JobDetailEvent.ok(JobDetail.from(bulkOperationJob));
		} catch (Exception e) {
			return JobDetailEvent.serverError(e);
		}
	}
}
