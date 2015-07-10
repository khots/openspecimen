package edu.wustl.catissuecore.namegenerator;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.krishagni.catissueplus.core.biospecimen.domain.Participant;

import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.common.util.logger.Logger;


public class DefaultPpidGenerator implements LabelGenerator{

	private static final Logger LOGGER = Logger.getCommonLogger(DefaultPpidGenerator.class);
	
	@Override
	public void setLabel(Object object) {
		String ppid = "";
		if(object instanceof CollectionProtocolRegistration){
			CollectionProtocolRegistration cpr = (CollectionProtocolRegistration)object;
			edu.wustl.catissuecore.domain.Participant part = cpr.getParticipant();
			if(part != null && part.getCollectionProtocolRegistrationCollection() != null 
					&& !part.getCollectionProtocolRegistrationCollection().isEmpty()){
				for (CollectionProtocolRegistration eCpr : part.getCollectionProtocolRegistrationCollection()) {
					ppid = eCpr.getProtocolParticipantIdentifier();
					if(!StringUtils.isEmpty(ppid))
					{
						break;
					}
				}
				
			}
			((CollectionProtocolRegistration)object).setProtocolParticipantIdentifier(ppid);
		}
		else if(object instanceof com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration){
			com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration cpr = (com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration)object;
			if(cpr.getParticipant() != null && cpr.getParticipant().getCprCollection() != null && !cpr.getParticipant().getCprCollection().isEmpty()){
				Participant part = cpr.getParticipant();
				com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration eCpr = part.getCprCollection().entrySet().iterator().next().getValue();
				ppid = eCpr.getProtocolParticipantIdentifier();
			}
			((com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration)object).setProtocolParticipantIdentifier(ppid);
		}
		if(!StringUtils.isEmpty(ppid)){
			return;
		}
		String token = "SYS_PPID";
			try {
				StringBuilder builder = new StringBuilder(100);
				String ppIdPrefix = XMLPropertyHandler.getValue("PPID_prefix");
				String ppIdPostfix = XMLPropertyHandler.getValue("PPID_postfix");
				builder.append(ppIdPrefix).append(TokenFactory.getInstance(token).getTokenValue(object)).append(ppIdPostfix);
				ppid = builder.toString();
				if(object instanceof CollectionProtocolRegistration){
					((CollectionProtocolRegistration)object).setProtocolParticipantIdentifier(ppid);
				}
				else if(object instanceof com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration){
					((com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration)object).setProtocolParticipantIdentifier(ppid);
				}
			}
			catch(Exception exp)
			{
				LOGGER.error("Error while generating PPID");
				LOGGER.error(exp);
			}
	}

	@Override
	public void setLabel(List object) throws LabelGenException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabel(Collection<AbstractDomainObject> object) throws LabelGenException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabel(Object object, boolean ignoreCollectedStatus) throws LabelGenException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLabel(Object object) throws LabelGenException {
		// TODO Auto-generated method stub
		return null;
	}

}
