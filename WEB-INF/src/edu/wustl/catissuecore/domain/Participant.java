/**
 * <p>Title: Participant Class>
 * <p>Description:  An individual from whom a specimen is collected. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @author Gautam Shetty
 * @version 1.00
 * Created on Apr 7, 2005
 */

package edu.wustl.catissuecore.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.struts.upload.FormFile;

import edu.wustl.catissuecore.actionForm.ParticipantForm;
import edu.wustl.catissuecore.bean.ConsentResponseBean;
import edu.wustl.catissuecore.bizlogic.CollectionProtocolBizLogic;
import edu.wustl.catissuecore.util.ConsentUtil;
import edu.wustl.catissuecore.util.global.AppUtility;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.bizlogic.IActivityStatus;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.participant.domain.IParticipant;
import edu.wustl.common.util.MapDataParser;
import edu.wustl.common.util.ObjectCloner;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * An individual from whom a specimen is collected.
 *
 * @hibernate.class table="CATISSUE_PARTICIPANT"
 * @author aniruddha_phadnis
 * @author gautam_shetty
 */
public class Participant extends AbstractDomainObject
		implements
			java.io.Serializable,
			IActivityStatus,
			IParticipant
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static final Logger logger = Logger.getCommonLogger(Participant.class);

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 * */
	protected Long id;

	/**
	 * Last name of the participant.
	 */
	protected String lastName;

	/**
	 * First name of the participant.
	 */
	protected String firstName;

	/**
	 * Middle name of the participant.
	 */
	protected String middleName;

	/**
	 * Birth date of participant.
	 */
	protected Date birthDate;

	/**
	 * The gender of the participant.
	 */
	protected String gender;

	/**
	 * The genetic constitution of the individual.
	 */
	protected String sexGenotype;

	/**
	 * Participant's race origination.
	 */
	protected Collection raceCollection = new HashSet();

	/**
	 * Participant's ethnicity status.
	 */
	protected String ethnicity;

	/**
	 * Social Security Number of participant.
	 */
	protected String socialSecurityNumber;

	/**
	 * Defines whether this participant record can be queried (Active) or not
	 * queried (Inactive) by any actor.
	 */
	protected String activityStatus;

	/**
	 * Death date of participant.
	 */
	protected Date deathDate;

	/**
	 * Defines the vital status of the participant like 'Dead', 'Alive' or
	 * 'Unknown'.
	 */
	protected String vitalStatus;

	protected String empiIdStatus = null;

	private String gridValueSelected;
	/**
	 * A collection of medical record identification number that refers to a
	 * Participant.
	 * */
	protected Collection<ParticipantMedicalIdentifier> participantMedicalIdentifierCollection = new LinkedHashSet<ParticipantMedicalIdentifier>();

	/**
	 * A collection of registration of a Participant to a Collection Protocol.
	 */
	protected Collection<CollectionProtocolRegistration> collectionProtocolRegistrationCollection = new HashSet<CollectionProtocolRegistration>();

	/**
	 * metaPhone Code
	 */
	protected String metaPhoneCode;

	private String empiId = "";

	
	
	// /**
	// * empiId : EMPI id of the participant.
	// */
	// protected String empiId = "";

	public String getMetaPhoneCode()
	{
		return metaPhoneCode;
	}

	public void setMetaPhoneCode(String metaPhoneCode)
	{
		this.metaPhoneCode = metaPhoneCode;
	}

	/**
	 * Default Constructor.
	 */
	public Participant()
	{
		super();
	}

	/**
	 * Parameterized Constructor.
	 *
	 * @param form
	 *            AbstractActionForm.
	 * @throws AssignDataException
	 *             : AssignDataException
	 */
	public Participant(AbstractActionForm form) throws AssignDataException
	{
		super();
		this.setAllValues(form);
	}

	/**
	 * Copy Constructor.
	 *
	 * @param participant
	 *            Participant object
	 */
	public Participant(final Participant participant)
	{
		super();
		id = Long.valueOf(participant.getId().longValue());
		lastName = participant.getLastName();
		firstName = participant.getFirstName();
		middleName = participant.getMiddleName();
		birthDate = participant.getBirthDate();
		gender = participant.getGender();
		sexGenotype = participant.getSexGenotype();
		ethnicity = participant.getEthnicity();
		socialSecurityNumber = participant.getSocialSecurityNumber();
		activityStatus = participant.getActivityStatus();
		deathDate = participant.getDeathDate();
		vitalStatus = participant.getVitalStatus();
		collectionProtocolRegistrationCollection = null;
		final Collection<Race> raceCollection = new ArrayList<Race>();
		final Iterator<Race> raceItr = participant.getRaceCollection().iterator();
		while (raceItr.hasNext())
		{
			final Race race = new Race(raceItr.next());
			race.setParticipant(this);
			raceCollection.add(race);
		}
		this.raceCollection = raceCollection;

		final Collection<ParticipantMedicalIdentifier> pmiCollection = new ArrayList<ParticipantMedicalIdentifier>();
		if (participant.getParticipantMedicalIdentifierCollection() != null)
		{
			final Iterator<ParticipantMedicalIdentifier> pmiItr = participant
					.getParticipantMedicalIdentifierCollection().iterator();
			while (pmiItr.hasNext())
			{
				final ParticipantMedicalIdentifier pmi = new ParticipantMedicalIdentifier(pmiItr
						.next());
				pmi.setParticipant(this);
				pmiCollection.add(pmi);
			}
			participantMedicalIdentifierCollection = pmiCollection;
		}
	}

	/**
	 * Returns System generated unique id.
	 *
	 * @return Long System generated unique id.
	 * @see #setId(Long)
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 *               unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence"
	 *                            value="CATISSUE_PARTICIPANT_SEQ"
	 */
	@Override
	public Long getId()
	{
		return id;
	}

	/**
	 * Sets system generated unique id.
	 *
	 * @param identifier
	 *            System generated unique id.
	 * @see #getId()
	 * */
	@Override
	public void setId(Long identifier)
	{
		id = identifier;
	}

	/**
	 * Returns the last name of the Participant.
	 *
	 * @return String representing the last name of the Participant.
	 * @see #setLastName(String)
	 * @hibernate.property name="lastName" type="string" column="LAST_NAME"
	 *                     length="255"
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name of the Participant.
	 *
	 * @param lastName
	 *            Last Name of the Participant.
	 * @see #getLastName()
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Returns the first name of the Participant.
	 *
	 * @return String representing the first name of the Participant.
	 * @see #setFirstName(String)
	 * @hibernate.property name="firstName" type="string" column="FIRST_NAME"
	 *                     length="255"
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name of the Participant.
	 *
	 * @param firstName
	 *            String representing the first name of the Participant.
	 * @see #getFirstName()
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Returns the middle name of the Participant.
	 *
	 * @return String representing the middle name of the Participant.
	 * @see #setMiddleName(String)
	 * @hibernate.property name="middleName" type="string" column="MIDDLE_NAME"
	 *                     length="255"
	 */
	public String getMiddleName()
	{
		return middleName;
	}

	/**
	 * Sets the middle name of the Participant.
	 *
	 * @param middleName
	 *            String representing the middle name of the Participant.
	 * @see #getMiddleName()
	 */
	public void setMiddleName(String middleName)
	{
		this.middleName = middleName;
	}

	/**
	 * Returns the date of birth of the Participant.
	 *
	 * @return String representing the middle name of the Participant.
	 * @see #setBirthDate(String)
	 * @hibernate.property name="birthDate" column="BIRTH_DATE" type="date"
	 */
	public Date getBirthDate()
	{
		return birthDate;
	}

	/**
	 * Sets the date of birth of the Participant.
	 *
	 * @param birthDate
	 *            String representing the date of birth of the Participant.
	 * @see #getDateOfBirth()
	 */
	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	/**
	 * Returns the gender of a participant.
	 *
	 * @return String representing the gender of a participant.
	 * @see #setGender(String)
	 * @hibernate.property name="gender" type="string" column="GENDER"
	 *                     length="20"
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 * Sets the gender of a participant.
	 *
	 * @param gender
	 *            the gender of a participant.
	 * @see #getGender()
	 */
	public void setGender(String gender)
	{
		this.gender = gender;
	}

	/**
	 * Returns the genotype of a participant.
	 *
	 * @return String representing the genotype of a participant.
	 * @see #setSexGenotype(String)
	 * @hibernate.property name="sexGenotype" type="string" column="GENOTYPE"
	 *                     length="50"
	 */
	public String getSexGenotype()
	{
		return sexGenotype;
	}

	/**
	 * Sets the genotype of a participant.
	 *
	 * @param sexGenotype
	 *            the genotype of a participant.
	 * @see #getSexGenotype()
	 */
	public void setSexGenotype(String sexGenotype)
	{
		this.sexGenotype = sexGenotype;
	}

	// /**
	// * Returns the race of the Participant.
	// * @return String representing the race of the Participant.
	// * @see #setRace(String)
	// * @hibernate.property name="race" type="string"
	// * column="RACE" length="50"
	// */
	// public String getRace()
	// {
	// return race;
	// }
	//
	// /**
	// * Sets the race of the Participant.
	// * @param race String representing the race of the Participant.
	// * @see #getRace()
	// */
	// public void setRace(String race)
	// {
	// this.race = race;
	// }

	/**
	 * @return Returns the raceCollection.
	 * @hibernate.set name="raceCollection" table="CATISSUE_RACE"
	 *                cascade="save-update" inverse="false" lazy="false"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.element type="string" column="NAME" length="30"
	 */
	public Collection getRaceCollection()
	{
		return raceCollection;
	}

	/**
	 * @param raceCollection
	 *            The raceCollection to set.
	 */
	public void setRaceCollection(Collection raceCollection)
	{
		this.raceCollection = raceCollection;
	}

	/**
	 * Returns the ethnicity of the Participant.
	 *
	 * @return Ethnicity of the Participant.
	 * @see #setEthnicity(String)
	 * @hibernate.property name="ethnicity" type="string" column="ETHNICITY"
	 *                     length="50"
	 */
	public String getEthnicity()
	{
		return ethnicity;
	}

	/**
	 * Sets the ethnicity of the Participant.
	 *
	 * @param ethnicity
	 *            Ethnicity of the Participant.
	 * @see #getEthnicity()
	 */
	public void setEthnicity(String ethnicity)
	{
		this.ethnicity = ethnicity;
	}

	/**
	 * Returns the Social Security Number of the Participant.
	 *
	 * @return String representing the Social Security Number of the
	 *         Participant.
	 * @see #setSocialSecurityNumber(String)
	 * @hibernate.property name="socialSecurityNumber" type="string"
	 *                     column="SOCIAL_SECURITY_NUMBER" length="50"
	 *                     unique="true"
	 */
	public String getSocialSecurityNumber()
	{
		return socialSecurityNumber;
	}

	/**
	 * Sets the Social Security Number of the Participant.
	 *
	 * @param socialSecurityNumber
	 *            - String representing the Social Security Number of the
	 *            Participant.
	 * @see #getSocialSecurityNumber()
	 */
	public void setSocialSecurityNumber(String socialSecurityNumber)
	{
		this.socialSecurityNumber = socialSecurityNumber;
	}

	/**
	 * Returns the activity status of the participant.
	 *
	 * @hibernate.property name="activityStatus" type="string"
	 *                     column="ACTIVITY_STATUS" length="50"
	 * @return Returns the activity status of the participant.
	 * @see #setActivityStatus(String)
	 */
	public String getActivityStatus()
	{
		return activityStatus;
	}

	/**
	 * Sets the activity status of the participant.
	 *
	 * @param activityStatus
	 *            activity status of the participant.
	 * @see #getActivityStatus()
	 */
	public void setActivityStatus(String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * Returns the date of death of the Participant.
	 *
	 * @return Date representing the death date of the Participant.
	 * @see #setDeathDate(Date)
	 * @hibernate.property name="deathDate" column="DEATH_DATE" type="date"
	 */
	public Date getDeathDate()
	{
		return deathDate;
	}

	/**
	 * Sets the date of birth of the Participant.
	 *
	 * @param deathDate
	 *            The deathDate to set.
	 */
	public void setDeathDate(Date deathDate)
	{
		this.deathDate = deathDate;
	}

	/**
	 * Returns the vital status of the participant.
	 *
	 * @return Returns the vital status of the participant.
	 * @see #setVitalStatus(String)
	 * @hibernate.property name="vitalStatus" type="string"
	 *                     column="VITAL_STATUS" length="50"
	 */
	public String getVitalStatus()
	{
		return vitalStatus;
	}

	/**
	 * Sets the vital status of the Participant.
	 *
	 * @param vitalStatus
	 *            The vitalStatus to set.
	 */
	public void setVitalStatus(String vitalStatus)
	{
		this.vitalStatus = vitalStatus;
	}

	/**
	 * Returns collection of medical identifiers associated with this
	 * participant.
	 *
	 * @return collection of medical identifiers of this participant.
	 * @hibernate.set name="participantMedicalIdentifierCollection"
	 *                table="CATISSUE_PART_MEDICAL_ID" cascade="none"
	 *                inverse="true" lazy="false"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.collection-one-to-many
	 *                                   class="edu.wustl.catissuecore.domain.ParticipantMedicalIdentifier"
	 * @see setParticipantMedicalIdentifierCollection(Collection)
	 */
	public Collection getParticipantMedicalIdentifierCollection()
	{
		return participantMedicalIdentifierCollection;
	}

	/**
	 * Sets the collection of medical identifiers of this participant.
	 *
	 * @param participantMedicalIdentifierCollection
	 *            collection of medical identifiers of this participant.
	 * @see #getParticipantMedicalIdentifierCollection()
	 */
	public void setParticipantMedicalIdentifierCollection(Collection partiMediIdColn)
	{
		participantMedicalIdentifierCollection = partiMediIdColn;
	}

	/**
	 * Returns collection of collection protocol registrations of this
	 * participant.
	 *
	 * @return collection of collection protocol registrations of this
	 *         participant.
	 * @hibernate.set name="collectionProtocolRegistrationCollection"
	 *                table="CATISSUE_COLL_PROT_REG"
	 * @hibernate.collection-key column="PARTICIPANT_ID"
	 * @hibernate.collection-one-to-many
	 *                                   class="edu.wustl.catissuecore.domain.CollectionProtocolRegistration"
	 * @see setCollectionProtocolRegistrationCollection(Collection)
	 */
	public Collection<CollectionProtocolRegistration> getCollectionProtocolRegistrationCollection()
	{
		return collectionProtocolRegistrationCollection;
	}

	/**
	 * Sets the collection protocol registrations of this participant.
	 *
	 * @param collectionProtocolRegistrationCollection
	 *            - Collection of collection protocol registrations of this
	 *            participant.
	 * @see #getCollectionProtocolRegistrationCollection()
	 */
	public void setCollectionProtocolRegistrationCollection(
			Collection collectionProtocolRegistrationCollection)
	{
		this.collectionProtocolRegistrationCollection = collectionProtocolRegistrationCollection;
	}

	// public String getEmpiId()
	// {
	// return this.empiId;
	// }
	//
	// public void setEmpiId(String empiId)
	// {
	// this.empiId = empiId;
	// }

	/**
	 * This function Copies the data from a StorageTypeForm object to a
	 * StorageType object.
	 *
	 * @param abstractForm
	 *            - A StorageTypeForm object containing the information about
	 *            the StorageType.
	 * @throws AssignDataException
	 *             : AssignDataException
	 * */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		final String nullString = null;
		try
		{
			final ParticipantForm form = (ParticipantForm) abstractForm;
			final Validator validator = new Validator();

			activityStatus = form.getActivityStatus();
			firstName = form.getFirstName();
			middleName = form.getMiddleName();
			lastName = form.getLastName();

			empiId = form.getEmpiId();
			empiIdStatus = form.getEmpiIdStatus();
			if (validator.isValidOption(form.getGender()))
			{
				gender = form.getGender();
			}
			else
			{
				gender = nullString;
			}

			if (validator.isValidOption(form.getGenotype()))
			{
				sexGenotype = form.getGenotype();
			}
			else
			{
				sexGenotype = nullString;
			}

			if (validator.isValidOption(form.getEthnicity()))
			{
				ethnicity = form.getEthnicity();
			}
			else
			{
				ethnicity = nullString;
			}
			
			// if(validator.isValidOption(form.getRace()) )
			// this.race = form.getRace();
			// else
			// this.race = null;
			raceCollection.clear();
			final String[] raceTypes = form.getRaceTypes();
			if (raceTypes != null)
			{
				for (int i = 0; i < raceTypes.length; i++)
				{
					if (!raceTypes[i].equals("-1"))
					{
						final Race race = new Race();
						race.setRaceName(raceTypes[i]);
						race.setParticipant(this);
						raceCollection.add(race);
					}

				}
			}

			final String socialSecurityNumberTemp = form.getSocialSecurityNumber();

			if (!Validator.isEmpty(socialSecurityNumberTemp))
			{
				socialSecurityNumber = socialSecurityNumberTemp;
			}
			else
			{
				socialSecurityNumber = nullString;
			}

			birthDate = CommonUtilities.parseDate(form.getBirthDate(), CommonUtilities
					.datePattern(form.getBirthDate()));

			deathDate = CommonUtilities.parseDate(form.getDeathDate(), CommonUtilities
					.datePattern(form.getDeathDate()));

			if (validator.isValidOption(form.getVitalStatus()))
			{
				vitalStatus = form.getVitalStatus();
			}
			else
			{
				vitalStatus = nullString;
			}

			participantMedicalIdentifierCollection.clear();
			final Map map = form.getValues();
			logger.debug("Map " + map);
			final MapDataParser parser = new MapDataParser("edu.wustl.catissuecore.domain");
			participantMedicalIdentifierCollection = parser.generateData(map);

			// Collection Protocol Registration of the participant
			// (Abhishek Mehta)
//			collectionProtocolRegistrationCollection.clear();
			final Map mapCollectionProtocolRegistrationCollection = form
					.getCollectionProtocolRegistrationValues();
			logger.debug("Map " + map);
			if(form.getCpId()!=-1){
				CollectionProtocolRegistration cpr = new CollectionProtocolRegistration();
				CollectionProtocol cp = new CollectionProtocol();
				cp.setId(form.getCpId());
				cpr.setCollectionProtocol(cp);
				cpr.setRegistrationDate(CommonUtilities
						.parseDate(form.getRegistrationDate()));
				if(form.getCprId()!=null && !form.getCprId().equals("")){
				cpr.setId(Long.parseLong(form.getCprId()));
				
				cpr.setActivityStatus(form.getCprActivityStatus());
				}
				if(!"".equals(form.getPpId())){
					cpr.setProtocolParticipantIdentifier(form.getPpId());
				}
				cpr.setBarcode(form.getBarcode());
				Set<CollectionProtocolRegistration> list = new HashSet<CollectionProtocolRegistration>();
				list.add(cpr);
				boolean isAdd = true;
				for (CollectionProtocolRegistration collProtReg : collectionProtocolRegistrationCollection) {
					if(collProtReg.getId().equals(cpr.getId())){
						updateExisting(cpr, collProtReg);
//						collProtReg.set
						isAdd = false;
					}
				}
				if(isAdd){
					collectionProtocolRegistrationCollection.add(cpr);// = list;
				}
			}else{
				final MapDataParser parserCollectionProtocolRegistrationCollection = new MapDataParser(
						"edu.wustl.catissuecore.domain");
				collectionProtocolRegistrationCollection.addAll(parserCollectionProtocolRegistrationCollection
						.generateData(mapCollectionProtocolRegistrationCollection));
			}
			logger.debug("ParticipantMedicalIdentifierCollection "
					+ participantMedicalIdentifierCollection);
			if(form.getCprId().equals("")){
			this.setConsentsResponseToCollectionProtocolRegistration(form);
			}
			 
		}
		catch (final Exception excp)
		{
			// use of logger as per bug 79
			Participant.logger.error(excp.getMessage(), excp);
			excp.printStackTrace();
			final ErrorKey errorKey = ErrorKey.getErrorKey("assign.data.error");
			throw new AssignDataException(errorKey, null, "Participant.java :");
		}
	}

	private void updateExisting(CollectionProtocolRegistration cpr, CollectionProtocolRegistration collProtReg) {
		collProtReg.setBarcode(cpr.getBarcode());
		collProtReg.setActivityStatus(cpr.getActivityStatus());
		collProtReg.setConsentDocumentName(cpr.getConsentDocumentName());
		collProtReg.setProtocolParticipantIdentifier(cpr.getProtocolParticipantIdentifier());
		collProtReg.setRegistrationDate(cpr.getRegistrationDate());
	}

	/**
	 * Setting Consent Response for the collection protocol.
	 *
	 * @param form
	 *            ParticipantForm.
	 * @throws Exception
	 *             : Exception
	 */
	private void setConsentsResponseToCollectionProtocolRegistration(ParticipantForm form)
			throws Exception
	{
		logger.debug(":: participant id  :" + form.getId());
		final Collection<ConsentResponseBean> consentResponseBeanCollection = form
				.getConsentResponseBeanCollection();
		final Iterator itr = collectionProtocolRegistrationCollection.iterator();
		while (itr.hasNext())
		{
			final CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration) itr
					.next();
			this.setConsentResponse(collectionProtocolRegistration, consentResponseBeanCollection);
		}
	}

	/**
	 * Set Consent Response for given collection protocol.
	 *
	 * @param collectionProtocolRegistration
	 *            CollectionProtocolRegistration.
	 * @param consentResponseBeanCollection
	 *            Collection.
	 * @throws Exception
	 *             : Exception
	 */
	private void setConsentResponse(CollectionProtocolRegistration collectionProtocolRegistration,
			Collection consentResponseBeanCollection) throws Exception
	{
		if (collectionProtocolRegistration.getCollectionProtocol() != null)
		{
			final String cpIDcollectionProtocolRegistration = collectionProtocolRegistration
					.getCollectionProtocol().getId().toString();
			final Collection consentTierCollection = this
					.getConsentList(cpIDcollectionProtocolRegistration);

			final Collection consentTierResponseCollection = this
					.prepareConsentTierResponseCollection(consentTierCollection, false);
			collectionProtocolRegistration
					.setConsentTierResponseCollection(consentTierResponseCollection);
		}
	
	}

	/**
	 * Preparing consent response collection from entered response.
	 *
	 * @param consentResponse
	 *            Collection.
	 * @param isResponse
	 *            boolean.
	 * @return Collection.
	 */
	private Collection prepareConsentTierResponseCollection(Collection consentResponse,
			boolean isResponse)
	{
		final Collection consentTierResponseCollection = new HashSet();
		if (consentResponse != null && !consentResponse.isEmpty())
		{
			if (isResponse)
			{
				final Iterator iter = consentResponse.iterator();
				ConsentUtil.createConsentResponseColl(consentTierResponseCollection, iter);
			}
			else
			{
				final Iterator iter = consentResponse.iterator();
				while (iter.hasNext())
				{
					final ConsentTier consentTier = (ConsentTier) iter.next();
					final ConsentTierResponse consentTierResponse = new ConsentTierResponse();
					consentTierResponse.setResponse(Constants.NOT_SPECIFIED);
					consentTierResponse.setConsentTier(consentTier);
					consentTierResponseCollection.add(consentTierResponse);
				}
			}
		}
		return consentTierResponseCollection;
	}

	/**
	 * Consent List for given collection protocol.
	 *
	 * @param collectionProtocolID
	 *            String.
	 * @return Collection.
	 * @throws BizLogicException
	 *             : BizLogicException
	 * @throws NumberFormatException
	 *             : NumberFormatException
	 */
	private Collection getConsentList(String collectionProtocolID) throws NumberFormatException,
			BizLogicException
	{
		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final CollectionProtocolBizLogic collectionProtocolBizLogic = (CollectionProtocolBizLogic) factory
				.getBizLogic(Constants.COLLECTION_PROTOCOL_FORM_ID);
		final Collection consentTierCollection = (Collection) collectionProtocolBizLogic
				.retrieveAttribute(CollectionProtocol.class.getName(), Long
						.valueOf(collectionProtocolID), "elements(consentTierCollection)");
		return consentTierCollection;
	}

	/**
	 * Returns message label to display on success add or edit.
	 *
	 * @return String.
	 */
	public String getMessageLabel()
	{
		return AppUtility.getlLabel(lastName, firstName);
	}

	// /**
	// * Get getEmpiIdStatus
	// */
	// public String getEmpiIdStatus()
	// {
	// throw new UnsupportedOperationException("Un-Implemented method");
	// }
	// /**
	// * set EmpiId Status
	// * @param empiIdStatus EMPI Status
	// */
	// public void setEmpiIdStatus(String empiIdStatus)
	// {
	// throw new UnsupportedOperationException("Un-Implemented method");
	// }

	public String getEmpiId()
	{
		return empiId;
	}

	public String getEmpiIdStatus()
	{

		return empiIdStatus;
	}

	public String getGridValueSelected()
	{
		return gridValueSelected;
	}

	public void setEmpiId(String empiId)
	{
		this.empiId = empiId;
	}

	public void setEmpiIdStatus(String empiIdStatus)
	{
		this.empiIdStatus = empiIdStatus;
	}

	public void setGridValueSelected(String gridValueSelected)
	{
		this.gridValueSelected = gridValueSelected;
	}

	public Participant clone()
	{
		return new ObjectCloner().clone(this);
	}

	public Integer getBirthYear()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEthnicityCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getParticipantCode()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void setBirthYear(Integer arg0)
	{
		// TODO Auto-generated method stub

	}

	public void setEthnicityCollection(Collection arg0)
	{
		// TODO Auto-generated method stub

	}

	public void setParticipantCode(String arg0)
	{
		// TODO Auto-generated method stub

	}


}