package com.krishagni.core.tests;


import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
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

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.krishagni.catissueplus.core.biospecimen.events.CollectionProtocolRegistrationDetail;
import com.krishagni.catissueplus.core.biospecimen.events.CreateRegistrationEvent;
import com.krishagni.catissueplus.core.biospecimen.events.RegistrationCreatedEvent;
import com.krishagni.catissueplus.core.biospecimen.services.CollectionProtocolRegistrationService;
import com.krishagni.core.common.ApplicationContextConfigurer;
import com.krishagni.core.common.WebContextLoader;
import com.krishagni.core.tests.testdata.CprTestData;

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
	
	@Before
	public void setup() {

	}

	@Test
	@DatabaseSetup("create_reg_initial_contents.xml")
	@DatabaseTearDown("create_reg_expected_contents.xml")
	@ExpectedDatabase(value="create_reg_expected_contents.xml", assertionMode=DatabaseAssertionMode.NON_STRICT)
	public void registerParticipant() throws Exception {
		CollectionProtocolRegistrationDetail cpr = CprTestData.getCprDetail();
		
		CreateRegistrationEvent req = new CreateRegistrationEvent();
		req.setCpId(1L);
		req.setCprDetail(cpr);
		req.setSessionDataBean(CprTestData.getSessionDataBean());
		
		RegistrationCreatedEvent resp = cprSvc.createRegistration(req);
		
		System.out.println(resp.getMessage());
		Assert.assertEquals(true, resp.isSuccess());
	}
}
