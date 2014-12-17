package com.krishagni.core.tests;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.ApplicationContext;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.ParticipantErrorCode;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.CreateRegistrationEvent;
import com.krishagni.catissueplus.core.biospecimen.events.RegistrationCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;
import com.krishagni.catissueplus.core.common.errors.ErroneousField;
import com.krishagni.catissueplus.core.common.events.ResponseEvent;
import com.krishagni.core.common.ApplicationContextConfigurer;
import com.krishagni.core.common.WebContextLoader;
import com.krishagni.core.tests.testdata.CprTestData;
import com.krishagni.catissueplus.core.common.errors.CatissueErrorCode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = {ApplicationContextConfigurer.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@WebAppConfiguration
public class CollectionProtocolRegistrationTest {
	@Resource
    private WebApplicationContext webApplicationContext;
	
	@Autowired
	private CollectionProtocolRegistrationService cprSvc;

	@Autowired
	private ApplicationContext ctx;
	
	private final String PPID = "participant protocol identifier";
	
	private final String PARTICIPANT = "participant";
	
	private final String COLLECTION_PROTOCOL = "collection protocol";

	@Test
	@DatabaseSetup("CollectionProtocolRegistrationTest.registerParticipant.initial.xml")
	@DatabaseTearDown("CollectionProtocolRegistrationTest.registerParticipant.teardown.xml")
	@ExpectedDatabase(value="CollectionProtocolRegistrationTest.registerParticipant.expected.xml", 
		assertionMode=DatabaseAssertionMode.NON_STRICT)
	public void registerParticipant() throws Exception {
		CollectionProtocolRegistrationDetail cpr = CprTestData.getCprDetail();
		
		CreateRegistrationEvent req = new CreateRegistrationEvent();
		req.setCpId(1L);
		req.setCprDetail(cpr);
		req.setSessionDataBean(CprTestData.getSessionDataBean());
		
		RegistrationCreatedEvent resp = cprSvc.createRegistration(req);
		
		Assert.assertEquals(true, resp.isSuccess());
	}
	
	@Test
	@DatabaseSetup("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.initial.xml")
	@DatabaseTearDown("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.teardown.xml")
	@ExpectedDatabase(value="CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.expected.xml", 
		assertionMode=DatabaseAssertionMode.NON_STRICT)
	public void registerParticipantDuplicatePPID() throws Exception {		
		CollectionProtocolRegistrationDetail cpr = CprTestData.getCprDetail();
		cpr.setPpid("duplicate-ppid");
		CreateRegistrationEvent req = new CreateRegistrationEvent();
		req.setCprDetail(cpr);
		req.setSessionDataBean(CprTestData.getSessionDataBean());
		
		RegistrationCreatedEvent resp = cprSvc.createRegistration(req);
		
		Assert.assertEquals(false, resp.isSuccess());
		Assert.assertEquals(true, isErrorCodePresent(resp, ParticipantErrorCode.DUPLICATE_PPID, PPID));
	}
	
	@Test
	@DatabaseSetup("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.initial.xml")
	@DatabaseTearDown("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.teardown.xml")
	@ExpectedDatabase(value="CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.expected.xml", 
		assertionMode=DatabaseAssertionMode.NON_STRICT)
	public void registerParticipantInvalidParticipantId() throws Exception {		
		CollectionProtocolRegistrationDetail cpr = CprTestData.getCprDetail();
		cpr.getParticipant().setId(-1L);
		
		CreateRegistrationEvent req = new CreateRegistrationEvent();
		req.setCprDetail(cpr);
		req.setSessionDataBean(CprTestData.getSessionDataBean());
		
		RegistrationCreatedEvent resp = cprSvc.createRegistration(req);
		
		Assert.assertEquals(false, resp.isSuccess());
		Assert.assertEquals(true, isErrorCodePresent(resp, ParticipantErrorCode.INVALID_ATTR_VALUE, PARTICIPANT));
	}
	
	@Test
	@DatabaseSetup("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.initial.xml")
	@DatabaseTearDown("CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.teardown.xml")
	@ExpectedDatabase(value="CollectionProtocolRegistrationTest.registerParticipantDuplicatePPID.expected.xml", 
		assertionMode=DatabaseAssertionMode.NON_STRICT)
	public void registerParticipantInvalidCpid() throws Exception {		
		CollectionProtocolRegistrationDetail cpr = CprTestData.getCprDetail();
		cpr.setCpId(-1L);
		
		CreateRegistrationEvent req = new CreateRegistrationEvent();
		req.setCprDetail(cpr);
		req.setSessionDataBean(CprTestData.getSessionDataBean());
		
		RegistrationCreatedEvent resp = cprSvc.createRegistration(req);
		
		Assert.assertEquals(false, resp.isSuccess());
		Assert.assertEquals(true, isErrorCodePresent(resp, ParticipantErrorCode.INVALID_ATTR_VALUE, COLLECTION_PROTOCOL));
	}
	
	private void printResponse(ResponseEvent event) {
		for (ErroneousField ef: event.getErroneousFields()) {
			System.out.println("Error: " + ef.getErrorMessage() + " Field: " + ef.getFieldName());
		}
	}
	
	private boolean isErrorCodePresent(ResponseEvent event, CatissueErrorCode errorCode, String field) {
		for (ErroneousField ef: event.getErroneousFields()) {
			if (ef.getErrorMessage().equals(errorCode.message())
					&& ef.getErrorCode() == errorCode.code())
				
				if (field != null) {
					if (ef.getFieldName().equals(field)) {
						return true;
					}
					
					continue;
				}
				return true;
		}
		return false;
	}
}
