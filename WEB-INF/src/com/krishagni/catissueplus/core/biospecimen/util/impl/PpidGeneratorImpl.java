
package com.krishagni.catissueplus.core.biospecimen.util.impl;

import org.apache.commons.lang.StringUtils;

import com.krishagni.catissueplus.core.biospecimen.domain.CollectionProtocolRegistration;
import com.krishagni.catissueplus.core.biospecimen.domain.factory.ParticipantErrorCode;
import com.krishagni.catissueplus.core.biospecimen.util.PpidGenerator;
import com.krishagni.catissueplus.core.common.errors.ObjectCreationException;
import com.krishagni.catissueplus.core.common.util.KeyGenFactory;

import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.namegenerator.LabelGenerator;
import edu.wustl.catissuecore.namegenerator.LabelGeneratorFactory;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Variables;

public class PpidGeneratorImpl implements PpidGenerator {

	private KeyGenFactory keyFactory;
	
	private final String PPID = "participant protocol identifier";

	public void setKeyFactory(KeyGenFactory keyFactory) {
		this.keyFactory = keyFactory;
	}

	private String generatePpid(String ppidFormat, Long updatedValue) {

		int ppidFormatEndIndex = ppidFormat.indexOf("\"", 1) + 1;
		String ppidValue = ppidFormat.substring(0, ppidFormatEndIndex).trim().replace("\"", "");

		Long digitValueCount = Long.parseLong(getDigitValueCount(ppidValue));

		StringBuffer digits = new StringBuffer();
		char[] updatedValueArray = updatedValue.toString().toCharArray();
		StringBuffer ppidMiddleValue = getPpidMiddleValue(updatedValueArray.length, digitValueCount, digits, updatedValue);

		StringBuffer tokenValue = new StringBuffer();
		int ppidMiddleValueStartIndex = ppidValue.indexOf("%");
		int ppidMiddleValueEndIndex = ppidValue.indexOf("d");
		String ppidStartValue = ppidValue.substring(0, ppidMiddleValueStartIndex);
		String ppidEndValue = ppidValue.substring(ppidMiddleValueEndIndex + 1, ppidValue.length());
		tokenValue.append(ppidStartValue);
		tokenValue.append(ppidMiddleValue);
		tokenValue.append(ppidEndValue);
		return tokenValue.toString();
	}

	private static String getDigitValueCount(String format) {
		int beginIndex = format.indexOf("%");
		int endIndex = format.indexOf("d");
		String value = format.substring(beginIndex + 1, endIndex);

		return value;
	}

	private static StringBuffer getPpidMiddleValue(int length, Long digitValueCount, StringBuffer digits,
			Long updatedValue) {
		StringBuffer ppidMiddleValue = new StringBuffer();
		if (length >= digitValueCount) {
			ppidMiddleValue.append(updatedValue);
		}
		else {

			for (int i = 0; i < digitValueCount - length; i++) {
				digits.append("0");
			}
			ppidMiddleValue.append(digits);
			ppidMiddleValue.append(updatedValue);
		}
		return ppidMiddleValue;

	}

	@Override
	public void generatePpid(CollectionProtocolRegistration cpr) {
		ObjectCreationException exception = new ObjectCreationException();
		String ppidFormat = cpr.getCollectionProtocol().getPpidFormat();
		if (StringUtils.isBlank(ppidFormat) && !Variables.isProtocolParticipantIdentifierLabelGeneratorAvl) {
			exception.addError(ParticipantErrorCode.MISSING_ATTR_VALUE, PPID);
			throw exception;
		}
		if (StringUtils.isBlank(ppidFormat) && StringUtils.isEmpty(cpr.getProtocolParticipantIdentifier())) {
			try {
				final LabelGenerator ppIdGenrator = LabelGeneratorFactory
						.getInstance(Constants.PROTOCOL_PARTICIPANT_IDENTIFIER_LABEL_GENERATOR_PROPERTY_NAME);
				ppIdGenrator.setLabel(cpr);
			}
			catch (Exception ex) {
				exception.addError(ParticipantErrorCode.MISSING_ATTR_VALUE, PPID);
				throw exception;
			}
			return;
		}

		Long value = keyFactory.getValueByKey(cpr.getCollectionProtocol().getId().toString(),
				CollectionProtocol.class.getName());
		cpr.setProtocolParticipantIdentifier(generatePpid(ppidFormat, value));
		return;
	}

}
