package edu.wustl.catissuecore.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.wustl.catissuecore.bean.CollectionProtocolBean;
import edu.wustl.catissuecore.bean.CollectionProtocolEventBean;
import edu.wustl.catissuecore.bean.GenericSpecimen;
import edu.wustl.catissuecore.bean.SpecimenRequirementBean;
import edu.wustl.catissuecore.bizlogic.CollectionProtocolBizLogic;
import edu.wustl.catissuecore.domain.AbstractSpecimenCollectionGroup;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.ConsentTier;
import edu.wustl.catissuecore.domain.MolecularSpecimen;
import edu.wustl.catissuecore.domain.Quantity;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;
import edu.wustl.catissuecore.domain.SpecimenCollectionRequirementGroup;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.dbManager.DAOException;

public class CollectionProtocolUtil {

	
	private LinkedHashMap<String, CollectionProtocolEventBean> eventBean = 
						new LinkedHashMap<String, CollectionProtocolEventBean> ();
	
	public static CollectionProtocolBean getCollectionProtocolBean(CollectionProtocol collectionProtocol)
	{
		CollectionProtocolBean collectionProtocolBean;
		long[] protocolCoordinatorIds = null;
		collectionProtocolBean = new CollectionProtocolBean();
		collectionProtocolBean.setConsentTierCounter(collectionProtocol.getConsentTierCollection().size());
		Long id = new Long (collectionProtocol.getId().longValue());
		collectionProtocolBean.setIdentifier(id);
		
		Collection userCollection = collectionProtocol.getCoordinatorCollection();
		if(userCollection != null)
		{
			protocolCoordinatorIds = new long[userCollection.size()];
			int i=0;
			Iterator it = userCollection.iterator();
			while(it.hasNext())
			{
				User user = (User)it.next();
				protocolCoordinatorIds[i] = user.getId().longValue();
				i++;
			}
		}
		
		collectionProtocolBean.setProtocolCoordinatorIds(protocolCoordinatorIds);
		collectionProtocolBean.setPrincipalInvestigatorId(collectionProtocol.getPrincipalInvestigator().getId().longValue());
		Date date = collectionProtocol.getStartDate();
		collectionProtocolBean.setStartDate(edu.wustl.common.util.Utility.parseDateToString(date, Constants.DATE_FORMAT) );
		collectionProtocolBean.setDescriptionURL(collectionProtocol.getDescriptionURL());
		collectionProtocolBean.setUnsignedConsentURLName(collectionProtocol.getUnsignedConsentDocumentURL());
		collectionProtocolBean.setConsentWaived (collectionProtocol.getConsentsWaived().booleanValue());   
		collectionProtocolBean.setIrbID(collectionProtocol.getIrbIdentifier());
		collectionProtocolBean.setTitle(collectionProtocol.getTitle());
		collectionProtocolBean.setShortTitle(collectionProtocol.getShortTitle());
		collectionProtocolBean.setEnrollment(String.valueOf(collectionProtocol.getEnrollment()));		
		collectionProtocolBean.setConsentValues(prepareConsentTierMap(collectionProtocol.getConsentTierCollection()));		
		return collectionProtocolBean;
	}

	public static Map prepareConsentTierMap(Collection consentTierColl)
	{
		Map tempMap = new HashMap();
		if(consentTierColl!=null)
		{
			Iterator consentTierCollIter = consentTierColl.iterator();			
			int i = 0;
			while(consentTierCollIter.hasNext())
			{
				ConsentTier consent = (ConsentTier)consentTierCollIter.next();
				String statement = "ConsentBean:"+i+"_statement";
				String preDefinedStatementkey = "ConsentBean:"+i+"_predefinedConsents";
				String statementkey = "ConsentBean:"+i+"_consentTierID";
				tempMap.put(statement, consent.getStatement());
				tempMap.put(preDefinedStatementkey, consent.getStatement());
				tempMap.put(statementkey, consent.getId());
				i++;
			}
		}
		return tempMap;
	}

	public static CollectionProtocolEventBean getCollectionProtocolEventBean(
			CollectionProtocolEvent collectionProtocolEvent, int counter)
	{
		CollectionProtocolEventBean eventBean = new CollectionProtocolEventBean();
				
		eventBean.setStudyCalenderEventPoint(new Double
				(collectionProtocolEvent.getStudyCalendarEventPoint())
											);
		eventBean.setCollectionPointLabel(collectionProtocolEvent.getCollectionPointLabel());
		SpecimenCollectionRequirementGroup specimenRequirementGroup =
								collectionProtocolEvent.getRequiredCollectionSpecimenGroup();
		eventBean.setClinicalDiagnosis(specimenRequirementGroup.getClinicalDiagnosis());
		eventBean.setClinicalStatus(specimenRequirementGroup.getClinicalStatus());
		eventBean.setId(collectionProtocolEvent.getId().longValue());
		eventBean.setUniqueIdentifier("E"+ counter++);
		AbstractSpecimenCollectionGroup requirementGroup =collectionProtocolEvent.getRequiredCollectionSpecimenGroup();
		
		eventBean.setSpecimenCollRequirementGroupId(
				requirementGroup.getId().longValue());
		
		eventBean.setSpecimenRequirementbeanMap(
				getSpecimensMap(specimenRequirementGroup.getSpecimenCollection(), 
						eventBean.getUniqueIdentifier()) );

		return eventBean;
	}
	

	public static LinkedHashMap<String, GenericSpecimen> getSpecimensMap(Collection specimenCollection,
			String parentUniqueId)
	{
		LinkedHashMap<String, GenericSpecimen> specimenMap = new LinkedHashMap<String, GenericSpecimen>();
		
		Iterator specimenIterator = specimenCollection.iterator();
		int specCtr=0;
		while(specimenIterator.hasNext())
		{
			Specimen specimen = (Specimen)specimenIterator.next();
			if (specimen.getParentSpecimen() == null)
			{
				SpecimenRequirementBean specBean =getSpecimenBean(specimen, null, parentUniqueId, specCtr++);

				specimenMap.put(specBean.getUniqueIdentifier(), specBean);				
			}
			
		}
		return specimenMap;
	}
	
	private static LinkedHashMap<String, GenericSpecimen> getChildAliquots(Specimen specimen, 
			String parentuniqueId)
	{
		Collection specimenChildren = specimen.getChildrenSpecimen();
		Iterator iterator = specimenChildren.iterator();
		LinkedHashMap<String, GenericSpecimen>  aliquotMap = new
			LinkedHashMap<String, GenericSpecimen> ();
		int aliqCtr =1;
		
		while(iterator.hasNext())
		{
			Specimen childSpecimen = (Specimen) iterator.next();
			if(Constants.ALIQUOT.equals(childSpecimen.getLineage()))
			{
				SpecimenRequirementBean specimenBean = getSpecimenBean(
						childSpecimen, specimen.getLabel(), parentuniqueId, aliqCtr++);
				
				aliquotMap.put(specimenBean.getUniqueIdentifier(), specimenBean);
			}
		}
		return aliquotMap;
	}

	private static LinkedHashMap<String, GenericSpecimen> getChildDerived(Specimen specimen, 
			String parentuniqueId)
	{
		Collection specimenChildren = specimen.getChildrenSpecimen();
		Iterator iterator = specimenChildren.iterator();
		LinkedHashMap<String, GenericSpecimen>  derivedMap = new
			LinkedHashMap<String, GenericSpecimen> ();
		int deriveCtr=1;
		while(iterator.hasNext())
		{
			Specimen childSpecimen = (Specimen) iterator.next();
			if(Constants.DERIVED_SPECIMEN.equals(childSpecimen.getLineage()))
			{
				SpecimenRequirementBean specimenBean = 
					getSpecimenBean(childSpecimen, specimen.getLabel(),
							parentuniqueId, deriveCtr++);
				derivedMap.put(specimenBean.getUniqueIdentifier(), specimenBean);
			}
		}
		return derivedMap;
	}	

	private static String getUniqueId(String lineage, int ctr)
	{
		if(Constants.NEW_SPECIMEN.equals(lineage))
		{
			return Constants.UNIQUE_IDENTIFIER_FOR_NEW_SPECIMEN + ctr;
		}

		if(Constants.DERIVED_SPECIMEN.equals(lineage))
		{
			return Constants.UNIQUE_IDENTIFIER_FOR_DERIVE + ctr;
		}
		if(Constants.ALIQUOT.equals(lineage))
		{
			return Constants.UNIQUE_IDENTIFIER_FOR_ALIQUOT + ctr;
		}
		return null;
	}
	private static SpecimenRequirementBean getSpecimenBean(Specimen specimen, String parentName,
										String parentUniqueId, int specCtr)
	{
		
		SpecimenRequirementBean speRequirementBean = new SpecimenRequirementBean();
		speRequirementBean.setLineage(specimen.getLineage());
		speRequirementBean.setUniqueIdentifier(
				parentUniqueId + getUniqueId(specimen.getLineage(),specCtr));

		speRequirementBean.setDisplayName(Constants.ALIAS_SPECIMEN 
						+ "_" + speRequirementBean.getUniqueIdentifier());
		
		speRequirementBean.setClassName(specimen.getClassName());
		speRequirementBean.setType(specimen.getType());
		speRequirementBean.setId(specimen.getId().longValue());
		SpecimenCharacteristics characteristics = specimen.getSpecimenCharacteristics();

		if(characteristics != null)
		{
			speRequirementBean.setTissueSite(characteristics.getTissueSite());
			speRequirementBean.setTissueSide(characteristics.getTissueSide());
		}
		
		speRequirementBean.setSpecimenCharsId(specimen.getSpecimenCharacteristics().getId().longValue());
		speRequirementBean.setPathologicalStatus(specimen.getPathologicalStatus());
		
		if("Molecular".equals(specimen.getClassName()))
		{
			Double concentration = ((MolecularSpecimen)specimen).getConcentrationInMicrogramPerMicroliter();
			if (concentration != null)
			{
				speRequirementBean.setConcentration( String.valueOf(concentration.doubleValue()));
			}
		}
		
		Quantity quantity = specimen.getInitialQuantity();
		
		if(quantity != null)
		{
			if(quantity.getValue()!=null)
			{
				Double quantityValue = quantity.getValue();
				speRequirementBean.setQuantity(String.valueOf(quantityValue.doubleValue()));
			}
		}
		
		speRequirementBean.setStorageContainerForSpecimen("Virtual");
/*		
		specimen.getSpecimenEventCollection()
		private String collectionEventCollectionProcedure;
		
		private String collectionEventContainer;
		
		private String receivedEventReceivedQuality;
		
		
		private long collectionEventId;																											// Mandar : CollectionEvent 10-July-06
		private long collectionEventSpecimenId;
		private long collectionEventUserId;		

		private long receivedEventId;
		private long receivedEventSpecimenId;
		private long receivedEventUserId;

	 */	
		
		speRequirementBean.setParentName(parentName);
		
		LinkedHashMap<String, GenericSpecimen> aliquotMap = 
			getChildAliquots(specimen, speRequirementBean.getUniqueIdentifier());
		LinkedHashMap<String, GenericSpecimen> derivedMap =
			getChildDerived(specimen, speRequirementBean.getUniqueIdentifier());
		
		Collection aliquotCollection = aliquotMap.values();
		Collection derivedCollection = derivedMap.values();
		if (aliquotCollection != null && !aliquotCollection.isEmpty())
		{
			Iterator iterator = aliquotCollection.iterator();
			GenericSpecimen aliquotSpecimen = (GenericSpecimen)iterator.next();
			speRequirementBean.setStorageContainerForAliquotSpecimem(
					aliquotSpecimen.getStorageContainerForSpecimen() );
			speRequirementBean.setQuantityPerAliquot(aliquotSpecimen.getQuantity());
		}
		
		speRequirementBean.setNoOfAliquots(String.valueOf(aliquotCollection.size()));			
		speRequirementBean.setAliquotSpecimenCollection(aliquotMap);
		speRequirementBean.setDeriveSpecimenCollection(derivedMap);
		speRequirementBean.setNoOfDeriveSpecimen(derivedCollection.size());			
	    speRequirementBean.setDeriveSpecimen(derivedMap);

		if (derivedCollection != null && !derivedCollection.isEmpty())
		{
			Iterator iterator = derivedCollection.iterator();
			GenericSpecimen derivedSpecimen = (GenericSpecimen)iterator.next();
			speRequirementBean.setDeriveClassName(derivedSpecimen.getClassName());
			speRequirementBean.setDeriveType(derivedSpecimen.getType());
			speRequirementBean.setDeriveConcentration(derivedSpecimen.getConcentration());
			speRequirementBean.setDeriveQuantity(derivedSpecimen.getQuantity());
		}
		
		return speRequirementBean;
	}

	/**
	 * @param mapping
	 * @param request
	 * @param cpSessionList
	 */
	public static void updateSession(HttpServletRequest request,  Long id)
			throws DAOException{
		
		List sessionCpList = new CollectionProtocolBizLogic().retrieveCP(
				CollectionProtocol.class.getName(), "id",id);

		if (sessionCpList == null || sessionCpList.size()<2){
			
			throw new DAOException("Fail to retrieve Collection protocol..");
		}
		
		HttpSession session = request.getSession();
		session.removeAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN);
		CollectionProtocolBean collectionProtocolBean = 
			(CollectionProtocolBean)sessionCpList.get(0);
		collectionProtocolBean.setOperation("update");
		session.setAttribute(Constants.COLLECTION_PROTOCOL_SESSION_BEAN,
				sessionCpList.get(0));
		
		session.removeAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP);
		session.setAttribute(Constants.COLLECTION_PROTOCOL_EVENT_SESSION_MAP,
				sessionCpList.get(1));
	}


	/**
	 * @param collectionProtocolEventColl
	 * @return
	 */
	public static  LinkedHashMap<String, CollectionProtocolEventBean> getCollectionProtocolEventMap(
			Collection collectionProtocolEventColl) {
		Iterator iterator = collectionProtocolEventColl.iterator();
		LinkedHashMap<String, CollectionProtocolEventBean> eventMap = 
			new LinkedHashMap<String, CollectionProtocolEventBean>();
		int ctr=1;
		while(iterator.hasNext())
		{
			CollectionProtocolEvent collectionProtocolEvent=
				(CollectionProtocolEvent)iterator.next();
			
			CollectionProtocolEventBean eventBean =
				CollectionProtocolUtil.getCollectionProtocolEventBean(collectionProtocolEvent,ctr++);
			eventMap.put(eventBean.getUniqueIdentifier(), eventBean);
		}
		return eventMap;
	}
	

}
