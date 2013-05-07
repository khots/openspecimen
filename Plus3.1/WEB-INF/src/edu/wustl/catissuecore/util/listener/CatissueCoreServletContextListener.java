/*
 * $Name: 1.41.2.41.2.3 $
 *
 * */

package edu.wustl.catissuecore.util.listener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import javax.jms.JMSException;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.ehcache.CacheException;

import org.apache.commons.io.FilenameUtils;

import titli.model.util.TitliResultGroup;
import au.com.bytecode.opencsv.CSVReader;
import edu.wustl.bulkoperator.util.BulkEMPIOperationsUtility;
import edu.wustl.bulkoperator.util.BulkOperationUtility;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.catissuecore.action.annotations.AnnotationConstants;
import edu.wustl.catissuecore.annotations.AnnotationUtil;
import edu.wustl.catissuecore.cpSync.SyncCPThreadExecuterImpl;
import edu.wustl.catissuecore.domain.Address;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Distribution;
import edu.wustl.catissuecore.domain.DistributionProtocol;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.interceptor.SpecimenDataBackloader;
import edu.wustl.catissuecore.interceptor.wmq.SpecimenWmqProcessor;
import edu.wustl.catissuecore.namegenerator.LabelAndBarcodeGeneratorInitializer;
import edu.wustl.catissuecore.util.CatissueCoreCacheManager;
import edu.wustl.catissuecore.util.EmailHandler;
import edu.wustl.catissuecore.util.HelpXMLPropertyHandler;
import edu.wustl.catissuecore.util.ParticipantAttributeDisplayInfoUtility;
import edu.wustl.catissuecore.util.ProtectionGroups;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.DefaultValueManager;
import edu.wustl.catissuecore.util.global.Variables;
import edu.wustl.common.audit.AuditManager;
import edu.wustl.common.beans.EmailServerProperties;
import edu.wustl.common.cde.CDEManager;
import edu.wustl.common.exception.ApplicationException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.exception.ParseException;
import edu.wustl.common.participant.utility.ParticipantManagerUtility;
import edu.wustl.common.participant.utility.RaceGenderCodesProperyHandler;
import edu.wustl.common.util.CVSTagReader;
import edu.wustl.common.util.EmailClient;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.dao.util.DAOUtility;
import edu.wustl.query.util.listener.QueryCoreServletContextListenerUtil;
import edu.wustl.simplequery.bizlogic.QueryBizLogic;

/**
 *
 * @author aarti_sharma
 *
 * */
public class CatissueCoreServletContextListener implements ServletContextListener
{

	/**
	 * CatissueCoreServletContextListener Logger.
	 */
	private static final Logger logger = Logger
			.getCommonLogger(CatissueCoreServletContextListener.class);
	/**
	 * DATASOURCE_JNDI_NAME.
	 */
	private static final String JNDI_NAME = "java:/catissuecore";

	//class level instance to access methods for registering and unregistering queues
	private final ParticipantManagerUtility participantManagerUtility = new ParticipantManagerUtility();

	/**
	 * This method is called during server startup, It is used when want some initliazation before
	 * server start.
	 * @param sce ServletContextEvent
	 */
	public void contextInitialized(final ServletContextEvent sce)
	{
		try
		{
			logger.info("Initializing catissue application");
			final ServletContext servletContext = sce.getServletContext();
			ApplicationProperties
			.initBundle(servletContext.getInitParameter("resourcebundleclass"));
	
			CommonServiceLocator.getInstance().setAppHome(sce.getServletContext().getRealPath(""));
			logger.info(":::::::::::::Application home ::::::::::::"
					+ CommonServiceLocator.getInstance().getAppHome());
			ErrorKey.init("~");
			AuditManager.init();
			LoggerConfig.configureLogger(CommonServiceLocator.getInstance().getPropDirPath());
			this.setGlobalVariable();
			this.initCatissueParams();
			logApplnInfo();
			DefaultValueManager.validateAndInitDefaultValueMap();
			BulkOperationUtility.changeBulkOperationStatusToFailed();
			initCiderIntegration();
			QueryCoreServletContextListenerUtil.contextInitialized(sce, "java:/query");
			if (XMLPropertyHandler.getValue(Constants.EMPI_ENABLED).equalsIgnoreCase("true"))
			{
				BulkEMPIOperationsUtility.changeBulkOperationStatusToFailed();
				// eMPI integration initialization
				initeMPI();
			}
			if (Constants.TRUE.equals(XMLPropertyHandler.getValue("Imaging.enabled")))
			{
				Variables.isImagingConfigurred = true;
			}
			SyncCPThreadExecuterImpl executerImpl = SyncCPThreadExecuterImpl.getInstance();
			executerImpl.init();
			initializeParticipantConfig();
			initEmailclient();
			logger.info("Initialization complete");
		}
		catch (final Exception e)
		{
			CatissueCoreServletContextListener.logger.error(
					"Application failed to initialize" + e.getMessage(), e);
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	private void initEmailclient() {
		EmailServerProperties emailServerProps = new EmailServerProperties();
		emailServerProps.setFromAddr(XMLPropertyHandler.getValue("email.sendEmailFrom.emailAddress"));
		emailServerProps.setFromPassword(XMLPropertyHandler.getValue("email.sendEmailFrom.emailPassword"));
		emailServerProps.setIsSMTPAuthEnabled(XMLPropertyHandler.getValue("email.smtp.auth.enabled"));
		emailServerProps.setServerHost(XMLPropertyHandler.getValue("email.mailServer"));
		emailServerProps.setServerPort(XMLPropertyHandler.getValue("email.mailServer.port"));
		emailServerProps.setIsStartTLSEnabled(XMLPropertyHandler.getValue("email.smtp.starttls.enabled"));
	
		String subPropFile = "/email-templates/emailSubjects.properties";
		String tmplPropFile = "/email-templates/emailTemplates.properties";
		EmailClient.initialize(emailServerProps, subPropFile, tmplPropFile);
	}

	private void initCiderIntegration()
	{
		if (XMLPropertyHandler.getValue("CiderWmqEnabled").equalsIgnoreCase("true"))
		{
			SpecimenWmqProcessor.getInstance();
			Timer dataBackloader = new Timer(true);
			dataBackloader.scheduleAtFixedRate(new SpecimenDataBackloader(),
					DAOUtility.getStartTimeForTodaysDate("23:30"), (24 * 60 * 60 * 1000));
		}
	}

	/**
	 * Inite mpi.
	 */
	private void initeMPI()
	{
		try
		{
			checkEMPIAdminUser();
			RaceGenderCodesProperyHandler.init("HL7MesRaceGenderCodes.xml");
			participantManagerUtility.registerWMQListener();

			try
			{
				ParticipantManagerUtility.initialiseParticiapntMatchScheduler();
			}
			catch (Exception excep)
			{
				logger.error(" ####### ERROR WHILE INITIALISING THE SHECUDER FOR PROCESSING THE PARTICIPANTS ######### ");
				logger.error(excep.getMessage(), excep);
			}

		}
		catch (JMSException excep)
		{
			logger.error(" EMPI : ERROR WHILE REGISTERING WMQ LISTENER");
			logger.error(excep.getMessage(), excep);
		}
		catch (Exception excep)
		{
			logger.error("Could not initialized application, Error in loading the HL7 race gender code property handler.");
			logger.error(excep.getMessage(), excep);
		}
		catch (Error excep)
		{
			logger.error("EMPI : ERROR WHILE REGISTERING WMQ LISTENER ");
			logger.error(excep.getMessage(), excep);
		}

	}

	private void checkEMPIAdminUser() throws ApplicationException, MessagingException
	{
		String eMPIAdminUName = XMLPropertyHandler.getValue(Constants.HL7_LISTENER_ADMIN_USER);
		User validUser = AppUtility.getUser(eMPIAdminUName);
		EmailHandler emailHandlrObj = new EmailHandler();
		if (validUser == null)
		{
			emailHandlrObj.sendEMPIAdminUserNotExitsEmail();
		}
		if (validUser != null
				&& Constants.ACTIVITY_STATUS_CLOSED.equals(validUser.getActivityStatus()))
		{
			emailHandlrObj.sendEMPIAdminUserClosedEmail(validUser);
		}
	}

	/**
	 * Initialize caTissue default properties.
	 * @throws ClassNotFoundException ClassNotFoundException
	 * @throws DAOException DAOException
	 * @throws ParseException ParseException
	 * @throws IOException 
	 */
	public void initCatissueParams() throws ClassNotFoundException, DAOException, ParseException,
			IOException
	{
		edu.wustl.query.util.global.Utility.setReadDeniedAndEntitySqlMap();
		this.addDefaultProtectionGroupsToMap();
		final QueryBizLogic bLogic = new QueryBizLogic();
		bLogic.initializeQueryData();
		this.createAliasAndPageOfMap();
		LabelAndBarcodeGeneratorInitializer.init();
		this.initialiseVariablesForEdinburgh();
		this.initialiseVariablesForDFCI();
		this.initialiseVariableForAppInfo();
		this.initEntityCache();
		Utility.initializePrivilegesMap();
		this.initTitliIndex();
		this.initCDEManager();
		this.initDashboardCache();
		final String absolutePath = CommonServiceLocator.getInstance().getPropDirPath()
				+ File.separator + "PrintServiceImplementor.properties";
		Variables.setPrinterInfo(absolutePath);
		System.setProperty("app.propertiesDir", CommonServiceLocator.getInstance().getPropDirPath());
	}

	/**
	 * Set Global variable.
	 * @throws Exception Exception
	 */
	private void setGlobalVariable() throws Exception
	{
		final String path = System.getProperty("app.propertiesFile");
		XMLPropertyHandler.init(path);
		new File(path);
		final int maximumTreeNodeLimit = Integer.parseInt(XMLPropertyHandler
				.getValue(Constants.MAXIMUM_TREE_NODE_LIMIT));
		Variables.maximumTreeNodeLimit = maximumTreeNodeLimit;
		HelpXMLPropertyHandler.init(CommonServiceLocator.getInstance().getPropDirPath()
				+ File.separator + "help_links.xml");
	}

	/**
	 * Initialize Titli.
	 */
	private void initTitliIndex()
	{
		TitliResultGroup.isTitliConfigured = Boolean.parseBoolean(XMLPropertyHandler
				.getValue(Constants.KEYWORD_CONFIGURED));
	}

	/**
	 * Initialize Entity cache.
	 */
	private void initEntityCache()
	{
		try
		{
			final CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();
			AnnotationUtil.getSystemEntityList();
			final Long participantId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_PARTICIPANT);
			cacheManager.addObjectToCache("participantEntityId", participantId);
			final Long scgId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_SPECIMEN_COLLN_GROUP);
			cacheManager.addObjectToCache("scgEntityId", scgId);
			final Long specimenEntityId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_SPECIMEN);
			cacheManager.addObjectToCache("specimenEntityId", specimenEntityId);
			final Long cpEntityId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_COLLECTION_PROTOCOL);
			cacheManager.addObjectToCache(AnnotationConstants.COLLECTION_PROTOCOL_ENTITY_ID,
					cpEntityId);

			final Long entityId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_PARTICIPANT_REC_ENTRY);
			cacheManager.addObjectToCache(AnnotationConstants.PARTICIPANT_REC_ENTRY_ENTITY_ID,
					entityId);
			final Long spRecEtyId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_SPECIMEN_REC_ENTRY);
			cacheManager.addObjectToCache(AnnotationConstants.SPECIMEN_REC_ENTRY_ENTITY_ID,
					spRecEtyId);
			final Long scgRecEtyId = edu.common.dynamicextensions.xmi.AnnotationUtil
					.getEntityId(AnnotationConstants.ENTITY_NAME_SCG_REC_ENTRY);
			cacheManager.addObjectToCache(AnnotationConstants.SCG_REC_ENTRY_ENTITY_ID, scgRecEtyId);
			EntityCache.getInstance();
			logger.debug("Entity Cache is initialised");
		}
		catch (final Exception e)
		{
			CatissueCoreServletContextListener.logger.error("Exception occured while initialising "
					+ "entity cache" + e.getMessage(), e);
			throw new RuntimeException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Initialize CDE Manager.
	 */
	private void initCDEManager()
	{
		try
		{
			CDEManager.init();
		}
		catch (final Exception ex)
		{
			CatissueCoreServletContextListener.logger.error("Could not initialized application, "
					+ "Error in creating CDE manager.");
			CatissueCoreServletContextListener.logger.error(ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Application Information.
	 */
	private void logApplnInfo()
	{

		final String appHome = CommonServiceLocator.getInstance().getAppHome();
		final StringBuffer fileName = new StringBuffer();
		fileName.append(appHome).append(File.separator)
				.append(ApplicationProperties.getValue("application.version.file"));
		final CVSTagReader cvsTagReader = new CVSTagReader();
		final String cvsTag = cvsTagReader.readTag(fileName.toString());
		Variables.applicationCvsTag = cvsTag;
		logger.info("========================================================");
		logger.info("Application Information");
		logger.info("Name: " + CommonServiceLocator.getInstance().getAppName());
		logger.info("CVS TAG: " + Variables.applicationCvsTag);
		logger.info("Path: " + appHome);
		logger.info("========================================================");
	}

	/**
	 * Add Default Protection Groups.
	 */
	private void addDefaultProtectionGroupsToMap()
	{

		final Map<String, String[]> protectionGroupsForObjectTypes = new HashMap<String, String[]>();

		protectionGroupsForObjectTypes.put(Site.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(Address.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(StorageContainer.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(DistributionProtocol.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(Distribution.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(User.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(Participant.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(CollectionProtocol.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(CollectionProtocolRegistration.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(SpecimenCollectionGroup.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(Specimen.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
//		protectionGroupsForObjectTypes.put(FluidSpecimen.class.getName(),
//				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
//		protectionGroupsForObjectTypes.put(TissueSpecimen.class.getName(),
//				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
//		protectionGroupsForObjectTypes.put(MolecularSpecimen.class.getName(),
//				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
//		protectionGroupsForObjectTypes.put(CellSpecimen.class.getName(),
//				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});
		protectionGroupsForObjectTypes.put(SpecimenCharacteristics.class.getName(),
				new String[]{ProtectionGroups.PUBLIC_DATA_GROUP});

		edu.wustl.security.global.Constants.STATIC_PG_FOR_OBJ_TYPES
				.putAll(protectionGroupsForObjectTypes);
	}

	/**
	 * TO create map of Alias verses corresponding PAGE_OF values.
	 * This is required in Simple Query Edit feature,
	 * It contains mapping of alias name for the query tables &
	 * the corresponding PAGE_OF values.
	 * Patch ID: SimpleSearchEdit_9
	 */
	private void createAliasAndPageOfMap()
	{
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_BIOHAZARD, Constants.PAGE_OF_BIOHAZARD);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_CANCER_RESEARCH_GROUP,
				Constants.PAGE_OF_CANCER_RESEARCH_GROUP);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_COLLECTION_PROTOCOL,
				Constants.PAGE_OF_COLLECTION_PROTOCOL);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_COLLECTION_PROTOCOL_REG,
				Constants.PAGE_OF_COLLECTION_PROTOCOL_REG);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DEPARTMENT, Constants.PAGE_OF_DEPARTMENT);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION,
				Constants.PAGE_OF_DISTRIBUTION);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION_PROTOCOL,
				Constants.PAGE_OF_DISTRIBUTION_PROTOCOL);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_DISTRIBUTION_ARRAY,
				Constants.PAGE_OF_DISTRIBUTION_ARRAY);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_INSTITUTE, Constants.PAGE_OF_INSTITUTE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_PARTICIPANT, Constants.PAGE_OF_PARTICIPANT);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SITE, Constants.PAGE_OF_SITE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN, Constants.PAGE_OF_NEW_SPECIMEN);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_ARRAY,
				Constants.PAGE_OF_SPECIMEN_ARRAY);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_ARRAY_TYPE,
				Constants.PAGE_OF_SPECIMEN_ARRAY_TYPE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_SPECIMEN_COLLECTION_GROUP,
				Constants.PAGE_OF_SPECIMEN_COLLECTION_GROUP);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_STORAGE_CONTAINER,
				Constants.PAGE_OF_STORAGE_CONTAINER);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_STORAGE_TYPE,
				Constants.PAGE_OF_STORAGE_TYPE);
		Variables.aliasAndPageOfMap.put(Constants.ALIAS_USER, Constants.PAGE_OF_USER);
		logger.debug("Initialization of aliasAndPageOf Map completed...");
	}

	/**
	 * Context destroyed.
	 * Shutting down catch manager
	 * @param sce ServletContextEvent
	 */
	public void contextDestroyed(final ServletContextEvent sce)
	{
		try
		{
			BulkOperationUtility.changeBulkOperationStatusToFailed();
			final CatissueCoreCacheManager catissueCoreCacheManager = CatissueCoreCacheManager
					.getInstance();
			catissueCoreCacheManager.shutdown();
			SpecimenWmqProcessor.cleanup();
			SyncCPThreadExecuterImpl executerImpl = SyncCPThreadExecuterImpl.getInstance();
			executerImpl.shutdown();

		}
		catch (final CacheException e)
		{
			CatissueCoreServletContextListener.logger.error("Exception occured while shutting "
					+ "instance of CatissueCoreCacheManager" + e.getMessage(), e);
		}
		catch (final DAOException e)
		{
			CatissueCoreServletContextListener.logger.error("Exception occured while updating "
					+ "the Bulk Operation job status." + e.getMessage(), e);
		}
	}

	/**
	 * Initialize variables required for Edinburg requirement.
	 */
	private void initialiseVariablesForEdinburgh()
	{
		if (Constants.FALSE.equals(XMLPropertyHandler.getValue(Constants.IS_STATE_REQUIRED)))
		{
			Variables.isStateRequired = false;
		}
		if (Constants.TRUE.equals(XMLPropertyHandler.getValue(Constants.IS_CP_TITLE_CHANGE)))
		{
			Variables.isCPTitleChange = true;
		}
		if (Constants.TRUE.equals(XMLPropertyHandler.getValue(Constants.IS_REMOVE_SSN)))
		{
			Variables.isSSNRemove = true;
		}
		if (Constants.TRUE.equals(XMLPropertyHandler.getValue(Constants.IS_REMOVE_SEX_GENOTYPE)))
		{
			Variables.isSexGenoTypeRemove = true;
		}
		if (Constants.TRUE.equals(XMLPropertyHandler.getValue(Constants.IS_REMOVE_RACE)))
		{
			Variables.isRaceRemove = true;
		}
		if (Constants.TRUE.equals(XMLPropertyHandler.getValue(Constants.IS_REMOVE_ETHNICITY)))
		{
			Variables.isEthnicityRemove = true;
		}
	}

	/**
	 * Initialize variables required for DFCI requirement.
	 */
	private void initialiseVariablesForDFCI()
	{
		if (Constants.FALSE.equals(XMLPropertyHandler.getValue(Constants.IS_LAST_NAME_NULL)))
		{
			Variables.isLastNameNull = false;
		}
	}

	/**
	 * Application info initialize.
	 */
	private void initialiseVariableForAppInfo()
	{
		if (XMLPropertyHandler.getValue(Constants.APP_ADDITIONAL_INFO) != null)
		{
			Variables.applicationAdditionInfo = XMLPropertyHandler
					.getValue(Constants.APP_ADDITIONAL_INFO);
		}
	}
	private void initializeParticipantConfig() throws Exception
	{
		ParticipantAttributeDisplayInfoUtility.initializeParticipantConfigObject();
	}

	/** This method reads the file containing default dashboard items and system level dashboard items.
	 *  And stores the items in two different Arraylist defined as Constants. These constants are further used
	 *  to display the respective dashboard.
	 */

	private void initDashboardCache() throws IOException
	{

		String filepath = XMLPropertyHandler.getValue(Constants.DASHBOARD_ITEMS_FILE_PATH);
		String filename = FilenameUtils.getName(filepath);
		
		//get the file from server's properties folder
		String server_file_path =  CommonServiceLocator.getInstance().getPropDirPath()
				+ File.separator + filename;
		
		if (filepath != null && !filepath.isEmpty())
		{
			CSVReader reader = new CSVReader(new FileReader(server_file_path));

			List<String[]> dashboardItems = reader.readAll();
			List<String[]> systemDashboardItems = new ArrayList<String[]>();
			List<String[]> defaultDashboardItems = new ArrayList<String[]>();

			if (!dashboardItems.isEmpty())
				dashboardItems.remove(0); //remove the header from items

			for (String[] item : dashboardItems)
			{
				if (item.length == 3)
				{
					String type = item[2];
					if (edu.wustl.common.util.global.Constants.SYSTEM_DASHBOARD
							.equalsIgnoreCase(type))
					{
						systemDashboardItems.add(item);
					}
					else if (edu.wustl.common.util.global.Constants.DEFAULT_DASHBOARD
							.equalsIgnoreCase(type))
					{
						defaultDashboardItems.add(item);
					}
				}
			}
			edu.wustl.common.util.global.Constants.DEFAULT_DASHBOARD_ITEMS = defaultDashboardItems;
			edu.wustl.common.util.global.Constants.SYSTEM_DASHBOARD_ITEMS = systemDashboardItems;

			reader.close();
		}
	}

}